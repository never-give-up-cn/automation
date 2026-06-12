package com.never_give_up.automation.demo.ui;

import java.awt.Color;

public enum BuildingZone {
    ZONE_APPLICATION("📡 应用层", new Color(0, 200, 200, 50), new Color(0, 200, 200)),
    ZONE_TRANSPORT("🔌 传输层 (TCP/UDP)", new Color(255, 165, 0, 40), new Color(255, 165, 0)),
    ZONE_NETWORK("🌐 网络层 (IP/ICMP)", new Color(255, 255, 0, 40), new Color(255, 255, 0)),
    ZONE_LINK("🔗 链路层 (Ethernet/ARP)", new Color(0, 160, 255, 40), new Color(0, 160, 255)),
    ZONE_PHYSICAL("⚡ 物理层", new Color(200, 200, 200, 40), new Color(200, 200, 200)),
    ZONE_SECURITY("🛡️ 安全防护", new Color(255, 80, 80, 50), new Color(255, 80, 80)),
    ZONE_NETWORK_DEVICE("🔌 网络设备", new Color(100, 100, 200, 40), new Color(100, 100, 200)),
    ZONE_QOS("🎯 QoS/拥塞控制", new Color(100, 200, 100, 40), new Color(100, 200, 100)),
    ZONE_GATEWAY("🚪 网关/NAT", new Color(255, 140, 0, 40), new Color(255, 140, 0)),
    ZONE_ROUTER("📡 路由协议", new Color(200, 100, 0, 40), new Color(200, 100, 0)),
    ZONE_DHCP("📡 DHCP", new Color(100, 100, 255, 40), new Color(100, 100, 255)),
    ZONE_DNS("🌐 DNS", new Color(0, 200, 200, 40), new Color(0, 200, 200)),
    ZONE_IPV6("🌍 IPv6", new Color(80, 80, 180, 40), new Color(80, 80, 180)),
    ZONE_VPN("🔒 VPN隧道", new Color(100, 80, 160, 40), new Color(100, 80, 160)),
    ZONE_ENCRYPTION("🔐 加密证书", new Color(180, 180, 100, 40), new Color(180, 180, 100)),
    ZONE_MONITOR("📊 监控管理", new Color(80, 180, 200, 40), new Color(80, 180, 200)),
    ZONE_MULTICAST("📡 组播路由", new Color(180, 80, 180, 40), new Color(180, 80, 180)),
    ZONE_DIAGNOSTIC("🔧 诊断工具", new Color(150, 150, 150, 40), new Color(150, 150, 150)),
    ZONE_CORE("🏛️ 核心服务", new Color(120, 120, 120, 40), new Color(120, 120, 120)),
    ZONE_MINING("⛏️ 采矿/数据源", new Color(40, 100, 220, 40), new Color(40, 100, 220));

    public final String name;
    public final Color bgColor;
    public final Color borderColor;

    BuildingZone(String name, Color bgColor, Color borderColor) {
        this.name = name;
        this.bgColor = bgColor;
        this.borderColor = borderColor;
    }
}