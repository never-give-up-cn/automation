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

    public boolean match(String sIp, String dIp, String proto) {
        for (AclRule r : ruleList) {
            if (r.srcIp.equals(sIp) && r.dstIp.equals(dIp) && r.proto.equals(proto)) {
                return r.permit;
            }
        }
        return false;
    }

    public void reset() {
        ruleList.clear();
    }

    public void clear() {
        reset();
    }
}