package com.never_give_up.automation.demo.factory.transport.tcp;

public class TcpStateMachineFactory {
    public enum TcpState {
        CLOSED, LISTEN, SYN_SENT, SYN_RECEIVED, ESTABLISHED,
        FIN_WAIT_1, FIN_WAIT_2, CLOSE_WAIT, LAST_ACK, TIME_WAIT
    }

    private TcpState currentState = TcpState.CLOSED;

    public void sendSyn() { currentState = TcpState.SYN_SENT; }
    public void recvSynAck() { currentState = TcpState.ESTABLISHED; }
    public void recvFin() { currentState = TcpState.CLOSE_WAIT; }
    public void timeWait() { currentState = TcpState.TIME_WAIT; }
    public void close() { currentState = TcpState.CLOSED; }

    public TcpState getState() { return currentState; }
    public void reset() { currentState = TcpState.CLOSED; }
}