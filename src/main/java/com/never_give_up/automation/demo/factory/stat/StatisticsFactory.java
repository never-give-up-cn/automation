package com.never_give_up.automation.demo.factory.stat;

public class StatisticsFactory {
    public long packetsTx, packetsRx;
    public long bytesTx, bytesRx;
    public long loss;
    public long delay;
    public long rtt;
    public long jitter;

    public void tx(int bytes) { packetsTx++; bytesTx += bytes; }
    public void rx(int bytes) { packetsRx++; bytesRx += bytes; }
    public void drop() { loss++; }

    public void reset() {
        packetsTx = packetsRx = bytesTx = bytesRx = loss = delay = rtt = jitter = 0;
    }
}