package com.never_give_up.automation.demo.factory.application.dns;

import java.util.HashMap;
import java.util.Map;

public class DnsZoneFactory {
    private final Map<String, DnsRecord> records = new HashMap<>();

    public static class DnsRecord {
        public String type; // A, AAAA, CNAME, MX, NS, SOA, TXT
        public String value;
        public int ttl;
    }

    public void addRecord(String domain, String type, String value) {
        DnsRecord r = new DnsRecord();
        r.type = type;
        r.value = value;
        r.ttl = 300;
        records.put(domain + "_" + type, r);
    }

    public DnsRecord query(String domain, String type) {
        return records.get(domain + "_" + type);
    }

    public void reset() { records.clear(); }
}