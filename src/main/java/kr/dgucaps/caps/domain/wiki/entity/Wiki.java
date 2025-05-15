package kr.dgucaps.caps.domain.wiki.entity;

import jakarta.persistence.*;
import kr.dgucaps.caps.domain.common.entity.BaseTimeEntity;
import kr.dgucaps.caps.domain.member.entity.Member;
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
    @JoinColumn(name = "editor_id", nullable = false)
    private Member editor;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String jamo;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    @Builder
    public Wiki(Member editor, String title, String content, String jamo) {
        this.editor = editor;
        this.title = title;
        this.content = content;
        this.jamo = jamo;
        this.isDeleted = false;
    }
}
