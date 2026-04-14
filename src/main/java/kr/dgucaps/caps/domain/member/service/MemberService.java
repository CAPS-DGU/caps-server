package kr.dgucaps.caps.domain.member.service;

import kr.dgucaps.caps.domain.member.dto.request.UpdateMemberRequest;
import kr.dgucaps.caps.domain.member.dto.response.MemberInfoResponse;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import kr.dgucaps.caps.global.error.ErrorCode;
import kr.dgucaps.caps.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberInfoResponse getMemberInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        return MemberInfoResponse.from(member);
    }

    @Transactional
    public MemberInfoResponse updateMember(Long memberId, UpdateMemberRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        member.updateMember(request.comment(), request.profileImageUrl());
        return MemberInfoResponse.from(member);
    }

    @Transactional
    public void updateLastLogin(Member member) {
        member.updateLastLogin();
        memberRepository.save(member);
    }
}
