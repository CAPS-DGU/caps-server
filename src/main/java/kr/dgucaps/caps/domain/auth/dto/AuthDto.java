package kr.dgucaps.caps.domain.auth.dto;

import kr.dgucaps.caps.domain.member.entity.Role;
import lombok.Builder;

@Builder
public record AuthDto(
        long memberId,
        String oAuthId,
        String name,
        Role role
) {
    public static AuthDto of(long memberId, String providerId, String name, Role role) {
        return AuthDto.builder()
                .memberId(memberId)
                .oAuthId(providerId)
                .name(name)
                .role(role)
                .build();
    }
}
