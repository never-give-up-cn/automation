package com.never_give_up.automation.demo.factory.application;

public class Http23PacketFactory {
    public byte[] buildHttp2Frame(int streamId) {
        byte[] frame = new byte[9];
        frame[1] = 0x01; // HEADERS frame
        frame[5] = (byte)(streamId>>24);
        return frame;
    }
}