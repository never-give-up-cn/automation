package com.never_give_up.automation.demo.factory.tunnel;
//16. GRE/IPIP 隧道工厂（TunnelFactory）
public class TunnelFactory {
    public byte[] greEncapsulate(byte[] innerPacket) {
        byte[] gre = new byte[4 + innerPacket.length];
        gre[0] = 1;
        gre[1] = 0;
        gre[2] = 0x08;
        gre[3] = 0x00;
        System.arraycopy(innerPacket, 0, gre, 4, innerPacket.length);
        return gre;
    }

    public byte[] ipipEncapsulate(byte[] inner, int src, int dst) {
        byte[] outer = new byte[20 + inner.length];
        outer[0] = 0x45;
        outer[12] = (byte) (src >> 24);
        outer[16] = (byte) (dst >> 24);
        System.arraycopy(inner, 0, outer, 20, inner.length);
        return outer;
    }
}