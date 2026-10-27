package com.never_give_up.automation.demo.engine;

import com.never_give_up.automation.demo.config.NetworkConfig;
import com.never_give_up.automation.demo.ui.BuildingZone;
import com.never_give_up.automation.demo.winModel.DataPacket;
import lombok.Getter;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class MapManager {
    /** 主干传送带覆盖的阶段区间 */
    private static final int MAIN_LAST_STAGE = 45;
    /** 扩展工厂支线覆盖的阶段区间 */
    private static final int EXT_LAST_STAGE = 75;

    private final int[][] mapLayout = new int[NetworkConfig.MAP_ROWS][NetworkConfig.MAP_COLS];
    private final String[][] buildingLayout = new String[NetworkConfig.MAP_ROWS][NetworkConfig.MAP_COLS];
    private final Map<String, BuildingZone> buildingZoneMap = new HashMap<>();
    /** 工位标签 → 坐标 */
    private final Map<String, Point> positions = new LinkedHashMap<>();
    /** 传送带产线模型 */
    private ProductionLine productionLine;
    private Point pcFactory;
    private Point serverPos;
    private Point dhcpServerPos;

    public MapManager() {
        initMap();
        initZoneMapping();
        collectPositions();
        this.productionLine = new ProductionLine(positions);
    }

    /** 扫描布局表，收集所有工位的坐标 */
    private void collectPositions() {
        for (int r = 0; r < NetworkConfig.MAP_ROWS; r++) {
            for (int c = 0; c < NetworkConfig.MAP_COLS; c++) {
                String tag = buildingLayout[r][c];
                if (!"NONE".equals(tag)) {
                    positions.put(tag, new Point(c * NetworkConfig.TILE_SIZE + NetworkConfig.TILE_SIZE / 2,
                            r * NetworkConfig.TILE_SIZE + NetworkConfig.TILE_SIZE / 2));
                }
            }
        }
    }

    private void initMap() {
        // 初始化地图布局
        for (int r = 0; r < NetworkConfig.MAP_ROWS; r++) {
            for (int c = 0; c < NetworkConfig.MAP_COLS; c++) {
                buildingLayout[r][c] = "NONE";
            }
        }

        // ===================== 主干传送带 =====================
        // 协议节点按阶段顺序排成三行蛇形传送带，每行 15 个工位，
        // 相邻工位之间留出两个格子的距离，让传送带和机械臂有地方展开。
        int[] spineRows = {3, 7, 11};
        for (int i = 0; i < MAIN_LAST_STAGE; i++) {
            int rowIdx = i / 15;
            int slot = i % 15;
            int row = spineRows[rowIdx];
            // 第二行反向（蛇形），使换行处正好落在同一列上，传送带垂直折返
            int col = (rowIdx % 2 == 1) ? 30 - slot * 2 : 2 + slot * 2;
            buildingLayout[row][col] = DataPacket.getTagForStage(i + 1);
        }

        // 源 PC：产线起点
        buildingLayout[spineRows[0]][0] = "PC_FACTORY";
        pcFactory = findBuildingCoords("PC_FACTORY");

        // 接收服务器：产线终点
        buildingLayout[spineRows[2]][32] = "RX_ST";
        serverPos = findBuildingCoords("RX_ST");

        // ===================== 扩展工厂支线 =====================
        // 46 阶段之后的工厂挂在接收端应用层旁边，独立成两条支线
        for (int i = MAIN_LAST_STAGE; i < EXT_LAST_STAGE; i++) {
            int k = i - MAIN_LAST_STAGE;
            int row;
            int col;
            if (k < 16) {
                row = 15;
                col = 30 - k * 2;
            } else {
                row = 17;
                col = (k - 16) * 2;
            }
            buildingLayout[row][col] = DataPacket.getTagForStage(i + 1);
        }

        // ===================== DHCP 支线 =====================
        // PC → Request → Discover → Server → Offer → ACK
        buildingLayout[5][2] = "DHCP_REQ";
        buildingLayout[5][4] = "DHCP_DISC";
        buildingLayout[5][6] = "DHCP_SERVER";
        buildingLayout[5][8] = "DHCP_OFFER";
        buildingLayout[5][10] = "DHCP_ACK";
        dhcpServerPos = findBuildingCoords("DHCP_SERVER");

        // ===================== 独立设备区 =====================
        // 不在主干传送带上的网络设备，单独摆在厂区一角
        buildingLayout[0][32] = "SWITCH";
        buildingLayout[0][34] = "HUB";
        buildingLayout[0][36] = "BRIDGE";
        buildingLayout[0][38] = "SUBNET_A";
        buildingLayout[0][40] = "SUBNET_B";
        buildingLayout[1][32] = "LINK_UP";
        buildingLayout[1][34] = "LINK_DOWN";
        buildingLayout[1][36] = "IDS";
        buildingLayout[1][38] = "CC_SLOW";
        buildingLayout[1][40] = "CC_AVOID";
        buildingLayout[2][40] = "CC_FAST";

        // ===================== 矿区 =====================
        // 可放置矿机的资源点
        mapLayout[18][32] = 1;
        mapLayout[19][32] = 1;
        mapLayout[18][34] = 2;
        mapLayout[19][34] = 2;
        buildingLayout[18][32] = "MINER_H";
        buildingLayout[19][32] = "MINER_H";
        buildingLayout[18][34] = "MINER_S";
        buildingLayout[19][34] = "MINER_S";

        // ===================== 公网边界 =====================
        // 路由器所在的区域不允许放置其它设备
        for (int r : new int[]{7, 11}) {
            for (int c = 0; c <= 8; c++) {
                mapLayout[r][c] = 9;
            }
        }
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
        Point cached = positions.get(tag);
        if (cached != null) {
            return cached;
        }
        // 玩家后放置的工厂不在初始坐标表里，退回扫描布局表
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

    /** 根据坐标反查工位标签（用于判断报文是从哪台组装机出发的） */
    public String findTagAt(Point p) {
        if (p == null) {
            return null;
        }
        for (Map.Entry<String, Point> entry : positions.entrySet()) {
            if (entry.getValue().equals(p)) {
                return entry.getKey();
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

    /** 公网边界：路由器所在的工位不允许被拆除 */
    public boolean isGatewayBoundary(int row, int col) {
        return (row == 7 || row == 11) && col >= 0 && col <= 8;
    }
}