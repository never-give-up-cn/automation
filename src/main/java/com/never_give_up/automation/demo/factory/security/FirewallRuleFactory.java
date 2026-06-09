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
        return false;
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
