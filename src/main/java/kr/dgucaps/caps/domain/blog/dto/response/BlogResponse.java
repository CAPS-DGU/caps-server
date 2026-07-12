package kr.dgucaps.caps.domain.blog.dto.response;

import kr.dgucaps.caps.domain.blog.entity.BlogCategory;
import kr.dgucaps.caps.domain.blog.entity.BlogFile;
import kr.dgucaps.caps.domain.blog.entity.BlogImage;
import kr.dgucaps.caps.domain.blog.entity.BlogPost;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record BlogResponse(
        Integer id,
        String title,
        String subtitle,
        String content,
        String thumbnailUrl,
        BlogCategory category,
        boolean isPrivate,
        Float writerGrade,
        String writerName,
        LocalDateTime createdAt,
        List<String> fileUrls,
        List<String> imageUrls
) {
    public static BlogResponse from(BlogPost blogPost) {
        return BlogResponse.builder()
                .id(blogPost.getId())
                .title(blogPost.getTitle())
                .subtitle(blogPost.getSubtitle())
                .content(blogPost.getContent())
                .thumbnailUrl(blogPost.getThumbnailUrl())
                .category(blogPost.getCategory())
                .isPrivate(blogPost.isPrivate())
                .writerGrade(blogPost.getWriterGrade())
                .writerName(blogPost.getWriterName())
                .createdAt(blogPost.getCreatedAt())
                .fileUrls(blogPost.getFiles().stream().map(BlogFile::getFileUrl).toList())
                .imageUrls(blogPost.getImages().stream().map(BlogImage::getFileUrl).toList())
                .build();
    }
}
