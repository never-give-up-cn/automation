package com.never_give_up.automation.demo.factory.application.rtp;

public class RtpPacketFactory {
    private int seq = 0;
    private long timestamp = 0;
    private int ssrc = 12345;
    private final int version = 2;

    public byte[] buildRtpPacket(byte[] payload) {
        byte[] rtp = new byte[12 + payload.length];
        rtp[0] = (byte) (version << 6);
        rtp[1] = 0x00;
        rtp[2] = (byte) (seq >> 8);
        rtp[3] = (byte) seq;
        rtp[4] = (byte) (timestamp >> 24);
        rtp[5] = (byte) (timestamp >> 16);
        rtp[6] = (byte) (timestamp >> 8);
        rtp[7] = (byte) timestamp;
        rtp[8] = (byte) (ssrc >> 24);
        rtp[9] = (byte) (ssrc >> 16);
        rtp[10] = (byte) (ssrc >> 8);
        rtp[11] = (byte) ssrc;
        System.arraycopy(payload, 0, rtp, 12, payload.length);

        seq++;
        timestamp += 160;
        return rtp;
    }

    public void reset() {
        seq = 0;
        timestamp = 0;
        ssrc = 12345;
    }
}