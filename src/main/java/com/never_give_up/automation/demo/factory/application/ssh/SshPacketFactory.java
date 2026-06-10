package com.never_give_up.automation.demo.factory.application.ssh;

public class SshPacketFactory {
    private String version = "SSH-2.0-OpenSSH_8.9";
    private boolean handshakeDone = false;

    public byte[] buildVersionBanner() {
        return (version + "\r\n").getBytes();
    }

    public byte[] buildKexInit() {
        // 模拟KEX初始化报文
        byte[] pkt = new byte[64];
        pkt[0] = 0x14;
        return pkt;
    }

    public void setHandshakeDone() {
        this.handshakeDone = true;
    }

    public void reset() {
        version = "SSH-2.0-OpenSSH_8.9";
        handshakeDone = false;
    }
}