package com.never_give_up.automation.demo.factory.session;

import java.util.HashMap;
import java.util.Map;

public class SessionTableFactory {
    private final Map<String, Session> sessionMap = new HashMap<>();

    public static class Session {
        public String srcIp, dstIp;
        public int srcPort, dstPort;
        public String proto;
        public long createTime;
    }

    public void createSession(String srcIp, int sPort, String dstIp, int dPort, String proto) {
        String key = srcIp + sPort + dstIp + dPort + proto;
        Session s = new Session();
        s.srcIp = srcIp;
        s.dstIp = dstIp;
        s.srcPort = sPort;
        s.dstPort = dPort;
        s.proto = proto;
        sessionMap.put(key, s);
    }

    public boolean exists(String srcIp, int s, String d, int dp, String p) {
        return sessionMap.containsKey(srcIp + s + d + dp + p);
    }

    public void reset() { sessionMap.clear(); }
}