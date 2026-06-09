package com.never_give_up.automation.demo.factory.link;

/**
 * LLC (Logical Link Control) 头部
 * 用于 IEEE 802.2 逻辑链路控制协议
 */
public class LlcHeader {
    // DSAP (Destination Service Access Point) - 目标服务访问点
    private byte dsap;
    // SSAP (Source Service Access Point) - 源服务访问点
    private byte ssap;
    // Control Field - 控制字段（用于无编号信息帧 UI）
    private byte control;

    // 常用 SAP 值
    public static final byte SAP_NULL = 0x00;      // Null SAP
    public static final byte SAP_LLC_MGMT = 0x02;   // LLC Management
    public static final byte SAP_IP = 0x06;         // IP 协议
    public static final byte SAP_SNA = 0x04;        // SNA Path Control
    public static final byte SAP_NETBIOS = 0xF0;    // NetBIOS
    public static final byte SAP_IPX = 0xE0;        // IPX/SPX

    // 控制字段 - UI 帧 (Unnumbered Information)
    public static final byte UI_FRAME = 0x03;       // 无编号信息帧

    public LlcHeader() {
        this.dsap = SAP_IP;
        this.ssap = SAP_IP;
        this.control = UI_FRAME;
    }

    public LlcHeader(byte dsap, byte ssap, byte control) {
        this.dsap = dsap;
        this.ssap = ssap;
        this.control = control;
    }

    // Getters and Setters
    public byte getDsap() { return dsap; }
    public void setDsap(byte dsap) { this.dsap = dsap; }

    public byte getSsap() { return ssap; }
    public void setSsap(byte ssap) { this.ssap = ssap; }

    public byte getControl() { return control; }
    public void setControl(byte control) { this.control = control; }

    /**
     * 序列化 LLC 头为字节数组
     * @return 3 字节的 LLC 头部
     */
    public byte[] serialize() {
        return new byte[]{dsap, ssap, control};
    }

    /**
     * 从字节数组反序列化 LLC 头
     */
    public static LlcHeader deserialize(byte[] data, int offset) {
        LlcHeader header = new LlcHeader();
        header.dsap = data[offset];
        header.ssap = data[offset + 1];
        header.control = data[offset + 2];
        return header;
    }

    @Override
    public String toString() {
        return String.format("LLC[DSAP=0x%02X, SSAP=0x%02X, Ctrl=0x%02X]",
                dsap & 0xFF, ssap & 0xFF, control & 0xFF);
    }
}