package com.never_give_up.automation.demo.factory.connection;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//9. TCP 连接管理工厂（TcpConnectionFactory）
public class TcpConnectionFactory {
    private final Map<String, TcpControlBlock> conns = new ConcurrentHashMap<>();

    public TcpControlBlock create(int srcIp, int srcPort, int dstIp, int dstPort) {
        String key = srcIp + ":" + srcPort + "->" + dstIp + ":" + dstPort;
        TcpControlBlock cb = new TcpControlBlock();
        cb.state = "SYN_SENT";
        conns.put(key, cb);
        return cb;
    }

    public static class TcpControlBlock {
        public String state;
        public long sndUna;
        public long sndNxt;
        public long rcvNxt;
    }
}