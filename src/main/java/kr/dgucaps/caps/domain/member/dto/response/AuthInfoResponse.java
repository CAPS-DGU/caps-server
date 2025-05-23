package kr.dgucaps.caps.domain.member.dto.response;

import kr.dgucaps.caps.domain.dto.MemberSummary;
import kr.dgucaps.caps.domain.member.entity.Member;
import lombok.Builder;

@Builder
public record AuthInfoResponse(
        MemberSummary member,
        boolean registrationComplete
) {
    public static AuthInfoResponse of(Member member) {
        return AuthInfoResponse.builder()
                .member(MemberSummary.from(member))
                .registrationComplete(member.isRegistrationComplete())
                .build();
    }
}
