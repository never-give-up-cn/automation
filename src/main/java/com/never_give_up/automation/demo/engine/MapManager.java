package com.never_give_up.automation.demo.engine;

import com.never_give_up.automation.demo.config.NetworkConfig;
import com.never_give_up.automation.demo.ui.BuildingZone;
import lombok.Getter;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

@Getter
public class MapManager {
    private final int[][] mapLayout = new int[NetworkConfig.MAP_ROWS][NetworkConfig.MAP_COLS];
    private final String[][] buildingLayout = new String[NetworkConfig.MAP_ROWS][NetworkConfig.MAP_COLS];
    private final Map<String, BuildingZone> buildingZoneMap = new HashMap<>();
    private Point pcFactory;
    private Point serverPos;
    private Point dhcpServerPos;

    // 在 MapManager 中添加调试方法
    public void printDhcpCoords() {
        System.out.println("DHCP_DISC: " + findBuildingCoords("DHCP_DISC"));
        System.out.println("DHCP_SERVER: " + findBuildingCoords("DHCP_SERVER"));
        System.out.println("DHCP_OFFER: " + findBuildingCoords("DHCP_OFFER"));
        System.out.println("DHCP_REQ: " + findBuildingCoords("DHCP_REQ"));
        System.out.println("DHCP_ACK: " + findBuildingCoords("DHCP_ACK"));
        System.out.println("PC_FACTORY: " + findBuildingCoords("PC_FACTORY"));
    }

    public MapManager() {
        initMap();
        initZoneMapping();
    }

    private void initMap() {
        // 初始化矿石资源点
        mapLayout[2][1] = 1;
        mapLayout[3][1] = 1;
        mapLayout[12][1] = 2;
        mapLayout[13][1] = 2;

        // 初始化地图布局和边界
        for (int r = 0; r < NetworkConfig.MAP_ROWS; r++) {
            for (int c = 0; c < NetworkConfig.MAP_COLS; c++) {
                buildingLayout[r][c] = "NONE";
                if (c == 20 || c == 33) mapLayout[r][c] = 9;
            }
        }

        // 源PC和服务器
        buildingLayout[NetworkConfig.MAP_ROWS / 2][3] = "PC_FACTORY";
        pcFactory = new Point(3 * NetworkConfig.TILE_SIZE + NetworkConfig.TILE_SIZE / 2,
                (NetworkConfig.MAP_ROWS / 2) * NetworkConfig.TILE_SIZE + NetworkConfig.TILE_SIZE / 2);
        buildingLayout[NetworkConfig.MAP_ROWS / 2][NetworkConfig.MAP_COLS - 3] = "RX_ST";
        serverPos = findBuildingCoords("RX_ST");

        // DHCP服务器位置
        int dhcpRow = NetworkConfig.MAP_ROWS / 2 - 4;
        buildingLayout[dhcpRow][5] = "DHCP_SERVER";
        dhcpServerPos = findBuildingCoords("DHCP_SERVER");

        // 采矿机
        buildingLayout[2][1] = "MINER_H";
        buildingLayout[3][1] = "MINER_H";
        buildingLayout[12][1] = "MINER_S";
        buildingLayout[13][1] = "MINER_S";

        int startRow = NetworkConfig.MAP_ROWS / 2 - 3;

        // 应用层及传输层封装路径
        buildingLayout[startRow][4] = "DNS_CLIENT";
        buildingLayout[startRow][5] = "DNS_LOCAL";
        buildingLayout[startRow][6] = "DNS_ROOT";
        buildingLayout[startRow][7] = "DNS_AUTH";
        buildingLayout[startRow][8] = "TX_APP";
        buildingLayout[startRow][9] = "T_SP";
        buildingLayout[startRow][10] = "T_DP";
        buildingLayout[startRow][11] = "T_SEQ";
        buildingLayout[startRow][12] = "T_ACK";
        buildingLayout[startRow][13] = "T_CTL";
        buildingLayout[startRow][14] = "T_WIN";
        buildingLayout[startRow][15] = "T_CHK";
        buildingLayout[startRow][16] = "T_CORE";

        // 网络层封装路径
        buildingLayout[startRow][17] = "TX_IPH";
        buildingLayout[startRow][18] = "TX_IP_FRAG";

        // 链路层封装路径
        buildingLayout[startRow][19] = "TX_ARP";
        buildingLayout[startRow][20] = "ETH_DST";
        buildingLayout[startRow][21] = "ETH_SRC";
        buildingLayout[startRow][22] = "ETH_TYPE";
        buildingLayout[startRow][23] = "TX_LLC";
        buildingLayout[startRow][24] = "TX_FCS";

        // DHCP路径
        buildingLayout[dhcpRow][4] = "DHCP_DISC";
        buildingLayout[dhcpRow][6] = "DHCP_OFFER";
        buildingLayout[dhcpRow][7] = "DHCP_REQ";
        buildingLayout[dhcpRow][8] = "DHCP_ACK";

        // 边界网关
        int gatewayRow = NetworkConfig.MAP_ROWS / 2;
        buildingLayout[gatewayRow][20] = "R_LAN";
        buildingLayout[gatewayRow][22] = "R_TAB";
        buildingLayout[gatewayRow][24] = "R_NAT";
        buildingLayout[gatewayRow][26] = "R_WAN";

        // 公网路由器
        buildingLayout[gatewayRow][28] = "ROUTER1";
        buildingLayout[gatewayRow][30] = "ROUTER2";
        buildingLayout[gatewayRow][32] = "ROUTER3";

        // 接收端解封装路径
        int receiveRow = NetworkConfig.MAP_ROWS / 2 - 2;
        buildingLayout[receiveRow][36] = "RX_LLC";
        buildingLayout[receiveRow][38] = "RX_IP";
        buildingLayout[receiveRow][40] = "RX_TCP";
        buildingLayout[receiveRow][42] = "RX_APP";

        // 更多接收端工厂
        buildingLayout[receiveRow][34] = "RX_ETH";
        buildingLayout[receiveRow][35] = "RX_ARP";
        buildingLayout[receiveRow][37] = "RX_FCS";
        buildingLayout[receiveRow][39] = "RX_FRAG";
        buildingLayout[receiveRow][41] = "RX_PORT";

        // 防火墙和安全设备
        int securityRow = NetworkConfig.MAP_ROWS / 2 - 5;
        buildingLayout[securityRow][20] = "FW_IN";
        buildingLayout[securityRow][21] = "FW_OUT";
        buildingLayout[securityRow][22] = "IDS";

        // 队列和缓冲区
        int queueRow = NetworkConfig.MAP_ROWS / 2 - 1;
        buildingLayout[queueRow][22] = "Q_IN";
        buildingLayout[queueRow][23] = "Q_OUT";
        buildingLayout[queueRow][24] = "Q_DROP";

        // 拥塞控制设备
        int congestionRow = NetworkConfig.MAP_ROWS / 2 + 1;
        buildingLayout[congestionRow][14] = "CC_SLOW";
        buildingLayout[congestionRow][15] = "CC_AVOID";
        buildingLayout[congestionRow][16] = "CC_FAST";

        // 五元组和会话
        buildingLayout[startRow][25] = "FIVETUPLE";
        buildingLayout[startRow][26] = "SESSION";

        // 带宽控制
        buildingLayout[gatewayRow][25] = "BW_CTRL";

        // 网络设备
        buildingLayout[2][15] = "SWITCH";
        buildingLayout[3][16] = "HUB";
        buildingLayout[4][17] = "BRIDGE";

        // 子网和链路
        buildingLayout[1][18] = "SUBNET_A";
        buildingLayout[2][19] = "SUBNET_B";
        buildingLayout[5][20] = "LINK_UP";
        buildingLayout[6][21] = "LINK_DOWN";

        // Stage 46-61 建筑
        int newRow = NetworkConfig.MAP_ROWS / 2 - 9;
        buildingLayout[newRow][5] = "TCP_OPTION";
        buildingLayout[newRow][6] = "IP_OPTION";
        buildingLayout[newRow][7] = "ETH_PADDING";
        buildingLayout[newRow][8] = "UDP_CHECKSUM";
        buildingLayout[newRow][9] = "ICMP_ERROR";
        buildingLayout[newRow][10] = "IP_FORWARD";
        buildingLayout[newRow][11] = "TCP_WINDOW";
        buildingLayout[newRow][12] = "TCP_TIMER";
        buildingLayout[newRow][13] = "VLAN_TAG";
        buildingLayout[newRow][14] = "TUNNEL_GRE";
        buildingLayout[newRow][15] = "IGMP_MCAST";
        buildingLayout[newRow][16] = "NDP_DISC";
        buildingLayout[newRow][17] = "DNS_RECURSIVE";
        buildingLayout[newRow][18] = "DHCP_FULL";
        buildingLayout[newRow][19] = "TLS_HANDSHAKE";
        buildingLayout[newRow][20] = "SERIALIZE";

        // Stage 62-75 建筑
        int newRow2 = NetworkConfig.MAP_ROWS / 2 - 10;
        buildingLayout[newRow2][2] = "BIT_STREAM";
        buildingLayout[newRow2][3] = "PHY_CHANNEL";
        buildingLayout[newRow2][4] = "PPPOE";
        buildingLayout[newRow2][5] = "MACSEC";
        buildingLayout[newRow2][6] = "OSPF";
        buildingLayout[newRow2][7] = "BGP";
        buildingLayout[newRow2][8] = "QOS";
        buildingLayout[newRow2][9] = "NAT64";
        buildingLayout[newRow2][10] = "TCP_REASSEMBLY";
        buildingLayout[newRow2][11] = "ATTACK";
        buildingLayout[newRow2][12] = "NTP";
        buildingLayout[newRow2][13] = "SNMP";
        buildingLayout[newRow2][14] = "HTTP23";
        buildingLayout[newRow2][15] = "IPSEC";
    }

    private void initZoneMapping() {
        // 采矿/数据源区域
        buildingZoneMap.put("MINER_H", BuildingZone.ZONE_MINING);
        buildingZoneMap.put("MINER_S", BuildingZone.ZONE_MINING);
        buildingZoneMap.put("PC_FACTORY", BuildingZone.ZONE_MINING);
        buildingZoneMap.put("RX_ST", BuildingZone.ZONE_MINING);
        buildingZoneMap.put("TX_APP", BuildingZone.ZONE_APPLICATION);

        // 传输层
        buildingZoneMap.put("T_SP", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_DP", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_SEQ", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_ACK", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_CTL", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_WIN", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_CHK", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_CORE", BuildingZone.ZONE_TRANSPORT);

        // 网络层
        buildingZoneMap.put("TX_IPH", BuildingZone.ZONE_NETWORK);
        buildingZoneMap.put("TX_IP_FRAG", BuildingZone.ZONE_NETWORK);
        buildingZoneMap.put("RX_IP", BuildingZone.ZONE_NETWORK);

        // 链路层
        buildingZoneMap.put("ETH_DST", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("ETH_SRC", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("ETH_TYPE", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("TX_LLC", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("TX_FCS", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("TX_ARP", BuildingZone.ZONE_LINK);

        // 网关/NAT
        buildingZoneMap.put("R_LAN", BuildingZone.ZONE_GATEWAY);
        buildingZoneMap.put("R_TAB", BuildingZone.ZONE_GATEWAY);
        buildingZoneMap.put("R_NAT", BuildingZone.ZONE_GATEWAY);
        buildingZoneMap.put("R_WAN", BuildingZone.ZONE_GATEWAY);

        // 路由协议
        buildingZoneMap.put("ROUTER1", BuildingZone.ZONE_ROUTER);
        buildingZoneMap.put("ROUTER2", BuildingZone.ZONE_ROUTER);
        buildingZoneMap.put("ROUTER3", BuildingZone.ZONE_ROUTER);

        // DHCP
        buildingZoneMap.put("DHCP_DISC", BuildingZone.ZONE_DHCP);
        buildingZoneMap.put("DHCP_SERVER", BuildingZone.ZONE_DHCP);
        buildingZoneMap.put("DHCP_OFFER", BuildingZone.ZONE_DHCP);
        buildingZoneMap.put("DHCP_REQ", BuildingZone.ZONE_DHCP);
        buildingZoneMap.put("DHCP_ACK", BuildingZone.ZONE_DHCP);

        // DNS
        buildingZoneMap.put("DNS_CLIENT", BuildingZone.ZONE_DNS);
        buildingZoneMap.put("DNS_LOCAL", BuildingZone.ZONE_DNS);
        buildingZoneMap.put("DNS_ROOT", BuildingZone.ZONE_DNS);
        buildingZoneMap.put("DNS_AUTH", BuildingZone.ZONE_DNS);

        // 安全防护
        buildingZoneMap.put("FW_IN", BuildingZone.ZONE_SECURITY);
        buildingZoneMap.put("FW_OUT", BuildingZone.ZONE_SECURITY);
        buildingZoneMap.put("IDS", BuildingZone.ZONE_SECURITY);

        // QoS
        buildingZoneMap.put("Q_IN", BuildingZone.ZONE_QOS);
        buildingZoneMap.put("Q_OUT", BuildingZone.ZONE_QOS);
        buildingZoneMap.put("Q_DROP", BuildingZone.ZONE_QOS);
        buildingZoneMap.put("CC_SLOW", BuildingZone.ZONE_QOS);
        buildingZoneMap.put("CC_AVOID", BuildingZone.ZONE_QOS);
        buildingZoneMap.put("CC_FAST", BuildingZone.ZONE_QOS);
    }

    public Point findBuildingCoords(String tag) {
        for (int r = 0; r < NetworkConfig.MAP_ROWS; r++) {
            for (int c = 0; c < NetworkConfig.MAP_COLS; c++) {
                if (buildingLayout[r][c].equals(tag)) {
                    return new Point(c * NetworkConfig.TILE_SIZE + NetworkConfig.TILE_SIZE / 2,
                            r * NetworkConfig.TILE_SIZE + NetworkConfig.TILE_SIZE / 2);
                }
            }
        }
        return null;
    }

    public BuildingZone getZoneForBuilding(String tag) {
        return buildingZoneMap.get(tag);
    }

    public boolean isValidBuildingPosition(int row, int col) {
        if (row < 0 || row >= NetworkConfig.MAP_ROWS || col < 0 || col >= NetworkConfig.MAP_COLS) {
            return false;
        }
        return buildingLayout[row][col].equals("NONE") && mapLayout[row][col] != 9;
    }

    public void placeBuilding(int row, int col, String buildingType) {
        if (isValidBuildingPosition(row, col)) {
            buildingLayout[row][col] = buildingType;
        }
    }

    public void removeBuilding(int row, int col) {
        if (row >= 0 && row < NetworkConfig.MAP_ROWS && col >= 0 && col < NetworkConfig.MAP_COLS) {
            String current = buildingLayout[row][col];
            if (!current.equals("NONE") && !current.equals("PC_FACTORY") &&
                    !current.equals("RX_ST") && !current.equals("DHCP_SERVER") && mapLayout[row][col] != 9) {
                buildingLayout[row][col] = "NONE";
            }
        }
    }

    public boolean isGatewayBoundary(int col) {
        return col == 20 || col == 33;
    }
}