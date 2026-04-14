package kr.dgucaps.caps.domain.redis.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RequiredArgsConstructor
@RedisHash(value = "refresh_token", timeToLive = 604800)
public class RefreshToken {

    @Id
    private String memberId;

    private String refreshToken;

    @Builder
    public RefreshToken(String memberId, String refreshToken) {
        this.memberId = memberId;
        this.refreshToken = refreshToken;
    }
}
