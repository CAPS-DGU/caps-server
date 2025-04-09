package kr.dgucaps.caps.domain.member.dto.response;

public record MemberTokenResponse(
        String accessToken,
        String refreshToken
) {
    public static MemberTokenResponse of(String accessToken, String refreshToken) {
        return new MemberTokenResponse(accessToken, refreshToken);
    }
}