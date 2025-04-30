package com.meetime.service;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.stereotype.Component;

@Component
public class HubspotRateLimiter {

    private final RateLimiter rateLimiter = RateLimiter.create(11.0); // 110 chamadas a cada 10 segundos

    public void acquire() {
        rateLimiter.acquire();
    }
}
