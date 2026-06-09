package com.never_give_up.automation.demo.network;

import com.never_give_up.automation.demo.model.BasePacket;
import com.never_give_up.automation.demo.model.DhcpPacket;
import com.never_give_up.automation.demo.factory.application.DhcpPacketFactory;
import com.never_give_up.automation.demo.factory.address.IpAddressFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class DhcpServer extends NetworkDevice {
    private DhcpPacketFactory dhcpFactory;
    private IpAddressFactory ipFactory;
    private Map<String, String> addressPool = new HashMap<>();
    private Map<String, Long> leaseTable = new HashMap<>();
    private long defaultLeaseTime = 3600000;
    private String subnetMask = "255.255.255.0";
    private String gateway;
    private String dnsServer;

    public DhcpServer(String name, String ipAddress, String macAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.dhcpFactory = new DhcpPacketFactory();
        this.ipFactory = new IpAddressFactory();
    }

    @Override
    public void processPacket(BasePacket packet) {
        if (!isOnline()) return;

        if (packet instanceof DhcpPacket) {
            processDhcpMessage((DhcpPacket) packet);
        }
    }

    private void processDhcpMessage(DhcpPacket packet) {
        switch (packet.getMessageType()) {
            case 1:
                handleDiscover(packet);
                break;
            case 3:
                handleRequest(packet);
                break;
            case 5:
                handleRelease(packet);
                break;
        }
    }

    private void handleDiscover(DhcpPacket discover) {
        String offeredIp = allocateIp(discover.getClientMac());
        if (offeredIp != null) {
            DhcpPacket offer = dhcpFactory.createOffer(
                    discover.getClientMac(),
                    offeredIp,
                    ipAddress
            );
            sendPacket(offer);
        }
    }

    private void handleRequest(DhcpPacket request) {
        String assignedIp = request.getRequestedIp();
        if (assignedIp == null) {
            assignedIp = getAllocatedIp(request.getClientMac());
        }

        if (assignedIp != null) {
            DhcpPacket ack = dhcpFactory.createAck(
                    request.getClientMac(),
                    assignedIp,
                    ipAddress
            );
            ack.setLeaseTime((int) (defaultLeaseTime / 1000));
            sendPacket(ack);
        }
    }

    private void handleRelease(DhcpPacket release) {
        String ip = getAllocatedIp(release.getClientMac());
        if (ip != null) {
            releaseIp(ip);
        }
    }

    private String allocateIp(String clientMac) {
        String ip = ipFactory.allocatePrivateIp(clientMac);
        addressPool.put(clientMac, ip);
        leaseTable.put(clientMac, System.currentTimeMillis() + defaultLeaseTime);
        return ip;
    }

    private String getAllocatedIp(String clientMac) {
        return addressPool.get(clientMac);
    }

    private void releaseIp(String ip) {
        ipFactory.releaseIp(ip);
        addressPool.values().remove(ip);
    }

    public void addAddressPool(String startIp, String endIp) {
    }

    public void setLeaseTime(long leaseTimeMs) {
        this.defaultLeaseTime = leaseTimeMs;
    }

    private void sendPacket(BasePacket packet) {
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
