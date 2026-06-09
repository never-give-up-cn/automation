package com.never_give_up.automation.demo.factory.queue;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.BasePacket;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.PriorityBlockingQueue;

@Getter
public class PacketQueueFactory implements INetworkFactory<PacketQueue> {
    private final Map<String, PacketQueue> createdQueues = new HashMap<>();
    private int queueCounter = 0;

    public enum QueuePolicy {
        FIFO,           // 先进先出
        LIFO,           // 后进先出
        PRIORITY,       // 优先级队列
        WEIGHTED_FAIR,  // 加权公平队列
        ROUND_ROBIN     // 轮询
    }

    public enum DropPolicy {
        DROP_TAIL,      // 尾部丢弃
        RANDOM_EARLY,   // 随机早期检测
        HEAD_DROP       // 头部丢弃
    }

    private abstract static class BasePacketQueue implements PacketQueue {
        protected final String name;
        protected final int capacity;
        protected final QueuePolicy policy;
        protected final DropPolicy dropPolicy;
        protected long enqueueCount = 0;
        protected long dequeueCount = 0;
        protected long dropCount = 0;

        protected BasePacketQueue(String name, int capacity, QueuePolicy policy, DropPolicy dropPolicy) {
            this.name = name;
            this.capacity = capacity;
            this.policy = policy;
            this.dropPolicy = dropPolicy;
        }

        @Override
        public boolean isEmpty() {
            return size() == 0;
        }

        @Override
        public boolean isFull() {
            return size() >= capacity;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public QueuePolicy getPolicy() {
            return policy;
        }

        @Override
        public long getEnqueueCount() {
            return enqueueCount;
        }

        @Override
        public long getDequeueCount() {
            return dequeueCount;
        }

        @Override
        public long getDropCount() {
            return dropCount;
        }
    }

    private static class FifoPacketQueue extends BasePacketQueue {
        private final Queue<BasePacket> queue;

        public FifoPacketQueue(String name, int capacity, DropPolicy dropPolicy) {
            super(name, capacity, QueuePolicy.FIFO, dropPolicy);
            this.queue = new LinkedList<>();
        }

        @Override
        public boolean enqueue(BasePacket packet) {
            if (isFull()) {
                handleDrop(packet);
                return false;
            }
            queue.offer(packet);
            enqueueCount++;
            return true;
        }

        @Override
        public BasePacket dequeue() {
            BasePacket packet = queue.poll();
            if (packet != null) {
                dequeueCount++;
            }
            return packet;
        }

        @Override
        public int size() {
            return queue.size();
        }

        @Override
        public void clear() {
            queue.clear();
        }

        private void handleDrop(BasePacket packet) {
            dropCount++;
            switch (dropPolicy) {
                case DROP_TAIL:
                    break;
                case HEAD_DROP:
                    BasePacket dropped = queue.poll();
                    if (dropped != null) {
                        queue.offer(packet);
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private static class PriorityPacketQueue extends BasePacketQueue {
        private final PriorityQueue<BasePacket> queue;

        public PriorityPacketQueue(String name, int capacity, DropPolicy dropPolicy) {
            super(name, capacity, QueuePolicy.PRIORITY, dropPolicy);
            this.queue = new PriorityQueue<>(Comparator.comparingInt(this::getPriority));
        }

        private int getPriority(BasePacket packet) {
            if (packet.getPacketType() == null) return 5;
            switch (packet.getPacketType().toUpperCase()) {
                case "ICMP": return 1;
                case "TCP": return 3;
                case "UDP": return 5;
                default: return 5;
            }
        }

        @Override
        public boolean enqueue(BasePacket packet) {
            if (isFull()) {
                dropCount++;
                return false;
            }
            queue.offer(packet);
            enqueueCount++;
            return true;
        }

        @Override
        public BasePacket dequeue() {
            BasePacket packet = queue.poll();
            if (packet != null) {
                dequeueCount++;
            }
            return packet;
        }

        @Override
        public int size() {
            return queue.size();
        }

        @Override
        public void clear() {
            queue.clear();
        }
    }

    public PacketQueue createQueue(String name, int capacity, QueuePolicy policy, DropPolicy dropPolicy) {
        PacketQueue queue;
        switch (policy) {
            case PRIORITY:
                queue = new PriorityPacketQueue(name, capacity, dropPolicy);
                break;
            case FIFO:
            default:
                queue = new FifoPacketQueue(name, capacity, dropPolicy);
                break;
        }
        createdQueues.put(name, queue);
        queueCounter++;
        return queue;
    }

    public PacketQueue createFifoQueue(String name, int capacity) {
        return createQueue(name, capacity, QueuePolicy.FIFO, DropPolicy.DROP_TAIL);
    }

    public PacketQueue createPriorityQueue(String name, int capacity) {
        return createQueue(name, capacity, QueuePolicy.PRIORITY, DropPolicy.DROP_TAIL);
    }

    public PacketQueue createDefaultQueue(String name) {
        queueCounter++;
        return createFifoQueue("Queue-" + name, 100);
    }

    public PacketQueue getQueue(String name) {
        return createdQueues.get(name);
    }

    public void removeQueue(String name) {
        PacketQueue queue = createdQueues.remove(name);
        if (queue != null) {
            queue.clear();
        }
    }

    public List<PacketQueue> getAllQueues() {
        return new ArrayList<>(createdQueues.values());
    }

    @Override
    public PacketQueue produce() {
        queueCounter++;
        return createDefaultQueue(String.valueOf(queueCounter));
    }

    @Override
    public void reset() {
        createdQueues.values().forEach(PacketQueue::clear);
        createdQueues.clear();
        queueCounter = 0;
    }

    public int getQueueCount() {
        return createdQueues.size();
    }
}
