package com.never_give_up.automation.demo.factory.link;

/**
 * 链路层工厂 - 完整实现
 * 负责 Ethernet II 帧封装/解封装、FCS 计算、LLC 处理
 */
public class LinkLayerFactory {
    private LlcHeader currentLlcHeader;
    private byte[] currentFcs;
    private String currentSrcMac;
    private String currentDstMac;
    private int currentEtherType;
    private boolean fcsValid;

    // Ethernet 常量
    private static final int ETHERNET_HEADER_LEN = 14;
    private static final int FCS_LEN = 4;

    // 默认 MAC 地址
    private static final String DEFAULT_MAC = "00:00:00:00:00:00";

    // EtherType 常量
    public static final int ETHERTYPE_IPV4 = 0x0800;
    public static final int ETHERTYPE_ARP = 0x0806;
    public static final int ETHERTYPE_IPV6 = 0x86DD;
    public static final int ETHERTYPE_VLAN = 0x8100;

    public void setLlcHeader() {
        this.currentLlcHeader = new LlcHeader();
    }

    public LlcHeader getLlcHeader() {
        return currentLlcHeader;
    }

    public byte[] calculateFcs(byte[] data) {
        java.util.zip.CRC32 crc32 = new java.util.zip.CRC32();
        crc32.update(data);
        long crcValue = crc32.getValue();
        currentFcs = new byte[]{
                (byte) ((crcValue >> 24) & 0xFF),
                (byte) ((crcValue >> 16) & 0xFF),
                (byte) ((crcValue >> 8) & 0xFF),
                (byte) (crcValue & 0xFF)
        };
        return currentFcs;
    }

    public byte[] buildEthernetFrame(String dstMac, String srcMac, int etherType,
                                     byte[] networkData, boolean useLlc) {
        // 添加 null 检查，使用默认值
        String safeDstMac = dstMac != null ? dstMac : DEFAULT_MAC;
        String safeSrcMac = srcMac != null ? srcMac : DEFAULT_MAC;

        byte[] dstMacBytes = parseMac(safeDstMac);
        byte[] srcMacBytes = parseMac(safeSrcMac);

        int totalLen = ETHERNET_HEADER_LEN + (networkData != null ? networkData.length : 0);
        byte[] frame = new byte[totalLen + FCS_LEN];
        int offset = 0;

        // DST MAC
        System.arraycopy(dstMacBytes, 0, frame, offset, 6);
        offset += 6;
        // SRC MAC
        System.arraycopy(srcMacBytes, 0, frame, offset, 6);
        offset += 6;
        // EtherType
        frame[offset] = (byte) ((etherType >> 8) & 0xFF);
        frame[offset + 1] = (byte) (etherType & 0xFF);
        offset += 2;
        // Payload
        if (networkData != null && networkData.length > 0) {
            System.arraycopy(networkData, 0, frame, offset, networkData.length);
        }

        // FCS
        byte[] fcs = calculateFcs(java.util.Arrays.copyOf(frame, totalLen));
        System.arraycopy(fcs, 0, frame, totalLen, FCS_LEN);

        return frame;
    }

    public byte[] extractNetworkData(byte[] ethernetFrame) {
        if (ethernetFrame == null || !verifyFcs(ethernetFrame)) return null;

        int etherType = ((ethernetFrame[12] & 0xFF) << 8) | (ethernetFrame[13] & 0xFF);
        currentEtherType = etherType;
        currentSrcMac = formatMac(java.util.Arrays.copyOfRange(ethernetFrame, 6, 12));
        currentDstMac = formatMac(java.util.Arrays.copyOfRange(ethernetFrame, 0, 6));

        return java.util.Arrays.copyOfRange(ethernetFrame, 14, ethernetFrame.length - FCS_LEN);
    }

    public boolean verifyFcs(byte[] frame) {
        if (frame == null || frame.length < FCS_LEN) return false;
        byte[] data = java.util.Arrays.copyOf(frame, frame.length - FCS_LEN);
        byte[] receivedFcs = java.util.Arrays.copyOfRange(frame, frame.length - FCS_LEN, frame.length);
        byte[] calculatedFcs = calculateFcs(data);
        fcsValid = java.util.Arrays.equals(calculatedFcs, receivedFcs);
        return fcsValid;
    }

    /**
     * 解析 MAC 地址，添加 null 和格式检查
     */
    private byte[] parseMac(String mac) {
        if (mac == null || mac.isEmpty()) {
            return new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        }

        String[] parts = mac.split(":");
        if (parts.length != 6) {
            // 如果不是标准格式，尝试其他格式或返回默认值
            return new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        }

        byte[] result = new byte[6];
        try {
            for (int i = 0; i < 6; i++) {
                result[i] = (byte) Integer.parseInt(parts[i], 16);
            }
        } catch (NumberFormatException e) {
            // 解析失败，返回默认值
            return new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        }
        return result;
    }

    private String formatMac(byte[] mac) {
        if (mac == null || mac.length != 6) return "??:??:??:??:??:??";
        return String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                mac[0] & 0xFF, mac[1] & 0xFF, mac[2] & 0xFF,
                mac[3] & 0xFF, mac[4] & 0xFF, mac[5] & 0xFF);
    }

    public String getRemoveEthernetHeaderInfo() {
        return String.format("【链路层拆封】MAC: %s → %s, Type: 0x%04X, FCS: %s",
                currentSrcMac != null ? currentSrcMac : "??:??:??:??:??:??",
                currentDstMac != null ? currentDstMac : "??:??:??:??:??:??",
                currentEtherType, fcsValid ? "✅" : "❌");
    }

    public byte[] getCurrentFcs() { return currentFcs; }
    public void resetLinkLayer() {
        fcsValid = false;
        currentSrcMac = null;
        currentDstMac = null;
        currentEtherType = 0;
    }
}