package com.never_give_up.automation.demo.factory.window;
//8. TCP 滑动窗口工厂（TcpWindowFactory）
public class TcpWindowFactory {
    private long cwnd = 1024;
    private long ssthresh = 16384;
    private long rwnd = 65535;

    public long effectiveWindow() {
        return Math.min(cwnd, rwnd);
    }

    public void ack() {
        if (cwnd < ssthresh) cwnd += 1024;
        else cwnd += 1024 * 1024 / (int) cwnd;
    }

    public void loss() {
        ssthresh = cwnd / 2;
        cwnd = 1024;
    }
}