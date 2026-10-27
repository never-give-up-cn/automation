package com.never_give_up.automation.demo.ui;

import com.never_give_up.automation.demo.config.NetworkConfig;
import com.never_give_up.automation.demo.engine.GameEngine;
import com.never_give_up.automation.demo.engine.MapManager;
import com.never_give_up.automation.demo.engine.ProductionLine;
import com.never_give_up.automation.demo.winModel.DataPacket;
import com.never_give_up.automation.demo.winModel.OreCart;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 厂区画布（俯视图）：把协议栈画成一条自动化产线。
 * <p>
 * 参照「TCP/IP 数据包组装产线 · 俯视图动画」：
 * </p>
 * <ol>
 *   <li><b>主干传送带</b>：协议栈的工位依次排布在传送带上，工件（数据包）
 *       骑在传送带上向下一站流去；</li>
 *   <li><b>分支产线</b>：TCP / IP / ETH 三个封装层各有一条支线，支线顶端是
 *       「生成机」，支线传送带把定长头部块（蓝色 TCP、绿色 IP、紫色 ETH）
 *       源源不断地送到主干旁待命；</li>
 *   <li><b>机械臂</b>：工件抵达对应工位时，机械臂俯身从支线上抓起头部块，
 *       摆到主干上「钉」在工件尾部，工件因此越走越厚；</li>
 *   <li><b>拆包工位</b>：接收端把 TCP / IP / ETH 头部拆下来，机械臂把头部块
 *       拎回收纳箱，工件随之变薄，回到应用层时只剩净荷。</li>
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

    /** 支线传送带宽度 */
    private static final int FEEDER_BELT_W = 40;
    /** 支线传送带长度 */
    private static final int FEEDER_BELT_LEN = 112;
    /** 生成机 / 收纳箱尺寸 */
    private static final int GEN_SIZE = 76;
    /** 机械臂两节长度 */
    private static final double ARM_L1 = 78;
    private static final double ARM_L2 = 78;
    /** 机械臂一个完整取放周期（毫秒） */
    private static final long ARM_CYCLE_MS = 480;
    /** 工件的净荷宽度 */
    private static final int PAYLOAD_W = 40;
    /** 工件上一个协议头块的宽度 */
    private static final int HEAD_W = 14;
    /** 工件净荷高度 */
    private static final int PAYLOAD_H = 28;

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

    /** 头部产线 */
    private final List<Feeder> feeders = new ArrayList<>();
    /** 报文 ID → 已装上的协议层（工件随封装变厚、随解封装变薄） */
    private final Map<Integer, Set<String>> packetLayers = new HashMap<>();
    /** 报文 ID → 上一帧所在工位（用于侦测工件抵达新工位） */
    private final Map<Integer, String> lastStation = new HashMap<>();

    /** 火花粒子 */
    private static class Spark {
        double x, y, vx, vy, life, max;
    }

    /**
     * 一条头部产线：支线传送带 + 生成机（或收纳箱）+ 机械臂。
     */
    private static class Feeder {
        /** 服务的工位标签 */
        String station;
        /** 协议层：TCP / IP / ETH */
        String layer;
        /** 是不是拆包工位（把头部从工件上取走） */
        boolean strip;
        Color color;
        Color dark;

        /** 工位（主干上）坐标 */
        Point trunk;
        /** 生成机 / 收纳箱中心 */
        Point machine;
        /** 机械臂底座 */
        Point armBase;
        /** 支线传送带下沿 */
        int beltBottom;
        /** 支线传送带上沿 */
        int beltTop;

        /** 当前动作周期开始时间，-1 表示空闲 */
        long cycleStart = -1;
        /** 本轮动作对应的工件 */
        DataPacket packet;
        /** 是否已经完成放件 */
        boolean released;
    }

    public GameCanvas(GameEngine engine, MapManager mapManager,
                      List<DataPacket> dataPackets,
                      List<OreCart> oreCarts) {
        this.engine = engine;
        this.mapManager = mapManager;
        this.dataPackets = dataPackets;
        this.oreCarts = oreCarts;
        setBackground(FLOOR);
        buildFeeders();
        setPreferredSize(new Dimension(
                (int) (NetworkConfig.MAP_COLS * NetworkConfig.TILE_SIZE * scale),
                (int) (NetworkConfig.MAP_ROWS * NetworkConfig.TILE_SIZE * scale)
        ));
    }

    /** 根据工位坐标推导每条头部产线的几何布局 */
    private void buildFeeders() {
        for (Object[] def : FEEDER_DEFS) {
            String station = (String) def[0];
            String layer = (String) def[1];
            boolean strip = (Boolean) def[2];

            Point trunk = mapManager.findBuildingCoords(station);
            if (trunk == null) {
                continue;
            }

            Feeder f = new Feeder();
            f.station = station;
            f.layer = layer;
            f.strip = strip;
            f.color = colorOfLayer(layer);
            f.dark = darkOfLayer(layer);
            f.trunk = trunk;
            // 支线：主干 → 向上 112px → 生成机
            f.beltBottom = trunk.y - 44;
            f.beltTop = f.beltBottom - FEEDER_BELT_LEN;
            f.machine = new Point(trunk.x, f.beltTop - GEN_SIZE / 2 - 2);
            // 机械臂底座：支线右侧、主干上方
            f.armBase = new Point(trunk.x + 112, trunk.y - 84);
            feeders.add(f);
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

    /**
     * 报文抵达某台组装机：机器亮灯、炉火闪烁。
     * <p>如果该工位挂着头部产线，同时启动机械臂的取放动作。</p>
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
            for (int i = 0; i < 6; i++) {
                Spark s = new Spark();
                s.x = pos.x + (Math.random() - 0.5) * 30;
                s.y = pos.y + (Math.random() - 0.5) * 16;
                s.vx = (Math.random() - 0.5) * 150;
                s.vy = -Math.random() * 150 - 40;
                s.max = 0.25 + Math.random() * 0.35;
                s.life = s.max;
                sparks.add(s);
            }
        }

        // 启动对应头部产线的机械臂
        for (Feeder f : feeders) {
            if (!f.station.equals(tag)) {
                continue;
            }
            if (f.cycleStart > 0 && now - f.cycleStart < ARM_CYCLE_MS) {
                continue;
            }
            // 封装工位看协议栈是否真的加上了这一层；拆包工位看工件身上是否还带着这一层
            boolean applicable = f.strip
                    ? (packet != null && layersOf(packet).contains(f.layer))
                    : hasLayer(packet, f.layer);
            if (!applicable) {
                continue;
            }
            f.cycleStart = now;
            f.packet = packet;
            f.released = false;
        }
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

    /** 推进特效（由主循环每帧调用） */
    public void updateEffects() {
        animTime += 33;
        long now = System.currentTimeMillis();

        // 侦测工件抵达新工位 → 点亮对应的组装机 / 启动机械臂
        Set<Integer> alive = new LinkedHashSet<>();
        for (DataPacket p : dataPackets) {
            alive.add(p.getCartId());
            String tag = p.getCurrentTag();
            if (tag == null) {
                continue;
            }
            String prev = lastStation.put(p.getCartId(), tag);
            if (!tag.equals(prev)) {
                markWorking(tag, p);
            }
        }
        lastStation.keySet().removeIf(id -> !alive.contains(id));

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
        drawFeederBelts(g2);
        drawMachines(g2);
        drawWorkpieces(g2);
        drawFeederStations(g2);
        drawFeederArms(g2);
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

    // ===================== 支线传送带 =====================

    /** 分支产线的传送带：把头部块从生成机送到主干旁待命 */
    private void drawFeederBelts(Graphics2D g2) {
        for (Feeder f : feeders) {
            if (f.strip) {
                continue;
            }
            int x = f.trunk.x;

            g2.setPaint(new GradientPaint(x - FEEDER_BELT_W / 2, 0, BELT_TOP, x + FEEDER_BELT_W / 2, 0, BELT_BOTTOM));
            g2.fillRoundRect(x - FEEDER_BELT_W / 2, f.beltTop, FEEDER_BELT_W,
                    f.beltBottom - f.beltTop, 8, 8);

            // 横向滚动纹理（参考实现里的支线纹理）
            g2.setColor(new Color(BELT_TEX.getRed(), BELT_TEX.getGreen(), BELT_TEX.getBlue(), 95));
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            double offset = (animTime * TEX_SPEED * 1.4) % 16;
            for (double y = f.beltTop - 16; y < f.beltBottom + 16; y += 16) {
                double sy = y + offset;
                g2.drawLine(x - FEEDER_BELT_W / 2 + 2, (int) sy, x + FEEDER_BELT_W / 2 - 2, (int) sy);
            }

            g2.setColor(BELT_RAIL);
            g2.setStroke(new BasicStroke(2f));
            g2.drawLine(x - FEEDER_BELT_W / 2, f.beltTop, x - FEEDER_BELT_W / 2, f.beltBottom);
            g2.drawLine(x + FEEDER_BELT_W / 2, f.beltTop, x + FEEDER_BELT_W / 2, f.beltBottom);

            // 支线流向箭头（朝下，指向主干）
            g2.setColor(new Color(0x95, 0xA5, 0xA6, 130));
            Polygon arrow = new Polygon();
            arrow.addPoint(x, f.beltBottom - 6);
            arrow.addPoint(x - 7, f.beltBottom - 18);
            arrow.addPoint(x + 7, f.beltBottom - 18);
            g2.fillPolygon(arrow);
        }
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
                            r * NetworkConfig.TILE_SIZE + NetworkConfig.TILE_SIZE / 2);
                }
                drawMachine(g2, tag, p.x, p.y);
            }
        }
    }

    /** 俯视角度的一台组装机 */
    private void drawMachine(Graphics2D g2, String tag, double cx, double cy) {
        long now = System.currentTimeMillis();
        Long busyUntil = machineBusyUntil.get(tag);
        boolean busy = busyUntil != null && now < busyUntil;
        double phase = busy ? (now - machineBusySince.getOrDefault(tag, now)) / 900.0 : 0;

        BuildingZone zone = mapManager.getZoneForBuilding(tag);
        Color accent = zone != null ? zone.borderColor : new Color(0x63, 0x6E, 0x72);

        int w = 76, h = 56;
        int x = (int) cx - w / 2;
        int y = (int) cy - h / 2;

        // 机体
        g2.setPaint(new GradientPaint(0, y, MACHINE_TOP, 0, y + h, MACHINE_BOTTOM));
        g2.fillRoundRect(x, y, w, h, 8, 8);
        g2.setColor(MACHINE_STROKE);
        g2.setStroke(new BasicStroke(1.6f));
        g2.drawRoundRect(x, y, w, h, 8, 8);

        // 加工发光区
        if (busy) {
            double pulse = 0.2 + 0.5 * Math.abs(Math.sin(phase * Math.PI * 6));
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

        // 进出料口（传送带穿过机体的左右两侧）
        g2.setColor(BELT_RAIL);
        g2.fillRect(x - 8, (int) cy - BELT_HALF_WIDTH + 3, 10, BELT_HALF_WIDTH * 2 - 6);
        g2.fillRect(x + w - 2, (int) cy - BELT_HALF_WIDTH + 3, 10, BELT_HALF_WIDTH * 2 - 6);

        // 名称标签
        g2.setFont(new Font("微软雅黑", Font.BOLD, 9));
        g2.setColor(new Color(0x7F, 0x8C, 0x8D));
        String label = tag;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(label, (int) cx - fm.stringWidth(label) / 2, (int) cy + h / 2 + 14);

        // 接收端服务器的缓冲区格子
        if ("RX_ST".equals(tag)) {
            g2.setColor(BUSY_RED);
            for (int i = 0; i < serverBufferCount; i++) {
                g2.fillRect((int) cx - 20 + i * 8, (int) cy - 24, 5, 7);
            }
        }
    }

    // ===================== 生成机 / 收纳箱 =====================

    private void drawFeederStations(Graphics2D g2) {
        long now = System.currentTimeMillis();
        for (Feeder f : feeders) {
            int mx = f.machine.x;
            int my = f.machine.y;
            int x = mx - GEN_SIZE / 2;
            int y = my - GEN_SIZE / 2;

            // 产线标签
            g2.setFont(new Font("微软雅黑", Font.BOLD, 11));
            g2.setColor(f.dark);
            String title = f.layer + (f.strip ? " 回收" : " 产线");
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(title, mx - fm.stringWidth(title) / 2, y - 12);

            if (f.strip) {
                // 拆包工位：收纳箱
                g2.setPaint(new GradientPaint(0, y, BIN_COLOR, 0, y + GEN_SIZE, MACHINE_BOTTOM));
                g2.fillRoundRect(x, y, GEN_SIZE, GEN_SIZE, 6, 6);
                g2.setColor(MACHINE_STROKE);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(x, y, GEN_SIZE, GEN_SIZE, 6, 6);
                g2.setColor(f.color);
                g2.fillRoundRect(x + 14, y + 16, GEN_SIZE - 28, GEN_SIZE - 32, 4, 4);
                g2.setColor(f.dark);
                g2.fillOval(mx - 4, y - 9, 8, 8);
                continue;
            }

            // 生成机：机壳
            g2.setPaint(new GradientPaint(0, y, MACHINE_TOP, 0, y + GEN_SIZE, MACHINE_BOTTOM));
            g2.fillRoundRect(x, y, GEN_SIZE, GEN_SIZE, 6, 6);
            g2.setColor(MACHINE_STROKE);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x, y, GEN_SIZE, GEN_SIZE, 6, 6);

            // 内部脉动块：空闲时呼吸，工作时保持高亮
            boolean working = isFeederWorking(f, now);
            double pulse = working ? 0.8 : 0.35 + 0.35 * Math.sin(animTime / 420.0 + f.trunk.x);
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
    }

    private boolean isFeederWorking(Feeder f, long now) {
        return f.cycleStart > 0 && now - f.cycleStart < ARM_CYCLE_MS;
    }

    // ===================== 机械臂 =====================

    /**
     * 机械臂：俯身从支线上抓起头部块 → 摆到主干上钉在工件尾部 → 复位。
     * <p>封装工位：从支线抓块 → 放到工件尾部；拆包工位：从工件上抓块 → 放进收纳箱。</p>
     */
    private void drawFeederArms(Graphics2D g2) {
        long now = System.currentTimeMillis();
        for (Feeder f : feeders) {
            Point source = armSource(f);
            Point target = armTarget(f);
            double grip = 0;
            boolean carrying = false;

            if (f.cycleStart > 0) {
                double u = (now - f.cycleStart) / (double) ARM_CYCLE_MS;
                // 放件动作与帧率无关：即使某一帧掉帧越过放件点，头部也一定会被装上/拆掉
                if (u >= 0.64 && !f.released) {
                    f.released = true;
                    applyRelease(f);
                }
                if (u <= 1) {
                    double toDst = easeInOut(clamp01((u - 0.22) / 0.36));
                    double back = easeInOut(clamp01((u - 0.70) / 0.30));

                    double endX;
                    double endY;
                    if (u < 0.22) {
                        endX = source.x;
                        endY = source.y;
                    } else if (u < 0.58) {
                        endX = lerp(source.x, target.x, toDst);
                        endY = lerp(source.y, target.y, toDst);
                    } else if (u < 0.70) {
                        endX = target.x;
                        endY = target.y;
                    } else {
                        endX = lerp(target.x, source.x, back);
                        endY = lerp(target.y, source.y, back);
                    }

                    // 0.22 抓起，0.64 放下
                    if (u >= 0.22 && u < 0.64) {
                        grip = 1;
                        carrying = true;
                    }
                    drawArmLink(g2, f.armBase.x, f.armBase.y, endX, endY, ARM_L1, ARM_L2, grip);
                    if (carrying) {
                        drawHeaderBlock(g2, endX, endY, f);
                    }
                    continue;
                }
                f.cycleStart = -1;
                f.packet = null;
            }

            // 空闲：机械臂停在取件点待命
            drawArmLink(g2, f.armBase.x, f.armBase.y, source.x, source.y, ARM_L1, ARM_L2, 0);
            if (!f.strip) {
                // 支带上循环下落的待命头部块
                double cycle = (animTime % 2400) / 2400.0;
                double by = lerp(f.beltTop + 20, source.y, cycle);
                drawHeaderBlock(g2, source.x, by, f);
            }
        }
    }

    /** 取件点：封装工位在支线传送带下端，拆包工位在工件上 */
    private Point armSource(Feeder f) {
        if (f.strip) {
            DataPacket p = f.packet;
            double x = p != null ? p.getX() : f.trunk.x;
            double y = p != null ? p.getY() : f.trunk.y;
            return new Point((int) Math.round(x + layerOffset(p, f.layer)), (int) Math.round(y));
        }
        return new Point(f.trunk.x, f.beltBottom - 26);
    }

    /** 放件点：封装工位放在工件尾部，拆包工位放进收纳箱 */
    private Point armTarget(Feeder f) {
        if (f.strip) {
            return new Point(f.machine.x, f.machine.y + GEN_SIZE / 2 - 10);
        }
        DataPacket p = f.packet;
        double x = p != null ? p.getX() : f.trunk.x;
        double y = p != null ? p.getY() : f.trunk.y;
        return new Point((int) Math.round(x + layerOffset(p, f.layer)), (int) Math.round(y));
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

    /** 放件瞬间：给工件加上（或拆掉）这一层协议头 */
    private void applyRelease(Feeder f) {
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

    /** 支线上的头部块 */
    private void drawHeaderBlock(Graphics2D g2, double x, double y, Feeder f) {
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

    // ===================== 工件（数据包） =====================

    private void drawWorkpieces(Graphics2D g2) {
        for (DataPacket packet : dataPackets) {
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

    /** 取报文的协议层集合（首次出现时按报文自身状态初始化） */
    private Set<String> layersOf(DataPacket packet) {
        Set<String> layers = packetLayers.get(packet.getCartId());
        if (layers != null) {
            return layers;
        }
        layers = new LinkedHashSet<>();
        int stage = packet.getStage();
        // 已经走过的封装工位，头部直接算装上了
        if (packet.isHasTcp() && stage > 13) {
            layers.add("TCP");
        }
        if (packet.isHasIp() && stage > 14) {
            layers.add("IP");
        }
        if (packet.isHasEther() && stage > 19) {
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

        // 头部产线说明
        int bx = x + 110;
        int by = 26;
        g2.setColor(new Color(0x2C, 0x3E, 0x50));
        g2.drawString("头部产线（机械臂自动封装）", bx, by);
        by += 18;
        drawLegendItem(g2, bx, by, TCP_COLOR, "TCP 头（蓝色）");
        drawLegendItem(g2, bx, by + 16, IP_COLOR, "IP 头（绿色）");
        drawLegendItem(g2, bx, by + 32, ETH_COLOR, "ETH 头（紫色）");
    }

    private void drawLegendItem(Graphics2D g2, int x, int y, Color color, String text) {
        g2.setColor(color);
        g2.fillRect(x, y - 8, 11, 11);
        g2.setColor(new Color(0x55, 0x5F, 0x66));
        g2.drawString(text, x + 17, y);
    }
}
