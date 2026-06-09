package com.never_give_up.automation.demo.factory.address;

import com.never_give_up.automation.demo.core.INetworkFactory;
import lombok.Getter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class IpAddressFactory implements INetworkFactory<String> {
    private final Map<String, String> deviceIpMap = new HashMap<>();
    private final Set<String> usedIps = new HashSet<>();

    private String privateNetwork = "192.168.1";
    private String publicNetwork = "10.0.0";
    private int privateCounter = 1;
    private int publicCounter = 1;
    private int subnetMask = 24;

    public String allocatePrivateIp(String deviceName) {
        String ip;
        do {
            ip = privateNetwork + "." + (privateCounter++);
            if (privateCounter > 254) {
                throw new RuntimeException("私有地址池耗尽");
            }
        } while (usedIps.contains(ip));

        deviceIpMap.put(deviceName, ip);
        usedIps.add(ip);
        return ip;
    }

    public String allocatePublicIp(String deviceName) {
        String ip;
        do {
            ip = publicNetwork + "." + (publicCounter++);
            if (publicCounter > 254) {
                throw new RuntimeException("公网地址池耗尽");
            }
        } while (usedIps.contains(ip));

        deviceIpMap.put(deviceName, ip);
        usedIps.add(ip);
        return ip;
    }

    public void releaseIp(String ip) {
        usedIps.remove(ip);
        deviceIpMap.values().remove(ip);
    }

    public String setCustomIp(String deviceName, String ip) {
        if (isValidIp(ip) && !usedIps.contains(ip)) {
            deviceIpMap.put(deviceName, ip);
            usedIps.add(ip);
            return ip;
        } else {
            throw new IllegalArgumentException("无效的IP地址或已被占用: " + ip);
        }
    }

    public String getDeviceIp(String deviceName) {
        return deviceIpMap.get(deviceName);
    }

    public boolean isValidIp(String ip) {
        try {
            InetAddress.getByName(ip);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public boolean isInSameSubnet(String ip1, String ip2) {
        if (!isValidIp(ip1) || !isValidIp(ip2)) return false;

        try {
            byte[] bytes1 = InetAddress.getByName(ip1).getAddress();
            byte[] bytes2 = InetAddress.getByName(ip2).getAddress();

            int mask = subnetMask;
            for (int i = 0; i < 4; i++) {
                int bits = Math.min(8, mask);
                int maskByte = (0xFF << (8 - bits)) & 0xFF;

                if ((bytes1[i] & maskByte) != (bytes2[i] & maskByte)) {
                    return false;
                }
                mask -= bits;
            }
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }

    public String getNetworkAddress(String ip) {
        if (!isValidIp(ip)) return null;

        try {
            byte[] bytes = InetAddress.getByName(ip).getAddress();
            int[] octets = new int[4];

            int mask = subnetMask;
            for (int i = 0; i < 4; i++) {
                int bits = Math.min(8, mask);
                int maskByte = (0xFF << (8 - bits)) & 0xFF;
                octets[i] = bytes[i] & maskByte;
                mask -= bits;
            }

            return String.format("%d.%d.%d.%d", octets[0], octets[1], octets[2], octets[3]);
        } catch (UnknownHostException e) {
            return null;
        }
    }

    public void setSubnetMask(int mask) {
        if (mask >= 0 && mask <= 32) {
            this.subnetMask = mask;
        }
    }

    public void setPrivateNetwork(String network) {
        this.privateNetwork = network;
    }

    public void setPublicNetwork(String network) {
        this.publicNetwork = network;
    }

    @Override
    public String produce() {
        return allocatePrivateIp("AutoDevice_" + privateCounter);
    }

    @Override
    public void reset() {
        deviceIpMap.clear();
        usedIps.clear();
        privateCounter = 1;
        publicCounter = 1;
    }
}
