package com.never_give_up.automation.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class TcpPacket extends BasePacket {
    private int sourcePort;
    private int destinationPort;
    private int sequenceNumber;
    private int acknowledgmentNumber;
    private int dataOffset = 5;
    private int reserved;
    private String flags = "";
    private int windowSize;
    private int checksum;
    private int urgentPointer;
    private byte[] options;

    public boolean isSyn() { return flags.contains("SYN"); }
    public boolean isAck() { return flags.contains("ACK"); }
    public boolean isFin() { return flags.contains("FIN"); }
    public boolean isRst() { return flags.contains("RST"); }
    public boolean isPsh() { return flags.contains("PSH"); }

    @Override
    public byte[] serialize() {
        int headerLength = dataOffset * 4;
        byte[] header = new byte[headerLength];

        header[0] = (byte) (sourcePort >> 8);
        header[1] = (byte) sourcePort;
        header[2] = (byte) (destinationPort >> 8);
        header[3] = (byte) destinationPort;

        header[4] = (byte) (sequenceNumber >> 24);
        header[5] = (byte) (sequenceNumber >> 16);
        header[6] = (byte) (sequenceNumber >> 8);
        header[7] = (byte) sequenceNumber;

        header[8] = (byte) (acknowledgmentNumber >> 24);
        header[9] = (byte) (acknowledgmentNumber >> 16);
        header[10] = (byte) (acknowledgmentNumber >> 8);
        header[11] = (byte) acknowledgmentNumber;

        header[12] = (byte) (dataOffset << 4);

        byte flagByte = 0;
        if (flags.contains("FIN")) flagByte |= 0x01;
        if (flags.contains("SYN")) flagByte |= 0x02;
        if (flags.contains("RST")) flagByte |= 0x04;
        if (flags.contains("PSH")) flagByte |= 0x08;
        if (flags.contains("ACK")) flagByte |= 0x10;
        if (flags.contains("URG")) flagByte |= 0x20;
        header[13] = flagByte;

        header[14] = (byte) (windowSize >> 8);
        header[15] = (byte) windowSize;

        return header;
    }

    @Override
    public void deserialize(byte[] data) {
        if (data.length < 20) return;

        sourcePort = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);
        destinationPort = ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);

        sequenceNumber = ((data[4] & 0xFF) << 24) | ((data[5] & 0xFF) << 16) |
                        ((data[6] & 0xFF) << 8) | (data[7] & 0xFF);

        acknowledgmentNumber = ((data[8] & 0xFF) << 24) | ((data[9] & 0xFF) << 16) |
                              ((data[10] & 0xFF) << 8) | (data[11] & 0xFF);

        dataOffset = (data[12] >> 4) & 0xF;

        byte flagByte = data[13];
        StringBuilder flagStr = new StringBuilder();
        if ((flagByte & 0x01) != 0) flagStr.append("FIN,");
        if ((flagByte & 0x02) != 0) flagStr.append("SYN,");
        if ((flagByte & 0x04) != 0) flagStr.append("RST,");
        if ((flagByte & 0x08) != 0) flagStr.append("PSH,");
        if ((flagByte & 0x10) != 0) flagStr.append("ACK,");
        if ((flagByte & 0x20) != 0) flagStr.append("URG,");

        if (flagStr.length() > 0) {
            flags = flagStr.substring(0, flagStr.length() - 1);
        }

        windowSize = ((data[14] & 0xFF) << 8) | (data[15] & 0xFF);
    }
}
