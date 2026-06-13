package com.never_give_up.automation.demo.factory.application.http;

import java.util.HashMap;
import java.util.Map;

/**
 * HTTP/2 Settings 工厂 - SETTINGS 帧参数定义和管理
 * 基于 RFC 7540 Section 6.5.2
 */
public class Http2SettingsFactory {

    // ========== 标准 SETTINGS 标识符 ==========
    public static final short SETTINGS_HEADER_TABLE_SIZE = 0x01;
    public static final short SETTINGS_ENABLE_PUSH = 0x02;
    public static final short SETTINGS_MAX_CONCURRENT_STREAMS = 0x03;
    public static final short SETTINGS_INITIAL_WINDOW_SIZE = 0x04;
    public static final short SETTINGS_MAX_FRAME_SIZE = 0x05;
    public static final short SETTINGS_MAX_HEADER_LIST_SIZE = 0x06;

    /**
     * HTTP/2 设置参数集合
     */
    public static class Http2Settings {
        private int headerTableSize = 4096;
        private boolean enablePush = true;
        private int maxConcurrentStreams = 100;
        private int initialWindowSize = 65535;
        private int maxFrameSize = 16384;
        private int maxHeaderListSize = Integer.MAX_VALUE;

        // Getters and setters
        public int getHeaderTableSize() { return headerTableSize; }
        public void setHeaderTableSize(int size) { this.headerTableSize = size; }
        public boolean isEnablePush() { return enablePush; }
        public void setEnablePush(boolean enable) { this.enablePush = enable; }
        public int getMaxConcurrentStreams() { return maxConcurrentStreams; }
        public void setMaxConcurrentStreams(int max) { this.maxConcurrentStreams = max; }
        public int getInitialWindowSize() { return initialWindowSize; }
        public void setInitialWindowSize(int size) { this.initialWindowSize = size; }
        public int getMaxFrameSize() { return maxFrameSize; }
        public void setMaxFrameSize(int size) { this.maxFrameSize = size; }
        public int getMaxHeaderListSize() { return maxHeaderListSize; }
        public void setMaxHeaderListSize(int size) { this.maxHeaderListSize = size; }

        /**
         * 序列化为 SETTINGS 帧载荷
         */
        public byte[] serialize() {
            Http2FrameFactory frameFactory = new Http2FrameFactory();
            byte[][] entries = new byte[6][];
            int count = 0;

            if (headerTableSize != 4096) {
                entries[count++] = frameFactory.buildSettingEntry(SETTINGS_HEADER_TABLE_SIZE, headerTableSize);
            }
            if (!enablePush) {
                entries[count++] = frameFactory.buildSettingEntry(SETTINGS_ENABLE_PUSH, 0);
            }
            if (maxConcurrentStreams != 100) {
                entries[count++] = frameFactory.buildSettingEntry(SETTINGS_MAX_CONCURRENT_STREAMS, maxConcurrentStreams);
            }
            if (initialWindowSize != 65535) {
                entries[count++] = frameFactory.buildSettingEntry(SETTINGS_INITIAL_WINDOW_SIZE, initialWindowSize);
            }
            if (maxFrameSize != 16384) {
                entries[count++] = frameFactory.buildSettingEntry(SETTINGS_MAX_FRAME_SIZE, maxFrameSize);
            }
            if (maxHeaderListSize != Integer.MAX_VALUE) {
                entries[count++] = frameFactory.buildSettingEntry(SETTINGS_MAX_HEADER_LIST_SIZE, maxHeaderListSize);
            }

            int totalLen = 0;
            for (int i = 0; i < count; i++) {
                totalLen += entries[i].length;
            }
            byte[] result = new byte[totalLen];
            int offset = 0;
            for (int i = 0; i < count; i++) {
                System.arraycopy(entries[i], 0, result, offset, entries[i].length);
                offset += entries[i].length;
            }
            return result;
        }

        /**
         * 从 SETTINGS 帧载荷反序列化
         */
        public static Http2Settings deserialize(byte[] payload) {
            Http2Settings settings = new Http2Settings();
            if (payload == null || payload.length % 6 != 0) return settings;

            for (int i = 0; i < payload.length; i += 6) {
                short id = (short) (((payload[i] & 0xFF) << 8) | (payload[i + 1] & 0xFF));
                int value = (payload[i + 2] & 0xFF) << 24 | (payload[i + 3] & 0xFF) << 16
                        | (payload[i + 4] & 0xFF) << 8 | (payload[i + 5] & 0xFF);

                switch (id) {
                    case SETTINGS_HEADER_TABLE_SIZE:
                        settings.setHeaderTableSize(value);
                        break;
                    case SETTINGS_ENABLE_PUSH:
                        settings.setEnablePush(value != 0);
                        break;
                    case SETTINGS_MAX_CONCURRENT_STREAMS:
                        settings.setMaxConcurrentStreams(value);
                        break;
                    case SETTINGS_INITIAL_WINDOW_SIZE:
                        settings.setInitialWindowSize(value);
                        break;
                    case SETTINGS_MAX_FRAME_SIZE:
                        settings.setMaxFrameSize(value);
                        break;
                    case SETTINGS_MAX_HEADER_LIST_SIZE:
                        settings.setMaxHeaderListSize(value);
                        break;
                }
            }
            return settings;
        }
    }

    /**
     * 获取参数名称
     */
    public static String getSettingName(short id) {
        switch (id) {
            case SETTINGS_HEADER_TABLE_SIZE: return "HEADER_TABLE_SIZE";
            case SETTINGS_ENABLE_PUSH: return "ENABLE_PUSH";
            case SETTINGS_MAX_CONCURRENT_STREAMS: return "MAX_CONCURRENT_STREAMS";
            case SETTINGS_INITIAL_WINDOW_SIZE: return "INITIAL_WINDOW_SIZE";
            case SETTINGS_MAX_FRAME_SIZE: return "MAX_FRAME_SIZE";
            case SETTINGS_MAX_HEADER_LIST_SIZE: return "MAX_HEADER_LIST_SIZE";
            default: return "UNKNOWN_SETTING";
        }
    }
}
