package com.never_give_up.automation.demo.factory.vpn;

public class L2tpFactory {
    private int tunnelId = 100;
    private int sessionId = 200;

    public byte[] buildL2tpPacket(byte[] payload) {
        byte[] l2tp = new byte[6 + payload.length];
        l2tp[0] = (byte) 0x80; // Flags

        // 修复：全部显式强转 byte
        l2tp[2] = (byte) ((tunnelId >> 8) & 0xFF);
        l2tp[3] = (byte) (tunnelId & 0xFF);
        l2tp[4] = (byte) ((sessionId >> 8) & 0xFF);
        l2tp[5] = (byte) (sessionId & 0xFF);

        System.arraycopy(payload, 0, l2tp, 6, payload.length);
        return l2tp;
    }

    public void reset() {
        tunnelId = 100;
        sessionId = 200;
    }
}