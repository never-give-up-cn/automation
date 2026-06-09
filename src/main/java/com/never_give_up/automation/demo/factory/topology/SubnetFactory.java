package com.never_give_up.automation.demo.factory.topology;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.network.TopologyManager;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class SubnetFactory implements INetworkFactory<TopologyManager.Subnet> {
    private final List<TopologyManager.Subnet> createdSubnets = new ArrayList<>();
    private final Map<String, TopologyManager.Subnet> subnetMap = new HashMap<>();
    private int subnetCounter = 0;

    public static class SubnetConfig {
        private String name;
        private String networkAddress;
        private String subnetMask;
        private List<String> deviceNames = new ArrayList<>();

        public SubnetConfig(String name, String network, String mask) {
            this.name = name;
            this.networkAddress = network;
            this.subnetMask = mask;
        }

        public SubnetConfig addDevice(String deviceName) {
            this.deviceNames.add(deviceName);
            return this;
        }

        public SubnetConfig addDevices(List<String> devices) {
            this.deviceNames.addAll(devices);
            return this;
        }

        public String getName() {
            return name;
        }

        public String getNetworkAddress() {
            return networkAddress;
        }

        public String getSubnetMask() {
            return subnetMask;
        }

        public List<String> getDeviceNames() {
            return deviceNames;
        }
    }

    public TopologyManager.Subnet createSubnet(SubnetConfig config) {
        TopologyManager.Subnet subnet = new TopologyManager.Subnet(
                config.getName(),
                config.getNetworkAddress(),
                config.getSubnetMask()
        );
        subnet.getDeviceNames().addAll(config.getDeviceNames());
        createdSubnets.add(subnet);
        subnetMap.put(config.getName(), subnet);
        subnetCounter++;
        return subnet;
    }

    public TopologyManager.Subnet createSubnet(String name, String network, String mask) {
        SubnetConfig config = new SubnetConfig(name, network, mask);
        return createSubnet(config);
    }

    public TopologyManager.Subnet createClassCSubnet(String name, String networkPrefix) {
        return createSubnet(name, networkPrefix + ".0", "255.255.255.0");
    }

    public TopologyManager.Subnet createClassBSubnet(String name, String networkPrefix) {
        return createSubnet(name, networkPrefix + ".0.0", "255.255.0.0");
    }

    public TopologyManager.Subnet createDefaultSubnet(String name) {
        subnetCounter++;
        return createClassCSubnet("Subnet-" + name, "192.168." + subnetCounter);
    }

    public void addDeviceToSubnet(String subnetName, String deviceName) {
        TopologyManager.Subnet subnet = subnetMap.get(subnetName);
        if (subnet != null && !subnet.getDeviceNames().contains(deviceName)) {
            subnet.getDeviceNames().add(deviceName);
        }
    }

    public void removeDeviceFromSubnet(String subnetName, String deviceName) {
        TopologyManager.Subnet subnet = subnetMap.get(subnetName);
        if (subnet != null) {
            subnet.getDeviceNames().remove(deviceName);
        }
    }

    public void removeSubnet(String name) {
        TopologyManager.Subnet subnet = subnetMap.remove(name);
        if (subnet != null) {
            createdSubnets.remove(subnet);
        }
    }

    public TopologyManager.Subnet getSubnet(String name) {
        return subnetMap.get(name);
    }

    public List<TopologyManager.Subnet> getAllSubnets() {
        return new ArrayList<>(createdSubnets);
    }

    public List<String> getDevicesInSubnet(String subnetName) {
        TopologyManager.Subnet subnet = subnetMap.get(subnetName);
        return subnet != null ? subnet.getDeviceNames() : new ArrayList<>();
    }

    public boolean isDeviceInSubnet(String subnetName, String deviceName) {
        TopologyManager.Subnet subnet = subnetMap.get(subnetName);
        return subnet != null && subnet.getDeviceNames().contains(deviceName);
    }

    @Override
    public TopologyManager.Subnet produce() {
        subnetCounter++;
        return createDefaultSubnet(String.valueOf(subnetCounter));
    }

    @Override
    public void reset() {
        createdSubnets.clear();
        subnetMap.clear();
        subnetCounter = 0;
    }

    public int getSubnetCount() {
        return createdSubnets.size();
    }
}
