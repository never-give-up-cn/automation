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

    // --- 内部类：追踪每个在途数据包的重传定时器 ---
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

    // 🔥 流量控制与拥塞控制核心变量
    private int rwnd = 3;
    private int cwnd = 1;
    private int ssthresh = 8;

    private int serverBufferCount = 0;
    private final int SERVER_BUFFER_MAX = 5;
    private long lastServerConsumeTime = 0;
    private int serverDecodeDelay = 1200;

    // 瓶颈物理参数
    private final int WAN_BOTTLE_NECK_MAX = 2;

    private int inFlightCount = 0;
    private long stateTimerWatchdog = 0;
    private final long RTO_TIMEOUT = 4000;
    private int nextSeqNum = 100;

    // 零窗口探测
    private long lastProbeTime = 0;
    private final long PROBE_INTERVAL = 3000;

    private List<RetransmissionTask> activeTimers = new CopyOnWriteArrayList<>();

    private String selectedBuilding = "NONE";
    private final int PRICE_MINER = 30;
    private final int PRICE_MACHINE = 20;
    private final int PRICE_UPGRADE_SERVER = 400;

    private final int TILE_SIZE = 40;
    private final int MAP_COLS = 38;
    private final int MAP_ROWS = 16;

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

    private int packetsAckedSinceLastIncrease = 0;

    public DataCartFactoryGame() {
        setTitle("异星数据工厂 v5.3 终极 TCP 拥塞控制(慢启动/拥塞避免)与公网物理排队版");
        setSize(1680, 950);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initMap();

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createTitledBorder("📊 智能雷达全自动调度台 (搭载 拥塞控制算法 cwnd & ssthresh 监测)"));
        lblDashboard = new JLabel("", JLabel.CENTER);
        lblDashboard.setFont(new Font("微软雅黑", Font.BOLD, 15));
        topPanel.add(lblDashboard, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        shopPanel = new JPanel();
        shopPanel.setLayout(new BoxLayout(shopPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(shopPanel);
        scrollPane.setPreferredSize(new Dimension(340, 0));
        add(scrollPane, BorderLayout.WEST);
        buildShopUI();

        canvas = new GameCanvas();
        add(canvas, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setBorder(BorderFactory.createTitledBorder("📟 Wireshark 协议栈分析仪 - 拥塞状态时序跟踪控制台"));
        prgNetwork = new JProgressBar(0, 100);
        prgNetwork.setStringPainted(true);
        bottomPanel.add(prgNetwork, BorderLayout.NORTH);

        txtHexDisplay = new JTextArea(11, 80);
        txtHexDisplay.setEditable(false);
        txtHexDisplay.setBackground(new Color(10, 12, 16));
        txtHexDisplay.setForeground(new Color(50, 255, 120));
        txtHexDisplay.setFont(new Font("Consolas", Font.PLAIN, 12));
        bottomPanel.add(new JScrollPane(txtHexDisplay), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        JButton resetButton = new JButton("🔄 重置会话");
        resetButton.addActionListener(e -> resetTcpSession());
        bottomPanel.add(resetButton, BorderLayout.SOUTH);

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
    }

    private void buildShopUI() {
        ButtonGroup group = new ButtonGroup();

        JButton btnUpgradeServer = new JButton("🚀 超频接收端 CPU (加快解包速度) 花费 $400");
        btnUpgradeServer.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUpgradeServer.addActionListener(e -> {
            if (funds >= PRICE_UPGRADE_SERVER && serverDecodeDelay > 200) {
                funds -= PRICE_UPGRADE_SERVER;
                serverDecodeDelay = Math.max(200, serverDecodeDelay - 300);
                txtHexDisplay.setText("【⚡ 硬件升级】: 服务器超频成功！应用层处理延迟降至 " + serverDecodeDelay + "ms！");
                updateTopLabel();
            }
        });
        shopPanel.add(Box.createVerticalStrut(10));
        shopPanel.add(btnUpgradeServer);
        shopPanel.add(Box.createVerticalStrut(15));

        String[][] categories = {
                {"【1. 内网采矿与原始数据区】", "MINER_H", "🔷 Hello 采矿机", "MINER_S", "🟩 Say 采矿机", "TX_DATA", "🟥 应用层：数据载荷车间"},
                {"【2. 传输层核心部件厂 (TX-TCP)】", "T_SP", "🔩 1. 源端口机床", "T_DP", "🎯 2. 目的端口机床", "T_SEQ", "🔢 3. 序列号刻印线", "T_ACK", "📜 4. 确认号确认线", "T_CTL", "🚩 5. 状态控制位闸", "T_WIN", "🌊 6. 滑动窗口阀", "T_CHK", "🔥 7. Checksum 校验炉", "T_CORE", "🟧 8. TCP 核心段总装厂"},
                {"【3. 网络层与链路层 (TX-IP/MAC)】", "TX_IPD", "🟨 9. IP 数据载荷线", "TX_IPH", "🟨 10. IP 首部壳注塑厂", "TX_LLC", "🟩 11. Link头：LLC 锁", "TX_FCS", "🟩 12. Link尾：FCS 校验翼"},
                {"【4. 边界网关核心车间】", "R_LAN", "🎛️ Step 2: LAN口拆包", "R_TAB", "🔀 Step 3: 路由寻路查表", "R_WAN", "🛠️ Step 4: WAN口新二层"},
                {"【5. 接收端解包裹车间 (RX)】", "RX_LLC", "🔓 Step A: 链路层剥离", "RX_IP", "💛 Step B: 网络层壳剥离", "RX_TCP", "🧡 Step C: 传输层拆解"}
        };

        for (String[] cat : categories) {
            JLabel title = new JLabel(cat[0]); title.setForeground(new Color(120, 30, 180));
            shopPanel.add(title);
            for (int i = 1; i < cat.length; i += 2) {
                final String tag = cat[i]; String name = cat[i+1];
                JRadioButton rad = new JRadioButton(name); group.add(rad);
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
                if (c == 13 || c == 24) mapLayout[r][c] = 9;
            }
        }

        buildingLayout[MAP_ROWS/2][3] = "PC_FACTORY";
        pcFactory = new Point(3 * TILE_SIZE + TILE_SIZE/2, (MAP_ROWS/2) * TILE_SIZE + TILE_SIZE/2);
        buildingLayout[MAP_ROWS/2][MAP_COLS-3] = "RX_ST";

        buildingLayout[2][1] = "MINER_H"; buildingLayout[3][1] = "MINER_H";
        buildingLayout[12][1] = "MINER_S"; buildingLayout[13][1] = "MINER_S";

        int topRow = MAP_ROWS / 2 - 2;
        buildingLayout[topRow][4] = "TX_DATA";
        buildingLayout[topRow][5] = "T_SP";
        buildingLayout[topRow][6] = "T_DP";
        buildingLayout[topRow][7] = "T_SEQ";
        buildingLayout[topRow][8] = "T_ACK";
        buildingLayout[topRow][9] = "T_CTL";
        buildingLayout[topRow][10] = "T_WIN";
        buildingLayout[topRow][11] = "T_CHK";
        buildingLayout[topRow][12] = "T_CORE";

        int botRow = MAP_ROWS / 2 + 2;
        buildingLayout[botRow][5] = "TX_IPD";
        buildingLayout[botRow][6] = "TX_IPH";
        buildingLayout[botRow][7] = "TX_LLC";
        buildingLayout[botRow][8] = "TX_FCS";

        int midRow = MAP_ROWS / 2;
        buildingLayout[midRow][12] = "R_LAN";
        buildingLayout[midRow][14] = "R_TAB";
        buildingLayout[midRow][16] = "R_WAN";

        buildingLayout[midRow][26] = "RX_LLC";
        buildingLayout[midRow][28] = "RX_IP";
        buildingLayout[midRow][30] = "RX_TCP";
    }

    private void autoTriggerHandshake() {
        currentTcpState = TcpState.SYN_SENT;
        stateTimerWatchdog = System.currentTimeMillis();
        pendingDataCarts.add(new DataCart(pcFactory.x, pcFactory.y, "SYN", 0));
        txtHexDisplay.setText("【🤖 智能雷达开局机制】: 原材料就绪，弹射 [SYN] 控制轻骑兵车建立握手连接...");
        updateTopLabel();
    }

    private long lastResourceTick = 0;

    private void gameTick() {
        long now = System.currentTimeMillis();

        if (currentTcpState != TcpState.CLOSED && currentTcpState != TcpState.ESTABLISHED) {
            if (now - stateTimerWatchdog > 20000) {
                txtHexDisplay.setText("【⏰ 守护线程】: 关键连接帧在网关外发生彻底断裂。重置拓扑会话。");
                resetTcpSession();
            }
        }

        // 服务器异步解包
        if (serverBufferCount > 0 && (now - lastServerConsumeTime >= serverDecodeDelay)) {
            serverBufferCount--;
            serverReceivedCount++;
            lastServerConsumeTime = now;
            rwnd = SERVER_BUFFER_MAX - serverBufferCount;
            updateTopLabel();
        }

        // 🔥 拥塞控制：ARQ 超时重传主控扫描（使用 removeIf 安全删除）
        if (currentTcpState == TcpState.ESTABLISHED) {
            // 先收集需要删除的已确认任务
            List<RetransmissionTask> toRemoveTimers = new ArrayList<>();
            for (RetransmissionTask task : activeTimers) {
                if (task.isAcked) {
                    toRemoveTimers.add(task);
                }
            }
            activeTimers.removeAll(toRemoveTimers);

            // 处理超时重传
            for (RetransmissionTask task : activeTimers) {
                if (!task.isAcked && (now - task.sendTime > RTO_TIMEOUT)) {
                    task.retryCount++;
                    if (task.retryCount >= 5) {
                        txtHexDisplay.setText(String.format("【💀 连接崩溃】: 数据包 SEQ=%d 重传 %d 次仍然失败，网络瘫痪！重置会话。", task.seqNum, task.retryCount));
                        resetTcpSession();
                        return;
                    }
                    task.sendTime = now;

                    ssthresh = Math.max(2, cwnd / 2);
                    cwnd = 1;
                    packetsAckedSinceLastIncrease = 0;

                    DataCart retransmitCart = new DataCart(pcFactory.x, pcFactory.y, "DATA", task.seqNum);
                    pendingDataCarts.add(retransmitCart);
                    inFlightCount++;

                    txtHexDisplay.setText(String.format("【💥 TCP 拥塞控制机制严重警报】:\n业务包 [SEQ: %d] 超时！\n惩罚：ssthresh=%d, cwnd=1", task.seqNum, ssthresh));
                    updateTopLabel();
                }
            }
        }

        // 统计当前公网物理带宽的占有量
        int currentCartsInWan = 0;
        for (DataCart c : dataCarts) {
            int currentCol = (int)(c.x / TILE_SIZE);
            if (currentCol >= 13 && currentCol <= 24 && !c.isReturnTrip) {
                currentCartsInWan++;
            }
        }

        // 1. 生产线弹射控制
        if (now - lastResourceTick >= 1000) {
            for (int r = 0; r < MAP_ROWS; r++) {
                for (int c = 0; c < MAP_COLS; c++) {
                    if (buildingLayout[r][c].equals("MINER_H")) oreCarts.add(new OreCart(c*TILE_SIZE+TILE_SIZE/2, r*TILE_SIZE+TILE_SIZE/2, "HELLO"));
                    if (buildingLayout[r][c].equals("MINER_S")) oreCarts.add(new OreCart(c*TILE_SIZE+TILE_SIZE/2, r*TILE_SIZE+TILE_SIZE/2, "SAY"));
                }
            }

            if (currentTcpState == TcpState.CLOSED) {
                if (helloStock >= 2 && sayStock >= 1) {
                    autoTriggerHandshake();
                }
            }
            else if (currentTcpState == TcpState.ESTABLISHED) {
                int effectiveWindow = Math.min(cwnd, rwnd);
                while (effectiveWindow > 0 && inFlightCount < effectiveWindow
                        && helloStock >= 2 && sayStock >= 1
                        && serverReceivedCount + serverBufferCount < totalDataToTransmit) {
                    helloStock -= 2;
                    sayStock -= 1;
                    inFlightCount++;

                    int currentSeq = nextSeqNum++;
                    activeTimers.add(new RetransmissionTask(currentSeq, now));
                    pendingDataCarts.add(new DataCart(pcFactory.x, pcFactory.y, "DATA", currentSeq));
                }
                if (serverReceivedCount >= totalDataToTransmit && inFlightCount == 0 && activeTimers.isEmpty() && serverBufferCount == 0) {
                    currentTcpState = TcpState.FIN_WAIT_1;
                    stateTimerWatchdog = now;
                    pendingDataCarts.add(new DataCart(pcFactory.x, pcFactory.y, "FIN_PC", 0));
                    txtHexDisplay.setText("【🛑 生产达成】：定量契约履行完毕，弹射 [FIN] 释放网路空间。");
                }
            }
            lastResourceTick = now;
            updateTopLabel();
        }

        // 零窗口探测
        if (currentTcpState == TcpState.ESTABLISHED && Math.min(cwnd, rwnd) == 0 && rwnd == 0) {
            if (now - lastProbeTime >= PROBE_INTERVAL) {
                lastProbeTime = now;
                pendingDataCarts.add(new DataCart(pcFactory.x, pcFactory.y, "ZWP", 0));
                txtHexDisplay.setText("【⚠️ 流量限制】：对端接收缓冲区满。弹射 [ZWP] 零窗口探测小车。");
            }
        }

        // 2. 原矿车移动
        oreCarts.removeIf(c -> {
            c.update();
            if (c.isArrived) {
                if (c.oreType.equals("HELLO")) helloStock++; else sayStock++;
                return true;
            }
            return false;
        });

        // 3. 数据长车移动（收集待删除元素）
        List<DataCart> toRemove = new ArrayList<>();
        for (DataCart cart : dataCarts) {
            int currentCol = (int)(cart.x / TILE_SIZE);

            if (currentCol >= 13 && currentCol <= 24 && !cart.isReturnTrip) {
                if (currentCartsInWan > WAN_BOTTLE_NECK_MAX && !isForemostCartInWan(cart)) {
                    cart.waitInQueueTimer++;
                    if (cart.waitInQueueTimer > 120) {
                        cart.isDropped = true;
                    }
                    continue;
                }
            }

            cart.update();
            if (cart.isDropped) {
                if (cart.cartType.equals("DATA")) {
                    inFlightCount = Math.max(0, inFlightCount - 1);
                } else {
                    resetTcpSession();
                    return;
                }
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

        prgNetwork.setValue((int)(((double)serverReceivedCount / totalDataToTransmit) * 100));
        canvas.repaint();
    }

    private boolean isForemostCartInWan(DataCart target) {
        double maxProgressX = 0;
        DataCart foremost = null;
        for (DataCart c : dataCarts) {
            int col = (int)(c.x / TILE_SIZE);
            if (col >= 13 && col <= 24 && !c.isReturnTrip) {
                if (c.x > maxProgressX) {
                    maxProgressX = c.x; foremost = c;
                }
            }
        }
        return foremost == null || foremost == target;
    }

    private void handleCartArrival(DataCart cart) {
        Point serverPos = findBuildingCoords("RX_ST");
        long now = System.currentTimeMillis();

        if (!cart.isReturnTrip) {
            if (cart.cartType.equals("SYN")) {
                DataCart synAck = new DataCart(serverPos.x, serverPos.y, "SYN_ACK", 0);
                synAck.isReturnTrip = true; pendingDataCarts.add(synAck);
                stateTimerWatchdog = now;
            }
            else if (cart.cartType.equals("ACK_PC")) {
                if (currentTcpState == TcpState.SYN_SENT) {
                    currentTcpState = TcpState.ESTABLISHED;
                    cwnd = 1; ssthresh = 8;
                    packetsAckedSinceLastIncrease = 0;
                    txtHexDisplay.setText("【🤝 三次握手完全建立！】: cwnd=1, ssthresh=8");
                }
            }
            else if (cart.cartType.equals("DATA")) {
                if (serverBufferCount < SERVER_BUFFER_MAX) {
                    serverBufferCount++;
                    if (serverBufferCount == 1) lastServerConsumeTime = now;

                    rwnd = SERVER_BUFFER_MAX - serverBufferCount;
                    DataCart dataAck = new DataCart(serverPos.x, serverPos.y, "DATA_ACK", cart.sequenceNumber);
                    dataAck.advertisedWindow = rwnd;
                    dataAck.isReturnTrip = true;
                    pendingDataCarts.add(dataAck);

                    funds += 500;
                } else {
                    txtHexDisplay.setText("【🚨 接收端缓冲区溢出丢包】: 包 [SEQ: " + cart.sequenceNumber + "] 坠毁！");
                }
            }
            else if (cart.cartType.equals("ZWP")) {
                DataCart probeAck = new DataCart(serverPos.x, serverPos.y, "DATA_ACK", 0);
                probeAck.advertisedWindow = SERVER_BUFFER_MAX - serverBufferCount;
                probeAck.isReturnTrip = true;
                pendingDataCarts.add(probeAck);
            }
            else if (cart.cartType.equals("FIN_PC")) {
                DataCart finAck = new DataCart(serverPos.x, serverPos.y, "FIN_ACK_SRV", 0);
                finAck.isReturnTrip = true; pendingDataCarts.add(finAck);
                DataCart srvFin = new DataCart(serverPos.x, serverPos.y, "FIN_SRV", 0);
                srvFin.isReturnTrip = true; pendingDataCarts.add(srvFin);
                stateTimerWatchdog = now;
            }
        }
        else {
            if (cart.cartType.equals("SYN_ACK")) {
                DataCart finalAck = new DataCart(pcFactory.x, pcFactory.y, "ACK_PC", 0);
                pendingDataCarts.add(finalAck); stateTimerWatchdog = now;
            }
            else if (cart.cartType.equals("DATA_ACK")) {
                this.rwnd = cart.advertisedWindow;

                if (cart.sequenceNumber > 0) {
                    if (cwnd < ssthresh) {
                        cwnd++;
                        txtHexDisplay.setText(String.format("【📈 慢启动】: cwnd=%d (阈值=%d)", cwnd, ssthresh));
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
            }
            else if (cart.cartType.equals("FIN_ACK_SRV")) {
                if (currentTcpState == TcpState.FIN_WAIT_1) {
                    currentTcpState = TcpState.FIN_WAIT_2; stateTimerWatchdog = now;
                }
            }
            else if (cart.cartType.equals("FIN_SRV")) {
                currentTcpState = TcpState.TIME_WAIT;
                DataCart lastAck = new DataCart(pcFactory.x, pcFactory.y, "LAST_ACK_PC", 0);
                pendingDataCarts.add(lastAck);
                Timer timer = new Timer(1500, e -> {
                    if (currentTcpState == TcpState.TIME_WAIT) {
                        resetTcpSession();
                        JOptionPane.showMessageDialog(this, "🎉 数据传输完成！会话已正常关闭。", "胜利", JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                timer.setRepeats(false);
                timer.start();
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
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("💰 资金: %d | 🌐 状态: [%s] | 🚩 cwnd: %d | 🎯 ssthresh: %d | 📥 rwnd: %d | 🎛️ 有效滑窗: %d | 📥 仓储: %d/%d | 📦 达成: %d/%d",
                funds, currentTcpState.toString(), cwnd, ssthresh, rwnd, effectiveWin, serverBufferCount, SERVER_BUFFER_MAX, serverReceivedCount, totalDataToTransmit));
        lblDashboard.setText(sb.toString());
    }

    private Point findBuildingCoords(String tag) {
        for (int r = 0; r < MAP_ROWS; r++) {
            for (int c = 0; c < MAP_COLS; c++) {
                if (buildingLayout[r][c].equals(tag)) return new Point(c*TILE_SIZE+TILE_SIZE/2, r*TILE_SIZE+TILE_SIZE/2);
            }
        }
        return null;
    }

    private class OreCart {
        double x, y; double speed = 5.0; String oreType; boolean isArrived = false;
        public OreCart(double x, double y, String type) { this.x = x; this.y = y; this.oreType = type; }
        public void update() {
            double dx = pcFactory.x - x; double dy = pcFactory.y - y; double dist = Math.sqrt(dx*dx+dy*dy);
            if (dist <= speed) isArrived = true; else { x += (dx/dist)*speed; y += (dy/dist)*speed; }
        }
    }

    private class DataCart {
        double x, y; double speed = 9.0; int stage; int timer = 0;
        boolean isArrived = false; boolean isDropped = false; boolean isReturnTrip = false;
        String cartType; String currentLayerStatus = "";
        int sequenceNumber;
        int advertisedWindow = 3;
        int waitInQueueTimer = 0;

        boolean c_Payload=false, c_SP=false, c_DP=false, c_SEQ=false, c_ACK=false, c_CTL=false, c_WIN=false, c_CHK=false;
        boolean hasTcp=false, hasIp=false, hasLlc=false, hasFcs=false, isRouterTranslated=false;

        public DataCart(double sx, double sy, String type, int seq) {
            this.x = sx; this.y = sy; this.cartType = type; this.sequenceNumber = seq;
            if (isControlFrame(type)) { this.stage = 2; this.c_Payload = false; } else { this.stage = 1; }
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
                if (machine != null) target = machine; else { isDropped = true; return; }
            }

            double dx = target.x - x; double dy = target.y - y; double dist = Math.sqrt(dx*dx+dy*dy);
            if (dist <= speed) {
                x = target.x; y = target.y;
                if (!isReturnTrip) {
                    processStageCraft();
                    if (stage < 19) { timer = 2; stage++; } else { isArrived = true; }
                } else { isArrived = true; }
            } else { x += (dx/dist)*speed; y += (dy/dist)*speed; }
        }

        private Point findTargetMachine(int s) {
            String tag = "NONE";
            switch(s) {
                case 1: tag = "TX_DATA"; break; case 2: tag = "T_SP"; break;
                case 3: tag = "T_DP"; break;    case 4: tag = "T_SEQ"; break;
                case 5: tag = "T_ACK"; break;   case 6: tag = "T_CTL"; break;
                case 7: tag = "T_WIN"; break;   case 8: tag = "T_CHK"; break;
                case 9: tag = "T_CORE"; break;  case 10: tag = "TX_IPD"; break;
                case 11: tag = "TX_IPH"; break; case 12: tag = "TX_LLC"; break;
                case 13: tag = "TX_FCS"; break;
                case 14: tag = "R_LAN"; break;  case 15: tag = "R_TAB"; break; case 16: tag = "R_WAN"; break;
                case 17: tag = "RX_LLC"; break; case 18: tag = "RX_IP"; break;  case 19: tag = "RX_TCP"; break;
            }
            return findBuildingCoords(tag);
        }

        private void processStageCraft() {
            switch(stage) {
                case 1: c_Payload = true; currentLayerStatus = "🟥 封装载荷"; break;
                case 2: c_SP = true; currentLayerStatus = "⚙️ 源端口"; break;
                case 3: c_DP = true; currentLayerStatus = "⚙️ 目的端口"; break;
                case 4: c_SEQ = true; currentLayerStatus = "🔢 SEQ:" + (sequenceNumber > 0 ? sequenceNumber : "Ctrl"); break;
                case 5: c_ACK = true; currentLayerStatus = "📜 确认号"; break;
                case 6: c_CTL = true; currentLayerStatus = "🚩 [" + cartType + "]"; break;
                case 7: c_WIN = true; currentLayerStatus = "🌊 滑窗阀"; break;
                case 8: c_CHK = true; currentLayerStatus = "🔥 校验和"; break;
                case 9: hasTcp = true; currentLayerStatus = c_Payload ? "🟧 TCP(带载)" : "🟧 TCP(空载)"; break;
                case 10: currentLayerStatus = "🟨 IP载荷"; break;
                case 11: hasIp = true; currentLayerStatus = "🟨 IP首部"; break;
                case 12: hasLlc = true; currentLayerStatus = "🟩 以太网头"; break;
                case 13: hasFcs = true; currentLayerStatus = "🟩 FCS尾"; break;
                case 14: hasLlc = false; hasFcs = false; currentLayerStatus = "🎛️ LAN拆包"; break;
                case 15: isRouterTranslated = true; currentLayerStatus = "🔀 NAT查表"; break;
                case 16: hasLlc = true; hasFcs = true; currentLayerStatus = "🛠️ WAN重封"; break;
                case 17: hasLlc = false; hasFcs = false; currentLayerStatus = "🔓 剥链路层"; break;
                case 18: hasIp = false; currentLayerStatus = "💛 剥网络层"; break;
                case 19: hasTcp = false; currentLayerStatus = "🧡 传输解体"; break;
            }
        }
    }

    private class GameCanvas extends JPanel {
        public GameCanvas() { setBackground(new Color(18, 20, 26)); }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g); Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            for (int r = 0; r < MAP_ROWS; r++) {
                for (int c = 0; c < MAP_COLS; c++) {
                    int x = c * TILE_SIZE, y = r * TILE_SIZE;
                    if (mapLayout[r][c] == 9) {
                        g2.setColor(new Color(35, 38, 48)); g2.fillRect(x, y, TILE_SIZE, TILE_SIZE);
                        g2.setColor(Color.BLACK); g2.drawRect(x, y, TILE_SIZE, TILE_SIZE); continue;
                    }
                    g2.setColor(new Color(30, 32, 40)); g2.drawRect(x, y, TILE_SIZE, TILE_SIZE);
                    if (mapLayout[r][c] == 1) { g2.setColor(new Color(40, 100, 220, 60)); g2.fillOval(x+6, y+6, TILE_SIZE-12, TILE_SIZE-12); }
                    if (mapLayout[r][c] == 2) { g2.setColor(new Color(40, 200, 100, 60)); g2.fillOval(x+6, y+6, TILE_SIZE-12, TILE_SIZE-12); }
                }
            }

            g2.setColor(new Color(255, 100, 0, 15));
            g2.fillRect(13 * TILE_SIZE, 0, (24 - 13 + 1) * TILE_SIZE, MAP_ROWS * TILE_SIZE);

            int wanCarCount = 0;
            for (DataCart c : dataCarts) {
                int col = (int)(c.x / TILE_SIZE);
                if (col >= 13 && col <= 24 && !c.isReturnTrip) wanCarCount++;
            }
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("微软雅黑", Font.BOLD, 14));
            g2.drawString("🚦 公网车辆: " + wanCarCount + "/" + WAN_BOTTLE_NECK_MAX, 14 * TILE_SIZE, 30);

            for (int r = 0; r < MAP_ROWS; r++) {
                for (int c = 0; c < MAP_COLS; c++) {
                    int x = c * TILE_SIZE, y = r * TILE_SIZE; String tag = buildingLayout[r][c];
                    if (tag.equals("NONE")) continue;

                    g2.setColor(new Color(45, 48, 58)); g2.fillRect(x+2, y+2, TILE_SIZE-4, TILE_SIZE-4);
                    g2.setColor(Color.GRAY); g2.drawRect(x+2, y+2, TILE_SIZE-4, TILE_SIZE-4);
                    g2.setFont(new Font("Consolas", Font.BOLD, 9)); g2.setColor(Color.WHITE);

                    if(tag.equals("PC_FACTORY")) { g2.setColor(new Color(0,130,200)); g2.fillRect(x+2,y+2,TILE_SIZE-4,TILE_SIZE-4); g2.setColor(Color.WHITE); g2.drawString("💻源PC", x+4, y+24); }
                    else if(tag.equals("RX_ST")) {
                        g2.setColor(new Color(190,30,50)); g2.fillRect(x+2,y+2,TILE_SIZE-4,TILE_SIZE-4); g2.setColor(Color.YELLOW); g2.drawString("🏛️服务器", x+2, y+24);
                        for(int b=0; b<serverBufferCount; b++) { g2.setColor(Color.RED); g2.fillRect(x + 4 + (b*6), y + 4, 5, 6); }
                    }
                    else if(tag.startsWith("T_") || tag.startsWith("TX_")) { g2.setColor(Color.ORANGE); g2.drawRect(x+2, y+2, TILE_SIZE-4, TILE_SIZE-4); g2.drawString(tag, x+4, y+24); }
                    else if(tag.startsWith("R_")) { g2.setColor(Color.CYAN); g2.drawRect(x+2, y+2, TILE_SIZE-4, TILE_SIZE-4); g2.drawString(tag, x+3, y+24); }
                    else if(tag.startsWith("RX_")) { g2.setColor(Color.MAGENTA); g2.drawRect(x+2, y+2, TILE_SIZE-4, TILE_SIZE-4); g2.drawString(tag, x+3, y+24); }
                    else if(tag.startsWith("MINER_H")) { g2.setColor(Color.CYAN); g2.drawString("🔷矿机", x+4, y+24); }
                    else if(tag.startsWith("MINER_S")) { g2.setColor(Color.GREEN); g2.drawString("🟩矿机", x+4, y+24); }
                }
            }

            oreCarts.forEach(c -> { g2.setColor(c.oreType.equals("HELLO") ? Color.CYAN : Color.GREEN); g2.fillOval((int)c.x-5, (int)c.y-5, 10, 10); });

            for (DataCart cart : dataCarts) {
                int cx = (int)cart.x; int cy = (int)cart.y; int bx = cx - 22;

                if (cart.waitInQueueTimer > 0) g2.setColor(Color.RED);
                else if (cart.cartType.equals("ZWP")) g2.setColor(Color.YELLOW);
                else if (cart.cartType.startsWith("SYN")) g2.setColor(Color.CYAN);
                else if (cart.cartType.startsWith("FIN")) g2.setColor(Color.PINK);
                else if (cart.cartType.contains("ACK")) g2.setColor(Color.GREEN);
                else g2.setColor(Color.LIGHT_GRAY);
                g2.fillOval(cx-7, cy-7, 14, 14);

                if (cart.hasLlc) { g2.setColor(cart.isRouterTranslated ? Color.PINK : Color.GREEN); g2.fillRect(bx, cy - 6, 6, 12); bx += 6; }
                if (cart.hasIp) { g2.setColor(Color.YELLOW); g2.fillRect(bx, cy - 6, 6, 12); bx += 6; }
                if (cart.hasTcp) { g2.setColor(Color.ORANGE); g2.fillRect(bx, cy - 6, 7, 12); bx += 7; }
                if (cart.c_Payload && !cart.isControlFrame(cart.cartType)) { g2.setColor(Color.RED); g2.fillRect(bx, cy - 4, 10, 8); }

                g2.setFont(new Font("微软雅黑", Font.BOLD, 11));
                String direction = cart.isReturnTrip ? "◀ 回传" : (cart.waitInQueueTimer > 0 ? "⚠️ 排队中" : "▶ 封包");
                String nameTag = cart.cartType.equals("DATA") ? "DATA-" + cart.sequenceNumber : cart.cartType;
                String label = String.format("【%s】%s 📍%s", nameTag, direction, cart.currentLayerStatus);

                g2.setColor(new Color(0, 0, 0, 160));
                g2.fillRect(cx - 40, cy - 28, g2.getFontMetrics().stringWidth(label) + 8, 16);
                g2.setColor(cart.waitInQueueTimer > 0 ? Color.RED : Color.YELLOW);
                g2.drawString(label, cx - 36, cy - 15);
            }
        }
    }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> new DataCartFactoryGame().setVisible(true)); }
}