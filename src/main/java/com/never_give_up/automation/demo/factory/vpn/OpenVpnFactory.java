package com.never_give_up.automation.demo.factory.vpn;

public class OpenVpnFactory {
    private int opcode = 0x01;
    private int keyId = 0;

    public byte[] buildOpenVpnPacket(byte[] data) {
        byte[] ovpn = new byte[8 + data.length];
        ovpn[0] = (byte) ((opcode << 3) | keyId);
        System.arraycopy(data, 0, ovpn, 8, data.length);
        return ovpn;
    }

    public void reset() {
        opcode = 0x01;
        keyId = 0;
    }
}