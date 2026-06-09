package com.never_give_up.automation.demo.animation;

import com.never_give_up.automation.demo.model.BasePacket;
import lombok.Data;

@Data
public class DataCart {
    // 动画属性
    public double x, y;
    public double speed = 8.0;
    public String cartType;
    public int sequenceNumber;
    public int ttl = 64;
    public int stage = -1;
    public boolean isReturnTrip = false;
    public boolean isArrived = false;
    public boolean isDropped = false;
    public boolean isNatted = false;
    public boolean isRetransmission = false;

    // 持有协议报文（与动画解耦）
    private BasePacket packet;

    // 兼容旧代码的字段（逐步迁移）
    public String httpBody;
    public byte[] encryptedData;
    public byte[] serverCertificate;
    public int ackNumber;
    public int advertisedWindow;
    public String domain;
    public String resolvedIp;
    public String natPublicIp;
    public int natPublicPort;
    public String currentLayerStatus = "";

    public DataCart(double x, double y, String type, int seq) {
        this.x = x;
        this.y = y;
        this.cartType = type;
        this.sequenceNumber = seq;
    }
}
