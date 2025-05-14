package kr.dgucaps.caps.domain.wiki.entity;

import jakarta.persistence.*;
import kr.dgucaps.caps.domain.common.entity.BaseTimeEntity;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.wiki.util.WikiJamoUtils;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

@Entity
@Getter
@Table(name = "wiki")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE wiki SET is_deleted = true WHERE id = ?")
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

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    @PrePersist
    public void initJamo(){
        this.jamo = WikiJamoUtils.convertToJamo(this.title);
    }

    @Builder
    public Wiki(Member member, String title, String content) {
        this.member = member;
        this.title = title;
        this.content = content;
        this.isDeleted = false;
    }

    // 배포 후 false로 변경
    public void updateJamo(String jamo) {
        this.jamo = jamo;
    }
}
