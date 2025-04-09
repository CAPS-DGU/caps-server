package kr.dgucaps.caps.domain.member.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MemberTokenRequest(
        @NotBlank
        String accessToken,

        @NotBlank
        String refreshToken
) {
}
