package com.never_give_up.automation.demo.factory.capture;

import java.util.ArrayList;
import java.util.List;

public class PacketCaptureFactory {
    private final List<PcapPacket> capture = new ArrayList<>();

    public static class PcapPacket {
        public long time;
        public byte[] data;
        public int len;
    }

    public void capture(byte[] pkt) {
        PcapPacket p = new PcapPacket();
        p.time = System.currentTimeMillis();
        p.data = pkt.clone();
        p.len = pkt.length;
        capture.add(p);
    }

    public void save() {}
    public void load() {}
    public List<PcapPacket> replay() { return capture; }

    public void reset() { capture.clear(); }
}