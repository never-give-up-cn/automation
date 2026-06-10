package com.never_give_up.automation.demo.factory.application.auth;

import java.security.SecureRandom;

public class RadiusPacketFactory {
    private final SecureRandom random = new SecureRandom();
    private byte[] authenticator = new byte[16];

    public byte[] buildAccessRequest(String user, String pwd) {
        random.nextBytes(authenticator);
        byte[] pkt = new byte[64];
        pkt[0] = 0x01; // Access-Request
        pkt[1] = 0x01;
        System.arraycopy(authenticator, 0, pkt, 4, 16);
        return pkt;
    }

    public void reset() {
        authenticator = new byte[16];
    }
}