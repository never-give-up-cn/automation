package com.never_give_up.automation.demo;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataCartFactoryGame extends JFrame {

    enum TcpState {
        CLOSED, SYN_SENT, ESTABLISHED,
        FIN_WAIT_1, FIN_WAIT_2, CLOSE_WAIT, LAST_ACK, TIME_WAIT
    }

    private static class DnsEntry {
        String domain;
        String ipAddress;
        long ttl;
        long createTime;
        public DnsEntry(String domain, String ip, long ttl) {
            this.domain = domain;
            this.ipAddress = ip;
            this.ttl = ttl;
            this.createTime = System.currentTimeMillis();
        }
        public boolean isExpired() { return System.currentTimeMillis() - createTime > ttl; }
    }

    private static class ArpEntry {
        String ipAddress;
        String macAddress;
        long lastSeen;
        public ArpEntry(String ip, String mac) {
            this.ipAddress = ip;
            this.macAddress = mac;
            this.lastSeen = System.currentTimeMillis();
        }
    }

    private static class RetransmissionTask {
        int seqNum;
        long sendTime;
        int retryCount = 0;
        boolean isAcked = false;
        public RetransmissionTask(int seqNum, long sendTime) {
            this.seqNum = seqNum;
            this.sendTime = sendTime;
        }
    }

    private int funds = 3000;
    private int helloStock = 0;
    private int sayStock = 0;
    private int serverReceivedCount = 0;
    private final int totalDataToTransmit = 15;

    private TcpState currentTcpState = TcpState.CLOSED;
    private int rwnd = 3;
    private int cwnd = 1;
    private int ssthresh = 12;
    private int serverBufferCount = 0;
    private final int SERVER_BUFFER_MAX = 5;
    private long lastServerConsumeTime = 0;
    private int serverDecodeDelay = 600;
    private final int WAN_BOTTLE_NECK_MAX = 10;
    private long stateTimerWatchdog = 0;
    private final long RTO_TIMEOUT = 500000;
    private int nextSeqNum = 100;
    private long lastProbeTime = 0;
    private final long PROBE_INTERVAL = 3000;

    private List<RetransmissionTask> activeTimers = new CopyOnWriteArrayList<>();
    private Map<String, ArpEntry> arpCache = new ConcurrentHashMap<>();
    private Map<String, DnsEntry> dnsCache = new ConcurrentHashMap<>();
    private Set<Integer> ackedSeq = ConcurrentHashMap.newKeySet();

    private String targetDomain = "www.demo.com";
    private String resolvedServerIp = null;
    private boolean isDnsResolving = false;
    private boolean isDnsResolved = false;

    // DHCP 相关
    private boolean pcIpAssigned = false;
    private String pcIpAddress = null;
    private boolean dhcpInProgress = false;
    private int dhcpStep = 0; // 0=未开始,1=Discover已发,2=Offer收到,3=Request已发,4=ACK收到完成

    private String selectedBuilding = "NONE";
    private final int PRICE_MINER = 30;
    private final int PRICE_MACHINE = 20;
    private final int PRICE_UPGRADE_SERVER = 400;

    private final int TILE_SIZE = 40;
    private final int MAP_COLS = 55;
    private final int MAP_ROWS = 20;

    private int[][] mapLayout = new int[MAP_ROWS][MAP_COLS];
    private String[][] buildingLayout = new String[MAP_ROWS][MAP_COLS];
    private Point pcFactory;
    private List<OreCart> oreCarts = new CopyOnWriteArrayList<>();
    private List<DataCart> dataCarts = new CopyOnWriteArrayList<>();
    private List<DataCart> pendingDataCarts = new CopyOnWriteArrayList<>();

    private GameCanvas canvas;
    private JLabel lblDashboard;
    private JTextArea txtHexDisplay;
    private JProgressBar prgNetwork;
    private JPanel shopPanel;
    private JTextArea txtArpDisplay;
    private JTextArea txtNatDisplay;
    private JTextArea txtDnsDisplay;

    private int packetsAckedSinceLastIncrease = 0;
    private Set<Integer> sentSeq = ConcurrentHashMap.newKeySet();

    public DataCartFactoryGame() {
        setTitle("🌐 全协议栈网络可视化模拟器 (DHCP + TTL 递减 + 多路由器)");
        setSize(2000, 1050);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initMap();
        initArpCache();
        initDnsCache();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("📊 协议栈状态仪表盘"));
        lblDashboard = new JLabel("", JLabel.CENTER);
        lblDashboard.setFont(new Font("微软雅黑", Font.BOLD, 14));
        topPanel.add(lblDashboard, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(420, 0));

        shopPanel = new JPanel();
        shopPanel.setLayout(new BoxLayout(shopPanel, BoxLayout.Y_AXIS));
        JScrollPane shopScroll = new JScrollPane(shopPanel);
        shopScroll.setPreferredSize(new Dimension(420, 350));
        shopScroll.setBorder(BorderFactory.createTitledBorder("🏭 网络设备工厂"));

        txtArpDisplay = new JTextArea(6, 30);
        txtArpDisplay.setEditable(false);
        txtArpDisplay.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        txtArpDisplay.setBackground(new Color(20, 25, 35));
        txtArpDisplay.setForeground(new Color(100, 200, 255));
        JScrollPane arpScroll = new JScrollPane(txtArpDisplay);
        arpScroll.setBorder(BorderFactory.createTitledBorder("📋 ARP 缓存表"));

        txtNatDisplay = new JTextArea(6, 30);
        txtNatDisplay.setEditable(false);
        txtNatDisplay.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        txtNatDisplay.setBackground(new Color(20, 25, 35));
        txtNatDisplay.setForeground(new Color(255, 200, 100));
        JScrollPane natScroll = new JScrollPane(txtNatDisplay);
        natScroll.setBorder(BorderFactory.createTitledBorder("🌍 NAT 转换表"));

        txtDnsDisplay = new JTextArea(6, 30);
        txtDnsDisplay.setEditable(false);
        txtDnsDisplay.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        txtDnsDisplay.setBackground(new Color(20, 25, 35));
        txtDnsDisplay.setForeground(new Color(150, 255, 150));
        JScrollPane dnsScroll = new JScrollPane(txtDnsDisplay);
        dnsScroll.setBorder(BorderFactory.createTitledBorder("📚 DNS 缓存表"));

        JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, shopScroll, arpScroll);
        leftSplit.setResizeWeight(0.5);
        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, natScroll, dnsScroll);
        rightSplit.setResizeWeight(0.5);
        JSplitPane mainLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftSplit, rightSplit);
        mainLeft.setResizeWeight(0.6);
        leftPanel.add(mainLeft, BorderLayout.CENTER);
        add(leftPanel, BorderLayout.WEST);

        canvas = new GameCanvas();
        add(canvas, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("📟 协议分析控制台"));
        prgNetwork = new JProgressBar(0, 100);
        prgNetwork.setStringPainted(true);
        bottomPanel.add(prgNetwork, BorderLayout.NORTH);

        txtHexDisplay = new JTextArea(12, 80);
        txtHexDisplay.setEditable(false);
        txtHexDisplay.setBackground(new Color(10, 12, 16));
        txtHexDisplay.setForeground(new Color(50, 255, 120));
        txtHexDisplay.setFont(new Font("微软雅黑", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(txtHexDisplay);
        scrollPane.getViewport().setBackground(new Color(10, 12, 16));
        bottomPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton resetButton = new JButton("🔄 重置会话");
        resetButton.addActionListener(e -> resetTcpSession());
        JButton clearArpButton = new JButton("🗑️ 清空 ARP 缓存");
        clearArpButton.addActionListener(e -> { arpCache.clear(); updateArpDisplay(); });
        JButton clearDnsButton = new JButton("🗑️ 清空 DNS 缓存");
        clearDnsButton.addActionListener(e -> { dnsCache.clear(); updateDnsDisplay(); });
        JButton clearConsoleButton = new JButton("🗑️ 清空控制台");
        clearConsoleButton.addActionListener(e -> txtHexDisplay.setText(""));
        btnPanel.add(resetButton);
        btnPanel.add(clearArpButton);
        btnPanel.add(clearDnsButton);
        btnPanel.add(clearConsoleButton);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        buildShopUI();

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = e.getX() / TILE_SIZE;
                int row = e.getY() / TILE_SIZE;
                if (row >= MAP_ROWS || col >= MAP_COLS) return;

                if (SwingUtilities.isRightMouseButton(e)) {
                    String existing = buildingLayout[row][col];
                    if (existing.equals("NONE") || existing.equals("PC_FACTORY") || existing.equals("RX_ST")) return;
                    funds += existing.startsWith("MINER") ? PRICE_MINER / 2 : PRICE_MACHINE / 2;
                    buildingLayout[row][col] = "NONE";
                    canvas.repaint(); updateTopLabel(); return;
                }

                if (!buildingLayout[row][col].equals("NONE") || mapLayout[row][col] == 9) return;

                if (selectedBuilding.startsWith("MINER_")) {
                    int reqType = selectedBuilding.equals("MINER_H") ? 1 : 2;
                    if (mapLayout[row][col] == reqType && funds >= PRICE_MINER) {
                        funds -= PRICE_MINER;
                        buildingLayout[row][col] = selectedBuilding;
                    }
                } else {
                    if (mapLayout[row][col] == 0 && funds >= PRICE_MACHINE) {
                        funds -= PRICE_MACHINE;
                        buildingLayout[row][col] = selectedBuilding;
                    }
                }
                updateTopLabel(); canvas.repaint();
            }
        });
        stateTimerWatchdog = System.currentTimeMillis();  // 添加这一行
        updateTopLabel();
        new Timer(30, e -> gameTick()).start();
        new Timer(1000, e -> { updateArpDisplay(); updateDnsDisplay(); }).start();
    }

    private void appendToConsole(String text) {
        SwingUtilities.invokeLater(() -> {
            txtHexDisplay.append(text + "\n");
            txtHexDisplay.setCaretPosition(txtHexDisplay.getDocument().getLength());
        });
    }

    private void initArpCache() {
        arpCache.put("192.168.1.1", new ArpEntry("192.168.1.1", "00:1A:2B:3C:4D:5E"));
        arpCache.put("192.168.1.100", new ArpEntry("192.168.1.100", "00:1A:2B:3C:4D:5F"));
        arpCache.put("10.0.0.1", new ArpEntry("10.0.0.1", "00:1A:2B:3C:4D:60"));
    }

    private void initDnsCache() {
        dnsCache.put("www.demo.com", new DnsEntry("www.demo.com", "10.0.0.1", 3600000));
        dnsCache.put("google.com", new DnsEntry("google.com", "8.8.8.8", 3600000));
    }

    private void updateArpDisplay() {
        StringBuilder arpSb = new StringBuilder();
        for (ArpEntry entry : arpCache.values()) {
            arpSb.append(String.format("%s → %s\n", entry.ipAddress, entry.macAddress));
        }
        txtArpDisplay.setText(arpSb.toString());
    }

    private void updateDnsDisplay() {
        StringBuilder dnsSb = new StringBuilder();
        for (DnsEntry entry : dnsCache.values()) {
            dnsSb.append(String.format("%s → %s (TTL:%ds)\n", entry.domain, entry.ipAddress, entry.ttl/1000));
        }
        txtDnsDisplay.setText(dnsSb.toString());
    }

    private void buildShopUI() {
        ButtonGroup group = new ButtonGroup();

        JButton btnUpgradeServer = new JButton("🚀 超频接收端 CPU (加快解包) $400");
        btnUpgradeServer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUpgradeServer.addActionListener(e -> {
            if (funds >= PRICE_UPGRADE_SERVER && serverDecodeDelay > 200) {
                funds -= PRICE_UPGRADE_SERVER;
                serverDecodeDelay = Math.max(200, serverDecodeDelay - 300);
                appendToConsole("【⚡ 硬件升级】: 服务器超频成功！解包延迟降至 " + serverDecodeDelay + "ms");
                updateTopLabel();
            }
        });
        shopPanel.add(Box.createVerticalStrut(10));
        shopPanel.add(btnUpgradeServer);
        shopPanel.add(Box.createVerticalStrut(15));

        String[][] categories = {
                {"【1. 内网采矿与原始数据】", "MINER_H", "🔷 Hello 采矿机", "MINER_S", "🟩 Say 采矿机"},
                {"【2. 应用层】", "TX_APP", "📦 应用数据载荷"},
                {"【3. DNS 解析】", "DNS_CLIENT", "🔍 DNS 客户端", "DNS_LOCAL", "📡 本地 DNS", "DNS_ROOT", "🌐 根 DNS", "DNS_AUTH", "🏢 权威 DNS"},
                {"【4. DHCP 客户端】", "DHCP_DISC", "🔎 Discover", "DHCP_OFFER", "📥 Offer", "DHCP_REQ", "📤 Request", "DHCP_ACK", "✅ ACK"},
                {"【5. 传输层 - TCP 封装】", "T_SP", "🔩 源端口", "T_DP", "🎯 目的端口", "T_SEQ", "🔢 序列号",
                        "T_ACK", "📜 确认号", "T_CTL", "🚩 控制位", "T_WIN", "🌊 滑动窗口",
                        "T_CHK", "🔥 校验和", "T_CORE", "🟧 TCP 段总装"},
                {"【6. 网络层 - IP 封装】", "TX_IPH", "📦 IP 首部", "TX_IP_FRAG", "✂️ IP 分片器"},
                {"【7. 链路层 - Ethernet II】", "TX_ARP", "🔍 ARP 解析", "ETH_DST", "🟦 目的 MAC", "ETH_SRC", "🟦 源 MAC",
                        "ETH_TYPE", "🟦 EtherType", "TX_LLC", "🟩 LLC", "TX_FCS", "🟩 FCS"},
                {"【8. 边界网关】", "R_LAN", "🎛️ LAN 拆包", "R_TAB", "🔀 路由查表", "R_NAT", "🌍 NAT 转换", "R_WAN", "🛠️ WAN 封装"},
                {"【9. 公网路由器】", "ROUTER1", "📡 Router1", "ROUTER2", "📡 Router2", "ROUTER3", "📡 Router3"},
                {"【10. 接收端解封装】", "RX_LLC", "🔓 链路层解封", "RX_IP", "💛 网络层解封", "RX_TCP", "🧡 传输层解封", "RX_APP", "💚 应用层交付"}
        };

        for (String[] cat : categories) {
            JLabel title = new JLabel(cat[0]);
            title.setForeground(new Color(120, 30, 180));
            title.setFont(new Font("微软雅黑", Font.BOLD, 12));
            shopPanel.add(title);
            for (int i = 1; i < cat.length; i += 2) {
                final String tag = cat[i];
                String name = cat[i+1];
                JRadioButton rad = new JRadioButton(name);
                group.add(rad);
                rad.addActionListener(e -> selectedBuilding = tag);
                shopPanel.add(rad);
            }
        }
    }

    private void initMap() {
        mapLayout[2][1] = 1; mapLayout[3][1] = 1;
        mapLayout[12][1] = 2; mapLayout[13][1] = 2;

        for (int r = 0; r < MAP_ROWS; r++) {
            for (int c = 0; c < MAP_COLS; c++) {
                buildingLayout[r][c] = "NONE";
                if (c == 20 || c == 33) mapLayout[r][c] = 9;
            }
        }

        buildingLayout[MAP_ROWS/2][3] = "PC_FACTORY";
        pcFactory = new Point(3 * TILE_SIZE + TILE_SIZE/2, (MAP_ROWS/2) * TILE_SIZE + TILE_SIZE/2);
        buildingLayout[MAP_ROWS/2][MAP_COLS-3] = "RX_ST";
        buildingLayout[2][1] = "MINER_H"; buildingLayout[3][1] = "MINER_H";
        buildingLayout[12][1] = "MINER_S"; buildingLayout[13][1] = "MINER_S";

        int startRow = MAP_ROWS / 2 - 3;

        // DNS 行
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
        buildingLayout[startRow][17] = "TX_IPH";
        buildingLayout[startRow][18] = "TX_IP_FRAG";
        buildingLayout[startRow][19] = "TX_ARP";
        buildingLayout[startRow][20] = "ETH_DST";
        buildingLayout[startRow][21] = "ETH_SRC";
        buildingLayout[startRow][22] = "ETH_TYPE";
        buildingLayout[startRow][23] = "TX_LLC";
        buildingLayout[startRow][24] = "TX_FCS";

        // DHCP 行 (在 DNS 上方)
        int dhcpRow = MAP_ROWS / 2 - 4;
        buildingLayout[dhcpRow][4] = "DHCP_DISC";
        buildingLayout[dhcpRow][5] = "DHCP_SERVER";
        buildingLayout[dhcpRow][6] = "DHCP_OFFER";
        buildingLayout[dhcpRow][7] = "DHCP_REQ";
        buildingLayout[dhcpRow][8] = "DHCP_ACK";

        int gatewayRow = MAP_ROWS / 2;
        buildingLayout[gatewayRow][20] = "R_LAN";
        buildingLayout[gatewayRow][22] = "R_TAB";
        buildingLayout[gatewayRow][24] = "R_NAT";
        buildingLayout[gatewayRow][26] = "R_WAN";
        buildingLayout[gatewayRow][28] = "ROUTER1";
        buildingLayout[gatewayRow][30] = "ROUTER2";
        buildingLayout[gatewayRow][32] = "ROUTER3";

        int receiveRow = MAP_ROWS / 2 - 2;
        buildingLayout[receiveRow][36] = "RX_LLC";
        buildingLayout[receiveRow][38] = "RX_IP";
        buildingLayout[receiveRow][40] = "RX_TCP";
        buildingLayout[receiveRow][42] = "RX_APP";
    }

    // ----- DHCP 触发 -----
    private void startDhcpIfNeeded() {
        if (!pcIpAssigned && !dhcpInProgress) {
            dhcpInProgress = true;
            dhcpStep = 0;
            DataCart discover = new DataCart(pcFactory.x, pcFactory.y, "DHCP_DISCOVER", 0);
            discover.stage = 1; // 目标：DHCP_DISC
            pendingDataCarts.add(discover);
            appendToConsole("【🔎 DHCP】: 发送 DHCP Discover (PC 尚无 IP)");
            updateTopLabel();
        }
    }

    private void startDnsResolution() {
        if (!pcIpAssigned) {   // 没有 IP 无法上网
            appendToConsole("【⛔ DNS 阻断】: PC 未获取 IP，等待 DHCP 完成...");
            return;
        }
        if (isDnsResolved || isDnsResolving) return;
        isDnsResolving = true;
        appendToConsole("【🌐 DNS 解析开始】: 查询域名 " + targetDomain);

        DnsEntry cached = dnsCache.get(targetDomain);
        if (cached != null && !cached.isExpired()) {
            resolvedServerIp = cached.ipAddress;
            isDnsResolved = true;
            isDnsResolving = false;
            appendToConsole("【📚 DNS 缓存命中】: " + targetDomain + " → " + resolvedServerIp);
            performArpResolution(resolvedServerIp);
            startTcpHandshake();
            return;
        }

        DataCart dnsQuery = new DataCart(pcFactory.x, pcFactory.y, "DNS_QUERY", 0);
        dnsQuery.domain = targetDomain;
        pendingDataCarts.add(dnsQuery);
        appendToConsole("【📤 DNS 查询】: 发送 DNS 请求到本地 DNS 服务器");
    }

    private void performArpResolution(String targetIp) {
        if (!arpCache.containsKey(targetIp)) {
            appendToConsole("【🔍 ARP 请求】: 谁拥有 " + targetIp + "？");
            String mac = String.format("00:1A:2B:%02X:%02X:%02X",
                    new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
            arpCache.put(targetIp, new ArpEntry(targetIp, mac));
            appendToConsole("【📥 ARP 响应】: " + targetIp + " → " + mac);
            updateArpDisplay();
        } else {
            appendToConsole("【✅ ARP 缓存命中】: " + targetIp + " → " + arpCache.get(targetIp).macAddress);
        }
    }

    private void startTcpHandshake() {
        currentTcpState = TcpState.SYN_SENT;
        stateTimerWatchdog = System.currentTimeMillis();
        sentSeq.clear();
        ackedSeq.clear();

        DataCart syn = new DataCart(pcFactory.x, pcFactory.y, "SYN", 0);
        syn.sequenceNumber = 100;
        syn.ttl = 64;
        pendingDataCarts.add(syn);
        appendToConsole("【🤝 三次握手开始】: 发送 SYN (seq=100) 到 " + resolvedServerIp + " (TTL=64)");
        updateTopLabel();
    }

    private long lastResourceTick = 0;

    private void gameTick() {
        long now = System.currentTimeMillis();

        // 1. DHCP 自动触发
        startDhcpIfNeeded();

        if (now - stateTimerWatchdog > 60000) {
            appendToConsole("【⏰ 超时】: 连接超时，重置会话");
            resetTcpSession();
        }

        if (serverBufferCount > 0 && (now - lastServerConsumeTime >= serverDecodeDelay)) {
            serverBufferCount--;
            serverReceivedCount++;
            lastServerConsumeTime = now;
            rwnd = SERVER_BUFFER_MAX - serverBufferCount;
            updateTopLabel();
            appendToConsole(String.format("【📥 服务器处理】: 已处理 %d/%d 个数据包", serverReceivedCount, totalDataToTransmit));
        }

        if (currentTcpState == TcpState.ESTABLISHED) {
            List<RetransmissionTask> toRemove = new ArrayList<>();
            for (RetransmissionTask task : activeTimers) if (task.isAcked) toRemove.add(task);
            activeTimers.removeAll(toRemove);

            for (RetransmissionTask task : activeTimers) {
                if (!task.isAcked && (now - task.sendTime > RTO_TIMEOUT)) {
                    task.retryCount++;
                    if (task.retryCount >= 5) {
                        appendToConsole(String.format("【💀 连接崩溃】: SEQ=%d 重传失败", task.seqNum));
                        resetTcpSession();
                        return;
                    }
                    task.sendTime = now;
                    ssthresh = Math.max(2, cwnd / 2);
                    cwnd = 1;
                    packetsAckedSinceLastIncrease = 0;

                    DataCart retransmit = new DataCart(pcFactory.x, pcFactory.y, "DATA", task.seqNum);
                    retransmit.isRetransmission = true;
                    retransmit.ttl = 64;
                    pendingDataCarts.add(retransmit);
                    appendToConsole(String.format("【⚠️ 超时重传】: SEQ=%d (第%d次), ssthresh=%d, cwnd=1",
                            task.seqNum, task.retryCount, ssthresh));
                    updateTopLabel();
                }
            }
        }

        int currentCartsInWan = 0;
        for (DataCart c : dataCarts) {
            int col = (int)(c.x / TILE_SIZE);
            if (!c.isReturnTrip && c.stage >= 25 && c.stage <= 31 && col >= 21 && col <= 34) {
                currentCartsInWan++;
            }
        }

        if (now - lastResourceTick >= 1000) {
            for (int r = 0; r < MAP_ROWS; r++) {
                for (int c = 0; c < MAP_COLS; c++) {
                    if (buildingLayout[r][c].equals("MINER_H"))
                        oreCarts.add(new OreCart(c*TILE_SIZE+TILE_SIZE/2, r*TILE_SIZE+TILE_SIZE/2, "HELLO"));
                    if (buildingLayout[r][c].equals("MINER_S"))
                        oreCarts.add(new OreCart(c*TILE_SIZE+TILE_SIZE/2, r*TILE_SIZE+TILE_SIZE/2, "SAY"));
                }
            }

            // 只有 DHCP 完成后才启动 TCP 传输
            if (pcIpAssigned) {
                if (currentTcpState == TcpState.CLOSED) {
                    if (helloStock >= 2 && sayStock >= 1) {
                        if (!isDnsResolved && !isDnsResolving) startDnsResolution();
                        else if (isDnsResolved) startDnsResolution();
                    }
                } else if (currentTcpState == TcpState.ESTABLISHED) {
                    int effectiveWindow = Math.min(cwnd, rwnd);
                    appendToConsole(String.format("【📊 库存】: helloStock=%d, sayStock=%d, 有效窗口=%d",
                            helloStock, sayStock, effectiveWindow));
                    while (effectiveWindow > 0 && helloStock >= 2 && sayStock >= 1 && serverReceivedCount < totalDataToTransmit) {
                        helloStock -= 2; sayStock -= 1;
                        int currentSeq = nextSeqNum++;
                        activeTimers.add(new RetransmissionTask(currentSeq, now));
                        DataCart dataPkt = new DataCart(pcFactory.x, pcFactory.y, "DATA", currentSeq);
                        dataPkt.ttl = 64;
                        pendingDataCarts.add(dataPkt);
                        sentSeq.add(currentSeq);
                        appendToConsole(String.format("【📤 发送数据】: SEQ=%d, cwnd=%d, rwnd=%d, TTL=%d", currentSeq, cwnd, rwnd, 64));
                    }
                    if (serverReceivedCount >= totalDataToTransmit && activeTimers.isEmpty() && serverBufferCount == 0) {
                        currentTcpState = TcpState.FIN_WAIT_1;
                        stateTimerWatchdog = now;
                        DataCart fin = new DataCart(pcFactory.x, pcFactory.y, "FIN_PC", 0);
                        fin.ttl = 64;
                        pendingDataCarts.add(fin);
                        appendToConsole("【🏁 数据传输完成】: 发送 FIN，开始四次挥手");
                    }
                }
            }
            lastResourceTick = now;
            updateTopLabel();
        }

        if (currentTcpState == TcpState.ESTABLISHED && Math.min(cwnd, rwnd) == 0 && rwnd == 0) {
            if (now - lastProbeTime >= PROBE_INTERVAL) {
                lastProbeTime = now;
                DataCart zwp = new DataCart(pcFactory.x, pcFactory.y, "ZWP", 0);
                zwp.ttl = 64;
                pendingDataCarts.add(zwp);
                appendToConsole("【🔍 零窗口探测】: 发送 ZWP 探测包");
            }
        }

        oreCarts.removeIf(c -> {
            c.update();
            if (c.isArrived) { if (c.oreType.equals("HELLO")) helloStock++; else sayStock++; return true; }
            return false;
        });

        List<DataCart> toRemoveCarts = new ArrayList<>();
        for (DataCart cart : dataCarts) {
            int col = (int)(cart.x / TILE_SIZE);
            if (!cart.isReturnTrip && cart.stage >= 25 && cart.stage <= 31 && col >= 21 && col <= 34) {
                if (currentCartsInWan > WAN_BOTTLE_NECK_MAX && !isForemostCartInWan(cart)) {
                    cart.waitInQueueTimer++;
                    if (cart.waitInQueueTimer > 120) {
                        cart.isDropped = true;
                        appendToConsole(String.format("【💥 公网丢包】: %s 在公网排队超时，已坠毁", cart.cartType));
                    }
                    continue;
                } else {
                    cart.waitInQueueTimer = 0;
                }
            }

            cart.update();
            if (cart.isDropped) {
                toRemoveCarts.add(cart);
                updateTopLabel();
            } else if (cart.isArrived) {
                handleCartArrival(cart);
                toRemoveCarts.add(cart);
                updateTopLabel();
            }
        }
        dataCarts.removeAll(toRemoveCarts);

        if (!pendingDataCarts.isEmpty()) {
            dataCarts.addAll(pendingDataCarts);
            pendingDataCarts.clear();
        }

        dnsCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        prgNetwork.setValue((int)(((double)serverReceivedCount / totalDataToTransmit) * 100));
        canvas.repaint();
    }

    private boolean isForemostCartInWan(DataCart target) {
        double maxProgressX = 0;
        DataCart foremost = null;
        for (DataCart c : dataCarts) {
            int col = (int)(c.x / TILE_SIZE);
            if (!c.isReturnTrip && c.stage >= 25 && c.stage <= 31 && col >= 21 && col <= 34) {
                if (c.x > maxProgressX) { maxProgressX = c.x; foremost = c; }
            }
        }
        return foremost == null || foremost == target;
    }

    private void handleCartArrival(DataCart cart) {
        Point serverPos = findBuildingCoords("RX_ST");
        Point dhcpServerPos = findBuildingCoords("DHCP_SERVER");
        long now = System.currentTimeMillis();

        // ---------- DHCP 处理 ----------
        if (!cart.isReturnTrip) {
            switch (cart.cartType) {
                case "DHCP_DISCOVER":
                    if (cart.stage == 2) { // 到达 DHCP_SERVER
                        appendToConsole("【🔎 DHCP】: Discover 到达服务器");
                        DataCart offer = new DataCart(dhcpServerPos.x, dhcpServerPos.y, "DHCP_OFFER", 0);
                        offer.isReturnTrip = true;
                        offer.stage = 1; // 回家路径：DHCP_OFFER -> PC_FACTORY
                        pendingDataCarts.add(offer);
                        appendToConsole("【📥 DHCP】: 服务器回复 Offer (192.168.1.100)");
                    }
                    break;
                case "DHCP_REQUEST":
                    if (cart.stage == 2) { // 到达 DHCP_SERVER
                        appendToConsole("【📤 DHCP】: Request 到达服务器");
                        DataCart ack = new DataCart(dhcpServerPos.x, dhcpServerPos.y, "DHCP_ACK", 0);
                        ack.isReturnTrip = true;
                        ack.stage = 1;
                        pendingDataCarts.add(ack);
                        appendToConsole("【✅ DHCP】: 服务器回复 ACK，分配 IP 192.168.1.100");
                    }
                    break;
            }
        } else { // 回程包
            switch (cart.cartType) {
                case "DHCP_OFFER":
                    appendToConsole("【📥 DHCP】: Offer 到达客户端");
                    dhcpStep = 2;
                    // 发送 Request
                    DataCart request = new DataCart(pcFactory.x, pcFactory.y, "DHCP_REQUEST", 0);
                    request.stage = 1; // 目标：DHCP_REQ -> DHCP_SERVER
                    pendingDataCarts.add(request);
                    appendToConsole("【📤 DHCP】: 发送 Request");
                    break;
                case "DHCP_ACK":
                    appendToConsole("【✅ DHCP】: ACK 到达，IP 分配成功！");
                    pcIpAssigned = true;
                    pcIpAddress = "192.168.1.100";
                    dhcpInProgress = false;
                    dhcpStep = 4;
                    funds += 200;
                    updateTopLabel();
                    appendToConsole("【🌐 网络就绪】: PC IP = " + pcIpAddress);
                    break;
            }
        }

        // ---------- 原有协议处理 ----------
        if (!cart.isReturnTrip) {
            switch(cart.cartType) {
                case "DNS_QUERY":
                    appendToConsole("【📥 DNS 查询】: 到达 DNS 服务器，正在递归查询...");
                    DataCart dnsResp = new DataCart(cart.x, cart.y, "DNS_RESPONSE", 0);
                    dnsResp.domain = cart.domain;
                    dnsResp.resolvedIp = "10.0.0.1";
                    dnsResp.isReturnTrip = true;
                    pendingDataCarts.add(dnsResp);
                    break;
                case "DNS_RESPONSE":
                    resolvedServerIp = cart.resolvedIp;
                    isDnsResolved = true;
                    isDnsResolving = false;
                    dnsCache.put(targetDomain, new DnsEntry(targetDomain, resolvedServerIp, 3600000));
                    updateDnsDisplay();
                    appendToConsole("【🌐 DNS 解析成功】: " + targetDomain + " → " + resolvedServerIp);
                    performArpResolution(resolvedServerIp);
                    startTcpHandshake();
                    break;
                case "SYN":
                case "DATA":
                case "FIN_PC":
                case "ZWP":
                    if (cart.currentLayerStatus.contains("Router")) {
                        cart.ttl--;
                        if (cart.ttl <= 0) {
                            cart.isDropped = true;
                            appendToConsole(String.format("【⚠️ ICMP Time Exceeded】: %s (SEQ=%d) TTL 降为 0，数据包被丢弃",
                                    cart.cartType, cart.sequenceNumber));
                            return;
                        }
                        appendToConsole(String.format("【📡 路由器】: %s 经过 %s，TTL=%d",
                                cart.cartType, cart.currentLayerStatus, cart.ttl));
                    }
                    break;
            }
            if (cart.cartType.equals("SYN") && !cart.isReturnTrip && cart.isArrived) {
                DataCart synAck = new DataCart(serverPos.x, serverPos.y, "SYN_ACK", 0);
                synAck.isReturnTrip = true;
                synAck.ackNumber = cart.sequenceNumber + 1;
                synAck.sequenceNumber = 200;
                pendingDataCarts.add(synAck);
                appendToConsole("【🤝 三次握手】: 收到 SYN，回复 SYN-ACK (seq=200, ack=" + (cart.sequenceNumber+1) + ")");
                stateTimerWatchdog = now;
                return;
            }
            if (cart.cartType.equals("DATA") && !cart.isReturnTrip && cart.isArrived) {
                if (serverBufferCount < SERVER_BUFFER_MAX) {
                    serverBufferCount++;
                    if (serverBufferCount == 1) lastServerConsumeTime = now;
                    rwnd = SERVER_BUFFER_MAX - serverBufferCount;

                    if (!ackedSeq.contains(cart.sequenceNumber)) {
                        ackedSeq.add(cart.sequenceNumber);
                        DataCart dataAck = new DataCart(serverPos.x, serverPos.y, "DATA_ACK", cart.sequenceNumber);
                        dataAck.advertisedWindow = rwnd;
                        dataAck.isReturnTrip = true;
                        pendingDataCarts.add(dataAck);
                        funds += 500;
                        appendToConsole(String.format("【📦 数据交付】: SEQ=%d 已接收，回复 ACK (rwnd=%d)",
                                cart.sequenceNumber, rwnd));
                    }
                } else {
                    appendToConsole(String.format("【💥 缓冲区溢出】: SEQ=%d 丢失", cart.sequenceNumber));
                }
                return;
            }
            if (cart.cartType.equals("FIN_PC") && !cart.isReturnTrip && cart.isArrived) {
                DataCart finAck = new DataCart(serverPos.x, serverPos.y, "FIN_ACK_SRV", 0);
                finAck.isReturnTrip = true;
                pendingDataCarts.add(finAck);
                DataCart srvFin = new DataCart(serverPos.x, serverPos.y, "FIN_SRV", 0);
                srvFin.isReturnTrip = true;
                pendingDataCarts.add(srvFin);
                appendToConsole("【👋 四次挥手】: 收到 FIN，回复 FIN-ACK，发送 FIN");
                stateTimerWatchdog = now;
                return;
            }
            if (cart.cartType.equals("ZWP") && !cart.isReturnTrip && cart.isArrived) {
                DataCart probeAck = new DataCart(serverPos.x, serverPos.y, "DATA_ACK", 0);
                probeAck.advertisedWindow = SERVER_BUFFER_MAX - serverBufferCount;
                probeAck.isReturnTrip = true;
                pendingDataCarts.add(probeAck);
                appendToConsole("【🔍 零窗口探测响应】: rwnd=" + (SERVER_BUFFER_MAX - serverBufferCount));
                return;
            }
        } else {
            switch(cart.cartType) {
                case "SYN_ACK":
                    DataCart finalAck = new DataCart(pcFactory.x, pcFactory.y, "ACK_PC", 0);
                    finalAck.ackNumber = cart.sequenceNumber + 1;
                    pendingDataCarts.add(finalAck);
                    // 修复：直接进入连接建立状态
                    currentTcpState = TcpState.ESTABLISHED;
                    cwnd = 1;
                    ssthresh = 12;
                    packetsAckedSinceLastIncrease = 0;
                    appendToConsole("【🤝 三次握手完成】: 收到 SYN-ACK，发送 ACK，连接建立！cwnd=1, ssthresh=12");
                    stateTimerWatchdog = now;
                    break;
                case "ACK_PC":
                    // 已通过 SYN_ACK 处理，此处保留但不会触发
                    break;
                case "DATA_ACK":
                    rwnd = cart.advertisedWindow;
                    if (cart.sequenceNumber > 0) {
                        if (cwnd < ssthresh) {
                            cwnd++;
                            appendToConsole(String.format("【📈 慢启动】: cwnd=%d, ssthresh=%d", cwnd, ssthresh));
                        } else {
                            packetsAckedSinceLastIncrease++;
                            if (packetsAckedSinceLastIncrease >= cwnd) {
                                cwnd++;
                                packetsAckedSinceLastIncrease = 0;
                                appendToConsole(String.format("【🐌 拥塞避免】: cwnd=%d", cwnd));
                            }
                        }
                        for (RetransmissionTask task : activeTimers) {
                            if (task.seqNum == cart.sequenceNumber) {
                                task.isAcked = true;
                                break;
                            }
                        }
                    }
                    break;
                case "FIN_ACK_SRV":
                    if (currentTcpState == TcpState.FIN_WAIT_1) {
                        currentTcpState = TcpState.FIN_WAIT_2;
                        appendToConsole("【👋 四次挥手】: 收到 FIN-ACK，进入 FIN-WAIT-2");
                        stateTimerWatchdog = now;
                    }
                    break;
                case "FIN_SRV":
                    currentTcpState = TcpState.TIME_WAIT;
                    DataCart lastAck = new DataCart(pcFactory.x, pcFactory.y, "LAST_ACK_PC", 0);
                    pendingDataCarts.add(lastAck);
                    appendToConsole("【👋 四次挥手】: 收到 FIN，回复 ACK，进入 TIME-WAIT");
                    Timer timer = new Timer(1500, e -> {
                        if (currentTcpState == TcpState.TIME_WAIT) {
                            resetTcpSession();
                            JOptionPane.showMessageDialog(DataCartFactoryGame.this,
                                    "🎉 数据传输完成！\n\n共传输 " + totalDataToTransmit + " 个数据包\n" +
                                            "演示了 DHCP、DNS、ARP、TCP 三次握手、滑动窗口、拥塞控制、IP 分片、NAT、Ethernet II 封装、TTL 递减、四次挥手",
                                    "传输成功", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                    break;
            }
        }
    }

    private void resetTcpSession() {
        currentTcpState = TcpState.CLOSED;
        serverReceivedCount = 0;
        serverBufferCount = 0;
        cwnd = 1; ssthresh = 12; rwnd = 3;
        packetsAckedSinceLastIncrease = 0;
        nextSeqNum = 100;
        sentSeq.clear();
        ackedSeq.clear();
        dataCarts.clear();
        pendingDataCarts.clear();
        activeTimers.clear();
        isDnsResolving = false;
        isDnsResolved = false;
        resolvedServerIp = null;
        // DHCP 状态保持不变（已获得IP）
        updateTopLabel();
        canvas.repaint();
    }

    private void updateTopLabel() {
        int effectiveWin = Math.min(cwnd, rwnd);
        String ipStatus = pcIpAssigned ? pcIpAddress : "未分配";
        lblDashboard.setText(String.format(
                "💰 资金:%d | 🏷️ TCP:%s | 🌐 IP:%s | 🚩 cwnd:%d | 🎯 ssthresh:%d | 📥 rwnd:%d | 🎛️ 有效窗口:%d | " +
                        "📦 仓储:%d/%d | ✅ 达成:%d/%d | 🔍 ARP:%d | 📚 DNS:%d | 🌐 域名:%s → %s",
                funds, currentTcpState, ipStatus, cwnd, ssthresh, rwnd, effectiveWin,
                serverBufferCount, SERVER_BUFFER_MAX, serverReceivedCount, totalDataToTransmit,
                arpCache.size(), dnsCache.size(), targetDomain,
                resolvedServerIp == null ? "未解析" : resolvedServerIp));
    }

    private Point findBuildingCoords(String tag) {
        for (int r = 0; r < MAP_ROWS; r++) {
            for (int c = 0; c < MAP_COLS; c++) {
                if (buildingLayout[r][c].equals(tag))
                    return new Point(c*TILE_SIZE+TILE_SIZE/2, r*TILE_SIZE+TILE_SIZE/2);
            }
        }
        return null;
    }

    // ----- 内部类 OreCart -----
    private class OreCart {
        double x, y; double speed = 6.0; String oreType; boolean isArrived = false;
        public OreCart(double x, double y, String type) { this.x = x; this.y = y; this.oreType = type; }
        public void update() {
            double dx = pcFactory.x - x; double dy = pcFactory.y - y;
            double dist = Math.sqrt(dx*dx+dy*dy);
            if (dist <= speed) isArrived = true;
            else { x += (dx/dist)*speed; y += (dy/dist)*speed; }
        }
    }

    // ----- 内部类 DataCart -----
    private class DataCart {
        double x, y; double speed = 12.0; int stage; int timer = 0;
        boolean isArrived = false; boolean isDropped = false; boolean isReturnTrip = false;
        boolean isRetransmission = false;
        String cartType; String currentLayerStatus = "";
        int sequenceNumber = 0;
        int ackNumber = 0;
        int advertisedWindow = 3;
        int waitInQueueTimer = 0;
        int ttl = 64;

        String domain;
        String resolvedIp;

        boolean hasPayload = true;
        boolean hasApp = false, hasTcp = false, hasIp = false, hasEther = false, hasLlc = false, hasFcs = false;
        boolean c_Payload=false, c_SP=false, c_DP=false, c_SEQ=false, c_ACK=false, c_CTL=false, c_WIN=false, c_CHK=false;

        public DataCart(double sx, double sy, String type, int seq) {
            this.x = sx; this.y = sy; this.cartType = type; this.sequenceNumber = seq;
            if (isControlFrame(type)) { this.stage = 2; }
            else { this.stage = 1; }
        }

        public boolean isControlFrame(String type) {
            return type.equals("SYN") || type.equals("SYN_ACK") || type.equals("ACK_PC")
                    || type.equals("FIN_PC") || type.equals("FIN_ACK_SRV") || type.equals("FIN_SRV")
                    || type.equals("DATA_ACK") || type.equals("LAST_ACK_PC") || type.equals("ZWP")
                    || type.equals("DNS_QUERY") || type.equals("DNS_RESPONSE")
                    || type.equals("DHCP_DISCOVER") || type.equals("DHCP_OFFER")
                    || type.equals("DHCP_REQUEST") || type.equals("DHCP_ACK");
        }

        public void update() {
            if (timer > 0) { timer--; return; }
            Point target;

            // DHCP 返回包或正向包都走自定义 stage 路径
            if (isDHCP()) {
                target = findTargetMachine(stage, cartType);
                if (target == null) target = isReturnTrip ? pcFactory : findBuildingCoords("DHCP_SERVER");
            } else {
                target = isReturnTrip ? pcFactory : findTargetMachine(stage, cartType);
            }

            if (target == null) {
                target = pcFactory; // fallback
            }

            double dx = target.x - x; double dy = target.y - y;
            double dist = Math.sqrt(dx*dx+dy*dy);
            if (dist <= speed) {
                x = target.x; y = target.y;
                if (!isReturnTrip || isDHCP()) {
                    processStageCraft();
                    if (stage >= 26 && stage <= 28) {
                        ttl--;
                        if (ttl <= 0) {
                            isDropped = true;
                            appendToConsole(String.format("【⚠️ ICMP Time Exceeded】: %s (SEQ=%d) TTL 降为 0，数据包被丢弃",
                                    cartType, sequenceNumber));
                            return;
                        }
                        appendToConsole(String.format("【📡 路由器】: %s 经过 %s，TTL=%d",
                                cartType, currentLayerStatus, ttl));
                    }
                    if (!isDHCP()) {
                        if (stage < 32) {
                            timer = 1;
                            stage++;
                            appendToConsole(String.format("【🚚 数据包】: %s 到达 %s，下一站 stage=%d, TTL=%d",
                                    cartType, currentLayerStatus, stage, ttl));
                        } else {
                            isArrived = true;
                        }
                    } else {
                        // DHCP 包: stage 递增，到达终点时 isArrived
                        int maxStage = (cartType.equals("DHCP_DISCOVER") || cartType.equals("DHCP_REQUEST")) ? 2 : 2;
                        if (stage < maxStage) {
                            timer = 1;
                            stage++;
                            appendToConsole(String.format("【🚚 DHCP】: %s stage=%d", cartType, stage));
                        } else {
                            isArrived = true;
                        }
                    }
                } else {
                    isArrived = true;
                }
            } else {
                x += (dx/dist)*speed;
                y += (dy/dist)*speed;
            }
        }

        private boolean isDHCP() {
            return cartType.equals("DHCP_DISCOVER") || cartType.equals("DHCP_OFFER")
                    || cartType.equals("DHCP_REQUEST") || cartType.equals("DHCP_ACK");
        }

        private Point findTargetMachine(int s, String type) {
            // DHCP 自定义路径
            if (type.equals("DHCP_DISCOVER")) {
                switch(s) {
                    case 1: return findBuildingCoords("DHCP_DISC");
                    case 2: return findBuildingCoords("DHCP_SERVER");
                }
            } else if (type.equals("DHCP_OFFER")) {
                switch(s) {
                    case 1: return findBuildingCoords("DHCP_OFFER");
                    case 2: return findBuildingCoords("PC_FACTORY");
                }
            } else if (type.equals("DHCP_REQUEST")) {
                switch(s) {
                    case 1: return findBuildingCoords("DHCP_REQ");
                    case 2: return findBuildingCoords("DHCP_SERVER");
                }
            } else if (type.equals("DHCP_ACK")) {
                switch(s) {
                    case 1: return findBuildingCoords("DHCP_ACK");
                    case 2: return findBuildingCoords("PC_FACTORY");
                }
            }

            // 原有路径
            String tag = "NONE";
            switch(s) {
                case 1: tag = "DNS_CLIENT"; break;
                case 2: tag = "DNS_LOCAL"; break;
                case 3: tag = "DNS_ROOT"; break;
                case 4: tag = "DNS_AUTH"; break;
                case 5: tag = "TX_APP"; break;
                case 6: tag = "T_SP"; break; case 7: tag = "T_DP"; break;
                case 8: tag = "T_SEQ"; break; case 9: tag = "T_ACK"; break;
                case 10: tag = "T_CTL"; break; case 11: tag = "T_WIN"; break;
                case 12: tag = "T_CHK"; break; case 13: tag = "T_CORE"; break;
                case 14: tag = "TX_IPH"; break; case 15: tag = "TX_IP_FRAG"; break;
                case 16: tag = "TX_ARP"; break;
                case 17: tag = "ETH_DST"; break;
                case 18: tag = "ETH_SRC"; break;
                case 19: tag = "ETH_TYPE"; break;
                case 20: tag = "TX_LLC"; break;
                case 21: tag = "TX_FCS"; break;
                case 22: tag = "R_LAN"; break;
                case 23: tag = "R_TAB"; break;
                case 24: tag = "R_NAT"; break;
                case 25: tag = "R_WAN"; break;
                case 26: tag = "ROUTER1"; break;
                case 27: tag = "ROUTER2"; break;
                case 28: tag = "ROUTER3"; break;
                case 29: tag = "RX_LLC"; break;
                case 30: tag = "RX_IP"; break;
                case 31: tag = "RX_TCP"; break;
                case 32: tag = "RX_APP"; break;
                default: return null;
            }
            return findBuildingCoords(tag);
        }

        private void processStageCraft() {
            switch(stage) {
                case 1: if (cartType.equals("DHCP_DISCOVER")) currentLayerStatus = "🔎 DHCP Discover"; break;
                case 2: if (cartType.equals("DHCP_OFFER")) currentLayerStatus = "📥 DHCP Offer"; break;
                // ... 其他 stage 沿用原逻辑
            }
            // 保留原有描述（简化，不影响）
            if (cartType.startsWith("DHCP")) return;
            switch(stage) {
                case 1: currentLayerStatus = "🔍 DNS 查询 (" + domain + ")"; break;
                case 2: currentLayerStatus = "📡 本地 DNS"; break;
                case 3: currentLayerStatus = "🌐 根 DNS"; break;
                case 4: currentLayerStatus = "🏢 权威 DNS → " + resolvedIp; break;
                case 5: hasApp = true; currentLayerStatus = "💚 应用数据"; break;
                case 6: c_SP = true; currentLayerStatus = "⚙️ 源端口"; break;
                case 7: c_DP = true; currentLayerStatus = "🎯 目的端口"; break;
                case 8: c_SEQ = true; currentLayerStatus = "🔢 SEQ:" + sequenceNumber; break;
                case 9: c_ACK = true; currentLayerStatus = "📜 ACK:" + ackNumber; break;
                case 10: c_CTL = true; currentLayerStatus = "🚩 [" + cartType + "]"; break;
                case 11: c_WIN = true; currentLayerStatus = "🌊 WIN:" + advertisedWindow; break;
                case 12: c_CHK = true; currentLayerStatus = "🔥 校验和"; break;
                case 13: hasTcp = true; currentLayerStatus = "🧡 TCP 段"; break;
                case 14: hasIp = true; currentLayerStatus = "💛 IP 首部"; break;
                case 15: currentLayerStatus = "✂️ IP 分片"; break;
                case 16: currentLayerStatus = "🔍 ARP 解析"; break;
                case 17: hasEther = true; currentLayerStatus = "🟦 目的 MAC (DST)"; break;
                case 18: hasEther = true; currentLayerStatus = "🟦 源 MAC (SRC)"; break;
                case 19: hasEther = true; currentLayerStatus = "🟦 EtherType (0x0800)"; break;
                case 20: hasLlc = true; currentLayerStatus = "🟩 LLC (可选)"; break;
                case 21: hasFcs = true; currentLayerStatus = "🟩 FCS"; break;
                case 22: hasLlc = false; hasFcs = false; currentLayerStatus = "🎛️ LAN 拆包"; break;
                case 23: currentLayerStatus = "🔀 路由查表"; break;
                case 24: currentLayerStatus = "🌍 NAT 转换"; break;
                case 25: hasLlc = true; hasFcs = true; currentLayerStatus = "🛠️ WAN 封装"; break;
                case 26: currentLayerStatus = "📡 Router1"; break;
                case 27: currentLayerStatus = "📡 Router2"; break;
                case 28: currentLayerStatus = "📡 Router3"; break;
                case 29: hasLlc = false; hasFcs = false; currentLayerStatus = "🔓 剥链路层"; break;
                case 30: hasIp = false; currentLayerStatus = "💛 剥网络层"; break;
                case 31: hasTcp = false; currentLayerStatus = "🧡 剥传输层"; break;
                case 32: hasApp = false; currentLayerStatus = "💚 应用交付"; break;
                default: currentLayerStatus = "未知状态";
            }
        }
    }

    // ----- 画布 -----
    private class GameCanvas extends JPanel {
        public GameCanvas() { setBackground(new Color(18, 20, 26)); }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int r = 0; r < MAP_ROWS; r++) {
                for (int c = 0; c < MAP_COLS; c++) {
                    int x = c * TILE_SIZE, y = r * TILE_SIZE;
                    if (mapLayout[r][c] == 9) {
                        g2.setColor(new Color(35, 38, 48));
                        g2.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        g2.setColor(Color.BLACK);
                        g2.drawRect(x, y, TILE_SIZE, TILE_SIZE);
                        continue;
                    }
                    g2.setColor(new Color(30, 32, 40));
                    g2.drawRect(x, y, TILE_SIZE, TILE_SIZE);
                    if (mapLayout[r][c] == 1) {
                        g2.setColor(new Color(40, 100, 220, 60));
                        g2.fillOval(x+6, y+6, TILE_SIZE-12, TILE_SIZE-12);
                    }
                    if (mapLayout[r][c] == 2) {
                        g2.setColor(new Color(40, 200, 100, 60));
                        g2.fillOval(x+6, y+6, TILE_SIZE-12, TILE_SIZE-12);
                    }
                }
            }

            g2.setColor(new Color(255, 100, 0, 15));
            g2.fillRect(21 * TILE_SIZE, 0, 14 * TILE_SIZE, MAP_ROWS * TILE_SIZE);

            int wanCarCount = 0;
            for (DataCart c : dataCarts) {
                int col = (int)(c.x / TILE_SIZE);
                if (!c.isReturnTrip && c.stage >= 25 && c.stage <= 31 && col >= 21 && col <= 34) wanCarCount++;
            }
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("微软雅黑", Font.BOLD, 14));
            g2.drawString("🚦 公网车辆: " + wanCarCount + "/" + WAN_BOTTLE_NECK_MAX, 22 * TILE_SIZE, 30);

            for (int r = 0; r < MAP_ROWS; r++) {
                for (int c = 0; c < MAP_COLS; c++) {
                    int x = c * TILE_SIZE, y = r * TILE_SIZE;
                    String tag = buildingLayout[r][c];
                    if (tag.equals("NONE")) continue;

                    g2.setColor(new Color(45, 48, 58));
                    g2.fillRect(x+2, y+2, TILE_SIZE-4, TILE_SIZE-4);
                    g2.setColor(Color.GRAY);
                    g2.drawRect(x+2, y+2, TILE_SIZE-4, TILE_SIZE-4);
                    g2.setFont(new Font("Consolas", Font.BOLD, 8));
                    g2.setColor(Color.WHITE);

                    if (tag.equals("PC_FACTORY")) {
                        g2.setColor(new Color(0,130,200));
                        g2.fillRect(x+2,y+2,TILE_SIZE-4,TILE_SIZE-4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("💻 源PC", x+4, y+24);
                    } else if (tag.equals("RX_ST")) {
                        g2.setColor(new Color(190,30,50));
                        g2.fillRect(x+2,y+2,TILE_SIZE-4,TILE_SIZE-4);
                        g2.setColor(Color.YELLOW);
                        g2.drawString("🏛️ 服务器", x+2, y+24);
                        for(int b=0; b<serverBufferCount; b++) {
                            g2.setColor(Color.RED);
                            g2.fillRect(x + 4 + (b*6), y + 4, 5, 6);
                        }
                    } else if (tag.startsWith("DHCP_")) {
                        g2.setColor(new Color(100, 100, 255));
                        g2.fillRect(x+2, y+2, TILE_SIZE-4, TILE_SIZE-4);
                        g2.setColor(Color.WHITE);
                        g2.drawString(tag, x+3, y+24);
                    } else if (tag.equals("DNS_CLIENT") || tag.equals("DNS_LOCAL") || tag.equals("DNS_ROOT") || tag.equals("DNS_AUTH")) {
                        g2.setColor(new Color(0, 200, 200));
                        g2.fillRect(x+2, y+2, TILE_SIZE-4, TILE_SIZE-4);
                        g2.setColor(Color.BLACK);
                        g2.drawString(tag, x+3, y+24);
                    } else if (tag.startsWith("ETH_")) {
                        g2.setColor(new Color(0, 160, 255));
                        g2.fillRect(x+2, y+2, TILE_SIZE-4, TILE_SIZE-4);
                        g2.setColor(Color.BLACK);
                        g2.drawString(tag, x+3, y+24);
                    } else if (tag.startsWith("ROUTER")) {
                        g2.setColor(new Color(200, 100, 0));
                        g2.fillRect(x+2, y+2, TILE_SIZE-4, TILE_SIZE-4);
                        g2.setColor(Color.BLACK);
                        g2.drawString(tag, x+3, y+24);
                    } else if (tag.startsWith("T_") || tag.startsWith("TX_") || tag.startsWith("R_") || tag.startsWith("RX_")) {
                        if (tag.contains("NAT")) g2.setColor(new Color(255, 165, 0));
                        else if (tag.contains("ARP")) g2.setColor(new Color(0, 255, 255));
                        else if (tag.startsWith("R_")) g2.setColor(Color.CYAN);
                        else if (tag.startsWith("RX_")) g2.setColor(Color.MAGENTA);
                        else g2.setColor(Color.ORANGE);
                        g2.drawRect(x+2, y+2, TILE_SIZE-4, TILE_SIZE-4);
                        g2.drawString(tag, x+3, y+24);
                    } else if (tag.startsWith("MINER_H")) {
                        g2.setColor(Color.CYAN);
                        g2.drawString("🔷 矿机", x+4, y+24);
                    } else if (tag.startsWith("MINER_S")) {
                        g2.setColor(Color.GREEN);
                        g2.drawString("🟩 矿机", x+4, y+24);
                    }
                }
            }

            for (OreCart c : oreCarts) {
                g2.setColor(c.oreType.equals("HELLO") ? Color.CYAN : Color.GREEN);
                g2.fillOval((int)c.x-5, (int)c.y-5, 10, 10);
            }

            for (DataCart cart : dataCarts) {
                int cx = (int)cart.x, cy = (int)cart.y;

                if (cart.waitInQueueTimer > 0) g2.setColor(Color.RED);
                else if (cart.cartType.startsWith("DHCP")) g2.setColor(Color.MAGENTA);
                else if (cart.cartType.equals("ZWP")) g2.setColor(Color.YELLOW);
                else if (cart.cartType.startsWith("SYN")) g2.setColor(Color.CYAN);
                else if (cart.cartType.startsWith("FIN")) g2.setColor(Color.PINK);
                else if (cart.cartType.contains("ACK")) g2.setColor(Color.GREEN);
                else if (cart.cartType.startsWith("DNS")) g2.setColor(new Color(0, 200, 200));
                else if (cart.isRetransmission) g2.setColor(Color.ORANGE);
                else g2.setColor(Color.LIGHT_GRAY);
                g2.fillOval(cx-7, cy-7, 14, 14);

                int bx = cx - 30;
                if (cart.hasApp) { g2.setColor(new Color(100, 255, 100)); g2.fillRect(bx, cy-5, 6, 10); bx += 6; }
                if (cart.hasTcp) { g2.setColor(Color.ORANGE); g2.fillRect(bx, cy-5, 7, 10); bx += 7; }
                if (cart.hasIp) { g2.setColor(Color.YELLOW); g2.fillRect(bx, cy-5, 6, 10); bx += 6; }
                if (cart.hasEther) { g2.setColor(new Color(0, 160, 255)); g2.fillRect(bx, cy-5, 8, 10); bx += 8; }
                if (cart.hasLlc) { g2.setColor(Color.GREEN); g2.fillRect(bx, cy-5, 6, 10); bx += 6; }
                if (cart.hasFcs) { g2.setColor(Color.GREEN.darker()); g2.fillRect(bx, cy-5, 6, 10); }

                g2.setFont(new Font("微软雅黑", Font.BOLD, 10));
                String direction = cart.isReturnTrip ? "◀ 回传" : (cart.waitInQueueTimer > 0 ? "⚠️ 排队" : "▶ 发送");
                String nameTag = cart.cartType;
                if (cart.isRetransmission && cart.cartType.equals("DATA")) nameTag = "重传-" + cart.sequenceNumber;
                String label = String.format("%s %s TTL:%d", nameTag, direction, cart.ttl);
                if (cart.domain != null && !cart.domain.isEmpty()) label += " [" + cart.domain + "]";
                if (cart.resolvedIp != null) label += " → " + cart.resolvedIp;

                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(cx - 55, cy - 25, g2.getFontMetrics().stringWidth(label) + 8, 16);
                g2.setColor(cart.waitInQueueTimer > 0 ? Color.RED : Color.YELLOW);
                g2.drawString(label, cx - 53, cy - 14);
            }
        }
    }

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        SwingUtilities.invokeLater(() -> new DataCartFactoryGame().setVisible(true));
    }
}