package com.never_give_up.automation.demo.factory.function;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

public class RouteTableFactory {
    private final List<RouteEntry> routes = new ArrayList<>();

    @Data
    public static class RouteEntry {
        private final String network;
        private final String nextHop;
        private final String interfaceName;
        private final int metric;

        public RouteEntry(String network, String nextHop, String interfaceName, int metric) {
            this.network = network;
            this.nextHop = nextHop;
            this.interfaceName = interfaceName;
            this.metric = metric;
        }
    }

    public void addRoute(String network, String nextHop, String interfaceName, int metric) {
        routes.add(new RouteEntry(network, nextHop, interfaceName, metric));
    }

    public void addDefaultRoute(String nextHop, String interfaceName) {
        routes.add(new RouteEntry("0.0.0.0", nextHop, interfaceName, 999));
    }

    public RouteEntry lookup(String destinationIp) {
        RouteEntry bestMatch = null;
        int longestPrefix = -1;

        for (RouteEntry route : routes) {
            if (route.getNetwork().equals("0.0.0.0")) {
                if (bestMatch == null || route.getMetric() < bestMatch.getMetric()) {
                    bestMatch = route;
                }
                continue;
            }

            if (isInNetwork(destinationIp, route.getNetwork())) {
                int prefixLength = getPrefixLength(route.getNetwork());
                if (prefixLength > longestPrefix) {
                    longestPrefix = prefixLength;
                    bestMatch = route;
                }
            }
        }

        return bestMatch;
    }

    private boolean isInNetwork(String ip, String network) {
        String[] ipParts = ip.split("\\.");
        String[] netParts = network.split("\\.");

        for (int i = 0; i < 4; i++) {
            if (!ipParts[i].equals(netParts[i])) {
                return false;
            }
        }
        return true;
    }

    private int getPrefixLength(String network) {
        String[] parts = network.split("\\.");
        int prefix = 0;
        for (String part : parts) {
            if (part.equals("0")) break;
            prefix++;
        }
        return prefix * 8;
    }

    public void removeRoute(String network) {
        routes.removeIf(r -> r.getNetwork().equals(network));
    }

    public void clear() {
        routes.clear();
    }

    public List<RouteEntry> getRoutes() {
        return new ArrayList<>(routes);
    }
}
