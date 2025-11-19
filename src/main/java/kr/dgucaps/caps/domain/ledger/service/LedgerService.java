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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LedgerService {

    private final LedgerRepository ledgerRepository;
    private final MemberRepository memberRepository;
    private final S3FileStorageService s3FileStorageService;

    public List<LedgerListResponse> getLedgersByPage(int page) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by("createdAt").descending());
        return ledgerRepository.findAll(pageable).map(LedgerListResponse::from).toList();
    }

    public LedgerResponse getLedgerById(Long ledgerId) {
        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.LEDGER_NOT_FOUND));
        return LedgerResponse.from(ledger);
    }

    @Transactional
    public LedgerResponse createLedger(Long memberId, CreateOrModifyLedgerRequest request) {
        String createdFileUrl = resolveFileUrl(request, null);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.MEMBER_NOT_FOUND));
        Ledger ledger = ledgerRepository.save(request.toEntity(member, createdFileUrl));
        return LedgerResponse.from(ledger);
    }

    @Transactional
    public LedgerResponse modifyLedger(Long ledgerId, CreateOrModifyLedgerRequest request) {
        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.LEDGER_NOT_FOUND));

        String originalFileUrl = ledger.getFileUrl();
        String modifiedFileUrl = resolveFileUrl(request, originalFileUrl);

        ledger.updateLedger(request.title(), request.content(), modifiedFileUrl);

        if (originalFileUrl != null && !originalFileUrl.equals(modifiedFileUrl)) {
            s3FileStorageService.remove(originalFileUrl);
        }

        return LedgerResponse.from(ledger);
    }

    @Transactional
    public void deleteLedger(Long ledgerId) {
        Ledger ledger = ledgerRepository.findById(ledgerId)
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.LEDGER_NOT_FOUND));

        String fileUrl = ledger.getFileUrl();
        if (!fileUrl.isBlank()) {
            s3FileStorageService.remove(fileUrl);
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

    public String resolveFileUrl(CreateOrModifyLedgerRequest request, String originalFileUrl) {
        if (request.hasNewFile()) {
            return s3FileStorageService.store(request.file());
        }
        if (request.fileUrl() != null && !request.fileUrl().isBlank()) {
            return request.fileUrl();
        }
        return originalFileUrl;
    }
}
