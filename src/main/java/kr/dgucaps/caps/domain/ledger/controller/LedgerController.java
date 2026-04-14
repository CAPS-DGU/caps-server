package kr.dgucaps.caps.domain.ledger.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import kr.dgucaps.caps.domain.ledger.dto.request.CreateOrModifyLedgerRequest;
import kr.dgucaps.caps.domain.ledger.service.LedgerService;
import kr.dgucaps.caps.global.annotation.Auth;
import kr.dgucaps.caps.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ledgers")
@Validated
public class LedgerController implements LedgerApi {

    private final LedgerService ledgerService;

    @PreAuthorize("hasAnyRole('MEMBER', 'GRADUATE', 'COUNCIL', 'PRESIDENT', 'ADMIN')")
    @GetMapping
    public ResponseEntity<SuccessResponse<?>> getLedgersList(
            @RequestParam(value = "page", required = false, defaultValue = "1") @Valid @Min(1) Integer page) {
        return SuccessResponse.ok(ledgerService.getLedgersByPage(page-1));
    }

    @PreAuthorize("hasAnyRole('MEMBER', 'GRADUATE', 'COUNCIL', 'PRESIDENT', 'ADMIN')")
    @GetMapping("/{ledgerId}")
    public ResponseEntity<SuccessResponse<?>> getSpecificLedger(@PathVariable("ledgerId") Long ledgerId) {
        return SuccessResponse.ok(ledgerService.getLedgerById(ledgerId));
    }

    @PreAuthorize("hasAnyRole('COUNCIL', 'PRESIDENT', 'ADMIN')")
    @PostMapping
    public ResponseEntity<SuccessResponse<?>> createLedger(
            @Auth Long memberId,
            @RequestBody @Valid CreateOrModifyLedgerRequest request
    ) {
        return SuccessResponse.created(ledgerService.createLedger(memberId, request));
    }

    @PreAuthorize("hasAnyRole('COUNCIL', 'PRESIDENT', 'ADMIN')")
    @PatchMapping("/{ledgerId}")
    public ResponseEntity<SuccessResponse<?>> modifyLedger(
            @Auth Long memberId,
            @PathVariable("ledgerId") Long ledgerId,
            @RequestBody @Valid CreateOrModifyLedgerRequest request
    ) {
        return SuccessResponse.ok(ledgerService.modifyLedger(ledgerId, memberId, request));
    }

    @PreAuthorize("hasAnyRole('COUNCIL', 'PRESIDENT', 'ADMIN')")
    @DeleteMapping("/{ledgerId}")
    public ResponseEntity<SuccessResponse<?>> deleteLedger(
            @Auth Long memberId,
            @PathVariable("ledgerId") Long ledgerId
    ) {
        ledgerService.deleteLedger(ledgerId, memberId);
        return ResponseEntity.noContent().build();
    }

}