package com.never_give_up.automation.demo.ui;

import com.never_give_up.automation.demo.config.NetworkConfig;
import com.never_give_up.automation.demo.engine.GameEngine;
import com.never_give_up.automation.demo.engine.MapManager;
import com.never_give_up.automation.demo.winModel.DataPacket;
import com.never_give_up.automation.demo.winModel.OreCart;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameCanvas extends JPanel {
    private final GameEngine engine;
    private final MapManager mapManager;
    private final List<DataPacket> dataPackets;  // 改为 DataPacket
    private final List<OreCart> oreCarts;

    private double scale = 1.0;
    private int viewOffsetX = 0;
    private int viewOffsetY = 0;
    private int serverBufferCount = 0;

    public GameCanvas(GameEngine engine, MapManager mapManager,
                      List<DataPacket> dataPackets,  // 改为 DataPacket
                      List<OreCart> oreCarts) {
        this.engine = engine;
        this.mapManager = mapManager;
        this.dataPackets = dataPackets;
        this.oreCarts = oreCarts;
        setBackground(new Color(18, 20, 26));
        setPreferredSize(new Dimension(
                (int) (NetworkConfig.MAP_COLS * NetworkConfig.TILE_SIZE * scale),
                (int) (NetworkConfig.MAP_ROWS * NetworkConfig.TILE_SIZE * scale)
        ));
    }

    public void updateServerBuffer(int bufferCount) {
        this.serverBufferCount = bufferCount;
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
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.translate(viewOffsetX, viewOffsetY);
        g2.scale(scale, scale);

        drawZoneBackgrounds(g2);
        drawGrid(g2);
        drawBuildings(g2);
        drawOreCarts(g2);
        drawDataPackets(g2);
        drawWanIndicator(g2);
    }

    private void drawZoneBackgrounds(Graphics2D g2) {
        Map<BuildingZone, Rectangle> zoneBounds = new HashMap<>();

        for (int r = 0; r < NetworkConfig.MAP_ROWS; r++) {
            for (int c = 0; c < NetworkConfig.MAP_COLS; c++) {
                String tag = mapManager.getBuildingLayout()[r][c];
                if (tag.equals("NONE")) continue;

                BuildingZone zone = mapManager.getZoneForBuilding(tag);
                if (zone != null) {
                    int x = c * NetworkConfig.TILE_SIZE;
                    int y = r * NetworkConfig.TILE_SIZE;
                    Rectangle rect = zoneBounds.get(zone);
                    if (rect == null) {
                        rect = new Rectangle(x, y, NetworkConfig.TILE_SIZE, NetworkConfig.TILE_SIZE);
                        zoneBounds.put(zone, rect);
                    } else {
                        rect.setBounds(
                                Math.min(rect.x, x),
                                Math.min(rect.y, y),
                                Math.max(rect.x + rect.width, x + NetworkConfig.TILE_SIZE) - Math.min(rect.x, x),
                                Math.max(rect.y + rect.height, y + NetworkConfig.TILE_SIZE) - Math.min(rect.y, y)
                        );
                    }
                }
            }
        }

        for (Map.Entry<BuildingZone, Rectangle> entry : zoneBounds.entrySet()) {
            BuildingZone zone = entry.getKey();
            Rectangle rect = entry.getValue();
            rect.grow(5, 5);

            g2.setColor(zone.bgColor);
            g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);
            g2.setColor(zone.borderColor);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);
            g2.setFont(new Font("微软雅黑", Font.BOLD, 14));
            g2.setColor(zone.borderColor);
            g2.drawString(zone.name, rect.x + 10, rect.y + 25);
        }

        g2.setStroke(new BasicStroke(1f));
    }

    private void drawGrid(Graphics2D g2) {
        g2.setColor(new Color(40, 45, 55));
        for (int r = 0; r <= NetworkConfig.MAP_ROWS; r++) {
            g2.drawLine(0, r * NetworkConfig.TILE_SIZE,
                    NetworkConfig.MAP_COLS * NetworkConfig.TILE_SIZE, r * NetworkConfig.TILE_SIZE);
        }
        for (int c = 0; c <= NetworkConfig.MAP_COLS; c++) {
            g2.drawLine(c * NetworkConfig.TILE_SIZE, 0,
                    c * NetworkConfig.TILE_SIZE, NetworkConfig.MAP_ROWS * NetworkConfig.TILE_SIZE);
        }
    }

    private void drawBuildings(Graphics2D g2) {
        for (int r = 0; r < NetworkConfig.MAP_ROWS; r++) {
            for (int c = 0; c < NetworkConfig.MAP_COLS; c++) {
                int x = c * NetworkConfig.TILE_SIZE;
                int y = r * NetworkConfig.TILE_SIZE;
                String tag = mapManager.getBuildingLayout()[r][c];

                if (tag.equals("NONE")) continue;

                BuildingZone zone = mapManager.getZoneForBuilding(tag);
                Color baseColor = zone != null ? zone.borderColor : new Color(100, 100, 100);

                g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 80));
                g2.fillRect(x + 2, y + 2, NetworkConfig.TILE_SIZE - 4, NetworkConfig.TILE_SIZE - 4);
                g2.setColor(baseColor);
                g2.drawRect(x + 2, y + 2, NetworkConfig.TILE_SIZE - 4, NetworkConfig.TILE_SIZE - 4);
                g2.setFont(new Font("Consolas", Font.BOLD, 8));
                g2.setColor(Color.WHITE);

                if (tag.equals("PC_FACTORY")) {
                    g2.setColor(new Color(0, 130, 200));
                    g2.fillRect(x + 2, y + 2, NetworkConfig.TILE_SIZE - 4, NetworkConfig.TILE_SIZE - 4);
                    g2.setColor(Color.WHITE);
                    g2.drawString("[PC] 源PC", x + 4, y + 24);
                } else if (tag.equals("RX_ST")) {
                    g2.setColor(new Color(190, 30, 50));
                    g2.fillRect(x + 2, y + 2, NetworkConfig.TILE_SIZE - 4, NetworkConfig.TILE_SIZE - 4);
                    g2.setColor(Color.YELLOW);
                    g2.drawString("[SRV] 服务器", x + 2, y + 24);
                    for (int b = 0; b < serverBufferCount; b++) {
                        g2.setColor(Color.RED);
                        g2.fillRect(x + 4 + (b * 6), y + 4, 5, 6);
                    }
                } else if (tag.startsWith("MINER_H")) {
                    g2.setColor(Color.CYAN);
                    g2.drawString("[H] 矿机", x + 4, y + 24);
                } else if (tag.startsWith("MINER_S")) {
                    g2.setColor(Color.GREEN);
                    g2.drawString("[S] 矿机", x + 4, y + 24);
                } else if (tag.startsWith("DHCP_")) {
                    g2.setColor(new Color(100, 100, 255));
                    g2.fillRect(x + 2, y + 2, NetworkConfig.TILE_SIZE - 4, NetworkConfig.TILE_SIZE - 4);
                    g2.drawString(tag, x + 3, y + 24);
                } else if (tag.startsWith("ROUTER")) {
                    g2.setColor(new Color(200, 100, 0));
                    g2.fillRect(x + 2, y + 2, NetworkConfig.TILE_SIZE - 4, NetworkConfig.TILE_SIZE - 4);
                    g2.drawString(tag, x + 3, y + 24);
                } else if (tag.equals("DNS_CLIENT") || tag.equals("DNS_LOCAL") ||
                        tag.equals("DNS_ROOT") || tag.equals("DNS_AUTH")) {
                    g2.setColor(new Color(0, 200, 200));
                    g2.fillRect(x + 2, y + 2, NetworkConfig.TILE_SIZE - 4, NetworkConfig.TILE_SIZE - 4);
                    g2.setColor(Color.BLACK);
                    g2.drawString(tag, x + 3, y + 24);
                } else {
                    String displayText = tag.length() > 8 ? tag.substring(0, 6) : tag;
                    g2.drawString(displayText, x + 4, y + 24);
                }
            }
        }
    }

    private void drawOreCarts(Graphics2D g2) {
        for (OreCart cart : oreCarts) {
            g2.setColor(cart.getOreType().equals("HELLO") ? Color.CYAN : Color.GREEN);
            g2.fillOval((int) cart.getX() - 5, (int) cart.getY() - 5, 10, 10);
        }
    }

    // 修改这里：使用 DataPacket 替代 DataCart
    private void drawDataPackets(Graphics2D g2) {
        if (dataPackets.isEmpty()) {
            // 每60帧打印一次
            if (System.currentTimeMillis() % 6000 < 100) {
                System.out.println("No data packets to draw");
            }
            return;
        }

        System.out.println("Drawing " + dataPackets.size() + " packets");
        for (DataPacket packet : dataPackets) {  // 改为 DataPacket
            int cx = (int) packet.getX();
            int cy = (int) packet.getY();

            Color color = getPacketColor(packet);
            g2.setColor(color);
            g2.fillOval(cx - 7, cy - 7, 14, 14);

            drawProtocolLayers(g2, packet, cx, cy);
            drawPacketLabel(g2, packet, cx, cy);
        }
    }

    // 修改参数类型为 DataPacket
    private Color getPacketColor(DataPacket packet) {
        if (packet.getWaitInQueueTimer() > 0) return Color.RED;
        if (packet.getCartType().equals("ICMP_TIMEEXCEEDED")) return new Color(200, 50, 50);
        if (packet.getCartType().equals("ICMP_ECHO_REQ")) return new Color(100, 100, 255);
        if (packet.getCartType().equals("ICMP_ECHO_REPLY")) return new Color(50, 200, 50);
        if (packet.getCartType().startsWith("DHCP")) return Color.MAGENTA;
        if (packet.getCartType().startsWith("SYN")) return Color.CYAN;
        if (packet.getCartType().startsWith("FIN")) return Color.PINK;
        if (packet.getCartType().contains("ACK")) return Color.GREEN;
        if (packet.getCartType().startsWith("DNS")) return new Color(0, 200, 200);
        if (packet.isRetransmission()) return Color.ORANGE;
        if (packet.getCartType().startsWith("TLS_")) return new Color(255, 165, 0);
        if (packet.getCartType().equals("HTTP_GET") || packet.getCartType().equals("HTTP_200_OK"))
            return new Color(150, 0, 200);
        if (packet.getCartType().equals("UDP_DATA")) return new Color(50, 150, 255);
        return Color.LIGHT_GRAY;
    }

    // 修改参数类型为 DataPacket
    private void drawProtocolLayers(Graphics2D g2, DataPacket packet, int cx, int cy) {
        int bx = cx - 30;
        if (packet.isHasApp()) {
            g2.setColor(new Color(100, 255, 100));
            g2.fillRect(bx, cy - 5, 6, 10);
            bx += 6;
        }
        if (packet.isHasTcp()) {
            g2.setColor(Color.ORANGE);
            g2.fillRect(bx, cy - 5, 7, 10);
            bx += 7;
        }
        if (packet.isHasIp()) {
            g2.setColor(Color.YELLOW);
            g2.fillRect(bx, cy - 5, 6, 10);
            bx += 6;
        }
        if (packet.isHasEther()) {
            g2.setColor(new Color(0, 160, 255));
            g2.fillRect(bx, cy - 5, 8, 10);
            bx += 8;
        }
        if (packet.isHasLlc()) {
            g2.setColor(Color.GREEN);
            g2.fillRect(bx, cy - 5, 6, 10);
            bx += 6;
        }
        if (packet.isHasFcs()) {
            g2.setColor(Color.GREEN.darker());
            g2.fillRect(bx, cy - 5, 6, 10);
        }
    }

    // 修改参数类型为 DataPacket
    private void drawPacketLabel(Graphics2D g2, DataPacket packet, int cx, int cy) {
        g2.setFont(new Font("微软雅黑", Font.BOLD, 10));
        String direction = packet.isReturnTrip() ? "[R] 回传" :
                (packet.getWaitInQueueTimer() > 0 ? "[W] 排队" : "[S] 发送");
        String label = String.format("%s %s TTL:%d", packet.getCartType(), direction, packet.getTtl());

        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(cx - 55, cy - 25, g2.getFontMetrics().stringWidth(label) + 8, 16);
        g2.setColor(packet.getWaitInQueueTimer() > 0 ? Color.RED : Color.YELLOW);
        g2.drawString(label, cx - 53, cy - 14);
    }

    private void drawWanIndicator(Graphics2D g2) {
        g2.setColor(new Color(255, 100, 0, 15));
        g2.fillRect(21 * NetworkConfig.TILE_SIZE, 0,
                14 * NetworkConfig.TILE_SIZE, NetworkConfig.MAP_ROWS * NetworkConfig.TILE_SIZE);

        int wanCarCount = 0;
        for (DataPacket packet : dataPackets) {  // 改为 DataPacket
            int col = (int) (packet.getX() / NetworkConfig.TILE_SIZE);
            if (!packet.isReturnTrip() && packet.getStage() >= 25 && packet.getStage() <= 31 && col >= 21 && col <= 34) {
                wanCarCount++;
            }
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("微软雅黑", Font.BOLD, 14));
        g2.drawString("[CAR] 公网车辆: " + wanCarCount + "/" + NetworkConfig.WAN_BOTTLE_NECK_MAX,
                22 * NetworkConfig.TILE_SIZE, 30);
    }
}