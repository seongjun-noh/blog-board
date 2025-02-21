package com.example.blog_board.service.redis;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    // Prefix 정의
    private static final String BLACKLIST_PREFIX = "blackList:";
    private static final String PREFIX_REFRESH_TOKEN = "refreshToken:";




    // 블랙리스트 저장
    public void saveBlackList(String token, long timeout) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + token, "blacklisted", timeout, TimeUnit.MILLISECONDS);
    }

    // 블랙리스트 여부 확인
    public boolean isBlacklisted(String accessToken) {
        return this.exists(BLACKLIST_PREFIX, accessToken);
    }




    // 리프레쉬 토큰 저장
    public void saveRefreshToken(String userEmail, String token, long timeout) {
        redisTemplate.opsForValue().set(PREFIX_REFRESH_TOKEN + userEmail, token, timeout, TimeUnit.MILLISECONDS);
    }

    // 리프레쉬 토큰 조회
    public String getRefreshToken(String userEmail) {
        return redisTemplate.opsForValue().get(PREFIX_REFRESH_TOKEN + userEmail);
    }

    // 리프레쉬 토큰 삭제
    public void deleteRefreshToken(String userEmail) {
        redisTemplate.delete(PREFIX_REFRESH_TOKEN + userEmail);
	}




    // 키 삭제
    public void deleteKey(String prefix, String key) {
        redisTemplate.delete(prefix + key);
    }

    // 키 존재 여부 확인
    public boolean exists(String prefix, String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(prefix + key));
    }
}
