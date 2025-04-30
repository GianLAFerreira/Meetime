package com.meetime.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenStorageService {

    private static final String ACCESS_TOKEN_KEY = "hubspot:access_token";
    private static final long EXPIRATION_TIME_SECONDS = 1800L; // 30 minutos

    private final StringRedisTemplate redisTemplate;

    public void saveAccessToken(String accessToken) {
        redisTemplate.opsForValue().set(ACCESS_TOKEN_KEY, accessToken, EXPIRATION_TIME_SECONDS, TimeUnit.SECONDS);
    }

    public String getAccessToken() {
        return redisTemplate.opsForValue().get(ACCESS_TOKEN_KEY);
    }
}

