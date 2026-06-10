package com.never_give_up.automation.demo.factory.application.telnet;

public class TelnetPacketFactory {
    private static final byte IAC = (byte) 0xFF;
    private static final byte WILL = (byte) 0xFB;
    private static final byte ECHO = (byte) 0x01;

    public byte[] buildNegotiate() {
        // 协商回显选项
        return new byte[]{IAC, WILL, ECHO};
    }

    public byte[] buildData(String data) {
        return data.getBytes();
    }

    public void reset() {}
}