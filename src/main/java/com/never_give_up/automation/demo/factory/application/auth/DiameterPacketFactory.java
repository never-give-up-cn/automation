package com.never_give_up.automation.demo.factory.application.auth;

public class DiameterPacketFactory {
    private int hopByHopId = 1001;
    private int endToEndId = 2001;
    private final int version = 1;

    public byte[] buildDiameterRequest(int cmdCode) {
        byte[] pkt = new byte[20];
        pkt[0] = (byte) version;
        pkt[1] = 0x00;
        pkt[2] = 0x00;
        pkt[3] = 20;
        pkt[4] = (byte) (cmdCode >> 8);
        pkt[5] = (byte) cmdCode;
        pkt[8] = (byte) (hopByHopId >> 8);
        pkt[9] = (byte) hopByHopId;
        pkt[12] = (byte) (endToEndId >> 8);
        pkt[13] = (byte) endToEndId;
        return pkt;
    }

    public void reset() {
        hopByHopId = 1001;
        endToEndId = 2001;
    }
}