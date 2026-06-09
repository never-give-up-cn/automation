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
    private boolean fcsValid = false;

    // Ethernet 头部长度: DST MAC(6) + SRC MAC(6) + EtherType(2) = 14
    private static final int ETHERNET_HEADER_LEN = 14;
    // LLC 头部长度: DSAP(1) + SSAP(1) + Control(1) = 3
    private static final int LLC_HEADER_LEN = 3;
    // FCS 长度: 4 字节 (CRC32)
    private static final int FCS_LEN = 4;

    // 存储当前正在处理的帧数据
    private byte[] currentFrameData;
    private String currentSrcMac;
    private String currentDstMac;
    private int currentEtherType;
    private LlcHeader extractedLlcHeader;

    /**
     * 设置 LLC 头部 (IEEE 802.2)
     */
    public void setLlcHeader() {
        this.currentLlcHeader = new LlcHeader();
    }

    /**
     * 设置自定义 LLC 头部
     */
    public void setLlcHeader(byte dsap, byte ssap, byte control) {
        this.currentLlcHeader = new LlcHeader(dsap, ssap, control);
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
        if (data == null || data.length == 0) {
            return new byte[]{0x00, 0x00, 0x00, 0x00};
        }

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
     * 为当前帧计算并附加 FCS
     * @param frameWithoutFcs 不含 FCS 的完整帧
     * @return 包含 FCS 的完整帧
     */
    public byte[] attachFcs(byte[] frameWithoutFcs) {
        if (frameWithoutFcs == null) return null;

        byte[] fcs = calculateFcs(frameWithoutFcs);
        byte[] completeFrame = new byte[frameWithoutFcs.length + FCS_LEN];
        System.arraycopy(frameWithoutFcs, 0, completeFrame, 0, frameWithoutFcs.length);
        System.arraycopy(fcs, 0, completeFrame, frameWithoutFcs.length, FCS_LEN);

        this.currentFrameData = completeFrame;
        this.fcsValid = true;

        return completeFrame;
    }

    /**
     * 验证 FCS 是否正确
     * @param data 包含 FCS 的完整数据帧
     * @return true 表示校验通过
     */
    public boolean verifyFcs(byte[] data) {
        if (data == null || data.length < FCS_LEN) {
            this.fcsValid = false;
            return false;
        }

        // 分离数据和 FCS
        byte[] frameData = Arrays.copyOf(data, data.length - FCS_LEN);
        byte[] receivedFcs = Arrays.copyOfRange(data, data.length - FCS_LEN, data.length);

        // 重新计算 CRC
        byte[] calculatedFcs = calculateFcs(frameData);

        // 比较
        this.fcsValid = Arrays.equals(calculatedFcs, receivedFcs);
        return this.fcsValid;
    }

    /**
     * 获取当前 FCS
     */
    public byte[] getCurrentFcs() {
        return currentFcs;
    }

    /**
     * 检查 FCS 是否有效
     */
    public boolean isFcsValid() {
        return fcsValid;
    }

    /**
     * 构建完整的 Ethernet + LLC 帧（发送端）
     * @param dstMac 目标 MAC 地址
     * @param srcMac 源 MAC 地址
     * @param etherType EtherType 字段
     * @param networkData 网络层数据（如 IP 包）
     * @param useLlc 是否使用 LLC 封装
     * @return 完整的以太网帧（含 FCS）
     */
    public byte[] buildEthernetFrame(String dstMac, String srcMac, int etherType,
                                     byte[] networkData, boolean useLlc) {
        // 解析 MAC 地址
        byte[] dstMacBytes = parseMacAddress(dstMac);
        byte[] srcMacBytes = parseMacAddress(srcMac);

        // 计算总长度（不含 FCS，稍后附加）
        int totalLen = ETHERNET_HEADER_LEN;
        byte[] llcData = null;

        if (useLlc && currentLlcHeader != null) {
            llcData = currentLlcHeader.serialize();
            totalLen += LLC_HEADER_LEN;
        }

        totalLen += networkData.length;

        // 构建以太网帧（不含 FCS）
        byte[] frameWithoutFcs = new byte[totalLen];
        int offset = 0;

        // 目标 MAC (6 字节)
        System.arraycopy(dstMacBytes, 0, frameWithoutFcs, offset, 6);
        offset += 6;

        // 源 MAC (6 字节)
        System.arraycopy(srcMacBytes, 0, frameWithoutFcs, offset, 6);
        offset += 6;

        // EtherType
        frameWithoutFcs[offset] = (byte) ((etherType >> 8) & 0xFF);
        frameWithoutFcs[offset + 1] = (byte) (etherType & 0xFF);
        offset += 2;

        // LLC 头部（如果使用）
        if (useLlc && llcData != null) {
            System.arraycopy(llcData, 0, frameWithoutFcs, offset, LLC_HEADER_LEN);
            offset += LLC_HEADER_LEN;
        }

        // 网络层数据
        System.arraycopy(networkData, 0, frameWithoutFcs, offset, networkData.length);

        // 存储当前帧信息
        this.currentSrcMac = srcMac;
        this.currentDstMac = dstMac;
        this.currentEtherType = etherType;

        // 附加 FCS
        byte[] completeFrame = attachFcs(frameWithoutFcs);
        this.ethernetFrame = completeFrame;

        return completeFrame;
    }

    /**
     * 移除 Ethernet 头部，提取 LLC + 网络层数据（接收端）
     * @param ethernetFrame 完整的以太网帧（含 FCS）
     * @return 包含 LLC 头和网络层数据的字节数组，如果校验失败返回 null
     */
    public byte[] extractNetworkData(byte[] ethernetFrame) {
        if (ethernetFrame == null || ethernetFrame.length < ETHERNET_HEADER_LEN + FCS_LEN) {
            return null;
        }

        // 首先验证 FCS
        if (!verifyFcs(ethernetFrame)) {
            return null; // FCS 校验失败，帧损坏
        }

        // 提取 Ethernet 头部信息
        byte[] dstMac = Arrays.copyOfRange(ethernetFrame, 0, 6);
        byte[] srcMac = Arrays.copyOfRange(ethernetFrame, 6, 12);
        int etherType = ((ethernetFrame[12] & 0xFF) << 8) | (ethernetFrame[13] & 0xFF);

        this.currentSrcMac = formatMacAddress(srcMac);
        this.currentDstMac = formatMacAddress(dstMac);
        this.currentEtherType = etherType;

        int dataStart = ETHERNET_HEADER_LEN;

        // 提取 LLC 头部（如果存在）
        if (ethernetFrame.length >= dataStart + LLC_HEADER_LEN) {
            this.extractedLlcHeader = LlcHeader.deserialize(ethernetFrame, dataStart);
            dataStart += LLC_HEADER_LEN;
        }

        // 提取网络层数据（到 FCS 之前）
        int dataEnd = ethernetFrame.length - FCS_LEN;
        byte[] networkData = Arrays.copyOfRange(ethernetFrame, dataStart, dataEnd);

        return networkData;
    }

    /**
     * 简化的链路层拆封信息（用于模拟器控制台）
     * @return 拆封信息描述
     */
    public String getRemoveEthernetHeaderInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append("【📥 链路层拆封】\n");
        sb.append("  ├─ 目标 MAC: ").append(currentDstMac != null ? currentDstMac : "??:??:??:??:??:??").append("\n");
        sb.append("  ├─ 源 MAC: ").append(currentSrcMac != null ? currentSrcMac : "??:??:??:??:??:??").append("\n");

        if (currentEtherType != 0) {
            sb.append("  ├─ EtherType: 0x").append(String.format("%04X", currentEtherType));
            if (currentEtherType == 0x0800) sb.append(" (IPv4)");
            else if (currentEtherType == 0x0806) sb.append(" (ARP)");
            else if (currentEtherType == 0x86DD) sb.append(" (IPv6)");
            sb.append("\n");
        }

        if (extractedLlcHeader != null) {
            sb.append("  ├─ ").append(extractedLlcHeader.toString()).append("\n");
        }

        sb.append("  └─ FCS 校验: ").append(fcsValid ? "✅ 通过" : "❌ 失败");

        if (!fcsValid) {
            sb.append(" (帧损坏，已丢弃)");
        }

        return sb.toString();
    }

    /**
     * 重置链路层状态（接收端处理后调用）
     */
    public void resetLinkLayer() {
        currentLlcHeader = null;
        currentFcs = null;
        extractedLlcHeader = null;
        fcsValid = false;
        currentSrcMac = null;
        currentDstMac = null;
        currentEtherType = 0;
        currentFrameData = null;
    }

    /**
     * 解析 MAC 地址字符串为字节数组
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
     * 获取当前帧的 FCS 校验状态描述
     */
    public String getFcsStatus() {
        if (currentFrameData == null) {
            return "未计算";
        }
        return fcsValid ? "校验通过" : "校验失败";
    }

    /**
     * 重置工厂状态
     */
    public void reset() {
        currentLlcHeader = null;
        currentFcs = null;
        ethernetFrame = null;
        currentFrameData = null;
        extractedLlcHeader = null;
        fcsValid = false;
        currentSrcMac = null;
        currentDstMac = null;
        currentEtherType = 0;
    }
}