package kr.dgucaps.caps.domain.blog.entity;

import jakarta.persistence.*;
import kr.dgucaps.caps.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "blog_post")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlogPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 100)
    private String subtitle;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail_url", length = 512)
    private String thumbnailUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BlogCategory category;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

    @Column(name = "writer_grade", nullable = false)
    private Float writerGrade;

    @Column(name = "writer_name", nullable = false, length = 50)
    private String writerName;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "blogPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<BlogFile> files = new ArrayList<>();

    @OneToMany(mappedBy = "blogPost", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    private List<BlogImage> images = new ArrayList<>();

    @Builder
    public BlogPost(
            Member member,
            String title,
            String subtitle,
            String content,
            String thumbnailUrl,
            BlogCategory category,
            boolean isPrivate,
            Float writerGrade,
            String writerName
    ) {
        this.member = member;
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.category = category;
        this.isPrivate = isPrivate;
        this.writerGrade = writerGrade;
        this.writerName = writerName;
        this.viewCount = 0;
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void addFile(String fileUrl, int sortOrder) {
        this.files.add(new BlogFile(this, fileUrl, sortOrder));
    }

    public void addImage(String fileUrl, int sortOrder) {
        this.images.add(new BlogImage(this, fileUrl, sortOrder));
    }

    public void clearFiles() {
        this.files.clear();
    }

    public void clearImages() {
        this.images.clear();
    }

    public void update(
            String title,
            String subtitle,
            String content,
            String thumbnailUrl,
            BlogCategory category,
            boolean isPrivate,
            Float writerGrade,
            String writerName
    ) {
        this.title = title;
        this.subtitle = subtitle;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.category = category;
        this.isPrivate = isPrivate;
        this.writerGrade = writerGrade;
        this.writerName = writerName;
    }
}
