package com.never_give_up.automation.demo.factory.transport.tcp;

import java.util.ArrayList;
import java.util.List;

public class TcpSackFactory {
    private final List<Block> blocks = new ArrayList<>();

    public void addBlock(long left, long right) {
        blocks.add(new Block(left, right));
    }

    public byte[] buildSackOption() {
        int len = 2 + blocks.size() * 8;
        byte[] opt = new byte[len];
        opt[0] = 5;
        opt[1] = (byte) len;
        int idx = 2;
        for (Block b : blocks) {
            writeLong(opt, idx, b.left); idx +=4;
            writeLong(opt, idx, b.right); idx +=4;
        }
        return opt;
    }

    private void writeLong(byte[] d, int i, long v) {
        d[i] = (byte)(v>>24); d[i+1]=(byte)(v>>16); d[i+2]=(byte)(v>>8); d[i+3]=(byte)v;
    }

    public void reset() { blocks.clear(); }
    record Block(long left, long right){}
}