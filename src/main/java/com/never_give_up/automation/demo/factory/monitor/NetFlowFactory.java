package com.never_give_up.automation.demo.factory.monitor;

import java.util.ArrayList;
import java.util.List;

/** NetFlow 流量采集 */
public class NetFlowFactory {
    public static class FlowRecord {
        String srcIp;
        String dstIp;
        int srcPort;
        int dstPort;
        long packetCount;
    }

    private final List<FlowRecord> flowRecords = new ArrayList<>();

    public void addRecord(String sIp, String dIp, int sPort, int dPort, long pktCnt) {
        FlowRecord r = new FlowRecord();
        r.srcIp = sIp;
        r.dstIp = dIp;
        r.srcPort = sPort;
        r.dstPort = dPort;
        r.packetCount = pktCnt;
        flowRecords.add(r);
    }

    public List<FlowRecord> getRecords() {
        return flowRecords;
    }

    public void reset() {
        flowRecords.clear();
    }
}