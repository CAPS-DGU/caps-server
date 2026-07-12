package kr.dgucaps.caps.domain.blog.service;

import kr.dgucaps.caps.domain.auth.dto.CustomOAuth2User;
import kr.dgucaps.caps.domain.blog.dto.response.BlogListResponse;
import kr.dgucaps.caps.domain.blog.entity.BlogCategory;
import kr.dgucaps.caps.domain.blog.entity.BlogPost;
import kr.dgucaps.caps.domain.blog.repository.BlogPostRepository;
import kr.dgucaps.caps.domain.member.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BlogService {

    private static final int PAGE_SIZE = 12;

    private final BlogPostRepository blogPostRepository;

    // 카테고리·권한에 맞는 게시물 목록 페이지 반환
    public Page<BlogListResponse> getBlogsByPage(BlogCategory category, int page, Long memberId) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<BlogPost> blogPage = findBlogs(category, memberId, pageable);
        return blogPage.map(BlogListResponse::from);
    }

    // 권한에 따라 공개/비공개 포함 범위를 나눠 조회
    private Page<BlogPost> findBlogs(BlogCategory category, Long memberId, Pageable pageable) {
        if (canViewAllPrivatePosts()) {
            return blogPostRepository.findByCategory(category, pageable);
        }
        if (memberId != null) {
            return blogPostRepository.findVisibleByCategory(category, memberId, pageable);
        }
        return blogPostRepository.findByCategoryAndIsPrivateFalse(category, pageable);
    }

    // 현재 사용자가 모든 비공개 게시물을 볼 수 있는 역할인지 확인
    private boolean canViewAllPrivatePosts() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof CustomOAuth2User customUser)) {
            return false;
        }
        Role role = customUser.authDto().role();
        return role == Role.PRESIDENT || role == Role.ADMIN;
    }
}
