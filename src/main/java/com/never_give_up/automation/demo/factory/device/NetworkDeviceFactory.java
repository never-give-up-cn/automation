package com.never_give_up.automation.demo.factory.device;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.network.*;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class NetworkDeviceFactory implements INetworkFactory<NetworkDevice> {
    private final Map<String, NetworkDevice> createdDevices = new HashMap<>();
    private int hostCounter = 0;
    private int routerCounter = 0;
    private int gatewayCounter = 0;
    private int dnsServerCounter = 0;
    private int dhcpServerCounter = 0;

    public Host createHost(String name, String ipAddress, String macAddress) {
        Host host = new Host(name, ipAddress, macAddress);
        createdDevices.put(name, host);
        return host;
    }

    public Host createHost(String ipAddress, String macAddress) {
        hostCounter++;
        return createHost("Host-" + hostCounter, ipAddress, macAddress);
    }

    public Host createHostWithDefaults() {
        hostCounter++;
        String ip = "192.168.1." + (100 + hostCounter);
        String mac = String.format("00:1A:2B:00:00:%02X", hostCounter);
        return createHost("Host-" + hostCounter, ip, mac);
    }

    public Router createRouter(String name, String ipAddress, String macAddress) {
        Router router = new Router(name, ipAddress, macAddress);
        createdDevices.put(name, router);
        return router;
    }

    public Router createRouter(String name, String ipAddress, String macAddress, String publicIp) {
        Router router = new Router(name, ipAddress, macAddress, publicIp);
        createdDevices.put(name, router);
        return router;
    }

    public Router createRouter(String ipAddress, String macAddress) {
        routerCounter++;
        return createRouter("Router-" + routerCounter, ipAddress, macAddress);
    }

    public Router createRouterWithNat(String name, String privateIp, String macAddress, String publicIp) {
        Router router = createRouter(name, privateIp, macAddress, publicIp);
        router.setNatEnabled(true);
        return router;
    }

    public Gateway createGateway(String name, String internalIp, String externalIp, String macAddress) {
        Gateway gateway = new Gateway(name, internalIp, externalIp, macAddress);
        createdDevices.put(name, gateway);
        return gateway;
    }

    public Gateway createGateway(String ipAddress, String macAddress) {
        gatewayCounter++;
        return createGateway("Gateway-" + gatewayCounter, ipAddress, "203.0.113." + gatewayCounter, macAddress);
    }

    public DnsServer createDnsServer(String name, String ipAddress, String macAddress) {
        DnsServer dnsServer = new DnsServer(name, ipAddress, macAddress);
        createdDevices.put(name, dnsServer);
        return dnsServer;
    }

    public DnsServer createDnsServer(String ipAddress, String macAddress) {
        dnsServerCounter++;
        return createDnsServer("DnsServer-" + dnsServerCounter, ipAddress, macAddress);
    }

    public DhcpServer createDhcpServer(String name, String ipAddress, String macAddress, String poolStart, String poolEnd) {
        DhcpServer dhcpServer = new DhcpServer(name, ipAddress, macAddress);
        dhcpServer.addAddressPool(poolStart, poolEnd);
        createdDevices.put(name, dhcpServer);
        return dhcpServer;
    }

    public DhcpServer createDhcpServer(String ipAddress, String macAddress) {
        dhcpServerCounter++;
        String name = "DhcpServer-" + dhcpServerCounter;
        DhcpServer dhcpServer = new DhcpServer(name, ipAddress, macAddress);
        // 设置默认地址池
        dhcpServer.addAddressPool("192.168.1.100", "192.168.1.200");
        createdDevices.put(name, dhcpServer);
        return dhcpServer;
    }

    public NetworkDevice getDevice(String name) {
        return createdDevices.get(name);
    }

    public void removeDevice(String name) {
        createdDevices.remove(name);
    }

    @Override
    public NetworkDevice produce() {
        return createHostWithDefaults();
    }

    @Override
    public void reset() {
        createdDevices.clear();
        hostCounter = 0;
        routerCounter = 0;
        gatewayCounter = 0;
        dnsServerCounter = 0;
        dhcpServerCounter = 0;
    }

    public int getDeviceCount() {
        return createdDevices.size();
    }
}
