package com.never_give_up.automation.demo.factory.network;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.IcmpPacket;

import java.util.Random;

public class IcmpPacketFactory implements INetworkFactory<IcmpPacket> {
    private final Random random = new Random();

    public IcmpPacket createEchoRequest(int identifier, int sequenceNum) {
        IcmpPacket packet = new IcmpPacket();
        packet.setPacketType("ICMP_ECHO_REQUEST");
        packet.setType(8);
        packet.setCode(0);
        packet.setIdentifier(identifier);
        packet.setSequenceNumber(sequenceNum);
        packet.setTimestamp(System.currentTimeMillis());
        return packet;
    }

    public IcmpPacket createEchoReply(int identifier, int sequenceNum) {
        IcmpPacket packet = new IcmpPacket();
        packet.setPacketType("ICMP_ECHO_REPLY");
        packet.setType(0);
        packet.setCode(0);
        packet.setIdentifier(identifier);
        packet.setSequenceNumber(sequenceNum);
        return packet;
    }

    public IcmpPacket createTimeExceeded(int identifier, int sequenceNum) {
        IcmpPacket packet = new IcmpPacket();
        packet.setPacketType("ICMP_TIME_EXCEEDED");
        packet.setType(11);
        packet.setCode(0);
        packet.setIdentifier(identifier);
        packet.setSequenceNumber(sequenceNum);
        return packet;
    }

    public IcmpPacket createDestinationUnreachable(int code) {
        IcmpPacket packet = new IcmpPacket();
        packet.setPacketType("ICMP_DEST_UNREACHABLE");
        packet.setType(3);
        packet.setCode(code);
        return packet;
    }

    @Override
    public IcmpPacket produce() {
        return createEchoRequest(random.nextInt(65536), 1);
    }

    @Override
    public void reset() {
    }
}
