package com.never_give_up.automation.demo.network;

import com.never_give_up.automation.demo.model.BasePacket;
import com.never_give_up.automation.demo.model.DnsPacket;
import com.never_give_up.automation.demo.factory.application.DnsPacketFactory;
import com.never_give_up.automation.demo.factory.function.DnsCacheFactory;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class DnsServer extends NetworkDevice {
    private DnsCacheFactory dnsCache;
    private DnsPacketFactory dnsFactory;
    private Map<String, String> zoneRecords = new HashMap<>();
    private boolean recursiveEnabled = true;
    private boolean authoritative = false;

    public DnsServer(String name, String ipAddress, String macAddress) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.dnsCache = new DnsCacheFactory();
        this.dnsFactory = new DnsPacketFactory();
    }

    @Override
    public void processPacket(BasePacket packet) {
        if (!isOnline()) return;

        if (packet instanceof DnsPacket) {
            processDnsQuery((DnsPacket) packet);
        }
    }

    private void processDnsQuery(DnsPacket query) {
        if (query.isQuery()) {
            String resolvedIp = resolveDomain(query.getDomain());

            if (resolvedIp != null) {
                DnsPacket response = dnsFactory.createResponse(
                        query.getDomain(),
                        resolvedIp,
                        query.getTransactionId()
                );
                sendResponse(response);
            } else if (recursiveEnabled) {
                forwardRecursiveQuery(query);
            }
        }
    }

    private String resolveDomain(String domain) {
        String cached = dnsCache.resolve(domain);
        if (cached != null) {
            return cached;
        }

        return zoneRecords.get(domain);
    }

    private void forwardRecursiveQuery(DnsPacket query) {
    }

    private void sendResponse(DnsPacket response) {
    }

    public void addZoneRecord(String domain, String ip) {
        zoneRecords.put(domain, ip);
    }

    public void addCachedRecord(String domain, String ip, long ttl) {
        dnsCache.addEntry(domain, ip, ttl);
    }

    public void setRecursive(boolean enabled) {
        this.recursiveEnabled = enabled;
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
