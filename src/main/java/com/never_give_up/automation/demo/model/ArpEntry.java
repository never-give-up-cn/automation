package com.never_give_up.automation.demo.model;

public class ArpEntry {
    public String ipAddress;
    public String macAddress;
    public long lastSeen;

    public ArpEntry(String ip, String mac) {
        this.ipAddress = ip;
        this.macAddress = mac;
        this.lastSeen = System.currentTimeMillis();
    }
}
