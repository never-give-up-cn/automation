package com.never_give_up.automation.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UdpPacket extends BasePacket {
    private int sourcePort;
    private int destinationPort;
    private int length;
    private int checksum;

    @Override
    public byte[] serialize() {
        byte[] header = new byte[8];

        header[0] = (byte) (sourcePort >> 8);
        header[1] = (byte) sourcePort;
        header[2] = (byte) (destinationPort >> 8);
        header[3] = (byte) destinationPort;
        header[4] = (byte) (length >> 8);
        header[5] = (byte) length;
        header[6] = (byte) (checksum >> 8);
        header[7] = (byte) checksum;

        return header;
    }

    @Override
    public void deserialize(byte[] data) {
        if (data.length < 8) return;

        sourcePort = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);
        destinationPort = ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
        length = ((data[4] & 0xFF) << 8) | (data[5] & 0xFF);
        checksum = ((data[6] & 0xFF) << 8) | (data[7] & 0xFF);
    }
}
