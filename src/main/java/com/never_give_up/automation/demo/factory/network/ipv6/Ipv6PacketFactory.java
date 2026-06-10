package com.never_give_up.automation.demo.factory.network.ipv6;

import lombok.Data;
import java.util.Arrays;

@Data
public class Ipv6PacketFactory {
    private int version = 6;
    private int trafficClass;
    private int flowLabel;
    private int payloadLength;
    private byte nextHeader;
    private int hopLimit = 64;
    private byte[] srcIpv6 = new byte[16];
    private byte[] dstIpv6 = new byte[16];

    public byte[] buildIpv6Packet(byte[] payload) {
        this.payloadLength = payload.length;
        byte[] packet = new byte[40 + payloadLength];
        packet[0] = (byte) ((version << 4) | (trafficClass >> 4));
        packet[1] = (byte) (((trafficClass & 0x0F) << 4) | ((flowLabel >> 16) & 0x0F));
        packet[2] = (byte) ((flowLabel >> 8) & 0xFF);
        packet[3] = (byte) (flowLabel & 0xFF);
        packet[4] = (byte) ((payloadLength >> 8) & 0xFF);
        packet[5] = (byte) (payloadLength & 0xFF);
        packet[6] = nextHeader;
        packet[7] = (byte) hopLimit;
        System.arraycopy(srcIpv6, 0, packet, 8, 16);
        System.arraycopy(dstIpv6, 0, packet, 24, 16);
        System.arraycopy(payload, 0, packet, 40, payload.length);
        return packet;
    }

    public void reset() {
        trafficClass = 0;
        flowLabel = 0;
        payloadLength = 0;
        nextHeader = 0;
        hopLimit = 64;
        Arrays.fill(srcIpv6, (byte) 0);
        Arrays.fill(dstIpv6, (byte) 0);
    }
}