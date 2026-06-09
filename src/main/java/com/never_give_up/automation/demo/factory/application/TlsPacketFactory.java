package com.never_give_up.automation.demo.factory.application;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.TlsPacket;

import javax.crypto.Cipher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

public class TlsPacketFactory implements INetworkFactory<TlsPacket> {
    private KeyPair serverKeyPair;
    private Cipher rsaCipher;

    public TlsPacketFactory() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            serverKeyPair = keyGen.generateKeyPair();
            rsaCipher = Cipher.getInstance("RSA");
        } catch (Exception e) {
            throw new RuntimeException("TLS 初始化失败", e);
        }
    }

    public TlsPacket createClientHello(byte[] clientRandom) {
        TlsPacket packet = new TlsPacket();
        packet.setPacketType("TLS_CLIENT_HELLO");
        packet.setTlsMessageType("CLIENT_HELLO");
        packet.setTlsRecordType((byte) 0x16);
        packet.setClientRandom(clientRandom);
        return packet;
    }

    public TlsPacket createServerHelloCert(byte[] serverRandom) {
        TlsPacket packet = new TlsPacket();
        packet.setPacketType("TLS_SERVER_HELLO_CERT");
        packet.setTlsMessageType("SERVER_HELLO");
        packet.setTlsRecordType((byte) 0x16);
        packet.setServerRandom(serverRandom);
        packet.setServerCertificate(serverKeyPair.getPublic().getEncoded());
        return packet;
    }

    public TlsPacket createClientKeyExchange(byte[] encryptedPreMaster) {
        TlsPacket packet = new TlsPacket();
        packet.setPacketType("TLS_CLIENT_KEY_EXCHANGE");
        packet.setTlsMessageType("KEY_EXCHANGE");
        packet.setTlsRecordType((byte) 0x16);
        packet.setEncryptedData(encryptedPreMaster);
        return packet;
    }

    public TlsPacket createServerFinished() {
        TlsPacket packet = new TlsPacket();
        packet.setPacketType("TLS_SERVER_FINISHED");
        packet.setTlsMessageType("FINISHED");
        packet.setTlsRecordType((byte) 0x16);
        packet.setFinished(true);
        return packet;
    }

    public TlsPacket createChangeCipherSpec() {
        TlsPacket packet = new TlsPacket();
        packet.setPacketType("TLS_CHANGE_CIPHER_SPEC");
        packet.setTlsRecordType((byte) 0x14);
        packet.setChangeCipherSpec(true);
        return packet;
    }

    public KeyPair getServerKeyPair() {
        return serverKeyPair;
    }

    @Override
    public TlsPacket produce() {
        return createClientHello(new byte[32]);
    }

    @Override
    public void reset() {
    }
}
