package com.never_give_up.automation.demo.network;

import com.never_give_up.automation.demo.model.BasePacket;
import com.never_give_up.automation.demo.model.IpPacket;
import com.never_give_up.automation.demo.factory.function.NatMappingFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Gateway extends NetworkDevice {
    private NatMappingFactory natFactory;
    private List<String> allowedProtocols = new ArrayList<>();
    private List<String> blockedIps = new ArrayList<>();
    private int maxConnections = 1000;
    private int currentConnections = 0;

    public Gateway(String name, String internalIp, String externalIp, String macAddress) {
        this.name = name;
        this.ipAddress = internalIp;
        this.macAddress = macAddress;
        this.natFactory = new NatMappingFactory(externalIp);
        this.allowedProtocols.add("TCP");
        this.allowedProtocols.add("UDP");
        this.allowedProtocols.add("ICMP");
    }

    @Override
    public void processPacket(BasePacket packet) {
        if (!isOnline()) return;

        if (packet instanceof IpPacket) {
            processIpPacket((IpPacket) packet);
        }
    }

    private void processIpPacket(IpPacket packet) {
        if (isBlocked(packet.getSourceIp())) {
            dropPacket(packet, "IP blocked");
            return;
        }

        if (currentConnections >= maxConnections) {
            dropPacket(packet, "Connection limit reached");
            return;
        }

        applyNat(packet);
        forwardPacket(packet);
        currentConnections++;
    }

    private void applyNat(IpPacket packet) {
        NatMappingFactory.NatEntry entry = natFactory.createMapping(
                packet.getSourceIp(), 0
        );
        packet.setSourceIp(entry.getPublicIp());
    }

    private void forwardPacket(IpPacket packet) {
    }

    private boolean isBlocked(String ip) {
        return blockedIps.contains(ip);
    }

    private void dropPacket(IpPacket packet, String reason) {
    }

    public void blockIp(String ip) {
        if (!blockedIps.contains(ip)) {
            blockedIps.add(ip);
        }
    }

    public void unblockIp(String ip) {
        blockedIps.remove(ip);
    }

    public void allowProtocol(String protocol) {
        if (!allowedProtocols.contains(protocol)) {
            allowedProtocols.add(protocol);
        }
    }

    public void denyProtocol(String protocol) {
        allowedProtocols.remove(protocol);
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
