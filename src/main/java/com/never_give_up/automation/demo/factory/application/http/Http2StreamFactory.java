package com.never_give_up.automation.demo.factory.application.http;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP/2 流管理工厂 - 流状态、标识符分配、优先级
 * 基于 RFC 7540 Section 5
 */
public class Http2StreamFactory {

    public enum StreamState {
        IDLE,          // 空闲
        OPEN,          // 开放
        RESERVED_LOCAL,   // 本地保留（PUSH_PROMISE）
        RESERVED_REMOTE,  // 远程保留
        HALF_CLOSED_LOCAL, // 半关闭（本地）
        HALF_CLOSED_REMOTE,// 半关闭（远程）
        CLOSED         // 关闭
    }

    /**
     * HTTP/2 流
     */
    public static class Http2Stream {
        private final int streamId;
        private StreamState state;
        private int weight = 16;
        private boolean exclusive;
        private int parentStreamId;
        private long createdTime;

        public Http2Stream(int streamId) {
            this.streamId = streamId;
            this.state = StreamState.IDLE;
            this.createdTime = System.currentTimeMillis();
        }

        public int getStreamId() { return streamId; }
        public StreamState getState() { return state; }
        public void setState(StreamState state) { this.state = state; }
        public int getWeight() { return weight; }
        public void setWeight(int weight) { this.weight = weight; }
        public boolean isExclusive() { return exclusive; }
        public void setExclusive(boolean exclusive) { this.exclusive = exclusive; }
        public int getParentStreamId() { return parentStreamId; }
        public void setParentStreamId(int parentStreamId) { this.parentStreamId = parentStreamId; }
        public long getCreatedTime() { return createdTime; }
    }

    private int nextClientStreamId = 1;  // 客户端流 ID 从 1 开始（奇数）
    private int nextServerStreamId = 2;  // 服务端流 ID 从 2 开始（偶数）
    private final Map<Integer, Http2Stream> streams = new HashMap<>();

    /**
     * 分配客户端流 ID
     */
    public int allocateClientStreamId() {
        int id = nextClientStreamId;
        nextClientStreamId += 2;
        return id;
    }

    /**
     * 分配服务端流 ID（用于 PUSH_PROMISE）
     */
    public int allocateServerStreamId() {
        int id = nextServerStreamId;
        nextServerStreamId += 2;
        return id;
    }

    /**
     * 创建新流
     */
    public Http2Stream createStream(int streamId) {
        Http2Stream stream = new Http2Stream(streamId);
        stream.setState(StreamState.OPEN);
        streams.put(streamId, stream);
        return stream;
    }

    /**
     * 从 IDLE 转换到 OPEN
     */
    public boolean openStream(int streamId) {
        Http2Stream stream = streams.get(streamId);
        if (stream == null) {
            stream = new Http2Stream(streamId);
            stream.setState(StreamState.OPEN);
            streams.put(streamId, stream);
            return true;
        }
        return stream.getState() == StreamState.IDLE;
    }

    /**
     * 关闭流
     */
    public boolean closeStream(int streamId) {
        Http2Stream stream = streams.get(streamId);
        if (stream != null) {
            stream.setState(StreamState.CLOSED);
            return true;
        }
        return false;
    }

    /**
     * 半关闭流（本地）
     */
    public boolean halfCloseLocal(int streamId) {
        Http2Stream stream = streams.get(streamId);
        if (stream == null || stream.getState() != StreamState.OPEN) return false;
        stream.setState(StreamState.HALF_CLOSED_LOCAL);
        return true;
    }

    /**
     * 半关闭流（远程）
     */
    public boolean halfCloseRemote(int streamId) {
        Http2Stream stream = streams.get(streamId);
        if (stream == null || stream.getState() != StreamState.OPEN) return false;
        stream.setState(StreamState.HALF_CLOSED_REMOTE);
        return true;
    }

    /**
     * 设置流优先级
     */
    public void setPriority(int streamId, boolean exclusive, int parentStreamId, int weight) {
        Http2Stream stream = streams.get(streamId);
        if (stream != null) {
            stream.setExclusive(exclusive);
            stream.setParentStreamId(parentStreamId);
            stream.setWeight(Math.max(1, Math.min(256, weight)));
        }
    }

    /**
     * 获取流
     */
    public Http2Stream getStream(int streamId) {
        return streams.get(streamId);
    }

    /**
     * 获取指定状态的流数量
     */
    public long getStreamCountByState(StreamState state) {
        return streams.values().stream().filter(s -> s.getState() == state).count();
    }

    /**
     * 获取有效流数量（OPEN + HALF_CLOSED）
     */
    public long getActiveStreamCount() {
        return streams.values().stream()
                .filter(s -> s.getState() == StreamState.OPEN
                        || s.getState() == StreamState.HALF_CLOSED_LOCAL
                        || s.getState() == StreamState.HALF_CLOSED_REMOTE)
                .count();
    }

    /**
     * 判断流 ID 是否为客户端发起（奇数）
     */
    public boolean isClientInitiated(int streamId) {
        return (streamId & 0x01) == 1;
    }

    /**
     * 重置
     */
    public void reset() {
        streams.clear();
        nextClientStreamId = 1;
        nextServerStreamId = 2;
    }

    /**
     * 获取所有流
     */
    public Map<Integer, Http2Stream> getAllStreams() {
        return streams;
    }
}
