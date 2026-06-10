package com.never_give_up.automation.demo.factory.security;

import java.util.HashSet;
import java.util.Set;

/** MAC 地址认证 */
public class MacAuthFactory {
    private final Set<String> allowMac = new HashSet<>();

    public void allowMac(String mac) {
        allowMac.add(mac);
    }

    public boolean check(String mac) {
        return allowMac.contains(mac);
    }

    public void reset() {
        allowMac.clear();
    }
}