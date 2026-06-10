package com.never_give_up.automation.demo.factory.balance;

import java.util.List;

public class LoadBalancerFactory {
    public enum Algorithm { RR, LC, IP_HASH, WEIGHT }

    private Algorithm algo = Algorithm.RR;
    private final LbRoundRobinFactory rr = new LbRoundRobinFactory();
    private final LbLeastConnFactory lc = new LbLeastConnFactory();

    public void setAlgorithm(Algorithm a) { algo = a; }
    public String select(List<String> servers, String clientIp) {
        return switch (algo) {
            case RR -> rr.selectServer();
            case LC -> lc.selectServer();
            default -> servers.get(0);
        };
    }

    public void reset() {
        rr.reset();
        lc.reset();
    }
}