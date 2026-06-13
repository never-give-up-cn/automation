package com.never_give_up.automation.demo.model;

public class RetransmissionTask {
    public int seqNum;
    public long sendTime;
    public int retryCount = 0;
    public boolean isAcked = false;

    public RetransmissionTask(int seqNum, long sendTime) {
        this.seqNum = seqNum;
        this.sendTime = sendTime;
    }
}
