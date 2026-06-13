package com.never_give_up.automation.demo.factory.application;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.factory.application.http.*;
import com.never_give_up.automation.demo.model.HttpPacket;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP 主工厂 - 门面模式，协调 HTTP/1.1 + HTTP/2 各子工厂
 * 保持对现有 createGetRequest / createPostRequest / createResponse 的向后兼容
 */
public class HttpPacketFactory implements INetworkFactory<HttpPacket> {

    // ========== HTTP/1.1 子工厂 ==========
    private final HttpRequestFactory requestFactory = new HttpRequestFactory();
    private final HttpResponseFactory responseFactory = new HttpResponseFactory();
    private final HttpHeaderFactory headerFactory = new HttpHeaderFactory();
    private final HttpBodyFactory bodyFactory = new HttpBodyFactory();
    private final HttpCookieFactory cookieFactory = new HttpCookieFactory();
    private final HttpAuthFactory authFactory = new HttpAuthFactory();
    private final HttpCacheFactory cacheFactory = new HttpCacheFactory();
    private final HttpChunkedFactory chunkedFactory = new HttpChunkedFactory();

    // ========== HTTP/2 子工厂 ==========
    private final Http2FrameFactory http2FrameFactory = new Http2FrameFactory();
    private final Http2SettingsFactory http2SettingsFactory = new Http2SettingsFactory();
    private final Http2StreamFactory http2StreamFactory = new Http2StreamFactory();

    private static final String HTTP_VERSION = "HTTP/1.1";

    // ========== 向后兼容的原有方法 ==========

    public HttpPacket createGetRequest(String uri) {
        HttpPacket packet = new HttpPacket();
        packet.setPacketType("HTTP_REQUEST");
        packet.setMethod("GET");
        packet.setUri(uri);
        packet.setVersion(HTTP_VERSION);
        packet.addHeader("Host", "localhost");
        packet.addHeader("Accept", "*/*");
        packet.addHeader("User-Agent", "DataCartFactoryGame/1.0");
        packet.addHeader("Connection", "keep-alive");
        return packet;
    }

    public HttpPacket createPostRequest(String uri, String body) {
        HttpPacket packet = new HttpPacket();
        packet.setPacketType("HTTP_REQUEST");
        packet.setMethod("POST");
        packet.setUri(uri);
        packet.setVersion(HTTP_VERSION);
        packet.addHeader("Host", "localhost");
        packet.addHeader("Content-Type", "application/json");
        packet.addHeader("Content-Length", String.valueOf(body.length()));
        packet.setBody(body);
        return packet;
    }

    public HttpPacket createResponse(int statusCode, String body) {
        HttpPacket packet = new HttpPacket();
        packet.setPacketType("HTTP_RESPONSE");
        packet.setStatusCode(statusCode);

        HttpResponseFactory.StatusCode sc = HttpResponseFactory.StatusCode.fromCode(statusCode);
        packet.setStatusPhrase(sc != null ? sc.getPhrase() : "Unknown");

        // 使用子工厂构建响应头
        Map<String, String> headers = headerFactory.buildDefaultResponseHeaders(
                body != null && !body.isEmpty() ? "text/html" : "text/plain",
                body != null ? body.length() : 0
        );
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            packet.addHeader(entry.getKey(), entry.getValue());
        }

        if (body != null && !body.isEmpty()) {
            packet.setBody(body);
        }
        return packet;
    }

    public HttpPacket parseRequest(byte[] data) {
        HttpPacket packet = new HttpPacket();
        packet.deserialize(data);
        return packet;
    }

    // ========== 新增扩展方法：使用子工厂构建更丰富的 HTTP 消息 ==========

    /**
     * 使用子工厂创建任意方法请求
     */
    public HttpPacket createRequest(HttpRequestFactory.HttpMethod method, String uri, String host, String body) {
        HttpPacket packet = new HttpPacket();
        packet.setPacketType("HTTP_REQUEST");
        packet.setMethod(method.name());
        packet.setUri(uri);
        packet.setVersion(HTTP_VERSION);

        Map<String, String> headers = headerFactory.buildDefaultRequestHeaders(host);
        if (body != null && !body.isEmpty()) {
            headers.put("Content-Type", "application/json");
            headers.put("Content-Length", String.valueOf(body.length()));
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            packet.addHeader(entry.getKey(), entry.getValue());
        }
        if (body != null && !body.isEmpty()) {
            packet.setBody(body);
        }
        return packet;
    }

    /**
     * 使用完整状态码创建响应
     */
    public HttpPacket createFullResponse(HttpResponseFactory.StatusCode status,
                                          String contentType, String body) {
        HttpPacket packet = new HttpPacket();
        packet.setPacketType("HTTP_RESPONSE");
        packet.setStatusCode(status.getCode());
        packet.setStatusPhrase(status.getPhrase());
        packet.setVersion(HTTP_VERSION);

        Map<String, String> headers = headerFactory.buildDefaultResponseHeaders(
                contentType, body != null ? body.length() : 0
        );
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            packet.addHeader(entry.getKey(), entry.getValue());
        }
        if (body != null && !body.isEmpty()) {
            packet.setBody(body);
        }
        return packet;
    }

    /**
     * 构建带 Cookie 的请求
     */
    public HttpPacket createRequestWithCookies(HttpRequestFactory.HttpMethod method, String uri,
                                                String host, java.util.List<HttpCookieFactory.Cookie> cookies) {
        HttpPacket packet = createRequest(method, uri, host, null);
        String cookieHeader = cookieFactory.buildCookieHeader(cookies);
        if (!cookieHeader.isEmpty()) {
            packet.addHeader("Cookie", cookieHeader);
        }
        return packet;
    }

    /**
     * 构建带认证的请求
     */
    public HttpPacket createRequestWithAuth(HttpRequestFactory.HttpMethod method, String uri,
                                             String host, String authHeader) {
        HttpPacket packet = createRequest(method, uri, host, null);
        packet.addHeader("Authorization", authHeader);
        return packet;
    }

    /**
     * 构建分块传输的请求
     */
    public HttpPacket createChunkedRequest(String uri, String host, java.util.List<byte[]> chunks) {
        HttpPacket packet = createRequest(HttpRequestFactory.HttpMethod.POST, uri, host, null);
        byte[] chunkedBody = chunkedFactory.encodeChunkedBody(chunks);
        packet.addHeader("Transfer-Encoding", "chunked");
        packet.setBody(new String(chunkedBody, java.nio.charset.StandardCharsets.ISO_8859_1));
        return packet;
    }

    // ========== HTTP/2 方法 ==========

    public Http2FrameFactory getHttp2FrameFactory() { return http2FrameFactory; }
    public Http2SettingsFactory getHttp2SettingsFactory() { return http2SettingsFactory; }
    public Http2StreamFactory getHttp2StreamFactory() { return http2StreamFactory; }

    /**
     * 构建 HTTP/2 HEADERS 帧
     */
    public byte[] buildHttp2HeadersFrame(int streamId, byte[] headerBlock, boolean endStream) {
        return http2FrameFactory.buildHeadersFrame(streamId, headerBlock, endStream, true);
    }

    /**
     * 构建 HTTP/2 DATA 帧
     */
    public byte[] buildHttp2DataFrame(int streamId, byte[] data, boolean endStream) {
        return http2FrameFactory.buildDataFrame(streamId, data, endStream);
    }

    /**
     * 构建 HTTP/2 GOAWAY 帧
     */
    public byte[] buildHttp2GoAwayFrame(int lastStreamId, int errorCode, String debug) {
        return http2FrameFactory.buildGoAwayFrame(lastStreamId, errorCode, debug);
    }

    /**
     * 创建 HTTP/2 流
     */
    public Http2StreamFactory.Http2Stream createHttp2Stream() {
        int streamId = http2StreamFactory.allocateClientStreamId();
        return http2StreamFactory.createStream(streamId);
    }

    // ========== 子工厂 Getter ==========

    public HttpRequestFactory getRequestFactory() { return requestFactory; }
    public HttpResponseFactory getResponseFactory() { return responseFactory; }
    public HttpHeaderFactory getHeaderFactory() { return headerFactory; }
    public HttpBodyFactory getBodyFactory() { return bodyFactory; }
    public HttpCookieFactory getCookieFactory() { return cookieFactory; }
    public HttpAuthFactory getAuthFactory() { return authFactory; }
    public HttpCacheFactory getCacheFactory() { return cacheFactory; }
    public HttpChunkedFactory getChunkedFactory() { return chunkedFactory; }

    // ========== INetworkFactory 接口 ==========

    @Override
    public HttpPacket produce() {
        return createGetRequest("/");
    }

    @Override
    public void reset() {
        http2StreamFactory.reset();
        cacheFactory.clear();
    }
}
