package com.never_give_up.automation.demo.factory.security;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.FiveTuple;
import lombok.Getter;

import java.util.*;

@Getter
public class FirewallRuleFactory implements INetworkFactory<FirewallRule> {
    private final List<FirewallRule> createdRules = new ArrayList<>();
    private final Map<String, FirewallRule> ruleMap = new HashMap<>();
    private int ruleCounter = 0;

    public FirewallRuleFactory() {
        initDefaultRules();
    }

    public enum Action {ALLOW, DENY, DROP, REJECT, LOG}

    public enum Direction {INBOUND, OUTBOUND, BOTH}

    public enum Protocol {ANY, TCP, UDP, ICMP, HTTP, HTTPS, DNS, DHCP}

    public static class RuleBuilder {
        private String name;
        private int priority = 100;
        private Action action = Action.ALLOW;
        private Direction direction = Direction.BOTH;
        private Protocol protocol = Protocol.ANY;
        private String sourceIp = "*";
        private String destinationIp = "*";
        private String sourcePortRange = "*";
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
            rule.setEnabled(true);
            return rule;
        }
    }

    public static boolean ipMatchesCidr(String ip, String cidr) {
        // 添加 null 检查
        if (ip == null || cidr == null) {
            return false;
        }
        if (cidr.equals("*")) {
            return true;
        }
        // 纯IP相等匹配
        if (!cidr.contains("/")) {
            return ip.equals(cidr);
        }
        try {
            String[] parts = cidr.split("/");
            String network = parts[0];
            int prefixLen = Integer.parseInt(parts[1]);

            long ipNum = ipToLong(ip);
            long netNum = ipToLong(network);

            long mask;
            if (prefixLen <= 0) {
                mask = 0L;
            } else if (prefixLen >= 32) {
                mask = 0xFFFFFFFFL;
            } else {
                mask = (0xFFFFFFFFL << (32 - prefixLen)) & 0xFFFFFFFFL;
            }

            return (ipNum & mask) == (netNum & mask);
        } catch (Exception e) {
            return ip.equals(cidr);
        }
    }

    public static long ipToLong(String ip) {
        String[] octets = ip.split("\\.");
        return (Long.parseLong(octets[0]) << 24)
                | (Long.parseLong(octets[1]) << 16)
                | (Long.parseLong(octets[2]) << 8)
                | Long.parseLong(octets[3]);
    }

    // ===================== 端口匹配 =====================
    public static boolean portMatches(int port, String portRange) {
        if (portRange == null || portRange.equals("*")) return true;
        try {
            if (portRange.contains("-")) {
                String[] parts = portRange.split("-");
                int start = Integer.parseInt(parts[0].trim());
                int end = Integer.parseInt(parts[1].trim());
                return port >= start && port <= end;
            } else {
                return port == Integer.parseInt(portRange.trim());
            }
        } catch (Exception e) {
            return false;
        }
    }

    // ===================== 协议匹配 =====================
    public static boolean protocolMatches(int protoNumber, Protocol ruleProto) {
        if (ruleProto == Protocol.ANY) return true;
        return switch (ruleProto) {
            case TCP -> protoNumber == 6;
            case UDP -> protoNumber == 17;
            case ICMP -> protoNumber == 1;
            case DNS -> protoNumber == 17 || protoNumber == 6;
            case HTTP, HTTPS -> protoNumber == 6;
            default -> false;
        };
    }

    public FirewallRule createRule(RuleBuilder builder) {
        ruleCounter++;
        String ruleId = "RULE-" + String.format("%04d", ruleCounter);
        FirewallRule rule = builder.build(ruleId);
        createdRules.add(rule);
        ruleMap.put(ruleId, rule);
        return rule;
    }

    // ===================== 初始化默认规则 =====================
    // 在 FirewallRuleFactory 的 initDefaultRules() 方法中修改
    // ===================== 初始化默认规则 =====================
    public void initDefaultRules() {
        reset();

        // 规则1: 允许所有 UDP DNS 查询（最高优先级）
        createRule(new RuleBuilder()
                .name("Allow DNS Queries")
                .priority(5)  // 高优先级
                .action(Action.ALLOW)
                .direction(Direction.OUTBOUND)
                .protocol(Protocol.UDP)
                .destinationPort("53")
                .description("放行所有DNS查询"));

        // 规则2: 允许内网 192.168.1.0/24 全部出站
        createRule(new RuleBuilder()
                .name("Allow LAN Outbound")
                .priority(10)
                .action(Action.ALLOW)
                .direction(Direction.OUTBOUND)
                .protocol(Protocol.ANY)
                .sourceIp("192.168.1.0/24")
                .destinationIp("*")
                .sourcePort("*")
                .destinationPort("*")
                .description("内网全部放行出站"));

        // 规则3: 允许 ICMP (Ping)
        createRule(new RuleBuilder()
                .name("Allow ICMP")
                .priority(8)
                .action(Action.ALLOW)
                .direction(Direction.BOTH)
                .protocol(Protocol.ICMP)
                .description("放行ICMP"));

        // 规则4: 默认拒绝所有（最低优先级）
        createRule(new RuleBuilder()
                .name("Default Deny All")
                .priority(9999)
                .action(Action.DENY)
                .direction(Direction.BOTH)
                .protocol(Protocol.ANY)
                .description("默认拒绝所有流量"));
    }

    // ===================== 核心判断 =====================
    public boolean shouldAllow(FiveTuple fiveTuple, Direction direction) {
        for (FirewallRule rule : getEnabledRules()) {
            if (ruleMatches(rule, fiveTuple, direction)) {
                return rule.getAction() == Action.ALLOW;
            }
        }
        return false;
    }

    // 完整规则匹配
    private boolean ruleMatches(FirewallRule rule, FiveTuple tuple, Direction dir) {
        System.out.println("===== 开始匹配规则: " + rule.getName() + " =====");

        // 方向
        if (rule.getDirection() != Direction.BOTH && rule.getDirection() != dir) {
            System.out.println("方向不匹配，跳过");
            return false;
        }
        // 协议
        if (!protocolMatches(tuple.getProtocol(), rule.getProtocol())) {
            System.out.println("协议不匹配，跳过");
            return false;
        }
        // 源IP
        if (!ipMatchesCidr(tuple.getSourceIp(), rule.getSourceIp())) {
            System.out.println("源IP不匹配，跳过");
            return false;
        }
        // 目标IP
        if (!ipMatchesCidr(tuple.getDestinationIp(), rule.getDestinationIp())) {
            System.out.println("目标IP不匹配，跳过");
            return false;
        }
        // 源端口
        if (!portMatches(tuple.getSourcePort(), rule.getSourcePortRange())) {
            System.out.println("源端口不匹配，跳过");
            return false;
        }
        // 目标端口
        if (!portMatches(tuple.getDestinationPort(), rule.getDestinationPortRange())) {
            System.out.println("目标端口不匹配，跳过");
            return false;
        }

        System.out.println("✅ 规则完全命中");
        return true;
    }

    // ===================== 快捷出站判断 =====================
    public boolean allowOutbound(String srcIp, String dstIp, int srcPort, int dstPort, String protocolName) {
        System.out.println("开始判断出站规则"+srcIp+" → "+dstIp+" "+srcPort+" → "+dstPort+" "+protocolName);
        int proto = switch (protocolName.toUpperCase()) {
            case "TCP" -> 6;
            case "UDP" -> 17;
            case "ICMP" -> 1;
            default -> 0;
        };
        return shouldAllow(new FiveTuple(srcIp, dstIp, srcPort, dstPort, proto), Direction.OUTBOUND);
    }

    public boolean allowOutbound(String srcIp, String dstIp, int srcPort, int dstPort, int protocol) {
        return shouldAllow(new FiveTuple(srcIp, dstIp, srcPort, dstPort, protocol), Direction.OUTBOUND);
    }

    public boolean allowInbound(String srcIp, String dstIp, int srcPort, int dstPort, int protocol) {
        return shouldAllow(new FiveTuple(srcIp, dstIp, srcPort, dstPort, protocol), Direction.INBOUND);
    }

    // ===================== 工厂接口实现 =====================
    @Override
    public FirewallRule produce() {
        return new RuleBuilder().build("TEMPLATE-" + UUID.randomUUID());
    }

    @Override
    public void reset() {
        createdRules.clear();
        ruleMap.clear();
        ruleCounter = 0;
    }

    // 获取启用的规则（按优先级排序）
    public List<FirewallRule> getEnabledRules() {
        return createdRules.stream()
                .filter(FirewallRule::isEnabled)
                .sorted(Comparator.comparingInt(FirewallRule::getPriority))
                .toList();
    }

    // ===================== 规则管理（已实现） =====================
    public void enableRule(String ruleId) {
        Optional.ofNullable(ruleMap.get(ruleId)).ifPresent(rule -> rule.setEnabled(true));
    }

    public void disableRule(String ruleId) {
        Optional.ofNullable(ruleMap.get(ruleId)).ifPresent(rule -> rule.setEnabled(false));
    }

    public void removeRule(String ruleId) {
        Optional.ofNullable(ruleMap.remove(ruleId)).ifPresent(createdRules::remove);
    }

    public FirewallRule getRule(String ruleId) {
        return ruleMap.get(ruleId);
    }

    public List<FirewallRule> getAllRules() {
        return new ArrayList<>(createdRules);
    }

    public List<FirewallRule> getRulesByAction(Action action) {
        return createdRules.stream()
                .filter(rule -> rule.getAction() == action)
                .toList();
    }

    public Optional<FirewallRule> findMatchingRule(FiveTuple fiveTuple, Direction direction) {
        return getEnabledRules().stream()
                .filter(rule -> ruleMatches(rule, fiveTuple, direction))
                .findFirst();
    }

    public int getRuleCount() {
        return createdRules.size();
    }
}