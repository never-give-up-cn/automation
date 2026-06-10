package com.never_give_up.automation.demo.factory.tool;

/** Curl HTTP 请求工具 */
public class CurlFactory {

    public byte[] buildGetRequest(String url) {
        String req = "GET " + url + " HTTP/1.1\r\nHost: localhost\r\n\r\n";
        return req.getBytes();
    }

    public byte[] buildPostRequest(String url, String body) {
        int contentLength = body.getBytes().length;
        String req = "POST " + url + " HTTP/1.1\r\n"
                + "Host: localhost\r\n"
                + "Content-Length: " + contentLength + "\r\n"
                + "\r\n"
                + body;
        return req.getBytes();
    }

    public void reset() {
    }
}