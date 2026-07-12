package kr.dgucaps.caps.domain.blog.service;

import kr.dgucaps.caps.domain.auth.dto.CustomOAuth2User;
import kr.dgucaps.caps.domain.blog.dto.request.CreateOrModifyBlogRequest;
import kr.dgucaps.caps.domain.blog.dto.response.BlogListResponse;
import kr.dgucaps.caps.domain.blog.dto.response.BlogResponse;
import kr.dgucaps.caps.domain.blog.entity.BlogCategory;
import kr.dgucaps.caps.domain.blog.entity.BlogPost;
import kr.dgucaps.caps.domain.blog.repository.BlogPostRepository;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.entity.Role;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import kr.dgucaps.caps.global.error.ErrorCode;
import kr.dgucaps.caps.global.error.exception.EntityNotFoundException;
import kr.dgucaps.caps.global.error.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BlogService {

    private static final int PAGE_SIZE = 12;

    private final BlogPostRepository blogPostRepository;
    private final MemberRepository memberRepository;

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
        attachFilesAndImages(blogPost, request.fileUrls(), request.imageUrls());
        return BlogResponse.from(blogPostRepository.save(blogPost));
    }

    // 첨부파일·본문 이미지 연결
    private void attachFilesAndImages(BlogPost blogPost, List<String> fileUrls, List<String> imageUrls) {
        if (fileUrls != null) {
            for (int i = 0; i < fileUrls.size(); i++) {
                blogPost.addFile(fileUrls.get(i), i);
            }
        }
        if (imageUrls != null) {
            for (int i = 0; i < imageUrls.size(); i++) {
                blogPost.addImage(imageUrls.get(i), i);
            }
        }
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
