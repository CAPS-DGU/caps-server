package kr.dgucaps.caps.domain.member.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CompleteRegistrationRequest(
        @NotBlank
        @Pattern(regexp = "^[0-9]{10}$")
        String studentNumber,
        @NotNull
        @DecimalMin(value = "0.0")
        float grade,
        @NotBlank
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$")
        String phoneNumber
) {
}
