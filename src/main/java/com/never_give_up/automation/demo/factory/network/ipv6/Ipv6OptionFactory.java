package com.never_give_up.automation.demo.factory.network.ipv6;

import java.util.ArrayList;
import java.util.List;

public class Ipv6OptionFactory {
    private final List<Ipv6Option> options = new ArrayList<>();

    public void addOption(int type, byte[] data) {
        options.add(new Ipv6Option(type, data));
    }

    public byte[] buildOptions() {
        int len = 0;
        for (Ipv6Option opt : options) len += opt.getTotalLen();
        byte[] buf = new byte[len];
        int idx = 0;
        for (Ipv6Option opt : options) {
            buf[idx++] = (byte) opt.type;
            buf[idx++] = (byte) opt.dataLen;
            System.arraycopy(opt.data, 0, buf, idx, opt.dataLen);
            idx += opt.dataLen;
        }
        return buf;
    }

    public void reset() {
        options.clear();
    }

    static class Ipv6Option {
        int type;
        int dataLen;
        byte[] data;
        public Ipv6Option(int type, byte[] data) {
            this.type = type;
            this.dataLen = data.length;
            this.data = data;
        }
        public int getTotalLen() { return 2 + dataLen; }
    }
}