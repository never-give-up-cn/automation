package com.never_give_up.automation.demo.factory.route;

import java.util.HashMap;
import java.util.Map;

public class ForwardingEngineFactory {
    private final Map<String, String> fib = new HashMap<>();

    public void addFibEntry(String prefix, String nextHop) {
        fib.put(prefix, nextHop);
    }

    public String longestPrefixMatch(String ip) {
        return fib.getOrDefault("0.0.0.0/0", "drop");
    }

    public String forward(String dstIp) {
        return longestPrefixMatch(dstIp);
    }

    public void reset() { fib.clear(); }
}