package com.never_give_up.automation.demo.factory.address;

import com.never_give_up.automation.demo.core.INetworkFactory;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@Getter
public class PortFactory implements INetworkFactory<Integer> {
    private static final int WELL_KNOWN_PORT_MAX = 1023;
    private static final int REGISTERED_PORT_START = 1024;
    private static final int REGISTERED_PORT_END = 49151;
    private static final int EPHEMERAL_PORT_START = 49152;
    private static final int EPHEMERAL_PORT_END = 65535;

    private final Set<Integer> usedPorts = new HashSet<>();
    private int ephemeralCounter = EPHEMERAL_PORT_START;
    private int registeredCounter = REGISTERED_PORT_START;

    public int allocateEphemeralPort() {
        while (usedPorts.contains(ephemeralCounter)) {
            ephemeralCounter++;
            if (ephemeralCounter > EPHEMERAL_PORT_END) {
                ephemeralCounter = EPHEMERAL_PORT_START;
            }
            if (usedPorts.size() >= (EPHEMERAL_PORT_END - EPHEMERAL_PORT_START)) {
                throw new RuntimeException("临时端口池耗尽");
            }
        }
        usedPorts.add(ephemeralCounter);
        return ephemeralCounter++;
    }

    public int allocateRegisteredPort() {
        while (usedPorts.contains(registeredCounter)) {
            registeredCounter++;
            if (registeredCounter > REGISTERED_PORT_END) {
                throw new RuntimeException("注册端口池耗尽");
            }
        }
        usedPorts.add(registeredCounter);
        return registeredCounter++;
    }

    public void reservePort(int port) {
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("端口号必须在 0-65535 范围内");
        }
        usedPorts.add(port);
    }

    public void releasePort(int port) {
        usedPorts.remove(port);
    }

    public boolean isPortAvailable(int port) {
        return !usedPorts.contains(port) && port >= 0 && port <= 65535;
    }

    public boolean isWellKnownPort(int port) {
        return port >= 0 && port <= WELL_KNOWN_PORT_MAX;
    }

    public boolean isEphemeralPort(int port) {
        return port >= EPHEMERAL_PORT_START && port <= EPHEMERAL_PORT_END;
    }

    public String getPortRange(int port) {
        if (isWellKnownPort(port)) {
            return "Well-Known (0-1023)";
        } else if (port <= REGISTERED_PORT_END) {
            return "Registered (1024-49151)";
        } else {
            return "Ephemeral (49152-65535)";
        }
    }

    @Override
    public Integer produce() {
        return allocateEphemeralPort();
    }

    @Override
    public void reset() {
        usedPorts.clear();
        ephemeralCounter = EPHEMERAL_PORT_START;
        registeredCounter = REGISTERED_PORT_START;
    }
}
