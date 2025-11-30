package kr.dgucaps.caps.domain.member.dto.response;

import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.entity.Role;
import lombok.Builder;

@Builder
public record MemberInfoResponse(
        long id,
        Role role,
        String name,
        String studentNumber,
        float grade,
        String email,
        String phoneNumber,
        String comment,
        String profileImageUrl,
        boolean registrationComplete,
        boolean isDeleted
) {
    public static MemberInfoResponse from(Member member) {
        return MemberInfoResponse.builder()
                .id(member.getId())
                .role(member.getRole())
                .name(member.getName())
                .studentNumber(member.getStudentNumber())
                .grade(member.getGrade())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .comment(member.getComment())
                .profileImageUrl(member.getProfileImageUrl())
                .registrationComplete(member.isRegistrationComplete())
                .isDeleted(member.isDeleted())
                .build();
    }
}
