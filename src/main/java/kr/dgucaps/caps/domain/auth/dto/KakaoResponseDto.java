package kr.dgucaps.caps.domain.auth.dto;

import java.util.Map;

public record KakaoResponseDto(
        Map<String, Object> attribute
) implements OAuth2ResponseDto {
    private Map<String, Object> getKakaoAccount() {
        return (Map<String, Object>) attribute.get("kakao_account");
    }

    private Map<String, Object> getKakaoProfile() {
        return (Map<String, Object>) getKakaoAccount().get("profile");
    }

    @Override
    public String getProviderId() {
        return attribute.get("id").toString();
    }

    @Override
    public String getName() {
        Map<String, Object> kakaoProfile = (Map<String, Object>) getKakaoAccount().get("profile");
        return kakaoProfile.get("nickname").toString();
    }

    @Override
    public String getEmail() {
        return getKakaoAccount().get("email").toString();
    }

    @Override
    public String getProfileImageUrl() {
        return getKakaoProfile().get("profile_image_url").toString();
    }
}

/*
 * Kakao 응답 예시:
 * {
 *  "id": 123456789,
 *  "kakao_account": {
 *      "profile": {
 *          "profile_image_url": "http://yyy.kakao.com/dn/.../img_640x640.jpg"
 *      },
 *      "name": "홍길동",
 *      "email": "sample@sample.com",
 *      "phone_number": "+82 010-1234-5678"
 *      }
 *  }
 */
