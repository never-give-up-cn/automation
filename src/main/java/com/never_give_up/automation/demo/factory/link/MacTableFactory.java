package com.never_give_up.automation.demo.factory.link;

import java.util.HashMap;
import java.util.Map;

public class MacTableFactory {
    private final Map<String, String> macToPort = new HashMap<>();

    public void learn(String mac, String port) {
        macToPort.put(mac, port);
    }

    public String getPort(String mac) {
        return macToPort.getOrDefault(mac, "flood");
    }

    public void clear() { macToPort.clear(); }
    public void reset() { clear(); }
}