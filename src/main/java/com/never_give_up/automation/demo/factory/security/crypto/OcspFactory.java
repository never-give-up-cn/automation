package com.never_give_up.automation.demo.factory.security.crypto;

/** OCSP 在线证书状态查询 */
public class OcspFactory {
    public byte[] buildRequest(String serialNumber) {
        byte[] req = new byte[64];
        return req;
    }

    public byte[] buildResponse(boolean valid) {
        byte[] resp = new byte[48];
        resp[0] = (byte) (valid ? 0x00 : 0x01);
        return resp;
    }

    public void reset() {}
}