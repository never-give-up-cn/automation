package com.never_give_up.automation.demo.factory.transition;

public class Nat64Factory {
    public byte[] convertIpv4ToIpv6(byte[] ipv4) {
        byte[] ipv6 = new byte[16];
        ipv6[0] = 0x64; ipv6[1] = (byte)0xFF;
        ipv6[2] = (byte)0x9B;
        System.arraycopy(ipv4,0,ipv6,12,4);
        return ipv6;
    }
}