package com.never_give_up.automation.demo.factory.application.http;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 头部工厂 - 通用头/请求头/响应头/实体头的构造与解析
 */
public class HttpHeaderFactory {

    // ========== 标准头字段常量 ==========

    // General headers
    public static final String DATE = "Date";
    public static final String CONNECTION = "Connection";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String VIA = "Via";
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String UPGRADE = "Upgrade";

    // Request headers
    public static final String HOST = "Host";
    public static final String USER_AGENT = "User-Agent";
    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String AUTHORIZATION = "Authorization";
    public static final String COOKIE = "Cookie";
    public static final String REFERER = "Referer";
    public static final String ORIGIN = "Origin";
    public static final String IF_NONE_MATCH = "If-None-Match";
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String RANGE = "Range";
    public static final String EXPECT = "Expect";

    // Response headers
    public static final String SERVER = "Server";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
    public static final String LOCATION = "Location";
    public static final String ETAG = "ETag";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String AGE = "Age";
    public static final String RETRY_AFTER = "Retry-After";
    public static final String ACCEPT_RANGES = "Accept-Ranges";

    // Entity headers
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String CONTENT_LANGUAGE = "Content-Language";
    public static final String CONTENT_LOCATION = "Content-Location";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String CONTENT_RANGE = "Content-Range";
    public static final String ALLOW = "Allow";
    public static final String EXPIRES = "Expires";

    /**
     * 构建单个请求头行
     */
    public byte[] buildHeader(String name, String value) {
        String line = name + ": " + value + "\r\n";
        return line.getBytes(StandardCharsets.ISO_8859_1);
    }

    /**
     * 从 Map 构建所有头部字节
     */
    public byte[] buildHeaders(Map<String, String> headers) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        return sb.toString().getBytes(StandardCharsets.ISO_8859_1);
    }

    /**
     * 构建标准请求头集合
     */
    public Map<String, String> buildDefaultRequestHeaders(String host) {
        Map<String, String> headers = new HashMap<>();
        headers.put(HOST, host);
        headers.put(USER_AGENT, "DataCartFactoryGame/1.0");
        headers.put(ACCEPT, "*/*");
        headers.put(CONNECTION, "keep-alive");
        return headers;
    }

    /**
     * 构建标准响应头集合
     */
    public Map<String, String> buildDefaultResponseHeaders(String contentType, long contentLength) {
        Map<String, String> headers = new HashMap<>();
        headers.put(SERVER, "DataCartServer/1.0");
        headers.put(DATE, getCurrentHttpDate());
        headers.put(CONTENT_TYPE, contentType);
        headers.put(CONTENT_LENGTH, String.valueOf(contentLength));
        headers.put(CONNECTION, "keep-alive");
        return headers;
    }

    /**
     * 解析头部行
     * 返回 [name, value] 数组，或 null
     */
    public String[] parseHeaderLine(String line) {
        if (line == null || line.isEmpty()) return null;
        int colon = line.indexOf(": ");
        if (colon <= 0) return null;
        return new String[]{line.substring(0, colon), line.substring(colon + 2)};
    }

    /**
     * 解析完整头部块为 Map
     */
    public Map<String, String> parseHeaders(byte[] data) {
        Map<String, String> headers = new HashMap<>();
        String text = new String(data, StandardCharsets.ISO_8859_1);
        for (String line : text.split("\r\n")) {
            String[] kv = parseHeaderLine(line);
            if (kv != null) {
                headers.put(kv[0], kv[1]);
            }
        }
        return headers;
    }

    /**
     * 解析头部块（从完整 HTTP 消息中提取头部区域）
     */
    public Map<String, String> parseHeadersFromMessage(byte[] data) {
        String text = new String(data, StandardCharsets.ISO_8859_1);
        int headerEnd = text.indexOf("\r\n\r\n");
        if (headerEnd == -1) return new HashMap<>();
        String headerSection = text.substring(0, headerEnd);
        // 跳过状态行/请求行
        String[] lines = headerSection.split("\r\n");
        Map<String, String> headers = new HashMap<>();
        for (int i = 1; i < lines.length; i++) {
            String[] kv = parseHeaderLine(lines[i]);
            if (kv != null) {
                headers.put(kv[0], kv[1]);
            }
        }
        return headers;
    }

    /**
     * 获取当前 HTTP 日期格式
     */
    public static String getCurrentHttpDate() {
        return java.time.format.DateTimeFormatter
                .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", java.util.Locale.US)
                .format(java.time.ZonedDateTime.now(java.time.ZoneId.of("GMT")));
    }
}
