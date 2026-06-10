package com.never_give_up.automation.demo.factory.security.ipsec;

/** AH 认证头 */
public class IpsecAhFactory {
    private int spi = 0x12345678;
    private long seq = 0;

    public byte[] wrapAh(byte[] ipPacket) {
        byte[] ah = new byte[12 + ipPacket.length];
        ah[0] = 0x04; // Next Header
        ah[1] = 0x03; // Payload Len
        ah[4] = (byte) (spi >> 24);
        ah[5] = (byte) (spi >> 16);
        ah[6] = (byte) (spi >> 8);
        ah[7] = (byte) spi;
        ah[8] = (byte) (seq >> 24);
        ah[9] = (byte) (seq >> 16);
        ah[10] = (byte) (seq >> 8);
        ah[11] = (byte) seq;
        System.arraycopy(ipPacket, 0, ah, 12, ipPacket.length);
        seq++;
        return ah;
    }

    public void reset() {
        spi = 0x12345678;
        seq = 0;
    }
}