package com.never_give_up.automation.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class IcmpPacket extends BasePacket {
    private int type;
    private int code;
    private int checksum;
    private int identifier;
    private int sequenceNumber;
    private long timestamp;
    private byte[] data;

    public boolean isEchoRequest() { return type == 8; }
    public boolean isEchoReply() { return type == 0; }
    public boolean isTimeExceeded() { return type == 11; }
    public boolean isDestinationUnreachable() { return type == 3; }

    @Override
    public byte[] serialize() {
        byte[] header = new byte[8];

        header[0] = (byte) type;
        header[1] = (byte) code;
        header[2] = (byte) (checksum >> 8);
        header[3] = (byte) checksum;
        header[4] = (byte) (identifier >> 8);
        header[5] = (byte) identifier;
        header[6] = (byte) (sequenceNumber >> 8);
        header[7] = (byte) sequenceNumber;

        if (data != null && data.length > 0) {
            byte[] result = new byte[header.length + data.length];
            System.arraycopy(header, 0, result, 0, header.length);
            System.arraycopy(data, 0, result, header.length, data.length);
            return result;
        }

        return header;
    }

    @Override
    public void deserialize(byte[] data) {
        if (data.length < 8) return;

        type = data[0] & 0xFF;
        code = data[1] & 0xFF;
        checksum = ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
        identifier = ((data[4] & 0xFF) << 8) | (data[5] & 0xFF);
        sequenceNumber = ((data[6] & 0xFF) << 8) | (data[7] & 0xFF);

        if (data.length > 8) {
            this.data = new byte[data.length - 8];
            System.arraycopy(data, 8, this.data, 0, this.data.length);
        }
    }
}
