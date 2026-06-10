package com.never_give_up.automation.demo.factory.flow;

import java.util.HashMap;
import java.util.Map;

public class FlowFactory {
    private final Map<String, Flow> flowMap = new HashMap<>();

    public static class Flow {
        public String flowId;
        public long packets;
        public long bytes;
        public long rtt;
        public long duration;
    }

    public void updateFlow(String fid, long bytes) {
        Flow f = flowMap.getOrDefault(fid, new Flow());
        f.flowId = fid;
        f.packets++;
        f.bytes += bytes;
        flowMap.put(fid, f);
    }

    public Flow getFlow(String fid) {
        return flowMap.get(fid);
    }

    public void reset() { flowMap.clear(); }
}