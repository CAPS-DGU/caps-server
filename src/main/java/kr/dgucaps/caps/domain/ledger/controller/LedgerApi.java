package kr.dgucaps.caps.domain.ledger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import kr.dgucaps.caps.domain.ledger.dto.request.CreateOrModifyLedgerRequest;
import kr.dgucaps.caps.global.annotation.Auth;
import kr.dgucaps.caps.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Ledger", description = "장부 API")
public interface LedgerApi {

    @Operation(
            summary = "게시물 목록 조회",
            description = "게시물의 목록을 확인합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LedgerApi.class)))
    })
    ResponseEntity<SuccessResponse<?>> getLedgersList(@RequestParam(value = "page", required = false, defaultValue = "1") @Valid @Min(1) Integer page);

    @Operation(
            summary = "게시물 상세 조회",
            description = "게시물의 상세 내용을 확인합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시물 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LedgerApi.class))),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 장부 아이디")
    })
    ResponseEntity<SuccessResponse<?>> getSpecificLedger(@PathVariable("ledgerId") Long ledgerId);

    @Operation(
            summary = "게시물 등록",
            description = "새로운 게시물을 장부 게시판에 등록합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시물 작성 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LedgerApi.class))),
            @ApiResponse(responseCode = "401", description = "게시물을 작성할 권한이 없음")
    })
    ResponseEntity<SuccessResponse<?>> createLedger(@Auth Long memberId, @Valid @RequestBody CreateOrModifyLedgerRequest request);

    @Operation(
            summary = "게시물 수정",
            description = "기존 게시물의 내용을 수정합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시물 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LedgerApi.class))),
            @ApiResponse(responseCode = "401", description = "게시물을 수정할 권한이 없음")
    })
    ResponseEntity<SuccessResponse<?>> modifyLedger(@Auth Long memberId, @PathVariable("ledgerId") Long ledgerId, @Valid @RequestBody CreateOrModifyLedgerRequest request);

    @Operation(
            summary = "게시물 삭제",
            description = "기존 게시물을 장부 게시판에서 삭제합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "게시물 삭제 성공 (본문 없음)"),
            @ApiResponse(responseCode = "401", description = "게시물을 삭제할 권한이 없음"),
            @ApiResponse(responseCode = "404", description = "해당 게시물이 존재하지 않음")
    })
    ResponseEntity<SuccessResponse<?>> deleteLedger(@PathVariable("ledgerId") Long ledgerId);
}
