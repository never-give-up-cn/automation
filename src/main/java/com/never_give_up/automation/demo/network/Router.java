package com.never_give_up.automation.demo.network;

import com.never_give_up.automation.demo.model.BasePacket;
import com.never_give_up.automation.demo.model.IpPacket;
import com.never_give_up.automation.demo.factory.function.RouteTableFactory;
import com.never_give_up.automation.demo.factory.function.NatMappingFactory;
import com.never_give_up.automation.demo.factory.network.IpPacketFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class Router extends NetworkDevice {
    private RouteTableFactory routeTable;
    private NatMappingFactory natFactory;
    private IpPacketFactory ipFactory;
    private List<NetworkInterface> networkInterfaces = new ArrayList<>();
    private int ttlDecrement = 1;
    private boolean natEnabled = false;

    @Data
    public static class NetworkInterface {
        private String name;
        private String ipAddress;
        private String macAddress;
        private int bandwidth;
        private boolean up;

        public NetworkInterface(String name, String ip, String mac) {
            this.name = name;
            this.ipAddress = ip;
            this.macAddress = mac;
            this.bandwidth = 1000;
            this.up = true;
        }
    }

    public Router(String name, String ipAddress, String macAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.routeTable = new RouteTableFactory();
        this.ipFactory = new IpPacketFactory();
    }

    public Router(String name, String ipAddress, String macAddress, String publicIp) {
        this(name, ipAddress, macAddress);
        this.natFactory = new NatMappingFactory(publicIp);
        this.natEnabled = true;
    }

    @Override
    public void processPacket(BasePacket packet) {
        if (!isOnline()) return;

        if (packet instanceof IpPacket) {
            routePacket((IpPacket) packet);
        }
    }

    private void routePacket(IpPacket packet) {
        ipFactory.decrementTtl(packet);

        if (packet.getTtl() <= 0) {
            handleTtlExceeded(packet);
            return;
        }

        if (natEnabled) {
            applyNat(packet);
        }

        RouteTableFactory.RouteEntry route = routeTable.lookup(packet.getDestinationIp());
        if (route != null) {
            forwardPacket(packet, route);
        } else {
            dropPacket(packet, "No route to destination");
        }
    }

    private void applyNat(IpPacket packet) {
        NatMappingFactory.NatEntry entry = natFactory.createMapping(
                packet.getSourceIp(), 0
        );
        packet.setSourceIp(entry.getPublicIp());
    }

    private void forwardPacket(IpPacket packet, RouteTableFactory.RouteEntry route) {
    }

    private void handleTtlExceeded(IpPacket packet) {
    }

    private void dropPacket(IpPacket packet, String reason) {
    }

    public void addInterface(String name, String ip, String mac) {
        NetworkInterface iface = new NetworkInterface(name, ip, mac);
        networkInterfaces.add(iface);
        interfaces.put(name, ip);
    }

    public void addRoute(String network, String nextHop, String interfaceName, int metric) {
        routeTable.addRoute(network, nextHop, interfaceName, metric);
    }

    public void addDefaultRoute(String nextHop, String interfaceName) {
        routeTable.addDefaultRoute(nextHop, interfaceName);
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
