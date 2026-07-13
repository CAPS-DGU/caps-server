package kr.dgucaps.caps.domain.blog.service;

import kr.dgucaps.caps.domain.auth.dto.CustomOAuth2User;
import kr.dgucaps.caps.domain.blog.dto.request.CreateOrModifyBlogRequest;
import kr.dgucaps.caps.domain.blog.dto.response.BlogListResponse;
import kr.dgucaps.caps.domain.blog.dto.response.BlogResponse;
import kr.dgucaps.caps.domain.blog.entity.BlogCategory;
import kr.dgucaps.caps.domain.blog.entity.BlogFile;
import kr.dgucaps.caps.domain.blog.entity.BlogImage;
import kr.dgucaps.caps.domain.blog.entity.BlogPost;
import kr.dgucaps.caps.domain.blog.integration.BlogS3FilesDeletedEvent;
import kr.dgucaps.caps.domain.blog.repository.BlogPostRepository;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.entity.Role;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import kr.dgucaps.caps.global.error.ErrorCode;
import kr.dgucaps.caps.global.error.exception.EntityNotFoundException;
import kr.dgucaps.caps.global.error.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BlogService {

    private static final int PAGE_SIZE = 12;

    private final BlogPostRepository blogPostRepository;
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher publisher;

    // 카테고리·권한별 게시물 목록 조회
    public Page<BlogListResponse> getBlogsByPage(BlogCategory category, int page, Long memberId) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<BlogPost> blogPage = findBlogs(category, memberId, pageable);
        return blogPage.map(BlogListResponse::from);
    }

    // 게시물 상세 조회 및 조회수 증가
    @Transactional
    public BlogResponse getBlogById(Integer blogId, Long memberId) {
        BlogPost blogPost = blogPostRepository.findWithDetailsById(blogId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BLOG_NOT_FOUND));
        validatePrivateAccess(blogPost, memberId);
        blogPost.increaseViewCount();
        return BlogResponse.from(blogPost);
    }

    // 게시물 작성
    @Transactional
    public BlogResponse createBlog(Long memberId, CreateOrModifyBlogRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        BlogPost blogPost = request.toEntity(member);
        attachFiles(blogPost, request.fileUrls());
        attachImages(blogPost, request.imageUrls());
        return BlogResponse.from(blogPostRepository.save(blogPost));
    }

    // 게시물 수정
    @Transactional
    public BlogResponse modifyBlog(Integer blogId, Long memberId, CreateOrModifyBlogRequest request) {
        BlogPost blogPost = blogPostRepository.findWithDetailsById(blogId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BLOG_NOT_FOUND));

        // 교체 전 URL 보관 (S3 정리 비교용)
        List<String> oldFileUrls = blogPost.getFiles().stream().map(BlogFile::getFileUrl).toList();
        List<String> oldImageUrls = blogPost.getImages().stream().map(BlogImage::getFileUrl).toList();
        String oldThumbnailUrl = blogPost.getThumbnailUrl();

        // 본문 필드 갱신
        blogPost.update(
                request.title(),
                request.subtitle(),
                request.content(),
                request.thumbnailUrl(),
                request.category(),
                request.isPrivate(),
                request.writerGrade(),
                request.writerName()
        );

        List<String> urlsToDelete = new ArrayList<>();

        // 첨부파일 전체 교체 (null이면 기존 유지)
        if (request.fileUrls() != null) {
            urlsToDelete.addAll(collectRemovedUrls(oldFileUrls, request.fileUrls()));
            blogPost.clearFiles();
            attachFiles(blogPost, request.fileUrls());
        }
        // 본문 이미지 전체 교체 (null이면 기존 유지)
        if (request.imageUrls() != null) {
            urlsToDelete.addAll(collectRemovedUrls(oldImageUrls, request.imageUrls()));
            blogPost.clearImages();
            attachImages(blogPost, request.imageUrls());
        }
        // 썸네일 교체 시 기존 URL 삭제
        if (!Objects.equals(oldThumbnailUrl, request.thumbnailUrl()) && oldThumbnailUrl != null) {
            urlsToDelete.add(oldThumbnailUrl);
        }

        publishS3DeleteEvent(urlsToDelete);
        return BlogResponse.from(blogPost);
    }

    // 게시물 삭제
    @Transactional
    public void deleteBlog(Integer blogId) {
        BlogPost blogPost = blogPostRepository.findWithDetailsById(blogId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.BLOG_NOT_FOUND));

        List<String> urlsToDelete = new ArrayList<>();
        blogPost.getFiles().stream().map(BlogFile::getFileUrl).forEach(urlsToDelete::add);
        blogPost.getImages().stream().map(BlogImage::getFileUrl).forEach(urlsToDelete::add);
        if (blogPost.getThumbnailUrl() != null) {
            urlsToDelete.add(blogPost.getThumbnailUrl());
        }

        blogPostRepository.delete(blogPost);
        publishS3DeleteEvent(urlsToDelete);
    }

    // 첨부파일 연결
    private void attachFiles(BlogPost blogPost, List<String> fileUrls) {
        if (fileUrls == null) {
            return;
        }
        for (int i = 0; i < fileUrls.size(); i++) {
            blogPost.addFile(fileUrls.get(i), i);
        }
    }

    // 본문 이미지 연결
    private void attachImages(BlogPost blogPost, List<String> imageUrls) {
        if (imageUrls == null) {
            return;
        }
        for (int i = 0; i < imageUrls.size(); i++) {
            blogPost.addImage(imageUrls.get(i), i);
        }
    }

    // 제거된 URL 수집
    private List<String> collectRemovedUrls(List<String> oldUrls, List<String> newUrls) {
        return oldUrls.stream()
                .filter(oldUrl -> !newUrls.contains(oldUrl))
                .toList();
    }

    // S3 삭제 이벤트 발행
    private void publishS3DeleteEvent(List<String> fileKeys) {
        if (fileKeys.isEmpty()) {
            return;
        }
        publisher.publishEvent(new BlogS3FilesDeletedEvent(fileKeys));
    }

    // 권한별 공개/비공개 범위 조회
    private Page<BlogPost> findBlogs(BlogCategory category, Long memberId, Pageable pageable) {
        if (canViewAllPrivatePosts()) {
            return blogPostRepository.findByCategory(category, pageable);
        }
        if (memberId != null) {
            return blogPostRepository.findVisibleByCategory(category, memberId, pageable);
        }
        return blogPostRepository.findByCategoryAndIsPrivateFalse(category, pageable);
    }

    // 비공개 게시물 열람 권한 검사
    private void validatePrivateAccess(BlogPost blogPost, Long memberId) {
        if (!blogPost.isPrivate()) {
            return;
        }
        if (canViewAllPrivatePosts()) {
            return;
        }
        if (memberId != null && blogPost.getMember().getId().equals(memberId)) {
            return;
        }
        throw new ForbiddenException(ErrorCode.BLOG_PRIVATE);
    }

    // 비공개 전체 열람 역할 검사
    private boolean canViewAllPrivatePosts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomOAuth2User customUser)) {
            return false;
        }
        Role role = customUser.authDto().role();
        return role == Role.PRESIDENT || role == Role.ADMIN;
    }
}
