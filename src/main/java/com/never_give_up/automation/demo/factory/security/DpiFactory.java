package com.never_give_up.automation.demo.factory.security;

/** 深度包检测 DPI */
public class DpiFactory {
    public enum AppProto { UNKNOWN, HTTP, HTTPS, FTP, SIP }

    public AppProto detectProtocol(byte[] packet) {
        if (packet.length < 8) return AppProto.UNKNOWN;
        // 简易特征识别
        String head = new String(packet, 0, 4).toUpperCase();
        if (head.startsWith("GET ") || head.startsWith("POST")) return AppProto.HTTP;
        return AppProto.UNKNOWN;
    }

    public void reset() {}
}