package com.never_give_up.automation.demo.factory.security.crypto;

import java.util.HashMap;
import java.util.Map;

public class CertificateStoreFactory {
    private final Map<String, String> keyStore = new HashMap<>();
    private final Map<String, String> trustStore = new HashMap<>();

    public void addKey(String alias, String key) { keyStore.put(alias, key); }
    public void addTrustCert(String alias, String cert) { trustStore.put(alias, cert); }
    public boolean isTrusted(String cert) { return trustStore.containsValue(cert); }

    public void reset() {
        keyStore.clear();
        trustStore.clear();
    }
}