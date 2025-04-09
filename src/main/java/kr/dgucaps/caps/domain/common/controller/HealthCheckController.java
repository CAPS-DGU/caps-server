package kr.dgucaps.caps.domain.common.controller;

import kr.dgucaps.caps.global.common.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class HealthCheckController {

    // 서버 상태 확인 API
    @GetMapping("/")
    public ResponseEntity<SuccessResponse<?>> CapsServer() {
        return SuccessResponse.ok("Hello! CAPS Server!");
    }
}