package com.never_give_up.automation.demo.factory.security;

/** 入侵防御系统 IPS */
public class IpsFactory {
    private final String[] blackIp = {"10.0.0.99", "192.168.1.88"};

    public boolean isAttack(String srcIp, byte[] packet) {
        // 简易黑名单 + 特征匹配
        for (String ip : blackIp) {
            if (ip.equals(srcIp)) return true;
        }
        return false;
    }

    public void reset() {}
}