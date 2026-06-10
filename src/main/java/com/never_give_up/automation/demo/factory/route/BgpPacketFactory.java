package com.never_give_up.automation.demo.factory.route;

public class BgpPacketFactory {
    public byte[] buildOpen(int myAs) {
        byte[] pkt = new byte[19];
        // 全部强转 byte，修复 int → byte 错误
        pkt[0] = (byte) 0xFF;
        pkt[1] = (byte) 0x01;
        pkt[10] = (byte) (myAs >> 8);
        pkt[11] = (byte) myAs;
        return pkt;
    }

    public byte[] buildUpdate() {
        return new byte[40];
    }
}