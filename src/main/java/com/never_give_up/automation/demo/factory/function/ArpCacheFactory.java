package com.never_give_up.automation.demo.factory.function;

import lombok.Data;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ArpCacheFactory {
    private final Map<String, ArpEntry> cache = new ConcurrentHashMap<>();
    private long timeoutMs = 300000;

    @Data
    public static class ArpEntry {
        private final String ipAddress;
        private final String macAddress;
        private final long createTime;

        public ArpEntry(String ip, String mac) {
            this.ipAddress = ip;
            this.macAddress = mac;
            this.createTime = System.currentTimeMillis();
        }

        public boolean isExpired(long timeoutMs) {
            return System.currentTimeMillis() - createTime > timeoutMs;
        }
    }

    public void addEntry(String ip, String mac) {
        cache.put(ip, new ArpEntry(ip, mac));
    }

    public String getMac(String ip) {
        ArpEntry entry = cache.get(ip);
        if (entry == null || entry.isExpired(timeoutMs)) {
            cache.remove(ip);
            return null;
        }
        return entry.getMacAddress();
    }

    public void removeEntry(String ip) {
        cache.remove(ip);
    }

    public void clear() {
        cache.clear();
    }

    public void setTimeout(long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }

    public Map<String, ArpEntry> getCache() {
        return cache;
    }

    public int size() {
        return cache.size();
    }
}
