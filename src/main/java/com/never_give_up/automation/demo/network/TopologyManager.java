package com.never_give_up.automation.demo.network;

import com.never_give_up.automation.demo.model.BasePacket;
import lombok.Data;
import lombok.Getter;

import java.util.*;

@Getter
public class TopologyManager {
    private final List<NetworkDevice> devices = new ArrayList<>();
    private final Map<String, List<Link>> connections = new HashMap<>();
    private final Map<String, Subnet> subnets = new HashMap<>();

    @Data
    public static class Link {
        private String sourceDevice;
        private String targetDevice;
        private int bandwidth;
        private int latency;
        private double lossRate;
        private boolean active;

        public Link(String source, String target, int bandwidth, int latency, double lossRate) {
            this.sourceDevice = source;
            this.targetDevice = target;
            this.bandwidth = bandwidth;
            this.latency = latency;
            this.lossRate = lossRate;
            this.active = true;
        }
    }

    @Data
    public static class Subnet {
        private String name;
        private String networkAddress;
        private String subnetMask;
        private List<String> deviceNames = new ArrayList<>();

        public Subnet(String name, String network, String mask) {
            this.name = name;
            this.networkAddress = network;
            this.subnetMask = mask;
        }
    }

    public void addDevice(NetworkDevice device) {
        devices.add(device);
    }

    public void removeDevice(String deviceName) {
        devices.removeIf(d -> d.getName().equals(deviceName));
        connections.remove(deviceName);
        connections.values().forEach(links ->
            links.removeIf(link -> link.getTargetDevice().equals(deviceName))
        );
    }

    public void addLink(String sourceDevice, String targetDevice, int bandwidth, int latency, double lossRate) {
        Link link = new Link(sourceDevice, targetDevice, bandwidth, latency, lossRate);
        connections.computeIfAbsent(sourceDevice, k -> new ArrayList<>()).add(link);

        Link reverseLink = new Link(targetDevice, sourceDevice, bandwidth, latency, lossRate);
        connections.computeIfAbsent(targetDevice, k -> new ArrayList<>()).add(reverseLink);
    }

    public void addSubnet(String name, String network, String mask) {
        subnets.put(name, new Subnet(name, network, mask));
    }

    public void addDeviceToSubnet(String subnetName, String deviceName) {
        Subnet subnet = subnets.get(subnetName);
        if (subnet != null) {
            subnet.getDeviceNames().add(deviceName);
        }
    }

    public NetworkDevice getDevice(String name) {
        return devices.stream()
                .filter(d -> d.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public List<NetworkDevice> getNeighbors(String deviceName) {
        List<Link> links = connections.getOrDefault(deviceName, Collections.emptyList());
        List<NetworkDevice> neighbors = new ArrayList<>();

        for (Link link : links) {
            if (link.isActive()) {
                NetworkDevice device = getDevice(link.getTargetDevice());
                if (device != null) {
                    neighbors.add(device);
                }
            }
        }

        return neighbors;
    }

    public List<Link> getLinks(String deviceName) {
        return connections.getOrDefault(deviceName, Collections.emptyList());
    }

    public List<NetworkDevice> getDevicesInSubnet(String subnetName) {
        Subnet subnet = subnets.get(subnetName);
        if (subnet == null) return Collections.emptyList();

        List<NetworkDevice> result = new ArrayList<>();
        for (String deviceName : subnet.getDeviceNames()) {
            NetworkDevice device = getDevice(deviceName);
            if (device != null) {
                result.add(device);
            }
        }

        return result;
    }

    public void sendPacket(String sourceDevice, String targetDevice, BasePacket packet) {
        NetworkDevice source = getDevice(sourceDevice);
        NetworkDevice target = getDevice(targetDevice);

        if (source != null && target != null) {
            source.processPacket(packet);
        }
    }

    public List<String> findPath(String sourceDevice, String targetDevice) {
        if (sourceDevice.equals(targetDevice)) {
            return List.of(sourceDevice);
        }

        Queue<List<String>> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();

        List<String> initialPath = new ArrayList<>();
        initialPath.add(sourceDevice);
        queue.offer(initialPath);
        visited.add(sourceDevice);

        while (!queue.isEmpty()) {
            List<String> path = queue.poll();
            String currentDevice = path.get(path.size() - 1);

            for (Link link : connections.getOrDefault(currentDevice, Collections.emptyList())) {
                String nextDevice = link.getTargetDevice();

                if (nextDevice.equals(targetDevice)) {
                    List<String> completePath = new ArrayList<>(path);
                    completePath.add(nextDevice);
                    return completePath;
                }

                if (!visited.contains(nextDevice)) {
                    visited.add(nextDevice);
                    List<String> newPath = new ArrayList<>(path);
                    newPath.add(nextDevice);
                    queue.offer(newPath);
                }
            }
        }

        return Collections.emptyList();
    }

    public void clear() {
        devices.clear();
        connections.clear();
        subnets.clear();
    }

    public int getDeviceCount() {
        return devices.size();
    }

    public int getLinkCount() {
        return connections.values().stream().mapToInt(List::size).sum() / 2;
    }
}
