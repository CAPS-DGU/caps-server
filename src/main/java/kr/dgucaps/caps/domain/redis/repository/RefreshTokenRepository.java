package kr.dgucaps.caps.domain.redis.repository;

import kr.dgucaps.caps.domain.redis.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
