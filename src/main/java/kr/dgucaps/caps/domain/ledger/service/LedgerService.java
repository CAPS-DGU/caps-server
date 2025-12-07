package kr.dgucaps.caps.domain.ledger.service;

import kr.dgucaps.caps.domain.ledger.dto.request.CreateOrModifyLedgerRequest;
import kr.dgucaps.caps.domain.ledger.dto.response.LedgerListResponse;
import kr.dgucaps.caps.domain.ledger.dto.response.LedgerResponse;
import kr.dgucaps.caps.domain.ledger.entity.Ledger;
import kr.dgucaps.caps.domain.ledger.repository.LedgerRepository;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.member.repository.MemberRepository;
import kr.dgucaps.caps.global.error.ErrorCode;
import kr.dgucaps.caps.global.error.exception.EntityNotFoundException;
import kr.dgucaps.caps.global.error.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final MemberRepository memberRepository;

    public Page<LedgerListResponse> getLedgersByPage(int page) {
        Sort sort = Sort.by(Sort.Order.desc("isPinned"), Sort.Order.desc("createdAt"));
        Pageable pageable = PageRequest.of(page, 12, sort);
        Page<Ledger> ledgerPage = ledgerRepository.findAll(pageable);
        return ledgerPage.map(LedgerListResponse::from);
    }

    @Transactional
    public LedgerResponse getLedgerById(Long ledgerId) {
        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.LEDGER_NOT_FOUND));
        ledger.increaseViewCount();
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
    public LedgerResponse modifyLedger(Long ledgerId, Long memberId, CreateOrModifyLedgerRequest request) {
        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.LEDGER_NOT_FOUND));

        if (!ledger.getMember().getId().equals(memberId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }

        ledger.updateLedger(request.title(), request.content(), request.fileUrls(), request.isPinned());
        Ledger updatedLedger = ledgerRepository.save(ledger);
        return LedgerResponse.from(updatedLedger);
    }

    @Transactional
    public void deleteLedger(Long ledgerId, Long memberId) {
        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.LEDGER_NOT_FOUND));

        if (!ledger.getMember().getId().equals(memberId)) {
            throw new ForbiddenException(ErrorCode.FORBIDDEN);
        }

        ledgerRepository.deleteById(ledgerId);
    }

}