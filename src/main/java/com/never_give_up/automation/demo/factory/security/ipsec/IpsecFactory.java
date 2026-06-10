package com.never_give_up.automation.demo.factory.security.ipsec;

import java.util.Random;
public class IpsecFactory {
    private final Random r = new Random();
    public byte[] buildESP(int spi) {
        byte[] esp = new byte[16];
        esp[0] = (byte)(spi>>24);
        return esp;
    }
}