package com.never_give_up.automation.demo.factory.application;

public class NtpPacketFactory {
    public byte[] buildNtpRequest(int stratum) {
        byte[] pkt = new byte[48];
        pkt[0] = (byte)(0x10 | stratum);
        return pkt;
    }
}