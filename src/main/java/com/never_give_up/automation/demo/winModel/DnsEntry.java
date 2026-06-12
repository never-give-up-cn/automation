package com.never_give_up.automation.demo.winModel;

import lombok.Data;

@Data
public class DnsEntry {
    private String domain;
    private String ipAddress;
    private long ttl;
    private long createTime;

    public DnsEntry(String domain, String ip, long ttl) {
        this.domain = domain;
        this.ipAddress = ip;
        this.ttl = ttl;
        this.createTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createTime > ttl;
    }
}