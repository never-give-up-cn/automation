package com.never_give_up.automation.demo.factory.function;

/**
 * 校验和计算工厂
 * 用于 TCP/UDP/IP 头的校验和计算
 *
 * @author Network Simulator
 */
public class ChecksumFactory {

    /**
     * 计算 IP 头校验和
     * @param ipHeader IP 头部字节数组（包含 checksum 字段，该字段位置填 0）
     * @return 16位校验和
     */
    public int calculateIpChecksum(byte[] ipHeader) {
        int sum = 0;
        // IP 头长度通常是 20 字节（无选项）或更多
        for (int i = 0; i < ipHeader.length; i += 2) {
            // 跳过 checksum 字段本身（第 10-11 字节）
            if (i == 10) continue;
            int word;
            if (i + 1 < ipHeader.length) {
                word = ((ipHeader[i] & 0xFF) << 8) | (ipHeader[i + 1] & 0xFF);
            } else {
                word = (ipHeader[i] & 0xFF) << 8;
            }
            sum += word;
            // 处理进位
            while ((sum >> 16) != 0) {
                sum = (sum & 0xFFFF) + (sum >> 16);
            }
        }
        return ~sum & 0xFFFF;
    }

    /**
     * 计算 TCP 校验和（包含伪头部）
     * @param tcpSegment TCP 段数据
     * @param srcIp 源 IP 地址
     * @param dstIp 目标 IP 地址
     * @return 16位校验和
     */
    public int calculateTcpChecksum(byte[] tcpSegment, String srcIp, String dstIp) {
        // 构建伪头部
        byte[] pseudoHeader = buildPseudoHeader(srcIp, dstIp, 6, tcpSegment.length);
        // 合并伪头部和 TCP 段
        byte[] data = concat(pseudoHeader, tcpSegment);
        // 计算校验和
        return calculateChecksum(data);
    }

    /**
     * 计算 UDP 校验和（包含伪头部）
     * @param udpDatagram UDP 数据报
     * @param srcIp 源 IP 地址
     * @param dstIp 目标 IP 地址
     * @return 16位校验和
     */
    public int calculateUdpChecksum(byte[] udpDatagram, String srcIp, String dstIp) {
        byte[] pseudoHeader = buildPseudoHeader(srcIp, dstIp, 17, udpDatagram.length);
        byte[] data = concat(pseudoHeader, udpDatagram);
        int checksum = calculateChecksum(data);
        // UDP 允许校验和为 0 表示不校验
        return checksum == 0 ? 0xFFFF : checksum;
    }

    /**
     * 计算 ICMP 校验和
     * @param icmpPacket ICMP 包数据
     * @return 16位校验和
     */
    public int calculateIcmpChecksum(byte[] icmpPacket) {
        return calculateChecksum(icmpPacket);
    }

    /**
     * 构建 TCP/UDP 伪头部
     * @param srcIp 源 IP
     * @param dstIp 目标 IP
     * @param protocol 协议号 (6=TCP, 17=UDP)
     * @param length 传输层长度
     * @return 12字节伪头部
     */
    private byte[] buildPseudoHeader(String srcIp, String dstIp, int protocol, int length) {
        byte[] pseudo = new byte[12];
        byte[] srcBytes = ipToBytes(srcIp);
        byte[] dstBytes = ipToBytes(dstIp);

        System.arraycopy(srcBytes, 0, pseudo, 0, 4);
        System.arraycopy(dstBytes, 0, pseudo, 4, 4);
        pseudo[8] = 0;                          // 保留
        pseudo[9] = (byte) protocol;            // 协议
        pseudo[10] = (byte) ((length >> 8) & 0xFF); // 长度高位
        pseudo[11] = (byte) (length & 0xFF);        // 长度低位

        return pseudo;
    }

    /**
     * 通用校验和计算
     * @param data 数据字节数组
     * @return 16位校验和
     */
    private int calculateChecksum(byte[] data) {
        int sum = 0;
        int length = data.length;
        int i = 0;

        // 按 16 位字累加
        while (length > 1) {
            sum += ((data[i] & 0xFF) << 8) | (data[i + 1] & 0xFF);
            i += 2;
            length -= 2;
        }

        // 处理奇数字节
        if (length > 0) {
            sum += (data[i] & 0xFF) << 8;
        }

        // 处理进位
        while ((sum >> 16) != 0) {
            sum = (sum & 0xFFFF) + (sum >> 16);
        }

        // 取反
        return ~sum & 0xFFFF;
    }

    /**
     * 验证 IP 校验和
     * @param ipHeader 包含校验和的 IP 头部
     * @return true 表示校验和正确
     */
    public boolean verifyIpChecksum(byte[] ipHeader) {
        // 保存原校验和
        int originalChecksum = ((ipHeader[10] & 0xFF) << 8) | (ipHeader[11] & 0xFF);
        // 将校验和字段置零
        ipHeader[10] = 0;
        ipHeader[11] = 0;
        // 重新计算
        int calculated = calculateIpChecksum(ipHeader);
        // 恢复校验和
        ipHeader[10] = (byte) ((originalChecksum >> 8) & 0xFF);
        ipHeader[11] = (byte) (originalChecksum & 0xFF);

        return originalChecksum == calculated;
    }

    /**
     * 验证 TCP 校验和
     * @param tcpSegment TCP 段（包含校验和）
     * @param srcIp 源 IP
     * @param dstIp 目标 IP
     * @return true 表示校验和正确
     */
    public boolean verifyTcpChecksum(byte[] tcpSegment, String srcIp, String dstIp) {
        if (tcpSegment.length < 20) return false;

        // 保存原校验和
        int originalChecksum = ((tcpSegment[16] & 0xFF) << 8) | (tcpSegment[17] & 0xFF);
        // 将校验和字段置零
        tcpSegment[16] = 0;
        tcpSegment[17] = 0;
        // 重新计算
        int calculated = calculateTcpChecksum(tcpSegment, srcIp, dstIp);
        // 恢复校验和
        tcpSegment[16] = (byte) ((originalChecksum >> 8) & 0xFF);
        tcpSegment[17] = (byte) (originalChecksum & 0xFF);

        return originalChecksum == calculated;
    }

    /**
     * IP 地址字符串转字节数组
     */
    private byte[] ipToBytes(String ip) {
        if (ip == null || ip.isEmpty()) {
            return new byte[]{0, 0, 0, 0};
        }
        String[] parts = ip.split("\\.");
        byte[] result = new byte[4];
        for (int i = 0; i < 4 && i < parts.length; i++) {
            result[i] = (byte) Integer.parseInt(parts[i]);
        }
        return result;
    }

    /**
     * 合并两个字节数组
     */
    private byte[] concat(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    /**
     * 快速计算（直接传入数据）
     */
    public static short computeChecksum(byte[] data) {
        int sum = 0;
        int i = 0;
        while (i < data.length - 1) {
            sum += ((data[i] & 0xFF) << 8) | (data[i + 1] & 0xFF);
            i += 2;
        }
        if (i < data.length) {
            sum += (data[i] & 0xFF) << 8;
        }
        while ((sum >> 16) != 0) {
            sum = (sum & 0xFFFF) + (sum >> 16);
        }
        return (short) (~sum & 0xFFFF);
    }
}