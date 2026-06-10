package com.never_give_up.automation.demo.factory.nd;

//18. 邻居发现 ND 工厂（NdpFactory）
public class NdpFactory {
    public byte[] neighborSolicitation(byte[] targetIp) {
        byte[] pkt = new byte[24];
        pkt[0] = (byte) 0x87;
        System.arraycopy(targetIp, 0, pkt, 8, 16);
        return pkt;
    }

    public byte[] neighborAdvertisement(byte[] srcMac) {
        byte[] pkt = new byte[24];
        pkt[0] = (byte) 0x88;
        System.arraycopy(srcMac, 0, pkt, 24 - 6, 6);
        return pkt;
    }
}
