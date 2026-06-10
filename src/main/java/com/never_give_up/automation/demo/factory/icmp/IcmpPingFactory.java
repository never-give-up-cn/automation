package com.never_give_up.automation.demo.factory.icmp;

/** ICMP Echo Ping 实现 */
public class IcmpPingFactory {
    public int seq = 0;
    private final int id = 0x1234;

    public byte[] buildEchoRequest() {
        byte[] icmp = new byte[64];
        icmp[0] = 0x08; // Echo Request
        icmp[1] = 0x00;
        icmp[2] = 0x00;
        icmp[3] = 0x00;
        icmp[4] = (byte) (id >> 8);
        icmp[5] = (byte) id;
        icmp[6] = (byte) (seq >> 8);
        icmp[7] = (byte) seq;
        seq++;
        return icmp;
    }

    public byte[] buildEchoReply() {
        byte[] reply = buildEchoRequest();
        reply[0] = 0x00; // Echo Reply
        return reply;
    }

    public void reset() {
        seq = 0;
    }
}