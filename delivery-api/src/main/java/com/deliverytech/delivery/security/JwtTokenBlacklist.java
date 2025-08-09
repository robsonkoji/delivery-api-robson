package com.deliverytech.delivery.security;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.*;

import org.springframework.stereotype.Component;

@Component
public class JwtTokenBlacklist {

    private final Map<String, Instant> blacklist = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    public void add(String token, Instant expiration) {
        blacklist.put(token, expiration);
        long delay = expiration.getEpochSecond() - Instant.now().getEpochSecond();
        scheduler.schedule(() -> blacklist.remove(token), delay, TimeUnit.SECONDS);
    }

    public boolean isBlacklisted(String token) {
        return blacklist.containsKey(token);
    }
}

