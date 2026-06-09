package com.never_give_up.automation.demo.factory.security;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.FiveTuple;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class FirewallRuleFactory implements INetworkFactory<FirewallRule> {
    private final List<FirewallRule> createdRules = new ArrayList<>();
    private final Map<String, FirewallRule> ruleMap = new HashMap<>();
    private int ruleCounter = 0;

    public enum Action {
        ALLOW,      // 允许通过
        DENY,       // 拒绝
        DROP,       // 丢弃（不响应）
        REJECT,     // 拒绝并返回错误
        LOG         // 仅记录日志
    }

    public enum Direction {
        INBOUND,    // 入站
        OUTBOUND,   // 出站
        BOTH        // 双向
    }

    public enum Protocol {
        ANY,
        TCP,
        UDP,
        ICMP,
        HTTP,
        HTTPS,
        DNS,
        DHCP
    }

    public static class RuleBuilder {
        private String name;
        private int priority = 100;
        private Action action = Action.ALLOW;
        private Direction direction = Direction.BOTH;
        private Protocol protocol = Protocol.ANY;
        private String sourceIp = "*";
        private String sourcePortRange = "*";
        private String destinationIp = "*";
        private String destinationPortRange = "*";
        private String description = "";

        public RuleBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RuleBuilder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public RuleBuilder action(Action action) {
            this.action = action;
            return this;
        }

        public RuleBuilder direction(Direction direction) {
            this.direction = direction;
            return this;
        }

        public RuleBuilder protocol(Protocol protocol) {
            this.protocol = protocol;
            return this;
        }

        public RuleBuilder sourceIp(String sourceIp) {
            this.sourceIp = sourceIp;
            return this;
        }

        public RuleBuilder sourcePort(String sourcePortRange) {
            this.sourcePortRange = sourcePortRange;
            return this;
        }

        public RuleBuilder destinationIp(String destinationIp) {
            this.destinationIp = destinationIp;
            return this;
        }

        public RuleBuilder destinationPort(String destinationPortRange) {
            this.destinationPortRange = destinationPortRange;
            return this;
        }

        public RuleBuilder description(String description) {
            this.description = description;
            return this;
        }

        public FirewallRule build(String ruleId) {
            FirewallRule rule = new FirewallRule(ruleId, name != null ? name : "Rule-" + ruleId, priority);
            rule.setAction(action);
            rule.setDirection(direction);
            rule.setProtocol(protocol);
            rule.setSourceIp(sourceIp);
            rule.setSourcePortRange(sourcePortRange);
            rule.setDestinationIp(destinationIp);
            rule.setDestinationPortRange(destinationPortRange);
            rule.setDescription(description);
            return rule;
        }
    }

    public FirewallRule createRule(RuleBuilder builder) {
        ruleCounter++;
        String ruleId = "RULE-" + String.format("%04d", ruleCounter);
        FirewallRule rule = builder.build(ruleId);
        createdRules.add(rule);
        ruleMap.put(ruleId, rule);
        return rule;
    }

    public FirewallRule createAllowAllRule() {
        return new RuleBuilder()
                .name("Allow All")
                .priority(1000)
                .action(Action.ALLOW)
                .description("Allow all traffic")
                .build("");
    }

    public FirewallRule createDenyAllRule() {
        return new RuleBuilder()
                .name("Deny All")
                .priority(9999)
                .action(Action.DENY)
                .description("Default deny all")
                .build("");
    }

    public FirewallRule createAllowTcpRule(String destIp, int destPort) {
        return new RuleBuilder()
                .name("Allow TCP to " + destIp + ":" + destPort)
                .priority(100)
                .action(Action.ALLOW)
                .protocol(Protocol.TCP)
                .destinationIp(destIp)
                .destinationPort(String.valueOf(destPort))
                .build("");
    }

    public FirewallRule createDenyIpRule(String sourceIp) {
        return new RuleBuilder()
                .name("Deny from " + sourceIp)
                .priority(50)
                .action(Action.DENY)
                .sourceIp(sourceIp)
                .description("Block all traffic from " + sourceIp)
                .build("");
    }

    public FirewallRule createAllowPortRangeRule(String destIp, int startPort, int endPort) {
        return new RuleBuilder()
                .name("Allow port range " + startPort + "-" + endPort)
                .priority(100)
                .action(Action.ALLOW)
                .destinationIp(destIp)
                .destinationPort(startPort + "-" + endPort)
                .build("");
    }

    public FirewallRule createAllowHttpRule(String destIp) {
        return createAllowTcpRule(destIp, 80);
    }

    public FirewallRule createAllowHttpsRule(String destIp) {
        return createAllowTcpRule(destIp, 443);
    }

    public FirewallRule createAllowDnsRule(String destIp) {
        return new RuleBuilder()
                .name("Allow DNS to " + destIp)
                .priority(100)
                .action(Action.ALLOW)
                .protocol(Protocol.DNS)
                .destinationIp(destIp)
                .destinationPort("53")
                .build("");
    }

    public void enableRule(String ruleId) {
        FirewallRule rule = ruleMap.get(ruleId);
        if (rule != null) {
            rule.setEnabled(true);
        }
    }

    public void disableRule(String ruleId) {
        FirewallRule rule = ruleMap.get(ruleId);
        if (rule != null) {
            rule.setEnabled(false);
        }
    }

    public void removeRule(String ruleId) {
        FirewallRule rule = ruleMap.remove(ruleId);
        if (rule != null) {
            createdRules.remove(rule);
        }
    }

    public FirewallRule getRule(String ruleId) {
        return ruleMap.get(ruleId);
    }

    public List<FirewallRule> getAllRules() {
        List<FirewallRule> sortedRules = new ArrayList<>(createdRules);
        Collections.sort(sortedRules);
        return sortedRules;
    }

    public List<FirewallRule> getEnabledRules() {
        return createdRules.stream()
                .filter(FirewallRule::isEnabled)
                .sorted()
                .collect(Collectors.toList());
    }

    public List<FirewallRule> getRulesByAction(Action action) {
        return createdRules.stream()
                .filter(r -> r.getAction() == action)
                .sorted()
                .collect(Collectors.toList());
    }

    public Optional<FirewallRule> findMatchingRule(FiveTuple fiveTuple, Direction direction) {
        return getEnabledRules().stream()
                .filter(rule -> rule.matches(fiveTuple, direction))
                .findFirst();
    }

    public boolean shouldAllow(FiveTuple fiveTuple, Direction direction) {
        Optional<FirewallRule> matchingRule = findMatchingRule(fiveTuple, direction);
        if (matchingRule.isPresent()) {
            FirewallRule rule = matchingRule.get();
            rule.incrementHitCount();
            return rule.getAction() == Action.ALLOW || rule.getAction() == Action.LOG;
        }
        return false;  // 默认拒绝
    }

    // ========== 新增：便捷的 allowOutbound / allowInbound 方法 ==========

    /**
     * 检查出站流量是否允许
     * @param srcIp 源IP
     * @param dstIp 目标IP
     * @param srcPort 源端口
     * @return true 表示允许通过
     */
    public boolean allowOutbound(String srcIp, String dstIp, int srcPort) {
        FiveTuple fiveTuple = new FiveTuple(srcIp, dstIp, srcPort, 0, 6); // 默认 TCP
        return shouldAllow(fiveTuple, Direction.OUTBOUND);
    }

    /**
     * 检查入站流量是否允许
     * @param dstIp 目标IP（本机）
     * @param srcIp 源IP（外部）
     * @param dstPort 目标端口
     * @return true 表示允许通过
     */
    public boolean allowInbound(String dstIp, String srcIp, int dstPort) {
        FiveTuple fiveTuple = new FiveTuple(srcIp, dstIp, 0, dstPort, 6); // 默认 TCP
        return shouldAllow(fiveTuple, Direction.INBOUND);
    }

    /**
     * 检查出站流量是否允许（带协议）
     * @param srcIp 源IP
     * @param dstIp 目标IP
     * @param srcPort 源端口
     * @param dstPort 目标端口
     * @param protocol 协议号 (1=ICMP, 6=TCP, 17=UDP)
     * @return true 表示允许通过
     */
    public boolean allowOutbound(String srcIp, String dstIp, int srcPort, int dstPort, int protocol) {
        FiveTuple fiveTuple = new FiveTuple(srcIp, dstIp, srcPort, dstPort, protocol);
        return shouldAllow(fiveTuple, Direction.OUTBOUND);
    }

    /**
     * 检查入站流量是否允许（带协议）
     * @param srcIp 源IP
     * @param dstIp 目标IP
     * @param srcPort 源端口
     * @param dstPort 目标端口
     * @param protocol 协议号 (1=ICMP, 6=TCP, 17=UDP)
     * @return true 表示允许通过
     */
    public boolean allowInbound(String srcIp, String dstIp, int srcPort, int dstPort, int protocol) {
        FiveTuple fiveTuple = new FiveTuple(srcIp, dstIp, srcPort, dstPort, protocol);
        return shouldAllow(fiveTuple, Direction.INBOUND);
    }

    /**
     * 检查出站流量是否允许（带协议名称）
     * @param srcIp 源IP
     * @param dstIp 目标IP
     * @param srcPort 源端口
     * @param dstPort 目标端口
     * @param protocolName 协议名称 (TCP, UDP, ICMP)
     * @return true 表示允许通过
     */
    public boolean allowOutbound(String srcIp, String dstIp, int srcPort, int dstPort, String protocolName) {
        int protocol = protocolNameToInt(protocolName);
        FiveTuple fiveTuple = new FiveTuple(srcIp, dstIp, srcPort, dstPort, protocol);
        return shouldAllow(fiveTuple, Direction.OUTBOUND);
    }

    /**
     * 检查入站流量是否允许（带协议名称）
     * @param srcIp 源IP
     * @param dstIp 目标IP
     * @param srcPort 源端口
     * @param dstPort 目标端口
     * @param protocolName 协议名称 (TCP, UDP, ICMP)
     * @return true 表示允许通过
     */
    public boolean allowInbound(String srcIp, String dstIp, int srcPort, int dstPort, String protocolName) {
        int protocol = protocolNameToInt(protocolName);
        FiveTuple fiveTuple = new FiveTuple(srcIp, dstIp, srcPort, dstPort, protocol);
        return shouldAllow(fiveTuple, Direction.INBOUND);
    }

    /**
     * 协议名称转协议号
     */
    private int protocolNameToInt(String protocolName) {
        if (protocolName == null) return 0;
        switch (protocolName.toUpperCase()) {
            case "TCP": return 6;
            case "UDP": return 17;
            case "ICMP": return 1;
            default: return 0;
        }
    }

    /**
     * 快速添加允许规则
     */
    public void addAllowRule(String srcIp, String dstIp, int port, Direction direction) {
        RuleBuilder builder = new RuleBuilder()
                .name("Allow " + direction + " " + srcIp + ":" + port + " -> " + dstIp)
                .priority(100)
                .action(Action.ALLOW)
                .direction(direction);

        if (srcIp != null && !srcIp.equals("*")) {
            builder.sourceIp(srcIp);
        }
        if (dstIp != null && !dstIp.equals("*")) {
            builder.destinationIp(dstIp);
        }
        if (port > 0) {
            builder.destinationPort(String.valueOf(port));
        }

        createRule(builder);
    }

    /**
     * 快速添加拒绝规则
     */
    public void addDenyRule(String srcIp, String dstIp, int port, Direction direction) {
        RuleBuilder builder = new RuleBuilder()
                .name("Deny " + direction + " " + srcIp + ":" + port + " -> " + dstIp)
                .priority(50)
                .action(Action.DENY)
                .direction(direction);

        if (srcIp != null && !srcIp.equals("*")) {
            builder.sourceIp(srcIp);
        }
        if (dstIp != null && !dstIp.equals("*")) {
            builder.destinationIp(dstIp);
        }
        if (port > 0) {
            builder.destinationPort(String.valueOf(port));
        }

        createRule(builder);
    }

    /**
     * 初始化默认防火墙规则
     */
    public void initDefaultRules() {
        reset();

        // 允许 HTTP/HTTPS 出站
        addAllowRule("*", "*", 80, Direction.OUTBOUND);
        addAllowRule("*", "*", 443, Direction.OUTBOUND);

        // 允许 DNS (UDP 53)
        RuleBuilder dnsRule = new RuleBuilder()
                .name("Allow DNS")
                .priority(100)
                .action(Action.ALLOW)
                .protocol(Protocol.DNS)
                .destinationPort("53")
                .direction(Direction.OUTBOUND);
        createRule(dnsRule);

        // 允许 ICMP (Ping)
        RuleBuilder icmpRule = new RuleBuilder()
                .name("Allow ICMP")
                .priority(100)
                .action(Action.ALLOW)
                .protocol(Protocol.ICMP)
                .direction(Direction.BOTH);
        createRule(icmpRule);

        // 允许 DHCP
        RuleBuilder dhcpRule = new RuleBuilder()
                .name("Allow DHCP")
                .priority(100)
                .action(Action.ALLOW)
                .protocol(Protocol.DHCP)
                .direction(Direction.OUTBOUND);
        createRule(dhcpRule);

        // 默认拒绝所有
        RuleBuilder defaultRule = new RuleBuilder()
                .name("Default Deny")
                .priority(9999)
                .action(Action.DENY)
                .direction(Direction.BOTH)
                .description("Default deny all");
        createRule(defaultRule);
    }

    @Override
    public FirewallRule produce() {
        return new RuleBuilder()
                .name("Default Rule")
                .build("");
    }

    @Override
    public void reset() {
        createdRules.clear();
        ruleMap.clear();
        ruleCounter = 0;
    }

    public int getRuleCount() {
        return createdRules.size();
    }
}