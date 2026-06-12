package com.never_give_up.automation.demo.winModel;

import lombok.Data;

@Data
public class NatEntry {
    private String insideIp;
    private int insidePort;
    private String publicIp;
    private int publicPort;

    public NatEntry(String insideIp, int insidePort, String publicIp, int publicPort) {
        this.insideIp = insideIp;
        this.insidePort = insidePort;
        this.publicIp = publicIp;
        this.publicPort = publicPort;
    }
}

