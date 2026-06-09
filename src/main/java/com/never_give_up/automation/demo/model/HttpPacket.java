package com.never_give_up.automation.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class HttpPacket extends BasePacket {
    private String method;
    private String uri;
    private String version = "HTTP/1.1";
    private int statusCode;
    private String statusPhrase;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public boolean isRequest() { return method != null; }
    public boolean isResponse() { return statusCode > 0; }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String name) {
        return headers.getOrDefault(name, null);
    }

    @Override
    public byte[] serialize() {
        StringBuilder sb = new StringBuilder();

        if (isRequest()) {
            sb.append(method).append(" ").append(uri).append(" ").append(version).append("\r\n");
        } else {
            sb.append(version).append(" ").append(statusCode).append(" ").append(statusPhrase).append("\r\n");
        }

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\r\n");
        }
        sb.append("\r\n");

        if (body != null && !body.isEmpty()) {
            sb.append(body);
        }

        return sb.toString().getBytes();
    }

    @Override
    public void deserialize(byte[] data) {
        String[] lines = new String(data).split("\r\n");
        if (lines.length == 0) return;

        String firstLine = lines[0];
        if (firstLine.startsWith("HTTP/")) {
            String[] parts = firstLine.split(" ", 3);
            version = parts[0];
            statusCode = Integer.parseInt(parts[1]);
            statusPhrase = parts[2];
        } else {
            String[] parts = firstLine.split(" ", 3);
            method = parts[0];
            uri = parts[1];
            version = parts[2];
        }

        int i = 1;
        while (i < lines.length && !lines[i].isEmpty()) {
            int colonIndex = lines[i].indexOf(": ");
            if (colonIndex > 0) {
                headers.put(lines[i].substring(0, colonIndex), lines[i].substring(colonIndex + 2));
            }
            i++;
        }

        if (i < lines.length) {
            StringBuilder bodyBuilder = new StringBuilder();
            for (int j = i + 1; j < lines.length; j++) {
                bodyBuilder.append(lines[j]);
                if (j < lines.length - 1) bodyBuilder.append("\r\n");
            }
            body = bodyBuilder.toString();
        }
    }
}
