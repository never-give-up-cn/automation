package com.never_give_up.automation.demo.factory.route;

public class OspfPacketFactory {
    public byte[] buildHello(int routerId, int areaId) {
        byte[] pkt = new byte[24];
        pkt[0] = 0x02;
        pkt[4] = (byte)(routerId>>24);
        pkt[8] = (byte)(areaId>>24);
        pkt[12] = 0; // Auth
        return pkt;
    }
}