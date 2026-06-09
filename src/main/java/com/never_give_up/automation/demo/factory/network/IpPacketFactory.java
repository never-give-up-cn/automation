package com.never_give_up.automation.demo.factory.network;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.core.ProtocolConst;
import com.never_give_up.automation.demo.model.IpPacket;

import java.util.Random;

public class IpPacketFactory implements INetworkFactory<IpPacket> {
    private final Random random = new Random();
    private int identificationCounter = 2000;

    public IpPacket createPacket(String srcIp, String dstIp, int protocol, byte[] payload) {
        IpPacket packet = new IpPacket();
        packet.setPacketType("IP");
        packet.setSourceIp(srcIp);
        packet.setDestinationIp(dstIp);
        packet.setProtocol(protocol);
        packet.setTotalLength(ProtocolConst.IP_HEADER_SIZE + (payload != null ? payload.length : 0));
        packet.setIdentification(identificationCounter++);
        packet.setTtl(ProtocolConst.DEFAULT_TTL);
        packet.setPayload(payload);
        return packet;
    }

    public IpPacket createTcpPacket(String srcIp, String dstIp, byte[] tcpPayload) {
        return createPacket(srcIp, dstIp, ProtocolConst.IP_PROTO_TCP, tcpPayload);
    }

    public IpPacket createUdpPacket(String srcIp, String dstIp, byte[] udpPayload) {
        return createPacket(srcIp, dstIp, ProtocolConst.IP_PROTO_UDP, udpPayload);
    }

    public IpPacket createIcmpPacket(String srcIp, String dstIp, byte[] icmpPayload) {
        return createPacket(srcIp, dstIp, ProtocolConst.IP_PROTO_ICMP, icmpPayload);
    }

    public void decrementTtl(IpPacket packet) {
        packet.setTtl(packet.getTtl() - 1);
    }

    @Override
    public IpPacket produce() {
        return createPacket("0.0.0.0", "0.0.0.0", ProtocolConst.IP_PROTO_TCP, new byte[0]);
    }

    @Override
    public void reset() {
        identificationCounter = 2000;
    }
}
