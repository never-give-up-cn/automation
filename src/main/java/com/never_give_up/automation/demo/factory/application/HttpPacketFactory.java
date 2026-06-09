package com.never_give_up.automation.demo.factory.application;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.HttpPacket;

import java.util.HashMap;
import java.util.Map;

public class HttpPacketFactory implements INetworkFactory<HttpPacket> {
    private static final String HTTP_VERSION = "HTTP/1.1";

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
        packet.setVersion(HTTP_VERSION);

        switch (statusCode) {
            case 200: packet.setStatusPhrase("OK"); break;
            case 301: packet.setStatusPhrase("Moved Permanently"); break;
            case 404: packet.setStatusPhrase("Not Found"); break;
            case 500: packet.setStatusPhrase("Internal Server Error"); break;
            default: packet.setStatusPhrase("Unknown");
        }

        if (body != null && !body.isEmpty()) {
            packet.addHeader("Content-Type", "text/html");
            packet.addHeader("Content-Length", String.valueOf(body.length()));
            packet.setBody(body);
        }

        return packet;
    }

    public HttpPacket parseRequest(byte[] data) {
        HttpPacket packet = new HttpPacket();
        packet.deserialize(data);
        return packet;
    }

    @Override
    public HttpPacket produce() {
        return createGetRequest("/");
    }

    @Override
    public void reset() {
    }
}
