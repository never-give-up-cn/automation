package com.never_give_up.automation.demo.factory.balance;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/** 最少连接负载均衡 */
public class LbLeastConnFactory {
    private final Map<String, Integer> connMap = new HashMap<>();

    public void addServer(String server) {
        connMap.putIfAbsent(server, 0);
    }

    public String selectServer() {
        return connMap.entrySet().stream()
                .min(Comparator.comparingInt(Map.Entry::getValue))
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    public void incConn(String server) {
        connMap.computeIfPresent(server, (k, v) -> v + 1);
    }

    public void decConn(String server) {
        connMap.computeIfPresent(server, (k, v) -> Math.max(0, v - 1));
    }

    public void reset() {
        connMap.clear();
    }
}