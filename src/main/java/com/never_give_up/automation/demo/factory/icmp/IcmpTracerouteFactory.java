package com.never_give_up.automation.demo.factory.icmp;

/** Traceroute 路由追踪（基于ICMP TTL超限） */
public class IcmpTracerouteFactory {
    public int ttl = 1;
    private final int id = 0x5678;

    public byte[] buildTracePacket() {
        byte[] icmp = new byte[40];
        icmp[0] = 0x08;
        icmp[4] = (byte) (id >> 8);
        icmp[5] = (byte) id;
        icmp[6] = (byte) (ttl >> 8);
        icmp[7] = (byte) ttl;
        ttl++;
        return icmp;
    }

    public void reset() {
        ttl = 1;
    }
}