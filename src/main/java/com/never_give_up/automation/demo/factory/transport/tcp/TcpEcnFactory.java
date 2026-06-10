package com.never_give_up.automation.demo.factory.transport.tcp;

public class TcpEcnFactory {
    private boolean ecnEnabled = true;

    public byte[] buildEcnFlag() {
        return new byte[]{0x03};
    }

    public void reset() {
        ecnEnabled = true;
    }
}