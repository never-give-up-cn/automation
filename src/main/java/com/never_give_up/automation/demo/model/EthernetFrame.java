package com.never_give_up.automation.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EthernetFrame extends BasePacket {
    private String destinationMac;
    private String sourceMac;
    private int etherType;
    private int frameCheckSequence;

    public boolean isArp() { return etherType == 0x0806; }
    public boolean isIp() { return etherType == 0x0800; }
    public boolean isIpv6() { return etherType == 0x86DD; }

    @Override
    public byte[] serialize() {
        byte[] header = new byte[14];

        byte[] dstMacBytes = parseMacAddress(destinationMac);
        System.arraycopy(dstMacBytes, 0, header, 0, 6);

        byte[] srcMacBytes = parseMacAddress(sourceMac);
        System.arraycopy(srcMacBytes, 0, header, 6, 6);

        header[12] = (byte) (etherType >> 8);
        header[13] = (byte) etherType;

        return header;
    }

    @Override
    public void deserialize(byte[] data) {
        if (data.length < 14) return;

        destinationMac = formatMacAddress(data, 0);
        sourceMac = formatMacAddress(data, 6);
        etherType = ((data[12] & 0xFF) << 8) | (data[13] & 0xFF);
    }

    private byte[] parseMacAddress(String mac) {
        String[] parts = mac.split(":");
        byte[] bytes = new byte[6];
        for (int i = 0; i < 6; i++) {
            bytes[i] = (byte) Integer.parseInt(parts[i], 16);
        }
        return bytes;
    }

    private String formatMacAddress(byte[] data, int offset) {
        return String.format("%02X:%02X:%02X:%02X:%02X:%02X",
                data[offset] & 0xFF, data[offset+1] & 0xFF, data[offset+2] & 0xFF,
                data[offset+3] & 0xFF, data[offset+4] & 0xFF, data[offset+5] & 0xFF);
    }
}
