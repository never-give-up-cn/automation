package com.never_give_up.automation.demo.factory.function;

import lombok.Data;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class NatMappingFactory {
    private final Map<String, NatEntry> natTable = new ConcurrentHashMap<>();
    private final AtomicInteger portCounter = new AtomicInteger(50001);
    private final String publicIp;

    @Data
    public static class NatEntry {
        private final String insideIp;
        private final int insidePort;
        private final String publicIp;
        private final int publicPort;
        private final long createTime;

        public NatEntry(String insideIp, int insidePort, String publicIp, int publicPort) {
            this.insideIp = insideIp;
            this.insidePort = insidePort;
            this.publicIp = publicIp;
            this.publicPort = publicPort;
            this.createTime = System.currentTimeMillis();
        }
    }

    public NatMappingFactory(String publicIp) {
        this.publicIp = publicIp;
    }

    public NatEntry createMapping(String insideIp, int insidePort) {
        String key = insideIp + ":" + insidePort;

        if (natTable.containsKey(key)) {
            return natTable.get(key);
        }

        int publicPort = portCounter.incrementAndGet();
        if (publicPort > 65535) {
            portCounter.set(50001);
            publicPort = portCounter.incrementAndGet();
        }

        NatEntry entry = new NatEntry(insideIp, insidePort, publicIp, publicPort);
        natTable.put(key, entry);
        return entry;
    }

    public NatEntry findMapping(String insideIp, int insidePort) {
        return natTable.get(insideIp + ":" + insidePort);
    }

    public NatEntry findInternalMapping(String publicIp, int publicPort) {
        return natTable.values().stream()
                .filter(e -> e.getPublicIp().equals(publicIp) && e.getPublicPort() == publicPort)
                .findFirst()
                .orElse(null);
    }

    public void removeMapping(String insideIp, int insidePort) {
        natTable.remove(insideIp + ":" + insidePort);
    }

    public void clear() {
        natTable.clear();
    }

    public int size() {
        return natTable.size();
    }

    public Map<String, NatEntry> getNatTable() {
        return natTable;
    }
}
