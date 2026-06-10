package com.never_give_up.automation.demo.factory.application;

public class SnmpPacketFactory {
    public byte[] buildGetRequest(String community) {
        byte[] pkt = new byte[30];
        pkt[0] = 0x30; pkt[4] = 0x02;
        return pkt;
    }
}