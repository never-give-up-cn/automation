package com.never_give_up.automation.demo;

import com.never_give_up.automation.demo.model.BuildingZone;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class GameCanvas extends JPanel {
    private final DataCartFactoryGame game;

    public GameCanvas(DataCartFactoryGame game) {
        this.game = game;
        setBackground(new Color(18, 20, 26));
    }

    // 获取逻辑坐标（考虑平移、缩放和滚动条）
    public int getLogicalX(int screenX) {
        // 获取当前滚动条位置
        JScrollPane scrollPane = (JScrollPane) getParent().getParent();
        JScrollBar hBar = scrollPane.getHorizontalScrollBar();
        int scrollX = hBar.getValue();

        // 计算实际画布上的坐标（考虑滚动条偏移）
        double canvasX = screenX + scrollX;

        // 再转换为逻辑坐标（考虑缩放和视图偏移）
        return (int) ((canvasX - game.viewOffsetX) / game.canvasScale);
    }

    public int getLogicalY(int screenY) {
        // 获取当前滚动条位置
        JScrollPane scrollPane = (JScrollPane) getParent().getParent();
        JScrollBar vBar = scrollPane.getVerticalScrollBar();
        int scrollY = vBar.getValue();

        // 计算实际画布上的坐标（考虑滚动条偏移）
        double canvasY = screenY + scrollY;

        // 再转换为逻辑坐标（考虑缩放和视图偏移）
        return (int) ((canvasY - game.viewOffsetY) / game.canvasScale);
    }

    // 获取屏幕坐标（用于绘制）
    private int toScreenX(int logicalX) {
        return (int) (logicalX * game.canvasScale) + game.viewOffsetX;
    }

    private int toScreenY(int logicalY) {
        return (int) (logicalY * game.canvasScale) + game.viewOffsetY;
    }

    // 获取绘制区域尺寸（缩放后的实际尺寸）
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(
                (int) (game.MAP_COLS * game.TILE_SIZE * game.canvasScale),
                (int) (game.MAP_ROWS * game.TILE_SIZE * game.canvasScale)
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 应用平移和缩放
        g2.translate(game.viewOffsetX, game.viewOffsetY);
        g2.scale(game.canvasScale, game.canvasScale);

        // 用于记录每个区域的范围
        Map<BuildingZone, Rectangle> zoneBounds = new HashMap<>();

        for (int r = 0; r < game.MAP_ROWS; r++) {
            for (int c = 0; c < game.MAP_COLS; c++) {
                String tag = game.buildingLayout[r][c];
                if (tag.equals("NONE")) continue;

                BuildingZone zone = game.buildingZoneMap.get(tag);
                if (zone != null) {
                    int x = c * game.TILE_SIZE;
                    int y = r * game.TILE_SIZE;
                    Rectangle rect = zoneBounds.get(zone);
                    if (rect == null) {
                        rect = new Rectangle(x, y, game.TILE_SIZE, game.TILE_SIZE);
                        zoneBounds.put(zone, rect);
                    } else {
                        rect.setBounds(
                                Math.min(rect.x, x),
                                Math.min(rect.y, y),
                                Math.max(rect.x + rect.width, x + game.TILE_SIZE) - Math.min(rect.x, x),
                                Math.max(rect.y + rect.height, y + game.TILE_SIZE) - Math.min(rect.y, y)
                        );
                    }
                }
            }
        }

        // 绘制区域背景
        for (Map.Entry<BuildingZone, Rectangle> entry : zoneBounds.entrySet()) {
            BuildingZone zone = entry.getKey();
            Rectangle rect = entry.getValue();

            // 稍微扩展区域边界
            rect.grow(5, 5);

            // 绘制半透明背景
            g2.setColor(zone.bgColor);
            g2.fillRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

            // 绘制区域边框
            g2.setColor(zone.borderColor);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(rect.x, rect.y, rect.width, rect.height, 15, 15);

            // 绘制区域标题
            g2.setFont(new Font("微软雅黑", Font.BOLD, 14));
            g2.setColor(zone.borderColor);
            g2.drawString(zone.name, rect.x + 10, rect.y + 25);
        }

        // 重置画笔
        g2.setStroke(new BasicStroke(1f));

        // 绘制网格和建筑
        for (int r = 0; r < game.MAP_ROWS; r++) {
            for (int c = 0; c < game.MAP_COLS; c++) {
                int x = c * game.TILE_SIZE, y = r * game.TILE_SIZE;
                String tag = game.buildingLayout[r][c];
                if (tag.equals("NONE")) continue;

                // 获取建筑所属区域
                BuildingZone zone = game.buildingZoneMap.get(tag);
                Color baseColor = zone != null ? zone.borderColor : new Color(45, 48, 58);

                // 建筑背景使用区域颜色的半透明版本
                g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 80));
                g2.fillRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                g2.setColor(baseColor);
                g2.drawRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                g2.setFont(new Font("Consolas", Font.BOLD, 8));
                g2.setColor(Color.WHITE);

                // 特殊建筑保持原有颜色逻辑（PC、服务器等）
                if (tag.equals("PC_FACTORY")) {
                    g2.setColor(new Color(0, 130, 200));
                    g2.fillRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                    g2.setColor(Color.WHITE);
                    g2.drawString("[PC] 源PC", x + 4, y + 24);
                } else if (tag.equals("RX_ST")) {
                    g2.setColor(new Color(190, 30, 50));
                    g2.fillRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                    g2.setColor(Color.YELLOW);
                    g2.drawString("[SRV] 服务器", x + 2, y + 24);
                    for (int b = 0; b < game.serverBufferCount; b++) {
                        g2.setColor(Color.RED);
                        g2.fillRect(x + 4 + (b * 6), y + 4, 5, 6);
                    }
                }
                // 矿机特殊处理
                else if (tag.startsWith("MINER_H")) {
                    g2.setColor(new Color(0, 200, 200, 100));
                    g2.fillRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                    g2.setColor(Color.CYAN);
                    g2.drawString("[H] 矿机", x + 4, y + 24);
                } else if (tag.startsWith("MINER_S")) {
                    g2.setColor(new Color(0, 255, 0, 100));
                    g2.fillRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                    g2.setColor(Color.GREEN);
                    g2.drawString("[S] 矿机", x + 4, y + 24);
                } else {
                    // 普通建筑，文字颜色使用区域边框颜色
                    g2.setColor(baseColor);
                    String displayText = tag.length() > 8 ? tag.substring(0, 6) : tag;
                    // 简化显示，去掉前缀
                    if (tag.startsWith("TX_") || tag.startsWith("RX_") || tag.startsWith("T_") || tag.startsWith("R_")) {
                        displayText = tag;
                    }
                    g2.drawString(displayText, x + 4, y + 24);
                }
            }
        }

        // 公网区域标识
        g2.setColor(new Color(255, 100, 0, 15));
        g2.fillRect(21 * game.TILE_SIZE, 0, 14 * game.TILE_SIZE, game.MAP_ROWS * game.TILE_SIZE);

        int wanCarCount = 0;
        for (DataCart c : game.dataCarts) {
            int col = (int) (c.x / game.TILE_SIZE);
            if (!c.isReturnTrip && c.stage >= 25 && c.stage <= 31 && col >= 21 && col <= 34) wanCarCount++;
        }
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("微软雅黑", Font.BOLD, 14));
        g2.drawString("[CAR] 公网车辆: " + wanCarCount + "/" + game.WAN_BOTTLE_NECK_MAX, 22 * game.TILE_SIZE, 30);

        // 绘制建筑
        for (int r = 0; r < game.MAP_ROWS; r++) {
            for (int c = 0; c < game.MAP_COLS; c++) {
                int x = c * game.TILE_SIZE, y = r * game.TILE_SIZE;
                String tag = game.buildingLayout[r][c];
                if (tag.equals("NONE")) continue;

                // 建筑背景
                g2.setColor(new Color(45, 48, 58));
                g2.fillRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                g2.setColor(Color.GRAY);
                g2.drawRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                g2.setFont(new Font("Consolas", Font.BOLD, 8));
                g2.setColor(Color.WHITE);

                // PC 工厂
                if (tag.equals("PC_FACTORY")) {
                    g2.setColor(new Color(0, 130, 200));
                    g2.fillRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                    g2.setColor(Color.WHITE);
                    g2.drawString("[PC] 源PC", x + 4, y + 24);
                }
                // 服务器
                else if (tag.equals("RX_ST")) {
                    g2.setColor(new Color(190, 30, 50));
                    g2.fillRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                    g2.setColor(Color.YELLOW);
                    g2.drawString("[SRV] 服务器", x + 2, y + 24);
                    for (int b = 0; b < game.serverBufferCount; b++) {
                        g2.setColor(Color.RED);
                        g2.fillRect(x + 4 + (b * 6), y + 4, 5, 6);
                    }
                }
                // DHCP 相关
                else if (tag.startsWith("DHCP_")) {
                    g2.setColor(new Color(100, 100, 255));
                    g2.fillRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                    g2.setColor(Color.WHITE);
                    g2.drawString(tag, x + 3, y + 24);
                }
                // DNS 相关
                else if (tag.equals("DNS_CLIENT") || tag.equals("DNS_LOCAL") || tag.equals("DNS_ROOT") || tag.equals("DNS_AUTH")) {
                    g2.setColor(new Color(0, 200, 200));
                    g2.fillRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                    g2.setColor(Color.BLACK);
                    g2.drawString(tag, x + 3, y + 24);
                }
                // 以太网相关
                else if (tag.startsWith("ETH_")) {
                    g2.setColor(new Color(0, 160, 255));
                    g2.fillRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                    g2.setColor(Color.BLACK);
                    g2.drawString(tag, x + 3, y + 24);
                }
                // 路由器
                else if (tag.startsWith("ROUTER")) {
                    g2.setColor(new Color(200, 100, 0));
                    g2.fillRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                    g2.setColor(Color.BLACK);
                    g2.drawString(tag, x + 3, y + 24);
                }
                // 传输层/网络层/链路层组件
                else if (tag.startsWith("T_") || tag.startsWith("TX_") || tag.startsWith("R_") || tag.startsWith("RX_")) {
                    if (tag.contains("NAT")) g2.setColor(new Color(255, 165, 0));
                    else if (tag.contains("ARP")) g2.setColor(new Color(0, 255, 255));
                    else if (tag.startsWith("R_")) g2.setColor(Color.CYAN);
                    else if (tag.startsWith("RX_")) g2.setColor(Color.MAGENTA);
                    else g2.setColor(Color.ORANGE);
                    g2.drawRect(x + 2, y + 2, game.TILE_SIZE - 4, game.TILE_SIZE - 4);
                    g2.drawString(tag, x + 3, y + 24);
                }
                // 矿机
                else if (tag.startsWith("MINER_H")) {
                    g2.setColor(Color.CYAN);
                    g2.drawString("[H] 矿机", x + 4, y + 24);
                } else if (tag.startsWith("MINER_S")) {
                    g2.setColor(Color.GREEN);
                    g2.drawString("[S] 矿机", x + 4, y + 24);
                }
                // 默认其他建筑
                else {
                    g2.setColor(new Color(100, 100, 100));
                    g2.drawString(tag.length() > 8 ? tag.substring(0, 6) : tag, x + 4, y + 24);
                }
            }
        }

        // 绘制采矿车
        for (OreCart c : game.oreCarts) {
            g2.setColor(c.oreType.equals("HELLO") ? Color.CYAN : Color.GREEN);
            g2.fillOval((int) c.x - 5, (int) c.y - 5, 10, 10);
        }

        // 绘制数据包
        for (DataCart cart : game.dataCarts) {
            int cx = (int) cart.x, cy = (int) cart.y;

            if (cart.waitInQueueTimer > 0) g2.setColor(Color.RED);
            else if (cart.cartType.equals("ICMP_TIMEEXCEEDED")) g2.setColor(new Color(200, 50, 50));
            else if (cart.cartType.equals("ICMP_ECHO_REQ")) g2.setColor(new Color(100, 100, 255));
            else if (cart.cartType.equals("ICMP_ECHO_REPLY")) g2.setColor(new Color(50, 200, 50));
            else if (cart.cartType.startsWith("DHCP")) g2.setColor(Color.MAGENTA);
            else if (cart.cartType.equals("ZWP")) g2.setColor(Color.YELLOW);
            else if (cart.cartType.startsWith("SYN")) g2.setColor(Color.CYAN);
            else if (cart.cartType.startsWith("FIN")) g2.setColor(Color.PINK);
            else if (cart.cartType.contains("ACK")) g2.setColor(Color.GREEN);
            else if (cart.cartType.startsWith("DNS")) g2.setColor(new Color(0, 200, 200));
            else if (cart.isRetransmission) g2.setColor(Color.ORANGE);
            else if (cart.cartType.startsWith("TLS_")) g2.setColor(new Color(255, 165, 0));
            else if (cart.cartType.equals("HTTP_GET") || cart.cartType.equals("HTTP_200_OK"))
                g2.setColor(new Color(150, 0, 200));
            else if (cart.cartType.equals("UDP_DATA")) g2.setColor(new Color(50, 150, 255));
            else g2.setColor(Color.LIGHT_GRAY);
            g2.fillOval(cx - 7, cy - 7, 14, 14);

            // 绘制协议层标识
            int bx = cx - 30;
            if (cart.hasApp) {
                g2.setColor(new Color(100, 255, 100));
                g2.fillRect(bx, cy - 5, 6, 10);
                bx += 6;
            }
            if (cart.hasTcp) {
                g2.setColor(Color.ORANGE);
                g2.fillRect(bx, cy - 5, 7, 10);
                bx += 7;
            }
            if (cart.hasIp) {
                g2.setColor(Color.YELLOW);
                g2.fillRect(bx, cy - 5, 6, 10);
                bx += 6;
            }
            if (cart.hasEther) {
                g2.setColor(new Color(0, 160, 255));
                g2.fillRect(bx, cy - 5, 8, 10);
                bx += 8;
            }
            if (cart.hasLlc) {
                g2.setColor(Color.GREEN);
                g2.fillRect(bx, cy - 5, 6, 10);
                bx += 6;
            }
            if (cart.hasFcs) {
                g2.setColor(Color.GREEN.darker());
                g2.fillRect(bx, cy - 5, 6, 10);
            }

            // 标签文字
            g2.setFont(new Font("微软雅黑", Font.BOLD, 10));
            String direction = cart.isReturnTrip ? "[R] 回传" : (cart.waitInQueueTimer > 0 ? "[W] 排队" : "[S] 发送");
            String nameTag = cart.cartType;
            if (cart.isRetransmission && cart.cartType.equals("DATA")) nameTag = "重传-" + cart.sequenceNumber;
            String label = String.format("%s %s TTL:%d", nameTag, direction, cart.ttl);
            if (cart.domain != null && !cart.domain.isEmpty()) label += " [" + cart.domain + "]";
            if (cart.resolvedIp != null) label += " -> " + cart.resolvedIp;

            g2.setColor(new Color(0, 0, 0, 180));
            g2.fillRect(cx - 55, cy - 25, g2.getFontMetrics().stringWidth(label) + 8, 16);
            g2.setColor(cart.waitInQueueTimer > 0 ? Color.RED : Color.YELLOW);
            g2.drawString(label, cx - 53, cy - 14);
        }
    }
}
