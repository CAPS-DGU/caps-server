package kr.dgucaps.caps.domain.ledger.service;

import kr.dgucaps.caps.domain.ledger.dto.request.CreateOrModifyLedgerRequest;
import kr.dgucaps.caps.domain.ledger.dto.response.LedgerResponse;
import kr.dgucaps.caps.domain.ledger.entity.Ledger;
import kr.dgucaps.caps.domain.ledger.repository.LedgerRepository;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import kr.dgucaps.caps.global.error.ErrorCode;
import kr.dgucaps.caps.global.error.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final MemberRepository memberRepository;

    public List<LedgerResponse> getLedgersByPage(int page) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());
        return ledgerRepository.findAll(pageable).map(LedgerResponse::from).toList();
    }

    public LedgerResponse getLedgerById(Long ledgerId) {
        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.LEDGER_NOT_FOUND));
        return LedgerResponse.from(ledger);
    }

    @Transactional
    public LedgerResponse createLedger(Long memberId, CreateOrModifyLedgerRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Ledger ledger = ledgerRepository.save(request.toEntity(member));
        return LedgerResponse.from(ledger);
    }

    @Transactional
    public LedgerResponse modifyLedger(Long memberId, Long ledgerId, CreateOrModifyLedgerRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.LEDGER_NOT_FOUND));
        ledger.updateLedger(member, request.title(), request.content(), request.fileUrl());
        return LedgerResponse.from(ledger);
    }

    @Transactional
    public void deleteLedger(Long ledgerId) {
        if (!ledgerRepository.existsById(ledgerId)) {
            throw new EntityNotFoundException(ErrorCode.LEDGER_NOT_FOUND);
        }
        ledgerRepository.deleteById(ledgerId);
    }

    @Transactional
    public void updateViewCount(Long ledgerId) {
        if (!ledgerRepository.existsById(ledgerId)) {
            throw new EntityNotFoundException(ErrorCode.LEDGER_NOT_FOUND);
        }
        ledgerRepository.updateView(ledgerId);
    }
}
