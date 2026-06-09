package com.never_give_up.automation.demo.factory.function;

import lombok.Data;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DnsCacheFactory {
    private final Map<String, DnsEntry> cache = new ConcurrentHashMap<>();

    @Data
    public static class DnsEntry {
        private final String domain;
        private final String ipAddress;
        private final long ttl;
        private final long createTime;

        public DnsEntry(String domain, String ip, long ttl) {
            this.domain = domain;
            this.ipAddress = ip;
            this.ttl = ttl;
            this.createTime = System.currentTimeMillis();
        }

        public boolean isExpired() {
            return System.currentTimeMillis() - createTime > ttl;
        }

        public long getRemainingMs() {
            return Math.max(0, ttl - (System.currentTimeMillis() - createTime));
        }
    }

    public void addEntry(String domain, String ip, long ttl) {
        cache.put(domain, new DnsEntry(domain, ip, ttl));
    }

    public String resolve(String domain) {
        DnsEntry entry = cache.get(domain);
        if (entry == null || entry.isExpired()) {
            cache.remove(domain);
            return null;
        }
        return entry.getIpAddress();
    }

    public void removeEntry(String domain) {
        cache.remove(domain);
    }

    public void clear() {
        cache.clear();
    }

    public int size() {
        return cache.size();
    }
}
