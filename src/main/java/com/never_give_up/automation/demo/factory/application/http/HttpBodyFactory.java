package com.never_give_up.automation.demo.factory.application.http;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 消息体工厂 - 处理各类 Content-Type 的编码解码
 */
public class HttpBodyFactory {

    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String CONTENT_TYPE_XML = "application/xml";
    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    public static final String CONTENT_TYPE_TEXT = "text/plain";
    public static final String CONTENT_TYPE_HTML = "text/html";
    public static final String CONTENT_TYPE_OCTET = "application/octet-stream";

    /**
     * 构建纯文本体
     */
    public byte[] buildTextBody(String text) {
        return text != null ? text.getBytes(StandardCharsets.UTF_8) : new byte[0];
    }

    /**
     * 构建 JSON 体
     */
    public byte[] buildJsonBody(String json) {
        if (json == null || json.isEmpty()) return new byte[0];
        return json.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 构建 HTML 体
     */
    public byte[] buildHtmlBody(String html) {
        return html != null ? html.getBytes(StandardCharsets.UTF_8) : new byte[0];
    }

    /**
     * 构建 XML 体
     */
    public byte[] buildXmlBody(String xml) {
        return xml != null ? xml.getBytes(StandardCharsets.UTF_8) : new byte[0];
    }

    /**
     * 构建 URL-Encoded 表单体
     * 输入: key=value&key2=value2 格式的字符串
     */
    public byte[] buildFormBody(String formData) {
        return formData != null ? formData.getBytes(StandardCharsets.UTF_8) : new byte[0];
    }

    /**
     * 从 Map 构建 URL-Encoded 表单体
     */
    public byte[] buildFormBodyFromMap(Map<String, String> params) {
        if (params == null || params.isEmpty()) return new byte[0];
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) sb.append('&');
            sb.append(urlEncode(entry.getKey())).append('=').append(urlEncode(entry.getValue()));
            first = false;
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 解析 URL-Encoded 表单体为 Map
     */
    public Map<String, String> parseFormBody(byte[] data) {
        Map<String, String> result = new HashMap<>();
        if (data == null || data.length == 0) return result;
        String body = new String(data, StandardCharsets.UTF_8);
        for (String pair : body.split("&")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                result.put(urlDecode(kv[0]), urlDecode(kv[1]));
            } else if (kv.length == 1) {
                result.put(urlDecode(kv[0]), "");
            }
        }
        return result;
    }

    /**
     * 构建 multipart/form-data 体
     */
    public byte[] buildMultipartBody(Map<String, String> fields, String boundary) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : fields.entrySet()) {
            sb.append("--").append(boundary).append("\r\n");
            sb.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n");
            sb.append("\r\n");
            sb.append(entry.getValue()).append("\r\n");
        }
        sb.append("--").append(boundary).append("--\r\n");
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 从 HTTP 消息中提取消息体
     */
    public byte[] extractBody(byte[] message) {
        if (message == null || message.length == 0) return new byte[0];
        String text = new String(message, StandardCharsets.ISO_8859_1);
        int bodyStart = text.indexOf("\r\n\r\n");
        if (bodyStart == -1) return new byte[0];
        bodyStart += 4;
        if (bodyStart >= message.length) return new byte[0];
        byte[] body = new byte[message.length - bodyStart];
        System.arraycopy(message, bodyStart, body, 0, body.length);
        return body;
    }

    /**
     * 根据 Content-Type 判断是否支持该类型
     */
    public boolean isSupportedContentType(String contentType) {
        if (contentType == null) return false;
        String lower = contentType.toLowerCase();
        return lower.startsWith(CONTENT_TYPE_JSON)
                || lower.startsWith(CONTENT_TYPE_XML)
                || lower.startsWith(CONTENT_TYPE_FORM)
                || lower.startsWith(CONTENT_TYPE_MULTIPART)
                || lower.startsWith(CONTENT_TYPE_TEXT)
                || lower.startsWith(CONTENT_TYPE_HTML);
    }

    // ========== URL 编解码辅助 ==========

    private static String urlEncode(String value) {
        if (value == null) return "";
        try {
            return java.net.URLEncoder.encode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }

    private static String urlDecode(String value) {
        if (value == null) return "";
        try {
            return java.net.URLDecoder.decode(value, "UTF-8");
        } catch (Exception e) {
            return value;
        }
    }
}
