package com.never_give_up.automation.demo.network;

import lombok.Getter;
import java.util.*;

@Getter
public class TopologyManager {
    private final List<NetworkDevice> devices = new ArrayList<>();
    private final Map<String, List<String>> connections = new HashMap<>();

    public void addDevice(NetworkDevice device) {
        devices.add(device);
    }

    public void connect(String device1, String device2) {
        connections.computeIfAbsent(device1, k -> new ArrayList<>()).add(device2);
        connections.computeIfAbsent(device2, k -> new ArrayList<>()).add(device1);
    }

    public NetworkDevice getDevice(String name) {
        return devices.stream()
                .filter(d -> d.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<NetworkDevice> getNeighbors(String deviceName) {
        List<String> neighborNames = connections.getOrDefault(deviceName, Collections.emptyList());
        List<NetworkDevice> neighbors = new ArrayList<>();
        for (String name : neighborNames) {
            NetworkDevice device = getDevice(name);
            if (device != null) {
                neighbors.add(device);
            }
        }
        return neighbors;
    }
}
