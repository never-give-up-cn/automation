package com.never_give_up.automation.demo.winModel;

import lombok.Data;

@Data
public class ArpEntry {
    private String ipAddress;
    private String macAddress;
    private long lastSeen;

    public ArpEntry(String ip, String mac) {
        this.ipAddress = ip;
        this.macAddress = mac;
        this.lastSeen = System.currentTimeMillis();
    }

    public boolean isExpired(long timeoutMs) {
        return System.currentTimeMillis() - lastSeen > timeoutMs;
    }
}

