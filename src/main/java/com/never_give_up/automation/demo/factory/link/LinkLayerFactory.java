package com.never_give_up.automation.demo.factory.link;

import java.util.Arrays;

/**
 * 链路层工厂
 * 负责处理 Ethernet II 帧、LLC、FCS 等链路层协议
 */
public class LinkLayerFactory {

    private LlcHeader currentLlcHeader;
    private byte[] currentFcs;
    private byte[] ethernetFrame;

    // Ethernet 头部长度: DST MAC(6) + SRC MAC(6) + EtherType(2) = 14
    private static final int ETHERNET_HEADER_LEN = 14;
    // LLC 头部长度: DSAP(1) + SSAP(1) + Control(1) = 3
    private static final int LLC_HEADER_LEN = 3;
    // FCS 长度: 4 字节 (CRC32)
    private static final int FCS_LEN = 4;

    /**
     * 设置 LLC 头部 (IEEE 802.2)
     * 用于封装 IP 数据报或其他网络层协议
     */
    public void setLlcHeader() {
        this.currentLlcHeader = new LlcHeader();
        System.out.println("【🔗 LLC】: 添加 LLC 头部 - DSAP=0x06(IP), SSAP=0x06(IP), Ctrl=0x03(UI帧)");
    }

    /**
     * 设置自定义 LLC 头部
     * @param dsap 目标服务访问点
     * @param ssap 源服务访问点
     * @param control 控制字段
     */
    public void setLlcHeader(byte dsap, byte ssap, byte control) {
        this.currentLlcHeader = new LlcHeader(dsap, ssap, control);
        System.out.println(String.format("【🔗 LLC】: 添加自定义 LLC 头部 - %s", currentLlcHeader));
    }

    /**
     * 获取当前 LLC 头部
     */
    public LlcHeader getLlcHeader() {
        return currentLlcHeader;
    }

    /**
     * 计算 FCS (Frame Check Sequence) - CRC32
     * @param data 待计算 CRC 的数据帧（不含 FCS 本身）
     * @return 4 字节的 CRC32 校验值
     */
    public byte[] calculateFcs(byte[] data) {
        // 使用标准的 CRC32 算法
        java.util.zip.CRC32 crc32 = new java.util.zip.CRC32();
        crc32.update(data);
        long crcValue = crc32.getValue();

        // 转换为 4 字节大端序
        currentFcs = new byte[]{
                (byte) ((crcValue >> 24) & 0xFF),
                (byte) ((crcValue >> 16) & 0xFF),
                (byte) ((crcValue >> 8) & 0xFF),
                (byte) (crcValue & 0xFF)
        };

        return currentFcs;
    }

    /**
     * 验证 FCS 是否正确
     * @param data 包含 FCS 的完整数据帧
     * @return true 表示校验通过
     */
    public boolean verifyFcs(byte[] data) {
        if (data.length < FCS_LEN) return false;

        // 分离数据和 FCS
        byte[] frameData = Arrays.copyOf(data, data.length - FCS_LEN);
        byte[] receivedFcs = Arrays.copyOfRange(data, data.length - FCS_LEN, data.length);

        // 重新计算 CRC
        byte[] calculatedFcs = calculateFcs(frameData);

        // 比较
        return Arrays.equals(calculatedFcs, receivedFcs);
    }

    /**
     * 获取当前 FCS
     */
    public byte[] getCurrentFcs() {
        return currentFcs;
    }

    /**
     * 构建完整的 Ethernet + LLC 帧
     * @param dstMac 目标 MAC 地址
     * @param srcMac 源 MAC 地址
     * @param etherType EtherType 字段（如果使用 LLC，EtherType 应为帧长度）
     * @param networkData 网络层数据（如 IP 包）
     * @param useLlc 是否使用 LLC 封装
     * @return 完整的以太网帧
     */
    public byte[] buildEthernetFrame(String dstMac, String srcMac, int etherType,
                                     byte[] networkData, boolean useLlc) {
        // 解析 MAC 地址
        byte[] dstMacBytes = parseMacAddress(dstMac);
        byte[] srcMacBytes = parseMacAddress(srcMac);

        // 计算总长度
        int totalLen = ETHERNET_HEADER_LEN;
        byte[] llcData = null;

        if (useLlc && currentLlcHeader != null) {
            // 使用 LLC 封装：EtherType 字段变为帧长度（IEEE 802.3）
            llcData = currentLlcHeader.serialize();
            totalLen += LLC_HEADER_LEN;
        }

        totalLen += networkData.length + FCS_LEN;

        // 构建以太网帧
        byte[] frame = new byte[totalLen];
        int offset = 0;

        // 目标 MAC (6 字节)
        System.arraycopy(dstMacBytes, 0, frame, offset, 6);
        offset += 6;

        // 源 MAC (6 字节)
        System.arraycopy(srcMacBytes, 0, frame, offset, 6);
        offset += 6;

        // EtherType / 帧长度
        if (useLlc && currentLlcHeader != null) {
            // IEEE 802.3: 帧长度字段 (≤ 1500)
            int frameLen = LLC_HEADER_LEN + networkData.length;
            frame[offset] = (byte) ((frameLen >> 8) & 0xFF);
            frame[offset + 1] = (byte) (frameLen & 0xFF);
        } else {
            // Ethernet II: EtherType (≥ 1536)
            frame[offset] = (byte) ((etherType >> 8) & 0xFF);
            frame[offset + 1] = (byte) (etherType & 0xFF);
        }
        offset += 2;

        // LLC 头部（如果使用）
        if (useLlc && llcData != null) {
            System.arraycopy(llcData, 0, frame, offset, LLC_HEADER_LEN);
            offset += LLC_HEADER_LEN;
        }

        // 网络层数据
        System.arraycopy(networkData, 0, frame, offset, networkData.length);
        offset += networkData.length;

        // FCS (CRC32)
        byte[] fcs = calculateFcs(Arrays.copyOf(frame, offset));
        System.arraycopy(fcs, 0, frame, offset, FCS_LEN);

        this.ethernetFrame = frame;
        return frame;
    }

    /**
     * 移除 Ethernet 头部，提取 LLC + 网络层数据
     */
    public byte[] removeEthernetHeader(byte[] ethernetFrame) {
        if (ethernetFrame.length <= ETHERNET_HEADER_LEN + FCS_LEN) {
            return null;
        }

        // 跳过 Ethernet 头部，提取到 FCS 之前
        int dataStart = ETHERNET_HEADER_LEN;
        int dataEnd = ethernetFrame.length - FCS_LEN;
        byte[] remaining = Arrays.copyOfRange(ethernetFrame, dataStart, dataEnd);

        return remaining;
    }

    /**
     * 解析 MAC 地址字符串为字节数组
     * @param mac MAC 地址格式: "00:1A:2B:3C:4D:5E"
     */
    private byte[] parseMacAddress(String mac) {
        if (mac == null || mac.isEmpty()) {
            return new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        }

        String[] parts = mac.split(":");
        if (parts.length != 6) {
            return new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        }

        byte[] macBytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            macBytes[i] = (byte) Integer.parseInt(parts[i], 16);
        }
        return macBytes;
    }

    /**
     * 格式化 MAC 地址字节数组为字符串
     */
    public static String formatMacAddress(byte[] mac) {
        if (mac == null || mac.length != 6) return "??:??:??:??:??:??";
        return String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                mac[0] & 0xFF, mac[1] & 0xFF, mac[2] & 0xFF,
                mac[3] & 0xFF, mac[4] & 0xFF, mac[5] & 0xFF);
    }

    /**
     * 重置工厂状态
     */
    public void reset() {
        currentLlcHeader = null;
        currentFcs = null;
        ethernetFrame = null;
    }
}