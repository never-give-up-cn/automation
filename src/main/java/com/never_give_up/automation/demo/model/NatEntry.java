package com.never_give_up.automation.demo.model;

public class NatEntry {
    public String insideIp;
    public int insidePort;
    public String publicIp;
    public int publicPort;

    public NatEntry(String insideIp, int insidePort, String publicIp, int publicPort) {
        this.insideIp = insideIp;
        this.insidePort = insidePort;
        this.publicIp = publicIp;
        this.publicPort = publicPort;
    }
}
