package kr.dgucaps.caps.domain.member.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateMemberRequest(
        @Size(max = 127)
        String comment,
        @Size(max = 500)
        String profileImageUrl
) {
}
