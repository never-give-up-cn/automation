package com.never_give_up.automation.demo.factory.control;

import com.never_give_up.automation.demo.model.FiveTuple;
import lombok.Data;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SessionFactory {
    private final Map<FiveTuple, Session> sessions = new ConcurrentHashMap<>();

    @Data
    public static class Session {
        private final FiveTuple fiveTuple;
        private final long createTime;
        private long lastActiveTime;
        private int packetCount;
        private long bytesTransferred;
        private String state;

        public Session(FiveTuple fiveTuple) {
            this.fiveTuple = fiveTuple;
            this.createTime = System.currentTimeMillis();
            this.lastActiveTime = createTime;
            this.packetCount = 0;
            this.bytesTransferred = 0;
            this.state = "ESTABLISHED";
        }

        public void updateActivity() {
            this.lastActiveTime = System.currentTimeMillis();
        }

        public void addPacket(int bytes) {
            this.packetCount++;
            this.bytesTransferred += bytes;
            updateActivity();
        }
    }

    public Session createSession(FiveTuple fiveTuple) {
        Session session = new Session(fiveTuple);
        sessions.put(fiveTuple, session);
        return session;
    }

    public Session getSession(FiveTuple fiveTuple) {
        return sessions.get(fiveTuple);
    }

    public void closeSession(FiveTuple fiveTuple) {
        Session session = sessions.get(fiveTuple);
        if (session != null) {
            session.setState("CLOSED");
        }
    }

    public void removeSession(FiveTuple fiveTuple) {
        sessions.remove(fiveTuple);
    }

    public int getActiveSessionCount() {
        return (int) sessions.values().stream()
                .filter(s -> "ESTABLISHED".equals(s.getState()))
                .count();
    }

    public void clear() {
        sessions.clear();
    }
}
