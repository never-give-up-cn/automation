package com.never_give_up.automation.demo.factory.socket;

public class Socket {
    public enum State { CLOSED, LISTEN, SYN_SENT, ESTABLISHED }

    private final String srcIp;
    private final int srcPort;
    private final String dstIp;
    private final int dstPort;
    private State state = State.CLOSED;

    public Socket(String srcIp, int srcPort, String dstIp, int dstPort) {
        this.srcIp = srcIp;
        this.srcPort = srcPort;
        this.dstIp = dstIp;
        this.dstPort = dstPort;
    }

    public void bind() { state = State.LISTEN; }
    public void listen() {}
    public void accept() { state = State.ESTABLISHED; }
    public void connect() { state = State.SYN_SENT; }
    public void close() { state = State.CLOSED; }

    // getter
}