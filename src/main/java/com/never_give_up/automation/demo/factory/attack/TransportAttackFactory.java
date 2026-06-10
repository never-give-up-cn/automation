package com.never_give_up.automation.demo.factory.attack;

public class TransportAttackFactory {
    public byte[] buildSynFlood(int fakeSrcIp) {
        byte[] tcp = new byte[20];
        tcp[0] = 0x02; // SYN flag
        return tcp;
    }
    public byte[] buildLandAttack(int ip) {
        return buildSynFlood(ip);
    }
}