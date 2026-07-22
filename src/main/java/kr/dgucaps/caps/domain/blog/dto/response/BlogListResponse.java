package kr.dgucaps.caps.domain.blog.dto.response;

import kr.dgucaps.caps.domain.blog.entity.BlogCategory;
import kr.dgucaps.caps.domain.blog.entity.BlogPost;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BlogListResponse(
        Integer id,
        String title,
        String subtitle,
        String thumbnailUrl,
        BlogCategory category,
        boolean isPrivate,
        Float writerGrade,
        String writerName,
        LocalDateTime createdAt
) {
    public static BlogListResponse from(BlogPost blogPost) {
        return BlogListResponse.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .subtitle(blogPost.getSubtitle())
                .thumbnailUrl(blogPost.getThumbnailUrl())
                .category(blogPost.getCategory())
                .isPrivate(blogPost.isPrivate())
                .writerGrade(blogPost.getWriterGrade())
                .writerName(blogPost.getWriterName())
                .createdAt(blogPost.getCreatedAt())
                .build();
    }
}
