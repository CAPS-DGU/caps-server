package kr.dgucaps.caps.domain.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kr.dgucaps.caps.domain.blog.entity.BlogCategory;
import kr.dgucaps.caps.domain.blog.entity.BlogPost;
import kr.dgucaps.caps.domain.member.entity.Member;

import java.util.List;

public record CreateOrModifyBlogRequest(
        @NotBlank String title,
        String subtitle,
        @NotBlank String content,
        String thumbnailUrl,
        @NotNull BlogCategory category,
        @NotNull Boolean isPrivate,
        @NotNull Float writerGrade,
        @NotBlank String writerName,
        List<String> fileUrls,
        List<String> imageUrls
) {
    public BlogPost toEntity(Member member) {
        return BlogPost.builder()
                .member(member)
                .title(title)
                .subtitle(subtitle)
                .content(content)
                .thumbnailUrl(thumbnailUrl)
                .category(category)
                .isPrivate(isPrivate)
                .writerGrade(writerGrade)
                .writerName(writerName)
                .build();
    }
}
