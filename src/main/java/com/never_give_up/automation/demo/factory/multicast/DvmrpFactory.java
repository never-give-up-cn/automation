package com.never_give_up.automation.demo.factory.multicast;

import java.util.HashMap;
import java.util.Map;

public class DvmrpFactory {
    private final Map<String, String> routes = new HashMap<>();

    public void addRoute(String group, String upstream) {
        routes.put(group, upstream);
    }

    public String getUpstream(String group) {
        return routes.get(group);
    }

    public void reset() {
        routes.clear();
    }
}