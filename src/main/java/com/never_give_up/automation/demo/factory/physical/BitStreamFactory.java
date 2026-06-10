package com.never_give_up.automation.demo.factory.physical;

public class BitStreamFactory {
    public byte[] buildPreamble() {
        return new byte[]{0x55,0x55,0x55,0x55,0x55,0x55,0x55};
    }
    public byte buildSFD() {
        return (byte) 0xD5;
    }
    public byte[] toBitStream(byte[] frame) {
        byte[] pre = buildPreamble();
        byte[] res = new byte[pre.length+1+frame.length];
        System.arraycopy(pre,0,res,0,7);
        res[7] = buildSFD();
        System.arraycopy(frame,0,res,8,frame.length);
        return res;
    }
}