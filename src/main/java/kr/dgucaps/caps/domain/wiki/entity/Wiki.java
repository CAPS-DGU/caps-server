package kr.dgucaps.caps.domain.wiki.entity;

import jakarta.persistence.*;
import kr.dgucaps.caps.domain.common.entity.BaseTimeEntity;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.wiki.util.WikiJamoUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "wiki")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wiki extends BaseTimeEntity {

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

    // 배포 후 false로 변경
    @Column(nullable = true)
    private String jamo;

    @PrePersist
    public void initJamo(){
        this.jamo = WikiJamoUtils.convertToJamo(this.title);
    }

    @Builder
    public Wiki(Member member, String title, String content) {
        this.member = member;
        this.title = title;
        this.content = content;
    }

    @OneToMany(mappedBy = "wiki")
    private List<WikiHistory> wikiHistories = new ArrayList<>();

    public void updateWiki(Member member, String title, String content) {
        this.member = member;
        this.title = title;
        this.content = content;
    }

    // 배포 후 false로 변경
    public void updateJamo(String jamo) {
        this.jamo = jamo;
    }
}
