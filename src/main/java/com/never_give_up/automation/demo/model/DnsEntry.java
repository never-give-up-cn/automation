package com.never_give_up.automation.demo.model;

public class DnsEntry {
    public String domain;
    public String ipAddress;
    public long ttl;
    public long createTime;

    public DnsEntry(String domain, String ip, long ttl) {
        this.domain = domain;
        this.ipAddress = ip;
        this.ttl = ttl;
        this.createTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createTime > ttl;
    }

    public long getRemainingMs() {
        return ttl - (System.currentTimeMillis() - createTime);
    }
}
