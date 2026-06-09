package com.never_give_up.automation.demo.factory.security;

import com.never_give_up.automation.demo.model.FiveTuple;
import lombok.Data;

@Data
public class FirewallRule implements Comparable<FirewallRule> {
    private String ruleId;
    private String name;
    private int priority;
    private FirewallRuleFactory.Action action;
    private FirewallRuleFactory.Direction direction;
    private FirewallRuleFactory.Protocol protocol;
    private String sourceIp;
    private String sourcePortRange;
    private String destinationIp;
    private String destinationPortRange;
    private boolean enabled;
    private long hitCount;
    private String description;

    public FirewallRule(String ruleId, String name, int priority) {
        this.ruleId = ruleId;
        this.name = name;
        this.priority = priority;
        this.action = FirewallRuleFactory.Action.DENY;
        this.direction = FirewallRuleFactory.Direction.BOTH;
        this.protocol = FirewallRuleFactory.Protocol.ANY;
        this.sourceIp = "*";
        this.sourcePortRange = "*";
        this.destinationIp = "*";
        this.destinationPortRange = "*";
        this.enabled = true;
        this.hitCount = 0;
    }

    public boolean matches(FiveTuple fiveTuple, FirewallRuleFactory.Direction packetDirection) {
        if (!enabled) return false;
        if (direction != FirewallRuleFactory.Direction.BOTH && direction != packetDirection) return false;
        if (protocol != FirewallRuleFactory.Protocol.ANY && !matchesProtocol(fiveTuple)) return false;
        if (!matchesIp(sourceIp, fiveTuple.getSourceIp())) return false;
        if (!matchesIp(destinationIp, fiveTuple.getDestinationIp())) return false;
        if (!matchesPort(sourcePortRange, fiveTuple.getSourcePort())) return false;
        if (!matchesPort(destinationPortRange, fiveTuple.getDestinationPort())) return false;
        return true;
    }

    private boolean matchesProtocol(FiveTuple fiveTuple) {
        switch (protocol) {
            case TCP: return fiveTuple.getProtocol() == 6;
            case UDP: return fiveTuple.getProtocol() == 17;
            case ICMP: return fiveTuple.getProtocol() == 1;
            default: return true;
        }
    }

    private boolean matchesIp(String pattern, String ip) {
        if ("*".equals(pattern)) return true;
        if (pattern.equals(ip)) return true;
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            return ip.startsWith(prefix);
        }
        return false;
    }

    private boolean matchesPort(String range, int port) {
        if ("*".equals(range)) return true;
        if (range.contains("-")) {
            String[] parts = range.split("-");
            int min = Integer.parseInt(parts[0]);
            int max = Integer.parseInt(parts[1]);
            return port >= min && port <= max;
        }
        return range.equals(String.valueOf(port));
    }

    public void incrementHitCount() {
        hitCount++;
    }

    @Override
    public int compareTo(FirewallRule other) {
        return Integer.compare(this.priority, other.priority);
    }
}
