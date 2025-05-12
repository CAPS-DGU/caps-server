package kr.dgucaps.caps.domain.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AutocompleteService {
    private final RedisTemplate<String, String> redisTemplate;
    private String key = "autocorrect";
    private int score = 0;

    public void addToSortedSet(String value) {
        redisTemplate.opsForZSet().add(key, value, score);
    }

    public Long findFromSortedSet(String value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }

    public Set<String> findAllValuesAfterIndexFromSortedSet(Long index) {
        return redisTemplate.opsForZSet().range(key, index, index + 200);
    }
}
