package com.never_give_up.automation.demo.factory.qos;

import java.util.Queue;

public class SchedulerFactory {
    public enum Type { FIFO, PQ, WRR, DRR, RED }

    private Type type = Type.FIFO;

    public byte[] schedule(Queue<byte[]> queue) {
        if (queue.isEmpty()) return null;
        return queue.poll();
    }

    public void setType(Type t) { type = t; }
    public void reset() {}
}