package com.never_give_up.automation.demo.factory.network.arp;

import java.util.HashMap;
import java.util.Map;

public class ArpTableFactory {
    private final Map<String, ArpEntry> arpTable = new HashMap<>();

    public static class ArpEntry {
        public String mac;
        public long expireTime;
        public String type; // static/dynamic
    }

    public void addDynamic(String ip, String mac) {
        ArpEntry e = new ArpEntry();
        e.mac = mac;
        e.type = "dynamic";
        e.expireTime = System.currentTimeMillis() + 180000;
        arpTable.put(ip, e);
    }

    public String resolve(String ip) {
        ArpEntry e = arpTable.get(ip);
        return e == null ? null : e.mac;
    }

    public void reset() { arpTable.clear(); }
}