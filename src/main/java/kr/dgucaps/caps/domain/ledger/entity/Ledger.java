package kr.dgucaps.caps.domain.ledger.entity;

import jakarta.persistence.*;
import kr.dgucaps.caps.domain.common.entity.BaseTimeEntity;
import kr.dgucaps.caps.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Getter
@Table(name = "ledger")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ledger extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true, name = "file_url")
    private String fileUrl;

    @Column(nullable = false, name = "view_count")
    private Integer viewCount = 0;

    @Column(nullable = false, name = "is_pinned")
    @ColumnDefault("false")
    private boolean isPinned;

    @Builder
    public Ledger(Member member, String title, String content, String fileUrl, Boolean isPinned) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.fileUrl = fileUrl;
        this.viewCount = 0;
        if (isPinned != null) {
            this.isPinned = isPinned;
        }
    }

    public void updateLedger(String title, String content, String fileUrl, Boolean isPinned) {
        this.title = title;
        this.content = content;
        if (fileUrl != null && !fileUrl.isBlank()) {
            this.fileUrl = fileUrl;
        }
        if (isPinned != null) {
            this.isPinned = isPinned;
        }
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

}