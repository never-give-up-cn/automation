package com.never_give_up.automation.demo.factory.application;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.DhcpPacket;

import java.util.Random;

public class DhcpPacketFactory  implements INetworkFactory<DhcpPacket> {
    private final Random random = new Random();

    public DhcpPacket createDiscover(String clientMac) {
        DhcpPacket packet = new DhcpPacket();
        packet.setPacketType("DHCP_DISCOVER");
        packet.setMessageType(1);
        packet.setTransactionId(generateTransactionId());
        packet.setClientMac(clientMac);
        packet.setClientIp("0.0.0.0");
        return packet;
    }

    public DhcpPacket createOffer(String clientMac, String offeredIp, String serverIp) {
        DhcpPacket packet = new DhcpPacket();
        packet.setPacketType("DHCP_OFFER");
        packet.setMessageType(2);
        packet.setTransactionId(generateTransactionId());
        packet.setClientMac(clientMac);
        packet.setOfferedIp(offeredIp);
        packet.setServerIp(serverIp);
        packet.setLeaseTime(3600);
        return packet;
    }

    public DhcpPacket createRequest(String clientMac, String requestedIp, String serverIp) {
        DhcpPacket packet = new DhcpPacket();
        packet.setPacketType("DHCP_REQUEST");
        packet.setMessageType(3);
        packet.setTransactionId(generateTransactionId());
        packet.setClientMac(clientMac);
        packet.setRequestedIp(requestedIp);
        packet.setServerIp(serverIp);
        return packet;
    }

    public DhcpPacket createAck(String clientMac, String assignedIp, String serverIp) {
        DhcpPacket packet = new DhcpPacket();
        packet.setPacketType("DHCP_ACK");
        packet.setMessageType(5);
        packet.setTransactionId(generateTransactionId());
        packet.setClientMac(clientMac);
        packet.setOfferedIp(assignedIp);
        packet.setServerIp(serverIp);
        packet.setLeaseTime(3600);
        return packet;
    }

    private String generateTransactionId() {
        return String.format("%08X", random.nextInt());
    }

    @Override
    public DhcpPacket produce() {
        return createDiscover("00:00:00:00:00:00");
    }

    @Override
    public void reset() {
    }
}
