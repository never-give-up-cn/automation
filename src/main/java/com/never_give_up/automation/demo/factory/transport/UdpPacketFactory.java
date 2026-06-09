package com.never_give_up.automation.demo.factory.transport;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.UdpPacket;

public class UdpPacketFactory implements INetworkFactory<UdpPacket> {
    public UdpPacket createData(int sourcePort, int destPort, byte[] payload) {
        UdpPacket packet = new UdpPacket();
        packet.setPacketType("UDP_DATA");
        packet.setSourcePort(sourcePort);
        packet.setDestinationPort(destPort);
        packet.setPayload(payload);
        packet.setLength(8 + (payload != null ? payload.length : 0));
        packet.setChecksum(0);
        return packet;
    }

    public UdpPacket createQuery(int sourcePort, int destPort) {
        return createData(sourcePort, destPort, new byte[0]);
    }

    @Override
    public UdpPacket produce() {
        return createData(0, 0, new byte[0]);
    }

    @Override
    public void reset() {
    }
}
