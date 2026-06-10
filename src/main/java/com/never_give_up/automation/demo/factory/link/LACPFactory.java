package com.never_give_up.automation.demo.factory.link;

public class LACPFactory {
    public int actorKey = 1;
    private int partnerKey = 1;

    public byte[] buildLacpPdu() {
        return new byte[100];
    }

    public void reset() {
        actorKey = 1;
        partnerKey = 1;
    }
}