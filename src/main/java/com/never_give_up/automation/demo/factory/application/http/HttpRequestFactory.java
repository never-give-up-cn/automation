package com.never_give_up.automation.demo.factory.application.http;

import java.nio.charset.StandardCharsets;

/**
 * HTTP 请求工厂 - 处理所有 HTTP 方法及请求行构造
 */
public class HttpRequestFactory {

    public enum HttpMethod {
        GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH
    }

    /**
     * 构建完整请求行
     */
    public byte[] buildRequestLine(HttpMethod method, String uri, String version) {
        String line = method.name() + " " + uri + " " + version + "\r\n";
        return line.getBytes(StandardCharsets.ISO_8859_1);
    }

    public byte[] buildGet(String uri) {
        return buildRequestLine(HttpMethod.GET, uri, "HTTP/1.1");
    }

    public byte[] buildHead(String uri) {
        return buildRequestLine(HttpMethod.HEAD, uri, "HTTP/1.1");
    }

    public byte[] buildPost(String uri) {
        return buildRequestLine(HttpMethod.POST, uri, "HTTP/1.1");
    }

    public byte[] buildPut(String uri) {
        return buildRequestLine(HttpMethod.PUT, uri, "HTTP/1.1");
    }

    public byte[] buildDelete(String uri) {
        return buildRequestLine(HttpMethod.DELETE, uri, "HTTP/1.1");
    }

    public byte[] buildPatch(String uri) {
        return buildRequestLine(HttpMethod.PATCH, uri, "HTTP/1.1");
    }

    public byte[] buildOptions(String uri) {
        return buildRequestLine(HttpMethod.OPTIONS, uri, "HTTP/1.1");
    }

    public byte[] buildTrace(String uri) {
        return buildRequestLine(HttpMethod.TRACE, uri, "HTTP/1.1");
    }

    public byte[] buildConnect(String host) {
        return buildRequestLine(HttpMethod.CONNECT, host, "HTTP/1.1");
    }

    /**
     * 解析请求行: METHOD URI VERSION
     */
    public String[] parseRequestLine(byte[] data) {
        if (data == null || data.length == 0) return null;
        String line = new String(data, StandardCharsets.ISO_8859_1).split("\r\n")[0];
        String[] parts = line.split(" ", 3);
        if (parts.length < 2) return null;
        return parts;
    }

    /**
     * 判断是否为有效 HTTP 方法
     */
    public boolean isValidMethod(String method) {
        if (method == null) return false;
        try {
            HttpMethod.valueOf(method.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
