package kr.dgucaps.caps.domain.member.dto;

import kr.dgucaps.caps.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public record CustomOAuth2User(
        Member member,
        Map<String, Object> attributes,
        String attributeKey
) implements OAuth2User, UserDetails {

    @Override
    public String getName() {
        return attributes.get(attributeKey).toString();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Member의 role을 authority로 반환
        return Collections.singletonList(new SimpleGrantedAuthority(member.getRole().getAuthority()));
    }

    @Override
    public String getPassword() {
        return null;
    }

    public String getUsername() {
        return member.getKakaoId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
