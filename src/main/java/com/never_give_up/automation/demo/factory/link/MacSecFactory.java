package com.never_give_up.automation.demo.factory.link;

import java.util.Random;
public class MacSecFactory {
    private final Random r = new Random();
    public byte[] buildSecTAG(long sci, int pn) {
        byte[] tag = new byte[16];
        tag[0] = (byte)(sci>>56); tag[1]=(byte)(sci>>48);
        tag[2] = (byte)(pn>>24); tag[3]=(byte)(pn>>16);
        return tag;
    }
    public byte[] buildICV() {
        byte[] icv = new byte[16];
        r.nextBytes(icv);
        return icv;
    }
}