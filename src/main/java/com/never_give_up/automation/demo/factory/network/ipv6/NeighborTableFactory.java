package com.never_give_up.automation.demo.factory.network.ipv6;

import java.util.HashMap;
import java.util.Map;

public class NeighborTableFactory {
    public enum State { REACHABLE, STALE, DELAY, PROBE }

    private final Map<String, Neighbor> table = new HashMap<>();

    public static class Neighbor {
        public String mac;
        public State state;
    }

    public void add(String ipv6, String mac) {
        Neighbor n = new Neighbor();
        n.mac = mac;
        n.state = State.REACHABLE;
        table.put(ipv6, n);
    }

    public String getMac(String ipv6) {
        Neighbor n = table.get(ipv6);
        return n == null ? null : n.mac;
    }

    public void reset() { table.clear(); }
}