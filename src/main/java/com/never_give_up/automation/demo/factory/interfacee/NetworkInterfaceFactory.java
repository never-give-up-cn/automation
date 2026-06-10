package com.never_give_up.automation.demo.factory.interfacee;
//14. 网络接口 / 网卡工厂（NetworkInterfaceFactory）
import java.util.*;

public class NetworkInterfaceFactory {
    private final List<NetworkInterface> ifaces = new ArrayList<>();

    public NetworkInterface create(String name, String mac, String ip) {
        NetworkInterface iface = new NetworkInterface();
        iface.name = name;
        iface.mac = mac;
        iface.ip = ip;
        iface.mtu = 1500;
        ifaces.add(iface);
        return iface;
    }

    public static class NetworkInterface {
        public String name;
        public String mac;
        public String ip;
        public int mtu;
        public Map<String, String> arpTable = new HashMap<>();
    }
}
