package com.never_give_up.automation.demo.ui;

import com.never_give_up.automation.demo.config.NetworkConfig;
import com.never_give_up.automation.demo.engine.GameEngine;
import com.never_give_up.automation.demo.engine.MapManager;
import com.never_give_up.automation.demo.engine.Material;
import com.never_give_up.automation.demo.engine.ProductionLine;
import com.never_give_up.automation.demo.winModel.DataPacket;
import com.never_give_up.automation.demo.winModel.OreCart;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 厂区画布（俯视图）：把协议栈画成一条自动化产线。
 * <p>
 * 产线结构（每种东西一条自己的传送带）：
 * </p>
 * <ol>
 *   <li><b>主干传送带 C2</b>：工件（数据包）骑在传送带上向下一站流去，
 *       所有工位（节点）都摆在传送带<b>外侧</b>，不再骑在带上；</li>
 *   <li><b>供给传送带 C1</b>：TCP / IP / ETH 每种头部块各有一条专属供给带，
 *       顶端是「生成机」，源源不断地把料块送到取件点；
 *       机械臂来不及取时，料块就在供给带上<b>排队</b>；</li>
 *   <li><b>组装机 + 通用机械臂</b>：<b>每一个节点</b>都配有一台组装机和一条
 *       通用机械臂。数据经过节点时，机械臂把工件从传送带上抓起来送进机器，
 *       机器加工完再由机械臂放回传送带，抓不过来就在传送带上排队；</li>
 *   <li><b>拆包工位</b>：接收端把 TCP / IP / ETH 头部拆下来，机械臂从工件上
 *       抓走头部块，送进拆包机，再丢进收纳箱，工件随之变薄。</li>
 * </ol>
 */
public class GameCanvas extends JPanel {

    // ===== 配色（俯视车间主题）=====
    private static final Color FLOOR = new Color(0xF4, 0xF7, 0xF6);
    private static final Color FLOOR_LINE = new Color(0xE2, 0xE8, 0xEA);

    private static final Color BELT_TOP = new Color(0x4A, 0x55, 0x68);
    private static final Color BELT_BOTTOM = new Color(0x2D, 0x37, 0x48);
    private static final Color BELT_RAIL = new Color(0x1A, 0x20, 0x2C);
    private static final Color BELT_TEX = new Color(0x8F, 0xA3, 0xB0);

    private static final Color MACHINE_TOP = new Color(0xF2, 0xF5, 0xF7);
    private static final Color MACHINE_BOTTOM = new Color(0xC3, 0xCE, 0xD3);
    private static final Color MACHINE_STROKE = new Color(0x63, 0x6E, 0x72);

    private static final Color ARM_TOP = new Color(0xF7, 0xB7, 0x31);
    private static final Color ARM_BOTTOM = new Color(0xD3, 0x54, 0x00);
    private static final Color ARM2_TOP = new Color(0xE6, 0x7E, 0x22);
    private static final Color ARM2_BOTTOM = new Color(0xA8, 0x43, 0x00);
    private static final Color ARM_JOINT = new Color(0xA0, 0x40, 0x00);
    private static final Color ARM_JAW = new Color(0x71, 0x80, 0x96);

    private static final Color GLOW = new Color(0xFE, 0xCA, 0x57);
    private static final Color SPARK = new Color(0xFE, 0xCA, 0x57);
    private static final Color OK_GREEN = new Color(0x27, 0xAE, 0x60);
    private static final Color BUSY_RED = new Color(0xE7, 0x4C, 0x3C);
    private static final Color BUSY_ORANGE = new Color(0xF3, 0x9C, 0x12);
    private static final Color BIN_COLOR = new Color(0xBD, 0xC3, 0xC7);

    /** 三种封装头的配色（与参考实现一致） */
    private static final Color TCP_COLOR = new Color(0x34, 0x98, 0xDB);
    private static final Color TCP_DARK = new Color(0x29, 0x80, 0xB9);
    private static final Color IP_COLOR = new Color(0x2E, 0xCC, 0x71);
    private static final Color IP_DARK = new Color(0x27, 0xAE, 0x60);
    private static final Color ETH_COLOR = new Color(0x9B, 0x59, 0xB6);
    private static final Color ETH_DARK = new Color(0x8E, 0x44, 0xAD);

    /** 协议头的叠放顺序：TCP → IP → ETH */
    private static final String[] LAYER_ORDER = {"TCP", "IP", "ETH"};

    private static final int BELT_HALF_WIDTH = 18;
    private static final double TEX_PERIOD = 42;
    /** 传送带纹理滚动速度（像素/毫秒），约 15 像素/帧 */
    private static final double TEX_SPEED = 0.45;

    /** 供给传送带宽度 */
    private static final int SUPPLY_BELT_W = 40;
    /** 供给传送带长度 */
    private static final int SUPPLY_BELT_LEN = 104;
    /** 生成机 / 收纳箱尺寸 */
    private static final int GEN_SIZE = 76;
    /** 组装机尺寸 */
    private static final int MACHINE_W = 76;
    private static final int MACHINE_H = 56;
    /** 组装机相对主干传送带的纵向偏移：机器摆在传送带外侧（上方） */
    private static final int MACHINE_OFFSET = -76;
    /** 组装机进料口相对主干中心的纵向距离（机器在传送带上方，进料口也在上方） */
    private static final int MACHINE_MOUTH_DY = MACHINE_OFFSET + MACHINE_H / 2 - 8;
    /** 机械臂两节长度 */
    private static final double ARM_L1 = 78;
    private static final double ARM_L2 = 78;
    /** 协议头工位机械臂一个完整周期（毫秒） */
    private static final long ARM_CYCLE_MS = 720;
    /** 通用工位机械臂一个完整周期（毫秒）：每个节点都要过一遍，节奏要快 */
    private static final long PROCESS_CYCLE_MS = 300;
    /** 又取料又加工的工位（需求是协议头原料的普通节点）一个周期（毫秒） */
    private static final long MIX_CYCLE_MS = 560;
    /** 生成机出料间隔（毫秒）：比机械臂一个周期短，供给带上就会排队 */
    private static final long GEN_INTERVAL = 320;
    /** 供给带上允许同时排队的料块数 */
    private static final int MAX_QUEUE = 4;
    /** 工件在工位上等机械臂的最长帧数（超时强制放行，避免产线卡死） */
    private static final int ARM_WAIT_LIMIT = 300;
    /** 供给带上料块之间的间距 */
    private static final double QUEUE_GAP = 30;
    /** 传送带上排队工件之间的间距 */
    private static final double SLOT_DIST = 38;
    /** 排队工件挪动速度（像素/帧） */
    private static final double QUEUE_MOVE_SPEED = 7;
    /** 工件的净荷宽度 */
    private static final int PAYLOAD_W = 40;
    /** 工件上一个协议头块的宽度 */
    private static final int HEAD_W = 14;
    /** 工件净荷高度 */
    private static final int PAYLOAD_H = 28;

    /** 工位类型：协议头工位（有专属供给带 / 回收箱） */
    private static final int KIND_HEADER = 0;
    /** 工位类型：通用加工工位（每个节点都有，机械臂抓工件进机加工） */
    private static final int KIND_PROCESS = 1;

    /** 取料方式：机械臂去专属供给带取原料块，加工完钉到工件上 */
    private static final int MODE_SEAL = 0;
    /** 取料方式：机械臂从工件身上把原料块拆下来，丢进回收箱 */
    private static final int MODE_STRIP = 1;
    /** 取料方式：工件本身就是原料，机械臂把工件抓进机器加工再放回传送带 */
    private static final int MODE_PROCESS = 2;
    /** 取料方式：普通节点也要协议头原料——先去供给带取料送进机器，再把工件抓进机器加工 */
    private static final int MODE_MIX = 3;

    /**
     * 头部产线定义：工位标签、协议层、是否为拆包工位。
     * <p>封装发生在 T_CORE(TCP) / TX_IPH(IP) / ETH_TYPE(ETH)，
     * 解封装发生在 RX_ETH / RX_IP / RX_TCP。</p>
     */
    private static final Object[][] FEEDER_DEFS = {
            {"T_CORE", "TCP", Boolean.FALSE},
            {"TX_IPH", "IP", Boolean.FALSE},
            {"ETH_TYPE", "ETH", Boolean.FALSE},
            {"RX_ETH", "ETH", Boolean.TRUE},
            {"RX_IP", "IP", Boolean.TRUE},
            {"RX_TCP", "TCP", Boolean.TRUE},
    };

    private final GameEngine engine;
    private final MapManager mapManager;
    private final List<DataPacket> dataPackets;
    private final List<OreCart> oreCarts;

    private double scale = 1.0;
    private int viewOffsetX = 0;
    private int viewOffsetY = 0;
    private int serverBufferCount = 0;

    /** 动画时钟（毫秒） */
    private long animTime = 0;
    /** 工位 → 加工结束时间戳（用于炉火闪烁与状态灯） */
    private final Map<String, Long> machineBusyUntil = new HashMap<>();
    /** 工位 → 开始加工的时间戳 */
    private final Map<String, Long> machineBusySince = new HashMap<>();
    /** 火花粒子 */
    private final List<Spark> sparks = new ArrayList<>();

    /** 产线上的全部工位（每个工位都有一台组装机 + 一条通用机械臂） */
    private final List<Station> stations = new ArrayList<>();
    /** 报文 ID → 已装上的协议层（工件随封装变厚、随解封装变薄） */
    private final Map<Integer, Set<String>> packetLayers = new HashMap<>();
    /** 报文 ID → 上一帧所在工位（用于侦测工件抵达新工位） */
    private final Map<Integer, String> lastStation = new HashMap<>();
    /** 报文 ID → 上一帧位置（用于判断工件是从哪个方向进站的） */
    private final Map<Integer, Point> lastPos = new HashMap<>();
    /** 报文 ID → 当前被机械臂夹着的位置（x, y, 是否在机器里藏起来了） */
    private final Map<Integer, double[]> carriedPos = new HashMap<>();
    /** 工位坐标 → 工位标签（用来侦测工件「路过」某个节点） */
    private final Map<Long, String> stationAtPos = new HashMap<>();
    /** 报文 ID → 已经被机械臂操作过的工位（同一个工位不重复入队） */
    private final Map<Integer, Set<String>> handledStations = new HashMap<>();
    /** 本帧还活着的报文 ID */
    private final Set<Integer> aliveIds = new LinkedHashSet<>();

    /** 火花粒子 */
    private static class Spark {
        double x, y, vx, vy, life, max;
    }

    /**
     * 供给传送带上的一个料块。
     * <p>一条带子只运输一种物料，料块自带物料属性，机械臂按筛选器取料。</p>
     */
    private static class SupplyItem {
        /** 物料种类 */
        final String material;
        /** 在供给带上的 y 坐标 */
        double y;

        SupplyItem(String material, double y) {
            this.material = material;
            this.y = y;
        }
    }

    /**
     * 一个工位：组装机 + 通用机械臂。
     * <p>协议头工位额外有一条专属供给带（生成机）或一个回收箱。</p>
     */
    private static class Station {
        /** 工位标签 */
        String tag;
        /** 工位类型 */
        int kind;
        /** 协议层：TCP / IP / ETH（仅协议头工位） */
        String layer;
        /** 是不是拆包工位（把头部从工件上取走） */
        boolean strip;
        Color color;
        Color dark;

        /** 设备需求：机器要消耗的原料物料（机械臂按需求去对应传送带上取料） */
        String demand = Material.PAYLOAD;
        /** 机械臂筛选器 · 白名单（留空 = 全部放行） */
        final Set<String> whitelist = new LinkedHashSet<>();
        /** 机械臂筛选器 · 黑名单 */
        final Set<String> blacklist = new LinkedHashSet<>();

        /** 工位（主干上）坐标 */
        Point trunk;
        /** 组装机中心（主干外侧） */
        Point machine;
        /** 机械臂底座 */
        Point armBase;
        /** 机械臂待机位 */
        Point home;
        /** 组装机进料口 */
        Point mouth;

        /** 供给传送带上沿 / 下沿（封装工位） */
        Point supplyTop;
        Point supplyBottom;
        /** 供给带上的取件点 y */
        double pickY;
        /** 生成机中心（封装工位） */
        Point generator;
        /** 收纳箱中心（拆包工位） */
        Point bin;

        /** 供给带上排队的料块（最前面的一块在最下） */
        final List<SupplyItem> supplyItems = new ArrayList<>();
        /** 上次出料时间 */
        long genAt;
        /** 等待机械臂处理的工件（队首 = 下一个被加工的） */
        final Deque<DataPacket> waiting = new ArrayDeque<>();

        /** 当前动作周期开始时间，-1 表示空闲 */
        long cycleStart = -1;
        /** 本轮动作对应的工件 */
        DataPacket packet;
        /** 本轮动作的取件点 / 出料点（动作开始时锁定，避免工件走动导致手臂乱晃） */
        Point pickPoint;
        Point outPoint;
        /** 是否已经把头部从工件上取走（拆包） */
        boolean grabbed;
        /** 是否已经放行工件 */
        boolean released;

        /** 机械臂当前姿态（每帧由 updateEffects 计算，绘制时直接用） */
        double poseX;
        double poseY;
        /** 夹爪里夹着东西（原料块或工件） */
        boolean poseGrip;
        /** 夹爪里夹着原料块 */
        boolean poseBlock;
        /** 原料块已经送进机器里，看不见 */
        boolean poseBlockHidden;
        /** 夹爪里夹着工件 */
        boolean poseWork;
        /** 工件已经送进机器里，看不见 */
        boolean poseWorkHidden;

        /** 排队方向：工件从工位往这个方向往回退 */
        double qx = -1;
        double qy = 0;
    }

    public GameCanvas(GameEngine engine, MapManager mapManager,
                      List<DataPacket> dataPackets,
                      List<OreCart> oreCarts) {
        this.engine = engine;
        this.mapManager = mapManager;
        this.dataPackets = dataPackets;
        this.oreCarts = oreCarts;
        setBackground(FLOOR);
        buildStations();
        setPreferredSize(new Dimension(
                (int) (NetworkConfig.MAP_COLS * NetworkConfig.TILE_SIZE * scale),
                (int) (NetworkConfig.MAP_ROWS * NetworkConfig.TILE_SIZE * scale)
        ));
        // 右键工位 → 弹出机械臂筛选器设置框
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!SwingUtilities.isRightMouseButton(e)) {
                    return;
                }
                String tag = stationAtScreen(e.getX(), e.getY());
                if (tag != null && filterListener != null) {
                    filterListener.onFilterRequested(tag);
                }
            }
        });
    }

    /**
     * 建立产线上的所有工位。
     * <p>协议头工位带专属供给带 / 回收箱，其余节点一律配一台通用机械臂：
     * 数据经过这个节点时，机械臂把工件抓进机器加工，再放回传送带。</p>
     * <p>每个工位都声明自己的<b>需求</b>（要消耗哪种原料）：
     * 封装工位要 TCP / IP / ETH 头（原料来自自己的供给带 C1），
     * 拆包工位要把工件身上的头拆下来，普通节点直接消耗工件本体。</p>
     */
    private void buildStations() {
        // 1. 协议头工位（封装 / 拆包）
        for (Object[] def : FEEDER_DEFS) {
            String station = (String) def[0];
            String layer = (String) def[1];
            boolean strip = (Boolean) def[2];

            Point trunk = mapManager.findBuildingCoords(station);
            if (trunk == null) {
                continue;
            }

            Station st = new Station();
            st.tag = station;
            st.kind = KIND_HEADER;
            st.layer = layer;
            st.strip = strip;
            st.demand = layer;
            st.color = colorOfLayer(layer);
            st.dark = darkOfLayer(layer);
            setupGeometry(st, trunk);
            stationAtPos.put(posKey(trunk.x, trunk.y), st.tag);

            if (strip) {
                // 拆包工位：组装机上方是收纳箱（回收下来的头部块丢这里）
                st.bin = new Point(trunk.x, st.machine.y - GEN_SIZE - 6);
            } else {
                // 封装工位：组装机上方是专属供给传送带 C1，供给带顶端是生成机
                ensureSupplyBelt(st);
            }
            stations.add(st);
        }

        // 2. 产线上的其余节点：都配一台通用机械臂
        ProductionLine line = mapManager.getProductionLine();
        if (line == null) {
            return;
        }
        for (String tag : line.getAllTags()) {
            if (stationFor(tag) != null) {
                continue;
            }
            Point trunk = mapManager.findBuildingCoords(tag);
            if (trunk == null) {
                continue;
            }
            Station st = new Station();
            st.tag = tag;
            st.kind = KIND_PROCESS;
            st.layer = "";
            st.strip = false;
            st.demand = Material.PAYLOAD;
            st.color = MACHINE_STROKE;
            st.dark = MACHINE_STROKE;
            setupGeometry(st, trunk);
            stationAtPos.put(posKey(trunk.x, trunk.y), st.tag);
            stations.add(st);
        }
    }

    /** 给工位装一条专属供给带 C1（一条带子只运一种物料） */
    private void ensureSupplyBelt(Station st) {
        if (st.supplyBottom != null) {
            return;
        }
        st.supplyBottom = new Point(st.trunk.x, st.machine.y - MACHINE_H / 2 - 4);
        st.supplyTop = new Point(st.trunk.x, st.supplyBottom.y - SUPPLY_BELT_LEN);
        st.generator = new Point(st.trunk.x, st.supplyTop.y - GEN_SIZE / 2 - 2);
        st.pickY = st.supplyBottom.y - 16;
    }

    /** 工位坐标索引的 key */
    private static long posKey(int x, int y) {
        return ((long) x << 32) | (y & 0xffffffffL);
    }

    /** 工位的通用几何：组装机摆在传送带外侧，机械臂底座在机器右侧 */
    private void setupGeometry(Station st, Point trunk) {
        st.trunk = trunk;
        // 组装机摆在主干传送带的外侧（上方），传送带从机器下方经过
        st.machine = new Point(trunk.x, trunk.y + MACHINE_OFFSET);
        // 机械臂底座：紧挨着组装机右侧（离本工位近一点，别跟隔壁工位混在一起）
        st.armBase = new Point(trunk.x + 66, st.machine.y - 24);
        st.home = new Point(trunk.x + 30, st.machine.y - 78);
        // 进料口：机器下沿正对传送带
        st.mouth = new Point(trunk.x, trunk.y + MACHINE_MOUTH_DY);
        // 排队方向默认往回退（上游方向的反向）
        double[] up = mapManager.getProductionLine() == null ? null
                : mapManager.getProductionLine().getUpstreamDir(st.tag);
        if (up != null) {
            st.qx = -up[0];
            st.qy = -up[1];
        }
    }

    private static Color colorOfLayer(String layer) {
        switch (layer) {
            case "TCP":
                return TCP_COLOR;
            case "IP":
                return IP_COLOR;
            default:
                return ETH_COLOR;
        }
    }

    private static Color darkOfLayer(String layer) {
        switch (layer) {
            case "TCP":
                return TCP_DARK;
            case "IP":
                return IP_DARK;
            default:
                return ETH_DARK;
        }
    }

    public void updateServerBuffer(int bufferCount) {
        this.serverBufferCount = bufferCount;
    }

    // ===================== 设备需求 + 机械臂筛选器 =====================

    /** 右键点到某个工位时的回调（用来弹出机械臂筛选器设置框） */
    public interface StationFilterListener {
        /**
         * @param tag 被右键的工位标签
         */
        void onFilterRequested(String tag);
    }

    /** 机械臂筛选器设置框的回调 */
    private StationFilterListener filterListener;

    /** 设置右键回调 */
    public void setStationFilterListener(StationFilterListener listener) {
        this.filterListener = listener;
    }

    /**
     * 屏幕坐标找工位（右键设置机械臂筛选器用）。
     *
     * @param sx 画布内的屏幕 x
     * @param sy 画布内的屏幕 y
     * @return 命中的工位标签；没命中返回 null
     */
    public String stationAtScreen(int sx, int sy) {
        double mx = (sx - viewOffsetX) / scale;
        double my = (sy - viewOffsetY) / scale;
        String best = null;
        double bestDist = 46 * 46;
        for (Station st : stations) {
            double dx = st.trunk.x - mx;
            double dy = st.trunk.y - my;
            double d = dx * dx + dy * dy;
            if (d < bestDist) {
                bestDist = d;
                best = st.tag;
            }
        }
        return best;
    }

    /** 产线上全部工位标签 */
    public List<String> getStationTags() {
        List<String> tags = new ArrayList<>();
        for (Station st : stations) {
            tags.add(st.tag);
        }
        return tags;
    }

    /** 某个工位声明的原料需求 */
    public String getStationDemand(String tag) {
        Station st = stationFor(tag);
        return st == null ? null : st.demand;
    }

    /**
     * 改一个工位的原料需求。
     * <p>改成协议头原料时，机械臂会去这条工位专属的供给带 C1 上取料，
     * 供给带上只运输这一种物料；改回工件本体则回到「抓工件进机加工」。</p>
     */
    public void setStationDemand(String tag, String material) {
        Station st = stationFor(tag);
        if (st == null || material == null) {
            return;
        }
        // 协议头工位只消耗协议头原料
        if (st.kind == KIND_HEADER && !Material.isHeader(material)) {
            return;
        }
        if (material.equals(st.demand)) {
            return;
        }
        st.demand = material;
        if (Material.isHeader(material)) {
            // 料块跟着需求走：这条带子上只运输这一种物料
            st.layer = material;
            st.color = colorOfLayer(material);
            st.dark = darkOfLayer(material);
            ensureSupplyBelt(st);
        } else {
            st.layer = "";
            if (st.kind == KIND_PROCESS) {
                st.color = MACHINE_STROKE;
                st.dark = MACHINE_STROKE;
            }
        }
        // 需求变了，带上原有的料块就不合用了，清掉重新生产
        st.supplyItems.clear();
        st.genAt = 0;
    }

    /** 机械臂筛选器 · 白名单（留空 = 全部放行） */
    public Set<String> getStationWhitelist(String tag) {
        Station st = stationFor(tag);
        return st == null ? new LinkedHashSet<>() : new LinkedHashSet<>(st.whitelist);
    }

    /** 机械臂筛选器 · 黑名单 */
    public Set<String> getStationBlacklist(String tag) {
        Station st = stationFor(tag);
        return st == null ? new LinkedHashSet<>() : new LinkedHashSet<>(st.blacklist);
    }

    /**
     * 给机械臂设置筛选器。
     *
     * @param whitelist 白名单（只有名单里的物料才取，空 = 全部放行）
     * @param blacklist 黑名单（名单里的物料一律不取）
     */
    public void setStationFilter(String tag, Collection<String> whitelist, Collection<String> blacklist) {
        Station st = stationFor(tag);
        if (st == null) {
            return;
        }
        st.whitelist.clear();
        st.blacklist.clear();
        if (whitelist != null) {
            st.whitelist.addAll(whitelist);
        }
        if (blacklist != null) {
            st.blacklist.addAll(blacklist);
        }
    }

    /** 清掉筛选器，恢复成「来什么取什么」 */
    public void resetStationFilter(String tag) {
        setStationFilter(tag, null, null);
    }

    /**
     * 机械臂的筛选规则：白名单优先，黑名单兜底。
     *
     * @return true 表示这条机械臂愿意取这种物料
     */
    public boolean acceptsMaterial(String tag, String material) {
        Station st = stationFor(tag);
        if (st == null) {
            return false;
        }
        return accepts(st, material);
    }

    /**
     * 某工位的供给带上堆了多少料块（机械臂取不过来就积压在带上）。
     */
    public int getSupplyBacklog(String tag) {
        Station st = stationFor(tag);
        return st == null ? 0 : st.supplyItems.size();
    }

    private static boolean accepts(Station st, String material) {
        if (material == null) {
            return false;
        }
        if (!st.whitelist.isEmpty() && !st.whitelist.contains(material)) {
            return false;
        }
        return !st.blacklist.contains(material);
    }

    /**
     * 报文抵达某台组装机：机器亮灯、炉火闪烁。
     * <p>每个节点都有机械臂，工件先排进机械臂的等候队列，
     * 等机械臂把上一件活干完再轮到它（抓不过来就排队）。</p>
     */
    public void markWorking(String tag, DataPacket packet) {
        if (tag == null) {
            return;
        }
        long now = System.currentTimeMillis();
        machineBusySince.put(tag, now);
        machineBusyUntil.put(tag, now + 900);

        Point pos = mapManager.findBuildingCoords(tag);
        if (pos != null) {
            // 火花在传送带外侧的机器上飞
            Point machinePos = isOnLine(tag) ? new Point(pos.x, pos.y + MACHINE_OFFSET) : pos;
            for (int i = 0; i < 6; i++) {
                Spark s = new Spark();
                s.x = machinePos.x + (Math.random() - 0.5) * 30;
                s.y = machinePos.y + (Math.random() - 0.5) * 16;
                s.vx = (Math.random() - 0.5) * 150;
                s.vy = -Math.random() * 150 - 40;
                s.max = 0.25 + Math.random() * 0.35;
                s.life = s.max;
                sparks.add(s);
            }
        }

        Station st = stationFor(tag);
        if (st == null || packet == null || packet.isArrived() || packet.isDropped()) {
            return;
        }
        // 同一个工位只操作一次：工件路过这个节点就由这台机械臂加工一遍
        Set<String> visited = handledStations.computeIfAbsent(packet.getCartId(),
                k -> new LinkedHashSet<>());
        if (visited.contains(tag)) {
            return;
        }
        visited.add(tag);
        // 筛选器把这种物料挡掉了：这台设备不收，工件直接过站
        if (!accepts(st, st.demand)) {
            return;
        }
        // 已经排过队就不重复入队
        for (DataPacket q : st.waiting) {
            if (q.getCartId() == packet.getCartId()) {
                return;
            }
        }
        if (st.kind == KIND_HEADER) {
            Set<String> layers = layersOf(packet);
            boolean applicable = st.strip
                    // 拆包：工件身上确实带着这一层才需要拆
                    ? layers.contains(st.layer)
                    // 封装：协议栈里有这一层、但工件上还没装上
                    : (hasLayer(packet, st.layer) && !layers.contains(st.layer));
            if (!applicable) {
                return;
            }
        }
        st.waiting.add(packet);
        // 工件停在工位上等机械臂（抓不过来就排队，最多等 ARM_WAIT_LIMIT 帧）
        packet.waitForArm(ARM_WAIT_LIMIT);
        // 记下来料方向：排队时工件沿着来路往回退
        updateQueueDir(st, packet);
    }

    /** 依据工件进站的方向，决定排队时往哪边退 */
    private void updateQueueDir(Station st, DataPacket packet) {
        Point prev = lastPos.get(packet.getCartId());
        if (prev == null || st.trunk == null) {
            return;
        }
        double dx = st.trunk.x - prev.x;
        double dy = st.trunk.y - prev.y;
        double d = Math.hypot(dx, dy);
        if (d < 6) {
            return;
        }
        st.qx = -dx / d;
        st.qy = -dy / d;
    }

    private static boolean hasLayer(DataPacket packet, String layer) {
        if (packet == null) {
            return false;
        }
        switch (layer) {
            case "TCP":
                return packet.isHasTcp();
            case "IP":
                return packet.isHasIp();
            default:
                return packet.isHasEther();
        }
    }

    /** 该工位是否在主干传送带上（决定机器是否摆在传送带外侧） */
    private boolean isOnLine(String tag) {
        ProductionLine line = mapManager.getProductionLine();
        return line != null && line.isOnLine(tag);
    }

    /** 推进特效（由主循环每帧调用） */
    public void updateEffects() {
        animTime += 33;
        long now = System.currentTimeMillis();

        // 侦测工件抵达 / 路过新工位 → 点亮对应的组装机 / 排进机械臂队列
        Set<Integer> alive = new LinkedHashSet<>();
        aliveIds.clear();
        for (DataPacket p : dataPackets) {
            alive.add(p.getCartId());
            aliveIds.add(p.getCartId());
            Set<String> visited = handledStations.get(p.getCartId());
            boolean firstSight = visited == null || visited.isEmpty();

            // 工件当前所在的节点（模型抵达节点时会更新）
            String tag = p.getCurrentTag();
            if (tag != null) {
                // 源站只是工件诞生的地方，不算「经过节点」，不用机械臂加工
                boolean born = firstSight && "PC_FACTORY".equals(tag);
                if (!born) {
                    markWorking(tag, p);
                }
            }

            // 工件「路过」的节点：模型只在终点更新 currentTag，
            // 中途经过的工位靠坐标识别，这些节点同样要由机械臂操作
            String pass = stationAtPos.get(
                    posKey((int) Math.round(p.getX()), (int) Math.round(p.getY())));
            if (pass != null && (visited == null || !visited.contains(pass))) {
                boolean born = firstSight && "PC_FACTORY".equals(pass);
                if (!born) {
                    markWorking(pass, p);
                }
            }
        }
        lastStation.keySet().removeIf(id -> !alive.contains(id));
        handledStations.keySet().removeIf(id -> !alive.contains(id));

        // 每个工位：供给带出料 + 机械臂开工 + 排队工件就位
        carriedPos.clear();
        for (Station st : stations) {
            if (st.supplyBottom != null) {
                advanceSupply(st, now);
            }
            updateCycle(st, now);
            // 先把排队的工件挪到位，机械臂才好抓
            layoutQueue(st, alive);
            tryStartCycle(st, now);
            computePose(st, stationProgress(st, now));
            // 通用工位：工件被机械臂夹着走，位置跟着夹爪走（computePose 已经同步过坐标）
            if (st.poseWork && st.packet != null && st.cycleStart > 0) {
                carriedPos.put(st.packet.getCartId(),
                        new double[]{st.poseX, st.poseY, st.poseWorkHidden ? 1 : 0});
            }
        }

        // 记录每个工件本帧的位置，下一帧用来判断它是从哪个方向进站的
        for (DataPacket p : dataPackets) {
            lastPos.put(p.getCartId(), new Point((int) p.getX(), (int) p.getY()));
        }

        for (int i = sparks.size() - 1; i >= 0; i--) {
            Spark s = sparks.get(i);
            if (s.life <= 0) {
                sparks.remove(i);
                continue;
            }
            s.vy += 420 * 0.033;
            s.x += s.vx * 0.033;
            s.y += s.vy * 0.033;
            s.life -= 0.033;
        }
        // 清理早已空闲的机器状态
        machineBusySince.entrySet().removeIf(e -> now - e.getValue() > 2000);
        machineBusyUntil.entrySet().removeIf(e -> e.getValue() < now - 500);
        // 清理已经消失的报文记录
        packetLayers.keySet().removeIf(id -> !alive.contains(id));
        lastPos.keySet().removeIf(id -> !alive.contains(id));
    }

    /**
     * 供给传送带：生成机不断出料，料块顺着带子往下走。
     * <p>一条带子只运输一种物料，生成机只生产这条带子对应的原料；
     * 机械臂来不及取（或者被筛选器挡掉）时，料块就在带上排队。</p>
     */
    private void advanceSupply(Station f, long now) {
        if (f.supplyBottom == null) {
            return;
        }
        if (f.genAt == 0) {
            f.genAt = now;
        }
        // 生成机出料
        while (now - f.genAt >= GEN_INTERVAL) {
            f.genAt += GEN_INTERVAL;
            double last = f.supplyItems.isEmpty() ? Double.MAX_VALUE
                    : f.supplyItems.get(f.supplyItems.size() - 1).y;
            if (f.supplyItems.size() < MAX_QUEUE && last > f.supplyTop.y + 46) {
                f.supplyItems.add(new SupplyItem(f.demand, f.supplyTop.y + 18.0));
            }
        }
        // 传送带把料块往下送：最前面的一块停在取件点，后面的依次排在他后面
        double step = 0.32 * 33;
        for (int i = 0; i < f.supplyItems.size(); i++) {
            SupplyItem item = f.supplyItems.get(i);
            double limit = (i == 0) ? f.pickY : f.supplyItems.get(i - 1).y - QUEUE_GAP;
            double y = item.y;
            if (y < limit) {
                y = Math.min(limit, y + step);
            }
            item.y = y;
        }
    }

    /**
     * 机械臂一轮动作的关键节点推进（与帧率无关）。
     * <p>抓取、钉头这些动作即使某一帧掉帧越过关键点，也一定会执行到。</p>
     */
    private void updateCycle(Station st, long now) {
        if (st.cycleStart <= 0) {
            return;
        }
        double u = (now - st.cycleStart) / (double) cycleMs(st);
        int mode = modeOf(st);
        if (mode == MODE_STRIP) {
            // 拆包：机械臂先从工件上把头部抓走，再把头丢进回收箱，最后放行工件
            if (u >= 0.14 && !st.grabbed) {
                st.grabbed = true;
                applyGrab(st);
            }
            if (u >= 0.80 && !st.released) {
                st.released = true;
                if (st.packet != null) {
                    st.packet.releaseFromArm();
                }
            }
        } else if (mode == MODE_SEAL) {
            // 封装：机械臂把原料块钉到工件尾部之后才放行
            if (u >= 0.80 && !st.released) {
                st.released = true;
                applyRelease(st);
            }
        } else if (mode == MODE_MIX) {
            // 又要原料又要加工：先把原料块送进机器，再把工件抓进机器加工，最后放行
            if (u >= 0.27 && !st.grabbed) {
                st.grabbed = true;
                stampLayer(st);
            }
            if (u >= 0.75 && !st.released) {
                st.released = true;
                if (st.packet != null) {
                    st.packet.setX(st.outPoint.x);
                    st.packet.setY(st.outPoint.y);
                    st.packet.releaseFromArm();
                }
            }
        } else {
            // 通用工位：工件被机械臂放回传送带上之后才能继续往前走
            if (u >= 0.74 && !st.released) {
                st.released = true;
                if (st.packet != null) {
                    // 工件必须精确回到工位坐标，传送带才能接着把它带走
                    st.packet.setX(st.outPoint.x);
                    st.packet.setY(st.outPoint.y);
                    st.packet.releaseFromArm();
                }
            }
        }
        if (u >= 1.0) {
            // 这一轮干完了，机械臂可以接下一件
            if (st.packet != null) {
                // 兜底：万一关键动作被跳过，也别让工件一直堵在工位上
                st.packet.releaseFromArm();
            }
            st.cycleStart = -1;
            st.packet = null;
            st.pickPoint = null;
            st.outPoint = null;
        }
    }

    /**
     * 机械臂开工条件：等候队列里有工件、料已经备齐、机械臂闲着。
     * <p>通用工位没有额外料（工件本身就是料），只要工件到了就能开工。</p>
     */
    private void tryStartCycle(Station st, long now) {
        if (st.cycleStart > 0 || st.waiting.isEmpty()) {
            return;
        }
        DataPacket packet = st.waiting.peek();
        if (packet == null) {
            st.waiting.poll();
            return;
        }
        // 工件已经交付 / 消失了：机械臂别去抓空气
        if (!aliveIds.contains(packet.getCartId())) {
            st.waiting.poll();
            return;
        }
        // 工件已经离开工位（等太久被放行了，或者已经走过去了）：机械臂来不及，
        // 这一层协议头直接补上/拆掉，保证工件身上的头始终是对的
        boolean stale = !packet.isWaitingForArm();
        if (!stale && st.kind == KIND_HEADER) {
            // 协议头工位必须是「抵达」才动手
            stale = !st.tag.equals(packet.getCurrentTag());
        }
        if (!stale) {
            // 已经走远了（不是排队的后退），机械臂够不着
            stale = distanceTo(packet.getX(), packet.getY(), st.trunk.x, st.trunk.y)
                    > SLOT_DIST * (MAX_QUEUE + 3);
        }
        if (stale) {
            st.waiting.poll();
            if (st.kind == KIND_HEADER) {
                serveStale(st, packet);
            }
            return;
        }
        // 工件得先开到工位上（排队往前挪到位），机械臂才抓得到
        if (distanceTo(packet.getX(), packet.getY(), st.trunk.x, st.trunk.y) > 3) {
            return;
        }
        int mode = modeOf(st);
        // 机械臂要取的原料块（供给带上按筛选器挑）
        SupplyItem picked = null;
        if (mode == MODE_STRIP) {
            // 拆包：料块就在工件身上，直接抓
            if (!layersOf(packet).contains(st.layer)) {
                st.waiting.poll();
                return;
            }
        } else if (mode == MODE_SEAL || mode == MODE_MIX) {
            // 封装 / 取料：得等供给带把料送到取件点，且要过筛选器
            picked = pickSupplyItem(st);
            if (picked == null) {
                return;
            }
        }
        st.waiting.poll();

        st.cycleStart = now;
        st.packet = packet;
        st.grabbed = false;
        st.released = false;

        if (mode == MODE_STRIP) {
            // 从工件身上抓走头部块
            st.pickPoint = new Point((int) Math.round(packet.getX() + layerOffset(packet, st.layer)),
                    (int) Math.round(packet.getY()));
            st.outPoint = new Point(st.bin.x, st.bin.y);
        } else if (mode == MODE_SEAL) {
            // 从供给带上抓起料块，加工完钉到主干上的工件尾部
            st.pickPoint = new Point(st.trunk.x, (int) Math.round(picked.y));
            st.outPoint = new Point((int) Math.round(packet.getX() + layerOffset(packet, st.layer)),
                    (int) Math.round(packet.getY()));
        } else if (mode == MODE_MIX) {
            // 先去供给带取原料块，再把工件抓进机器加工
            st.pickPoint = new Point(st.trunk.x, (int) Math.round(picked.y));
            st.outPoint = new Point(st.trunk.x, st.trunk.y);
        } else {
            // 通用工位：把工件从传送带上抓起来，送进机器加工，再放回传送带
            st.pickPoint = new Point(st.trunk.x, st.trunk.y);
            st.outPoint = new Point(st.trunk.x, st.trunk.y);
        }
    }

    /**
     * 从供给带上挑一个机械臂愿意取的料块。
     * <p>机械臂按工位需求 + 筛选器（白名单 / 黑名单）过滤，
     * 被挡掉的料块留在带上排队。</p>
     *
     * @return 挑中的料块；没有合适的就返回 null
     */
    private SupplyItem pickSupplyItem(Station st) {
        if (st.supplyBottom == null) {
            return null;
        }
        for (int i = 0; i < st.supplyItems.size(); i++) {
            SupplyItem item = st.supplyItems.get(i);
            if (!accepts(st, item.material)) {
                continue;
            }
            if (item.y >= st.pickY - 0.5) {
                st.supplyItems.remove(i);
                return item;
            }
        }
        return null;
    }

    /**
     * 排队工件就位：机械臂忙不过来时，工件沿着传送带往回退排队，
     * 机械臂空下来时队首再往前挪到工位上。
     */
    private void layoutQueue(Station st, Set<Integer> alive) {
        // 已经被放行（超时）或已经消失的工件，从队列里清掉
        Iterator<DataPacket> it = st.waiting.iterator();
        while (it.hasNext()) {
            DataPacket p = it.next();
            if (!alive.contains(p.getCartId())) {
                it.remove();
                continue;
            }
            if (!p.isWaitingForArm()) {
                // 等太久了被放行：把工件摆回工位坐标，别让它歪在队列里
                if (distanceTo(p.getX(), p.getY(), st.trunk.x, st.trunk.y) < SLOT_DIST * 3) {
                    p.setX(st.trunk.x);
                    p.setY(st.trunk.y);
                }
                it.remove();
            }
        }
        int base = st.cycleStart > 0 ? 1 : 0;
        int i = 0;
        for (DataPacket p : st.waiting) {
            double tx = st.trunk.x + st.qx * (i + base) * SLOT_DIST;
            double ty = st.trunk.y + st.qy * (i + base) * SLOT_DIST;
            double dx = tx - p.getX();
            double dy = ty - p.getY();
            double d = Math.hypot(dx, dy);
            if (d > 0.6) {
                double step = Math.min(d, QUEUE_MOVE_SPEED);
                p.setX(p.getX() + dx / d * step);
                p.setY(p.getY() + dy / d * step);
            }
            i++;
        }
    }

    /**
     * 计算机械臂当前姿态。
     * <pre>
     * 通用工位（工件本身就是原料）：
     *   0.00-0.14  待机位 → 传送带上抓取工件
     *   0.14-0.30  把工件送进组装机
     *   0.30-0.58  机器加工（工件在机器里面，看不见）
     *   0.58-0.74  把工件放回传送带
     *   0.74-1.00  空载复位
     * 取料工位（原料在供给带上）：
     *   0.00-0.14  待机位 → 供给带上抓原料块
     *   0.14-0.28  把原料块送进组装机
     *   0.28-0.52  机器加工（原料块在机器里面，看不见）
     *   0.52-0.68  把加工结果送到工件上
     *   0.68-0.80  停一下
     *   0.80-1.00  空载复位
     * 又取料又加工（普通节点也要协议头原料）：
     *   0.00-0.15  待机位 → 供给带上抓原料块
     *   0.15-0.27  把原料块送进组装机
     *   0.27-0.35  空手回到工件旁边
     *   0.35-0.47  把工件抓进组装机
     *   0.47-0.63  机器加工（工件在机器里面，看不见）
     *   0.63-0.75  把工件放回传送带
     *   0.75-1.00  空载复位
     * </pre>
     */
    private void computePose(Station st, double u) {
        double x;
        double y;
        boolean grip = false;
        boolean block = false;
        boolean blockHidden = false;
        boolean work = false;
        boolean workHidden = false;
        int mode = modeOf(st);
        if (u <= 0) {
            x = st.home.x;
            y = st.home.y;
        } else if (mode == MODE_PROCESS) {
            Point pick = st.pickPoint;
            Point mouth = st.mouth;
            Point out = st.outPoint;
            if (u < 0.14) {
                double t = easeInOut(u / 0.14);
                x = lerp(st.home.x, pick.x, t);
                y = lerp(st.home.y, pick.y, t);
            } else if (u < 0.30) {
                double t = easeInOut((u - 0.14) / 0.16);
                x = lerp(pick.x, mouth.x, t);
                y = lerp(pick.y, mouth.y, t);
            } else if (u < 0.58) {
                x = mouth.x;
                y = mouth.y;
            } else if (u < 0.74) {
                double t = easeInOut((u - 0.58) / 0.16);
                x = lerp(mouth.x, out.x, t);
                y = lerp(mouth.y, out.y, t);
            } else {
                double t = easeInOut((u - 0.74) / 0.26);
                x = lerp(out.x, st.home.x, t);
                y = lerp(out.y, st.home.y, t);
            }
            grip = u >= 0.14 && u < 0.74;
            work = grip;
            workHidden = u >= 0.30 && u < 0.58;
        } else if (mode == MODE_MIX) {
            Point supply = st.pickPoint;
            Point mouth = st.mouth;
            Point trunk = new Point(st.trunk.x, st.trunk.y);
            if (u < 0.15) {
                double t = easeInOut(u / 0.15);
                x = lerp(st.home.x, supply.x, t);
                y = lerp(st.home.y, supply.y, t);
            } else if (u < 0.27) {
                double t = easeInOut((u - 0.15) / 0.12);
                x = lerp(supply.x, mouth.x, t);
                y = lerp(supply.y, mouth.y, t);
            } else if (u < 0.35) {
                double t = easeInOut((u - 0.27) / 0.08);
                x = lerp(mouth.x, trunk.x, t);
                y = lerp(mouth.y, trunk.y, t);
            } else if (u < 0.47) {
                double t = easeInOut((u - 0.35) / 0.12);
                x = lerp(trunk.x, mouth.x, t);
                y = lerp(trunk.y, mouth.y, t);
            } else if (u < 0.63) {
                x = mouth.x;
                y = mouth.y;
            } else if (u < 0.75) {
                double t = easeInOut((u - 0.63) / 0.12);
                x = lerp(mouth.x, trunk.x, t);
                y = lerp(mouth.y, trunk.y, t);
            } else {
                double t = easeInOut((u - 0.75) / 0.25);
                x = lerp(trunk.x, st.home.x, t);
                y = lerp(trunk.y, st.home.y, t);
            }
            block = u >= 0.15 && u < 0.27;
            blockHidden = u >= 0.27 && u < 0.35;
            work = u >= 0.35 && u < 0.75;
            workHidden = u >= 0.47 && u < 0.63;
            grip = block || work;
        } else {
            Point pick = st.pickPoint;
            Point mouth = st.mouth;
            Point out = st.outPoint;
            if (u < 0.14) {
                double t = easeInOut(u / 0.14);
                x = lerp(st.home.x, pick.x, t);
                y = lerp(st.home.y, pick.y, t);
            } else if (u < 0.28) {
                double t = easeInOut((u - 0.14) / 0.14);
                x = lerp(pick.x, mouth.x, t);
                y = lerp(pick.y, mouth.y, t);
            } else if (u < 0.52) {
                x = mouth.x;
                y = mouth.y;
            } else if (u < 0.68) {
                double t = easeInOut((u - 0.52) / 0.16);
                x = lerp(mouth.x, out.x, t);
                y = lerp(mouth.y, out.y, t);
            } else if (u < 0.80) {
                x = out.x;
                y = out.y;
            } else {
                double t = easeInOut((u - 0.80) / 0.20);
                x = lerp(out.x, st.home.x, t);
                y = lerp(out.y, st.home.y, t);
            }
            grip = u >= 0.14 && u < 0.80;
            block = grip;
            blockHidden = u >= 0.28 && u < 0.52;
        }
        st.poseX = x;
        st.poseY = y;
        st.poseGrip = grip;
        st.poseBlock = block;
        st.poseBlockHidden = blockHidden;
        st.poseWork = work;
        st.poseWorkHidden = workHidden;

        // 工件被机械臂夹着走，位置跟着夹爪走
        if (work && st.packet != null && st.cycleStart > 0) {
            st.packet.setX(x);
            st.packet.setY(y);
        }
    }

    public void setScale(double scale) {
        this.scale = Math.max(NetworkConfig.MIN_SCALE, Math.min(NetworkConfig.MAX_SCALE, scale));
        setPreferredSize(new Dimension(
                (int) (NetworkConfig.MAP_COLS * NetworkConfig.TILE_SIZE * this.scale),
                (int) (NetworkConfig.MAP_ROWS * NetworkConfig.TILE_SIZE * this.scale)
        ));
        revalidate();
        repaint();
    }

    public void setViewOffset(int x, int y) {
        this.viewOffsetX = x;
        this.viewOffsetY = y;
        repaint();
    }

    public int getViewOffsetX() {
        return viewOffsetX;
    }

    public int getViewOffsetY() {
        return viewOffsetY;
    }

    public void translateView(int dx, int dy) {
        this.viewOffsetX += dx;
        this.viewOffsetY += dy;
        repaint();
    }

    public int getLogicalX(int screenX, int scrollX) {
        return (int) ((screenX + scrollX - viewOffsetX) / scale);
    }

    public int getLogicalY(int screenY, int scrollY) {
        return (int) ((screenY + scrollY - viewOffsetY) / scale);
    }

    public int toScreenX(int logicalX) {
        return (int) (logicalX * scale) + viewOffsetX;
    }

    public int toScreenY(int logicalY) {
        return (int) (logicalY * scale) + viewOffsetY;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.translate(viewOffsetX, viewOffsetY);
        g2.scale(scale, scale);

        drawFloor(g2);
        drawBelts(g2);
        drawSupplyBelts(g2);
        drawMachines(g2);
        drawWorkpieces(g2);
        drawStations(g2);
        drawArms(g2);
        drawSparks(g2);
        drawLegend(g2);

        g2.dispose();
    }

    // ===================== 地面 =====================

    private void drawFloor(Graphics2D g2) {
        int w = NetworkConfig.MAP_COLS * NetworkConfig.TILE_SIZE;
        int h = NetworkConfig.MAP_ROWS * NetworkConfig.TILE_SIZE;

        g2.setColor(FLOOR);
        g2.fillRect(0, 0, w, h);

        // 车间地砖
        g2.setColor(FLOOR_LINE);
        g2.setStroke(new BasicStroke(1f));
        for (int c = 0; c <= NetworkConfig.MAP_COLS; c++) {
            g2.drawLine(c * NetworkConfig.TILE_SIZE, 0, c * NetworkConfig.TILE_SIZE, h);
        }
        for (int r = 0; r <= NetworkConfig.MAP_ROWS; r++) {
            g2.drawLine(0, r * NetworkConfig.TILE_SIZE, w, r * NetworkConfig.TILE_SIZE);
        }
    }

    // ===================== 主干传送带 =====================

    private void drawBelts(Graphics2D g2) {
        ProductionLine line = mapManager.getProductionLine();
        if (line == null) {
            return;
        }
        for (ProductionLine.Belt belt : line.getBelts()) {
            List<Point> pts = belt.getPoints();
            for (int i = 1; i < pts.size(); i++) {
                Point a = pts.get(i - 1);
                Point b = pts.get(i);
                if (a.x == b.x) {
                    drawVerticalBelt(g2, a, b);
                } else {
                    drawHorizontalBelt(g2, a, b);
                }
            }
        }
        drawTrunkBeltLabel(g2, line);
    }

    /**
     * 主干传送带 C2 的物料标签：这条带子只运输工件本体，
     * 协议头块是各工位自己的供给带 C1 送上来的。
     */
    private void drawTrunkBeltLabel(Graphics2D g2, ProductionLine line) {
        for (ProductionLine.Belt belt : line.getBelts()) {
            if (!"PC_FACTORY".equals(belt.getFromTag())) {
                continue;
            }
            List<Point> pts = belt.getPoints();
            if (pts.size() < 2) {
                return;
            }
            Point a = pts.get(0);
            Point b = pts.get(1);
            double cx = (a.x + b.x) / 2.0;
            double cy = (a.y + b.y) / 2.0;
            g2.setFont(new Font("微软雅黑", Font.BOLD, 10));
            g2.setColor(new Color(0x2C, 0x3E, 0x50));
            g2.drawString("主干带 C2 · " + Material.displayName(belt.getMaterial()),
                    (int) cx + 8, (int) cy - BELT_HALF_WIDTH - 6);
            return;
        }
    }

    private void drawHorizontalBelt(Graphics2D g2, Point a, Point b) {
        int y = a.y;
        int x1 = Math.min(a.x, b.x);
        int x2 = Math.max(a.x, b.x);
        int dir = b.x >= a.x ? 1 : -1;

        // 带体
        g2.setPaint(new GradientPaint(0, y - BELT_HALF_WIDTH, BELT_TOP, 0, y + BELT_HALF_WIDTH, BELT_BOTTOM));
        g2.fillRoundRect(x1, y - BELT_HALF_WIDTH, x2 - x1, BELT_HALF_WIDTH * 2, 12, 12);

        // 滚动的斜纹纹理
        g2.setColor(new Color(BELT_TEX.getRed(), BELT_TEX.getGreen(), BELT_TEX.getBlue(), 90));
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double offset = (animTime * TEX_SPEED * dir) % TEX_PERIOD;
        for (double x = x1 - TEX_PERIOD; x < x2 + TEX_PERIOD; x += TEX_PERIOD) {
            double sx = x + offset;
            g2.drawLine((int) sx, y - BELT_HALF_WIDTH + 2, (int) (sx - 15), y + BELT_HALF_WIDTH - 2);
        }

        // 上下护栏
        g2.setColor(BELT_RAIL);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(x1, y - BELT_HALF_WIDTH, x2, y - BELT_HALF_WIDTH);
        g2.drawLine(x1, y + BELT_HALF_WIDTH, x2, y + BELT_HALF_WIDTH);

        // 流向箭头（沿带身重复出现）
        for (int x = x1 + 46; x < x2 - 20; x += 96) {
            drawFlowArrow(g2, x, y, dir);
        }
        if (x2 - x1 > 60) {
            drawFlowArrow(g2, x2 - 24, y, dir);
        }
    }

    private void drawVerticalBelt(Graphics2D g2, Point a, Point b) {
        int x = a.x;
        int y1 = Math.min(a.y, b.y);
        int y2 = Math.max(a.y, b.y);
        int dir = b.y >= a.y ? 1 : -1;

        g2.setPaint(new GradientPaint(x - BELT_HALF_WIDTH, 0, BELT_TOP, x + BELT_HALF_WIDTH, 0, BELT_BOTTOM));
        g2.fillRoundRect(x - BELT_HALF_WIDTH, y1, BELT_HALF_WIDTH * 2, y2 - y1, 12, 12);

        g2.setColor(new Color(BELT_TEX.getRed(), BELT_TEX.getGreen(), BELT_TEX.getBlue(), 90));
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        double offset = (animTime * TEX_SPEED * dir) % TEX_PERIOD;
        for (double y = y1 - TEX_PERIOD; y < y2 + TEX_PERIOD; y += TEX_PERIOD) {
            double sy = y + offset;
            g2.drawLine(x - BELT_HALF_WIDTH + 2, (int) sy, x + BELT_HALF_WIDTH - 2, (int) (sy - 15));
        }

        g2.setColor(BELT_RAIL);
        g2.setStroke(new BasicStroke(2f));
        g2.drawLine(x - BELT_HALF_WIDTH, y1, x - BELT_HALF_WIDTH, y2);
        g2.drawLine(x + BELT_HALF_WIDTH, y1, x + BELT_HALF_WIDTH, y2);

        drawFlowArrow(g2, x, (y1 + y2) / 2, dir);
    }

    /** 传送带上的流向箭头 */
    private void drawFlowArrow(Graphics2D g2, double cx, double cy, int dir) {
        g2.setColor(new Color(255, 255, 255, 70));
        Polygon arrow = new Polygon();
        if (dir >= 0) {
            arrow.addPoint((int) (cx + 9), (int) cy);
            arrow.addPoint((int) (cx - 4), (int) cy - 6);
            arrow.addPoint((int) (cx - 4), (int) cy + 6);
        } else {
            arrow.addPoint((int) (cx - 9), (int) cy);
            arrow.addPoint((int) (cx + 4), (int) cy - 6);
            arrow.addPoint((int) (cx + 4), (int) cy + 6);
        }
        g2.fillPolygon(arrow);
    }

    // ===================== 供给传送带 C1 =====================

    /** 每种物料一条专属供给带，把料从生成机送到组装机旁待取 */
    private void drawSupplyBelts(Graphics2D g2) {
        for (Station f : stations) {
            if (f.supplyBottom == null) {
                continue;
            }
            int x = f.supplyTop.x;
            int top = f.supplyTop.y;
            int bottom = f.supplyBottom.y;

            g2.setPaint(new GradientPaint(x - SUPPLY_BELT_W / 2, 0, BELT_TOP,
                    x + SUPPLY_BELT_W / 2, 0, BELT_BOTTOM));
            g2.fillRoundRect(x - SUPPLY_BELT_W / 2, top, SUPPLY_BELT_W, bottom - top, 8, 8);

            // 横向滚动纹理（料块往下的方向）
            g2.setColor(new Color(BELT_TEX.getRed(), BELT_TEX.getGreen(), BELT_TEX.getBlue(), 95));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            double offset = (animTime * TEX_SPEED * 1.4) % 16;
            for (double y = top - 16; y < bottom + 16; y += 16) {
                double sy = y + offset;
                g2.drawLine(x - SUPPLY_BELT_W / 2 + 2, (int) sy, x + SUPPLY_BELT_W / 2 - 2, (int) sy);
            }

            g2.setColor(BELT_RAIL);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(x - SUPPLY_BELT_W / 2, top, x - SUPPLY_BELT_W / 2, bottom);
            g2.drawLine(x + SUPPLY_BELT_W / 2, top, x + SUPPLY_BELT_W / 2, bottom);

            // 供给带的流向箭头（朝下，指向组装机）
            g2.setColor(new Color(0x95, 0xA5, 0xA6, 130));
            Polygon arrow = new Polygon();
            arrow.addPoint(x, bottom - 6);
            arrow.addPoint(x - 7, bottom - 18);
            arrow.addPoint(x + 7, bottom - 18);
            g2.fillPolygon(arrow);

            // 供给带编号：一条带子只运输一种物料
            g2.setFont(new Font("微软雅黑", Font.BOLD, 9));
            g2.setColor(f.dark);
            String label = Material.displayName(f.demand) + " 供给带";
            g2.drawString(label, x + SUPPLY_BELT_W / 2 + 6, top + 14);
        }
    }

    /** 组装机与主干之间的小滑槽 */
    private void drawChute(Graphics2D g2, double cx, double top, double bottom) {
        int w = 26;
        g2.setPaint(new GradientPaint((int) cx - w / 2, 0, BELT_TOP, (int) cx + w / 2, 0, BELT_BOTTOM));
        g2.fillRoundRect((int) cx - w / 2, (int) top, w, (int) (bottom - top), 6, 6);
        g2.setColor(BELT_RAIL);
        g2.setStroke(new BasicStroke(1.6f));
        g2.drawRoundRect((int) cx - w / 2, (int) top, w, (int) (bottom - top), 6, 6);
        g2.setColor(new Color(255, 255, 255, 70));
        Polygon arrow = new Polygon();
        arrow.addPoint((int) cx, (int) (bottom - 3));
        arrow.addPoint((int) cx - 5, (int) (bottom - 12));
        arrow.addPoint((int) cx + 5, (int) (bottom - 12));
        g2.fillPolygon(arrow);
    }

    // ===================== 组装机 =====================

    private void drawMachines(Graphics2D g2) {
        String[][] layout = mapManager.getBuildingLayout();
        for (int r = 0; r < NetworkConfig.MAP_ROWS; r++) {
            for (int c = 0; c < NetworkConfig.MAP_COLS; c++) {
                String tag = layout[r][c];
                if ("NONE".equals(tag)) {
                    continue;
                }
                Point p = mapManager.findBuildingCoords(tag);
                if (p == null) {
                    p = new Point(c * NetworkConfig.TILE_SIZE + NetworkConfig.TILE_SIZE / 2,
                            r * NetworkCodeTile());
                }
                drawMachine(g2, tag, p.x, p.y);
            }
        }
    }

    private static int NetworkCodeTile() {
        return NetworkConfig.TILE_SIZE / 2;
    }

    /** 俯视角度的一台组装机：摆在主干传送带的外侧 */
    private void drawMachine(Graphics2D g2, String tag, double cx, double cy) {
        long now = System.currentTimeMillis();
        boolean onLine = isOnLine(tag);

        // 传送带上的工位 → 机器搬到传送带上方（外侧），并用滑槽连回传送带
        double mx = cx;
        double my = onLine ? cy + MACHINE_OFFSET : cy;

        Long busyUntil = machineBusyUntil.get(tag);
        boolean busy = busyUntil != null && now < busyUntil;
        double phase = busy ? (now - machineBusySince.getOrDefault(tag, now)) / 900.0 : 0;

        // 工位上的机器：加工窗口由机械臂动作决定
        Station station = stationFor(tag);
        boolean crafting = false;
        if (station != null) {
            double u = stationProgress(station, now);
            // 料在机器里面加工的那一段
            crafting = u > 0 && (station.poseWorkHidden || station.poseBlockHidden);
            if (u > 0 && u < 1) {
                busy = true;
                phase = u;
            }
        }

        BuildingZone zone = mapManager.getZoneForBuilding(tag);
        Color accent = zone != null ? zone.borderColor : new Color(0x63, 0x6E, 0x72);

        int w = MACHINE_W, h = MACHINE_H;
        int x = (int) mx - w / 2;
        int y = (int) my - h / 2;

        if (onLine) {
            // 机器下方的滑槽：把主干上的工件送进机器 / 把成品送回主干
            drawChute(g2, mx, my + h / 2, cy - BELT_HALF_WIDTH);
        }

        // 机体
        g2.setPaint(new GradientPaint(0, y, MACHINE_TOP, 0, y + h, MACHINE_BOTTOM));
        g2.fillRoundRect(x, y, w, h, 8, 8);
        g2.setColor(MACHINE_STROKE);
        g2.setStroke(new BasicStroke(1.6f));
        g2.drawRoundRect(x, y, w, h, 8, 8);

        // 加工发光区：料在机器里面时最亮，其余时间随加工进度呼吸
        if (busy) {
            double pulse = crafting ? 0.75 : 0.2 + 0.5 * Math.abs(Math.sin(phase * Math.PI * 6));
            g2.setColor(new Color(GLOW.getRed(), GLOW.getGreen(), GLOW.getBlue(), (int) (255 * pulse)));
            g2.fillRoundRect(x + 12, y + 14, w - 24, h - 26, 6, 6);
        }

        // 顶边分区色条
        g2.setColor(accent);
        g2.fillRoundRect(x + 10, y + 4, w - 20, 5, 3, 3);

        // 状态指示灯
        if (busy) {
            g2.setColor(Math.sin(phase * Math.PI * 8) > 0 ? BUSY_RED : BUSY_ORANGE);
        } else {
            g2.setColor(OK_GREEN);
        }
        g2.fillOval(x + 9, y + 13, 9, 9);
        g2.setColor(new Color(0x1A, 0x25, 0x2F));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawOval(x + 9, y + 13, 9, 9);

        // 名称标签（在机器内侧；不在主干上的设备则挂在机器下方）
        g2.setFont(new Font("微软雅黑", Font.BOLD, 9));
        g2.setColor(new Color(0x5F, 0x6B, 0x72));
        String label = tag;
        FontMetrics fm = g2.getFontMetrics();
        if (onLine) {
            g2.drawString(label, x + w - fm.stringWidth(label) - 8, y + h - 8);
        } else {
            g2.setColor(new Color(0x7F, 0x8C, 0x8D));
            g2.drawString(label, (int) mx - fm.stringWidth(label) / 2, (int) my + h / 2 + 14);
        }

        // 需求牌：这台设备要消耗哪种原料（机械臂按需求去对应的传送带上取料）
        if (station != null) {
            drawDemandChip(g2, station, x + 6, y + 12);
        }

        // 接收端服务器的缓冲区格子
        if ("RX_ST".equals(tag)) {
            g2.setColor(BUSY_RED);
            for (int i = 0; i < serverBufferCount; i++) {
                g2.fillRect(x + 12 + i * 8, y + 12, 5, 7);
            }
        }

        // 机械臂等候队列：还有工件排队就挂个牌子
        if (station != null && !station.waiting.isEmpty()) {
            g2.setColor(BUSY_ORANGE);
            g2.fillRoundRect(x + w - 26, y - 16, 24, 14, 4, 4);
            g2.setFont(new Font("微软雅黑", Font.BOLD, 9));
            g2.setColor(Color.WHITE);
            String q = "等" + station.waiting.size();
            FontMetrics qfm = g2.getFontMetrics();
            g2.drawString(q, x + w - 26 + 12 - qfm.stringWidth(q) / 2, y - 5);
        }
    }

    /**
     * 机器上的需求牌：显示这台设备要消耗的原料，
     * 以及机械臂筛选器的状态（设了白名单 / 黑名单就挂个「筛」牌）。
     */
    private void drawDemandChip(Graphics2D g2, Station st, int x, int y) {
        String demand = st.demand;
        Color bg = Material.isHeader(demand) ? colorOfLayer(demand) : MACHINE_STROKE;
        Color fg = Color.WHITE;
        String text = Material.PAYLOAD.equals(demand) ? "件" : demand;
        g2.setFont(new Font("微软雅黑", Font.BOLD, 9));
        FontMetrics fm = g2.getFontMetrics();
        int cw = fm.stringWidth(text) + 8;
        g2.setColor(bg);
        g2.fillRoundRect(x, y, cw, 13, 4, 4);
        g2.setColor(fg);
        g2.drawString(text, x + 4, y + 10);

        // 筛选器状态
        if (!st.whitelist.isEmpty() || !st.blacklist.isEmpty()) {
            int fx = x + cw + 3;
            g2.setColor(BUSY_ORANGE);
            g2.fillRoundRect(fx, y, 15, 13, 4, 4);
            g2.setColor(Color.WHITE);
            g2.drawString("筛", fx + 3, y + 10);
        }
    }

    /** 找到某工位对应的产线 */
    private Station stationFor(String tag) {
        for (Station f : stations) {
            if (f.tag.equals(tag)) {
                return f;
            }
        }
        return null;
    }

    /** 机械臂动作进度（0 表示空闲，>0 表示进行中） */
    private double stationProgress(Station st, long now) {
        if (st.cycleStart <= 0) {
            return 0;
        }
        double u = (now - st.cycleStart) / (double) cycleMs(st);
        return u > 1 ? 0 : u;
    }

    /** 一个动作周期的时长 */
    private long cycleMs(Station st) {
        int mode = modeOf(st);
        if (mode == MODE_PROCESS) {
            return PROCESS_CYCLE_MS;
        }
        return mode == MODE_MIX ? MIX_CYCLE_MS : ARM_CYCLE_MS;
    }

    /**
     * 这个工位的机械臂怎么取料。
     * <p>协议头工位：封装工位从自己的供给带取原料块，拆包工位从工件上拆；
     * 普通节点默认消耗工件本体，用户把需求改成协议头原料后，
     * 它就先去供给带取料送进机器，再把工件抓进机器加工。</p>
     */
    private static int modeOf(Station st) {
        if (st.kind == KIND_HEADER) {
            return st.strip ? MODE_STRIP : MODE_SEAL;
        }
        return Material.isHeader(st.demand) ? MODE_MIX : MODE_PROCESS;
    }

    // ===================== 生成机 / 收纳箱 / 排队料块 =====================

    private void drawStations(Graphics2D g2) {
        long now = System.currentTimeMillis();
        for (Station f : stations) {
            if (f.bin != null) {
                drawBin(g2, f);
            }
            if (f.supplyBottom == null) {
                continue;
            }
            drawGenerator(g2, f, now);
            // 供给带上排队的料块
            for (SupplyItem item : f.supplyItems) {
                drawHeaderBlock(g2, f.supplyTop.x, item.y, f);
            }
        }
    }

    /** 拆包工位上方的收纳箱：拆下来的头部块丢在这里 */
    private void drawBin(Graphics2D g2, Station f) {
        int mx = f.bin.x;
        int my = f.bin.y;
        int x = mx - GEN_SIZE / 2;
        int y = my - GEN_SIZE / 2;

        g2.setFont(new Font("微软雅黑", Font.BOLD, 11));
        g2.setColor(f.dark);
        String title = f.layer + " 回收箱";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, mx - fm.stringWidth(title) / 2, y - 10);

        g2.setPaint(new GradientPaint(0, y, BIN_COLOR, 0, y + GEN_SIZE, MACHINE_BOTTOM));
        g2.fillRoundRect(x, y, GEN_SIZE, GEN_SIZE, 6, 6);
        g2.setColor(MACHINE_STROKE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x, y, GEN_SIZE, GEN_SIZE, 6, 6);
        g2.setColor(f.color);
        g2.fillRoundRect(x + 14, y + 16, GEN_SIZE - 28, GEN_SIZE - 32, 4, 4);
        g2.setColor(f.dark);
        g2.fillOval(mx - 4, y - 9, 8, 8);
    }

    /** 供给带顶端的生成机：源源不断地生产头部块 */
    private void drawGenerator(Graphics2D g2, Station f, long now) {
        int mx = f.generator.x;
        int my = f.generator.y;
        int x = mx - GEN_SIZE / 2;
        int y = my - GEN_SIZE / 2;

        g2.setFont(new Font("微软雅黑", Font.BOLD, 11));
        g2.setColor(f.dark);
        String title = f.layer + " 生成机";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(title, mx - fm.stringWidth(title) / 2, y - 12);

        // 机壳
        g2.setPaint(new GradientPaint(0, y, MACHINE_TOP, 0, y + GEN_SIZE, MACHINE_BOTTOM));
        g2.fillRoundRect(x, y, GEN_SIZE, GEN_SIZE, 6, 6);
        g2.setColor(MACHINE_STROKE);
        g2.setStroke(new BasicStroke(2f));
        g2.drawRoundRect(x, y, GEN_SIZE, GEN_SIZE, 6, 6);

        // 内部脉动块：空闲时呼吸，供给带上排队越多越亮
        boolean working = f.supplyItems.size() >= 2;
        double pulse = working ? 0.85 : 0.35 + 0.35 * Math.sin(animTime / 420.0 + f.trunk.x);
        g2.setColor(new Color(f.color.getRed(), f.color.getGreen(), f.color.getBlue(),
                (int) (255 * Math.min(1, pulse))));
        g2.fillRoundRect(x + 18, y + 24, GEN_SIZE - 36, GEN_SIZE - 40, 4, 4);

        // 指示灯
        g2.setColor(working ? BUSY_RED : f.dark);
        int led = working ? 10 : 8;
        g2.fillOval(mx - led / 2, y - 9, led, led);
        g2.setColor(new Color(0x1A, 0x25, 0x2F));
        g2.setStroke(new BasicStroke(1.2f));
        g2.drawOval(mx - led / 2, y - 9, led, led);
    }

    // ===================== 机械臂 =====================

    /**
     * 画所有工位上的机械臂。
     * <p>协议头工位：机械臂从供给带上抓起头部块，送进机器，再钉到工件尾部 / 丢进回收箱。</p>
     * <p>通用工位：机械臂把工件从传送带上抓进机器，加工完再放回传送带。</p>
     */
    private void drawArms(Graphics2D g2) {
        for (Station f : stations) {
            if (f.cycleStart <= 0) {
                // 空闲：机械臂停在待机位待命
                drawArmLink(g2, f.armBase.x, f.armBase.y, f.home.x, f.home.y, ARM_L1, ARM_L2, 0);
                continue;
            }
            double grip = f.poseGrip ? 1 : 0;
            drawArmLink(g2, f.armBase.x, f.armBase.y, f.poseX, f.poseY, ARM_L1, ARM_L2, grip);
            // 取料工位：夹爪里夹着原料块，料块在机器内部时不可见
            if (f.poseBlock && !f.poseBlockHidden) {
                drawHeaderBlock(g2, f.poseX, f.poseY, f);
            }
        }
    }

    /**
     * 计算某一层协议头在工件上的横向偏移（净荷居中，头部向右依次叠放）。
     */
    private double layerOffset(DataPacket packet, String layer) {
        Set<String> layers = packet != null ? layersOf(packet) : new LinkedHashSet<>();
        int index = 0;
        for (String l : LAYER_ORDER) {
            if (l.equals(layer)) {
                break;
            }
            if (layers.contains(l)) {
                index++;
            }
        }
        return PAYLOAD_W / 2.0 + HEAD_W * index + HEAD_W / 2.0;
    }

    /** 工件已经离开工位时的兜底：直接补上/拆掉这一层协议头 */
    private void serveStale(Station f, DataPacket packet) {
        Set<String> layers = layersOf(packet);
        if (f.strip) {
            layers.remove(f.layer);
        } else {
            layers.add(f.layer);
        }
        packet.releaseFromArm();
    }

    /** 拆包：机械臂从工件上把这一层头部抓下来 */
    private void applyGrab(Station f) {
        DataPacket packet = f.packet;
        if (packet == null) {
            return;
        }
        Set<String> layers = packetLayers.computeIfAbsent(packet.getCartId(),
                id -> new LinkedHashSet<>());
        layers.remove(f.layer);
        // 头已经拆下来了，工件可以继续往前走
        packet.releaseFromArm();
    }

    /** 封装：机械臂把这一层协议头钉到工件尾部 */
    private void applyRelease(Station f) {
        DataPacket packet = f.packet;
        if (packet == null) {
            return;
        }
        Set<String> layers = packetLayers.computeIfAbsent(packet.getCartId(),
                id -> new LinkedHashSet<>());
        if (f.strip) {
            layers.remove(f.layer);
        } else {
            layers.add(f.layer);
        }
        // 头已经装上了，工件可以继续往前走
        packet.releaseFromArm();
    }

    /**
     * 只把这一层协议头记到工件上，不放行工件。
     * <p>「又取料又加工」的工位：原料块先被送进机器（这时工件还得留在机器边上
     * 等着被抓进去加工），所以这里不能顺手把工件放掉。</p>
     */
    private void stampLayer(Station f) {
        DataPacket packet = f.packet;
        if (packet == null) {
            return;
        }
        Set<String> layers = packetLayers.computeIfAbsent(packet.getCartId(),
                id -> new LinkedHashSet<>());
        if (f.strip) {
            layers.remove(f.layer);
        } else {
            layers.add(f.layer);
        }
    }

    /** 头部块 */
    private void drawHeaderBlock(Graphics2D g2, double x, double y, Station f) {
        int w = 20, h = 28;
        g2.setColor(f.color);
        g2.fillRoundRect((int) x - w / 2, (int) y - h / 2, w, h, 3, 3);
        g2.setColor(f.dark);
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect((int) x - w / 2, (int) y - h / 2, w, h, 3, 3);
        g2.setColor(new Color(255, 255, 255, 90));
        g2.fillRoundRect((int) x - w / 2 + 3, (int) y - h / 2 + 3, w - 6, 7, 2, 2);
    }

    /**
     * 两连杆逆运动学绘制机械臂（与参考实现一致）。
     */
    private void drawArmLink(Graphics2D g2, double sx, double sy, double tx, double ty,
                             double L1, double L2, double grip) {
        // 底座
        g2.setColor(new Color(0x2C, 0x3E, 0x50));
        g2.fillOval((int) sx - 16, (int) sy - 16, 32, 32);
        g2.setColor(new Color(0x34, 0x49, 0x5E));
        g2.fillOval((int) sx - 8, (int) sy - 8, 16, 16);

        double dx = tx - sx;
        double dy = ty - sy;
        double d = Math.max(Math.hypot(dx, dy), Math.abs(L1 - L2) + 0.5);
        d = Math.min(d, L1 + L2 - 0.5);

        double base = Math.atan2(dy, dx);
        double cosA = Math.max(-1, Math.min(1, (d * d - L1 * L1 - L2 * L2) / (2 * L1 * L2)));
        double a2 = -Math.acos(cosA);
        double t2 = a2;
        double t1 = base - Math.atan2(L2 * Math.sin(t2), L1 + L2 * Math.cos(t2));

        double ex = sx + L1 * Math.cos(t1);
        double ey = sy + L1 * Math.sin(t1);
        double hx = sx + L1 * Math.cos(t1) + L2 * Math.cos(t1 + t2);
        double hy = sy + L1 * Math.sin(t1) + L2 * Math.sin(t1 + t2);

        // 大臂
        g2.setPaint(new GradientPaint(0, (float) (sy - 9), ARM_TOP, 0, (float) (sy + 9), ARM_BOTTOM));
        g2.setStroke(new BasicStroke(15f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine((int) sx, (int) sy, (int) ex, (int) ey);
        // 小臂
        g2.setPaint(new GradientPaint(0, (float) (sy - 7), ARM2_TOP, 0, (float) (sy + 7), ARM2_BOTTOM));
        g2.setStroke(new BasicStroke(11f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawLine((int) ex, (int) ey, (int) hx, (int) hy);

        // 关节
        g2.setColor(ARM_JOINT);
        g2.fillOval((int) sx - 6, (int) sy - 6, 12, 12);
        g2.fillOval((int) ex - 5, (int) ey - 5, 10, 10);

        // 夹爪
        double ang = t1 + t2;
        double px = hx + Math.cos(ang) * 6;
        double py = hy + Math.sin(ang) * 6;
        g2.setColor(new Color(0x4A, 0x55, 0x68));
        g2.fillOval((int) px - 3, (int) py - 3, 6, 6);
        double nx = -Math.sin(ang);
        double ny = Math.cos(ang);
        double off = 5 * (1 - grip);
        g2.setColor(ARM_JAW);
        g2.fillRect((int) (px + nx * (4 + off) - 4), (int) (py + ny * (4 + off) - 2), 8, 4);
        g2.fillRect((int) (px - nx * (4 + off) - 4), (int) (py - ny * (4 + off) - 2), 8, 4);
    }

    private static double easeInOut(double u) {
        u = clamp01(u);
        return u < 0.5 ? 2 * u * u : 1 - Math.pow(-2 * u + 2, 2) / 2;
    }

    private static double clamp01(double u) {
        return Math.max(0, Math.min(1, u));
    }

    private static double lerp(double a, double b, double t) {
        return a + (b - a) * t;
    }

    private static double distanceTo(double x1, double y1, double x2, double y2) {
        return Math.hypot(x2 - x1, y2 - y1);
    }

    // ===================== 工件（数据包） =====================

    private void drawWorkpieces(Graphics2D g2) {
        for (DataPacket packet : dataPackets) {
            // 工件被塞进组装机里加工时，看不见它
            double[] carried = carriedPos.get(packet.getCartId());
            if (carried != null && carried[2] > 0.5) {
                continue;
            }
            int cx = (int) packet.getX();
            int cy = (int) packet.getY();

            Set<String> layers = layersOf(packet);

            // 净荷（居中）
            Color color = getPacketColor(packet);
            int py = cy - PAYLOAD_H / 2;
            g2.setColor(color);
            g2.fillRoundRect(cx - PAYLOAD_W / 2, py, PAYLOAD_W, PAYLOAD_H, 4, 4);
            g2.setColor(color.darker());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(cx - PAYLOAD_W / 2, py, PAYLOAD_W, PAYLOAD_H, 4, 4);
            g2.setColor(new Color(255, 255, 255, 120));
            g2.fillRoundRect(cx - PAYLOAD_W / 2 + 6, py + 4, PAYLOAD_W - 12, 9, 3, 3);

            // 协议头块（从净荷右侧依次叠放：TCP → IP → ETH）
            int hx = cx + PAYLOAD_W / 2;
            for (String layer : LAYER_ORDER) {
                if (!layers.contains(layer)) {
                    continue;
                }
                Color lc = colorOfLayer(layer);
                g2.setColor(lc);
                g2.fillRect(hx, py, HEAD_W, PAYLOAD_H);
                g2.setColor(darkOfLayer(layer));
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRect(hx, py, HEAD_W, PAYLOAD_H);
                g2.setColor(new Color(255, 255, 255, 110));
                g2.fillRect(hx + 3, py + 4, HEAD_W - 6, 6);
                hx += HEAD_W;
            }

            // 回程件用蓝色描边区分
            if (packet.isReturnTrip()) {
                g2.setColor(new Color(0x2E, 0x86, 0xC1));
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawRoundRect(cx - PAYLOAD_W / 2 - 3, py - 3,
                        PAYLOAD_W + (hx - cx - PAYLOAD_W / 2) + 6, PAYLOAD_H + 6, 6, 6);
            }

            drawPacketLabel(g2, packet, cx, cy);
        }
    }

    /** 工件身上当前带着哪些协议头（供测试与调试查看封装进度） */
    public Set<String> getPacketLayers(DataPacket packet) {
        return Collections.unmodifiableSet(layersOf(packet));
    }

    /** 取报文的协议层集合（首次出现时按报文自身状态初始化） */
    private Set<String> layersOf(DataPacket packet) {
        if (packet == null) {
            return new LinkedHashSet<>();
        }
        Set<String> layers = packetLayers.get(packet.getCartId());
        if (layers != null) {
            return layers;
        }
        layers = new LinkedHashSet<>();
        int stage = packet.getStage();
        // 已经走过封装工位的，头部直接算装上了。
        // 注意：工件一抵达封装工位阶段号就会 +1，所以这里要用「已经离开该工位」的判断，
        // 否则头部会在机械臂动手之前就凭空出现。
        if (packet.isHasTcp() && stage > 14) {
            layers.add("TCP");
        }
        if (packet.isHasIp() && stage > 15) {
            layers.add("IP");
        }
        if (packet.isHasEther() && stage > 20) {
            layers.add("ETH");
        }
        packetLayers.put(packet.getCartId(), layers);
        return layers;
    }

    private void drawPacketLabel(Graphics2D g2, DataPacket packet, int cx, int cy) {
        g2.setFont(new Font("微软雅黑", Font.BOLD, 9));
        String direction = packet.isReturnTrip() ? "[回程]"
                : (packet.getWaitInQueueTimer() > 0 ? "[排队]" : "[发送]");
        String label = packet.getCartType() + " " + direction + " TTL:" + packet.getTtl();
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(label) + 8;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(cx - w / 2, cy - 34, w, 14);
        g2.setColor(packet.isReturnTrip() ? new Color(0x8E, 0xD8, 0xF8) : new Color(0x2B, 0x2B, 0x2B));
        g2.drawString(label, cx - w / 2 + 4, cy - 23);
    }

    private Color getPacketColor(DataPacket packet) {
        if (packet.getWaitInQueueTimer() > 0) return new Color(0xE7, 0x4C, 0x3C);
        if (packet.getCartType().equals("ICMP_TIMEEXCEEDED")) return new Color(0xC8, 0x32, 0x32);
        if (packet.getCartType().equals("ICMP_ECHO_REQ")) return new Color(0x64, 0x64, 0xFF);
        if (packet.getCartType().equals("ICMP_ECHO_REPLY")) return new Color(0x2E, 0xCC, 0x71);
        if (packet.getCartType().startsWith("DHCP")) return new Color(0xE0, 0x6C, 0xFF);
        if (packet.getCartType().startsWith("SYN")) return new Color(0x48, 0xD1, 0xE0);
        if (packet.getCartType().startsWith("FIN")) return new Color(0xFF, 0x8A, 0xC8);
        if (packet.getCartType().contains("ACK")) return new Color(0x5C, 0xD6, 0x5C);
        if (packet.getCartType().startsWith("DNS")) return new Color(0x35, 0xC4, 0xC4);
        if (packet.getCartType().startsWith("TLS_")) return new Color(0xFF, 0xA5, 0x00);
        if (packet.getCartType().equals("HTTP_GET") || packet.getCartType().equals("HTTP_200_OK"))
            return new Color(0x9B, 0x59, 0xB6);
        if (packet.getCartType().equals("UDP_DATA")) return new Color(0x32, 0x96, 0xFF);
        if (packet.isRetransmission()) return Color.ORANGE;
        return new Color(0xF1, 0xC4, 0x0F);
    }

    // ===================== 特效 =====================

    private void drawSparks(Graphics2D g2) {
        for (Spark s : sparks) {
            if (s.life <= 0) {
                continue;
            }
            double op = Math.max(0, s.life / s.max);
            g2.setColor(new Color(SPARK.getRed(), SPARK.getGreen(), SPARK.getBlue(), (int) (255 * op)));
            g2.fillOval((int) s.x - 2, (int) s.y - 2, 4, 4);
        }
    }

    // ===================== 图例 =====================

    private void drawLegend(Graphics2D g2) {
        g2.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        int x = 16;
        int y = 26;
        g2.setColor(new Color(0x2C, 0x3E, 0x50));
        g2.drawString("分区图例", x, y);
        y += 16;

        drawLegendItem(g2, x, y, new Color(0xE7, 0x4C, 0x3C), "安全防护");
        drawLegendItem(g2, x, y + 16, new Color(0xF3, 0x9C, 0x12), "QoS/调度控制");
        drawLegendItem(g2, x, y + 32, new Color(0x34, 0x98, 0xDB), "路由/数据源");
        drawLegendItem(g2, x, y + 48, new Color(0x2E, 0xCC, 0x71), "DNS");
        drawLegendItem(g2, x, y + 64, new Color(0x9B, 0x59, 0xB6), "应用层");
        drawLegendItem(g2, x, y + 80, new Color(0xF1, 0xC4, 0x0F), "传输层 (TCP/UDP)");
        drawLegendItem(g2, x, y + 96, new Color(0xE8, 0x44, 0xAD), "网络层 (IP/ICMP)");
        drawLegendItem(g2, x, y + 112, new Color(0xF3, 0x9C, 0x12), "网关/NAT");
        drawLegendItem(g2, x, y + 128, new Color(0x1F, 0x8A, 0xC8), "链路层 (Ethernet/ARP)");
        drawLegendItem(g2, x, y + 144, new Color(0xA0, 0x5A, 0x2B), "路由协议");

        // 产线说明
        int bx = x + 110;
        int by = 26;
        g2.setColor(new Color(0x2C, 0x3E, 0x50));
        g2.drawString("组装产线（节点在传送带外侧，每个节点都有机械臂）", bx, by);
        by += 18;
        drawLegendItem(g2, bx, by, TCP_COLOR, "TCP 头（蓝色）");
        drawLegendItem(g2, bx, by + 16, IP_COLOR, "IP 头（绿色）");
        drawLegendItem(g2, bx, by + 32, ETH_COLOR, "ETH 头（紫色）");
        by += 54;
        g2.setColor(new Color(0x55, 0x5F, 0x66));
        g2.drawString("每种料一条供给带 C1，机械臂抓料进组装机", bx, by);
        g2.drawString("加工完成后再由机械臂送上主干 C2，抓不过来就在带上排队", bx, by + 16);
        g2.drawString("通用机械臂：数据过每个节点都要被抓进机器加工一遍", bx, by + 32);
        g2.drawString("机器上的牌子 = 这台设备需要的原料（工件 / TCP / IP / ETH）", bx, by + 48);
        g2.setColor(BUSY_ORANGE);
        g2.drawString("右键工位可给机械臂设置筛选器（白名单 / 黑名单）", bx, by + 64);
    }

    private void drawLegendItem(Graphics2D g2, int x, int y, Color color, String text) {
        g2.setColor(color);
        g2.fillRect(x, y - 8, 11, 11);
        g2.setColor(new Color(0x55, 0x5F, 0x66));
        g2.drawString(text, x + 17, y);
    }
}
