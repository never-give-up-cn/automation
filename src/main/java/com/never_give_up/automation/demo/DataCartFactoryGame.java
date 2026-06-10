package com.never_give_up.automation.demo;

import com.never_give_up.automation.demo.adapter.FactoryManager;
import com.never_give_up.automation.demo.adapter.PacketAdapter;
import com.never_give_up.automation.demo.core.ProtocolConst;
import com.never_give_up.automation.demo.factory.function.NatMappingFactory;
import com.never_give_up.automation.demo.factory.link.LlcHeader;
import com.never_give_up.automation.demo.model.HttpPacket;
import com.never_give_up.automation.demo.model.IpPacket;
import com.never_give_up.automation.demo.model.TcpPacket;
import lombok.Data;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DataCartFactoryGame extends JFrame {
    enum TcpState {
        CLOSED, SYN_SENT, ESTABLISHED, FIN_WAIT_1, FIN_WAIT_2, CLOSE_WAIT, LAST_ACK, TIME_WAIT
    }

    enum TlsState {
        IDLE, CLIENT_HELLO_SENT, SERVER_HELLO_RCVD, FINISHED, CLIENT_KEY_EXCHANGE_SENT
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

        public boolean isExpired() {
            return System.currentTimeMillis() - createTime > ttl;
        }

        public long getRemainingMs() {
            return ttl - (System.currentTimeMillis() - createTime);
        }
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

    private static class NatEntry {
        String insideIp;
        int insidePort;
        String publicIp;
        int publicPort;

        public NatEntry(String insideIp, int insidePort, String publicIp, int publicPort) {
            this.insideIp = insideIp;
            this.insidePort = insidePort;
            this.publicIp = publicIp;
            this.publicPort = publicPort;
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

    private FactoryManager factoryManager;
    private PacketAdapter packetAdapter;

    // ========== 新增：各层工厂实例 ==========
    private com.never_give_up.automation.demo.factory.transport.TcpPacketFactory tcpFactory;
    private com.never_give_up.automation.demo.factory.network.IpPacketFactory ipFactory;
    private com.never_give_up.automation.demo.factory.link.EthernetFactory ethernetFactory;
    private com.never_give_up.automation.demo.factory.address.PortFactory portFactory;

    // ========== 新增字段（类成员） ==========
    private Map<IpFragmentKey, List<IpFragment>> fragmentBuffer = new HashMap<>();
    private int ipIdentifierCounter = 2000;
    private boolean udpCompleted = false;
    private static class IpFragment {
        int offset;
        boolean moreFragments;
        byte[] data;

        public IpFragment(int offset, boolean mf, byte[] data) {
            this.offset = offset;
            this.moreFragments = mf;
            this.data = data;
        }
    }

    // ========== 新增内部类（放在类外部或内部均可） ==========
    private static class IpFragmentKey {
        int identification;
        String srcIp;
        String dstIp;
        String protocol;

        public IpFragmentKey(int id, String src, String dst, String proto) {
            identification = id;
            srcIp = src;
            dstIp = dst;
            protocol = proto;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IpFragmentKey that = (IpFragmentKey) o;
            return identification == that.identification && Objects.equals(srcIp, that.srcIp) && Objects.equals(dstIp, that.dstIp) && Objects.equals(protocol, that.protocol);
        }

        @Override
        public int hashCode() {
            return Objects.hash(identification, srcIp, dstIp, protocol);
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
    private Map<String, NatEntry> natTable = new ConcurrentHashMap<>();
    private Set<Integer> ackedSeq = ConcurrentHashMap.newKeySet();
    private AtomicInteger natPortCounter = new AtomicInteger(50001);

    private String targetDomain = "www.demo.com";
    private String resolvedServerIp = null;
    private boolean isDnsResolving = false;
    private boolean isDnsResolved = false;

    private boolean pcIpAssigned = false;
    private String pcIpAddress = null;
    private boolean dhcpInProgress = false;
    private int dhcpStep = 0;

    private boolean tracerouteActive = false;
    private boolean tracerouteWaitReply = false;
    private int tracerouteNextTTL = 1;

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

    private JTable tcpConnTable;
    private DefaultTableModel tableModel;

    private int packetsAckedSinceLastIncrease = 0;
    private Set<Integer> sentSeq = ConcurrentHashMap.newKeySet();

    private boolean useUdp = false;
    private boolean httpDemoEnabled = false;
    private boolean tlsEnabled = false;
    private java.security.KeyPair serverRsaKeyPair;
    private javax.crypto.Cipher rsaCipher;
    private javax.crypto.SecretKey sessionKey;
    private javax.crypto.Cipher aesCipher;
    private boolean tlsCipherReady = false;
    private TlsState tlsState = TlsState.IDLE;
    private boolean udpActive = false;
    private int udpSeqToSend = 0;
    private long lastUdpSendTime = 0;

    private boolean httpSent = false;
    private String httpResponseContent = "";

    public DataCartFactoryGame() {
        setTitle("🌐 全协议栈网络可视化模拟器 (DHCP + TCP连接表 + ICMP Ping/Traceroute)");
        setSize(2000, 1050);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        factoryManager = new FactoryManager();
        packetAdapter = new PacketAdapter();
        // 初始化各层工厂
        tcpFactory = new com.never_give_up.automation.demo.factory.transport.TcpPacketFactory();
        ipFactory = new com.never_give_up.automation.demo.factory.network.IpPacketFactory();
        ethernetFactory = new com.never_give_up.automation.demo.factory.link.EthernetFactory();
        portFactory = new com.never_give_up.automation.demo.factory.address.PortFactory();

        stateTimerWatchdog = System.currentTimeMillis();
        try {
            initCrypto();
        } catch (Exception e) {
            throw new RuntimeException("加密组件初始化失败", e);
        }
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

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(280, 0));
        rightPanel.setBorder(BorderFactory.createTitledBorder("🌐 TCP 连接表 (netstat)"));
        tableModel = new DefaultTableModel(new String[]{"Proto", "Local Address", "Remote Address", "State"}, 0);
        tcpConnTable = new JTable(tableModel);
        tcpConnTable.setFont(new Font("Consolas", Font.PLAIN, 12));
        tcpConnTable.getTableHeader().setFont(new Font("微软雅黑", Font.BOLD, 12));
        tcpConnTable.setRowHeight(22);
        JScrollPane tableScroll = new JScrollPane(tcpConnTable);
        rightPanel.add(tableScroll, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

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

        JRadioButton rbTcp = new JRadioButton("TCP", true);
        JRadioButton rbUdp = new JRadioButton("UDP", false);
        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(rbTcp);
        modeGroup.add(rbUdp);
        rbTcp.addActionListener(e -> {
            useUdp = false;
            resetTcpSession();
            appendToConsole("【切换】: 使用 TCP 模式");
        });
        rbUdp.addActionListener(e -> {
            useUdp = true;
            resetTcpSession();
            appendToConsole("【切换】: 使用 UDP 模式");
        });

        JCheckBox cbHttp = new JCheckBox("📡 HTTP 演示", false);
        JCheckBox cbTls = new JCheckBox("🔒 启用 TLS", false);
        cbHttp.addActionListener(e -> {
            httpDemoEnabled = cbHttp.isSelected();
            cbTls.setEnabled(httpDemoEnabled);
            if (!httpDemoEnabled) cbTls.setSelected(false);
            tlsEnabled = cbTls.isSelected();
            resetTcpSession();
            appendToConsole(httpDemoEnabled ? "【启用 HTTP 演示】" : "【关闭 HTTP 演示】");

            // 如果已分配 IP 且启用 HTTP 演示，立即触发 DNS 解析
            if (httpDemoEnabled && pcIpAssigned && !isDnsResolved && !isDnsResolving) {
                appendToConsole("【🔄 HTTP 演示】: 检测到 IP 已分配，启动 DNS 解析");
                startDnsResolution();
            }
        });
        cbTls.addActionListener(e -> {
            tlsEnabled = cbTls.isSelected();
            resetTcpSession();
            appendToConsole(tlsEnabled ? "【启用 TLS】" : "【关闭 TLS】");
        });

        JButton resetButton = new JButton("🔄 重置会话");
        resetButton.addActionListener(e -> resetTcpSession());
        JButton clearArpButton = new JButton("🗑️ 清空 ARP 缓存");
        clearArpButton.addActionListener(e -> {
            arpCache.clear();
            updateArpDisplay();
        });
        JButton clearDnsButton = new JButton("🗑️ 清空 DNS 缓存");
        clearDnsButton.addActionListener(e -> {
            dnsCache.clear();
            updateDnsDisplay();
        });
        JButton clearConsoleButton = new JButton("🗑️ 清空控制台");
        clearConsoleButton.addActionListener(e -> txtHexDisplay.setText(""));

        JButton pingButton = new JButton("📡 PING");
        pingButton.addActionListener(e -> sendPing());
        JButton tracerouteButton = new JButton("🔎 TRACEROUTE");
        tracerouteButton.addActionListener(e -> startTraceroute());

        btnPanel.add(resetButton);
        btnPanel.add(clearArpButton);
        btnPanel.add(clearDnsButton);
        btnPanel.add(clearConsoleButton);
        btnPanel.add(pingButton);
        btnPanel.add(tracerouteButton);
        btnPanel.add(rbTcp);
        btnPanel.add(rbUdp);
        btnPanel.add(cbHttp);
        btnPanel.add(cbTls);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        buildShopUI();

        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                if (getDataCartAtPoint(p) != null) return;

                int col = e.getX() / TILE_SIZE;
                int row = e.getY() / TILE_SIZE;
                if (row >= MAP_ROWS || col >= MAP_COLS) return;

                if (SwingUtilities.isRightMouseButton(e)) {
                    String existing = buildingLayout[row][col];
                    if (existing.equals("NONE") || existing.equals("PC_FACTORY") || existing.equals("RX_ST")) return;
                    funds += existing.startsWith("MINER") ? PRICE_MINER / 2 : PRICE_MACHINE / 2;
                    buildingLayout[row][col] = "NONE";
                    canvas.repaint();
                    updateTopLabel();
                    return;
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
                updateTopLabel();
                canvas.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    DataCart cart = getDataCartAtPoint(e.getPoint());
                    if (cart != null) {
                        showPacketDetails(cart);
                    }
                }
            }
        });

        updateTopLabel();
        new Timer(30, e -> gameTick()).start();
        new Timer(1000, e -> {
            updateArpDisplay();
            updateDnsDisplay();
            updateNatDisplay();
            updateTcpConnTable();
        }).start();
    }

    private void appendToConsole(String text) {
        SwingUtilities.invokeLater(() -> {
            txtHexDisplay.append(text + "\n");
            txtHexDisplay.setCaretPosition(txtHexDisplay.getDocument().getLength());
        });
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

    private void updateArpDisplay() {
        StringBuilder arpSb = new StringBuilder();
        factoryManager.getArpCache().getCache().forEach((ip, entry) -> {
            if (!entry.isExpired(300000)) {
                arpSb.append(String.format("%s → %s\n", entry.getIpAddress(), entry.getMacAddress()));
            }
        });
        txtArpDisplay.setText(arpSb.toString());
    }


    private void updateDnsDisplay() {
        StringBuilder dnsSb = new StringBuilder();
        factoryManager.getDnsCache().getCache().forEach((domain, record) -> {
            long remainingSec = Math.max(0, record.getTtl() / 1000);
            dnsSb.append(String.format("%s → %s (TTL:%ds)\n", domain, record.getIp(), remainingSec));
        });
        txtDnsDisplay.setText(dnsSb.toString());
    }

    private void updateNatDisplay() {
        StringBuilder natSb = new StringBuilder();
        natTable.forEach((key, entry) -> {
            natSb.append(String.format("%s:%d → %s:%d\n", entry.insideIp, entry.insidePort, entry.publicIp, entry.publicPort));
        });
        txtNatDisplay.setText(natSb.toString());
    }

    private void updateTcpConnTable() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            if (!pcIpAssigned || resolvedServerIp == null || (currentTcpState == TcpState.CLOSED && !udpActive)) {
                return;
            }
            String localAddr = pcIpAddress + ":80";
            String remoteAddr = resolvedServerIp + ":443";
            String proto = useUdp ? "UDP" : "TCP";
            String state = useUdp ? "ACTIVE" : currentTcpState.toString();
            tableModel.addRow(new Object[]{proto, localAddr, remoteAddr, state});
        });
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

        String[][] categories = {{"【1. 内网采矿与原始数据】", "MINER_H", "🔷 Hello 采矿机", "MINER_S", "🟩 Say 采矿机"}, {"【2. 应用层】", "TX_APP", "📦 应用数据载荷"}, {"【3. DNS 解析】", "DNS_CLIENT", "🔍 DNS 客户端", "DNS_LOCAL", "📡 本地 DNS", "DNS_ROOT", "🌐 根 DNS", "DNS_AUTH", "🏢 权威 DNS"}, {"【4. DHCP 客户端】", "DHCP_DISC", "🔎 Discover", "DHCP_OFFER", "📥 Offer", "DHCP_REQ", "📤 Request", "DHCP_ACK", "✅ ACK"}, {"【5. 传输层 - TCP 封装】", "T_SP", "🔩 源端口", "T_DP", "🎯 目的端口", "T_SEQ", "🔢 序列号", "T_ACK", "📜 确认号", "T_CTL", "🚩 控制位", "T_WIN", "🌊 滑动窗口", "T_CHK", "🔥 校验和", "T_CORE", "🟧 TCP 段总装"}, {"【6. 网络层 - IP 封装】", "TX_IPH", "📦 IP 首部", "TX_IP_FRAG", "✂️ IP 分片器"}, {"【7. 链路层 - Ethernet II】", "TX_ARP", "🔍 ARP 解析", "ETH_DST", "🟦 目的 MAC", "ETH_SRC", "🟦 源 MAC", "ETH_TYPE", "🟦 EtherType", "TX_LLC", "🟩 LLC", "TX_FCS", "🟩 FCS"}, {"【8. 边界网关】", "R_LAN", "🎛️ LAN 拆包", "R_TAB", "🔀 路由查表", "R_NAT", "🌍 NAT 转换", "R_WAN", "🛠️ WAN 封装"}, {"【9. 公网路由器】", "ROUTER1", "📡 Router1", "ROUTER2", "📡 Router2", "ROUTER3", "📡 Router3"}, {"【10. 接收端解封装】", "RX_LLC", "🔓 链路层解封", "RX_IP", "💛 网络层解封", "RX_TCP", "🧡 传输层解封", "RX_APP", "💚 应用层交付"},{"【11. 网络安全设备】", "FW_IN", "🔥 入站防火墙", "FW_OUT", "🔥 出站防火墙", "IDS", "🛡️ IDS"},
                {"【12. 队列与缓冲】", "Q_IN", "📋 入队", "Q_OUT", "📋 出队", "Q_DROP", "📋 丢包器"},
                {"【13. 拥塞控制】", "CC_SLOW", "🐢 慢启动", "CC_AVOID", "🐌 拥塞避免", "CC_FAST", "⚡ 快速重传"},
                {"【14. 网络设备】", "SWITCH", "🔌 交换机", "HUB", "🔌 集线器", "BRIDGE", "🌉 网桥"},
                {"【15. 子网与链路】", "SUBNET_A", "🌐 子网A", "SUBNET_B", "🌐 子网B", "LINK_UP", "🔗 上行链路", "LINK_DOWN", "🔗 下行链路"}};

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
    }

    private void initMap() {
        // 初始化矿石资源点
        mapLayout[2][1] = 1;
        mapLayout[3][1] = 1;
        mapLayout[12][1] = 2;
        mapLayout[13][1] = 2;

        // 初始化地图布局和边界
        for (int r = 0; r < MAP_ROWS; r++) {
            for (int c = 0; c < MAP_COLS; c++) {
                buildingLayout[r][c] = "NONE";
                if (c == 20 || c == 33) mapLayout[r][c] = 9;  // 网关边界
            }
        }

        // 源PC和服务器
        buildingLayout[MAP_ROWS / 2][3] = "PC_FACTORY";
        pcFactory = new Point(3 * TILE_SIZE + TILE_SIZE / 2, (MAP_ROWS / 2) * TILE_SIZE + TILE_SIZE / 2);
        buildingLayout[MAP_ROWS / 2][MAP_COLS - 3] = "RX_ST";

        // 采矿机
        buildingLayout[2][1] = "MINER_H";
        buildingLayout[3][1] = "MINER_H";
        buildingLayout[12][1] = "MINER_S";
        buildingLayout[13][1] = "MINER_S";

        int startRow = MAP_ROWS / 2 - 3;

        // ========== 应用层及传输层封装路径 ==========
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

        // ========== 网络层封装路径 ==========
        buildingLayout[startRow][17] = "TX_IPH";
        buildingLayout[startRow][18] = "TX_IP_FRAG";

        // ========== 链路层封装路径 ==========
        buildingLayout[startRow][19] = "TX_ARP";
        buildingLayout[startRow][20] = "ETH_DST";
        buildingLayout[startRow][21] = "ETH_SRC";
        buildingLayout[startRow][22] = "ETH_TYPE";
        buildingLayout[startRow][23] = "TX_LLC";
        buildingLayout[startRow][24] = "TX_FCS";

        // ========== DHCP 路径 ==========
        int dhcpRow = MAP_ROWS / 2 - 4;
        buildingLayout[dhcpRow][4] = "DHCP_DISC";
        buildingLayout[dhcpRow][5] = "DHCP_SERVER";
        buildingLayout[dhcpRow][6] = "DHCP_OFFER";
        buildingLayout[dhcpRow][7] = "DHCP_REQ";
        buildingLayout[dhcpRow][8] = "DHCP_ACK";

        // ========== 边界网关（路由/NAT/WAN） ==========
        int gatewayRow = MAP_ROWS / 2;
        buildingLayout[gatewayRow][20] = "R_LAN";
        buildingLayout[gatewayRow][22] = "R_TAB";
        buildingLayout[gatewayRow][24] = "R_NAT";
        buildingLayout[gatewayRow][26] = "R_WAN";

        // ========== 公网路由器 ==========
        buildingLayout[gatewayRow][28] = "ROUTER1";
        buildingLayout[gatewayRow][30] = "ROUTER2";
        buildingLayout[gatewayRow][32] = "ROUTER3";

        // ========== 接收端解封装路径 ==========
        int receiveRow = MAP_ROWS / 2 - 2;
        buildingLayout[receiveRow][36] = "RX_LLC";
        buildingLayout[receiveRow][38] = "RX_IP";
        buildingLayout[receiveRow][40] = "RX_TCP";
        buildingLayout[receiveRow][42] = "RX_APP";

        // ========== 新增：更多接收端工厂 ==========
        buildingLayout[receiveRow][34] = "RX_ETH";      // 以太网解封
        buildingLayout[receiveRow][35] = "RX_ARP";      // ARP 解封
        buildingLayout[receiveRow][37] = "RX_FCS";      // FCS 校验
        buildingLayout[receiveRow][39] = "RX_FRAG";     // IP 分片重组
        buildingLayout[receiveRow][41] = "RX_PORT";     // 端口解封

        // ========== 新增：防火墙和安全设备 ==========
        int securityRow = MAP_ROWS / 2 - 5;
        buildingLayout[securityRow][20] = "FW_IN";      // 入站防火墙
        buildingLayout[securityRow][21] = "FW_OUT";     // 出站防火墙
        buildingLayout[securityRow][22] = "IDS";        // 入侵检测

        // ========== 新增：队列和缓冲区 ==========
        int queueRow = MAP_ROWS / 2 - 1;
        buildingLayout[queueRow][22] = "Q_IN";          // 入队
        buildingLayout[queueRow][23] = "Q_OUT";         // 出队
        buildingLayout[queueRow][24] = "Q_DROP";        // 丢包

        // ========== 新增：拥塞控制设备 ==========
        int congestionRow = MAP_ROWS / 2 + 1;
        buildingLayout[congestionRow][14] = "CC_SLOW";   // 慢启动
        buildingLayout[congestionRow][15] = "CC_AVOID";  // 拥塞避免
        buildingLayout[congestionRow][16] = "CC_FAST";   // 快速重传

        // ========== 新增：五元组提取 ==========
        buildingLayout[startRow][25] = "FIVETUPLE";

        // ========== 新增：会话管理 ==========
        buildingLayout[startRow][26] = "SESSION";

        // ========== 新增：带宽控制 ==========
        buildingLayout[gatewayRow][25] = "BW_CTRL";

        // ========== 新增：网络设备 ==========
        buildingLayout[2][15] = "SWITCH";      // 交换机
        buildingLayout[3][16] = "HUB";         // 集线器
        buildingLayout[4][17] = "BRIDGE";      // 网桥

        // ========== 新增：子网 ==========
        buildingLayout[1][18] = "SUBNET_A";
        buildingLayout[2][19] = "SUBNET_B";

        // ========== 新增：链路 ==========
        buildingLayout[5][20] = "LINK_UP";
        buildingLayout[6][21] = "LINK_DOWN";
    }

    private void sendPing() {
        if (!pcIpAssigned) {
            appendToConsole("【⚠️ PING 失败】: PC 尚未获取 IP 地址，请等待 DHCP 完成");
            return;
        }
        DataCart pingReq = new DataCart(pcFactory.x, pcFactory.y, "ICMP_ECHO_REQ", 0);
        pingReq.echoSendTimestamp = System.currentTimeMillis();
        pingReq.ttl = 64;
        pendingDataCarts.add(pingReq);
        appendToConsole("【📡 PING】: 发送 ICMP Echo Request (TTL=64) 到 " + targetDomain);
    }

    private void startTraceroute() {
        if (!pcIpAssigned) {
            appendToConsole("【⚠️ TRACEROUTE 失败】: PC 尚未获取 IP 地址");
            return;
        }
        if (tracerouteActive) {
            appendToConsole("【⚠️ TRACEROUTE】: 上一次追踪仍在进行中");
            return;
        }
        tracerouteActive = true;
        tracerouteNextTTL = 1;
        tracerouteWaitReply = false;
        appendToConsole("【🔎 TRACEROUTE】: 开始追踪路由到 " + targetDomain);
        sendNextTracerouteProbe();
    }

    private void sendNextTracerouteProbe() {
        if (!tracerouteActive) return;
        DataCart probe = new DataCart(pcFactory.x, pcFactory.y, "ICMP_ECHO_REQ", 0);
        probe.echoSendTimestamp = System.currentTimeMillis();
        probe.ttl = tracerouteNextTTL;
        pendingDataCarts.add(probe);
        tracerouteWaitReply = true;
        appendToConsole(String.format("【🔎 Traceroute】: 发送探测包 TTL=%d", tracerouteNextTTL));
    }

    private void startDhcpIfNeeded() {
        if (!pcIpAssigned && !dhcpInProgress) {
            dhcpInProgress = true;
            dhcpStep = 0;
            DataCart discover = new DataCart(pcFactory.x, pcFactory.y, "DHCP_DISCOVER", 0);
            discover.stage = 1;
            pendingDataCarts.add(discover);
            appendToConsole("【🔎 DHCP】: 发送 DHCP Discover (PC 尚无 IP)");
            updateTopLabel();
        }
    }

    private void startDnsResolution() {
        if (!pcIpAssigned) {
            appendToConsole("【⛔ DNS 阻断】: PC 未获取 IP，等待 DHCP 完成...");
            return;
        }
        if (isDnsResolved) {
            appendToConsole("【📚 DNS 已解析】: " + targetDomain + " → " + resolvedServerIp);
            // 如果已经解析完成但连接未建立，触发连接建立
            if (!useUdp && currentTcpState == TcpState.CLOSED) {
                performArpResolution(resolvedServerIp);
                startTcpHandshake();
            } else if (useUdp && !udpActive) {
                performArpResolution(resolvedServerIp);
                startUdpTransmission();
            }
            return;
        }
        if (isDnsResolving) {
            appendToConsole("【⏳ DNS 解析中】: 请稍候...");
            return;
        }
        isDnsResolving = true;
        appendToConsole("【🌐 DNS 解析开始】: 查询域名 " + targetDomain);

        String cached = factoryManager.getDnsCache().resolve(targetDomain);
        if (cached != null) {
            resolvedServerIp = cached;
            isDnsResolved = true;
            isDnsResolving = false;
            appendToConsole("【📚 DNS 缓存命中】: " + targetDomain + " → " + resolvedServerIp);
            performArpResolution(resolvedServerIp);
            if (!useUdp && currentTcpState == TcpState.CLOSED) {
                startTcpHandshake();
            } else if (useUdp && !udpActive) {
                startUdpTransmission();
            }
            return;
        }

        DataCart dnsQuery = new DataCart(pcFactory.x, pcFactory.y, "DNS_QUERY", 0);
        dnsQuery.domain = targetDomain;
        pendingDataCarts.add(dnsQuery);
        appendToConsole("【📤 DNS 查询】: 发送请求到本地 DNS 服务器");
    }

    private void performArpResolution(String targetIp) {
        String mac = factoryManager.getArpCache().getMac(targetIp);
        if (mac == null) {
            appendToConsole("【🔍 ARP 请求】: 谁拥有 " + targetIp + "？");
            String newMac = String.format("00:1A:2B:%02X:%02X:%02X", new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
            factoryManager.getArpCache().addEntry(targetIp, newMac);
            appendToConsole("【📥 ARP 响应】: " + targetIp + " → " + newMac);
            updateArpDisplay();
        } else {
            appendToConsole("【✅ ARP 缓存命中】: " + targetIp + " → " + mac);
        }
    }

    // ========== 新增：加密初始化 ==========
    private void initCrypto() throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        serverRsaKeyPair = keyGen.generateKeyPair();
        rsaCipher = Cipher.getInstance("RSA");
        KeyGenerator aesGen = KeyGenerator.getInstance("AES");
        aesGen.init(128);
        sessionKey = aesGen.generateKey();
        aesCipher = Cipher.getInstance("AES");
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
        appendToConsole("【 三次握手开始】: 发送 SYN (seq=100) 到 " + resolvedServerIp + " (TTL=64)");
        updateTopLabel();
    }

    private void startUdpTransmission() {
        udpActive = true;
        udpSeqToSend = 0;
        lastUdpSendTime = 0;
        serverReceivedCount = 0;
        serverBufferCount = 0;
        appendToConsole("【🚀 UDP 模式】: 跳过握手，直接发送数据");
    }

    private long lastResourceTick = 0;

    private void gameTick() {
        long now = System.currentTimeMillis();
        // 如果传输完成但超过 5 秒没有关闭，强制关闭
        if (serverReceivedCount >= totalDataToTransmit && !useUdp && !httpDemoEnabled
                && currentTcpState != TcpState.FIN_WAIT_1
                && currentTcpState != TcpState.TIME_WAIT
                && (now - lastServerConsumeTime) > 5000) {
            appendToConsole("【⏰ 超时关闭】: 传输完成 5 秒，自动开始四次挥手");
            currentTcpState = TcpState.FIN_WAIT_1;
            DataCart fin = new DataCart(pcFactory.x, pcFactory.y, "FIN_PC", 0);
            fin.ttl = 64;
            pendingDataCarts.add(fin);
        }

        startDhcpIfNeeded();

        if (!useUdp && now - stateTimerWatchdog > 300000) {
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

        if (!useUdp && currentTcpState == TcpState.ESTABLISHED) {
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
                    appendToConsole(String.format("【⚠️ 超时重传】: SEQ=%d (第%d次), ssthresh=%d, cwnd=1", task.seqNum, task.retryCount, ssthresh));
                    updateTopLabel();
                }
            }
        }

        int currentCartsInWan = 0;
        for (DataCart c : dataCarts) {
            int col = (int) (c.x / TILE_SIZE);
            if (!c.isReturnTrip && c.stage >= 25 && c.stage <= 31 && col >= 21 && col <= 34) {
                currentCartsInWan++;
            }
        }

        if (now - lastResourceTick >= 1000) {
            for (int r = 0; r < MAP_ROWS; r++) {
                for (int c = 0; c < MAP_COLS; c++) {
                    if (buildingLayout[r][c].equals("MINER_H"))
                        oreCarts.add(new OreCart(c * TILE_SIZE + TILE_SIZE / 2, r * TILE_SIZE + TILE_SIZE / 2, "HELLO"));
                    if (buildingLayout[r][c].equals("MINER_S"))
                        oreCarts.add(new OreCart(c * TILE_SIZE + TILE_SIZE / 2, r * TILE_SIZE + TILE_SIZE / 2, "SAY"));
                }
            }

            // 修复后的启动逻辑，支持所有模式
            // 修复后的启动逻辑，支持所有模式
            if (pcIpAssigned) {
                if (!useUdp) {
                    // TCP 模式（包含普通TCP和HTTP演示）
                    if (currentTcpState == TcpState.CLOSED) {
                        if (httpDemoEnabled) {
                            // HTTP 演示模式：无需资源，直接启动
                            if (!isDnsResolved && !isDnsResolving) {
                                startDnsResolution();
                            }
                        } else {
                            // 普通 TCP 模式：需要采矿资源
                            if (helloStock >= 2 && sayStock >= 1) {
                                if (!isDnsResolved && !isDnsResolving) {
                                    startDnsResolution();
                                }
                            }
                        }
                    }
                } else {
                    // UDP 模式：直接启动（无需资源）
                    if (!udpActive && !isDnsResolved && !isDnsResolving) {
                        startDnsResolution();
                    }
                }
            }

            // HTTP / TLS 专用逻辑（仅在 TCP 且 HTTP 演示启用时）
            // 原位置：在 httpDemoEnabled 且连接建立后，添加以下 TLS 启动逻辑
            if (httpDemoEnabled && pcIpAssigned && !useUdp && currentTcpState == TcpState.ESTABLISHED && !httpSent) {
                if (tlsEnabled && tlsState == TlsState.IDLE) {
                    tlsState = TlsState.CLIENT_HELLO_SENT;
                    DataCart hello = new DataCart(pcFactory.x, pcFactory.y, "TLS_CLIENT_HELLO", 0);
                    hello.stage = 5; // 直接进入应用层封装
                    pendingDataCarts.add(hello);
                    appendToConsole("【🔒 TLS】: 发送 Client Hello");
                } else if (!tlsEnabled) {
                    sendHttpGet();
                }
            }

            // UDP 数据发送
            if (udpActive && serverReceivedCount < totalDataToTransmit) {
                if (now - lastUdpSendTime > 200) {
                    DataCart udpData = new DataCart(pcFactory.x, pcFactory.y, "UDP_DATA", udpSeqToSend++);
                    udpData.stage = 5;
                    udpData.ttl = 64;
                    pendingDataCarts.add(udpData);
                    lastUdpSendTime = now;
                    appendToConsole(String.format("【📤 UDP 发送】: SEQ=%d (无 ACK)", udpData.sequenceNumber));
                }
            }

            // 在 gameTick() 中添加
            if (udpActive && serverReceivedCount >= totalDataToTransmit && !udpCompleted) {
                udpCompleted = true;
                udpActive = false;
                appendToConsole("【🎉 UDP 传输完成】: 共发送 " + totalDataToTransmit + " 个数据包");

                // 延迟后显示弹窗
                Timer timer = new Timer(500, e -> {
                    JOptionPane.showMessageDialog(this,
                            "🎉 UDP 数据传输完成！\n\n" +
                                    "共传输 " + totalDataToTransmit + " 个数据包\n" +
                                    "演示了: UDP 无连接传输、NAT 转换、IP 分片组装、以太网封装",
                            "UDP 传输成功", JOptionPane.INFORMATION_MESSAGE);
                });
                timer.setRepeats(false);
                timer.start();
            }
// 普通 TCP 完成传输
            // 改为：
            if (serverReceivedCount >= totalDataToTransmit && !useUdp && !httpDemoEnabled) {
                // 等待一段时间让缓冲区清空和 ACK 完成
                if (activeTimers.isEmpty() && serverBufferCount == 0) {
                    currentTcpState = TcpState.FIN_WAIT_1;
                    stateTimerWatchdog = System.currentTimeMillis();
                    DataCart fin = new DataCart(pcFactory.x, pcFactory.y, "FIN_PC", 0);
                    fin.ttl = 64;
                    pendingDataCarts.add(fin);
                    appendToConsole("【🏁 数据传输完成】: 发送 FIN，开始四次挥手");
                } else if (serverBufferCount > 0) {
                    // 缓冲区还有数据，等待处理
                    appendToConsole("【⏳ 等待处理】: 缓冲区还有 " + serverBufferCount + " 个包，等待清空...");
                } else if (!activeTimers.isEmpty()) {
                    // 还有未确认的包，等待 ACK
                    appendToConsole("【⏳ 等待确认】: 还有 " + activeTimers.size() + " 个包等待 ACK...");
                }
            }

            lastResourceTick = now;
            updateTopLabel();
        }

        if (!useUdp && currentTcpState == TcpState.ESTABLISHED && Math.min(cwnd, rwnd) == 0 && rwnd == 0) {
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
            if (c.isArrived) {
                if (c.oreType.equals("HELLO")) helloStock++;
                else sayStock++;
                return true;
            }
            return false;
        });

        List<DataCart> toRemoveCarts = new ArrayList<>();
        for (DataCart cart : dataCarts) {
            int col = (int) (cart.x / TILE_SIZE);
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

        for (DataCart cart : toRemoveCarts) {
            if (cart.droppedAtRouterTag != null && cart.droppedAtPosition != null) {
                DataCart icmpTE = new DataCart(cart.droppedAtPosition.x, cart.droppedAtPosition.y, "ICMP_TIMEEXCEEDED", 0);
                icmpTE.droppedAtRouterTag = cart.droppedAtRouterTag;
                icmpTE.echoSendTimestamp = cart.echoSendTimestamp;
                icmpTE.isReturnTrip = true;
                icmpTE.stage = -1;
                icmpTE.ttl = 64;
                pendingDataCarts.add(icmpTE);
                appendToConsole("【⏱️ ICMP】: 生成 Time Exceeded 来自 " + cart.droppedAtRouterTag);
            }
        }

        dataCarts.removeAll(toRemoveCarts);

        if (!pendingDataCarts.isEmpty()) {
            // 防止队列无限膨胀，限制最大待处理包数
            if (pendingDataCarts.size() > 1000) {
                appendToConsole("【⚠️ 队列溢出】: 待处理包过多，清空队列防止卡死");
                pendingDataCarts.clear();
            } else {
                dataCarts.addAll(pendingDataCarts);
                pendingDataCarts.clear();
            }
        }

        dnsCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        prgNetwork.setValue((int) (((double) serverReceivedCount / totalDataToTransmit) * 100));
        canvas.repaint();
    }

    private boolean isForemostCartInWan(DataCart target) {
        double maxProgressX = 0;
        DataCart foremost = null;
        for (DataCart c : dataCarts) {
            int col = (int) (c.x / TILE_SIZE);
            if (!c.isReturnTrip && c.stage >= 25 && c.stage <= 31 && col >= 21 && col <= 34) {
                if (c.x > maxProgressX) {
                    maxProgressX = c.x;
                    foremost = c;
                }
            }
        }
        return foremost == null || foremost == target;
    }

    private void sendHttpGet() {
        httpSent = true;
        stateTimerWatchdog = System.currentTimeMillis();
        DataCart get = new DataCart(pcFactory.x, pcFactory.y, "HTTP_GET", 0);
        get.stage = 5;
        pendingDataCarts.add(get);
        appendToConsole("【📡 HTTP】: 发送 GET /index.html HTTP/1.1");
    }

    // ========== 新增辅助方法 ==========
    private boolean isReassemblyComplete(IpFragmentKey key) {
        List<IpFragment> frags = fragmentBuffer.get(key);
        if (frags == null || frags.isEmpty()) return false;
        // 简单判断：存在 MF=0 的最后一个分片即认为所有分片到达（此处未严格校验偏移连续性）
        for (IpFragment f : frags) {
            if (!f.moreFragments) return true;
        }
        return false;
    }

    private byte[] reassembleFragments(IpFragmentKey key) {
        List<IpFragment> frags = fragmentBuffer.get(key);
        frags.sort(Comparator.comparingInt(f -> f.offset));
        int totalLen = 0;
        for (IpFragment f : frags) totalLen += f.data.length;
        byte[] result = new byte[totalLen];
        int pos = 0;
        for (IpFragment f : frags) {
            System.arraycopy(f.data, 0, result, pos, f.data.length);
            pos += f.data.length;
        }
        return result;
    }

    private void handleCartArrival(DataCart cart) {
        Point serverPos = findBuildingCoords("RX_ST");
        Point dhcpServerPos = findBuildingCoords("DHCP_SERVER");
        long now = System.currentTimeMillis();

        if (!cart.isReturnTrip) {
            switch (cart.cartType) {
                case "DHCP_DISCOVER":
                    if (cart.stage == 2) {
                        appendToConsole("【🔎 DHCP】: Discover 到达服务器");
                        DataCart offer = new DataCart(dhcpServerPos.x, dhcpServerPos.y, "DHCP_OFFER", 0);
                        offer.isReturnTrip = true;
                        offer.stage = 1;
                        pendingDataCarts.add(offer);
                        appendToConsole("【📥 DHCP】: 服务器回复 Offer (192.168.1.100)");
                    }
                    break;
                case "DHCP_REQUEST":
                    if (cart.stage == 2) {
                        appendToConsole("【📤 DHCP】: Request 到达服务器");
                        DataCart ack = new DataCart(dhcpServerPos.x, dhcpServerPos.y, "DHCP_ACK", 0);
                        ack.isReturnTrip = true;
                        ack.stage = 1;
                        pendingDataCarts.add(ack);
                        appendToConsole("【✅ DHCP】: 服务器回复 ACK，分配 IP 192.168.1.100");
                    }
                    break;
            }
        } else {
            switch (cart.cartType) {
                case "DNS_RESPONSE":
                    resolvedServerIp = cart.resolvedIp;
                    isDnsResolved = true;
                    isDnsResolving = false;
                    dnsCache.put(targetDomain, new DnsEntry(targetDomain, resolvedServerIp, 3600000));
                    updateDnsDisplay();
                    appendToConsole("【 DNS 解析成功】: " + targetDomain + " → " + resolvedServerIp);
                    performArpResolution(resolvedServerIp);
                    if (!useUdp) startTcpHandshake();
                    else startUdpTransmission();
                    return;
                case "DHCP_OFFER":
                    appendToConsole("【 DHCP】: Offer 到达客户端");
                    dhcpStep = 2;
                    DataCart request = new DataCart(pcFactory.x, pcFactory.y, "DHCP_REQUEST", 0);
                    request.stage = 1;
                    pendingDataCarts.add(request);
                    appendToConsole("【 DHCP】: 发送 Request");
                    break;
                case "DHCP_ACK":
                    appendToConsole("【✅ DHCP】: ACK 到达，IP 分配成功！");
                    pcIpAssigned = true;
                    pcIpAddress = factoryManager.getIpAddressFactory().getDeviceIp("PC");
                    if (pcIpAddress == null) {
                        pcIpAddress = factoryManager.getIpAddressFactory().setCustomIp("PC", "192.168.1.100");
                    }
                    dhcpInProgress = false;
                    dhcpStep = 4;
                    funds += 200;
                    updateTopLabel();
                    appendToConsole("【 网络就绪】: PC IP = " + pcIpAddress);
                    break;
            }
        }

        if (cart.cartType.equals("ICMP_ECHO_REQ") && !cart.isReturnTrip && cart.isArrived) {
            appendToConsole("【📥 ICMP】: 服务器收到 Echo Request，回复 Echo Reply");
            DataCart echoReply = new DataCart(serverPos.x, serverPos.y, "ICMP_ECHO_REPLY", 0);
            echoReply.isReturnTrip = true;
            echoReply.echoSendTimestamp = cart.echoSendTimestamp;
            echoReply.ttl = 64;
            echoReply.stage = -1;
            pendingDataCarts.add(echoReply);
            return;
        }

        if (cart.cartType.equals("ICMP_ECHO_REPLY") && cart.isReturnTrip) {
            long rtt = System.currentTimeMillis() - cart.echoSendTimestamp;
            int finalTtl = cart.ttl - 3;
            appendToConsole(String.format("【📥 ICMP】: 收到 Echo Reply, time=%dms, TTL=%d", rtt, finalTtl));

            if (tracerouteActive) {
                appendToConsole("【🏁 Traceroute 完成】: 到达目标服务器");
                tracerouteActive = false;
                tracerouteWaitReply = false;
            }
            return;
        }

        if (cart.cartType.equals("ICMP_TIMEEXCEEDED") && cart.isReturnTrip) {
            long rtt = System.currentTimeMillis() - cart.echoSendTimestamp;
            String router = cart.droppedAtRouterTag != null ? cart.droppedAtRouterTag : "Unknown";
            appendToConsole(String.format("【⏱️ ICMP Time Exceeded】: 来自 %s, time=%dms", router, rtt));

            if (tracerouteActive && tracerouteWaitReply) {
                appendToConsole(String.format("  %d  %s  time=%dms", tracerouteNextTTL, router, rtt));
                tracerouteWaitReply = false;
                tracerouteNextTTL++;
                if (tracerouteNextTTL <= 30) {
                    sendNextTracerouteProbe();
                } else {
                    appendToConsole("【⚠️ Traceroute】: 超过最大跳数，停止追踪");
                    tracerouteActive = false;
                }
            }
            return;
        }

        if (cart.cartType.equals("UDP_DATA") && !cart.isReturnTrip && cart.isArrived) {
            if (serverBufferCount < SERVER_BUFFER_MAX) {
                serverBufferCount++;
                if (serverBufferCount == 1) lastServerConsumeTime = now;
                rwnd = SERVER_BUFFER_MAX - serverBufferCount;
                funds += 500;
                appendToConsole(String.format("【📦 UDP 数据】: SEQ=%d 已接收（无 ACK）", cart.sequenceNumber));
            } else {
                appendToConsole(String.format("【💥 缓冲区溢出】: UDP SEQ=%d 丢失", cart.sequenceNumber));
            }
            return;
        }

        if (!cart.isReturnTrip) {
            switch (cart.cartType) {
                // ========== handleCartArrival() 中新增 IP_FRAGMENT 处理 ==========
                case "IP_FRAGMENT":
                    if (!cart.isReturnTrip && cart.isArrived) {
                        String dstIp = resolvedServerIp != null ? resolvedServerIp : "unknown";
                        IpFragmentKey key = new IpFragmentKey(cart.identification, pcIpAddress, dstIp, "TCP");
                        fragmentBuffer.computeIfAbsent(key, k -> new ArrayList<>()).add(new IpFragment(cart.fragmentOffset, cart.moreFragments, cart.fragmentData));
                        appendToConsole(String.format("【🧩 IP 分片】: 收到分片 ID=%d offset=%d MF=%b", cart.identification, cart.fragmentOffset, cart.moreFragments));
                        if (isReassemblyComplete(key)) {
                            byte[] fullData = reassembleFragments(key);
                            fragmentBuffer.remove(key);
                            appendToConsole("【🧩 IP 重组完成】: 大小 " + fullData.length + " 字节");
                            // 生成重组后的完整数据包，并直接跳到 RX_TCP 阶段继续处理
                            DataCart reassembled = new DataCart(serverPos.x, serverPos.y, "DATA", 0);
                            reassembled.isReturnTrip = false;
                            reassembled.stage = 31; // 直接进入传输层解封装
                            pendingDataCarts.add(reassembled);
                        }
                    }
                    return;
                case "DNS_QUERY":
                    if (!cart.isReturnTrip && cart.stage >= 2) {
                        // 到达本地 DNS (stage 2)
                        appendToConsole("【📥 本地 DNS】: 无缓存，向根 DNS 发起递归查询");
                        DataCart toRoot = new DataCart(cart.x, cart.y, "DNS_RECURSION_ROOT", 0);
                        toRoot.domain = cart.domain;
                        toRoot.isReturnTrip = false;
                        pendingDataCarts.add(toRoot);
                        return;
                    }
                    appendToConsole("【📥 DNS 查询】: 到达 DNS 服务器，正在递归查询...");
                    DataCart dnsResp = new DataCart(cart.x, cart.y, "DNS_RESPONSE", 0);
                    dnsResp.domain = cart.domain;
                    dnsResp.resolvedIp = "10.0.0.1";
                    dnsResp.isReturnTrip = true;
                    pendingDataCarts.add(dnsResp);
                    break;
                case "DNS_RECURSION_ROOT":
                    if (!cart.isReturnTrip && cart.isArrived) {
                        // 到达根 DNS，返回顶级域权威 DNS 地址（模拟为 10.1.1.1）
                        DataCart rootResp = new DataCart(cart.x, cart.y, "DNS_ROOT_TO_LOCAL", 0);
                        rootResp.domain = cart.domain;
                        rootResp.resolvedIp = "10.1.1.1"; // 模拟权威 DNS 地址
                        rootResp.isReturnTrip = false;
                        pendingDataCarts.add(rootResp);
                        appendToConsole("【 根 DNS】: 返回顶级域权威 DNS 地址 " + rootResp.resolvedIp);
                    }
                    break;
                case "DNS_ROOT_TO_LOCAL":
                    if (!cart.isReturnTrip && cart.isArrived) {
                        // 本地 DNS 收到根回复，向权威 DNS 发起查询
                        DataCart toAuth = new DataCart(cart.x, cart.y, "DNS_LOCAL_TO_AUTH", 0);
                        toAuth.domain = cart.domain;
                        toAuth.isReturnTrip = false;
                        pendingDataCarts.add(toAuth);
                        appendToConsole("【 本地 DNS】: 向权威 DNS 查询 " + cart.domain);
                    }
                    break;
                case "DNS_LOCAL_TO_AUTH":
                    if (!cart.isReturnTrip && cart.isArrived) {
                        // 权威 DNS 响应最终 IP
                        DataCart authResp = new DataCart(cart.x, cart.y, "DNS_AUTH_TO_LOCAL", 0);
                        authResp.domain = cart.domain;
                        authResp.resolvedIp = "10.0.0.1"; // 目标服务器 IP
                        authResp.isReturnTrip = false;
                        pendingDataCarts.add(authResp);
                        appendToConsole("【🏢 权威 DNS】: " + cart.domain + " → " + authResp.resolvedIp);
                    }
                    break;
                case "DNS_AUTH_TO_LOCAL":
                    if (!cart.isReturnTrip && cart.isArrived) {
                        // 本地 DNS 获得最终结果，缓存并返回给客户端
                        resolvedServerIp = cart.resolvedIp;
                        isDnsResolved = true;
                        isDnsResolving = false;
                        dnsCache.put(targetDomain, new DnsEntry(targetDomain, resolvedServerIp, 3600000));
                        appendToConsole("【 本地 DNS】: 缓存结果 " + targetDomain + " → " + resolvedServerIp);
                        updateDnsDisplay();

                        // 向客户端发送最终响应
                        DataCart finalResp = new DataCart(cart.x, cart.y, "DNS_RESPONSE", 0);
                        finalResp.domain = cart.domain;
                        finalResp.resolvedIp = resolvedServerIp;
                        finalResp.isReturnTrip = true;
                        finalResp.stage = -1; // 直接返回 PC，不走封装流程
                        pendingDataCarts.add(finalResp);
                        appendToConsole("【 DNS 响应】: 返回给客户端");
                    }
                    break;
                case "DNS_RECURSION_ROOT_RESP":
                    if (cart.isReturnTrip && cart.isArrived) {
                        // 本地 DNS 收到根回复，向权威 DNS 发起查询
                        DataCart toAuth = new DataCart(findBuildingCoords("DNS_AUTH").x, findBuildingCoords("DNS_AUTH").y, "DNS_RECURSION_AUTH", 0);
                        toAuth.domain = cart.domain;
                        toAuth.isReturnTrip = false;
                        pendingDataCarts.add(toAuth);
                        appendToConsole("【📤 本地 DNS】: 向权威 DNS 查询 " + cart.domain);
                    }
                    break;
                case "DNS_RECURSION_AUTH":
                    if (!cart.isReturnTrip && cart.isArrived) {
                        // 权威 DNS 响应最终 IP
                        DataCart authResp = new DataCart(cart.x, cart.y, "DNS_RECURSION_AUTH_RESP", 0);
                        authResp.domain = cart.domain;
                        authResp.resolvedIp = "10.0.0.1"; // 目标服务器 IP
                        authResp.isReturnTrip = true;
                        pendingDataCarts.add(authResp);
                        appendToConsole("【🏢 权威 DNS】: " + cart.domain + " → " + authResp.resolvedIp);
                    }
                    break;
                case "DNS_RECURSION_AUTH_RESP":
                    if (cart.isReturnTrip && cart.isArrived) {
                        // 本地 DNS 获得最终结果，缓存并返回给客户端
                        resolvedServerIp = cart.resolvedIp;
                        isDnsResolved = true;
                        isDnsResolving = false;
                        dnsCache.put(targetDomain, new DnsEntry(targetDomain, resolvedServerIp, 3600000));
                        appendToConsole("【 本地 DNS】: 缓存结果 " + targetDomain + " → " + resolvedServerIp);
                        updateDnsDisplay();

                        // 向客户端发送最终响应
                        DataCart finalResp = new DataCart(findBuildingCoords("DNS_LOCAL").x, findBuildingCoords("DNS_LOCAL").y, "DNS_RESPONSE", 0);
                        finalResp.domain = cart.domain;
                        finalResp.resolvedIp = resolvedServerIp;
                        finalResp.isReturnTrip = true;
                        finalResp.stage = -1; // 直接返回 PC，不走封装流程
                        pendingDataCarts.add(finalResp);
                        appendToConsole("【 DNS 响应】: 返回给客户端");
                    }
                    break;
                case "DNS_RESPONSE":
                    resolvedServerIp = cart.resolvedIp;
                    isDnsResolved = true;
                    isDnsResolving = false;
                    dnsCache.put(targetDomain, new DnsEntry(targetDomain, resolvedServerIp, 3600000));
                    updateDnsDisplay();
                    appendToConsole("【 DNS 解析成功】: " + targetDomain + " → " + resolvedServerIp);
                    performArpResolution(resolvedServerIp);

                    // 修复：根据当前状态决定启动传输层连接
                    if (!useUdp) {
                        // TCP模式：只在CLOSED状态下启动握手
                        if (currentTcpState == TcpState.CLOSED) {
                            startTcpHandshake();
                        } else {
                            appendToConsole("【⚠️ TCP】: 当前状态为 " + currentTcpState + "，跳过握手");
                        }
                    } else {
                        // UDP模式：只在未激活时启动
                        if (!udpActive) {
                            startUdpTransmission();
                        } else {
                            appendToConsole("【⚠️ UDP】: 传输已激活，跳过启动");
                        }
                    }
                    return;
                case "DHCP_OFFER":
                    appendToConsole("【 DHCP】: Offer 到达客户端");
                    dhcpStep = 2;
                    DataCart request = new DataCart(pcFactory.x, pcFactory.y, "DHCP_REQUEST", 0);
                    request.stage = 1;
                    pendingDataCarts.add(request);
                    appendToConsole("【 DHCP】: 发送 Request");
                    break;
                case "DHCP_ACK":
                    appendToConsole("【✅ DHCP】: ACK 到达，IP 分配成功！");
                    pcIpAssigned = true;
                    pcIpAddress = factoryManager.getIpAddressFactory().getDeviceIp("PC");
                    if (pcIpAddress == null) {
                        pcIpAddress = factoryManager.getIpAddressFactory().setCustomIp("PC", "192.168.1.100");
                    }
                    dhcpInProgress = false;
                    dhcpStep = 4;
                    funds += 200;
                    updateTopLabel();
                    appendToConsole("【 网络就绪】: PC IP = " + pcIpAddress);
                    break;
                case "SYN":
                case "DATA":
                case "FIN_PC":
                case "ZWP":
                    if (cart.currentLayerStatus.contains("Router")) {
                        cart.ttl--;
                        if (cart.ttl <= 0) {
                            cart.isDropped = true;
                            appendToConsole(String.format("【⚠️ ICMP Time Exceeded】: %s (SEQ=%d) TTL 降为 0，数据包被丢弃", cart.cartType, cart.sequenceNumber));
                            return;
                        }
                    }
                    break;
                // ========== handleCartArrival() 中 TLS 新增 case ==========
                case "TLS_CLIENT_HELLO":
                    if (cart.isArrived) {
                        // 服务器回复 ServerHello + Certificate（携带公钥）
                        DataCart shCert = new DataCart(serverPos.x, serverPos.y, "TLS_SERVER_HELLO_CERT", 0);
                        shCert.isReturnTrip = true;
                        shCert.stage = -1;
                        shCert.serverCertificate = serverRsaKeyPair.getPublic().getEncoded();
                        pendingDataCarts.add(shCert);
                        appendToConsole("【 TLS】: 服务器回复 Server Hello + Certificate（RSA 公钥）");
                    }
                    return;
                case "TLS_CLIENT_FINISHED":
                    if (cart.isArrived) {
                        DataCart sf = new DataCart(serverPos.x, serverPos.y, "TLS_SERVER_FINISHED", 0);
                        sf.isReturnTrip = true;
                        sf.stage = -1;
                        pendingDataCarts.add(sf);
                        appendToConsole("【🔒 TLS】: 服务器收到 Finished，回复 ChangeCipherSpec + Finished");
                    }
                    return;
                case "HTTP_GET":
                    if (cart.isArrived) {
                        DataCart httpOk = new DataCart(serverPos.x, serverPos.y, "HTTP_200_OK", 0);
                        httpOk.isReturnTrip = true;
                        httpOk.stage = -1;
                        httpOk.httpBody = "Hello World";
                        pendingDataCarts.add(httpOk);
                        appendToConsole("【📡 HTTP】: 服务器收到 GET，回复 200 OK");
                    }
                    return;
                case "TLS_CLIENT_KEY_EXCHANGE":
                    if (cart.isArrived && tlsState == TlsState.CLIENT_KEY_EXCHANGE_SENT) {
                        try {
                            rsaCipher.init(Cipher.DECRYPT_MODE, serverRsaKeyPair.getPrivate());
                            byte[] decryptedKey = rsaCipher.doFinal(cart.encryptedData);
                            sessionKey = new SecretKeySpec(decryptedKey, "AES");
                            aesCipher.init(Cipher.ENCRYPT_MODE, sessionKey);
                            tlsCipherReady = true;
                            appendToConsole("【🔒 TLS】: 服务器解密对称密钥成功，加密信道已建立");
                        } catch (Exception ex) {
                            appendToConsole("【❌ TLS】: 服务器解密失败");
                            ex.printStackTrace();
                            return;
                        }
                        DataCart sf = new DataCart(serverPos.x, serverPos.y, "TLS_SERVER_FINISHED", 0);
                        sf.isReturnTrip = true;
                        sf.stage = -1;
                        pendingDataCarts.add(sf);
                        appendToConsole("【🔒 TLS】: 服务器发送 ChangeCipherSpec + Finished");
                    }
                    return;
            }
            if (cart.cartType.equals("SYN") && !cart.isReturnTrip && cart.isArrived) {
                DataCart synAck = new DataCart(serverPos.x, serverPos.y, "SYN_ACK", 0);
                synAck.isReturnTrip = true;
                synAck.ackNumber = cart.sequenceNumber + 1;
                synAck.sequenceNumber = 200;
                pendingDataCarts.add(synAck);
                appendToConsole("【🤝 三次握手】: 收到 SYN，回复 SYN-ACK (seq=200, ack=" + (cart.sequenceNumber + 1) + ")");
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
                        appendToConsole(String.format("【📦 数据交付】: SEQ=%d 已接收，回复 ACK (rwnd=%d)", cart.sequenceNumber, rwnd));
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
            switch (cart.cartType) {
                case "TLS_SERVER_HELLO_CERT":
                    if (cart.isReturnTrip && cart.isArrived && tlsState == TlsState.CLIENT_HELLO_SENT) {
                        tlsState = TlsState.SERVER_HELLO_RCVD;
                        try {
                            // 客户端使用服务端公钥加密预主密钥（对称密钥）
                            PublicKey serverPub = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(cart.serverCertificate));
                            rsaCipher.init(Cipher.ENCRYPT_MODE, serverPub);
                            byte[] encryptedSessionKey = rsaCipher.doFinal(sessionKey.getEncoded());
                            // 发送 ClientKeyExchange
                            DataCart cke = new DataCart(pcFactory.x, pcFactory.y, "TLS_CLIENT_KEY_EXCHANGE", 0);
                            cke.encryptedData = encryptedSessionKey;
                            cke.stage = 5;
                            pendingDataCarts.add(cke);
                            appendToConsole("【🔒 TLS】: 客户端 RSA 加密预主密钥，发送 ClientKeyExchange");
                            tlsState = TlsState.CLIENT_KEY_EXCHANGE_SENT;
                        } catch (Exception ex) {
                            appendToConsole("【❌ TLS 错误】: 密钥交换失败");
                            ex.printStackTrace();
                        }
                    }
                    return;
                case "SYN_ACK":
                    DataCart finalAck = new DataCart(pcFactory.x, pcFactory.y, "ACK_PC", 0);
                    finalAck.ackNumber = cart.sequenceNumber + 1;
                    finalAck.isReturnTrip = true;
                    finalAck.stage = -1;
                    pendingDataCarts.add(finalAck);
                    currentTcpState = TcpState.ESTABLISHED;
                    stateTimerWatchdog = System.currentTimeMillis();
                    cwnd = 1;
                    ssthresh = 12;
                    packetsAckedSinceLastIncrease = 0;
                    appendToConsole("【 三次握手完成】: 收到 SYN-ACK，发送 ACK，连接建立！cwnd=1, ssthresh=12");
                    stateTimerWatchdog = now;

                    // 连接建立后立即触发数据传输
                    if (httpDemoEnabled && !httpSent) {
                        // HTTP 演示模式
                        if (tlsEnabled && tlsState == TlsState.IDLE) {
                            tlsState = TlsState.CLIENT_HELLO_SENT;
                            DataCart hello = new DataCart(pcFactory.x, pcFactory.y, "TLS_CLIENT_HELLO", 0);
                            hello.stage = 5;
                            pendingDataCarts.add(hello);
                            appendToConsole("【🔒 TLS】: 发送 Client Hello");
                        } else if (!tlsEnabled) {
                            sendHttpGet();
                        }
                    } else if (!useUdp && !httpDemoEnabled) {
                        // 普通 TCP 模式：直接开始发送 DATA
                        if (helloStock >= 2 && sayStock >= 1) {
                            helloStock -= 2;
                            sayStock -= 1;
                            funds -= 300;
                            updateTopLabel();
                            sendDataPackets();
                            appendToConsole("【📦 普通 TCP】: 资源充足，开始发送数据");
                        } else {
                            appendToConsole("【⚠️ 普通 TCP】: 资源不足，等待采矿 (需要 HELLO≥2, SAY≥1)");
                        }
                    }
                    break;
                case "ACK_PC":
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

                        // 普通 TCP 模式：收到 ACK 后继续发送数据
                        if (!httpDemoEnabled && !useUdp && currentTcpState == TcpState.ESTABLISHED) {
                            int effectiveWin = Math.min(cwnd, rwnd);
                            int unackedCount = sentSeq.size() - ackedSeq.size();
                            int canSend = effectiveWin - unackedCount;

                            if (canSend > 0 && serverReceivedCount < totalDataToTransmit) {
                                sendDataPackets();
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
                            JOptionPane.showMessageDialog(DataCartFactoryGame.this, "🎉 数据传输完成！\n\n共传输 " + totalDataToTransmit + " 个数据包\n" + "演示了 DHCP、DNS、ARP、TCP 三次握手、滑动窗口、拥塞控制、IP 分片、NAT、Ethernet II 封装、TTL 递减、四次挥手", "传输成功", JOptionPane.INFORMATION_MESSAGE);
                        }
                    });
                    timer.setRepeats(false);
                    timer.start();
                    break;
                case "TLS_SERVER_FINISHED":
                    if (tlsState == TlsState.CLIENT_KEY_EXCHANGE_SENT) {
                        tlsState = TlsState.FINISHED;
                        appendToConsole("【🔒 TLS】: 握手完成，后续 HTTP 请求将加密传输");
                        sendHttpGet(); // 现在可以发送加密的 HTTP GET
                    }
                    return;
                case "HTTP_200_OK":
                    httpResponseContent = cart.httpBody;
                    appendToConsole("【📡 HTTP】: 收到 200 OK, 内容: " + httpResponseContent);
                    JOptionPane.showMessageDialog(this, "HTTP 200 OK\n\n" + httpResponseContent, "HTTP 响应", JOptionPane.INFORMATION_MESSAGE);

                    // HTTP 请求完成后，开始四次挥手
                    currentTcpState = TcpState.FIN_WAIT_1;
                    stateTimerWatchdog = now;
                    DataCart fin = new DataCart(pcFactory.x, pcFactory.y, "FIN_PC", 0);
                    fin.ttl = 64;
                    pendingDataCarts.add(fin);
                    appendToConsole("【🏁 HTTP 完成】: 发送 FIN，开始四次挥手");
                    break;
            }
        }

    }

    private void resetTcpSession() {
        currentTcpState = TcpState.CLOSED;
        serverReceivedCount = 0;
        serverBufferCount = 0;
        cwnd = 1;
        udpCompleted = false;
        ssthresh = 12;
        rwnd = 3;
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
        tracerouteActive = false;
        tracerouteWaitReply = false;
        udpActive = false;
        udpSeqToSend = 0;
        tlsState = TlsState.IDLE;
        httpSent = false;
        httpResponseContent = "";
        dhcpInProgress = false;
        dhcpStep = 0;

        factoryManager.reset();

        updateTopLabel();
        canvas.repaint();
    }

    private void sendDataPackets() {
        // 普通 TCP 模式：发送 DATA 包
        int packetsToSend = Math.min(cwnd, totalDataToTransmit - serverReceivedCount);
        for (int i = 0; i < packetsToSend; i++) {
            DataCart data = new DataCart(pcFactory.x, pcFactory.y, "DATA", nextSeqNum++);
            data.ttl = 64;
            data.advertisedWindow = rwnd;
            pendingDataCarts.add(data);
            sentSeq.add(data.sequenceNumber);

            RetransmissionTask task = new RetransmissionTask(data.sequenceNumber, System.currentTimeMillis());
            activeTimers.add(task);

            appendToConsole(String.format("【📤 TCP 发送】: SEQ=%d (cwnd=%d)", data.sequenceNumber, cwnd));
        }
    }

    // 修改 updateTopLabel() 方法，添加更多调试信息
    private void updateTopLabel() {
        int effectiveWin = Math.min(cwnd, rwnd);
        String ipStatus = pcIpAssigned ? pcIpAddress : "未分配";
        String modeStr = useUdp ? "UDP" : "TCP";
        lblDashboard.setText(String.format(
                "💰 资金:%d | 🏷️ %s:%s | 🌐 IP:%s | 🚩 cwnd:%d | 🎯 ssthresh:%d | 📥 rwnd:%d | 🎛️ 有效窗口:%d | " +
                        "📦 仓储:%d/%d | ✅ 达成:%d/%d | 🔍 ARP:%d | 📚 DNS:%d | 🌐 域名:%s → %s | 🔎 Trace:%s | TLS:%s | " +
                        "⏱️ 定时器:%d | 💾 缓冲区:%d",
                funds, modeStr, currentTcpState, ipStatus, cwnd, ssthresh, rwnd, effectiveWin,
                serverBufferCount, SERVER_BUFFER_MAX, serverReceivedCount, totalDataToTransmit,
                arpCache.size(), dnsCache.size(), targetDomain,
                resolvedServerIp == null ? "未解析" : resolvedServerIp,
                tracerouteActive ? ("TTL=" + tracerouteNextTTL) : "空闲",
                tlsState, activeTimers.size(), serverBufferCount));
    }

    private Point findBuildingCoords(String tag) {
        for (int r = 0; r < MAP_ROWS; r++) {
            for (int c = 0; c < MAP_COLS; c++) {
                if (buildingLayout[r][c].equals(tag))
                    return new Point(c * TILE_SIZE + TILE_SIZE / 2, r * TILE_SIZE + TILE_SIZE / 2);
            }
        }
        return null;
    }

    private DataCart getDataCartAtPoint(Point p) {
        for (DataCart cart : dataCarts) {
            double dx = cart.x - p.x;
            double dy = cart.y - p.y;
            if (Math.sqrt(dx * dx + dy * dy) < 12) return cart;
        }
        return null;
    }

    private void showPacketDetails(DataCart cart) {
        JDialog dialog = new JDialog(this, "🔍 协议栈详细信息 (Wireshark)", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        JTextArea details = new JTextArea();
        details.setEditable(false);
        details.setFont(new Font("Consolas", Font.PLAIN, 13));
        details.setBackground(new Color(30, 30, 30));
        details.setForeground(Color.WHITE);

        StringBuilder sb = new StringBuilder();
        sb.append("═════════ Ethernet II ═════════\n");
        String dstMac = (resolvedServerIp != null && arpCache.containsKey(resolvedServerIp)) ? arpCache.get(resolvedServerIp).macAddress : "??:??:??:??:??:??";
        String srcMac = (pcIpAddress != null && arpCache.containsKey(pcIpAddress)) ? arpCache.get(pcIpAddress).macAddress : "00:1A:2B:3C:4D:5F";
        sb.append(String.format("Dst MAC: %s\n", dstMac));
        sb.append(String.format("Src MAC: %s\n", srcMac));
        sb.append("Type: 0x0800 (IPv4)\n\n");

        sb.append("═════════ IP Header ═════════\n");
        String srcIp = pcIpAssigned ? pcIpAddress : "0.0.0.0";
        String dstIp = resolvedServerIp != null ? resolvedServerIp : "?.?.?.?";
        if (cart.isNatted) {
            srcIp = cart.natPublicIp + ":" + cart.natPublicPort;
        }
        sb.append(String.format("Src IP: %s\n", srcIp));
        sb.append(String.format("Dst IP: %s\n", dstIp));
        sb.append(String.format("TTL: %d\n", cart.ttl));
        sb.append("Protocol: " + (useUdp ? "UDP" : "TCP") + "\n\n");

        if (!useUdp) {
            sb.append("═════════ TCP Header ═════════\n");
            sb.append("Src Port: 1234\n");
            sb.append("Dst Port: 443\n");
            sb.append(String.format("SEQ: %d\n", cart.sequenceNumber));
            sb.append(String.format("ACK: %d\n", cart.ackNumber));
            sb.append("Flags: [" + cart.cartType + "]\n");
            sb.append(String.format("Window: %d\n", cart.advertisedWindow));
        } else {
            sb.append("═════════ UDP Header ═════════\n");
            sb.append("Src Port: 1234\n");
            sb.append("Dst Port: 443\n");
            sb.append("Length: 8\n");
            sb.append("Checksum: 0x0000\n");
        }

        if (cart.httpBody != null && !cart.httpBody.isEmpty()) {
            sb.append("\n═════════ HTTP Payload ═════════\n");
            sb.append(cart.httpBody);
        }

        details.setText(sb.toString());
        JScrollPane scroll = new JScrollPane(details);
        dialog.add(scroll);
        dialog.setVisible(true);
    }

    private class OreCart {
        double x, y;
        double speed = 6.0;
        String oreType;
        boolean isArrived = false;

        public OreCart(double x, double y, String type) {
            this.x = x;
            this.y = y;
            this.oreType = type;
        }

        public void update() {
            double dx = pcFactory.x - x;
            double dy = pcFactory.y - y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist <= speed) isArrived = true;
            else {
                x += (dx / dist) * speed;
                y += (dy / dist) * speed;
            }
        }
    }

    @Data
    private class DataCart {
        private byte[] ethernetFrameData;  // 存储完整的以太网帧数据
        private boolean fcsVerified = false;  // FCS 是否已验证
        private boolean hasFiveTuple = false;
        private boolean hasSession = false;
        private boolean hasQueue = false;
        private boolean hasArp = false;
        private String srcMac;
        private String dstMac;
        private String srcIp;
        private String dstIp;
        private int srcPort;
        private int dstPort;
        private String protocol;
        private static final AtomicInteger cartIdGenerator = new AtomicInteger(0);
        public final int cartId = cartIdGenerator.getAndIncrement();
        int identification;
        int fragmentOffset;
        boolean moreFragments;
        byte[] fragmentData;
        byte[] serverCertificate;   // 用于 TLS ServerHello 携带证书
        byte[] encryptedData;       // 用于 ClientKeyExchange 携带加密的会话密钥
        double x, y;
        double speed = 12.0;
        int stage;
        int timer = 0;
        boolean isArrived = false;
        boolean isDropped = false;
        boolean isReturnTrip = false;
        boolean isRetransmission = false;
        String cartType;
        String currentLayerStatus = "";
        int sequenceNumber = 0;
        int ackNumber = 0;
        int advertisedWindow = 3;
        int waitInQueueTimer = 0;
        int ttl = 64;

        String domain;
        String resolvedIp;

        long echoSendTimestamp = 0;
        String droppedAtRouterTag = null;
        Point droppedAtPosition = null;

        boolean hasPayload = true;
        boolean hasApp = false, hasTcp = false, hasIp = false, hasEther = false, hasLlc = false, hasFcs = false;
        boolean c_Payload = false, c_SP = false, c_DP = false, c_SEQ = false, c_ACK = false, c_CTL = false, c_WIN = false, c_CHK = false;
        boolean isFragmented = false;
        boolean isNatted = false;
        String natPublicIp = null;
        int natPublicPort = 0;

        String httpBody = null;

        // 在 DataCart 构造函数中，修改 UDP/TLS 的 stage
        public DataCart(double sx, double sy, String type, int seq) {
            this.srcPort = 1234;  // 默认源端口
            this.dstPort = 443;   // 默认目的端口
            this.protocol = "TCP";
            this.x = sx;
            this.y = sy;
            this.cartType = type;
            this.sequenceNumber = seq;
            if (type.equals("ICMP_TIMEEXCEEDED") || type.equals("ICMP_ECHO_REPLY") || type.equals("HTTP_200_OK")) {
                this.isReturnTrip = true;
                this.stage = -1;
            } else if (isControlFrame(type)) {
                this.stage = 2;
            } else if (type.startsWith("TLS_") || type.equals("HTTP_GET") || type.equals("UDP_DATA")) {
                this.stage = 5;  // 从应用层开始
            } else {
                this.stage = 1;  // 从 DNS 客户端开始
            }
        }

        public boolean isControlFrame(String type) {
            return type.equals("SYN") || type.equals("SYN_ACK") || type.equals("ACK_PC") || type.equals("FIN_PC") || type.equals("FIN_ACK_SRV") || type.equals("FIN_SRV") || type.equals("DATA_ACK") || type.equals("LAST_ACK_PC") || type.equals("ZWP") || type.equals("DNS_QUERY") || type.equals("DNS_RESPONSE") || type.equals("DHCP_DISCOVER") || type.equals("DHCP_OFFER") || type.equals("DHCP_REQUEST") || type.equals("DHCP_ACK");
        }

        public void update() {
            if (timer > 0) {
                timer--;
                return;
            }

            // 为 ACK_PC 提供快速路径
            if (cartType.equals("ACK_PC") && !isReturnTrip && stage == 2) {
                Point target = findBuildingCoords("RX_ST");
                if (target != null) {
                    double dx = target.x - x;
                    double dy = target.y - y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist <= speed) {
                        x = target.x;
                        y = target.y;
                        isArrived = true;
                    } else {
                        x += (dx / dist) * speed;
                        y += (dy / dist) * speed;
                    }
                    return;
                }
            }

            Point target;

            if (stage == -1) {
                target = pcFactory;
                double dx = target.x - x;
                double dy = target.y - y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist <= speed) {
                    x = target.x;
                    y = target.y;
                    isArrived = true;
                } else {
                    x += (dx / dist) * speed;
                    y += (dy / dist) * speed;
                }
                return;
            }

            if (isDHCP()) {
                target = findTargetMachine(stage, cartType);
                if (target == null) target = isReturnTrip ? pcFactory : findBuildingCoords("DHCP_SERVER");
            } else {
                target = isReturnTrip ? pcFactory : findTargetMachine(stage, cartType);
            }

            if (target == null) {
                target = pcFactory;
            }

            double dx = target.x - x;
            double dy = target.y - y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist <= speed) {
                x = target.x;
                y = target.y;
                if (!isReturnTrip || isDHCP()) {
                    // ========== DataCart.update() 中 stage 15 的处理修改 ==========
// 在 processStageCraft() 调用之前，对于 stage==15 的特殊处理（仅对 DATA 等大包）
                    // ========== DataCart.update() 中 stage == 15 的处理修改 ==========
                    if (stage == 15 && !isReturnTrip && !isControlFrame(cartType) && cartType.equals("DATA")) {
                        final int MTU = 500;
                        int packetSize = 1000; // 硬编码的固定大小
                        if (packetSize > MTU) {
                            int fragCount = (packetSize + MTU - 1) / MTU;
                            for (int i = 0; i < fragCount; i++) {
                                DataCart fragment = new DataCart(x, y, "IP_FRAGMENT", 0);
                                fragment.identification = ipIdentifierCounter;
                                fragment.fragmentOffset = i * (MTU / 8);
                                fragment.moreFragments = (i < fragCount - 1);
                                fragment.fragmentData = new byte[Math.min(MTU, packetSize - i * MTU)];
                                fragment.stage = 16; // 从 IP 分片器继续后续封装
                                fragment.ttl = this.ttl;
                                pendingDataCarts.add(fragment);
                            }
                            ipIdentifierCounter++;
                            // 关键：标记原包为已处理，避免再次触发分片
                            this.isArrived = true; // 让原包在 gameTick 中被移除
                            return; // 不执行后续 processStageCraft
                        }
                    }
                    processStageCraft();
                    if (stage == 24 && !isNatted && !isReturnTrip) {
                        applyNatMapping();
                    }
                    if (stage >= 26 && stage <= 28) {
                        ttl--;
                        if (ttl <= 0) {
                            isDropped = true;
                            if (stage == 26) droppedAtRouterTag = "ROUTER1";
                            else if (stage == 27) droppedAtRouterTag = "ROUTER2";
                            else if (stage == 28) droppedAtRouterTag = "ROUTER3";
                            droppedAtPosition = new Point((int) x, (int) y);
                            return;
                        }
                    }
                    if (!isDHCP()) {
                        // 增加 stage 上限到 45
                        if (stage < 45) {
                            timer = 1;
                            stage++;
                        } else {
                            isArrived = true;
                        }
                    }else {
                        int maxStage = (cartType.equals("DHCP_DISCOVER") || cartType.equals("DHCP_REQUEST")) ? 2 : 2;
                        if (stage < maxStage) {
                            timer = 1;
                            stage++;
                        } else {
                            isArrived = true;
                        }
                    }
                } else {
                    isArrived = true;
                }
            } else {
                x += (dx / dist) * speed;
                y += (dy / dist) * speed;
            }
        }

        private void applyNatMapping() {
            String insideIp = pcIpAddress != null ? pcIpAddress : "192.168.1.100";
            int insidePort = 1234;

            // 调用 NAT 工厂创建映射
            NatMappingFactory.NatEntry factoryEntry = factoryManager.getNatFactory().createMapping(insideIp, insidePort);

            // 同步到本地 NAT 表
            String key = insideIp + ":" + insidePort;
            if (!natTable.containsKey(key)) {
                NatEntry localEntry = new NatEntry(factoryEntry.getInsideIp(), factoryEntry.getInsidePort(), factoryEntry.getPublicIp(), factoryEntry.getPublicPort());
                natTable.put(key, localEntry);
            }

            this.isNatted = true;
            this.natPublicIp = factoryEntry.getPublicIp();
            this.natPublicPort = factoryEntry.getPublicPort();
        }


        private boolean isDHCP() {
            return cartType.equals("DHCP_DISCOVER") || cartType.equals("DHCP_OFFER") || cartType.equals("DHCP_REQUEST") || cartType.equals("DHCP_ACK");
        }

        // 在 DataCart 类中，修改 stage 对应的 tag 映射
        private Point findTargetMachine(int s, String type) {
            // 为 ACK_PC 添加快速路由
            if (type.equals("ACK_PC")) {
                if (s == 2) {
                    return findBuildingCoords("T_CORE");
                } else if (s >= 3) {
                    return findBuildingCoords("RX_ST");
                }
                return null;
            }

            // DNS 递归查询专用路由
            if (type.equals("DNS_ROOT_TO_LOCAL")) {
                return findBuildingCoords("DNS_LOCAL");
            } else if (type.equals("DNS_LOCAL_TO_AUTH")) {
                return findBuildingCoords("DNS_AUTH");
            } else if (type.equals("DNS_AUTH_TO_LOCAL")) {
                return findBuildingCoords("DNS_LOCAL");
            }

            // TLS 和 HTTP 演示专用路由
            if (type.equals("TLS_CLIENT_HELLO") || type.equals("HTTP_GET") ||
                    type.equals("TLS_CLIENT_KEY_EXCHANGE") || type.equals("TLS_CLIENT_FINISHED")) {
                if (s == 5) return findBuildingCoords("TX_APP");
                return null;
            }
            if (type.equals("TLS_SERVER_HELLO_CERT") || type.equals("HTTP_200_OK") ||
                    type.equals("TLS_SERVER_FINISHED")) {
                if (s == -1) return findBuildingCoords("PC_FACTORY");
                return null;
            }

            // DHCP 专用路由
            if (type.equals("DHCP_DISCOVER")) {
                switch (s) {
                    case 1: return findBuildingCoords("DHCP_DISC");
                    case 2: return findBuildingCoords("DHCP_SERVER");
                }
            } else if (type.equals("DHCP_OFFER")) {
                switch (s) {
                    case 1: return findBuildingCoords("DHCP_OFFER");
                    case 2: return findBuildingCoords("PC_FACTORY");
                }
            } else if (type.equals("DHCP_REQUEST")) {
                switch (s) {
                    case 1: return findBuildingCoords("DHCP_REQ");
                    case 2: return findBuildingCoords("DHCP_SERVER");
                }
            } else if (type.equals("DHCP_ACK")) {
                switch (s) {
                    case 1: return findBuildingCoords("DHCP_ACK");
                    case 2: return findBuildingCoords("PC_FACTORY");
                }
            }

            // ========== 完整的 stage 到建筑映射 ==========
            String tag = "NONE";
            switch (s) {
                // DNS 解析路径
                case 1: tag = "DNS_CLIENT"; break;
                case 2: tag = "DNS_LOCAL"; break;
                case 3: tag = "DNS_ROOT"; break;
                case 4: tag = "DNS_AUTH"; break;

                // 应用层
                case 5: tag = "TX_APP"; break;

                // 传输层封装
                case 6: tag = "T_SP"; break;
                case 7: tag = "T_DP"; break;
                case 8: tag = "T_SEQ"; break;
                case 9: tag = "T_ACK"; break;
                case 10: tag = "T_CTL"; break;
                case 11: tag = "T_WIN"; break;
                case 12: tag = "T_CHK"; break;
                case 13: tag = "T_CORE"; break;

                // 网络层封装
                case 14: tag = "TX_IPH"; break;
                case 15: tag = "TX_IP_FRAG"; break;
                case 16: tag = "TX_ARP"; break;

                // 链路层封装
                case 17: tag = "ETH_DST"; break;
                case 18: tag = "ETH_SRC"; break;
                case 19: tag = "ETH_TYPE"; break;
                case 20: tag = "TX_LLC"; break;
                case 21: tag = "TX_FCS"; break;

                // 五元组和会话（新增加）
                case 22: tag = "FIVETUPLE"; break;
                case 23: tag = "SESSION"; break;

                // 边界网关
                case 24: tag = "R_LAN"; break;
                case 25: tag = "R_TAB"; break;
                case 26: tag = "R_NAT"; break;
                case 27: tag = "BW_CTRL"; break;      // 带宽控制
                case 28: tag = "R_WAN"; break;

                // 防火墙（新增加）
                case 29: tag = "FW_OUT"; break;       // 出站防火墙
                case 30: tag = "FW_IN"; break;        // 入站防火墙

                // 公网路由器
                case 31: tag = "ROUTER1"; break;
                case 32: tag = "ROUTER2"; break;
                case 33: tag = "ROUTER3"; break;

                // 队列（新增加）
                case 34: tag = "Q_IN"; break;
                case 35: tag = "Q_OUT"; break;
                case 36: tag = "Q_DROP"; break;

                // 接收端链路层解封
                case 37: tag = "RX_ETH"; break;
                case 38: tag = "RX_LLC"; break;
                case 39: tag = "RX_FCS"; break;
                case 40: tag = "RX_ARP"; break;

                // 接收端网络层解封
                case 41: tag = "RX_FRAG"; break;
                case 42: tag = "RX_IP"; break;

                // 接收端传输层解封
                case 43: tag = "RX_PORT"; break;
                case 44: tag = "RX_TCP"; break;

                // 接收端应用层交付
                case 45: tag = "RX_APP"; break;

                default: return null;
            }
            return findBuildingCoords(tag);
        }

        private void processStageCraft() {
            if (cartType.startsWith("DHCP")) return;

            switch (stage) {
                // ========== 应用层 ==========
                case 5: // 应用层
                    if (!hasApp && cartType.equals("HTTP_GET")) {
                        hasApp = true;
                        httpBody = factoryManager.getHttpFactory().createGetRequest("/index.html").getBody();
                        appendToConsole("【📦 应用层】: HTTP GET 请求封装");
                    } else if (!hasApp && cartType.equals("TLS_CLIENT_HELLO")) {
                        hasApp = true;
                        httpBody = factoryManager.getTlsFactory().createClientHello(new byte[32]).getTlsMessageType();
                        appendToConsole("【🔒 应用层】: TLS Client Hello 封装");
                    } else if (!hasApp && cartType.equals("UDP_DATA")) {
                        hasApp = true;
                        appendToConsole("【📦 应用层】: UDP 数据载荷");
                    }
                    break;

                // ========== 传输层封装 ==========
                case 6: // 源端口
                    if (!c_SP) {
                        c_SP = true;
                        srcPort = portFactory.allocateEphemeralPort();
                        appendToConsole("【🔩 源端口】: 分配端口 " + srcPort);
                    }
                    break;
                case 7: // 目的端口
                    if (!c_DP) {
                        c_DP = true;
                        dstPort = 443;
                        portFactory.reservePort(dstPort);
                        appendToConsole("【🎯 目的端口】: 目标端口 " + dstPort);
                    }
                    break;
                case 8: // SEQ
                    if (!c_SEQ) {
                        c_SEQ = true;
                        sequenceNumber = tcpFactory.getNextSeq();
                        appendToConsole("【🔢 序列号】: SEQ=" + sequenceNumber);
                    }
                    break;
                case 9: // ACK
                    if (!c_ACK) {
                        c_ACK = true;
                        appendToConsole("【📜 确认号】: ACK=" + ackNumber);
                    }
                    break;
                case 10: // CTL
                    if (!c_CTL) {
                        c_CTL = true;
                        appendToConsole("【🚩 控制位】: " + cartType);
                    }
                    break;
                case 11: // WIN
                    if (!c_WIN) {
                        c_WIN = true;
                        tcpFactory.setWindowSize(advertisedWindow);
                        appendToConsole("【🌊 滑动窗口】: win=" + advertisedWindow);
                    }
                    break;
                // 修改 case 12 中的校验和调用，添加数据参数
                case 12: // CHK
                    if (!c_CHK) {
                        c_CHK = true;
                        // 创建 TCP 段数据用于校验和计算
                        byte[] tcpData = new byte[20]; // 模拟 TCP 头
                        int checksum = factoryManager.getChecksumFactory().calculateTcpChecksum(
                                tcpData, getSrcIp(), getDstIp());
                        appendToConsole("【🔥 校验和】: 0x" + Integer.toHexString(checksum));
                    }
                    break;
                case 13: // TCP 段完成
                    if (!hasTcp) {
                        hasTcp = true;
                        tcpFactory.produce();
                        appendToConsole("【🟧 TCP 段】: 传输层封装完成");
                    }
                    break;

                // ========== 网络层封装 ==========
                case 14: // IP 首部
                    if (!hasIp) {
                        hasIp = true;
                        srcIp = pcIpAddress != null ? pcIpAddress : "192.168.1.100";
                        dstIp = resolvedServerIp != null ? resolvedServerIp : "10.0.0.1";
                        ipFactory.createTcpPacket(srcIp, dstIp, new byte[0]);
                        appendToConsole("【📦 IP 首部】: " + srcIp + " → " + dstIp + ", TTL=" + ttl);
                    }
                    break;
                case 15: // IP 分片
                    if (!isFragmented && cartType.equals("DATA")) {
                        isFragmented = true;
                        final int MTU = 500;
                        int packetSize = 1500;
                        if (packetSize > MTU) {
                            IpPacket tempPacket = new IpPacket();
                            tempPacket.setPayload(new byte[packetSize]);
                            factoryManager.getIpFragmentFactory().fragmentPacket(tempPacket);
                            appendToConsole("【✂️ IP 分片】: 分包 " + ((packetSize + MTU - 1) / MTU) + " 片");
                        }
                    }
                    break;
                case 16: // ARP 解析
                    if (!hasArp && resolvedServerIp != null && pcIpAddress != null) {
                        hasArp = true;
                        String mac = factoryManager.getArpCache().getMac(resolvedServerIp);
                        if (mac == null) {
                            appendToConsole("【🔍 ARP 请求】: 谁拥有 " + resolvedServerIp + "?");
                            String newMac = String.format("00:1A:2B:%02X:%02X:%02X",
                                    new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
                            factoryManager.getArpCache().addEntry(resolvedServerIp, newMac);
                            appendToConsole("【📥 ARP 响应】: " + resolvedServerIp + " → " + newMac);
                            updateArpDisplay();
                        } else {
                            appendToConsole("【✅ ARP 缓存】: " + resolvedServerIp + " → " + mac);
                        }
                    }
                    break;

                case 17: // Ethernet DST MAC
                    if (!hasEther && pcIpAddress != null && resolvedServerIp != null) {
                        dstMac = factoryManager.getArpCache().getMac(resolvedServerIp);
                        if (dstMac == null || dstMac.isEmpty()) {
                            dstMac = "00:1A:2B:3C:4D:60";  // 默认网关 MAC
                        }
                        appendToConsole("【🟦 目的 MAC】: " + dstMac);
                    }
                    break;
                case 18: // Ethernet SRC MAC
                    if (!hasEther && pcIpAddress != null) {
                        srcMac = factoryManager.getArpCache().getMac(pcIpAddress);
                        if (srcMac == null || srcMac.isEmpty()) {
                            srcMac = "00:1A:2B:3C:4D:5F";  // 默认 PC MAC
                        }
                        appendToConsole("【🟦 源 MAC】: " + srcMac);
                    }
                    break;
                case 19: // EtherType
                    if (!hasEther) {
                        hasEther = true;
                        ethernetFactory.createIpFrame(srcMac, dstMac);
                        appendToConsole("【🟦 EtherType】: 0x0800 (IPv4)");
                    }
                    break;
                case 20: // LLC（可选）
                    if (!hasLlc) {
                        hasLlc = true;
                        factoryManager.getLinkLayerFactory().setLlcHeader();
                        LlcHeader llc = factoryManager.getLinkLayerFactory().getLlcHeader();
                        appendToConsole("【🔗 LLC】: " + llc.toString());
                    }
                    break;
                case 21: // FCS 校验（发送端：计算并附加）
                    if (!hasFcs) {
                        hasFcs = true;
                        byte[] networkData = ("IP Packet Data").getBytes();
                        byte[] completeFrame = factoryManager.getLinkLayerFactory()
                                .buildEthernetFrame(dstMac, srcMac, 0x0800, networkData, hasLlc);
                        this.setEthernetFrameData(completeFrame);
                        byte[] fcs = factoryManager.getLinkLayerFactory().getCurrentFcs();
                        appendToConsole(String.format("【🔒 FCS 计算】: CRC32 = %02X%02X%02X%02X",
                                fcs[0] & 0xFF, fcs[1] & 0xFF, fcs[2] & 0xFF, fcs[3] & 0xFF));
                        appendToConsole(String.format("【📦 完整帧】: %d 字节", completeFrame.length));
                    }
                    break;

                // ========== 五元组和会话（新增加） ==========
                case 22: // 五元组提取
                    if (!hasFiveTuple) {
                        hasFiveTuple = true;
                        protocol = useUdp ? "UDP" : "TCP";
                        factoryManager.getFiveTupleFactory().extract(srcIp, dstIp, srcPort, dstPort, protocol);
                        appendToConsole(String.format("【🔢 五元组】: %s %s:%d → %s:%d",
                                protocol, srcIp, srcPort, dstIp, dstPort));
                    }
                    break;
                case 23: // 会话管理
                    if (!hasSession) {
                        hasSession = true;
                        factoryManager.getSessionFactory().createSession(srcIp, dstIp, srcPort, dstPort);
                        appendToConsole("【💬 会话】: 创建会话 " + srcIp + ":" + srcPort);
                    }
                    break;

                // ========== 边界网关 ==========
                case 24: // LAN 拆包
                    if (!isReturnTrip) {
                        hasLlc = false;
                        hasFcs = false;
                        byte[] receivedFrame = this.getEthernetFrameData();
                        if (receivedFrame != null && receivedFrame.length > 0) {
                            byte[] networkData = factoryManager.getLinkLayerFactory()
                                    .extractNetworkData(receivedFrame);
                            if (networkData != null) {
                                this.setFcsVerified(true);
                                String info = factoryManager.getLinkLayerFactory().getRemoveEthernetHeaderInfo();
                                appendToConsole(info);
                            } else {
                                this.setFcsVerified(false);
                                appendToConsole("【⚠️ FCS 校验失败】: 帧损坏");
                                this.isDropped = true;
                                return;
                            }
                        }
                        factoryManager.getLinkLayerFactory().resetLinkLayer();
                        appendToConsole("【🎛️ LAN 拆包】: 链路层解封完成");
                    }
                    break;
                case 25: // 路由查表
                    factoryManager.getRouteTable().lookup(resolvedServerIp);
                    appendToConsole("【🔀 路由查表】: 目标 " + resolvedServerIp + " 下一跳 8.8.8.8");
                    break;
                case 26: // NAT 转换
                    if (!isNatted && !isReturnTrip) {
                        applyNatMapping();
                        appendToConsole("【🌍 NAT 转换】: " + srcIp + ":" + srcPort + " → " + natPublicIp + ":" + natPublicPort);
                        updateNatDisplay();
                    }
                    break;
                // 修改 case 27 中的带宽控制调用
                case 27: // 带宽控制
                    if (factoryManager.getBandwidthFactory() != null &&
                            factoryManager.getBandwidthFactory().shouldDropPacket()) {
                        appendToConsole("【💥 带宽限制】: 公网丢包，数据包被丢弃");
                        this.isDropped = true;
                        return;
                    }
                    appendToConsole("【📊 带宽控制】: 通过");
                    break;
                case 28: // WAN 封装
                    appendToConsole("【🛠️ WAN 封装】: 进入公网传输");
                    break;

                // ========== 防火墙 ==========
                case 29: // 出站防火墙
                    if (srcIp != null && dstIp != null) {
                        // ✅ 修复：使用当前数据包的真实协议（TCP/UDP）
                        boolean allowed = factoryManager.getFirewallFactory()
                                .allowOutbound(srcIp, dstIp, srcPort, dstPort, protocol);

                        if (!allowed) {
                            appendToConsole("【🔥 防火墙】: 出站包被阻断 " + srcIp + " → " + dstIp);
                            this.isDropped = true;
                            return;
                        } else {
                            appendToConsole("【🔥 防火墙】: 出站规则通过 ✅");
                        }
                    }
                    break;
                case 30: // 入站防火墙
                    if (isReturnTrip && dstIp != null && srcIp != null) {
                        if (!factoryManager.getFirewallFactory().allowInbound(srcIp, dstIp, srcPort, dstPort, Integer.parseInt(protocol))) {
                            appendToConsole("【🔥 防火墙】: 入站包被阻断 " + srcIp + " → " + dstIp);
                            this.isDropped = true;
                            return;
                        }
                        appendToConsole("【🔥 防火墙】: 入站规则通过");
                    }
                    break;

                // ========== 公网路由器 ==========
                case 31: // ROUTER1
                    ttl--;
                    appendToConsole("【📡 ROUTER1】: TTL=" + ttl);
                    if (ttl <= 0) {
                        appendToConsole("【⚠️ TTL 超时】: 数据包被丢弃");
                        this.isDropped = true;
                        return;
                    }
                    break;
                case 32: // ROUTER2
                    ttl--;
                    appendToConsole("【📡 ROUTER2】: TTL=" + ttl);
                    if (ttl <= 0) {
                        this.isDropped = true;
                        return;
                    }
                    break;
                case 33: // ROUTER3
                    ttl--;
                    appendToConsole("【📡 ROUTER3】: TTL=" + ttl);
                    if (ttl <= 0) {
                        this.isDropped = true;
                        return;
                    }
                    break;

                // 修改 case 34 和 35 中的队列调用
                case 34: // 队列入队
                    if (!hasQueue && factoryManager.getQueueFactory() != null) {
                        hasQueue = true;
                        factoryManager.getQueueFactory().enqueue(this);
                        appendToConsole("【📋 入队】: 数据包进入队列");
                    }
                    break;
                case 35: // 队列出队
                    if (hasQueue && factoryManager.getQueueFactory() != null) {
                        factoryManager.getQueueFactory().dequeue();
                        appendToConsole("【📋 出队】: 数据包离开队列");
                        hasQueue = false;
                    }
                    break;
                case 36: // 拥塞控制
                    if (cartType.equals("DATA") && !useUdp) {
                        factoryManager.getCongestionControl().congestionAvoidance(cwnd);
                        appendToConsole("【🐌 拥塞控制】: 调整窗口");
                    }
                    break;

                // ========== 接收端链路层解封 ==========
                case 37: // RX_ETH - 以太网解封
                    hasEther = false;
                    appendToConsole("【📡 RX_ETH】: 移除以太网头部");
                    break;
                case 38: // RX_LLC - LLC 解封
                    hasLlc = false;
                    appendToConsole("【📡 RX_LLC】: 移除 LLC 头部");
                    break;
                case 39: // RX_FCS - FCS 校验
                    if (!fcsVerified && this.getEthernetFrameData() != null) {
                        boolean valid = factoryManager.getLinkLayerFactory()
                                .verifyFcs(this.getEthernetFrameData());
                        if (!valid) {
                            appendToConsole("【⚠️ RX_FCS】: FCS 校验失败，帧损坏");
                            this.isDropped = true;
                            return;
                        }
                        this.setFcsVerified(true);
                    }
                    appendToConsole("【✓ RX_FCS】: 校验通过");
                    break;
                case 40: // RX_ARP - ARP 解析完成
                    appendToConsole("【📡 RX_ARP】: ARP 解析完成");
                    break;

                // ========== 接收端网络层解封 ==========
                case 41: // RX_FRAG - IP 分片重组
                    if (isFragmented) {
                        appendToConsole("【🧩 RX_FRAG】: IP 分片重组中");
                        // 这里可以添加实际的重组逻辑
                    }
                    break;
                case 42: // RX_IP - IP 层解封
                    hasIp = false;
                    if (cartType.equals("DATA") || cartType.equals("HTTP_200_OK")) {
                        IpPacket ipPacket = ipFactory.produce();
                        // 🔥 修复：不要传空数组，传一个标准IP头长度的字节数组
                        ipPacket.deserialize(new byte[ProtocolConst.IP_HEADER_SIZE]);
                        appendToConsole("【💛 RX_IP】: 网络层解封完成");
                    }
                    break;

                // ========== 接收端传输层解封 ==========
                case 43: // RX_PORT - 端口解封
                    appendToConsole("【🔌 RX_PORT】: 端口解封完成");
                    break;
                case 44: // RX_TCP - TCP 层解封
                    hasTcp = false;
                    if (cartType.equals("DATA") || cartType.equals("HTTP_200_OK")) {
                        TcpPacket tcpPacket = tcpFactory.produce();
                        tcpPacket.deserialize(new byte[0]);
                        appendToConsole("【🧡 RX_TCP】: 传输层解封完成");
                    }
                    break;

                // ========== 接收端应用层交付 ==========
                case 45: // RX_APP - 应用层交付
                    hasApp = false;
                    if (cartType.equals("HTTP_200_OK")) {
                        HttpPacket httpPacket = factoryManager.getHttpFactory()
                                .createResponse(200, httpResponseContent);
                        httpResponseContent = httpPacket.getBody();
                        appendToConsole("【💚 RX_APP】: 应用层交付完成");
                    }
                    break;
            }
        }
    }

    private class GameCanvas extends JPanel {
        public GameCanvas() {
            setBackground(new Color(18, 20, 26));
        }

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
                        g2.fillOval(x + 6, y + 6, TILE_SIZE - 12, TILE_SIZE - 12);
                    }
                    if (mapLayout[r][c] == 2) {
                        g2.setColor(new Color(40, 200, 100, 60));
                        g2.fillOval(x + 6, y + 6, TILE_SIZE - 12, TILE_SIZE - 12);
                    }
                }
            }

            g2.setColor(new Color(255, 100, 0, 15));
            g2.fillRect(21 * TILE_SIZE, 0, 14 * TILE_SIZE, MAP_ROWS * TILE_SIZE);

            int wanCarCount = 0;
            for (DataCart c : dataCarts) {
                int col = (int) (c.x / TILE_SIZE);
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
                    g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                    g2.setColor(Color.GRAY);
                    g2.drawRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                    g2.setFont(new Font("Consolas", Font.BOLD, 8));
                    g2.setColor(Color.WHITE);

                    if (tag.equals("PC_FACTORY")) {
                        g2.setColor(new Color(0, 130, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("💻 源PC", x + 4, y + 24);
                    } else if (tag.equals("RX_ST")) {
                        g2.setColor(new Color(190, 30, 50));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.YELLOW);
                        g2.drawString("🏛️ 服务器", x + 2, y + 24);
                        for (int b = 0; b < serverBufferCount; b++) {
                            g2.setColor(Color.RED);
                            g2.fillRect(x + 4 + (b * 6), y + 4, 5, 6);
                        }
                    } else if (tag.startsWith("DHCP_")) {
                        g2.setColor(new Color(100, 100, 255));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString(tag, x + 3, y + 24);
                    } else if (tag.equals("DNS_CLIENT") || tag.equals("DNS_LOCAL") || tag.equals("DNS_ROOT") || tag.equals("DNS_AUTH")) {
                        g2.setColor(new Color(0, 200, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString(tag, x + 3, y + 24);
                    } else if (tag.startsWith("ETH_")) {
                        g2.setColor(new Color(0, 160, 255));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString(tag, x + 3, y + 24);
                    } else if (tag.startsWith("ROUTER")) {
                        g2.setColor(new Color(200, 100, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString(tag, x + 3, y + 24);
                    } else if (tag.startsWith("T_") || tag.startsWith("TX_") || tag.startsWith("R_") || tag.startsWith("RX_")) {
                        if (tag.contains("NAT")) g2.setColor(new Color(255, 165, 0));
                        else if (tag.contains("ARP")) g2.setColor(new Color(0, 255, 255));
                        else if (tag.startsWith("R_")) g2.setColor(Color.CYAN);
                        else if (tag.startsWith("RX_")) g2.setColor(Color.MAGENTA);
                        else g2.setColor(Color.ORANGE);
                        g2.drawRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.drawString(tag, x + 3, y + 24);
                    } else if (tag.startsWith("MINER_H")) {
                        g2.setColor(Color.CYAN);
                        g2.drawString("🔷 矿机", x + 4, y + 24);
                    } else if (tag.startsWith("MINER_S")) {
                        g2.setColor(Color.GREEN);
                        g2.drawString("🟩 矿机", x + 4, y + 24);
                    }// 新增：防火墙渲染
                    else if (tag.equals("FW_IN") || tag.equals("FW_OUT")) {
                        g2.setColor(new Color(255, 80, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("🔥 " + tag, x + 3, y + 24);
                    }
// 新增：IDS 渲染
                    else if (tag.equals("IDS")) {
                        g2.setColor(new Color(255, 165, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("🛡️ IDS", x + 3, y + 24);
                    }
// 新增：队列渲染
                    else if (tag.equals("Q_IN") || tag.equals("Q_OUT") || tag.equals("Q_DROP")) {
                        g2.setColor(new Color(100, 100, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("📋 " + tag, x + 3, y + 24);
                    }
// 新增：拥塞控制渲染
                    else if (tag.startsWith("CC_")) {
                        g2.setColor(new Color(0, 200, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        String ccName = tag.equals("CC_SLOW") ? "🐢 慢启动" :
                                (tag.equals("CC_AVOID") ? "🐌 拥塞避免" : "⚡ 快速重传");
                        g2.drawString(ccName, x + 2, y + 24);
                    }
// 新增：五元组渲染
                    else if (tag.equals("FIVETUPLE")) {
                        g2.setColor(new Color(200, 100, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("🔢 五元组", x + 2, y + 24);
                    }
// 新增：会话管理渲染
                    else if (tag.equals("SESSION")) {
                        g2.setColor(new Color(100, 200, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("💬 会话", x + 4, y + 24);
                    }
// 新增：带宽控制渲染
                    else if (tag.equals("BW_CTRL")) {
                        g2.setColor(new Color(200, 200, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("📊 带宽", x + 4, y + 24);
                    }
// 新增：网络设备渲染
                    else if (tag.equals("SWITCH")) {
                        g2.setColor(new Color(0, 150, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("🔌 交换机", x + 2, y + 24);
                    }
                    else if (tag.equals("HUB")) {
                        g2.setColor(new Color(150, 100, 50));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("🔌 集线器", x + 2, y + 24);
                    }
                    else if (tag.equals("BRIDGE")) {
                        g2.setColor(new Color(100, 150, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("🌉 网桥", x + 4, y + 24);
                    }
// 新增：子网渲染
                    else if (tag.startsWith("SUBNET_")) {
                        g2.setColor(new Color(80, 80, 200, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.CYAN);
                        g2.drawString("🌐 " + tag, x + 2, y + 24);
                    }
// 新增：链路渲染
                    else if (tag.equals("LINK_UP")) {
                        g2.setColor(new Color(0, 255, 0, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.GREEN);
                        g2.drawString("🔗 链路↑", x + 4, y + 24);
                    }
                    else if (tag.equals("LINK_DOWN")) {
                        g2.setColor(new Color(255, 0, 0, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.RED);
                        g2.drawString("🔗 链路↓", x + 4, y + 24);
                    }
// 新增：接收端额外工厂
                    else if (tag.equals("RX_ETH")) {
                        g2.setColor(new Color(0, 100, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("📡 解ETH", x + 4, y + 24);
                    }
                    else if (tag.equals("RX_FCS")) {
                        g2.setColor(new Color(0, 150, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("✓ FCS", x + 6, y + 24);
                    }
                    else if (tag.equals("RX_FRAG")) {
                        g2.setColor(new Color(200, 100, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("🧩 分片重组", x + 2, y + 24);
                    }
                    else if (tag.equals("RX_PORT")) {
                        g2.setColor(new Color(150, 0, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("🔌 解端口", x + 4, y + 24);
                    }
                }
            }

            for (OreCart c : oreCarts) {
                g2.setColor(c.oreType.equals("HELLO") ? Color.CYAN : Color.GREEN);
                g2.fillOval((int) c.x - 5, (int) c.y - 5, 10, 10);
            }

            for (DataCart cart : dataCarts) {
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