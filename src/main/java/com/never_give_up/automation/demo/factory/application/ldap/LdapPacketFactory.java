package com.never_give_up.automation.demo.factory.application.ldap;

public class LdapPacketFactory {
    private int msgId = 1;

    public byte[] buildBindRequest(String dn, String pwd) {
        byte[] ldap = new byte[48];
        ldap[0] = 0x30;
        ldap[1] = 46;
        ldap[2] = 0x02;
        ldap[3] = 1;
        ldap[4] = (byte) msgId;
        msgId++;
        return ldap;
    }

    public byte[] buildSearchRequest(String baseDn) {
        return new byte[32];
    }

    public void reset() {
        msgId = 1;
    }
}