package com.never_give_up.automation.demo.factory.application.http;

import java.nio.charset.StandardCharsets;

/**
 * HTTP/2 帧工厂 - 全部 10 种帧类型的构造与解析
 * 基于 RFC 7540 / RFC 9113
 */
public class Http2FrameFactory {

    // ========== 帧类型常量 ==========
    public static final byte FRAME_DATA = 0x00;
    public static final byte FRAME_HEADERS = 0x01;
    public static final byte FRAME_PRIORITY = 0x02;
    public static final byte FRAME_RST_STREAM = 0x03;
    public static final byte FRAME_SETTINGS = 0x04;
    public static final byte FRAME_PUSH_PROMISE = 0x05;
    public static final byte FRAME_PING = 0x06;
    public static final byte FRAME_GOAWAY = 0x07;
    public static final byte FRAME_WINDOW_UPDATE = 0x08;
    public static final byte FRAME_CONTINUATION = 0x09;

    // ========== 标志位常量 ==========
    public static final byte FLAG_END_STREAM = 0x01;
    public static final byte FLAG_ACK = 0x01;
    public static final byte FLAG_END_HEADERS = 0x04;
    public static final byte FLAG_PADDED = 0x08;
    public static final byte FLAG_PRIORITY = 0x20;

    /**
     * 构建通用帧头 (9 字节)
     * 格式: Length(3) | Type(1) | Flags(1) | StreamIdentifier(4)
     */
    public byte[] buildFrameHeader(int length, byte type, byte flags, int streamId) {
        byte[] header = new byte[9];
        // Length (3 bytes, big-endian)
        header[0] = (byte) ((length >> 16) & 0xFF);
        header[1] = (byte) ((length >> 8) & 0xFF);
        header[2] = (byte) (length & 0xFF);
        // Type
        header[3] = type;
        // Flags
        header[4] = flags;
        // Stream Identifier (31 bits, big-endian)
        header[5] = (byte) ((streamId >> 24) & 0xFF);
        header[6] = (byte) ((streamId >> 16) & 0xFF);
        header[7] = (byte) ((streamId >> 8) & 0xFF);
        header[8] = (byte) (streamId & 0xFF);
        return header;
    }

    /**
     * 构建完整帧
     */
    public byte[] buildFrame(byte type, byte flags, int streamId, byte[] payload) {
        int length = payload != null ? payload.length : 0;
        byte[] header = buildFrameHeader(length, type, flags, streamId);
        byte[] frame = new byte[9 + length];
        System.arraycopy(header, 0, frame, 0, 9);
        if (payload != null && length > 0) {
            System.arraycopy(payload, 0, frame, 9, length);
        }
        return frame;
    }

    // ========== 各帧类型构建方法 ==========

    /**
     * DATA 帧
     */
    public byte[] buildDataFrame(int streamId, byte[] data, boolean endStream) {
        byte flags = endStream ? FLAG_END_STREAM : 0;
        return buildFrame(FRAME_DATA, flags, streamId, data);
    }

    /**
     * HEADERS 帧
     */
    public byte[] buildHeadersFrame(int streamId, byte[] headerBlockFragment, boolean endStream, boolean endHeaders) {
        byte flags = 0;
        if (endStream) flags |= FLAG_END_STREAM;
        if (endHeaders) flags |= FLAG_END_HEADERS;
        return buildFrame(FRAME_HEADERS, flags, streamId, headerBlockFragment);
    }

    /**
     * HEADERS 帧（带优先级）
     */
    public byte[] buildHeadersFrameWithPriority(int streamId, byte[] headerBlockFragment,
                                                 boolean endStream, boolean endHeaders,
                                                 boolean exclusive, int parentStreamId, int weight) {
        byte[] priority = new byte[5];
        priority[0] = (byte) (exclusive ? (parentStreamId >> 24 | 0x80) : (parentStreamId >> 24 & 0x7F));
        priority[1] = (byte) ((parentStreamId >> 16) & 0xFF);
        priority[2] = (byte) ((parentStreamId >> 8) & 0xFF);
        priority[3] = (byte) (parentStreamId & 0xFF);
        priority[4] = (byte) (weight & 0xFF);

        byte[] payload = new byte[5 + (headerBlockFragment != null ? headerBlockFragment.length : 0)];
        System.arraycopy(priority, 0, payload, 0, 5);
        if (headerBlockFragment != null) {
            System.arraycopy(headerBlockFragment, 0, payload, 5, headerBlockFragment.length);
        }

        byte flags = FLAG_PRIORITY;
        if (endStream) flags |= FLAG_END_STREAM;
        if (endHeaders) flags |= FLAG_END_HEADERS;
        return buildFrame(FRAME_HEADERS, flags, streamId, payload);
    }

    /**
     * PRIORITY 帧
     */
    public byte[] buildPriorityFrame(int streamId, boolean exclusive, int parentStreamId, int weight) {
        byte[] payload = new byte[5];
        payload[0] = (byte) (exclusive ? (parentStreamId >> 24 | 0x80) : (parentStreamId >> 24 & 0x7F));
        payload[1] = (byte) ((parentStreamId >> 16) & 0xFF);
        payload[2] = (byte) ((parentStreamId >> 8) & 0xFF);
        payload[3] = (byte) (parentStreamId & 0xFF);
        payload[4] = (byte) (weight & 0xFF);
        return buildFrame(FRAME_PRIORITY, (byte) 0, streamId, payload);
    }

    /**
     * RST_STREAM 帧
     */
    public byte[] buildRstStreamFrame(int streamId, int errorCode) {
        byte[] payload = new byte[4];
        payload[0] = (byte) ((errorCode >> 24) & 0xFF);
        payload[1] = (byte) ((errorCode >> 16) & 0xFF);
        payload[2] = (byte) ((errorCode >> 8) & 0xFF);
        payload[3] = (byte) (errorCode & 0xFF);
        return buildFrame(FRAME_RST_STREAM, (byte) 0, streamId, payload);
    }

    /**
     * SETTINGS 帧
     */
    public byte[] buildSettingsFrame(byte[] settingsPayload, boolean ack) {
        byte flags = ack ? FLAG_ACK : 0;
        return buildFrame(FRAME_SETTINGS, flags, 0, settingsPayload);
    }

    /**
     * 构建单个 SETTINGS 参数 (6 字节)
     */
    public byte[] buildSettingEntry(short id, int value) {
        byte[] entry = new byte[6];
        entry[0] = (byte) ((id >> 8) & 0xFF);
        entry[1] = (byte) (id & 0xFF);
        entry[2] = (byte) ((value >> 24) & 0xFF);
        entry[3] = (byte) ((value >> 16) & 0xFF);
        entry[4] = (byte) ((value >> 8) & 0xFF);
        entry[5] = (byte) (value & 0xFF);
        return entry;
    }

    /**
     * PUSH_PROMISE 帧
     */
    public byte[] buildPushPromiseFrame(int streamId, int promisedStreamId, byte[] headerBlockFragment) {
        byte[] payload = new byte[4 + (headerBlockFragment != null ? headerBlockFragment.length : 0)];
        payload[0] = (byte) ((promisedStreamId >> 24) & 0xFF);
        payload[1] = (byte) ((promisedStreamId >> 16) & 0xFF);
        payload[2] = (byte) ((promisedStreamId >> 8) & 0xFF);
        payload[3] = (byte) (promisedStreamId & 0xFF);
        if (headerBlockFragment != null) {
            System.arraycopy(headerBlockFragment, 0, payload, 4, headerBlockFragment.length);
        }
        return buildFrame(FRAME_PUSH_PROMISE, FLAG_END_HEADERS, streamId, payload);
    }

    /**
     * PING 帧
     */
    public byte[] buildPingFrame(byte[] opaqueData, boolean ack) {
        if (opaqueData == null || opaqueData.length != 8) {
            opaqueData = new byte[8]; // 全零
        }
        byte flags = ack ? FLAG_ACK : 0;
        return buildFrame(FRAME_PING, flags, 0, opaqueData);
    }

    /**
     * GOAWAY 帧
     */
    public byte[] buildGoAwayFrame(int lastStreamId, int errorCode, String debugData) {
        byte[] debugBytes = debugData != null ? debugData.getBytes(StandardCharsets.UTF_8) : new byte[0];
        byte[] payload = new byte[8 + debugBytes.length];
        // Last Stream ID
        payload[0] = (byte) ((lastStreamId >> 24) & 0xFF);
        payload[1] = (byte) ((lastStreamId >> 16) & 0xFF);
        payload[2] = (byte) ((lastStreamId >> 8) & 0xFF);
        payload[3] = (byte) (lastStreamId & 0xFF);
        // Error Code
        payload[4] = (byte) ((errorCode >> 24) & 0xFF);
        payload[5] = (byte) ((errorCode >> 16) & 0xFF);
        payload[6] = (byte) ((errorCode >> 8) & 0xFF);
        payload[7] = (byte) (errorCode & 0xFF);
        // Debug Data
        if (debugBytes.length > 0) {
            System.arraycopy(debugBytes, 0, payload, 8, debugBytes.length);
        }
        return buildFrame(FRAME_GOAWAY, (byte) 0, 0, payload);
    }

    /**
     * WINDOW_UPDATE 帧
     */
    public byte[] buildWindowUpdateFrame(int streamId, int windowSizeIncrement) {
        byte[] payload = new byte[4];
        // 31 bits reserved
        payload[0] = (byte) ((windowSizeIncrement >> 24) & 0x7F);
        payload[1] = (byte) ((windowSizeIncrement >> 16) & 0xFF);
        payload[2] = (byte) ((windowSizeIncrement >> 8) & 0xFF);
        payload[3] = (byte) (windowSizeIncrement & 0xFF);
        return buildFrame(FRAME_WINDOW_UPDATE, (byte) 0, streamId, payload);
    }

    /**
     * CONTINUATION 帧
     */
    public byte[] buildContinuationFrame(int streamId, byte[] headerBlockFragment, boolean endHeaders) {
        byte flags = endHeaders ? FLAG_END_HEADERS : 0;
        return buildFrame(FRAME_CONTINUATION, flags, streamId, headerBlockFragment);
    }

    // ========== 帧解析 ==========

    /**
     * 解析帧头
     * 返回 [length, type, flags, streamId]
     */
    public int[] parseFrameHeader(byte[] frame) {
        if (frame == null || frame.length < 9) return null;
        int length = ((frame[0] & 0xFF) << 16) | ((frame[1] & 0xFF) << 8) | (frame[2] & 0xFF);
        byte type = frame[3];
        byte flags = frame[4];
        int streamId = (frame[5] & 0x7F) << 24 | (frame[6] & 0xFF) << 16
                | (frame[7] & 0xFF) << 8 | (frame[8] & 0xFF);
        return new int[]{length, type & 0xFF, flags & 0xFF, streamId};
    }

    /**
     * 获取帧类型名称
     */
    public String getFrameTypeName(byte type) {
        switch (type) {
            case FRAME_DATA: return "DATA";
            case FRAME_HEADERS: return "HEADERS";
            case FRAME_PRIORITY: return "PRIORITY";
            case FRAME_RST_STREAM: return "RST_STREAM";
            case FRAME_SETTINGS: return "SETTINGS";
            case FRAME_PUSH_PROMISE: return "PUSH_PROMISE";
            case FRAME_PING: return "PING";
            case FRAME_GOAWAY: return "GOAWAY";
            case FRAME_WINDOW_UPDATE: return "WINDOW_UPDATE";
            case FRAME_CONTINUATION: return "CONTINUATION";
            default: return "UNKNOWN";
        }
    }

    /**
     * 获取错误码名称
     */
    public static String getErrorCodeName(int errorCode) {
        switch (errorCode) {
            case 0x00: return "NO_ERROR";
            case 0x01: return "PROTOCOL_ERROR";
            case 0x02: return "INTERNAL_ERROR";
            case 0x03: return "FLOW_CONTROL_ERROR";
            case 0x04: return "SETTINGS_TIMEOUT";
            case 0x05: return "STREAM_CLOSED";
            case 0x06: return "FRAME_SIZE_ERROR";
            case 0x07: return "REFUSED_STREAM";
            case 0x08: return "CANCEL";
            case 0x09: return "COMPRESSION_ERROR";
            case 0x0A: return "CONNECT_ERROR";
            case 0x0B: return "ENHANCE_YOUR_CALM";
            case 0x0C: return "INADEQUATE_SECURITY";
            case 0x0D: return "HTTP_1_1_REQUIRED";
            default: return "UNKNOWN_ERROR";
        }
    }
}
