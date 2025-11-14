package kr.dgucaps.caps.domain.ledger.entity;

import jakarta.persistence.*;
import kr.dgucaps.caps.domain.common.entity.BaseTimeEntity;
import kr.dgucaps.caps.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Lob
    private String content;

    @Column(nullable = true, name = "file_url")
    private String fileUrl;

    @Column(nullable = false, name = "view_count")
    private Integer viewCount = 0;

    @Builder
    public Ledger(Member member, String title, String content, String fileUrl, Integer viewCount) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.fileUrl = fileUrl;
    }

    public void updateLedger(Member member, String title, String content, String fileUrl) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.fileUrl = fileUrl;
    }
}
