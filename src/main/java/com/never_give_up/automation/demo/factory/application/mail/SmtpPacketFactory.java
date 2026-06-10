package com.never_give_up.automation.demo.factory.application.mail;

public class SmtpPacketFactory {
    private String fromAddr;
    private String toAddr;

    public byte[] buildEhlo(String host) {
        return ("EHLO " + host + "\r\n").getBytes();
    }

    public byte[] buildMailFrom(String from) {
        this.fromAddr = from;
        return ("MAIL FROM:<" + from + ">\r\n").getBytes();
    }

    public byte[] buildRcptTo(String to) {
        this.toAddr = to;
        return ("RCPT TO:<" + to + ">\r\n").getBytes();
    }

    public byte[] buildData(String content) {
        return ("DATA\r\n" + content + "\r\n.\r\n").getBytes();
    }

    public void reset() {
        fromAddr = null;
        toAddr = null;
    }
}