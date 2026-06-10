package com.never_give_up.automation.demo.factory.tool;

import java.util.HashMap;
import java.util.Map;

/** Nslookup DNS 查询工具 */
public class NslookupFactory {
    private final Map<String, String> dnsMap = new HashMap<>();

    public void addDnsRecord(String domain, String ip) {
        dnsMap.put(domain, ip);
    }

    public String query(String domain) {
        return dnsMap.getOrDefault(domain, "NXDOMAIN");
    }

    public void reset() {
        dnsMap.clear();
    }
}