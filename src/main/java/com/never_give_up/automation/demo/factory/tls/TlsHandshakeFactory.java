package com.never_give_up.automation.demo.factory.tls;
//12. TLS 握手完整工厂（TlsHandshakeFactory）
public class TlsHandshakeFactory {
    public byte[] clientHello() {
        byte[] pkt = new byte[100];
        pkt[0] = 0x16;
        pkt[1] = 0x03;
        pkt[2] = 0x03;
        pkt[5] = 1;
        return pkt;
    }

    public byte[] serverHello() {
        return new byte[]{0x16, 0x03, 0x03, 0, 0x20, 2};
    }

    public byte[] certificate() {
        return new byte[]{0x16, 0x03, 0x03, 0, 0x30, 11};
    }

    public byte[] finished() {
        return new byte[]{0x16, 0x03, 0x03, 0, 0x10, 20};
    }
}