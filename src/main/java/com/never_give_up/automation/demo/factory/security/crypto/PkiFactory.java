package com.never_give_up.automation.demo.factory.security.crypto;

/** PKI 公钥基础设施 */
public class PkiFactory {
    private String rootCa = "Root-CA-001";

    public boolean verifyChain(String[] certChain) {
        return certChain != null && certChain.length > 0;
    }

    public void reset() {
        rootCa = "Root-CA-001";
    }
}