package com.never_give_up.automation.demo.factory.nat;

import java.util.ArrayList;
import java.util.List;

/** UPnP 端口映射 */
public class UpnpFactory {
    public static class PortMap {
        int externalPort;
        int internalPort;
        String internalIp;
        String protocol;
    }

    private final List<PortMap> portMaps = new ArrayList<>();

    public void addPortMap(int extPort, int intPort, String intIp, String proto) {
        PortMap map = new PortMap();
        map.externalPort = extPort;
        map.internalPort = intPort;
        map.internalIp = intIp;
        map.protocol = proto;
        portMaps.add(map);
    }

    public List<PortMap> getPortMaps() {
        return portMaps;
    }

    public void reset() {
        portMaps.clear();
    }
}