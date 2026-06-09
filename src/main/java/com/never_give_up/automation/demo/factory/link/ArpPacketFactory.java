package com.never_give_up.automation.demo.factory.link;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.ArpPacket;

public class ArpPacketFactory implements INetworkFactory<ArpPacket> {
    public ArpPacket createRequest(String senderMac, String senderIp, String targetIp) {
        ArpPacket packet = new ArpPacket();
        packet.setPacketType("ARP_REQUEST");
        packet.setOperation(1);
        packet.setSenderMac(senderMac);
        packet.setSenderIp(senderIp);
        packet.setTargetMac("00:00:00:00:00:00");
        packet.setTargetIp(targetIp);
        return packet;
    }

    public ArpPacket createReply(String senderMac, String senderIp, String targetMac, String targetIp) {
        ArpPacket packet = new ArpPacket();
        packet.setPacketType("ARP_REPLY");
        packet.setOperation(2);
        packet.setSenderMac(senderMac);
        packet.setSenderIp(senderIp);
        packet.setTargetMac(targetMac);
        packet.setTargetIp(targetIp);
        return packet;
    }

    @Override
    public ArpPacket produce() {
        return createRequest("00:00:00:00:00:00", "0.0.0.0", "0.0.0.0");
    }

    @Override
    public void reset() {
    }
}
