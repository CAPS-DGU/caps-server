package kr.dgucaps.caps.domain.member.entity;

import jakarta.persistence.*;
import kr.dgucaps.caps.domain.common.entity.BaseTimeEntity;
import kr.dgucaps.caps.domain.wiki.entity.Wiki;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, unique = true)
    private String kakaoId;

    @Column(length = 20, unique = true)
    private String username;

    @Column
    private String password;

    @Column(nullable = false, length = 12)
    private String name;

    @Column(length = 10, unique = true)
    private String studentNumber;

    @Column
    private Float grade;

    @Column(nullable = false, length = 40)
    private String email;

    @Column(nullable = false, length = 100, unique = true)
    private String phoneNumber;

    @Column(length = 127)
    private String comment;

    @Column(length = 500)
    private String profileImageUrl;

    @Column(nullable = false)
    private Integer point;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean registrationComplete;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean isDeleted;

    @Column
    private LocalDateTime lastLoginAt;

    @Builder
    public Member(String kakaoId, String name, String email, String phoneNumber, String profileImageUrl) {
        this.role = Role.NEW_MEMBER;
        this.kakaoId = kakaoId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.profileImageUrl = profileImageUrl;
        this.point = 0;
        this.registrationComplete = false;
        this.isDeleted = false;
    }

    @OneToMany(mappedBy = "member")
    List<Wiki> wikis = new ArrayList<>();

    public void completeRegistration(String studentNumber, float grade) {
        this.studentNumber = studentNumber;
        this.grade = grade;
        this.registrationComplete = true;
    }

    public void updateMember(String comment, String profileImageUrl) {
        if (comment != null) this.comment = comment;
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }
}
