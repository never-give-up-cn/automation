package com.never_give_up.automation.demo.factory.security;

import java.util.ArrayList;
import java.util.List;

/** ACL 访问控制列表 */
public class AclFactory {
    public static class AclRule {
        String srcIp;
        String dstIp;
        String proto;
        boolean permit;
    }

    private final List<AclRule> ruleList = new ArrayList<>();

    public void addRule(String sIp, String dIp, String proto, boolean permit) {
        AclRule rule = new AclRule();
        rule.srcIp = sIp;
        rule.dstIp = dIp;
        rule.proto = proto;
        rule.permit = permit;
        ruleList.add(rule);
    }

    // ✅ 修复：支持 0.0.0.0 = 任意IP，支持 null 匹配
    public boolean match(String sIp, String dIp, String proto) {
        // 没有规则 = 默认允许
        if (ruleList.isEmpty()) {
            return true;
        }

        for (AclRule r : ruleList) {
            // 源IP匹配（支持任意）
            boolean srcMatch = r.srcIp.equals("0.0.0.0") || r.srcIp.equals(sIp);
            // 目标IP匹配（支持任意）
            boolean dstMatch = r.dstIp.equals("0.0.0.0") || r.dstIp.equals(dIp);
            // 协议匹配（支持任意）
            boolean protoMatch = r.proto.equals("ANY") || r.proto.equals(proto);

            if (srcMatch && dstMatch && protoMatch) {
                return r.permit;
            }
        }

        // 默认拒绝
        return false;
    }

    public void reset() {
        ruleList.clear();
    }

    public void clear() {
        reset();
    }
}