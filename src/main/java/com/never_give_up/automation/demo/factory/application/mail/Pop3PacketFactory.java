package com.never_give_up.automation.demo.factory.application.mail;

public class Pop3PacketFactory {
    private int mailCount = 0;

    public byte[] buildUserCmd(String user) {
        return ("USER " + user + "\r\n").getBytes();
    }

    public byte[] buildPassCmd(String pwd) {
        return ("PASS " + pwd + "\r\n").getBytes();
    }

    public byte[] buildListCmd() {
        return "LIST\r\n".getBytes();
    }

    public byte[] buildRetrCmd(int index) {
        return ("RETR " + index + "\r\n").getBytes();
    }

    public void setMailCount(int count) {
        this.mailCount = count;
    }

    public void reset() {
        mailCount = 0;
    }
}