package com.never_give_up.automation.demo.factory.link;
//5. 以太网帧填充工厂（EthernetPaddingFactory）
public class EthernetPaddingFactory {
    private static final int MIN_FRAME = 46;

    public byte[] pad(byte[] payload) {
        if (payload.length >= MIN_FRAME) return payload;
        byte[] res = new byte[MIN_FRAME];
        System.arraycopy(payload, 0, res, 0, payload.length);
        return res;
    }
}