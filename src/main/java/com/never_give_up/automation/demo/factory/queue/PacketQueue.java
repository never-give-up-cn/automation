package com.never_give_up.automation.demo.factory.queue;

import com.never_give_up.automation.demo.model.BasePacket;

public interface PacketQueue {
    boolean enqueue(BasePacket packet);
    BasePacket dequeue();
    boolean isEmpty();
    boolean isFull();
    int size();
    int capacity();
    void clear();
    String getName();
    PacketQueueFactory.QueuePolicy getPolicy();
    long getEnqueueCount();
    long getDequeueCount();
    long getDropCount();
}
