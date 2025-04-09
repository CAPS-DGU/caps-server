package kr.dgucaps.caps.domain.member.service;

import kr.dgucaps.caps.domain.member.dto.CustomOAuth2User;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class KakaoOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();

        String kakaoId = String.valueOf(attributes.get("id"));
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) kakaoAccount.get("email");
        String name = (String) kakaoAccount.get("name");
        String phoneNumber = (String) kakaoAccount.get("phone_number");

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        String profileImageUrl = (String) profile.get("profile_image_url");

        Member member = memberRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .kakaoId(kakaoId)
                            .name(name)
                            .email(email)
                            .phoneNumber(phoneNumber)
                            .profileImageUrl(profileImageUrl)
                            .build();
                    return memberRepository.save(newMember);
                });

        return new CustomOAuth2User(member, attributes, "id");
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