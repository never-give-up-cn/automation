package com.never_give_up.automation.demo.factory.security;

/** Web 应用防火墙 WAF */
public class WafFactory {
    private static final String[] badKeyword = {"SELECT", "UNION", "DROP", "<script>"};

    public boolean checkHttpContent(String content) {
        for (String kw : badKeyword) {
            if (content.contains(kw)) {
                return false; // 拦截
            }
        }
        return true; // 放行
    }

    public void reset() {}
}