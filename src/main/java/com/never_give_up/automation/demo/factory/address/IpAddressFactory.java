package com.never_give_up.automation.demo.factory.address;

import com.never_give_up.automation.demo.core.INetworkFactory;
import lombok.Getter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

@Getter
public class IpAddressFactory implements INetworkFactory<String> {
    private final Map<String, String> allocatedIps = new HashMap<>();
    private final Set<String> usedIps = new HashSet<>();

    private final String privateNetwork = "192.168.1";
    private final String publicNetwork = "10.0.0";
    private int privateCounter = 1;
    private int publicCounter = 1;

    public String allocatePrivateIp(String deviceName) {
        String ip = privateNetwork + "." + (privateCounter++);
        allocatedIps.put(deviceName, ip);
        usedIps.add(ip);
        return ip;
    }

    public String allocatePublicIp(String deviceName) {
        String ip = publicNetwork + "." + (publicCounter++);
        allocatedIps.put(deviceName, ip);
        usedIps.add(ip);
        return ip;
    }

    public void releaseIp(String ip) {
        usedIps.remove(ip);
        allocatedIps.values().remove(ip);
    }

    public boolean isValidIp(String ip) {
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public boolean isInSameSubnet(String ip1, String ip2, String subnetMask) {
        // TODO: 实现子网判断逻辑
        return false;
    }

    public void setCustomIp(String deviceName, String ip) {
        if (isValidIp(ip) && !usedIps.contains(ip)) {
            allocatedIps.put(deviceName, ip);
            usedIps.add(ip);
        }
    }

    @Override
    public String produce() {
        return allocatePrivateIp("AutoDevice_" + privateCounter);
    }

    @Override
    public void reset() {
        allocatedIps.clear();
        usedIps.clear();
        privateCounter = 1;
        publicCounter = 1;
    }
}
