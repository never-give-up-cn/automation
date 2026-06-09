package com.never_give_up.automation.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.security.PublicKey;

@EqualsAndHashCode(callSuper = true)
@Data
public class TlsPacket extends BasePacket {
    private String tlsMessageType;
    private byte tlsRecordType;
    private int tlsVersion = 0x0303;
    private byte[] encryptedData;
    private byte[] serverCertificate;
    private PublicKey serverPublicKey;
    private byte[] clientRandom;
    private byte[] serverRandom;
    private byte[] preMasterSecret;
    private byte[] sessionKey;
    private boolean changeCipherSpec;
    private boolean finished;

    public boolean isClientHello() { return "CLIENT_HELLO".equals(tlsMessageType); }
    public boolean isServerHello() { return "SERVER_HELLO".equals(tlsMessageType); }
    public boolean isCertificate() { return "CERTIFICATE".equals(tlsMessageType); }
    public boolean isKeyExchange() { return "KEY_EXCHANGE".equals(tlsMessageType); }
    public boolean isFinished() { return finished; }

    @Override
    public byte[] serialize() {
        byte[] header = new byte[5];
        header[0] = tlsRecordType;
        header[1] = (byte) (tlsVersion >> 8);
        header[2] = (byte) tlsVersion;

        if (encryptedData != null) {
            header[3] = (byte) (encryptedData.length >> 8);
            header[4] = (byte) encryptedData.length;

            byte[] result = new byte[5 + encryptedData.length];
            System.arraycopy(header, 0, result, 0, 5);
            System.arraycopy(encryptedData, 0, result, 5, encryptedData.length);
            return result;
        }

        return header;
    }

    @Override
    public void deserialize(byte[] data) {
        if (data.length < 5) return;

        tlsRecordType = data[0];
        tlsVersion = ((data[1] & 0xFF) << 8) | (data[2] & 0xFF);
        int length = ((data[3] & 0xFF) << 8) | (data[4] & 0xFF);

        if (data.length > 5) {
            encryptedData = new byte[length];
            System.arraycopy(data, 5, encryptedData, 0, Math.min(length, data.length - 5));
        }
    }
}
