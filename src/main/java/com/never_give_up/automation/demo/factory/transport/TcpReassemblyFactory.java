package com.never_give_up.automation.demo.factory.transport;

import java.util.*;
public class TcpReassemblyFactory {
    private final TreeMap<Long,byte[]> segments = new TreeMap<>();
    public void addSegment(long seq, byte[] data) {
        segments.put(seq,data);
    }
    public byte[] reassemble(long expectSeq) {
        return new byte[0];
    }
}