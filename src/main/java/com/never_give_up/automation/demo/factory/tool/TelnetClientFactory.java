package com.never_give_up.automation.demo.factory.tool;

/** Telnet 客户端工具 */
public class TelnetClientFactory {
    private String remoteHost;
    private int remotePort;

    public void connect(String host, int port) {
        this.remoteHost = host;
        this.remotePort = port;
    }

    public byte[] sendData(String data) {
        return data.getBytes();
    }

    public void reset() {
        remoteHost = null;
        remotePort = 0;
    }
}