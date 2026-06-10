package com.never_give_up.automation.demo.factory.security.crypto;

/** X.509 证书解析与构造 */
public class X509Factory {
    private String subject = "CN=test-server";
    private String issuer = "CN=CA-Root";

    public byte[] buildCert() {
        byte[] cert = new byte[256];
        return cert;
    }

    public String getSubject() {
        return subject;
    }

    public void reset() {
        subject = "CN=test-server";
        issuer = "CN=CA-Root";
    }
}