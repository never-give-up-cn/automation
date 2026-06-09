package com.never_give_up.automation.demo.adapter;

import com.never_give_up.automation.demo.DataCartFactoryGame;
import com.never_give_up.automation.demo.animation.DataCart;
import com.never_give_up.automation.demo.model.*;

public class PacketAdapter {

    public static BasePacket dataCartToPacket(DataCart cart) {
        switch (cart.cartType) {
            case "SYN":
            case "SYN_ACK":
            case "ACK":
            case "DATA":
            case "FIN":
                return convertToTcpPacket(cart);

            case "DNS_QUERY":
            case "DNS_RESPONSE":
                return convertToDnsPacket(cart);

            case "DHCP_DISCOVER":
            case "DHCP_OFFER":
            case "DHCP_REQUEST":
            case "DHCP_ACK":
                return convertToDhcpPacket(cart);

            case "ICMP_ECHO_REQ":
            case "ICMP_ECHO_REPLY":
            case "ICMP_TIMEEXCEEDED":
                return convertToIcmpPacket(cart);

            default:
                return null;
        }
    }

    private static TcpPacket convertToTcpPacket(DataCart cart) {
        TcpPacket packet = new TcpPacket();
        packet.setPacketType(cart.cartType);
        packet.setSequenceNumber(cart.sequenceNumber);
        packet.setAcknowledgmentNumber(cart.ackNumber);
        packet.setWindowSize(cart.advertisedWindow);

        if (cart.cartType.equals("SYN")) {
            packet.setFlags("SYN");
        } else if (cart.cartType.equals("SYN_ACK")) {
            packet.setFlags("SYN,ACK");
        } else if (cart.cartType.equals("ACK")) {
            packet.setFlags("ACK");
        } else if (cart.cartType.equals("DATA")) {
            packet.setFlags("PSH,ACK");
        } else if (cart.cartType.equals("FIN")) {
            packet.setFlags("FIN,ACK");
        }

        return packet;
    }

    private static DnsPacket convertToDnsPacket(DataCart cart) {
        DnsPacket packet = new DnsPacket();
        packet.setPacketType(cart.cartType);
        packet.setDomain(cart.domain);
        packet.setTransactionId(cart.sequenceNumber);
        return packet;
    }

    private static DhcpPacket convertToDhcpPacket(DataCart cart) {
        DhcpPacket packet = new DhcpPacket();
        packet.setPacketType(cart.cartType);
        switch (cart.cartType) {
            case "DHCP_DISCOVER": packet.setMessageType(1); break;
            case "DHCP_OFFER": packet.setMessageType(2); break;
            case "DHCP_REQUEST": packet.setMessageType(3); break;
            case "DHCP_ACK": packet.setMessageType(4); break;
        }
        return packet;
    }

    private static IcmpPacket convertToIcmpPacket(DataCart cart) {
        IcmpPacket packet = new IcmpPacket();
        packet.setPacketType(cart.cartType);
        packet.setTtl(cart.ttl);
        return packet;
    }

    public static void packetToDataCart(BasePacket packet, DataCart cart) {
        if (packet instanceof TcpPacket) {
            applyTcpPacket((TcpPacket) packet, cart);
        } else if (packet instanceof DnsPacket) {
            applyDnsPacket((DnsPacket) packet, cart);
        }
    }

    private static void applyTcpPacket(TcpPacket packet, DataCart cart) {
        cart.sequenceNumber = packet.getSequenceNumber();
        cart.ackNumber = packet.getAcknowledgmentNumber();
        cart.advertisedWindow = packet.getWindowSize();
    }

    private static void applyDnsPacket(DnsPacket packet, DataCart cart) {
        cart.domain = packet.getDomain();
        cart.resolvedIp = packet.getIpAddress();
    }
}
