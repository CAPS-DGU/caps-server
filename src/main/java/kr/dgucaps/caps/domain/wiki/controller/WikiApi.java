package kr.dgucaps.caps.domain.wiki.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.dgucaps.caps.domain.member.entity.Member;
import kr.dgucaps.caps.domain.wiki.dto.request.CreateOrModifyWikiRequest;
import kr.dgucaps.caps.domain.wiki.dto.response.WikiResponse;
import kr.dgucaps.caps.domain.wiki.dto.response.WikiTitleResponse;
import kr.dgucaps.caps.global.common.SuccessResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Wiki", description = "위키 API")
public interface WikiApi {

    @Operation(
            summary = "위키 작성",
            description = "위키를 작성합니다."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "위키 작성 성공",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = WikiResponse.class))),
        @ApiResponse(responseCode = "409", description = "이미 존재하는 위키 제목")
    })
    ResponseEntity<SuccessResponse<?>> createWiki(@AuthenticationPrincipal(expression = "member") Member member,
                                                  @Valid @RequestBody CreateOrModifyWikiRequest request);

    @Operation(
            summary = "위키 수정",
            description = "위키를 수정합니다."
    )
    @ApiResponse(responseCode = "200", description = "위키 수정 성공",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = WikiResponse.class))
    )
    ResponseEntity<SuccessResponse<?>> modifyWiki(@AuthenticationPrincipal(expression = "member") Member member,
                                                  @Valid @RequestBody CreateOrModifyWikiRequest request);

    @Operation(
            summary = "위키 조회",
            description = "title에 해당하는 위키를 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위키 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WikiResponse.class))),
            @ApiResponse(responseCode = "404", description = "위키를 찾을 수 없음")
    })
    ResponseEntity<SuccessResponse<?>> getWiki(@PathVariable("title") String title);

    @Operation(
            summary = "수정 내역 조회",
            description = "title에 해당하는 위키의 수정 내역을 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "수정 내역 조회 성공",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = WikiResponse.class)))
    )
    ResponseEntity<SuccessResponse<?>> getWikiHistory(@PathVariable(value = "title") String title);

    @Operation(
            summary = "무작위 위키",
            description = "무작위 위키를 응답합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "무작위 위키 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WikiTitleResponse.class))),
            @ApiResponse(responseCode = "404", description = "위키를 찾을 수 없음")
    })
    ResponseEntity<SuccessResponse<?>> getRandomWiki();

    @Operation(
            summary = "최근 수정 위키",
            description = "최근 수정된 위키 10개의 제목을 리스트로 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "무작위 위키 조회 성공",
            content = @Content(mediaType = "application/json",
                    array = @ArraySchema(
                            schema = @Schema(implementation = WikiTitleResponse.class)))
    )
    ResponseEntity<SuccessResponse<?>> getRecentWiki();

    @Operation(
            summary = "위키 검색어 자동완성",
            description = "검색어에 해당하는 위키 7개를 자동완성하여 응답합니다"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "위키 검색어 자동완성 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = WikiTitleResponse.class))),
            @ApiResponse(responseCode = "404", description = "위키를 찾을 수 없음")
    })
    ResponseEntity<SuccessResponse<?>> getAutocompleteWiki(@PathVariable(value = "title") String title);

}
