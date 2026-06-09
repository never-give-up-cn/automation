package com.never_give_up.automation.demo.factory.control;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 会话管理工厂
 */
public class SessionFactory {

    /**
     * 会话类 - 改为静态内部类以避免内存泄漏
     */
    public static class Session {
        private String sessionId;
        private String srcIp;
        private String dstIp;
        private int srcPort;
        private int dstPort;
        private long createTime;
        private long lastActiveTime;
        private String state; // ESTABLISHED, CLOSED, etc.

        public Session(String srcIp, String dstIp, int srcPort, int dstPort) {
            this.srcIp = srcIp;
            this.dstIp = dstIp;
            this.srcPort = srcPort;
            this.dstPort = dstPort;
            this.sessionId = generateSessionId(srcIp, dstIp, srcPort, dstPort);
            this.createTime = System.currentTimeMillis();
            this.lastActiveTime = this.createTime;
            this.state = "ESTABLISHED";
        }

        /**
         * 生成会话ID - 改为静态方法更合适
         */
        private static String generateSessionId(String srcIp, String dstIp, int srcPort, int dstPort) {
            return srcIp + ":" + srcPort + "-" + dstIp + ":" + dstPort;
        }

        public void updateActiveTime() {
            this.lastActiveTime = System.currentTimeMillis();
        }

        public boolean isExpired(long timeoutMs) {
            return System.currentTimeMillis() - lastActiveTime > timeoutMs;
        }

        // Getters
        public String getSessionId() { return sessionId; }
        public String getSrcIp() { return srcIp; }
        public String getDstIp() { return dstIp; }
        public int getSrcPort() { return srcPort; }
        public int getDstPort() { return dstPort; }
        public String getState() { return state; }
        public void setState(String state) { this.state = state; }

        @Override
        public String toString() {
            return String.format("Session[%s, state=%s]", sessionId, state);
        }
    }

    private Map<String, Session> sessions = new ConcurrentHashMap<>();

    /**
     * 创建会话 - 支持4个参数
     */
    public Session createSession(String srcIp, String dstIp, int srcPort, int dstPort) {
        String sessionId = srcIp + ":" + srcPort + "-" + dstIp + ":" + dstPort;
        Session session = new Session(srcIp, dstIp, srcPort, dstPort);
        sessions.put(sessionId, session);
        System.out.println(String.format("【会话创建】: %s:%d → %s:%d", srcIp, srcPort, dstIp, dstPort));
        return session;
    }

    /**
     * 创建会话 - 支持2个参数（兼容旧代码）
     */
    public Session createSession(String srcIp, String dstIp) {
        return createSession(srcIp, dstIp, 0, 0);
    }

    /**
     * 获取会话
     */
    public Session getSession(String srcIp, String dstIp, int srcPort, int dstPort) {
        String sessionId = srcIp + ":" + srcPort + "-" + dstIp + ":" + dstPort;
        return sessions.get(sessionId);
    }

    /**
     * 通过会话ID获取会话
     */
    public Session getSessionById(String sessionId) {
        return sessions.get(sessionId);
    }

    /**
     * 更新会话活动时间
     */
    public void updateSession(String srcIp, String dstIp, int srcPort, int dstPort) {
        Session session = getSession(srcIp, dstIp, srcPort, dstPort);
        if (session != null) {
            session.updateActiveTime();
        }
    }

    /**
     * 关闭会话
     */
    public void closeSession(String srcIp, String dstIp, int srcPort, int dstPort) {
        String sessionId = srcIp + ":" + srcPort + "-" + dstIp + ":" + dstPort;
        Session session = sessions.get(sessionId);
        if (session != null) {
            session.setState("CLOSED");
            System.out.println("【会话关闭】: " + sessionId);
        }
    }

    /**
     * 移除过期会话
     */
    public void removeExpiredSessions(long timeoutMs) {
        sessions.values().removeIf(session -> session.isExpired(timeoutMs));
    }

    /**
     * 获取活跃会话数
     */
    public int getActiveSessionCount() {
        return sessions.size();
    }

    /**
     * 重置所有会话
     */
    public void reset() {
        sessions.clear();
    }
}