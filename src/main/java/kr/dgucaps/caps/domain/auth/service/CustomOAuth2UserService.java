package kr.dgucaps.caps.domain.auth.service;

import kr.dgucaps.caps.domain.auth.dto.AuthDto;
import kr.dgucaps.caps.domain.auth.dto.CustomOAuth2User;
import kr.dgucaps.caps.domain.auth.dto.KakaoResponseDto;
import kr.dgucaps.caps.domain.auth.dto.OAuth2ResponseDto;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2ResponseDto oAuth2Response;
        if (registrationId.equals("kakao")) {
            oAuth2Response = new KakaoResponseDto(oAuth2User.getAttributes());
        } else {
            return null;
        }
        String oauthId = oAuth2Response.getProviderId();
        Member existingMember = memberRepository.findByKakaoId(oauthId);
        Member member = null;
        if (existingMember == null) {
            member = Member.builder()
                    .kakaoId(oauthId)
                    .name(oAuth2Response.getName())
                    .email(oAuth2Response.getEmail())
                    .profileImageUrl(oAuth2Response.getProfileImageUrl())
                    .build();
            member = memberRepository.save(member);
        } else {
            member = existingMember;
        }
        AuthDto authDto = AuthDto.of(member.getId(), oauthId, oAuth2Response.getName(), member.getRole());
        return new CustomOAuth2User(authDto);
    }
}
