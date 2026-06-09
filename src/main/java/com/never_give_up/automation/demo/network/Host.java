package com.never_give_up.automation.demo.network;

import com.never_give_up.automation.demo.model.BasePacket;
import com.never_give_up.automation.demo.model.TcpPacket;
import com.never_give_up.automation.demo.model.UdpPacket;
import com.never_give_up.automation.demo.factory.transport.TcpPacketFactory;
import com.never_give_up.automation.demo.factory.transport.UdpPacketFactory;
import com.never_give_up.automation.demo.factory.application.HttpPacketFactory;
import com.never_give_up.automation.demo.factory.application.DnsPacketFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Host extends NetworkDevice {
    private String os;
    private List<Integer> openPorts = new ArrayList<>();
    private TcpPacketFactory tcpFactory;
    private UdpPacketFactory udpFactory;
    private HttpPacketFactory httpFactory;
    private DnsPacketFactory dnsFactory;

    private boolean dhcpEnabled = false;
    private boolean dnsEnabled = true;
    private String defaultGateway;
    private String dnsServer;

    public Host(String name, String ipAddress, String macAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.tcpFactory = new TcpPacketFactory();
        this.udpFactory = new UdpPacketFactory();
        this.httpFactory = new HttpPacketFactory();
        this.dnsFactory = new DnsPacketFactory();
    }

    @Override
    public void processPacket(BasePacket packet) {
        if (!isOnline()) return;

        if (packet instanceof TcpPacket) {
            processTcpPacket((TcpPacket) packet);
        } else if (packet instanceof UdpPacket) {
            processUdpPacket((UdpPacket) packet);
        }
    }

    private void processTcpPacket(TcpPacket packet) {
        switch (packet.getFlags()) {
            case "SYN":
                handleTcpSyn(packet);
                break;
            case "SYN,ACK":
                handleTcpSynAck(packet);
                break;
            case "ACK":
                handleTcpAck(packet);
                break;
            case "FIN,ACK":
                handleTcpFin(packet);
                break;
            default:
                handleTcpData(packet);
        }
    }

    private void handleTcpSyn(TcpPacket packet) {
        if (openPorts.contains(packet.getDestinationPort())) {
            TcpPacket synAck = tcpFactory.createSynAck(
                    packet.getSequenceNumber(),
                    packet.getDestinationPort()
            );
            synAck.setSourcePort(packet.getDestinationPort());
            sendPacket(synAck);
        }
    }

    private void handleTcpSynAck(TcpPacket packet) {
        TcpPacket ack = tcpFactory.createAck(
                packet.getSequenceNumber(),
                packet.getDestinationPort()
        );
        ack.setSourcePort(packet.getDestinationPort());
        sendPacket(ack);
    }

    private void handleTcpAck(TcpPacket packet) {
    }

    private void handleTcpFin(TcpPacket packet) {
        TcpPacket ack = tcpFactory.createAck(
                packet.getSequenceNumber(),
                packet.getDestinationPort()
        );
        sendPacket(ack);
    }

    private void handleTcpData(TcpPacket packet) {
        TcpPacket ack = tcpFactory.createAck(
                packet.getSequenceNumber(),
                packet.getDestinationPort()
        );
        sendPacket(ack);
    }

    private void processUdpPacket(UdpPacket packet) {
        // UDP 无连接处理
    }

    public void sendPacket(BasePacket packet) {
        // 由上层网络层处理
    }

    public void openPort(int port) {
        if (!openPorts.contains(port)) {
            openPorts.add(port);
        }
    }

    public void closePort(int port) {
        openPorts.remove(Integer.valueOf(port));
    }

    @Override
    public void start() {
        status = DeviceStatus.ONLINE;
    }

    @Override
    public void stop() {
        status = DeviceStatus.OFFLINE;
    }
}
