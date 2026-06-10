package com.never_give_up.automation.demo.factory.transport.tcp;

public class TcpKeepAliveFactory {
    private int idle = 30;
    private int interval = 5;
    private int probes = 4;

    public byte[] buildKeepAliveOption() {
        return new byte[]{1, 1, 1};
    }

    public void reset() {
        idle = 30;
        interval = 5;
        probes = 4;
    }
}