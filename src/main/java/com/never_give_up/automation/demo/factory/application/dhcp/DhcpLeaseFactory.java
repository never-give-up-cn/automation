package com.never_give_up.automation.demo.factory.application.dhcp;

import java.util.HashMap;
import java.util.Map;

public class DhcpLeaseFactory {
    private final Map<String, Lease> leaseMap = new HashMap<>();

    public static class Lease {
        public String mac;
        public String ip;
        public long leaseTime;
    }

    public void offer(String mac, String ip) {
        Lease l = new Lease();
        l.mac = mac;
        l.ip = ip;
        l.leaseTime = 86400;
        leaseMap.put(mac, l);
    }

    public String getIp(String mac) {
        Lease l = leaseMap.get(mac);
        return l == null ? null : l.ip;
    }

    public void reset() { leaseMap.clear(); }
}