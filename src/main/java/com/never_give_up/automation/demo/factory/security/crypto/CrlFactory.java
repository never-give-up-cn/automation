package com.never_give_up.automation.demo.factory.security.crypto;

import java.util.HashSet;
import java.util.Set;

/** 证书吊销列表 CRL */
public class CrlFactory {
    private final Set<String> revokedSn = new HashSet<>();

    public void revokeCert(String serialNumber) {
        revokedSn.add(serialNumber);
    }

    public boolean isRevoked(String sn) {
        return revokedSn.contains(sn);
    }

    public void reset() {
        revokedSn.clear();
    }
}