package kr.dgucaps.caps.domain.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 14440)
@AllArgsConstructor
public class RefreshToken {

    @Id
    private String id;

    private String refreshToken;
}
