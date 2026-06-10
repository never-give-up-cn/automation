package com.never_give_up.automation.demo.factory.security;

import java.util.HashMap;
import java.util.Map;

/** 速率限制 */
public class RateLimitFactory {
    private final Map<String, Long> lastTime = new HashMap<>();
    private final long intervalMs = 1000;

    public boolean allow(String key) {
        long now = System.currentTimeMillis();
        long last = lastTime.getOrDefault(key, 0L);
        if (now - last >= intervalMs) {
            lastTime.put(key, now);
            return true;
        }
        return false;
    }

    public void reset() {
        lastTime.clear();
    }
}