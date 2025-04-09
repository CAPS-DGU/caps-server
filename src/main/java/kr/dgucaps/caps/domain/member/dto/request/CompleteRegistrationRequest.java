package kr.dgucaps.caps.domain.member.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;

public record CompleteRegistrationRequest(
        @Pattern(regexp = "^[0-9]{10}$")
        String studentNumber,
        @DecimalMin(value = "0.0")
        float grade
) {
}
