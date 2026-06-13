package com.never_give_up.automation.demo.factory.application.http;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP Cookie 工厂 - Cookie/Set-Cookie 的构造与解析
 */
public class HttpCookieFactory {

    public static class Cookie {
        private String name;
        private String value;
        private String domain;
        private String path;
        private long maxAge = -1;
        private boolean secure;
        private boolean httpOnly;
        private String sameSite;

        public Cookie(String name, String value) {
            this.name = name;
            this.value = value;
        }

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        public String getDomain() { return domain; }
        public void setDomain(String domain) { this.domain = domain; }
        public String getPath() { return path; }
        public void setPath(String path) { this.path = path; }
        public long getMaxAge() { return maxAge; }
        public void setMaxAge(long maxAge) { this.maxAge = maxAge; }
        public boolean isSecure() { return secure; }
        public void setSecure(boolean secure) { this.secure = secure; }
        public boolean isHttpOnly() { return httpOnly; }
        public void setHttpOnly(boolean httpOnly) { this.httpOnly = httpOnly; }
        public String getSameSite() { return sameSite; }
        public void setSameSite(String sameSite) { this.sameSite = sameSite; }

        @Override
        public String toString() {
            return name + "=" + value;
        }
    }

    /**
     * 构建 Cookie 请求头值
     */
    public String buildCookieHeader(List<Cookie> cookies) {
        if (cookies == null || cookies.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Cookie c : cookies) {
            if (!first) sb.append("; ");
            sb.append(c.getName()).append("=").append(c.getValue());
            first = false;
        }
        return sb.toString();
    }

    /**
     * 构建 Set-Cookie 响应头值
     */
    public String buildSetCookieHeader(Cookie cookie) {
        StringBuilder sb = new StringBuilder();
        sb.append(cookie.getName()).append("=").append(cookie.getValue());

        if (cookie.getDomain() != null) {
            sb.append("; Domain=").append(cookie.getDomain());
        }
        if (cookie.getPath() != null) {
            sb.append("; Path=").append(cookie.getPath());
        }
        if (cookie.getMaxAge() >= 0) {
            sb.append("; Max-Age=").append(cookie.getMaxAge());
        }
        if (cookie.isSecure()) {
            sb.append("; Secure");
        }
        if (cookie.isHttpOnly()) {
            sb.append("; HttpOnly");
        }
        if (cookie.getSameSite() != null) {
            sb.append("; SameSite=").append(cookie.getSameSite());
        }
        return sb.toString();
    }

    /**
     * 解析 Cookie 请求头
     */
    public List<Cookie> parseCookieHeader(String headerValue) {
        List<Cookie> cookies = new ArrayList<>();
        if (headerValue == null || headerValue.isEmpty()) return cookies;
        for (String pair : headerValue.split(";\\s*")) {
            String[] kv = pair.split("=", 2);
            if (kv.length >= 1) {
                cookies.add(new Cookie(kv[0].trim(), kv.length >= 2 ? kv[1].trim() : ""));
            }
        }
        return cookies;
    }

    /**
     * 解析 Set-Cookie 响应头
     */
    public Cookie parseSetCookieHeader(String headerValue) {
        if (headerValue == null || headerValue.isEmpty()) return null;
        String[] parts = headerValue.split(";\\s*");
        if (parts.length == 0) return null;

        String[] nameValue = parts[0].split("=", 2);
        Cookie cookie = new Cookie(nameValue[0].trim(), nameValue.length >= 2 ? nameValue[1].trim() : "");

        for (int i = 1; i < parts.length; i++) {
            String[] attr = parts[i].split("=", 2);
            String attrName = attr[0].trim().toLowerCase();
            String attrValue = attr.length >= 2 ? attr[1].trim() : "";

            switch (attrName) {
                case "domain": cookie.setDomain(attrValue); break;
                case "path": cookie.setPath(attrValue); break;
                case "max-age":
                    try { cookie.setMaxAge(Long.parseLong(attrValue)); } catch (NumberFormatException ignored) {}
                    break;
                case "secure": cookie.setSecure(true); break;
                case "httponly": cookie.setHttpOnly(true); break;
                case "samesite": cookie.setSameSite(attrValue); break;
            }
        }
        return cookie;
    }

    /**
     * 从 Set-Cookie 头列表构建 Cookie 存储
     */
    public Map<String, Cookie> parseSetCookieHeaders(List<String> headerValues) {
        Map<String, Cookie> cookieJar = new HashMap<>();
        if (headerValues == null) return cookieJar;
        for (String value : headerValues) {
            Cookie cookie = parseSetCookieHeader(value);
            if (cookie != null) {
                cookieJar.put(cookie.getName(), cookie);
            }
        }
        return cookieJar;
    }

    /**
     * Cookie 过期检查
     */
    public boolean isExpired(Cookie cookie) {
        return cookie.getMaxAge() >= 0 && cookie.getMaxAge() * 1000L < System.currentTimeMillis();
    }
}
