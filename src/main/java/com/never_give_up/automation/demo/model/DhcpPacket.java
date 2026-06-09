package com.never_give_up.automation.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class DhcpPacket extends BasePacket {
    private int messageType;
    private String transactionId;
    private String clientMac;
    private String clientIp;
    private String offeredIp;
    private String serverIp;
    private String requestedIp;
    private int leaseTime;
    private Map<String, String> options = new HashMap<>();

    public boolean isDiscover() { return messageType == 1; }
    public boolean isOffer() { return messageType == 2; }
    public boolean isRequest() { return messageType == 3; }
    public boolean isAck() { return messageType == 5; }

    @Override
    public byte[] serialize() {
        byte[] packet = new byte[240];

        packet[0] = 1;
        packet[1] = 1;
        packet[2] = 6;
        packet[3] = 0;

        byte[] transId = transactionId.getBytes();
        System.arraycopy(transId, 0, packet, 4, Math.min(transId.length, 4));

        byte[] macBytes = clientMac.replace(":", "").getBytes();
        System.arraycopy(macBytes, 0, packet, 28, Math.min(macBytes.length, 12));

        if (offeredIp != null) {
            byte[] ipBytes = offeredIp.getBytes();
            System.arraycopy(ipBytes, 0, packet, 16, Math.min(ipBytes.length, 4));
        }

        packet[236] = (byte) 99;
        packet[237] = (byte) 130;
        packet[238] = (byte) 83;
        packet[239] = (byte) 99;

        return packet;
    }

    @Override
    public void deserialize(byte[] data) {
        if (data.length < 240) return;

        byte[] transIdBytes = new byte[4];
        System.arraycopy(data, 4, transIdBytes, 0, 4);
        transactionId = new String(transIdBytes);
    }
}
