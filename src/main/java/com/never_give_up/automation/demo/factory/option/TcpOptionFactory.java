package com.never_give_up.automation.demo.factory.option;
//2. TCP 选项头部工厂（TcpOptionFactory）
public class TcpOptionFactory {
    public static final byte NOP = 1;
    public static final byte MSS = 2;
    public static final byte WINDOW_SCALE = 3;
    public static final byte SACK_PERM = 4;
    public static final byte SACK = 5;
    public static final byte TIMESTAMP = 8;

    public byte[] mss(int value) {
        return new byte[]{MSS, 4, (byte) (value >> 8), (byte) value};
    }

    public byte[] windowScale(int shift) {
        return new byte[]{WINDOW_SCALE, 3, (byte) shift};
    }

    public byte[] sackPerm() {
        return new byte[]{SACK_PERM, 2};
    }

    public byte[] timestamp(long ts, long echo) {
        byte[] opt = new byte[10];
        opt[0] = TIMESTAMP;
        opt[1] = 10;
        opt[2] = (byte) (ts >> 24);
        opt[3] = (byte) (ts >> 16);
        opt[4] = (byte) (ts >> 8);
        opt[5] = (byte) ts;
        opt[6] = (byte) (echo >> 24);
        opt[7] = (byte) (echo >> 16);
        opt[8] = (byte) (echo >> 8);
        opt[9] = (byte) echo;
        return opt;
    }

    public byte[] nop() {
        return new byte[]{NOP};
    }

    public byte[] combine(byte[]... opts) {
        int len = 0;
        for (byte[] o : opts) len += o.length;
        byte[] res = new byte[len];
        int p = 0;
        for (byte[] o : opts) {
            System.arraycopy(o, 0, res, p, o.length);
            p += o.length;
        }
        return res;
    }
}