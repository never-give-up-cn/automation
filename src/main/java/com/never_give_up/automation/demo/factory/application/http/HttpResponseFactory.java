package com.never_give_up.automation.demo.factory.application.http;

import java.nio.charset.StandardCharsets;

/**
 * HTTP 响应工厂 - 完整状态码体系及原因短语
 */
public class HttpResponseFactory {

    public enum StatusCode {
        // 1xx Informational
        CONTINUE(100, "Continue"),
        SWITCHING_PROTOCOLS(101, "Switching Protocols"),
        PROCESSING(102, "Processing"),
        EARLY_HINTS(103, "Early Hints"),

        // 2xx Success
        OK(200, "OK"),
        CREATED(201, "Created"),
        ACCEPTED(202, "Accepted"),
        NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
        NO_CONTENT(204, "No Content"),
        RESET_CONTENT(205, "Reset Content"),
        PARTIAL_CONTENT(206, "Partial Content"),
        MULTI_STATUS(207, "Multi-Status"),
        ALREADY_REPORTED(208, "Already Reported"),
        IM_USED(226, "IM Used"),

        // 3xx Redirection
        MULTIPLE_CHOICES(300, "Multiple Choices"),
        MOVED_PERMANENTLY(301, "Moved Permanently"),
        FOUND(302, "Found"),
        SEE_OTHER(303, "See Other"),
        NOT_MODIFIED(304, "Not Modified"),
        USE_PROXY(305, "Use Proxy"),
        TEMPORARY_REDIRECT(307, "Temporary Redirect"),
        PERMANENT_REDIRECT(308, "Permanent Redirect"),

        // 4xx Client Error
        BAD_REQUEST(400, "Bad Request"),
        UNAUTHORIZED(401, "Unauthorized"),
        PAYMENT_REQUIRED(402, "Payment Required"),
        FORBIDDEN(403, "Forbidden"),
        NOT_FOUND(404, "Not Found"),
        METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
        NOT_ACCEPTABLE(406, "Not Acceptable"),
        PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
        REQUEST_TIMEOUT(408, "Request Timeout"),
        CONFLICT(409, "Conflict"),
        GONE(410, "Gone"),
        LENGTH_REQUIRED(411, "Length Required"),
        PRECONDITION_FAILED(412, "Precondition Failed"),
        PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
        URI_TOO_LONG(414, "URI Too Long"),
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
        RANGE_NOT_SATISFIABLE(416, "Range Not Satisfiable"),
        EXPECTATION_FAILED(417, "Expectation Failed"),
        IM_A_TEAPOT(418, "I'm a Teapot"),
        MISDIRECTED_REQUEST(421, "Misdirected Request"),
        UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
        LOCKED(423, "Locked"),
        FAILED_DEPENDENCY(424, "Failed Dependency"),
        TOO_EARLY(425, "Too Early"),
        UPGRADE_REQUIRED(426, "Upgrade Required"),
        PRECONDITION_REQUIRED(428, "Precondition Required"),
        TOO_MANY_REQUESTS(429, "Too Many Requests"),
        REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
        UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),

        // 5xx Server Error
        INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
        NOT_IMPLEMENTED(501, "Not Implemented"),
        BAD_GATEWAY(502, "Bad Gateway"),
        SERVICE_UNAVAILABLE(503, "Service Unavailable"),
        GATEWAY_TIMEOUT(504, "Gateway Timeout"),
        HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
        VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
        INSUFFICIENT_STORAGE(507, "Insufficient Storage"),
        LOOP_DETECTED(508, "Loop Detected"),
        NOT_EXTENDED(510, "Not Extended"),
        NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required");

        private final int code;
        private final String phrase;

        StatusCode(int code, String phrase) {
            this.code = code;
            this.phrase = phrase;
        }

        public int getCode() { return code; }
        public String getPhrase() { return phrase; }

        public static StatusCode fromCode(int code) {
            for (StatusCode sc : values()) {
                if (sc.code == code) return sc;
            }
            return null;
        }
    }

    /**
     * 构建状态行: VERSION CODE PHRASE
     */
    public byte[] buildStatusLine(String version, StatusCode status) {
        String line = version + " " + status.getCode() + " " + status.getPhrase() + "\r\n";
        return line.getBytes(StandardCharsets.ISO_8859_1);
    }

    public byte[] buildStatusLine(String version, int code) {
        StatusCode sc = StatusCode.fromCode(code);
        String phrase = sc != null ? sc.getPhrase() : "Unknown";
        String line = version + " " + code + " " + phrase + "\r\n";
        return line.getBytes(StandardCharsets.ISO_8859_1);
    }

    public byte[] buildOk() {
        return buildStatusLine("HTTP/1.1", StatusCode.OK);
    }

    public byte[] buildNotFound() {
        return buildStatusLine("HTTP/1.1", StatusCode.NOT_FOUND);
    }

    public byte[] buildServerError() {
        return buildStatusLine("HTTP/1.1", StatusCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * 解析状态行: VERSION CODE PHRASE
     */
    public String[] parseStatusLine(byte[] data) {
        if (data == null || data.length == 0) return null;
        String line = new String(data, StandardCharsets.ISO_8859_1).split("\r\n")[0];
        String[] parts = line.split(" ", 3);
        if (parts.length < 2) return null;
        return parts;
    }

    /**
     * 获取状态码分类描述
     */
    public String getCategory(int code) {
        if (code >= 100 && code < 200) return "Informational";
        if (code >= 200 && code < 300) return "Success";
        if (code >= 300 && code < 400) return "Redirection";
        if (code >= 400 && code < 500) return "Client Error";
        if (code >= 500 && code < 600) return "Server Error";
        return "Unknown";
    }
}
