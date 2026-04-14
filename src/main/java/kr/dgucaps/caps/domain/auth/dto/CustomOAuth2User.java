package kr.dgucaps.caps.domain.auth.dto;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public record CustomOAuth2User(
        AuthDto authDto
) implements OAuth2User {
    @Override
    public Map<String, Object> getAttributes() {
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getName() {
        return String.valueOf(authDto.memberId());
    }

    public String getOAuthId() {
        return authDto.oAuthId();
    }

    public String getUserName() {
        return authDto.name();
    }
}
