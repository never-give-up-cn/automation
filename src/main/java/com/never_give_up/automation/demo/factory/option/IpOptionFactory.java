package com.never_give_up.automation.demo.factory.option;

import java.util.ArrayList;
import java.util.List;

/**
 * IPv4 可选项头部工厂
 * 作用：生成标准 IPv4 可选字段，解决你当前 IP 头固定长度、无选项的问题
 * 支持：安全、路由记录、时间戳、松散源路由、严格源路由
 */
public class IpOptionFactory {

    // ======================== 选项类型常量（RFC 791）=========================
    public static final byte OPTION_END        = (byte) 0x00; // 选项结束
    public static final byte OPTION_NOOP      = (byte) 0x01; // 无操作
    public static final byte OPTION_SECURITY  = (byte) 0x02; // 安全选项
    public static final byte OPTION_RECORD_ROUTE = (byte) 0x07; // 记录路由
    public static final byte OPTION_TIMESTAMP = (byte) 0x08; // 时间戳
    public static final byte OPTION_LSR       = (byte) 0x09; // 松散源路由
    public static final byte OPTION_SSR       = (byte) 0x0A; // 严格源路由

    // ======================== 工厂方法：生成各类选项 ========================

    /**
     * 生成【安全选项】
     * 用途：标注数据包安全级别、隔离、权限
     */
    public byte[] createSecurityOption(int security, int compartment, int handlingRestrictions) {
        byte[] option = new byte[11];
        option[0] = OPTION_SECURITY;    // 类型
        option[1] = 11;                 // 长度
        option[2] = (byte) (security >> 8);
        option[3] = (byte) security;
        option[4] = (byte) (compartment >> 8);
        option[5] = (byte) compartment;
        option[6] = (byte) (handlingRestrictions >> 8);
        option[7] = (byte) handlingRestrictions;
        option[8] = 0;
        option[9] = 0;
        option[10] = 0;
        return option;
    }

    /**
     * 生成【记录路由选项】
     * 用途：让途经路由器自动记录自己的出口 IP
     */
    public byte[] createRecordRouteOption(List<Integer> initialIps) {
        int maxSize = 9; // 标准最多存 9 个 IP
        List<Integer> ips = new ArrayList<>(initialIps);
        while (ips.size() < maxSize) ips.add(0);

        byte[] option = new byte[3 + maxSize * 4];
        option[0] = OPTION_RECORD_ROUTE;
        option[1] = (byte) option.length;
        option[2] = 4; // 指针：从第4字节开始写入IP

        int idx = 3;
        for (int ip : ips) {
            option[idx++] = (byte) (ip >> 24);
            option[idx++] = (byte) (ip >> 16);
            option[idx++] = (byte) (ip >> 8);
            option[idx++] = (byte) ip;
        }
        return option;
    }

    /**
     * 生成【时间戳选项】
     * 用途：记录每一跳路由器的时间，用于网络延迟测量
     */
    public byte[] createTimestampOption(int flag, int shift) {
        byte[] option = new byte[12];
        option[0] = OPTION_TIMESTAMP;
        option[1] = 12;
        option[2] = 4;
        option[3] = (byte) ((flag << 4) | (shift & 0x0F));

        // 预留 8 字节存储时间戳
        for (int i = 4; i < 12; i++) option[i] = 0;
        return option;
    }

    /**
     * 生成【松散源路由 LSR】
     * 用途：指定数据包必须经过哪些路由，但可走其他中间节点
     */
    public byte[] createLooseSourceRouteOption(List<Integer> pathIps) {
        return buildSourceRouteOption(OPTION_LSR, pathIps);
    }

    /**
     * 生成【严格源路由 SSR】
     * 用途：数据包必须严格按照指定IP路径传输，不允许绕路
     */
    public byte[] createStrictSourceRouteOption(List<Integer> pathIps) {
        return buildSourceRouteOption(OPTION_SSR, pathIps);
    }

    // ======================== 工具方法 ========================

    /**
     * 统一构建源路由选项（LSR / SSR）
     */
    private byte[] buildSourceRouteOption(byte type, List<Integer> pathIps) {
        byte[] option = new byte[3 + pathIps.size() * 4];
        option[0] = type;
        option[1] = (byte) option.length;
        option[2] = 4;

        int idx = 3;
        for (int ip : pathIps) {
            option[idx++] = (byte) (ip >> 24);
            option[idx++] = (byte) (ip >> 16);
            option[idx++] = (byte) (ip >> 8);
            option[idx++] = (byte) ip;
        }
        return option;
    }

    /**
     * 填充结尾：END + NOOP（保证4字节对齐，IPv4 强制要求）
     */
    public byte[] padTo4Bytes(byte[] options) {
        int len = options.length;
        int pad = (4 - (len % 4)) % 4;

        byte[] result = new byte[len + pad];
        System.arraycopy(options, 0, result, 0, len);

        if (pad > 0) result[len] = OPTION_END;
        for (int i = len + 1; i < result.length; i++) {
            result[i] = OPTION_NOOP;
        }
        return result;
    }

    /**
     * 组合多个选项 → 最终可直接拼入 IP 头的选项区
     */
    public byte[] combineOptions(byte[]... options) {
        int totalLen = 0;
        for (byte[] o : options) totalLen += o.length;

        byte[] combined = new byte[totalLen];
        int ptr = 0;
        for (byte[] o : options) {
            System.arraycopy(o, 0, combined, ptr, o.length);
            ptr += o.length;
        }
        return padTo4Bytes(combined);
    }
}
