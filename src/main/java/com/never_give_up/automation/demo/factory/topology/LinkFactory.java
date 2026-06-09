package com.never_give_up.automation.demo.factory.topology;

import com.never_give_up.automation.demo.core.INetworkFactory;
import com.never_give_up.automation.demo.network.TopologyManager;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class LinkFactory implements INetworkFactory<TopologyManager.Link> {
    private final List<TopologyManager.Link> createdLinks = new ArrayList<>();
    private final Map<String, TopologyManager.Link> linkMap = new HashMap<>();
    private int linkCounter = 0;

    public static class LinkConfig {
        private String sourceDevice;
        private String targetDevice;
        private int bandwidth = 1000;
        private int latency = 10;
        private double lossRate = 0.01;
        private boolean active = true;

        public LinkConfig(String source, String target) {
            this.sourceDevice = source;
            this.targetDevice = target;
        }

        public LinkConfig bandwidth(int bandwidth) {
            this.bandwidth = bandwidth;
            return this;
        }

        public LinkConfig latency(int latency) {
            this.latency = latency;
            return this;
        }

        public LinkConfig lossRate(double lossRate) {
            this.lossRate = lossRate;
            return this;
        }

        public LinkConfig active(boolean active) {
            this.active = active;
            return this;
        }

        public String getSourceDevice() {
            return sourceDevice;
        }

        public String getTargetDevice() {
            return targetDevice;
        }

        public int getBandwidth() {
            return bandwidth;
        }

        public int getLatency() {
            return latency;
        }

        public double getLossRate() {
            return lossRate;
        }

        public boolean isActive() {
            return active;
        }
    }

    public TopologyManager.Link createLink(LinkConfig config) {
        TopologyManager.Link link = new TopologyManager.Link(
                config.getSourceDevice(),
                config.getTargetDevice(),
                config.getBandwidth(),
                config.getLatency(),
                config.getLossRate()
        );
        link.setActive(config.isActive());
        createdLinks.add(link);

        String key = generateLinkKey(config.getSourceDevice(), config.getTargetDevice());
        linkMap.put(key, link);
        linkCounter++;

        return link;
    }

    public TopologyManager.Link createLink(String source, String target, int bandwidth, int latency, double lossRate) {
        LinkConfig config = new LinkConfig(source, target)
                .bandwidth(bandwidth)
                .latency(latency)
                .lossRate(lossRate);
        return createLink(config);
    }

    public TopologyManager.Link createLink(String source, String target) {
        return createLink(source, target, 1000, 10, 0.01);
    }

    public TopologyManager.Link createHighSpeedLink(String source, String target) {
        return createLink(source, target, 10000, 1, 0.001);
    }

    public TopologyManager.Link createSlowLink(String source, String target) {
        return createLink(source, target, 100, 100, 0.05);
    }

    public TopologyManager.Link createUnreliableLink(String source, String target) {
        return createLink(source, target, 1000, 20, 0.1);
    }

    public void deactivateLink(TopologyManager.Link link) {
        link.setActive(false);
    }

    public void activateLink(TopologyManager.Link link) {
        link.setActive(true);
    }

    public void removeLink(TopologyManager.Link link) {
        createdLinks.remove(link);
        String key = generateLinkKey(link.getSourceDevice(), link.getTargetDevice());
        linkMap.remove(key);
    }

    public TopologyManager.Link getLink(String source, String target) {
        return linkMap.get(generateLinkKey(source, target));
    }

    public List<TopologyManager.Link> getAllLinks() {
        return new ArrayList<>(createdLinks);
    }

    public List<TopologyManager.Link> getLinksByDevice(String deviceName) {
        List<TopologyManager.Link> result = new ArrayList<>();
        for (TopologyManager.Link link : createdLinks) {
            if (link.getSourceDevice().equals(deviceName) ||
                link.getTargetDevice().equals(deviceName)) {
                result.add(link);
            }
        }
        return result;
    }

    private String generateLinkKey(String source, String target) {
        return source + "->" + target;
    }

    @Override
    public TopologyManager.Link produce() {
        return createLink("device1", "device2");
    }

    @Override
    public void reset() {
        createdLinks.clear();
        linkMap.clear();
        linkCounter = 0;
    }

    public int getLinkCount() {
        return createdLinks.size();
    }
}
