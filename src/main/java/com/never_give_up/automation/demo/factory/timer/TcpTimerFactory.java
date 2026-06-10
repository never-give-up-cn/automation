package com.never_give_up.automation.demo.factory.timer;
//7. TCP 重传定时器工厂（TcpTimerFactory）
public class TcpTimerFactory {
    private long srtt = 0;
    private long rttvar = 0;
    private long rto = 1000;

    public void updateRto(long rtt) {
        if (srtt == 0) {
            srtt = rtt;
            rttvar = rtt / 2;
        } else {
            rttvar = (3 * rttvar + Math.abs(srtt - rtt)) / 4;
            srtt = (7 * srtt + rtt) / 8;
        }
        rto = srtt + Math.max(100, 4 * rttvar);
    }

    public long getRto() {
        return rto;
    }
}