package com.never_give_up.automation.demo.factory.security.tls;

/** DTLS 数据报TLS */
public class DtlsFactory {
    private int epoch = 0;
    private long seq = 0;

    public byte[] buildDtlsRecord(byte[] appData) {
        byte[] record = new byte[13 + appData.length];
        record[0] = 0x17; // Application Data
        record[3] = (byte) (epoch >> 8);
        record[4] = (byte) epoch;
        record[5] = (byte) (seq >> 48);
        seq++;
        return record;
    }

    public void reset() {
        epoch = 0;
        seq = 0;
    }
}