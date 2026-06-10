package com.never_give_up.automation.demo.factory.vpn;

public class WireguardFactory {
    private int type = 1; // Initiation Packet

    public byte[] buildWgPacket(byte[] payload) {
        byte[] wg = new byte[16 + payload.length];
        wg[0] = (byte) type;
        return wg;
    }

    public void reset() {
        type = 1;
    }
}