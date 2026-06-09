package com.never_give_up.automation.demo.factory.control;

/**
 * 拥塞控制工厂
 */
public class CongestionControlFactory {

    private int cwnd = 1;      // 拥塞窗口
    private int ssthresh = 64;  // 慢启动阈值
    private int rtt = 100;      // 往返时间
    private int rto = 200;      // 重传超时
    private int dupAckCount = 0; // 重复 ACK 计数

    public CongestionControlFactory() {
        reset();
    }

    /**
     * 慢启动
     */
    public void slowStart(int currentCwnd) {
        if (currentCwnd < ssthresh) {
            cwnd = currentCwnd * 2;
            System.out.println("【慢启动】: cwnd=" + cwnd + ", ssthresh=" + ssthresh);
        } else {
            congestionAvoidance(currentCwnd);
        }
    }

    /**
     * 拥塞避免
     */
    public void congestionAvoidance(int currentCwnd) {
        if (currentCwnd >= ssthresh) {
            cwnd = currentCwnd + 1;
            System.out.println("【拥塞避免】: cwnd=" + cwnd);
        }
    }

    /**
     * 快速重传
     */
    public void fastRetransmit() {
        dupAckCount++;
        if (dupAckCount >= 3) {
            ssthresh = cwnd / 2;
            cwnd = ssthresh + 3;
            System.out.println("【快速重传】: ssthresh=" + ssthresh + ", cwnd=" + cwnd);
            dupAckCount = 0;
        }
    }

    /**
     * 超时处理
     */
    public void timeout() {
        ssthresh = cwnd / 2;
        cwnd = 1;
        dupAckCount = 0;
        System.out.println("【超时】: ssthresh=" + ssthresh + ", cwnd=" + cwnd);
    }

    /**
     * 收到 ACK 时调用
     */
    public void onAck(int currentCwnd) {
        if (currentCwnd < ssthresh) {
            cwnd = currentCwnd + 1;
        } else {
            cwnd = currentCwnd + 1 / cwnd;
        }
    }

    public int getCwnd() { return cwnd; }
    public int getSsthresh() { return ssthresh; }
    public void setCwnd(int cwnd) { this.cwnd = cwnd; }
    public void setSsthresh(int ssthresh) { this.ssthresh = ssthresh; }

    public void reset() {
        cwnd = 1;
        ssthresh = 64;
        dupAckCount = 0;
        rtt = 100;
        rto = 200;
    }
}