package com.never_give_up.automation.demo.factory.socket;

import java.util.HashMap;
import java.util.Map;

public class SocketFactory {
    private final Map<Integer, Socket> socketMap = new HashMap<>();

    public Socket createSocket(String srcIp, int srcPort, String dstIp, int dstPort) {
        Socket sock = new Socket(srcIp, srcPort, dstIp, dstPort);
        socketMap.put(srcPort, sock);
        return sock;
    }

    public Socket getSocket(int port) {
        return socketMap.get(port);
    }

    public void closeSocket(int port) {
        socketMap.remove(port);
    }

    public void reset() {
        socketMap.clear();
    }
}