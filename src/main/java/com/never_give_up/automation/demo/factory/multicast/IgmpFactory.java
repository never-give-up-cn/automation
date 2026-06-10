package com.never_give_up.automation.demo.factory.multicast;
//17. IGMP 组播工厂（IgmpFactory）
public class IgmpFactory {
    public byte[] joinGroup(int groupIp) {
        byte[] pkt = new byte[8];
        pkt[0] = 0x11;
        pkt[4] = (byte) (groupIp >> 24);
        pkt[5] = (byte) (groupIp >> 16);
        pkt[6] = (byte) (groupIp >> 8);
        pkt[7] = (byte) groupIp;
        return pkt;
    }

    public byte[] leaveGroup(int groupIp) {
        byte[] pkt = new byte[8];
        pkt[0] = 0x17;
        return pkt;
    }
}