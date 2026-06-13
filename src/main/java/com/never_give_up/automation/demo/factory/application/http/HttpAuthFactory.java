package com.never_give_up.automation.demo.factory.application.http;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * HTTP 认证工厂 - Basic/Digest/Bearer 认证方案
 */
public class HttpAuthFactory {

    public enum AuthScheme {
        BASIC, DIGEST, BEARER
    }

    /**
     * 构建 Basic 认证头值
     * Authorization: Basic base64(user:pass)
     */
    public String buildBasicAuth(String username, String password) {
        String credentials = username + ":" + password;
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
        return "Basic " + encoded;
    }

    /**
     * 解析 Basic 认证头
     * 返回 [username, password]
     */
    public String[] parseBasicAuth(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Basic ")) return null;
        String encoded = authHeader.substring(6);
        try {
            String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
            String[] parts = decoded.split(":", 2);
            return parts.length >= 2 ? parts : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * 构建 Digest 认证挑战 WWW-Authenticate
     */
    public String buildDigestChallenge(String realm, boolean stale) {
        String nonce = generateNonce();
        StringBuilder sb = new StringBuilder("Digest ");
        sb.append("realm=\"").append(realm).append("\", ");
        sb.append("nonce=\"").append(nonce).append("\", ");
        sb.append("qop=\"auth\", ");
        sb.append("algorithm=MD5");
        if (stale) {
            sb.append(", stale=true");
        }
        return sb.toString();
    }

    /**
     * 构建 Digest 认证响应 Authorization
     */
    public String buildDigestAuth(String username, String realm, String password,
                                  String method, String uri, String nonce) {
        String ha1 = md5Hex(username + ":" + realm + ":" + password);
        String ha2 = md5Hex(method + ":" + uri);
        String nc = "00000001";
        String cnonce = generateNonce();
        String response = md5Hex(ha1 + ":" + nonce + ":" + nc + ":" + cnonce + ":auth:" + ha2);

        StringBuilder sb = new StringBuilder("Digest ");
        sb.append("username=\"").append(username).append("\", ");
        sb.append("realm=\"").append(realm).append("\", ");
        sb.append("nonce=\"").append(nonce).append("\", ");
        sb.append("uri=\"").append(uri).append("\", ");
        sb.append("response=\"").append(response).append("\", ");
        sb.append("nc=").append(nc).append(", ");
        sb.append("cnonce=\"").append(cnonce).append("\", ");
        sb.append("qop=auth");
        return sb.toString();
    }

    /**
     * 构建 Bearer 认证头
     * Authorization: Bearer <token>
     */
    public String buildBearerAuth(String token) {
        return "Bearer " + token;
    }

    /**
     * 解析认证方案
     */
    public AuthScheme detectScheme(String authHeader) {
        if (authHeader == null) return null;
        if (authHeader.startsWith("Basic ")) return AuthScheme.BASIC;
        if (authHeader.startsWith("Digest ")) return AuthScheme.DIGEST;
        if (authHeader.startsWith("Bearer ")) return AuthScheme.BEARER;
        return null;
    }

    /**
     * 构建 401 响应 WWW-Authenticate 头
     */
    public String buildWwwAuthenticate(AuthScheme scheme, String realm) {
        switch (scheme) {
            case BASIC:
                return "Basic realm=\"" + realm + "\", charset=\"UTF-8\"";
            case DIGEST:
                return buildDigestChallenge(realm, false);
            case BEARER:
                return "Bearer realm=\"" + realm + "\"";
            default:
                return "Basic realm=\"" + realm + "\"";
        }
    }

    // ========== 辅助方法 ==========

    private String generateNonce() {
        byte[] nonceBytes = new byte[16];
        new SecureRandom().nextBytes(nonceBytes);
        return Base64.getEncoder().encodeToString(nonceBytes);
    }

    private String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
