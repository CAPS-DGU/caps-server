package kr.dgucaps.caps.domain.dto;

import kr.dgucaps.caps.domain.member.entity.Member;

public record MemberSummary(
        Long id,
        String name,
        String profileImageUrl,
        float grade
) {
    public static MemberSummary from(Member member) {
        return new MemberSummary(
                member.getId(),
                member.getName(),
                member.getProfileImageUrl(),
                member.getGrade()
        );
    }
}
