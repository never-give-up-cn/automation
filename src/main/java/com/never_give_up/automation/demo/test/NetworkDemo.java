package com.never_give_up.automation.demo.test;

import com.never_give_up.automation.demo.network.DnsServer;
import com.never_give_up.automation.demo.network.Host;
import com.never_give_up.automation.demo.network.Router;
import com.never_give_up.automation.demo.network.TopologyManager;

public class NetworkDemo {
    public static void main(String[] args) {
        TopologyManager topology = new TopologyManager();

        Host pc = new Host("PC", "192.168.1.100", "00:1A:2B:3C:4D:5F");
        Router router = new Router("Router", "192.168.1.1", "00:1A:2B:3C:4D:5E", "10.0.0.1");
        DnsServer dns = new DnsServer("DNS", "192.168.1.1", "00:1A:2B:3C:4D:60");
        Host server = new Host("Server", "10.0.0.100", "00:1A:2B:3C:4D:61");

        topology.addDevice(pc);
        topology.addDevice(router);
        topology.addDevice(dns);
        topology.addDevice(server);

        topology.addLink("PC", "Router", 1000, 10, 0.0);
        topology.addLink("Router", "DNS", 1000, 5, 0.0);
        topology.addLink("Router", "Server", 1000, 20, 0.0);

        topology.addSubnet("LAN", "192.168.1", "255.255.255.0");
        topology.addDeviceToSubnet("LAN", "PC");
        topology.addDeviceToSubnet("LAN", "Router");
        topology.addDeviceToSubnet("LAN", "DNS");

        router.addDefaultRoute("0.0.0.0", "eth0");
        dns.addZoneRecord("www.demo.com", "10.0.0.100");

        pc.start();
        router.start();
        dns.start();
        server.start();
    }
}
