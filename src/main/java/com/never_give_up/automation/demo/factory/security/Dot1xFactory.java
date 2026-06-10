package com.never_give_up.automation.demo.factory.security;

/** 802.1X 端口认证 */
public class Dot1xFactory {
    private boolean authenticated = false;

    public byte[] buildEapStart() {
        byte[] eap = new byte[6];
        eap[0] = 0x01; // EAPOL Start
        return eap;
    }

    public void setAuthResult(boolean pass) {
        this.authenticated = pass;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void reset() {
        authenticated = false;
    }
}