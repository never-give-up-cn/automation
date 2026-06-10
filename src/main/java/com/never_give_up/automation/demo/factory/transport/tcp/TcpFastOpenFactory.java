package com.never_give_up.automation.demo.factory.transport.tcp;

public class TcpFastOpenFactory {
    private byte[] cookie = new byte[8];

    public byte[] buildTfoOption() {
        byte[] opt = new byte[4 + cookie.length];
        opt[0] = 34;
        opt[1] = (byte) opt.length;
        System.arraycopy(cookie, 0, opt, 2, cookie.length);
        return opt;
    }

    public void reset() {
        cookie = new byte[8];
    }
}