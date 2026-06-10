package com.never_give_up.automation.demo.factory.multicast;

import java.util.HashSet;
import java.util.Set;

public class MldFactory {
    private final Set<String> mldGroups = new HashSet<>();

    public byte[] buildMldReport(String ipv6Group) {
        mldGroups.add(ipv6Group);
        return new byte[24];
    }

    public void reset() {
        mldGroups.clear();
    }
}