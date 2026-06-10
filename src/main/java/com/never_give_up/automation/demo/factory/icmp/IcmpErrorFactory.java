package com.never_give_up.automation.demo.factory.icmp;
//4. ICMP 错误报文工厂（IcmpErrorFactory）
public class IcmpErrorFactory {
    public byte[] timeExceeded(byte[] originalIpPacket) {
        byte[] pkt = new byte[8 + originalIpPacket.length];
        pkt[0] = 11;
        pkt[1] = 0;
        System.arraycopy(originalIpPacket, 0, pkt, 8, originalIpPacket.length);
        pkt[2] = (byte) 0xFF;
        return pkt;
    }

    public byte[] destUnreachable(byte[] originalIpPacket) {
        byte[] pkt = new byte[8 + originalIpPacket.length];
        pkt[0] = 3;
        pkt[1] = 0;
        System.arraycopy(originalIpPacket, 0, pkt, 8, originalIpPacket.length);
        return pkt;
    }

    public byte[] redirect(byte[] originalIpPacket) {
        byte[] pkt = new byte[8 + originalIpPacket.length];
        pkt[0] = 5;
        pkt[1] = 0;
        return pkt;
    }
}