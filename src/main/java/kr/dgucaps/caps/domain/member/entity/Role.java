package kr.dgucaps.caps.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

@Getter
@RequiredArgsConstructor
public enum Role implements GrantedAuthority {
    NEW_MEMBER("준회원"),
    MEMBER("회원"),
    GRADUATE("졸업생"),
    COUNCIL("집행부"),
    PRESIDENT("회장"),
    ADMIN("관리자");

    private final String title;

    @Override
    public String getAuthority() {
        return name();
    }
}
