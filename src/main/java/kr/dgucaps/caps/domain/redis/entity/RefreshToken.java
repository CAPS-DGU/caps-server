package kr.dgucaps.caps.domain.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "RT", timeToLive = 604800)
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {

    @Id
    private String memberId;

    private String token;
}
