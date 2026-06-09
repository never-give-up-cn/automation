package com.never_give_up.automation.demo.network;

import com.never_give_up.automation.demo.model.BasePacket;
import lombok.Data;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public abstract class NetworkDevice {
    protected String name;
    protected String ipAddress;
    protected String macAddress;
    protected Map<String, String> interfaces = new ConcurrentHashMap<>();
    protected DeviceStatus status = DeviceStatus.ONLINE;

    public enum DeviceStatus {
        ONLINE, OFFLINE, BUSY, MAINTENANCE
    }

    public abstract void processPacket(BasePacket packet);
    public abstract void start();
    public abstract void stop();

    public boolean isOnline() {
        return status == DeviceStatus.ONLINE;
    }
}
