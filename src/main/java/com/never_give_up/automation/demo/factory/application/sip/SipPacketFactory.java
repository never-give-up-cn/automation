package com.never_give_up.automation.demo.factory.application.sip;

public class SipPacketFactory {
    private String callId = "call-123456";

    public byte[] buildInvite(String uri) {
        String sip = "INVITE " + uri + " SIP/2.0\r\nCall-ID: " + callId + "\r\n\r\n";
        return sip.getBytes();
    }

    public byte[] buildBye() {
        return "BYE SIP/2.0\r\n\r\n".getBytes();
    }

    public void reset() {
        callId = "call-123456";
    }
}