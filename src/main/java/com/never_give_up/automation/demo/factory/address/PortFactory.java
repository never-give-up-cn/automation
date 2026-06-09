package com.never_give_up.automation.demo.factory.address;

import com.never_give_up.automation.demo.core.INetworkFactory;
import lombok.Getter;
import java.util.HashSet;
import java.util.Set;

@Getter
public class PortFactory implements INetworkFactory<Integer> {
    private static final int WELL_KNOWN_PORT_MAX = 1023;
    private static final int EPHEMERAL_PORT_START = 49152;
    private static final int EPHEMERAL_PORT_END = 65535;

    private final Set<Integer> usedPorts = new HashSet<>();
    private int ephemeralCounter = EPHEMERAL_PORT_START;

    public int allocateEphemeralPort() {
        while (usedPorts.contains(ephemeralCounter)) {
            ephemeralCounter++;
            if (ephemeralCounter > EPHEMERAL_PORT_END) {
                throw new RuntimeException("端口池耗尽");
            }
        }
        usedPorts.add(ephemeralCounter);
        return ephemeralCounter++;
    }

    public void releasePort(int port) {
        usedPorts.remove(port);
    }

    public boolean isWellKnownPort(int port) {
        return port <= WELL_KNOWN_PORT_MAX;
    }

    @Override
    public Integer produce() {
        return allocateEphemeralPort();
    }

    @Override
    public void reset() {
        usedPorts.clear();
        ephemeralCounter = EPHEMERAL_PORT_START;
    }
}
