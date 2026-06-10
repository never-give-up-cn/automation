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

    /**
     * 匹配协议 - 适配 int 类型的 protocol
     */
    private boolean matchesProtocol(FiveTuple fiveTuple) {
        int protoInt = fiveTuple.getProtocol();
        switch (protocol) {
            case TCP:
                return protoInt == 6;
            case UDP:
                return protoInt == 17;
            case ICMP:
                return protoInt == 1;
            case HTTP:
                return protoInt == 6;  // HTTP 使用 TCP
            case HTTPS:
                return protoInt == 6;  // HTTPS 使用 TCP
            case DNS:
                return protoInt == 17; // DNS 使用 UDP
            case DHCP:
                return protoInt == 17; // DHCP 使用 UDP
            case ANY:
                return true;
            default:
                return true;
        }
    }

    /**
     * IP 地址匹配（支持通配符）
     */
    private boolean matchesIp(String pattern, String ip) {
        if (pattern == null || "*".equals(pattern)) return true;
        if (pattern.equals(ip)) return true;

        // 针对 /24 网段，截取到最后一个点号 "."，即 "192.168.1."
        if (pattern.endsWith("/24")) {
            String prefix = pattern.substring(0, pattern.lastIndexOf(".") + 1);
            return ip.startsWith(prefix); // "192.168.1.100".startsWith("192.168.1.") -> true
        }

        if (pattern.endsWith("/*")) {
            String prefix = pattern.replace("/*", "");
            return ip.startsWith(prefix);
        }

        if (pattern.endsWith(".")) {
            return ip.startsWith(pattern);
        }
        return false;
    }

    /**
     * 端口匹配（支持范围）
     */
    private boolean matchesPort(String range, int port) {
        if (range == null || "*".equals(range)) return true;
        try {
            if (range.contains("-")) {
                String[] parts = range.split("-");
                int min = Integer.parseInt(parts[0]);
                int max = Integer.parseInt(parts[1]);
                return port >= min && port <= max;
            }
            return Integer.parseInt(range) == port;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void incrementHitCount() {
        hitCount++;
    }

    @Override
    public int compareTo(FirewallRule other) {
        return Integer.compare(this.priority, other.priority);
    }

    @Override
    public String toString() {
        return String.format("FirewallRule[%s, priority=%d, action=%s, enabled=%s, hits=%d]",
                name, priority, action, enabled, hitCount);
    }
}