package com.never_give_up.automation.demo.factory.control;

import lombok.Getter;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

@Getter
public class BandwidthFactory {
    private final int bandwidthKbps;
    private final int latencyMs;
    private final double packetLossRate;
    private final Random random = new Random();

    public BandwidthFactory(int bandwidthKbps, int latencyMs, double packetLossRate) {
        this.bandwidthKbps = bandwidthKbps;
        this.latencyMs = latencyMs;
        this.packetLossRate = Math.min(1.0, Math.max(0.0, packetLossRate));
    }

    public boolean shouldDropPacket() {
        return random.nextDouble() < packetLossRate;
    }

    public long calculateTransmissionDelay(int packetSizeBytes) {
        long bits = packetSizeBytes * 8L;
        return (bits * 1000) / bandwidthKbps;
    }

    public long getTotalDelay(int packetSizeBytes) {
        return calculateTransmissionDelay(packetSizeBytes) + latencyMs;
    }

    public static class DelayedPacket implements Delayed {
        private final Object packet;
        private final long executeTime;

        public DelayedPacket(Object packet, long delayMs) {
            this.packet = packet;
            this.executeTime = System.currentTimeMillis() + delayMs;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long remaining = executeTime - System.currentTimeMillis();
            return unit.convert(remaining, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed other) {
            return Long.compare(this.executeTime, ((DelayedPacket) other).executeTime);
        }

        public Object getPacket() {
            return packet;
        }
    }
}
