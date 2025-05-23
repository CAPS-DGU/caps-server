package kr.dgucaps.caps.domain.member.service;

import kr.dgucaps.caps.domain.member.dto.request.CompleteRegistrationRequest;
import kr.dgucaps.caps.domain.member.dto.response.AuthInfoResponse;
import kr.dgucaps.caps.domain.member.dto.response.MemberInfoResponse;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberInfoResponse completeRegistration(Member member, CompleteRegistrationRequest request) {
        member.completeRegistration(request.studentNumber(), request.grade());
        Member savedMember = memberRepository.save(member);
        return MemberInfoResponse.from(savedMember);
    }

    public AuthInfoResponse getAuthInfo(Member member) {
        return AuthInfoResponse.of(member);
    }
}
