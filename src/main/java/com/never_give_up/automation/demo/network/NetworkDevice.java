package com.never_give_up.automation.demo.network;

import lombok.Data;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public abstract class NetworkDevice {
    protected String name;
    protected String ipAddress;
    protected String macAddress;
    protected Map<String, String> interfaces = new ConcurrentHashMap<>();

    public abstract void processPacket(Object packet);
}

@Data
class Host extends NetworkDevice {
    private String os;
    private int[] openPorts;

    @Override
    public void processPacket(Object packet) {
        // 主机处理逻辑
    }
}

@Data
class Router extends NetworkDevice {
    private Map<String, String> routingTable = new ConcurrentHashMap<>();
    private int ttlDecrement = 1;

    public void addRoute(String network, String nextHop) {
        routingTable.put(network, nextHop);
    }

    public String lookupRoute(String destinationIp) {
        // 最长前缀匹配
        return routingTable.getOrDefault(destinationIp, "default");
    }

    @Override
    public void processPacket(Object packet) {
        // 路由器转发逻辑
    }
}

@Data
class DnsServer extends NetworkDevice {
    private Map<String, String> dnsRecords = new ConcurrentHashMap<>();
    private boolean recursiveEnabled = true;

    public void addRecord(String domain, String ip) {
        dnsRecords.put(domain, ip);
    }

    public String resolve(String domain) {
        return dnsRecords.getOrDefault(domain, null);
    }

    @Override
    public void processPacket(Object packet) {
        // DNS 查询处理
    }
}

@Data
class DhcpServer extends NetworkDevice {
    private Map<String, String> addressPool = new ConcurrentHashMap<>();
    private Map<String, Long> leaseTable = new ConcurrentHashMap<>();
    private long leaseTime = 3600000;

    public String allocateIp(String clientMac) {
        // DHCP 地址分配逻辑
        return addressPool.get(clientMac);
    }

    @Override
    public void processPacket(Object packet) {
        // DHCP 处理逻辑
    }
}

@Data
class Gateway extends NetworkDevice {
    private Map<String, String> natTable = new ConcurrentHashMap<>();

    public String translateAddress(String internalIp, int internalPort) {
        String key = internalIp + ":" + internalPort;
        return natTable.getOrDefault(key, null);
    }

    @Override
    public void processPacket(Object packet) {
        // NAT 转换逻辑
    }
}
