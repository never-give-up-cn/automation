package com.never_give_up.automation.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ArpPacket extends BasePacket {
    private int hardwareType = 1;
    private int protocolType = 0x0800;
    private int hardwareLength = 6;
    private int protocolLength = 4;
    private int operation;
    private String senderMac;
    private String senderIp;
    private String targetMac;
    private String targetIp;

    public boolean isRequest() { return operation == 1; }
    public boolean isReply() { return operation == 2; }

    @Override
    public byte[] serialize() {
        byte[] packet = new byte[28];

        packet[0] = (byte) (hardwareType >> 8);
        packet[1] = (byte) hardwareType;
        packet[2] = (byte) (protocolType >> 8);
        packet[3] = (byte) protocolType;
        packet[4] = (byte) hardwareLength;
        packet[5] = (byte) protocolLength;
        packet[6] = (byte) (operation >> 8);
        packet[7] = (byte) operation;

        byte[] senderMacBytes = parseMacAddress(senderMac);
        System.arraycopy(senderMacBytes, 0, packet, 8, 6);

        byte[] senderIpBytes = parseIpAddress(senderIp);
        System.arraycopy(senderIpBytes, 0, packet, 14, 4);

        byte[] targetMacBytes = parseMacAddress(targetMac);
        System.arraycopy(targetMacBytes, 0, packet, 18, 6);

        byte[] targetIpBytes = parseIpAddress(targetIp);
        System.arraycopy(targetIpBytes, 0, packet, 24, 4);

        return packet;
    }

    @Override
    public void deserialize(byte[] data) {
        if (data.length < 28) return;

        hardwareType = ((data[0] & 0xFF) << 8) | (data[1] & 0xFF);
        protocolType = ((data[2] & 0xFF) << 8) | (data[3] & 0xFF);
        operation = ((data[6] & 0xFF) << 8) | (data[7] & 0xFF);

        senderMac = formatMacAddress(data, 8);
        senderIp = formatIpAddress(data, 14);
        targetMac = formatMacAddress(data, 18);
        targetIp = formatIpAddress(data, 24);
    }

    private byte[] parseMacAddress(String mac) {
        String[] parts = mac.split(":");
        byte[] bytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) Integer.parseInt(parts[i], 16);
        }
        return bytes;
    }

    private byte[] parseIpAddress(String ip) {
        String[] parts = ip.split("\\.");
        byte[] bytes = new byte[4];
        for (int i = 0; i < 4; i++) {
            bytes[i] = (byte) Integer.parseInt(parts[i]);
        }
        return bytes;
    }

    private String formatMacAddress(byte[] data, int offset) {
        return String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                data[offset], data[offset+1], data[offset+2],
                data[offset+3], data[offset+4], data[offset+5]);
    }

    private String formatIpAddress(byte[] data, int offset) {
        return String.format("%d.%d.%d.%d",
                data[offset] & 0xFF, data[offset+1] & 0xFF,
                data[offset+2] & 0xFF, data[offset+3] & 0xFF);
    }
}
