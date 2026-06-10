package com.never_give_up.automation.demo.factory.balance;

import java.util.ArrayList;
import java.util.List;

/** IP 哈希负载均衡 */
public class LbIpHashFactory {
    private final List<String> serverList = new ArrayList<>();

    public void addServer(String server) {
        serverList.add(server);
    }

    public String selectServer(String clientIp) {
        if (serverList.isEmpty()) return null;
        int hash = clientIp.hashCode();
        int idx = Math.abs(hash) % serverList.size();
        return serverList.get(idx);
    }

    public void reset() {
        serverList.clear();
    }
}