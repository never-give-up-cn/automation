package com.never_give_up.automation.demo.winModel;

import lombok.Data;

@Data
public class RetransmissionTask {
    private int seqNum;
    private long sendTime;
    private int retryCount;
    private boolean acked;

    public RetransmissionTask(int seqNum, long sendTime) {
        this.seqNum = seqNum;
        this.sendTime = sendTime;
        this.retryCount = 0;
        this.acked = false;
    }
}
