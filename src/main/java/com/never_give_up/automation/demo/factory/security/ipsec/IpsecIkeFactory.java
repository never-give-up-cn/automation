package com.never_give_up.automation.demo.factory.security.ipsec;

/** IKE 密钥交换 */
public class IpsecIkeFactory {
    private int msgId = 1;

    public byte[] buildIkeSaInit() {
        byte[] ike = new byte[128];
        ike[0] = 0x20; // IKEv2 Header
        ike[4] = (byte) (msgId >> 24);
        msgId++;
        return ike;
    }

    public void reset() {
        msgId = 1;
    }
}