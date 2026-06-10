package com.never_give_up.automation.demo.factory.dhcp;
//11. DHCP 完整报文工厂（DhcpFullPacketFactory）
public class DhcpFullPacketFactory {
    public byte[] discover() { return createDhcp(1); }
    public byte[] offer() { return createDhcp(2); }
    public byte[] request() { return createDhcp(3); }
    public byte[] ack() { return createDhcp(5); }
    public byte[] release() { return createDhcp(7); }

    private byte[] createDhcp(int msgType) {
        byte[] pkt = new byte[240];
        pkt[0] = 1;
        pkt[240 - 1] = (byte) 0xFF;
        pkt[240 - 2] = (byte) msgType;
        pkt[240 - 3] = 53;
        return pkt;
    }
}