package com.never_give_up.automation.demo.factory.network.ipv6;

import java.util.HashMap;
import java.util.Map;

public class Ipv6NeighborDiscovery {
    private final Map<String, String> neighborCache = new HashMap<>();

    public void addNeighbor(String ipv6, String mac) {
        neighborCache.put(ipv6, mac);
    }

    public String getMac(String ipv6) {
        return neighborCache.get(ipv6);
    }

    public void reset() {
        neighborCache.clear();
    }

    public void clear() {
        neighborCache.clear();
    }
}