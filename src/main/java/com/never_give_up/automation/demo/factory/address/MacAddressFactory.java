package com.never_give_up.automation.demo.factory.address;

import com.never_give_up.automation.demo.core.INetworkFactory;
import lombok.Getter;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Getter
public class MacAddressFactory implements INetworkFactory<String> {
    private static final String DEFAULT_PREFIX = "00:1A:2B";
    private final Set<String> allocatedMacs = new HashSet<>();
    private final Random random = new Random();

    public String generateMac() {
        String mac;
        do {
            mac = String.format("%s:%02X:%02X:%02X",
                    DEFAULT_PREFIX,
                    random.nextInt(256),
                    random.nextInt(256),
                    random.nextInt(256));
        } while (allocatedMacs.contains(mac));

        allocatedMacs.add(mac);
        return mac;
    }

    public String generateMac(String prefix) {
        String mac;
        do {
            mac = String.format("%s:%02X:%02X:%02X",
                    prefix,
                    random.nextInt(256),
                    random.nextInt(256),
                    random.nextInt(256));
        } while (allocatedMacs.contains(mac));

        allocatedMacs.add(mac);
        return mac;
    }

    public void releaseMac(String mac) {
        allocatedMacs.remove(mac);
    }

    public boolean isValidMac(String mac) {
        return mac != null && mac.matches("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
    }

    public boolean isMulticast(String mac) {
        if (!isValidMac(mac)) return false;
        String[] parts = mac.split("[:-]");
        int firstByte = Integer.parseInt(parts[0], 16);
        return (firstByte & 0x01) == 1;
    }

    @Override
    public String produce() {
        return generateMac();
    }

    @Override
    public void reset() {
        allocatedMacs.clear();
    }
}
