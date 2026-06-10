package com.never_give_up.automation.demo.factory.link;

public class StpFactory {
    public String rootBridge = "00:00:00:00:00:01";
    private int rootCost = 0;

    public byte[] buildBpdu() {
        return new byte[35];
    }

    public void reset() {
        rootBridge = "00:00:00:00:00:01";
        rootCost = 0;
    }
}