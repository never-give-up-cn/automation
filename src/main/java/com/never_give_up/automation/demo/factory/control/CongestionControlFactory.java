package com.never_give_up.automation.demo.factory.control;

import lombok.Getter;

@Getter
public class CongestionControlFactory {
    private int cwnd = 1;
    private int ssthresh = 12;
    private int rwnd = 3;
    private int packetsAckedSinceLastIncrease = 0;

    public enum Phase {
        SLOW_START, CONGESTION_AVOIDANCE
    }

    public Phase getCurrentPhase() {
        return cwnd < ssthresh ? Phase.SLOW_START : Phase.CONGESTION_AVOIDANCE;
    }

    public int getEffectiveWindow() {
        return Math.min(cwnd, rwnd);
    }

    public void onAckReceived() {
        if (cwnd < ssthresh) {
            cwnd++;
        } else {
            packetsAckedSinceLastIncrease++;
            if (packetsAckedSinceLastIncrease >= cwnd) {
                cwnd++;
                packetsAckedSinceLastIncrease = 0;
            }
        }
    }

    public void onTimeout() {
        ssthresh = Math.max(2, cwnd / 2);
        cwnd = 1;
        packetsAckedSinceLastIncrease = 0;
    }

    public void onFastRetransmit() {
        ssthresh = Math.max(2, cwnd / 2);
        cwnd = ssthresh + 3;
    }

    public void onDuplicateAck() {
        if (getCurrentPhase() == Phase.CONGESTION_AVOIDANCE) {
            cwnd++;
        }
    }

    public void setRwnd(int size) {
        this.rwnd = size;
    }

    public void setSsthresh(int threshold) {
        this.ssthresh = threshold;
    }

    public void reset() {
        cwnd = 1;
        ssthresh = 12;
        rwnd = 3;
        packetsAckedSinceLastIncrease = 0;
    }

    public String getStatus() {
        return String.format("cwnd=%d, ssthresh=%d, rwnd=%d, phase=%s",
                cwnd, ssthresh, rwnd, getCurrentPhase());
    }
}
