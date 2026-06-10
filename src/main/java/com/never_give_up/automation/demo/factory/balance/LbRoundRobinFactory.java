package com.never_give_up.automation.demo.factory.balance;

import java.util.ArrayList;
import java.util.List;

/** 轮询负载均衡 */
public class LbRoundRobinFactory {
    private final List<String> serverList = new ArrayList<>();
    private int currentIndex = 0;

    public void addServer(String server) {
        serverList.add(server);
    }

    public String selectServer() {
        if (serverList.isEmpty()) return null;
        String server = serverList.get(currentIndex);
        currentIndex = (currentIndex + 1) % serverList.size();
        return server;
    }

    public void reset() {
        serverList.clear();
        currentIndex = 0;
    }
}