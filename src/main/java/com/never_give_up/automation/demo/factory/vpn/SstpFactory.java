package com.never_give_up.automation.demo.factory.vpn;

public class SstpFactory {
    private int msgType = 0x01;

    public byte[] buildSstpControl() {
        byte[] sstp = new byte[18];
        sstp[0] = (byte) msgType;
        return sstp;
    }

    public void reset() {
        msgType = 0x01;
    }
}