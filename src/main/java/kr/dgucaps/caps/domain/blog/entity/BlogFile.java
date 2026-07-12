package kr.dgucaps.caps.domain.blog.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "blog_file")
public class BlogFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "blog_id", nullable = false)
    private BlogPost blogPost;

    @Column(name = "file_url", nullable = false, length = 512)
    private String fileUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;

    public BlogFile(BlogPost blogPost, String fileUrl, int sortOrder) {
        this.blogPost = blogPost;
        this.fileUrl = fileUrl;
        this.sortOrder = sortOrder;
    }
}
