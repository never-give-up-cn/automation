package com.never_give_up.automation.demo.factory.application.mail;

public class ImapPacketFactory {
    private String tag = "A001";

    public byte[] buildLogin(String user, String pwd) {
        return (tag + " LOGIN " + user + " " + pwd + "\r\n").getBytes();
    }

    public byte[] buildSelect(String box) {
        return (tag + " SELECT " + box + "\r\n").getBytes();
    }

    public byte[] buildFetch(int seq) {
        return (tag + " FETCH " + seq + " BODY[]\r\n").getBytes();
    }

    public void reset() {
        tag = "A001";
    }
}