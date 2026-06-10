package com.never_give_up.automation.demo.factory.link;

public class MplsFactory {
    public int label = 16;
    public int ttl = 255;

    public byte[] addMplsHeader(byte[] packet) {
        byte[] mpls = new byte[4];
        mpls[0] = (byte) (label >> 12);
        mpls[1] = (byte) ((label >> 4) & 0xFF);
        mpls[2] = (byte) ((label & 0x0F) << 4);
        mpls[3] = (byte) ttl;
        byte[] res = new byte[mpls.length + packet.length];
        System.arraycopy(mpls,0,res,0,4);
        System.arraycopy(packet,0,res,4,packet.length);
        return res;
    }

    public void reset() {
        label = 16;
        ttl = 255;
    }
}