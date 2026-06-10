package com.never_give_up.automation.demo.factory.link;

import java.util.HashMap;
import java.util.Map;

public class CamTableFactory {
    private final Map<String, CamEntry> camTable = new HashMap<>();

    public static class CamEntry {
        public int vlan;
        public String port;
    }

    public void add(int vlan, String mac, String port) {
        CamEntry entry = new CamEntry();
        entry.vlan = vlan;
        entry.port = port;
        camTable.put(mac, entry);
    }

    public CamEntry get(String mac) {
        return camTable.get(mac);
    }

    public void reset() { camTable.clear(); }
}