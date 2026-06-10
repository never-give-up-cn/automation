package com.never_give_up.automation.demo.factory.tool;

import java.util.HashMap;
import java.util.Map;

/** ARP 命令行工具 */
public class ArpCommandFactory {
    private final Map<String, String> arpTable = new HashMap<>();

    public void addEntry(String ip, String mac) {
        arpTable.put(ip, mac);
    }

    public String getMac(String ip) {
        return arpTable.get(ip);
    }

    public void reset() {
        arpTable.clear();
    }
}