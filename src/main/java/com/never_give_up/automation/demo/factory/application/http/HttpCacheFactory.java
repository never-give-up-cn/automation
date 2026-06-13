package com.never_give_up.automation.demo.factory.application.http;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 缓存工厂 - 缓存控制指令及条件请求处理
 */
public class HttpCacheFactory {

    // ========== Cache-Control 请求指令 ==========
    public static final String NO_CACHE = "no-cache";
    public static final String NO_STORE = "no-store";
    public static final String MAX_AGE = "max-age";
    public static final String MAX_STALE = "max-stale";
    public static final String MIN_FRESH = "min-fresh";
    public static final String ONLY_IF_CACHED = "only-if-cached";

    // ========== Cache-Control 响应指令 ==========
    public static final String PUBLIC = "public";
    public static final String PRIVATE = "private";
    public static final String MUST_REVALIDATE = "must-revalidate";
    public static final String PROXY_REVALIDATE = "proxy-revalidate";
    public static final String S_MAXAGE = "s-maxage";
    public static final String NO_TRANSFORM = "no-transform";
    public static final String IMMUTABLE = "immutable";
    public static final String STALE_WHILE_REVALIDATE = "stale-while-revalidate";

    /**
     * 缓存条目
     */
    public static class CacheEntry {
        private final byte[] body;
        private final Map<String, String> headers;
        private final long cachedAt;
        private final String etag;
        private final String lastModified;

        public CacheEntry(byte[] body, Map<String, String> headers, String etag, String lastModified) {
            this.body = body;
            this.headers = headers;
            this.cachedAt = System.currentTimeMillis();
            this.etag = etag;
            this.lastModified = lastModified;
        }

        public byte[] getBody() { return body; }
        public Map<String, String> getHeaders() { return headers; }
        public long getCachedAt() { return cachedAt; }
        public String getEtag() { return etag; }
        public String getLastModified() { return lastModified; }
    }

    private final Map<String, CacheEntry> cache = new HashMap<>();

    /**
     * 构建 ETag 值（基于内容哈希）
     */
    public String buildEtag(byte[] content) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(content);
            StringBuilder sb = new StringBuilder("\"");
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            sb.append("\"");
            return sb.toString();
        } catch (Exception e) {
            return "\"\\\"\\\"\"";
        }
    }

    /**
     * 构建 Last-Modified 头值
     */
    public String buildLastModified(long timestamp) {
        return java.time.format.DateTimeFormatter
                .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", java.util.Locale.US)
                .format(java.time.Instant.ofEpochMilli(timestamp)
                        .atZone(java.time.ZoneId.of("GMT")));
    }

    /**
     * 构建 Cache-Control 请求指令
     */
    public String buildRequestCacheControl(String... directives) {
        return String.join(", ", directives);
    }

    /**
     * 构建 Cache-Control 响应指令
     */
    public String buildResponseCacheControl(int maxAge, boolean isPublic, boolean mustRevalidate) {
        StringBuilder sb = new StringBuilder();
        if (isPublic) sb.append("public, ");
        else sb.append("private, ");
        sb.append("max-age=").append(maxAge);
        if (mustRevalidate) sb.append(", must-revalidate");
        return sb.toString();
    }

    /**
     * 解析 Cache-Control 头
     */
    public Map<String, String> parseCacheControl(String headerValue) {
        Map<String, String> directives = new HashMap<>();
        if (headerValue == null || headerValue.isEmpty()) return directives;
        for (String directive : headerValue.split(",\\s*")) {
            String[] kv = directive.split("=", 2);
            directives.put(kv[0].trim().toLowerCase(), kv.length >= 2 ? kv[1].trim() : "");
        }
        return directives;
    }

    /**
     * 判断缓存是否新鲜（基于 max-age）
     */
    public boolean isFresh(CacheEntry entry, int maxAgeSeconds) {
        if (entry == null) return false;
        long age = (System.currentTimeMillis() - entry.getCachedAt()) / 1000;
        return age < maxAgeSeconds;
    }

    /**
     * 构建 If-None-Match 头值
     */
    public String buildIfNoneMatch(String etag) {
        return etag;
    }

    /**
     * 构建 If-Modified-Since 头值
     */
    public String buildIfModifiedSince(String lastModified) {
        return lastModified;
    }

    /**
     * 判断 304 Not Modified 响应
     */
    public boolean isNotModified(String requestEtag, String requestIMS,
                                 String cachedEtag, String cachedLastModified) {
        if (requestEtag != null && cachedEtag != null) {
            return requestEtag.equals(cachedEtag);
        }
        if (requestIMS != null && cachedLastModified != null) {
            return requestIMS.equals(cachedLastModified);
        }
        return false;
    }

    // ========== 缓存存储操作 ==========

    public void put(String key, CacheEntry entry) {
        cache.put(key, entry);
    }

    public CacheEntry get(String key) {
        return cache.get(key);
    }

    public void clear() {
        cache.clear();
    }

    public void remove(String key) {
        cache.remove(key);
    }
}
