package com.never_give_up.automation.demo.factory.security.ipsec;

/** ESP 封装安全载荷 */
public class IpsecEspFactory {
    private int spi = 0xABCDEF12;
    private long seq = 0;

    public byte[] wrapEsp(byte[] payload) {
        byte[] esp = new byte[8 + payload.length + 2];
        esp[0] = (byte) (spi >> 24);
        esp[1] = (byte) (spi >> 16);
        esp[2] = (byte) (spi >> 8);
        esp[3] = (byte) spi;
        esp[4] = (byte) (seq >> 24);
        esp[5] = (byte) (seq >> 16);
        esp[6] = (byte) (seq >> 8);
        esp[7] = (byte) seq;
        System.arraycopy(payload, 0, esp, 8, payload.length);
        seq++;
        return esp;
    }

    public void reset() {
        spi = 0xABCDEF12;
        seq = 0;
    }
}