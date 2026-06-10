package com.never_give_up.automation.demo.factory.balance;

import java.util.HashMap;
import java.util.Map;

/** 后端服务健康检查 */
public class LbHealthCheckFactory {
    private final Map<String, Boolean> serverStatus = new HashMap<>();

    public void registerServer(String server) {
        serverStatus.put(server, true);
    }

    public void setDown(String server) {
        serverStatus.put(server, false);
    }

    public void setUp(String server) {
        serverStatus.put(server, true);
    }

    public boolean isHealthy(String server) {
        return serverStatus.getOrDefault(server, false);
    }

    public void reset() {
        serverStatus.clear();
    }
}