package com.never_give_up.automation.demo;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DataCartFactoryGame extends JFrame {

    enum TcpState {
        CLOSED, SYN_SENT, ESTABLISHED,
        FIN_WAIT_1, FIN_WAIT_2, CLOSE_WAIT, LAST_ACK, TIME_WAIT
    }

    // --- ARP 缓存表项 ---
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

    // --- NAT 转换表项 ---
    private static class NatEntry {
        String privateIp;
        int privatePort;
        String publicIp;
        int publicPort;
        long created;
        public NatEntry(String privateIp, int privatePort, String publicIp, int publicPort) {
            this.privateIp = privateIp;
            this.privatePort = privatePort;
            this.publicIp = publicIp;
            this.publicPort = publicPort;
            this.created = System.currentTimeMillis();
        }
    }

    // --- IP 分片片段 ---
    private static class IpFragment {
        int packetId;
        int fragmentOffset;
        int totalLength;
        boolean isLastFragment;
        byte[] data;
        long receivedTime;
        public IpFragment(int id, int offset, int totalLen, boolean last, byte[] d) {
            this.packetId = id;
            this.fragmentOffset = offset;
            this.totalLength = totalLen;
            this.isLastFragment = last;
            this.data = d;
            this.receivedTime = System.currentTimeMillis();
        }
    }

    // --- 重传任务 ---
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

    // 拥塞控制变量
    private int rwnd = 3;
    private int cwnd = 1;
    private int ssthresh = 8;

    private int serverBufferCount = 0;
    private final int SERVER_BUFFER_MAX = 5;
    private long lastServerConsumeTime = 0;
    private int serverDecodeDelay = 1200;

    private final int WAN_BOTTLE_NECK_MAX = 2;
    private int inFlightCount = 0;
    private long stateTimerWatchdog = 0;
    private final long RTO_TIMEOUT = 4000;
    private int nextSeqNum = 100;
    private long lastProbeTime = 0;
    private final long PROBE_INTERVAL = 3000;

    private List<RetransmissionTask> activeTimers = new CopyOnWriteArrayList<>();

    // ARP 和 NAT 相关
    private Map<String, ArpEntry> arpCache = new HashMap<>();
    private List<NatEntry> natTable = new CopyOnWriteArrayList<>();
    private int nextPublicPort = 10000;

    // IP 分片重组缓冲区
    private Map<Integer, List<IpFragment>> fragmentBuffer = new HashMap<>();

    private String selectedBuilding = "NONE";
    private final int PRICE_MINER = 30;
    private final int PRICE_MACHINE = 20;
    private final int PRICE_UPGRADE_SERVER = 400;

    private final int TILE_SIZE = 40;
    private final int MAP_COLS = 42;
    private final int MAP_ROWS = 18;

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

    private int packetsAckedSinceLastIncrease = 0;
    private int nextPacketId = 1000;

    public DataCartFactoryGame() {
        setTitle("🌐 全协议栈网络可视化模拟器 | ARP | TCP三次握手 | 滑动窗口 | 拥塞控制 | NAT | IP分片 | 四次挥手");
        setSize(1800, 1000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initMap();
        initArpCache();

        // 顶部仪表盘
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("📊 协议栈状态仪表盘"));
        lblDashboard = new JLabel("", JLabel.CENTER);
        lblDashboard.setFont(new Font("微软雅黑", Font.BOLD, 14));
        topPanel.add(lblDashboard, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // 左侧面板
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(380, 0));

        shopPanel = new JPanel();
        shopPanel.setLayout(new BoxLayout(shopPanel, BoxLayout.Y_AXIS));
        JScrollPane shopScroll = new JScrollPane(shopPanel);
        shopScroll.setPreferredSize(new Dimension(380, 400));
        shopScroll.setBorder(BorderFactory.createTitledBorder("🏭 网络设备工厂"));

        // ARP 显示面板
        txtArpDisplay = new JTextArea(8, 30);
        txtArpDisplay.setEditable(false);
        txtArpDisplay.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtArpDisplay.setBackground(new Color(20, 25, 35));
        txtArpDisplay.setForeground(new Color(100, 200, 255));
        JScrollPane arpScroll = new JScrollPane(txtArpDisplay);
        arpScroll.setBorder(BorderFactory.createTitledBorder("📋 ARP 缓存表"));

        // NAT 显示面板
        txtNatDisplay = new JTextArea(6, 30);
        txtNatDisplay.setEditable(false);
        txtNatDisplay.setFont(new Font("Consolas", Font.PLAIN, 11));
        txtNatDisplay.setBackground(new Color(20, 25, 35));
        txtNatDisplay.setForeground(new Color(255, 200, 100));
        JScrollPane natScroll = new JScrollPane(txtNatDisplay);
        natScroll.setBorder(BorderFactory.createTitledBorder("🌍 NAT 转换表"));

        leftPanel.add(shopScroll, BorderLayout.NORTH);
        leftPanel.add(arpScroll, BorderLayout.CENTER);
        leftPanel.add(natScroll, BorderLayout.SOUTH);
        add(leftPanel, BorderLayout.WEST);

        // 中央画布
        canvas = new GameCanvas();
        add(canvas, BorderLayout.CENTER);

        // 底部控制台
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("📟 协议分析控制台"));
        prgNetwork = new JProgressBar(0, 100);
        prgNetwork.setStringPainted(true);
        bottomPanel.add(prgNetwork, BorderLayout.NORTH);

        txtHexDisplay = new JTextArea(12, 80);
        txtHexDisplay.setEditable(false);
        txtHexDisplay.setBackground(new Color(10, 12, 16));
        txtHexDisplay.setForeground(new Color(50, 255, 120));
        txtHexDisplay.setFont(new Font("Consolas", Font.PLAIN, 11));
        bottomPanel.add(new JScrollPane(txtHexDisplay), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton resetButton = new JButton("🔄 重置会话");
        resetButton.addActionListener(e -> resetTcpSession());
        JButton clearArpButton = new JButton("🗑️ 清空 ARP 缓存");
        clearArpButton.addActionListener(e -> { arpCache.clear(); updateArpAndNatDisplay(); });
        btnPanel.add(resetButton);
        btnPanel.add(clearArpButton);
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

        updateTopLabel();
        new Timer(30, e -> gameTick()).start();
        new Timer(1000, e -> updateArpAndNatDisplay()).start();
    }

    private void initArpCache() {
        arpCache.put("192.168.1.1", new ArpEntry("192.168.1.1", "00:1A:2B:3C:4D:5E"));
        arpCache.put("192.168.1.2", new ArpEntry("192.168.1.2", "00:1A:2B:3C:4D:5F"));
        arpCache.put("10.0.0.1", new ArpEntry("10.0.0.1", "00:1A:2B:3C:4D:60"));
        arpCache.put("8.8.8.8", new ArpEntry("8.8.8.8", "00:1A:2B:3C:4D:61"));
    }

    private void updateArpAndNatDisplay() {
        StringBuilder arpSb = new StringBuilder();
        for (ArpEntry entry : arpCache.values()) {
            arpSb.append(String.format("%s → %s\n", entry.ipAddress, entry.macAddress));
        }
        txtArpDisplay.setText(arpSb.toString());

        StringBuilder natSb = new StringBuilder();
        for (NatEntry entry : natTable) {
            natSb.append(String.format("%s:%d → %s:%d\n",
                    entry.privateIp, entry.privatePort, entry.publicIp, entry.publicPort));
        }
        txtNatDisplay.setText(natSb.toString());
    }

    private void buildShopUI() {
        ButtonGroup group = new ButtonGroup();

        JButton btnUpgradeServer = new JButton("🚀 超频接收端 CPU (加快解包) $400");
        btnUpgradeServer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUpgradeServer.addActionListener(e -> {
            if (funds >= PRICE_UPGRADE_SERVER && serverDecodeDelay > 200) {
                funds -= PRICE_UPGRADE_SERVER;
                serverDecodeDelay = Math.max(200, serverDecodeDelay - 300);
                txtHexDisplay.setText("【⚡ 硬件升级】: 服务器超频成功！解包延迟降至 " + serverDecodeDelay + "ms");
                updateTopLabel();
            }
        });
        shopPanel.add(Box.createVerticalStrut(10));
        shopPanel.add(btnUpgradeServer);
        shopPanel.add(Box.createVerticalStrut(15));

        String[][] categories = {
                {"【1. 内网采矿与原始数据】", "MINER_H", "🔷 Hello 采矿机", "MINER_S", "🟩 Say 采矿机"},
                {"【2. 应用层】", "TX_APP", "📦 应用数据载荷"},
                {"【3. 传输层 - TCP 封装】", "T_SP", "🔩 源端口", "T_DP", "🎯 目的端口", "T_SEQ", "🔢 序列号",
                        "T_ACK", "📜 确认号", "T_CTL", "🚩 控制位(SYN/ACK/FIN)", "T_WIN", "🌊 滑动窗口",
                        "T_CHK", "🔥 校验和", "T_CORE", "🟧 TCP 段总装"},
                {"【4. 网络层 - IP 封装与分片】", "TX_IPH", "📦 IP 首部", "TX_IP_FRAG", "✂️ IP 分片器",
                        "TX_IP_REASS", "🔧 IP 重组器"},
                {"【5. 链路层】", "TX_ARP", "🔍 ARP 解析", "TX_LLC", "🟩 LLC 封装", "TX_FCS", "🟩 FCS 校验"},
                {"【6. 边界网关】", "R_LAN", "🎛️ LAN 拆包", "R_TAB", "🔀 路由查表", "R_NAT", "🌍 NAT 转换", "R_WAN", "🛠️ WAN 封装"},
                {"【7. 接收端解封装】", "RX_LLC", "🔓 链路层解封", "RX_IP", "💛 网络层解封", "RX_TCP", "🧡 传输层解封", "RX_APP", "💚 应用层交付"}
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
                if (c == 15 || c == 28) mapLayout[r][c] = 9;
            }
        }

        buildingLayout[MAP_ROWS/2][3] = "PC_FACTORY";
        pcFactory = new Point(3 * TILE_SIZE + TILE_SIZE/2, (MAP_ROWS/2) * TILE_SIZE + TILE_SIZE/2);
        buildingLayout[MAP_ROWS/2][MAP_COLS-3] = "RX_ST";

        buildingLayout[2][1] = "MINER_H"; buildingLayout[3][1] = "MINER_H";
        buildingLayout[12][1] = "MINER_S"; buildingLayout[13][1] = "MINER_S";

        int startRow = MAP_ROWS / 2 - 3;

        buildingLayout[startRow][4] = "TX_APP";
        buildingLayout[startRow][5] = "T_SP";
        buildingLayout[startRow][6] = "T_DP";
        buildingLayout[startRow][7] = "T_SEQ";
        buildingLayout[startRow][8] = "T_ACK";
        buildingLayout[startRow][9] = "T_CTL";
        buildingLayout[startRow][10] = "T_WIN";
        buildingLayout[startRow][11] = "T_CHK";
        buildingLayout[startRow][12] = "T_CORE";
        buildingLayout[startRow][13] = "TX_IPH";
        buildingLayout[startRow][14] = "TX_IP_FRAG";
        buildingLayout[startRow][16] = "TX_ARP";
        buildingLayout[startRow][17] = "TX_LLC";
        buildingLayout[startRow][18] = "TX_FCS";

        int gatewayRow = MAP_ROWS / 2;
        buildingLayout[gatewayRow][14] = "R_LAN";
        buildingLayout[gatewayRow][16] = "R_TAB";
        buildingLayout[gatewayRow][18] = "R_NAT";
        buildingLayout[gatewayRow][20] = "R_WAN";

        int receiveRow = MAP_ROWS / 2 - 2;
        buildingLayout[receiveRow][30] = "RX_LLC";
        buildingLayout[receiveRow][32] = "RX_IP";
        buildingLayout[receiveRow][33] = "TX_IP_REASS";
        buildingLayout[receiveRow][34] = "RX_TCP";
        buildingLayout[receiveRow][36] = "RX_APP";
    }

    private void autoTriggerHandshake() {
        currentTcpState = TcpState.SYN_SENT;
        stateTimerWatchdog = System.currentTimeMillis();

        performArpResolution("192.168.1.100");

        DataCart syn = new DataCart(pcFactory.x, pcFactory.y, "SYN", 0);
        syn.sequenceNumber = 100;
        pendingDataCarts.add(syn);
        txtHexDisplay.setText("【🤝 三次握手开始】: 发送 SYN (seq=100)，同时触发 ARP 解析目标 MAC");
        updateTopLabel();
    }

    private void performArpResolution(String targetIp) {
        if (!arpCache.containsKey(targetIp)) {
            txtHexDisplay.append("\n【🔍 ARP 请求】: 谁拥有 " + targetIp + "？广播发送 ARP 请求");
            String mac = String.format("00:1A:2B:%02X:%02X:%02X",
                    new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
            arpCache.put(targetIp, new ArpEntry(targetIp, mac));
            txtHexDisplay.append("\n【📥 ARP 响应】: " + targetIp + " 的 MAC 地址是 " + mac);
            updateArpAndNatDisplay();
        } else {
            txtHexDisplay.append("\n【✅ ARP 缓存命中】: " + targetIp + " → " + arpCache.get(targetIp).macAddress);
        }
    }

    private long lastResourceTick = 0;

    private void gameTick() {
        long now = System.currentTimeMillis();

        if (currentTcpState != TcpState.CLOSED && currentTcpState != TcpState.ESTABLISHED) {
            if (now - stateTimerWatchdog > 20000) {
                txtHexDisplay.setText("【⏰ 超时】: 连接超时，重置会话");
                resetTcpSession();
            }
        }

        if (serverBufferCount > 0 && (now - lastServerConsumeTime >= serverDecodeDelay)) {
            serverBufferCount--;
            serverReceivedCount++;
            lastServerConsumeTime = now;
            rwnd = SERVER_BUFFER_MAX - serverBufferCount;
            updateTopLabel();
        }

        if (currentTcpState == TcpState.ESTABLISHED) {
            List<RetransmissionTask> toRemoveTimers = new ArrayList<>();
            for (RetransmissionTask task : activeTimers) {
                if (task.isAcked) toRemoveTimers.add(task);
            }
            activeTimers.removeAll(toRemoveTimers);

            for (RetransmissionTask task : activeTimers) {
                if (!task.isAcked && (now - task.sendTime > RTO_TIMEOUT)) {
                    task.retryCount++;
                    if (task.retryCount >= 5) {
                        txtHexDisplay.setText(String.format("【💀 连接崩溃】: SEQ=%d 重传失败", task.seqNum));
                        resetTcpSession();
                        return;
                    }
                    task.sendTime = now;
                    ssthresh = Math.max(2, cwnd / 2);
                    cwnd = 1;
                    packetsAckedSinceLastIncrease = 0;
                    pendingDataCarts.add(new DataCart(pcFactory.x, pcFactory.y, "DATA", task.seqNum));
                    inFlightCount++;
                    txtHexDisplay.setText(String.format("【⚠️ 超时重传】: SEQ=%d, ssthresh=%d, cwnd=1", task.seqNum, ssthresh));
                    updateTopLabel();
                }
            }
        }

        int currentCartsInWan = 0;
        for (DataCart c : dataCarts) {
            int col = (int)(c.x / TILE_SIZE);
            if (col >= 16 && col <= 28 && !c.isReturnTrip) currentCartsInWan++;
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

            if (currentTcpState == TcpState.CLOSED) {
                if (helloStock >= 2 && sayStock >= 1) autoTriggerHandshake();
            } else if (currentTcpState == TcpState.ESTABLISHED) {
                int effectiveWindow = Math.min(cwnd, rwnd);
                while (effectiveWindow > 0 && inFlightCount < effectiveWindow
                        && helloStock >= 2 && sayStock >= 1
                        && serverReceivedCount + serverBufferCount < totalDataToTransmit) {
                    helloStock -= 2; sayStock -= 1; inFlightCount++;
                    int currentSeq = nextSeqNum++;
                    activeTimers.add(new RetransmissionTask(currentSeq, now));
                    pendingDataCarts.add(new DataCart(pcFactory.x, pcFactory.y, "DATA", currentSeq));
                }
                if (serverReceivedCount >= totalDataToTransmit && inFlightCount == 0
                        && activeTimers.isEmpty() && serverBufferCount == 0) {
                    currentTcpState = TcpState.FIN_WAIT_1;
                    stateTimerWatchdog = now;
                    pendingDataCarts.add(new DataCart(pcFactory.x, pcFactory.y, "FIN_PC", 0));
                    txtHexDisplay.setText("【🏁 数据传输完成】: 发送 FIN，开始四次挥手");
                }
            }
            lastResourceTick = now;
            updateTopLabel();
        }

        if (currentTcpState == TcpState.ESTABLISHED && Math.min(cwnd, rwnd) == 0 && rwnd == 0) {
            if (now - lastProbeTime >= PROBE_INTERVAL) {
                lastProbeTime = now;
                pendingDataCarts.add(new DataCart(pcFactory.x, pcFactory.y, "ZWP", 0));
                txtHexDisplay.setText("【🔍 零窗口探测】: 发送 ZWP 探测包");
            }
        }

        oreCarts.removeIf(c -> {
            c.update();
            if (c.isArrived) {
                if (c.oreType.equals("HELLO")) helloStock++; else sayStock++;
                return true;
            }
            return false;
        });

        List<DataCart> toRemove = new ArrayList<>();
        for (DataCart cart : dataCarts) {
            int col = (int)(cart.x / TILE_SIZE);
            if (col >= 16 && col <= 28 && !cart.isReturnTrip) {
                if (currentCartsInWan > WAN_BOTTLE_NECK_MAX && !isForemostCartInWan(cart)) {
                    cart.waitInQueueTimer++;
                    if (cart.waitInQueueTimer > 120) cart.isDropped = true;
                    continue;
                }
            }
            cart.update();
            if (cart.isDropped) {
                if (cart.cartType.equals("DATA")) inFlightCount = Math.max(0, inFlightCount - 1);
                else resetTcpSession();
                toRemove.add(cart);
                updateTopLabel();
            } else if (cart.isArrived) {
                handleCartArrival(cart);
                toRemove.add(cart);
                updateTopLabel();
            }
        }
        dataCarts.removeAll(toRemove);

        if (!pendingDataCarts.isEmpty()) {
            dataCarts.addAll(pendingDataCarts);
            pendingDataCarts.clear();
        }

        arpCache.entrySet().removeIf(entry -> now - entry.getValue().lastSeen > 300000);
        natTable.removeIf(entry -> now - entry.created > 30000);
        fragmentBuffer.entrySet().removeIf(entry ->
                now - entry.getValue().get(0).receivedTime > 10000);

        prgNetwork.setValue((int)(((double)serverReceivedCount / totalDataToTransmit) * 100));
        canvas.repaint();
    }

    private boolean isForemostCartInWan(DataCart target) {
        double maxProgressX = 0;
        DataCart foremost = null;
        for (DataCart c : dataCarts) {
            int col = (int)(c.x / TILE_SIZE);
            if (col >= 16 && col <= 28 && !c.isReturnTrip) {
                if (c.x > maxProgressX) { maxProgressX = c.x; foremost = c; }
            }
        }
        return foremost == null || foremost == target;
    }

    private List<DataCart> fragmentPacket(DataCart original) {
        List<DataCart> fragments = new ArrayList<>();
        int mtu = 3;
        int payloadSize = original.hasPayload ? 8 : 0;
        int numFragments = (int)Math.ceil((double)payloadSize / mtu);

        for (int i = 0; i < numFragments; i++) {
            DataCart frag = new DataCart(original.x, original.y, original.cartType, original.sequenceNumber);
            frag.isFragment = true;
            frag.fragmentId = nextPacketId++;
            frag.fragmentOffset = i * mtu;
            frag.isLastFragment = (i == numFragments - 1);
            frag.hasPayload = true;
            fragments.add(frag);
        }
        return fragments;
    }

    private boolean reassemblePacket(int packetId, DataCart fragment) {
        if (!fragmentBuffer.containsKey(packetId)) {
            fragmentBuffer.put(packetId, new CopyOnWriteArrayList<>());
        }

        if (fragment.isLastFragment) {
            txtHexDisplay.append(String.format("\n【🔧 IP 重组】: 数据包 ID=%d 所有分片已收齐，开始重组", packetId));
            fragmentBuffer.remove(packetId);
            return true;
        }
        return false;
    }

    private void performNatTranslation(DataCart cart, boolean inbound) {
        if (inbound) {
            for (NatEntry entry : natTable) {
                if (entry.publicPort == cart.natPort) {
                    cart.destIp = entry.privateIp;
                    cart.destPort = entry.privatePort;
                    txtHexDisplay.append(String.format("\n【🌍 NAT 入向转换】: %s:%d → %s:%d",
                            entry.publicIp, entry.publicPort, entry.privateIp, entry.privatePort));
                    return;
                }
            }
        } else {
            String privateIp = "192.168.1.100";
            int privatePort = cart.srcPort;
            String publicIp = "203.0.113.1";
            int publicPort = nextPublicPort++;

            NatEntry entry = new NatEntry(privateIp, privatePort, publicIp, publicPort);
            natTable.add(entry);
            cart.srcIp = publicIp;
            cart.srcPort = publicPort;
            cart.hasNat = true;
            txtHexDisplay.append(String.format("\n【🌍 NAT 出向转换】: %s:%d → %s:%d",
                    privateIp, privatePort, publicIp, publicPort));
            updateArpAndNatDisplay();
        }
    }

    private void handleCartArrival(DataCart cart) {
        Point serverPos = findBuildingCoords("RX_ST");
        long now = System.currentTimeMillis();

        if (!cart.isReturnTrip) {
            switch(cart.cartType) {
                case "SYN":
                    performArpResolution("192.168.1.100");
                    DataCart synAck = new DataCart(serverPos.x, serverPos.y, "SYN_ACK", 0);
                    synAck.isReturnTrip = true;
                    synAck.ackNumber = cart.sequenceNumber + 1;
                    synAck.sequenceNumber = 200;
                    pendingDataCarts.add(synAck);
                    txtHexDisplay.setText("【🤝 三次握手】: 收到 SYN，回复 SYN-ACK (seq=200, ack=" + (cart.sequenceNumber+1) + ")");
                    stateTimerWatchdog = now;
                    break;

                case "ACK_PC":
                    if (currentTcpState == TcpState.SYN_SENT) {
                        currentTcpState = TcpState.ESTABLISHED;
                        cwnd = 1; ssthresh = 8;
                        packetsAckedSinceLastIncrease = 0;
                        txtHexDisplay.setText("【🤝 三次握手完成】: 收到 ACK，连接建立！cwnd=1, ssthresh=8");
                    }
                    break;

                case "DATA":
                    if (!cart.isFragment && cart.hasPayload && cart.payloadSize > 3) {
                        List<DataCart> fragments = fragmentPacket(cart);
                        for (DataCart frag : fragments) {
                            pendingDataCarts.add(frag);
                        }
                        txtHexDisplay.append(String.format("\n【✂️ IP 分片】: 数据包 SEQ=%d 被分片为 %d 个片段",
                                cart.sequenceNumber, fragments.size()));
                        return;
                    }

                    if (cart.isFragment) {
                        if (reassemblePacket(cart.fragmentId, cart)) {
                        } else {
                            return;
                        }
                    }

                    performNatTranslation(cart, false);

                    if (serverBufferCount < SERVER_BUFFER_MAX) {
                        serverBufferCount++;
                        if (serverBufferCount == 1) lastServerConsumeTime = now;
                        rwnd = SERVER_BUFFER_MAX - serverBufferCount;

                        DataCart dataAck = new DataCart(serverPos.x, serverPos.y, "DATA_ACK", cart.sequenceNumber);
                        dataAck.advertisedWindow = rwnd;
                        dataAck.isReturnTrip = true;
                        pendingDataCarts.add(dataAck);
                        funds += 500;
                        txtHexDisplay.append(String.format("\n【📦 数据交付】: SEQ=%d 已接收，回复 ACK (rwnd=%d)",
                                cart.sequenceNumber, rwnd));
                    } else {
                        txtHexDisplay.append(String.format("\n【💥 缓冲区溢出】: SEQ=%d 丢失", cart.sequenceNumber));
                    }
                    break;

                case "ZWP":
                    DataCart probeAck = new DataCart(serverPos.x, serverPos.y, "DATA_ACK", 0);
                    probeAck.advertisedWindow = SERVER_BUFFER_MAX - serverBufferCount;
                    probeAck.isReturnTrip = true;
                    pendingDataCarts.add(probeAck);
                    txtHexDisplay.setText("【🔍 零窗口探测响应】: 当前 rwnd=" + (SERVER_BUFFER_MAX - serverBufferCount));
                    break;

                case "FIN_PC":
                    DataCart finAck = new DataCart(serverPos.x, serverPos.y, "FIN_ACK_SRV", 0);
                    finAck.isReturnTrip = true;
                    pendingDataCarts.add(finAck);
                    DataCart srvFin = new DataCart(serverPos.x, serverPos.y, "FIN_SRV", 0);
                    srvFin.isReturnTrip = true;
                    pendingDataCarts.add(srvFin);
                    txtHexDisplay.setText("【👋 四次挥手】: 收到 FIN，回复 FIN-ACK，发送 FIN");
                    stateTimerWatchdog = now;
                    break;
            }
        } else {
            switch(cart.cartType) {
                case "SYN_ACK":
                    DataCart finalAck = new DataCart(pcFactory.x, pcFactory.y, "ACK_PC", 0);
                    finalAck.ackNumber = cart.sequenceNumber + 1;
                    pendingDataCarts.add(finalAck);
                    txtHexDisplay.setText("【🤝 三次握手】: 收到 SYN-ACK，回复 ACK (ack=" + (cart.sequenceNumber+1) + ")");
                    stateTimerWatchdog = now;
                    break;

                case "DATA_ACK":
                    this.rwnd = cart.advertisedWindow;
                    if (cart.sequenceNumber > 0) {
                        if (cwnd < ssthresh) {
                            cwnd++;
                            txtHexDisplay.setText(String.format("【📈 慢启动】: cwnd=%d, ssthresh=%d", cwnd, ssthresh));
                        } else {
                            packetsAckedSinceLastIncrease++;
                            if (packetsAckedSinceLastIncrease >= cwnd) {
                                cwnd++;
                                packetsAckedSinceLastIncrease = 0;
                                txtHexDisplay.setText(String.format("【🐌 拥塞避免】: cwnd=%d", cwnd));
                            }
                        }
                    }
                    for (RetransmissionTask task : activeTimers) {
                        if (task.seqNum == cart.sequenceNumber) {
                            task.isAcked = true;
                            break;
                        }
                    }
                    if (cart.sequenceNumber > 0) inFlightCount = Math.max(0, inFlightCount - 1);
                    break;

                case "FIN_ACK_SRV":
                    if (currentTcpState == TcpState.FIN_WAIT_1) {
                        currentTcpState = TcpState.FIN_WAIT_2;
                        txtHexDisplay.setText("【👋 四次挥手】: 收到 FIN-ACK，进入 FIN-WAIT-2");
                        stateTimerWatchdog = now;
                    }
                    break;

                case "FIN_SRV":
                    currentTcpState = TcpState.TIME_WAIT;
                    DataCart lastAck = new DataCart(pcFactory.x, pcFactory.y, "LAST_ACK_PC", 0);
                    pendingDataCarts.add(lastAck);
                    txtHexDisplay.setText("【👋 四次挥手】: 收到 FIN，回复 ACK，进入 TIME-WAIT");
                    Timer timer = new Timer(1500, e -> {
                        if (currentTcpState == TcpState.TIME_WAIT) {
                            resetTcpSession();
                            JOptionPane.showMessageDialog(this,
                                    "🎉 数据传输完成！\n\n完整过程演示：\n" +
                                            "1. ARP 解析 MAC 地址\n" +
                                            "2. TCP 三次握手建立连接\n" +
                                            "3. 滑动窗口流量控制\n" +
                                            "4. 拥塞控制（慢启动+拥塞避免）\n" +
                                            "5. IP 分片与重组\n" +
                                            "6. NAT 地址转换\n" +
                                            "7. TCP 四次挥手释放连接",
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
        serverReceivedCount = 0; inFlightCount = 0; serverBufferCount = 0;
        cwnd = 1; ssthresh = 8; rwnd = 3;
        packetsAckedSinceLastIncrease = 0;
        nextSeqNum = 100;
        dataCarts.clear(); pendingDataCarts.clear(); activeTimers.clear();
        updateTopLabel();
        canvas.repaint();
    }

    private void updateTopLabel() {
        int effectiveWin = Math.min(cwnd, rwnd);
        lblDashboard.setText(String.format(
                "💰 资金:%d | 🏷️ 状态:%s | 🚩 cwnd:%d | 🎯 ssthresh:%d | 📥 rwnd:%d | 🎛️ 有效窗口:%d | " +
                        "📦 仓储:%d/%d | ✅ 达成:%d/%d | 🔍 ARP缓存:%d | 🌍 NAT表:%d",
                funds, currentTcpState, cwnd, ssthresh, rwnd, effectiveWin,
                serverBufferCount, SERVER_BUFFER_MAX, serverReceivedCount, totalDataToTransmit,
                arpCache.size(), natTable.size()));
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

    private class OreCart {
        double x, y; double speed = 5.0; String oreType; boolean isArrived = false;
        public OreCart(double x, double y, String type) { this.x = x; this.y = y; this.oreType = type; }
        public void update() {
            double dx = pcFactory.x - x; double dy = pcFactory.y - y;
            double dist = Math.sqrt(dx*dx+dy*dy);
            if (dist <= speed) isArrived = true;
            else { x += (dx/dist)*speed; y += (dy/dist)*speed; }
        }
    }

    private class DataCart {
        double x, y; double speed = 9.0; int stage; int timer = 0;
        boolean isArrived = false; boolean isDropped = false; boolean isReturnTrip = false;
        String cartType; String currentLayerStatus = "";
        int sequenceNumber = 0;
        int ackNumber = 0;
        int advertisedWindow = 3;
        int waitInQueueTimer = 0;

        // IP 分片相关
        boolean isFragment = false;
        int fragmentId = 0;
        int fragmentOffset = 0;
        boolean isLastFragment = false;
        boolean hasPayload = false;
        int payloadSize = 8;

        // NAT 相关
        boolean hasNat = false;
        String srcIp = "192.168.1.100";
        int srcPort = 12345;
        String destIp = "10.0.0.1";
        int destPort = 80;
        int natPort = 0;

        // 协议栈层级标记
        boolean hasApp = false, hasTcp = false, hasIp = false, hasLlc = false, hasFcs = false;
        boolean c_Payload=false, c_SP=false, c_DP=false, c_SEQ=false, c_ACK=false, c_CTL=false, c_WIN=false, c_CHK=false;
        boolean isRouterTranslated = false;

        public DataCart(double sx, double sy, String type, int seq) {
            this.x = sx; this.y = sy; this.cartType = type; this.sequenceNumber = seq;
            if (isControlFrame(type)) { this.stage = 2; this.c_Payload = false; }
            else { this.stage = 1; this.hasPayload = true; }
        }

        public boolean isControlFrame(String type) {
            return type.equals("SYN") || type.equals("SYN_ACK") || type.equals("ACK_PC")
                    || type.equals("FIN_PC") || type.equals("FIN_ACK_SRV") || type.equals("FIN_SRV")
                    || type.equals("DATA_ACK") || type.equals("LAST_ACK_PC") || type.equals("ZWP");
        }

        public void update() {
            if (timer > 0) { timer--; return; }
            Point target = isReturnTrip ? pcFactory : findBuildingCoords("RX_ST");
            if (target == null) target = pcFactory;

            if (!isReturnTrip) {
                Point machine = findTargetMachine(stage);
                if (machine != null) target = machine;
                else { isDropped = true; return; }
            }

            double dx = target.x - x; double dy = target.y - y;
            double dist = Math.sqrt(dx*dx+dy*dy);
            if (dist <= speed) {
                x = target.x; y = target.y;
                if (!isReturnTrip) {
                    processStageCraft();
                    if (stage < 23) { timer = 2; stage++; }
                    else { isArrived = true; }
                } else { isArrived = true; }
            } else { x += (dx/dist)*speed; y += (dy/dist)*speed; }
        }

        private Point findTargetMachine(int s) {
            String tag = "NONE";
            switch(s) {
                case 1: tag = "TX_APP"; break;
                case 2: tag = "T_SP"; break; case 3: tag = "T_DP"; break;
                case 4: tag = "T_SEQ"; break; case 5: tag = "T_ACK"; break;
                case 6: tag = "T_CTL"; break; case 7: tag = "T_WIN"; break;
                case 8: tag = "T_CHK"; break; case 9: tag = "T_CORE"; break;
                case 10: tag = "TX_IPH"; break; case 11: tag = "TX_IP_FRAG"; break;
                case 12: tag = "TX_ARP"; break; case 13: tag = "TX_LLC"; break;
                case 14: tag = "TX_FCS"; break;
                case 15: tag = "R_LAN"; break; case 16: tag = "R_TAB"; break;
                case 17: tag = "R_NAT"; break; case 18: tag = "R_WAN"; break;
                case 19: tag = "RX_LLC"; break; case 20: tag = "RX_IP"; break;
                case 21: tag = "TX_IP_REASS"; break; case 22: tag = "RX_TCP"; break;
                case 23: tag = "RX_APP"; break;
            }
            return findBuildingCoords(tag);
        }

        private void processStageCraft() {
            switch(stage) {
                case 1: hasApp = true; currentLayerStatus = "💚 应用数据"; break;
                case 2: c_SP = true; currentLayerStatus = "⚙️ 源端口:" + srcPort; break;
                case 3: c_DP = true; currentLayerStatus = "🎯 目的端口:" + destPort; break;
                case 4: c_SEQ = true; currentLayerStatus = "🔢 SEQ:" + sequenceNumber; break;
                case 5: c_ACK = true; currentLayerStatus = "📜 ACK:" + ackNumber; break;
                case 6: c_CTL = true; currentLayerStatus = "🚩 [" + cartType + "]"; break;
                case 7: c_WIN = true; currentLayerStatus = "🌊 WIN:" + advertisedWindow; break;
                case 8: c_CHK = true; currentLayerStatus = "🔥 校验和"; break;
                case 9: hasTcp = true; currentLayerStatus = "🧡 TCP 段"; break;
                case 10: hasIp = true; currentLayerStatus = "💛 IP 首部"; break;
                case 11: if (hasPayload && payloadSize > 3) {
                    currentLayerStatus = "✂️ IP 分片 (offset=" + fragmentOffset + ")";
                } else { currentLayerStatus = "📦 IP 数据报"; }
                    break;
                case 12: currentLayerStatus = "🔍 ARP 解析"; break;
                case 13: hasLlc = true; currentLayerStatus = "🟩 LLC"; break;
                case 14: hasFcs = true; currentLayerStatus = "🟩 FCS"; break;
                case 15: hasLlc = false; hasFcs = false; currentLayerStatus = "🎛️ LAN 解包"; break;
                case 16: currentLayerStatus = "🔀 路由查表"; break;
                case 17: hasNat = true; currentLayerStatus = "🌍 NAT 转换"; break;
                case 18: hasLlc = true; hasFcs = true; currentLayerStatus = "🛠️ WAN 封装"; break;
                case 19: hasLlc = false; hasFcs = false; currentLayerStatus = "🔓 剥链路层"; break;
                case 20: if (isFragment) {
                    currentLayerStatus = "🔧 IP 重组中...";
                } else { hasIp = false; currentLayerStatus = "💛 剥网络层"; }
                    break;
                case 21: if (isFragment) {
                    currentLayerStatus = "✅ IP 重组完成";
                } else { currentLayerStatus = "待处理"; }
                    break;
                case 22: hasTcp = false; currentLayerStatus = "🧡 剥传输层"; break;
                case 23: hasApp = false; currentLayerStatus = "💚 应用交付"; break;
            }
        }
    }

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
            g2.fillRect(16 * TILE_SIZE, 0, (28 - 16 + 1) * TILE_SIZE, MAP_ROWS * TILE_SIZE);

            int wanCarCount = 0;
            for (DataCart c : dataCarts) {
                int col = (int)(c.x / TILE_SIZE);
                if (col >= 16 && col <= 28 && !c.isReturnTrip) wanCarCount++;
            }
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("微软雅黑", Font.BOLD, 14));
            g2.drawString("🚦 公网车辆: " + wanCarCount + "/" + WAN_BOTTLE_NECK_MAX, 17 * TILE_SIZE, 30);

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
                    } else if (tag.startsWith("T_") || tag.startsWith("TX_") || tag.startsWith("R_") || tag.startsWith("RX_")) {
                        if (tag.contains("NAT")) g2.setColor(new Color(255, 165, 0));
                        else if (tag.contains("ARP")) g2.setColor(new Color(0, 255, 255));
                        else if (tag.contains("FRAG") || tag.contains("REASS")) g2.setColor(new Color(255, 105, 180));
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
                else if (cart.cartType.equals("ZWP")) g2.setColor(Color.YELLOW);
                else if (cart.cartType.startsWith("SYN")) g2.setColor(Color.CYAN);
                else if (cart.cartType.startsWith("FIN")) g2.setColor(Color.PINK);
                else if (cart.cartType.contains("ACK")) g2.setColor(Color.GREEN);
                else if (cart.isFragment) g2.setColor(new Color(255, 105, 180));
                else g2.setColor(Color.LIGHT_GRAY);
                g2.fillOval(cx-7, cy-7, 14, 14);

                int bx = cx - 25;
                if (cart.hasApp) { g2.setColor(new Color(100, 255, 100)); g2.fillRect(bx, cy-5, 6, 10); bx += 6; }
                if (cart.hasTcp) { g2.setColor(Color.ORANGE); g2.fillRect(bx, cy-5, 7, 10); bx += 7; }
                if (cart.hasIp) { g2.setColor(Color.YELLOW); g2.fillRect(bx, cy-5, 6, 10); bx += 6; }
                if (cart.hasLlc) { g2.setColor(Color.GREEN); g2.fillRect(bx, cy-5, 6, 10); bx += 6; }
                if (cart.hasFcs) { g2.setColor(Color.GREEN.darker()); g2.fillRect(bx, cy-5, 6, 10); bx += 6; }
                if (cart.hasNat) { g2.setColor(new Color(255, 165, 0)); g2.fillRect(bx, cy-5, 6, 10); }

                g2.setFont(new Font("微软雅黑", Font.BOLD, 10));
                String direction = cart.isReturnTrip ? "◀ 回传" : (cart.waitInQueueTimer > 0 ? "⚠️ 排队" : "▶ 发送");
                String nameTag = cart.cartType.equals("DATA") ? "DATA-" + cart.sequenceNumber : cart.cartType;
                if (cart.isFragment) nameTag += "(分片)";
                String label = String.format("%s %s", nameTag, direction);

                g2.setColor(new Color(0, 0, 0, 180));
                g2.fillRect(cx - 35, cy - 25, g2.getFontMetrics().stringWidth(label) + 6, 14);
                g2.setColor(cart.waitInQueueTimer > 0 ? Color.RED : Color.YELLOW);
                g2.drawString(label, cx - 33, cy - 14);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DataCartFactoryGame().setVisible(true));
    }
}