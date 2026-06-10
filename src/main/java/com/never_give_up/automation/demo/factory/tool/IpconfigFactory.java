package com.never_give_up.automation.demo.factory.tool;

/** ipconfig / ifconfig 网络配置查看 */
public class IpconfigFactory {
    private String ipAddr = "192.168.1.100";
    private String mask = "255.255.255.0";
    private String gateway = "192.168.1.1";
    private String dns = "223.5.5.5";

    public String getConfigInfo() {
        return String.format("IP:%s Mask:%s Gateway:%s DNS:%s", ipAddr, mask, gateway, dns);
    }

    public void reset() {
        ipAddr = "192.168.1.100";
        mask = "255.255.255.0";
        gateway = "192.168.1.1";
        dns = "223.5.5.5";
    }
}