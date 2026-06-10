package com.never_give_up.automation.demo.factory.application.rtp;

public class RtcpPacketFactory {
    private int reportCount = 0;

    public byte[] buildSenderReport(int ssrc) {
        byte[] rtcp = new byte[28];
        rtcp[0] = (byte) (2 << 6);
        rtcp[1] = 0x00;
        rtcp[2] = 0x00;
        rtcp[3] = 0x1B;
        rtcp[4] = (byte) (ssrc >> 24);
        rtcp[5] = (byte) (ssrc >> 16);
        rtcp[6] = (byte) (ssrc >> 8);
        rtcp[7] = (byte) ssrc;
        reportCount++;
        return rtcp;
    }

    public void reset() {
        reportCount = 0;
    }
}