package com.never_give_up.automation.demo.factory.nat;

/** PCP 端口控制协议 */
public class PcpFactory {
    private int lifetime = 3600;

    public byte[] buildMapRequest(int intPort, String intIp) {
        byte[] pcp = new byte[24];
        pcp[0] = 0x02; // PCP Version
        pcp[1] = 0x01; // MAP Opcode
        return pcp;
    }

    public void setLifetime(int sec) {
        this.lifetime = sec;
    }

    public void reset() {
        lifetime = 3600;
    }
}