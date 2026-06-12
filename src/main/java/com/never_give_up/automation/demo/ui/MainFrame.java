package com.never_give_up.automation.demo.ui;

import com.never_give_up.automation.demo.adapter.FactoryManager;
import com.never_give_up.automation.demo.config.NetworkConfig;
import com.never_give_up.automation.demo.engine.GameEngine;
import com.never_give_up.automation.demo.engine.MapManager;
import com.never_give_up.automation.demo.model.*;
import com.never_give_up.automation.demo.winModel.DataPacket;
import com.never_give_up.automation.demo.winModel.OreCart;
import com.never_give_up.automation.demo.winModel.TcpState;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainFrame extends JFrame implements GameEngine.GameCallback {
    private GameEngine engine;
    private MapManager mapManager;
    private FactoryManager factoryManager;
    private GameCanvas canvas;

    // 统一使用 DataPacket
    private List<DataPacket> dataPackets = new CopyOnWriteArrayList<>();
    private List<DataPacket> pendingDataPackets = new CopyOnWriteArrayList<>();
    private List<OreCart> oreCarts = new CopyOnWriteArrayList<>();

    private JLabel lblDashboard;
    private JTextArea txtConsole;
    private JProgressBar prgNetwork;
    private JTable tcpConnTable;
    private DefaultTableModel tableModel;
    private JTextArea txtArpDisplay;
    private JTextArea txtNatDisplay;
    private JTextArea txtDnsDisplay;

    private String selectedBuilding = "NONE";
    private boolean isDragging = false;
    private int lastDragX = 0, lastDragY = 0;
    private double canvasScale = 1.0;
    private int viewOffsetX = 0, viewOffsetY = 0;
    private String targetDomain = "www.demo.com";

    public MainFrame() {
        setTitle("🌐 全协议栈网络可视化模拟器");
        setSize(2000, 1050);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initializeComponents();
        layoutComponents();
        setupTimers();

        setVisible(true);
    }

    private void initializeComponents() {
        factoryManager = new FactoryManager();
        engine = new GameEngine(factoryManager);
        engine.setCallback(this);
        mapManager = new MapManager();

        // 初始化缓存
        initArpCache();
        initDnsCache();

        canvas = new GameCanvas(engine, mapManager, dataPackets, oreCarts);

        lblDashboard = new JLabel("", JLabel.CENTER);
        lblDashboard.setFont(new Font("微软雅黑", Font.BOLD, 14));

        txtConsole = new JTextArea(12, 80);
        txtConsole.setEditable(false);
        txtConsole.setBackground(new Color(10, 12, 16));
        txtConsole.setForeground(new Color(50, 255, 120));
        txtConsole.setFont(new Font("微软雅黑", Font.PLAIN, 12));

        prgNetwork = new JProgressBar(0, 100);
        prgNetwork.setStringPainted(true);

        tableModel = new DefaultTableModel(new String[]{"Proto", "Local Address", "Remote Address", "State"}, 0);
        tcpConnTable = new JTable(tableModel);
        tcpConnTable.setFont(new Font("Consolas", Font.PLAIN, 12));

        txtArpDisplay = new JTextArea(6, 30);
        txtArpDisplay.setEditable(false);
        txtArpDisplay.setBackground(new Color(20, 25, 35));
        txtArpDisplay.setForeground(new Color(100, 200, 255));

        txtNatDisplay = new JTextArea(6, 30);
        txtNatDisplay.setEditable(false);
        txtNatDisplay.setBackground(new Color(20, 25, 35));
        txtNatDisplay.setForeground(new Color(255, 200, 100));

        txtDnsDisplay = new JTextArea(6, 30);
        txtDnsDisplay.setEditable(false);
        txtDnsDisplay.setBackground(new Color(20, 25, 35));
        txtDnsDisplay.setForeground(new Color(150, 255, 150));
        mapManager.printDhcpCoords();
    }

    private void initArpCache() {
        factoryManager.getArpCache().addEntry("192.168.1.1", "00:1A:2B:3C:4D:5E");
        factoryManager.getArpCache().addEntry("192.168.1.100", "00:1A:2B:3C:4D:5F");
        factoryManager.getArpCache().addEntry("10.0.0.1", "00:1A:2B:3C:4D:60");
    }

    private void initDnsCache() {
        factoryManager.getDnsCache().addEntry("www.demo.com", "10.0.0.1", 3600000);
        factoryManager.getDnsCache().addEntry("google.com", "8.8.8.8", 3600000);
    }

    private void layoutComponents() {
        add(createTopPanel(), BorderLayout.NORTH);
        add(createLeftPanel(), BorderLayout.WEST);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createRightPanel(), BorderLayout.EAST);
        add(createBottomPanel(), BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("📊 协议栈状态仪表盘"));
        topPanel.add(lblDashboard, BorderLayout.CENTER);
        return topPanel;
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(420, 0));

        JPanel shopPanel = createShopPanel();
        JScrollPane shopScroll = new JScrollPane(shopPanel);
        shopScroll.setPreferredSize(new Dimension(420, 350));
        shopScroll.setBorder(BorderFactory.createTitledBorder("🏭 网络设备工厂"));

        JScrollPane arpScroll = new JScrollPane(txtArpDisplay);
        arpScroll.setBorder(BorderFactory.createTitledBorder("📋 ARP 缓存表"));

        JScrollPane natScroll = new JScrollPane(txtNatDisplay);
        natScroll.setBorder(BorderFactory.createTitledBorder("🌍 NAT 转换表"));

        JScrollPane dnsScroll = new JScrollPane(txtDnsDisplay);
        dnsScroll.setBorder(BorderFactory.createTitledBorder("📚 DNS 缓存表"));

        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, shopScroll, arpScroll);
        leftSplit.setResizeWeight(0.5);
        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, natScroll, dnsScroll);
        rightSplit.setResizeWeight(0.5);
        JSplitPane mainLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftSplit, rightSplit);
        mainLeft.setResizeWeight(0.6);
        leftPanel.add(mainLeft, BorderLayout.CENTER);

        return leftPanel;
    }

    private JPanel createShopPanel() {
        JPanel shopPanel = new JPanel();
        shopPanel.setLayout(new BoxLayout(shopPanel, BoxLayout.Y_AXIS));

        JButton btnUpgradeServer = new JButton("🚀 超频接收端 CPU (加快解包) $" + NetworkConfig.PRICE_UPGRADE_SERVER);
        btnUpgradeServer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUpgradeServer.addActionListener(e -> {
            if (engine.getFunds() >= NetworkConfig.PRICE_UPGRADE_SERVER) {
                engine.deductFunds(NetworkConfig.PRICE_UPGRADE_SERVER);
                logToConsole("【⚡ 硬件升级】: 服务器超频成功！");
            }
        });
        shopPanel.add(Box.createVerticalStrut(10));
        shopPanel.add(btnUpgradeServer);
        shopPanel.add(Box.createVerticalStrut(15));

        String[][] categories = {
                {"【1. 内网采矿与原始数据】", "MINER_H", "🔷 Hello 采矿机", "MINER_S", "🟩 Say 采矿机"},
                {"【2. 应用层】", "TX_APP", "📦 应用数据载荷"},
                {"【3. DNS 解析】", "DNS_CLIENT", "🔍 DNS 客户端", "DNS_LOCAL", "📡 本地 DNS",
                        "DNS_ROOT", "🌐 根 DNS", "DNS_AUTH", "🏢 权威 DNS"},
                {"【4. DHCP 客户端】", "DHCP_DISC", "🔎 Discover", "DHCP_OFFER", "📥 Offer",
                        "DHCP_REQ", "📤 Request", "DHCP_ACK", "✅ ACK"},
                {"【5. 传输层 - TCP 封装】", "T_SP", "🔩 源端口", "T_DP", "🎯 目的端口",
                        "T_SEQ", "🔢 序列号", "T_ACK", "📜 确认号", "T_CTL", "🚩 控制位",
                        "T_WIN", "🌊 滑动窗口", "T_CHK", "🔥 校验和", "T_CORE", "🟧 TCP 段总装"},
                {"【6. 网络层 - IP 封装】", "TX_IPH", "📦 IP 首部", "TX_IP_FRAG", "✂️ IP 分片器"},
                {"【7. 链路层 - Ethernet II】", "TX_ARP", "🔍 ARP 解析", "ETH_DST", "🟦 目的 MAC",
                        "ETH_SRC", "🟦 源 MAC", "ETH_TYPE", "🟦 EtherType", "TX_LLC", "🟩 LLC", "TX_FCS", "🟩 FCS"},
                {"【8. 边界网关】", "R_LAN", "🎛️ LAN 拆包", "R_TAB", "🔀 路由查表",
                        "R_NAT", "🌍 NAT 转换", "R_WAN", "🛠️ WAN 封装"},
                {"【9. 公网路由器】", "ROUTER1", "📡 Router1", "ROUTER2", "📡 Router2", "ROUTER3", "📡 Router3"},
                {"【10. 接收端解封装】", "RX_LLC", "🔓 链路层解封", "RX_IP", "💛 网络层解封",
                        "RX_TCP", "🧡 传输层解封", "RX_APP", "💚 应用层交付"}
        };

        ButtonGroup group = new ButtonGroup();

        for (String[] cat : categories) {
            JLabel title = new JLabel(cat[0]);
            title.setForeground(new Color(120, 30, 180));
            title.setFont(new Font("微软雅黑", Font.BOLD, 12));
            shopPanel.add(title);

            for (int i = 1; i < cat.length; i += 2) {
                final String tag = cat[i];
                String name = cat[i + 1];
                JRadioButton rad = new JRadioButton(name);
                group.add(rad);
                rad.addActionListener(e -> selectedBuilding = tag);
                shopPanel.add(rad);
            }
        }

        return shopPanel;
    }

    private JPanel createCenterPanel() {
        JScrollPane mapScrollPane = new JScrollPane(canvas);
        mapScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mapScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mapScrollPane.getHorizontalScrollBar().setUnitIncrement(40);
        mapScrollPane.getVerticalScrollBar().setUnitIncrement(40);

        JPanel mapPanelWithHint = new JPanel(new BorderLayout());
        mapPanelWithHint.add(mapScrollPane, BorderLayout.CENTER);
        JLabel hintLabel = new JLabel("  💡 提示：按住 Ctrl 键 + 滚动鼠标滚轮可缩放地图", JLabel.LEFT);
        hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        hintLabel.setForeground(Color.GRAY);
        mapPanelWithHint.add(hintLabel, BorderLayout.SOUTH);

        setupCanvasMouseListeners(mapScrollPane);

        return mapPanelWithHint;
    }

    private void setupCanvasMouseListeners(JScrollPane scrollPane) {
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    isDragging = true;
                    lastDragX = e.getX();
                    lastDragY = e.getY();
                    canvas.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (!isDragging) {
                        handleBuildingPlacement(e, scrollPane);
                    }
                    isDragging = false;
                    canvas.setCursor(Cursor.getDefaultCursor());
                }

                if (SwingUtilities.isRightMouseButton(e)) {
                    handleBuildingRemoval(e, scrollPane);
                }
            }
        });

        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    int deltaX = e.getX() - lastDragX;
                    int deltaY = e.getY() - lastDragY;
                    viewOffsetX += deltaX;
                    viewOffsetY += deltaY;
                    canvas.setViewOffset(viewOffsetX, viewOffsetY);
                    lastDragX = e.getX();
                    lastDragY = e.getY();
                }
            }
        });

        canvas.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                double oldScale = canvasScale;
                if (e.getWheelRotation() < 0) {
                    canvasScale = Math.min(NetworkConfig.MAX_SCALE, canvasScale + NetworkConfig.SCALE_STEP);
                } else {
                    canvasScale = Math.max(NetworkConfig.MIN_SCALE, canvasScale - NetworkConfig.SCALE_STEP);
                }

                if (oldScale != canvasScale) {
                    canvas.setScale(canvasScale);
                    logToConsole(String.format("【🔍 画布缩放】: %.1f倍", canvasScale));
                }
            }
        });
    }

    private void handleBuildingPlacement(MouseEvent e, JScrollPane scrollPane) {
        int scrollX = scrollPane.getHorizontalScrollBar().getValue();
        int scrollY = scrollPane.getVerticalScrollBar().getValue();
        int logicalX = canvas.getLogicalX(e.getX(), scrollX);
        int logicalY = canvas.getLogicalY(e.getY(), scrollY);

        if (logicalX < 0 || logicalY < 0) return;

        int col = logicalX / NetworkConfig.TILE_SIZE;
        int row = logicalY / NetworkConfig.TILE_SIZE;

        if (row >= 0 && row < NetworkConfig.MAP_ROWS && col >= 0 && col < NetworkConfig.MAP_COLS) {
            if (!mapManager.isValidBuildingPosition(row, col)) return;

            if (selectedBuilding.startsWith("MINER_")) {
                int reqType = selectedBuilding.equals("MINER_H") ? 1 : 2;
                if (mapManager.getMapLayout()[row][col] == reqType && engine.deductFunds(NetworkConfig.PRICE_MINER)) {
                    mapManager.placeBuilding(row, col, selectedBuilding);
                    logToConsole(String.format("【🏗️ 建造】: 放置 %s 矿机", selectedBuilding));
                }
            } else if (!selectedBuilding.equals("NONE")) {
                if (engine.deductFunds(NetworkConfig.PRICE_MACHINE)) {
                    mapManager.placeBuilding(row, col, selectedBuilding);
                    logToConsole(String.format("【🏗️ 建造】: 放置 %s", selectedBuilding));
                }
            }
        }
    }

    private void handleBuildingRemoval(MouseEvent e, JScrollPane scrollPane) {
        int scrollX = scrollPane.getHorizontalScrollBar().getValue();
        int scrollY = scrollPane.getVerticalScrollBar().getValue();
        int logicalX = canvas.getLogicalX(e.getX(), scrollX);
        int logicalY = canvas.getLogicalY(e.getY(), scrollY);

        if (logicalX < 0 || logicalY < 0) return;

        int col = logicalX / NetworkConfig.TILE_SIZE;
        int row = logicalY / NetworkConfig.TILE_SIZE;

        if (row >= 0 && row < NetworkConfig.MAP_ROWS && col >= 0 && col < NetworkConfig.MAP_COLS) {
            String existing = mapManager.getBuildingLayout()[row][col];
            if (!existing.equals("NONE") && !existing.equals("PC_FACTORY") &&
                    !existing.equals("RX_ST") && !existing.equals("DHCP_SERVER") &&
                    !mapManager.isGatewayBoundary(col)) {
                int refund = existing.startsWith("MINER") ? NetworkConfig.PRICE_MINER / 2 : NetworkConfig.PRICE_MACHINE / 2;
                engine.addFunds(refund);
                mapManager.removeBuilding(row, col);
                logToConsole(String.format("【🗑️ 拆除】: 移除 %s，返还 %d 资金", existing, refund));
            }
        }
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(280, 0));
        rightPanel.setBorder(BorderFactory.createTitledBorder("🌐 TCP 连接表 (netstat)"));

        JScrollPane tableScroll = new JScrollPane(tcpConnTable);
        rightPanel.add(tableScroll, BorderLayout.CENTER);

        return rightPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("📟 协议分析控制台"));

        bottomPanel.add(prgNetwork, BorderLayout.NORTH);

        JScrollPane consoleScroll = new JScrollPane(txtConsole);
        consoleScroll.getViewport().setBackground(new Color(10, 12, 16));
        bottomPanel.add(consoleScroll, BorderLayout.CENTER);

        JPanel btnPanel = createButtonPanel();
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);

        return bottomPanel;
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout());

        JButton resetButton = new JButton("🔄 重置会话");
        resetButton.addActionListener(e -> engine.resetSession());

        JButton clearArpButton = new JButton("🗑️ 清空 ARP 缓存");
        clearArpButton.addActionListener(e -> {
            engine.getArpCache().clear();
            updateArpDisplay();
        });

        JButton clearDnsButton = new JButton("🗑️ 清空 DNS 缓存");
        clearDnsButton.addActionListener(e -> {
            engine.getDnsCache().clear();
            updateDnsDisplay();
        });

        JButton clearConsoleButton = new JButton("🗑️ 清空控制台");
        clearConsoleButton.addActionListener(e -> txtConsole.setText(""));

        JButton copyConsoleButton = new JButton("📋 复制日志");
        copyConsoleButton.addActionListener(e -> copyConsoleLog());

        JRadioButton rbTcp = new JRadioButton("TCP", true);
        JRadioButton rbUdp = new JRadioButton("UDP", false);
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(rbTcp);
        modeGroup.add(rbUdp);

        rbTcp.addActionListener(e -> {
            engine.setUseUdp(false);
            engine.resetSession();
            logToConsole("【切换】: 使用 TCP 模式");
        });

        rbUdp.addActionListener(e -> {
            engine.setUseUdp(true);
            engine.resetSession();
            logToConsole("【切换】: 使用 UDP 模式");
        });

        JCheckBox cbHttp = new JCheckBox("📡 HTTP 演示", false);
        cbHttp.addActionListener(e -> {
            boolean enabled = cbHttp.isSelected();
            engine.setHttpDemoEnabled(enabled);
            engine.resetSession();
            logToConsole(enabled ? "【启用 HTTP 演示】" : "【关闭 HTTP 演示】");
        });

        JCheckBox cbTls = new JCheckBox("🔒 启用 TLS", false);
        cbTls.addActionListener(e -> {
            boolean enabled = cbTls.isSelected();
            engine.setTlsEnabled(enabled);
            engine.resetSession();
            logToConsole(enabled ? "【启用 TLS】" : "【关闭 TLS】");
        });
        cbTls.setEnabled(false);

        btnPanel.add(resetButton);
        btnPanel.add(clearArpButton);
        btnPanel.add(clearDnsButton);
        btnPanel.add(clearConsoleButton);
        btnPanel.add(copyConsoleButton);
        btnPanel.add(rbTcp);
        btnPanel.add(rbUdp);
        btnPanel.add(cbHttp);
        btnPanel.add(cbTls);

        return btnPanel;
    }

    private void setupTimers() {

        new Timer(30, e -> gameTick()).start();
        new Timer(1000, e -> {
            updateArpDisplay();
            updateDnsDisplay();
            updateNatDisplay();
            updateTcpConnTable();
            updateDashboard();

            // 添加这行 - 启动 DHCP
            if (!engine.isPcIpAssigned() && !engine.isDhcpInProgress()) {
                engine.startDhcpIfNeeded(getPcFactory());
            }

            if (!pendingDataPackets.isEmpty()) {
                System.out.println("Moving " + pendingDataPackets.size() + " packets from pending to active");
                dataPackets.addAll(pendingDataPackets);
                pendingDataPackets.clear();
            }
        }).start();
    }

    private void gameTick() {
        long now = System.currentTimeMillis();
        int tickCount = 0;
        if (tickCount++ % 30 == 0) {  // 每30帧打印一次
            System.out.println("Game tick - DataPackets size: " + dataPackets.size() +
                    ", Pending size: " + pendingDataPackets.size());
        }
        // 服务器处理逻辑
        if (engine.getServerBufferCount() > 0 &&
                now - engine.getLastServerConsumeTime() >= engine.getServerDecodeDelay()) {
            engine.decrementServerBuffer();
            engine.incrementServerReceived();
            canvas.updateServerBuffer(engine.getServerBufferCount());
            logToConsole(String.format("【📥 服务器处理】: 已处理 %d/%d 个数据包",
                    engine.getServerReceivedCount(), NetworkConfig.TOTAL_DATA_TO_TRANSMIT));
        }

        // 检查重传超时 - 不要传递参数
        engine.checkRetransmissionTimeout();

        // 更新数据包位置
        updateDataPackets();

        // 更新进度条
        prgNetwork.setValue((int) (((double) engine.getServerReceivedCount() / NetworkConfig.TOTAL_DATA_TO_TRANSMIT) * 100));

        // 更新UI
        updateDashboard();
        canvas.updateServerBuffer(engine.getServerBufferCount());
        canvas.repaint();
    }

    private void updateDataPackets() {
        for (DataPacket packet : dataPackets) {
            packet.update();
        }
        dataPackets.removeIf(packet -> packet.isDropped() || packet.isArrived());
    }

    private void updateDashboard() {
        int effectiveWin = engine.getEffectiveWindow();
        String ipStatus = engine.isPcIpAssigned() ? engine.getPcIpAddress() : "未分配";
        String modeStr = engine.isUseUdp() ? "UDP" : "TCP";

        lblDashboard.setText(String.format(
                "💰 资金:%d | 🏷️ %s:%s | 🌐 IP:%s | 🚩 cwnd:%d | 🎯 ssthresh:%d | 📥 rwnd:%d | 🎛️ 有效窗口:%d | " +
                        "📦 仓储:%d/%d | ✅ 达成:%d/%d | 🔍 ARP:%d | 📚 DNS:%d | 🌐 域名:%s → %s",
                engine.getFunds(), modeStr, engine.getCurrentTcpState(), ipStatus,
                engine.getCwnd(), engine.getSsthresh(), engine.getRwnd(), effectiveWin,
                engine.getServerBufferCount(), NetworkConfig.SERVER_BUFFER_MAX,
                engine.getServerReceivedCount(), NetworkConfig.TOTAL_DATA_TO_TRANSMIT,
                engine.getArpCache().size(), engine.getDnsCache().size(),
                targetDomain, engine.getResolvedServerIp() == null ? "未解析" : engine.getResolvedServerIp()
        ));
    }

    private void updateArpDisplay() {
        StringBuilder sb = new StringBuilder();
        engine.getArpCache().forEach((ip, entry) -> {
            sb.append(String.format("%s → %s\n", entry.getIpAddress(), entry.getMacAddress()));
        });
        txtArpDisplay.setText(sb.toString());
    }

    private void updateDnsDisplay() {
        StringBuilder sb = new StringBuilder();
        engine.getDnsCache().forEach((domain, record) -> {
            long remainingSec = Math.max(0, record.getTtl() / 1000);
            sb.append(String.format("%s → %s (TTL:%ds)\n", domain, record.getIpAddress(), remainingSec));
        });
        txtDnsDisplay.setText(sb.toString());
    }

    private void updateNatDisplay() {
        StringBuilder sb = new StringBuilder();
        engine.getNatTable().forEach((key, entry) -> {
            sb.append(String.format("%s:%d → %s:%d\n",
                    entry.getInsideIp(), entry.getInsidePort(),
                    entry.getPublicIp(), entry.getPublicPort()));
        });
        txtNatDisplay.setText(sb.toString());
    }

    private void updateTcpConnTable() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            if (engine.isPcIpAssigned() && engine.getResolvedServerIp() != null &&
                    engine.getCurrentTcpState() != TcpState.CLOSED) {
                String localAddr = engine.getPcIpAddress() + ":80";
                String remoteAddr = engine.getResolvedServerIp() + ":443";
                tableModel.addRow(new Object[]{"TCP", localAddr, remoteAddr, engine.getCurrentTcpState().toString()});
            }
        });
    }

    private void copyConsoleLog() {
        String text = txtConsole.getText();
        if (text != null && !text.trim().isEmpty()) {
            java.awt.datatransfer.StringSelection selection =
                    new java.awt.datatransfer.StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
            JOptionPane.showMessageDialog(this, "✅ 已复制日志到剪贴板", "复制成功", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    public void onLog(String message) {
        logToConsole(message);
    }

    @Override
    public void onUpdate() {
        updateDashboard();
        canvas.repaint();
    }

    @Override
    public void onPacketArrived(DataPacket packet) {
        // 处理到达的数据包
        logToConsole(String.format("【📦 数据包到达】: %s", packet.getCartType()));
    }

    @Override
    public void addPendingPacket(DataPacket packet) {
        System.out.println("Adding pending packet: " + packet.getCartType() +
                " at (" + packet.getX() + "," + packet.getY() + ")");
        pendingDataPackets.add(packet);
    }

    @Override
    public Point getPcFactory() {
        return mapManager.getPcFactory();
    }

    @Override
    public Point findBuildingCoords(String tag) {
        return mapManager.findBuildingCoords(tag);
    }

    private void logToConsole(String text) {
        SwingUtilities.invokeLater(() -> {
            txtConsole.append(text + "\n");
            txtConsole.setCaretPosition(txtConsole.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}