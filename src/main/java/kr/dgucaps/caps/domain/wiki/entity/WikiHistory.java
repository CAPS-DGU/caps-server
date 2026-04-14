package kr.dgucaps.caps.domain.wiki.entity;

import jakarta.persistence.*;
import kr.dgucaps.caps.domain.common.entity.BaseTimeEntity;
import kr.dgucaps.caps.domain.member.entity.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "wiki_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WikiHistory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wiki_id", nullable = false)
    private Wiki wiki;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder
    public WikiHistory(Wiki wiki, Member member, String title, String content) {
        this.wiki = wiki;
        this.member = member;
        this.title = title;
        this.content = content;
    }
}
