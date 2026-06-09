package com.never_give_up.automation.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import com.never_give_up.automation.demo.core.ProtocolConst;

@EqualsAndHashCode(callSuper = true)
@Data
public class IpPacket extends BasePacket {
    private int version = 4;
    private int headerLength = 5;
    private int dscp;
    private int ecn;
    private int totalLength;
    private int identification;
    private int flags;
    private int fragmentOffset;
    private int ttl = ProtocolConst.DEFAULT_TTL;
    private int protocol;
    private int checksum;
    private String sourceIp;
    private String destinationIp;

    @Override
    public byte[] serialize() {
        byte[] header = new byte[ProtocolConst.IP_HEADER_SIZE];
        header[0] = (byte) ((version << 4) | headerLength);
        header[8] = (byte) ttl;
        header[9] = (byte) protocol;
        // TODO: 实现完整的 IP 首部序列化
        return header;
    }

    @Override
    public void deserialize(byte[] data) {
        version = (data[0] >> 4) & 0xF;
        ttl = data[8] & 0xFF;
        protocol = data[9] & 0xFF;
    }
}
