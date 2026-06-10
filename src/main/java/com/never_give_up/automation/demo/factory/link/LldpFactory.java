package com.never_give_up.automation.demo.factory.link;

public class LldpFactory {
    private String chassisId = "00:11:22:33:44:55";
    private String portId = "eth0";

    public byte[] buildLldpFrame() {
        return new byte[100];
    }

    public void reset() {
        chassisId = "00:11:22:33:44:55";
        portId = "eth0";
    }
}