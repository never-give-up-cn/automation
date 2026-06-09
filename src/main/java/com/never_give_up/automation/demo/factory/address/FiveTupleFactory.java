package com.never_give_up.automation.demo.factory.address;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.model.FiveTuple;

import java.util.HashMap;
import java.util.Map;

public class FiveTupleFactory implements INetworkFactory<FiveTuple> {
    private final IpAddressFactory ipFactory;
    private final PortFactory portFactory;
    private final Map<String, FiveTuple> sessionMap = new HashMap<>();

    public FiveTupleFactory(IpAddressFactory ipFactory, PortFactory portFactory) {
        this.ipFactory = ipFactory;
        this.portFactory = portFactory;
    }

    public FiveTuple createTcpSession(String sourceIp, String destIp, int destPort) {
        int sourcePort = portFactory.allocateEphemeralPort();
        FiveTuple tuple = new FiveTuple(sourceIp, destIp, sourcePort, destPort, 6);
        sessionMap.put(tuple.toString(), tuple);
        return tuple;
    }

    public FiveTuple createUdpSession(String sourceIp, String destIp, int destPort) {
        int sourcePort = portFactory.allocateEphemeralPort();
        FiveTuple tuple = new FiveTuple(sourceIp, destIp, sourcePort, destPort, 17);
        sessionMap.put(tuple.toString(), tuple);
        return tuple;
    }

    public FiveTuple createIcmpSession(String sourceIp, String destIp) {
        FiveTuple tuple = new FiveTuple(sourceIp, destIp, 0, 0, 1);
        sessionMap.put(tuple.toString(), tuple);
        return tuple;
    }

    public void closeSession(FiveTuple tuple) {
        sessionMap.remove(tuple.toString());
        portFactory.releasePort(tuple.getSourcePort());
    }

    public FiveTuple findSession(String key) {
        return sessionMap.get(key);
    }

    public boolean isValidFiveTuple(FiveTuple tuple) {
        if (tuple == null) return false;
        if (!ipFactory.isValidIp(tuple.getSourceIp())) return false;
        if (!ipFactory.isValidIp(tuple.getDestinationIp())) return false;
        if (tuple.getSourcePort() < 0 || tuple.getSourcePort() > 65535) return false;
        if (tuple.getDestinationPort() < 0 || tuple.getDestinationPort() > 65535) return false;
        if (tuple.getProtocol() < 1 || tuple.getProtocol() > 255) return false;
        return true;
    }

    public int getSessionCount() {
        return sessionMap.size();
    }

    @Override
    public FiveTuple produce() {
        String srcIp = ipFactory.produce();
        String destIp = ipFactory.produce();
        return createTcpSession(srcIp, destIp, 80);
    }

    @Override
    public void reset() {
        sessionMap.clear();
    }
}
