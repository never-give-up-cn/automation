package com.never_give_up.automation.demo.factory.link;

public class PppoeFactory {
    public byte[] buildPADI(int sessionId) {
        byte[] pkt = new byte[16];
        // 全部强转 byte，修复 int → byte 错误
        pkt[0] = (byte) 0x11;
        pkt[1] = (byte) 0x00;
        pkt[2] = (byte) (sessionId >> 8);
        pkt[3] = (byte) sessionId;
        return pkt;
    }

    public byte[] buildLCPRequest() {
        // 全部强转 byte，修复类型错误
        return new byte[]{
                (byte) 0xC0,
                (byte) 0x21,
                (byte) 0x01,
                (byte) 0x01,
                (byte) 0x00,
                (byte) 0x04
        };
    }
}