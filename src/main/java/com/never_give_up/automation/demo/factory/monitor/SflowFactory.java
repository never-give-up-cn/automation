package com.never_give_up.automation.demo.factory.monitor;

/** sFlow 采样流 */
public class SflowFactory {
    private int sampleRate = 1000;

    public boolean doSample(int pktSeq) {
        return pktSeq % sampleRate == 0;
    }

    public void setSampleRate(int rate) {
        this.sampleRate = rate;
    }

    public byte[] buildSflowPacket(byte[] sampleData) {
        byte[] sflow = new byte[20 + sampleData.length];
        System.arraycopy(sampleData, 0, sflow, 20, sampleData.length);
        return sflow;
    }

    public void reset() {
        sampleRate = 1000;
    }
}