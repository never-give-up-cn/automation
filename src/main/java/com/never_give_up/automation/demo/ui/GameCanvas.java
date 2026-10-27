package com.never_give_up.automation.demo.ui;

import com.never_give_up.automation.demo.config.NetworkConfig;
import com.never_give_up.automation.demo.engine.GameEngine;
import com.never_give_up.automation.demo.engine.MapManager;
import com.never_give_up.automation.demo.engine.ProductionLine;
import com.never_give_up.automation.demo.winModel.DataPacket;
import com.never_give_up.automation.demo.winModel.OreCart;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 厂区画布：把协议栈画成一条自动化产线。
 * <p>
 * 每个协议节点是一台「组装机」，节点之间由传送带连接，数据包（工件）
 * 骑在传送带上依次经过每一台组装机，抵达时机械臂取放工件、机器内
 * 炉火闪烁，随后工件继续向下一站流去。
 * </p>
 */
public class GameCanvas extends JPanel {

    // ===== 配色（与参考的自动化产线主题一致）=====
    private static final Color FLOOR = new Color(0xF4, 0xF7, 0xF6);
    private static final Color FLOOR_LINE = new Color(0xD7, 0xDF, 0xE2);
    private static final Color FLOOR_BASE = new Color(0xDF, 0xE6, 0xE9);

    private static final Color BELT_TOP = new Color(0x4A, 0x55, 0x68);
    private static final Color BELT_BOTTOM = new Color(0x2D, 0x37, 0x48);
    private static final Color BELT_RAIL = new Color(0x1A, 0x20, 0x2C);
    private static final Color BELT_TEX = new Color(0x8F, 0xA3, 0xB0);
    private static final Color BELT_LEG = new Color(0x63, 0x6E, 0x72);

    private static final Color MACHINE_TOP = new Color(0xEE, 0xF2, 0xF5);
    private static final Color MACHINE_BOTTOM = new Color(0xB2, 0xBE, 0xC3);
    private static final Color MACHINE_STROKE = new Color(0x63, 0x6E, 0x72);
    private static final Color MACHINE_BASE = new Color(0x4A, 0x5A, 0x6A);

    private static final Color ARM_TOP = new Color(0xF7, 0xB7, 0x31);
    private static final Color ARM_BOTTOM = new Color(0xD3, 0x54, 0x00);
    private static final Color ARM2_TOP = new Color(0xE6, 0x7E, 0x22);
    private static final Color ARM2_BOTTOM = new Color(0xA8, 0x43, 0x00);

    private static final Color GLOW = new Color(0xFE, 0xCA, 0x57);
    private static final Color SPARK = new Color(0xFE, 0xCA, 0x57);
    private static final Color OK_GREEN = new Color(0x27, 0xAE, 0x60);
    private static final Color BUSY_RED = new Color(0xE7, 0x4C, 0x3C);
    private static final Color BUSY_ORANGE = new Color(0xF3, 0x9C, 0x12);

    private static final int BELT_HALF_WIDTH = 13;
    private static final double TEX_PERIOD = 42;
    /** 传送带纹理滚动速度（像素/毫秒），约 15 像素/帧 */
    private static final double TEX_SPEED = 0.45;

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
    /** 工位 → 加工结束时间戳（用于机械臂动作与炉火闪烁） */
    private final Map<String, Long> machineBusyUntil = new HashMap<>();
    /** 工位 → 开始加工的时间戳 */
    private final Map<String, Long> machineBusySince = new HashMap<>();
    /** 火花粒子 */
    private final List<Spark> sparks = new ArrayList<>();

    /** 火花粒子 */
    private static class Spark {
        double x, y, vx, vy, life, max;
    }

    public GameCanvas(GameEngine engine, MapManager mapManager,
                      List<DataPacket> dataPackets,
                      List<OreCart> oreCarts) {
        this.engine = engine;
        this.mapManager = mapManager;
        this.dataPackets = dataPackets;
        this.oreCarts = oreCarts;
        setBackground(FLOOR);
        setPreferredSize(new Dimension(
                (int) (NetworkConfig.MAP_COLS * NetworkConfig.TILE_SIZE * scale),
                (int) (NetworkConfig.MAP_ROWS * NetworkConfig.TILE_SIZE * scale)
        ));
    }

    public void updateServerBuffer(int bufferCount) {
        this.serverBufferCount = bufferCount;
    }

    /** 报文抵达某台组装机：点亮机器、启动机械臂、迸发火花 */
    public void markWorking(String tag) {
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
    }

    /** 推进特效（由主循环每帧调用） */
    public void updateEffects() {
        animTime += 33;
        long now = System.currentTimeMillis();
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
        drawWorkpieces(g2);
        drawMachines(g2);
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

    // ===================== 传送带 =====================

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
                    drawVerticalBelt(g2, a, b, belt.isForward());
                } else {
                    drawHorizontalBelt(g2, a, b, belt.isForward());
                }
            }
        }
    }

    private void drawHorizontalBelt(Graphics2D g2, Point a, Point b, boolean forward) {
        int y = a.y;
        int x1 = Math.min(a.x, b.x);
        int x2 = Math.max(a.x, b.x);
        int dir = b.x >= a.x ? 1 : -1;

        // 支撑腿
        g2.setColor(BELT_LEG);
        for (int x = x1 + 30; x < x2; x += 130) {
            g2.fillRect(x - 4, y + BELT_HALF_WIDTH, 8, 16);
            g2.fillRect(x - 10, y + BELT_HALF_WIDTH + 14, 20, 5);
        }

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

        drawFlowArrow(g2, (x1 + x2) / 2, y, dir);
    }

    private void drawVerticalBelt(Graphics2D g2, Point a, Point b, boolean forward) {
        int x = a.x;
        int y1 = Math.min(a.y, b.y);
        int y2 = Math.max(a.y, b.y);
        int dir = b.y >= a.y ? 1 : -1;

        // 侧向支撑
        g2.setColor(BELT_LEG);
        for (int y = y1 + 30; y < y2; y += 130) {
            g2.fillRect(x + BELT_HALF_WIDTH, y - 4, 16, 8);
        }

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

    /** 一台组装机 */
    private void drawMachine(Graphics2D g2, String tag, double cx, double cy) {
        long now = System.currentTimeMillis();
        Long busyUntil = machineBusyUntil.get(tag);
        boolean busy = busyUntil != null && now < busyUntil;
        double phase = busy ? (now - machineBusySince.getOrDefault(tag, now)) / 900.0 : 0;

        BuildingZone zone = mapManager.getZoneForBuilding(tag);
        Color accent = zone != null ? zone.borderColor : new Color(0x63, 0x6E, 0x72);

        // 底座
        g2.setColor(MACHINE_BASE);
        g2.fillRoundRect((int) (cx - 34), (int) (cy + 14), 68, 12, 5, 5);
        g2.setColor(new Color(0x63, 0x6E, 0x72));
        g2.fillRect((int) (cx - 26), (int) (cy + 26), 7, 12);
        g2.fillRect((int) (cx + 19), (int) (cy + 26), 7, 12);

        // 机体
        g2.setPaint(new GradientPaint(0, (float) (cy - 40), MACHINE_TOP, 0, (float) (cy + 14), MACHINE_BOTTOM));
        g2.fillRoundRect((int) (cx - 34), (int) (cy - 40), 68, 54, 8, 8);
        g2.setColor(MACHINE_STROKE);
        g2.setStroke(new BasicStroke(1.6f));
        g2.drawRoundRect((int) (cx - 34), (int) (cy - 40), 68, 54, 8, 8);

        // 顶盖 + 分区色条
        g2.setPaint(new GradientPaint(0, (float) (cy - 46), MACHINE_TOP, 0, (float) (cy - 36), MACHINE_BOTTOM));
        g2.fillRoundRect((int) (cx - 38), (int) (cy - 48), 76, 14, 6, 6);
        g2.setColor(MACHINE_STROKE);
        g2.drawRoundRect((int) (cx - 38), (int) (cy - 48), 76, 14, 6, 6);
        g2.setColor(accent);
        g2.fillRoundRect((int) (cx - 32), (int) (cy - 45), 64, 5, 3, 3);

        // 加工发光区
        if (busy) {
            double pulse = 0.25 + 0.55 * Math.abs(Math.sin(phase * Math.PI * 6));
            g2.setColor(new Color(GLOW.getRed(), GLOW.getGreen(), GLOW.getBlue(), (int) (255 * pulse)));
            g2.fillRoundRect((int) (cx - 27), (int) (cy - 32), 54, 38, 6, 6);
        }
        g2.setColor(new Color(0x95, 0xA5, 0xA6));
        g2.fillRect((int) (cx - 27), (int) (cy + 2), 54, 4);

        // 状态指示灯
        if (busy) {
            boolean red = Math.sin(phase * Math.PI * 8) > 0;
            g2.setColor(red ? BUSY_RED : BUSY_ORANGE);
        } else {
            g2.setColor(OK_GREEN);
        }
        g2.fillOval((int) (cx - 5), (int) (cy - 40), 10, 10);
        g2.setColor(new Color(0x1A, 0x25, 0x2F));
        g2.drawOval((int) (cx - 5), (int) (cy - 40), 10, 10);

        // 进出料口
        g2.setColor(BELT_RAIL);
        g2.fillRect((int) (cx - 38), (int) (cy - BELT_HALF_WIDTH + 2), 6, BELT_HALF_WIDTH * 2 - 4);
        g2.fillRect((int) (cx + 32), (int) (cy - BELT_HALF_WIDTH + 2), 6, BELT_HALF_WIDTH * 2 - 4);

        // 名称标签
        g2.setFont(new Font("微软雅黑", Font.BOLD, 9));
        g2.setColor(new Color(0x7F, 0x8C, 0x8D));
        String label = tag;
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(label, (int) (cx - fm.stringWidth(label) / 2), (int) (cy + 48));

        // 接收端服务器的缓冲区格子
        if ("RX_ST".equals(tag)) {
            g2.setColor(BUSY_RED);
            for (int i = 0; i < serverBufferCount; i++) {
                g2.fillRect((int) (cx - 20 + i * 8), (int) (cy - 30), 5, 7);
            }
        }
    }

    // ===================== 机械臂 =====================

    private void drawArms(Graphics2D g2) {
        String[][] layout = mapManager.getBuildingLayout();
        for (int r = 0; r < NetworkConfig.MAP_ROWS; r++) {
            for (int c = 0; c < NetworkConfig.MAP_COLS; c++) {
                String tag = layout[r][c];
                if ("NONE".equals(tag)) {
                    continue;
                }
                Point p = mapManager.findBuildingCoords(tag);
                if (p == null) {
                    continue;
                }
                drawArm(g2, tag, p.x, p.y);
            }
        }
    }

    /**
     * 机械臂：空闲时轻轻摆动，加工时俯身到传送带上取放工件。
     */
    private void drawArm(Graphics2D g2, String tag, double cx, double cy) {
        long now = System.currentTimeMillis();
        Long busyUntil = machineBusyUntil.get(tag);
        boolean busy = busyUntil != null && now < busyUntil;

        // 底座
        double baseX = cx - 40;
        double baseY = cy - 2;
        g2.setPaint(new GradientPaint(0, (float) (baseY - 4), MACHINE_BASE, 0, (float) (baseY + 26), BELT_BOTTOM));
        g2.fillRoundRect((int) (baseX - 11), (int) (baseY - 4), 22, 30, 5, 5);
        g2.setColor(BELT_BOTTOM);
        g2.fillRoundRect((int) (baseX - 14), (int) (baseY - 8), 28, 8, 4, 4);

        double homeX = baseX - 20;
        double homeY = baseY - 30;

        double targetX;
        double targetY;
        double grip;

        if (busy) {
            double phase = (now - machineBusySince.getOrDefault(tag, now)) / 900.0;
            // 0~0.35 俯身取件，0.35~0.7 抬起，0.7~1 复位
            double dip;
            if (phase < 0.35) {
                dip = phase / 0.35;
            } else if (phase < 0.7) {
                dip = 1 - (phase - 0.35) / 0.35;
            } else {
                dip = 0;
            }
            dip = dip * dip * (3 - 2 * dip);
            targetX = homeX + (cx - 26 - homeX) * dip;
            targetY = homeY + (cy - 2 - homeY) * dip;
            grip = dip > 0.55 ? 1 : 0;
        } else {
            // 空闲：慢慢摆动
            double sway = Math.sin(animTime / 620.0 + cx / 90.0) * 7;
            targetX = homeX + sway;
            targetY = homeY + Math.abs(sway) * 0.4;
            grip = 0;
        }

        drawArmLink(g2, baseX, baseY, targetX, targetY, 30, 27, grip);
    }

    /**
     * 两连杆逆运动学绘制机械臂（与参考实现一致）。
     */
    private void drawArmLink(Graphics2D g2, double sx, double sy, double tx, double ty,
                             double L1, double L2, double grip) {
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
        g2.setColor(new Color(0xA0, 0x40, 0x00));
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
        g2.setColor(new Color(0x71, 0x80, 0x96));
        g2.fillRect((int) (px + nx * (4 + off) - 4), (int) (py + ny * (4 + off) - 2), 8, 4);
        g2.fillRect((int) (px - nx * (4 + off) - 4), (int) (py - ny * (4 + off) - 2), 8, 4);
    }

    // ===================== 工件（数据包） =====================

    private void drawWorkpieces(Graphics2D g2) {
        for (DataPacket packet : dataPackets) {
            int cx = (int) packet.getX();
            int cy = (int) packet.getY();

            Color color = getPacketColor(packet);
            // 工件本体
            g2.setColor(color);
            g2.fillRoundRect(cx - 15, cy - 12, 30, 24, 4, 4);
            g2.setColor(color.darker());
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(cx - 15, cy - 12, 30, 24, 4, 4);
            g2.setColor(new Color(255, 255, 255, 120));
            g2.fillRoundRect(cx - 9, cy - 7, 18, 13, 3, 3);

            // 协议分层标签
            drawProtocolLayers(g2, packet, cx, cy);

            // 回程件用蓝色描边区分
            if (packet.isReturnTrip()) {
                g2.setColor(new Color(0x2E, 0x86, 0xC1));
                g2.setStroke(new BasicStroke(1.8f));
                g2.drawRoundRect(cx - 18, cy - 15, 36, 30, 6, 6);
            }

            drawPacketLabel(g2, packet, cx, cy);
        }
    }

    private void drawProtocolLayers(Graphics2D g2, DataPacket packet, int cx, int cy) {
        int bx = cx - 15;
        int by = cy + 12;
        if (packet.isHasApp()) {
            g2.setColor(new Color(0x64, 0xFF, 0x64));
            g2.fillRect(bx, by, 6, 4);
            bx += 6;
        }
        if (packet.isHasTcp()) {
            g2.setColor(Color.ORANGE);
            g2.fillRect(bx, by, 7, 4);
            bx += 7;
        }
        if (packet.isHasIp()) {
            g2.setColor(Color.YELLOW);
            g2.fillRect(bx, by, 6, 4);
            bx += 6;
        }
        if (packet.isHasEther()) {
            g2.setColor(new Color(0x00, 0xA0, 0xFF));
            g2.fillRect(bx, by, 8, 4);
            bx += 8;
        }
        if (packet.isHasLlc()) {
            g2.setColor(Color.GREEN);
            g2.fillRect(bx, by, 6, 4);
            bx += 6;
        }
        if (packet.isHasFcs()) {
            g2.setColor(Color.GREEN.darker());
            g2.fillRect(bx, by, 6, 4);
        }
    }

    private void drawPacketLabel(Graphics2D g2, DataPacket packet, int cx, int cy) {
        g2.setFont(new Font("微软雅黑", Font.BOLD, 9));
        String direction = packet.isReturnTrip() ? "[回程]"
                : (packet.getWaitInQueueTimer() > 0 ? "[排队]" : "[发送]");
        String label = packet.getCartType() + " " + direction + " TTL:" + packet.getTtl();
        FontMetrics fm = g2.getFontMetrics();
        int w = fm.stringWidth(label) + 8;

        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(cx - w / 2, cy - 32, w, 14);
        g2.setColor(packet.isReturnTrip() ? new Color(0x8E, 0xD8, 0xF8) : new Color(0x2B, 0x2B, 0x2B));
        g2.drawString(label, cx - w / 2 + 4, cy - 21);
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
            double r = 1.5 + op * 2.2;
            g2.fillOval((int) (s.x - r), (int) (s.y - r), (int) (r * 2), (int) (r * 2));
        }
    }

    // ===================== 图例 =====================

    private void drawLegend(Graphics2D g2) {
        List<BuildingZone> zones = new ArrayList<>();
        String[][] layout = mapManager.getBuildingLayout();
        for (int r = 0; r < NetworkConfig.MAP_ROWS; r++) {
            for (int c = 0; c < NetworkConfig.MAP_COLS; c++) {
                String tag = layout[r][c];
                if ("NONE".equals(tag)) {
                    continue;
                }
                BuildingZone zone = mapManager.getZoneForBuilding(tag);
                if (zone != null && !zones.contains(zone)) {
                    zones.add(zone);
                }
            }
        }
        if (zones.isEmpty()) {
            return;
        }

        int x = 16;
        int y = 20;
        g2.setFont(new Font("微软雅黑", Font.BOLD, 10));
        g2.setColor(new Color(0x7F, 0x8C, 0x8D));
        g2.drawString("分区图例", x, y);
        y += 14;
        for (BuildingZone zone : zones) {
            g2.setColor(zone.borderColor);
            g2.fillRect(x, y - 7, 10, 10);
            g2.setColor(new Color(0x5D, 0x6D, 0x7E));
            g2.drawString(zone.name, x + 15, y + 1);
            y += 14;
        }
    }
}
