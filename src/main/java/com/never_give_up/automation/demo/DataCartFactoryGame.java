package com.never_give_up.automation.demo;

import com.never_give_up.automation.demo.adapter.FactoryManager;
import com.never_give_up.automation.demo.adapter.PacketAdapter;
import com.never_give_up.automation.demo.core.ProtocolConst;
import com.never_give_up.automation.demo.factory.application.Http23PacketFactory;
import com.never_give_up.automation.demo.factory.application.NtpPacketFactory;
import com.never_give_up.automation.demo.factory.application.SnmpPacketFactory;
import com.never_give_up.automation.demo.factory.application.auth.DiameterPacketFactory;
import com.never_give_up.automation.demo.factory.application.auth.RadiusPacketFactory;
import com.never_give_up.automation.demo.factory.application.dhcp.DhcpLeaseFactory;
import com.never_give_up.automation.demo.factory.application.dns.DnsZoneFactory;
import com.never_give_up.automation.demo.factory.application.ftp.*;
import com.never_give_up.automation.demo.factory.application.http.*;
import com.never_give_up.automation.demo.factory.application.ldap.LdapPacketFactory;
import com.never_give_up.automation.demo.factory.application.mail.ImapPacketFactory;
import com.never_give_up.automation.demo.factory.application.mail.Pop3PacketFactory;
import com.never_give_up.automation.demo.factory.application.mail.SmtpPacketFactory;
import com.never_give_up.automation.demo.factory.application.rtp.RtcpPacketFactory;
import com.never_give_up.automation.demo.factory.application.rtp.RtpPacketFactory;
import com.never_give_up.automation.demo.factory.application.sip.SipPacketFactory;
import com.never_give_up.automation.demo.factory.application.ssh.SshPacketFactory;
import com.never_give_up.automation.demo.factory.application.telnet.TelnetPacketFactory;
import com.never_give_up.automation.demo.factory.attack.TransportAttackFactory;
import com.never_give_up.automation.demo.factory.balance.*;
import com.never_give_up.automation.demo.factory.capture.PacketCaptureFactory;
import com.never_give_up.automation.demo.factory.event.EventFactory;
import com.never_give_up.automation.demo.factory.flow.FlowFactory;
import com.never_give_up.automation.demo.factory.function.NatMappingFactory;
import com.never_give_up.automation.demo.factory.icmp.IcmpPingFactory;
import com.never_give_up.automation.demo.factory.icmp.IcmpTracerouteFactory;
import com.never_give_up.automation.demo.factory.link.*;
import com.never_give_up.automation.demo.factory.log.LogFactory;
import com.never_give_up.automation.demo.factory.monitor.IpfixFactory;
import com.never_give_up.automation.demo.factory.monitor.NetFlowFactory;
import com.never_give_up.automation.demo.factory.monitor.SflowFactory;
import com.never_give_up.automation.demo.factory.multicast.DvmrpFactory;
import com.never_give_up.automation.demo.factory.multicast.MldFactory;
import com.never_give_up.automation.demo.factory.multicast.MulticastRoutingFactory;
import com.never_give_up.automation.demo.factory.multicast.PimSmFactory;
import com.never_give_up.automation.demo.factory.nat.NatHairpinningFactory;
import com.never_give_up.automation.demo.factory.nat.NatHolePunchFactory;
import com.never_give_up.automation.demo.factory.nat.PcpFactory;
import com.never_give_up.automation.demo.factory.nat.UpnpFactory;
import com.never_give_up.automation.demo.factory.network.arp.ArpTableFactory;
import com.never_give_up.automation.demo.factory.network.ipv6.*;
import com.never_give_up.automation.demo.factory.physical.BitStreamFactory;
import com.never_give_up.automation.demo.factory.physical.PhysicalChannelFactory;
import com.never_give_up.automation.demo.factory.qos.QosTrafficFactory;
import com.never_give_up.automation.demo.factory.qos.SchedulerFactory;
import com.never_give_up.automation.demo.factory.route.BgpPacketFactory;
import com.never_give_up.automation.demo.factory.route.ForwardingEngineFactory;
import com.never_give_up.automation.demo.factory.route.OspfPacketFactory;
import com.never_give_up.automation.demo.factory.security.*;
import com.never_give_up.automation.demo.factory.security.crypto.*;
import com.never_give_up.automation.demo.factory.security.ipsec.IpsecAhFactory;
import com.never_give_up.automation.demo.factory.security.ipsec.IpsecEspFactory;
import com.never_give_up.automation.demo.factory.security.ipsec.IpsecFactory;
import com.never_give_up.automation.demo.factory.security.ipsec.IpsecIkeFactory;
import com.never_give_up.automation.demo.factory.security.tls.DtlsFactory;
import com.never_give_up.automation.demo.factory.session.SessionTableFactory;
import com.never_give_up.automation.demo.factory.socket.Socket;
import com.never_give_up.automation.demo.factory.socket.SocketFactory;
import com.never_give_up.automation.demo.factory.stat.StatisticsFactory;
import com.never_give_up.automation.demo.factory.tool.*;
import com.never_give_up.automation.demo.factory.transition.Nat64Factory;
import com.never_give_up.automation.demo.factory.transport.TcpReassemblyFactory;
import com.never_give_up.automation.demo.factory.transport.tcp.*;
import com.never_give_up.automation.demo.factory.vpn.L2tpFactory;
import com.never_give_up.automation.demo.factory.vpn.OpenVpnFactory;
import com.never_give_up.automation.demo.factory.vpn.SstpFactory;
import com.never_give_up.automation.demo.factory.vpn.WireguardFactory;
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
import java.awt.geom.AffineTransform;
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
    // 在 DataCartFactoryGame 类中添加区域定义枚举和配置
    private enum BuildingZone {
        ZONE_APPLICATION("📡 应用层", new Color(0, 200, 200, 50), new Color(0, 200, 200)),
        ZONE_TRANSPORT("🔌 传输层 (TCP/UDP)", new Color(255, 165, 0, 40), new Color(255, 165, 0)),
        ZONE_NETWORK("🌐 网络层 (IP/ICMP)", new Color(255, 255, 0, 40), new Color(255, 255, 0)),
        ZONE_LINK("🔗 链路层 (Ethernet/ARP)", new Color(0, 160, 255, 40), new Color(0, 160, 255)),
        ZONE_PHYSICAL("⚡ 物理层", new Color(200, 200, 200, 40), new Color(200, 200, 200)),
        ZONE_SECURITY("🛡️ 安全防护", new Color(255, 80, 80, 50), new Color(255, 80, 80)),
        ZONE_NETWORK_DEVICE("🔌 网络设备", new Color(100, 100, 200, 40), new Color(100, 100, 200)),
        ZONE_QOS("🎯 QoS/拥塞控制", new Color(100, 200, 100, 40), new Color(100, 200, 100)),
        ZONE_GATEWAY("🚪 网关/NAT", new Color(255, 140, 0, 40), new Color(255, 140, 0)),
        ZONE_ROUTER("📡 路由协议", new Color(200, 100, 0, 40), new Color(200, 100, 0)),
        ZONE_DHCP("📡 DHCP", new Color(100, 100, 255, 40), new Color(100, 100, 255)),
        ZONE_DNS("🌐 DNS", new Color(0, 200, 200, 40), new Color(0, 200, 200)),
        ZONE_IPV6("🌍 IPv6", new Color(80, 80, 180, 40), new Color(80, 80, 180)),
        ZONE_VPN("🔒 VPN隧道", new Color(100, 80, 160, 40), new Color(100, 80, 160)),
        ZONE_ENCRYPTION("🔐 加密证书", new Color(180, 180, 100, 40), new Color(180, 180, 100)),
        ZONE_MONITOR("📊 监控管理", new Color(80, 180, 200, 40), new Color(80, 180, 200)),
        ZONE_MULTICAST("📡 组播路由", new Color(180, 80, 180, 40), new Color(180, 80, 180)),
        ZONE_DIAGNOSTIC("🔧 诊断工具", new Color(150, 150, 150, 40), new Color(150, 150, 150)),
        ZONE_CORE("🏛️ 核心服务", new Color(120, 120, 120, 40), new Color(120, 120, 120)),
        ZONE_MINING("⛏️ 采矿/数据源", new Color(40, 100, 220, 40), new Color(40, 100, 220)),
        ZONE_APPLICATION_PROTOCOL("📨 应用协议", new Color(100, 150, 200, 40), new Color(100, 150, 200)),
        ZONE_LINK_ENHANCE("🔗 链路增强", new Color(0, 140, 140, 40), new Color(0, 140, 140)),
        ZONE_NAT_ENHANCE("🌍 NAT增强", new Color(200, 120, 60, 40), new Color(200, 120, 60)),
        ZONE_LOAD_BALANCE("⚖️ 负载均衡", new Color(200, 100, 80, 40), new Color(200, 100, 80)),
        ZONE_ACCESS_CONTROL("🚫 访问控制", new Color(180, 100, 120, 40), new Color(180, 100, 120));

        final String name;
        final Color bgColor;
        final Color borderColor;

        BuildingZone(String name, Color bgColor, Color borderColor) {
            this.name = name;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
        }
    }

    // 添加建筑到区域的映射
    private Map<String, BuildingZone> buildingZoneMap = new HashMap<>();

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

    private int udpPacketsSent = 0;  // UDP 已发送包计数
    // 在类的字段声明区域添加
    private double canvasScale = 1.0;      // 当前缩放比例
    private final double MIN_SCALE = 0.5;   // 最小缩放比例
    private final double MAX_SCALE = 3.0;   // 最大缩放比例
    private final double SCALE_STEP = 0.1;  // 每次滚轮缩放步长
    // ===== 新增：拖拽平移相关字段 =====
    private int viewOffsetX = 0;     // 视图水平偏移（逻辑坐标）
    private int viewOffsetY = 0;     // 视图垂直偏移（逻辑坐标）
    private int lastDragX = 0;       // 上次拖拽的X坐标
    private int lastDragY = 0;       // 上次拖拽的Y坐标
    private boolean isDragging = false;  // 是否正在拖拽
    private boolean demoCompleted = false;
    private FactoryManager factoryManager;
    private PacketAdapter packetAdapter;
    // FTP 相关
    private boolean ftpDemoEnabled = false;
    private boolean ftpLoginSent = false;
    private boolean ftpPassSent = false;
    private boolean ftpLoggedIn = false;
    private int ftpDataPort = 0;
    private boolean ftpPasvMode = false;
    private boolean ftpListSent = false;
    private String ftpCurrentDir = "/";
    // ===================== 新增 14 个工厂实例 =====================
    private BitStreamFactory bitStreamFactory;
    private PhysicalChannelFactory physicalChannelFactory;
    private PppoeFactory pppoeFactory;
    private MacSecFactory macSecFactory;
    private OspfPacketFactory ospfPacketFactory;
    private BgpPacketFactory bgpPacketFactory;
    private QosTrafficFactory qosTrafficFactory;
    private Nat64Factory nat64Factory;
    private TcpReassemblyFactory tcpReassemblyFactory;
    private TransportAttackFactory transportAttackFactory;
    private NtpPacketFactory ntpPacketFactory;
    private SnmpPacketFactory snmpPacketFactory;
    private Http23PacketFactory http23PacketFactory;
    private IpsecFactory ipsecFactory;

    // ========== 新增：各层工厂实例 ==========
    private com.never_give_up.automation.demo.factory.transport.TcpPacketFactory tcpFactory;
    private com.never_give_up.automation.demo.factory.network.IpPacketFactory ipFactory;
    private EthernetFactory ethernetFactory;
    private com.never_give_up.automation.demo.factory.address.PortFactory portFactory;

    // ===================== 新增：IPv6 协议栈工厂 =====================
    private Ipv6PacketFactory ipv6PacketFactory;
    private Ipv6FragmentFactory ipv6FragmentFactory;
    private Ipv6OptionFactory ipv6OptionFactory;
    private Ipv6NeighborDiscovery ipv6NeighborDiscovery;

    // ===================== 新增：多播路由工厂 =====================
    private PimSmFactory pimSmFactory;
    private MldFactory mldFactory;
    private DvmrpFactory dvmrpFactory;

    // ===================== 新增：TCP 增强工厂 =====================
    private TcpKeepAliveFactory tcpKeepAliveFactory;
    private TcpSackFactory tcpSackFactory;
    private TcpEcnFactory tcpEcnFactory;
    private TcpFastOpenFactory tcpFastOpenFactory;

    // ===================== 新增：链路层增强工厂 =====================
    private LldpFactory lldpFactory;
    private StpFactory stpFactory;
    private LACPFactory lacpFactory;
    private MplsFactory mplsFactory;

    // ===================== 新增：应用层协议工厂 =====================
    private FtpPacketFactory ftpPacketFactory;
    private SmtpPacketFactory smtpPacketFactory;
    private Pop3PacketFactory pop3PacketFactory;
    private ImapPacketFactory imapPacketFactory;
    private SshPacketFactory sshPacketFactory;
    private TelnetPacketFactory telnetPacketFactory;
    private RtpPacketFactory rtpPacketFactory;
    private RtcpPacketFactory rtcpPacketFactory;
    private SipPacketFactory sipPacketFactory;
    private RadiusPacketFactory radiusPacketFactory;
    private DiameterPacketFactory diameterPacketFactory;
    private LdapPacketFactory ldapPacketFactory;

    // ===================== 新增：NAT 增强工厂 =====================
    private NatHairpinningFactory natHairpinningFactory;
    private NatHolePunchFactory natHolePunchFactory;
    private UpnpFactory upnpFactory;
    private PcpFactory pcpFactory;

    // ===================== 新增：负载均衡工厂 =====================
    private LbRoundRobinFactory lbRoundRobinFactory;
    private LbLeastConnFactory lbLeastConnFactory;
    private LbIpHashFactory lbIpHashFactory;
    private LbHealthCheckFactory lbHealthCheckFactory;

    // ===================== 新增：监控管理工厂 =====================
    private NetFlowFactory netFlowFactory;
    private SflowFactory sflowFactory;
    private IpfixFactory ipfixFactory;
    private IcmpPingFactory icmpPingFactory;
    private IcmpTracerouteFactory icmpTracerouteFactory;

    // ===================== 新增：VPN 隧道工厂 =====================
    private IpsecIkeFactory ipsecIkeFactory;
    private IpsecEspFactory ipsecEspFactory;
    private IpsecAhFactory ipsecAhFactory;
    private OpenVpnFactory openVpnFactory;
    private WireguardFactory wireguardFactory;
    private L2tpFactory l2tpFactory;
    private SstpFactory sstpFactory;

    // ===================== 新增：安全防火墙工厂 =====================
    private DpiFactory dpiFactory;
    private IpsFactory ipsFactory;
    private WafFactory wafFactory;
    private DdosMitigationFactory ddosMitigationFactory;
    private RateLimitFactory rateLimitFactory;

    // ===================== 新增：加密证书工厂 =====================
    private X509Factory x509Factory;
    private CrlFactory crlFactory;
    private OcspFactory ocspFactory;
    private PkiFactory pkiFactory;
    private DtlsFactory dtlsFactory;
//添加 FTP 响应确认标志
    private boolean ftpUserAcked = false;
    private boolean ftpPassAcked = false;

    // ===================== 新增：访问控制工厂 =====================
    private AclFactory aclFactory;
    private MacAuthFactory macAuthFactory;
    private Dot1xFactory dot1xFactory;

    // ===================== 新增：诊断工具工厂 =====================
    private NetstatFactory netstatFactory;
    private IpconfigFactory ipconfigFactory;
    private RoutePrintFactory routePrintFactory;
    private NslookupFactory nslookupFactory;
    private ArpCommandFactory arpCommandFactory;
    private TelnetClientFactory telnetClientFactory;
    private CurlFactory curlFactory;
    private WgetFactory wgetFactory;


    // ===================== 新增 20 个核心工厂实例 =====================
    private SocketFactory socketFactory;
    private TcpStateMachineFactory tcpStateMachineFactory;
    private MacTableFactory macTableFactory;
    private CamTableFactory camTableFactory;
    private ForwardingEngineFactory forwardingEngineFactory;
    private SessionTableFactory sessionTableFactory;
    private FlowFactory flowFactory;
    private LoadBalancerFactory loadBalancerFactory;
    private SchedulerFactory schedulerFactory;
    private DnsZoneFactory dnsZoneFactory;
    private DhcpLeaseFactory dhcpLeaseFactory;
    private ArpTableFactory arpTableFactory;
    private NeighborTableFactory neighborTableFactory;
    private MulticastRoutingFactory multicastRoutingFactory;
    private MplsLabelFactory mplsLabelFactory;
    private CertificateStoreFactory certificateStoreFactory;
    private EventFactory eventFactory;
    private StatisticsFactory statisticsFactory;
    private LogFactory logFactory;
    private PacketCaptureFactory packetCaptureFactory;

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
    private final int WAN_BOTTLE_NECK_MAX = 20; // 增加公网容量
    private long stateTimerWatchdog = 0;
    private final long RTO_TIMEOUT = 500000;
    private int nextSeqNum = 100;
    private long lastProbeTime = 0;
    private final long PROBE_INTERVAL = 3000;

    private int dnsRetryCount = 0;
    private long lastDnsQueryTime = 0;
    private final long DNS_TIMEOUT = 10000; // 10秒超时

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
    private boolean httpSubDemoActive = false;  // HTTP 子工厂测试激活
    private int httpSubDemoIndex = 0;           // 当前测试的 stage 偏移 (0-10)
    private boolean tlsEnabled = false;
    private java.security.KeyPair serverRsaKeyPair;
    private Cipher rsaCipher;
    private javax.crypto.SecretKey sessionKey;
    private Cipher aesCipher;
    private boolean tlsCipherReady = false;
    private TlsState tlsState = TlsState.IDLE;
    private boolean udpActive = false;
    private int udpSeqToSend = 0;
    private long lastUdpSendTime = 0;

    private boolean httpSent = false;
    private String httpResponseContent = "";

    // 添加一个方法来获取当前缩放后的坐标/尺寸（用于坐标转换）
    public int scaleX(int x) {
        return (int) (x * canvasScale);
    }

    public int scaleSize(int size) {
        return (int) (size * canvasScale);
    }

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
        ethernetFactory = new EthernetFactory();
        portFactory = new com.never_give_up.automation.demo.factory.address.PortFactory();
        // ===================== 初始化 14 个新工厂 =====================
        this.bitStreamFactory = factoryManager.getBitStreamFactory();
        this.physicalChannelFactory = factoryManager.getPhysicalChannelFactory();
        this.pppoeFactory = factoryManager.getPppoeFactory();
        this.macSecFactory = factoryManager.getMacSecFactory();
        this.ospfPacketFactory = factoryManager.getOspfPacketFactory();
        this.bgpPacketFactory = factoryManager.getBgpPacketFactory();
        this.qosTrafficFactory = factoryManager.getQosTrafficFactory();
        this.nat64Factory = factoryManager.getNat64Factory();
        this.tcpReassemblyFactory = factoryManager.getTcpReassemblyFactory();
        this.transportAttackFactory = factoryManager.getTransportAttackFactory();
        this.ntpPacketFactory = factoryManager.getNtpPacketFactory();
        this.snmpPacketFactory = factoryManager.getSnmpPacketFactory();
        this.http23PacketFactory = factoryManager.getHttp23PacketFactory();
        this.ipsecFactory = factoryManager.getIpsecFactory();

        // ===================== 初始化新增工厂 =====================
        this.ipv6PacketFactory = factoryManager.getIpv6PacketFactory();
        this.ipv6FragmentFactory = factoryManager.getIpv6FragmentFactory();
        this.ipv6OptionFactory = factoryManager.getIpv6OptionFactory();
        this.ipv6NeighborDiscovery = factoryManager.getIpv6NeighborDiscovery();

        this.pimSmFactory = factoryManager.getPimSmFactory();
        this.mldFactory = factoryManager.getMldFactory();
        this.dvmrpFactory = factoryManager.getDvmrpFactory();

        this.tcpKeepAliveFactory = factoryManager.getTcpKeepAliveFactory();
        this.tcpSackFactory = factoryManager.getTcpSackFactory();
        this.tcpEcnFactory = factoryManager.getTcpEcnFactory();
        this.tcpFastOpenFactory = factoryManager.getTcpFastOpenFactory();

        this.lldpFactory = factoryManager.getLldpFactory();
        this.stpFactory = factoryManager.getStpFactory();
        this.lacpFactory = factoryManager.getLacpFactory();
        this.mplsFactory = factoryManager.getMplsFactory();

        this.ftpPacketFactory = factoryManager.getFtpPacketFactory();
        this.smtpPacketFactory = factoryManager.getSmtpPacketFactory();
        this.pop3PacketFactory = factoryManager.getPop3PacketFactory();
        this.imapPacketFactory = factoryManager.getImapPacketFactory();
        this.sshPacketFactory = factoryManager.getSshPacketFactory();
        this.telnetPacketFactory = factoryManager.getTelnetPacketFactory();
        this.rtpPacketFactory = factoryManager.getRtpPacketFactory();
        this.rtcpPacketFactory = factoryManager.getRtcpPacketFactory();
        this.sipPacketFactory = factoryManager.getSipPacketFactory();
        this.radiusPacketFactory = factoryManager.getRadiusPacketFactory();
        this.diameterPacketFactory = factoryManager.getDiameterPacketFactory();
        this.ldapPacketFactory = factoryManager.getLdapPacketFactory();

        this.natHairpinningFactory = factoryManager.getNatHairpinningFactory();
        this.natHolePunchFactory = factoryManager.getNatHolePunchFactory();
        this.upnpFactory = factoryManager.getUpnpFactory();
        this.pcpFactory = factoryManager.getPcpFactory();

        this.lbRoundRobinFactory = factoryManager.getLbRoundRobinFactory();
        this.lbLeastConnFactory = factoryManager.getLbLeastConnFactory();
        this.lbIpHashFactory = factoryManager.getLbIpHashFactory();
        this.lbHealthCheckFactory = factoryManager.getLbHealthCheckFactory();

        this.netFlowFactory = factoryManager.getNetFlowFactory();
        this.sflowFactory = factoryManager.getSflowFactory();
        this.ipfixFactory = factoryManager.getIpfixFactory();
        this.icmpPingFactory = factoryManager.getIcmpPingFactory();
        this.icmpTracerouteFactory = factoryManager.getIcmpTracerouteFactory();

        this.ipsecIkeFactory = factoryManager.getIpsecIkeFactory();
        this.ipsecEspFactory = factoryManager.getIpsecEspFactory();
        this.ipsecAhFactory = factoryManager.getIpsecAhFactory();
        this.openVpnFactory = factoryManager.getOpenVpnFactory();
        this.wireguardFactory = factoryManager.getWireguardFactory();
        this.l2tpFactory = factoryManager.getL2tpFactory();
        this.sstpFactory = factoryManager.getSstpFactory();

        this.dpiFactory = factoryManager.getDpiFactory();
        this.ipsFactory = factoryManager.getIpsFactory();
        this.wafFactory = factoryManager.getWafFactory();
        this.ddosMitigationFactory = factoryManager.getDdosMitigationFactory();
        this.rateLimitFactory = factoryManager.getRateLimitFactory();

        this.x509Factory = factoryManager.getX509Factory();
        this.crlFactory = factoryManager.getCrlFactory();
        this.ocspFactory = factoryManager.getOcspFactory();
        this.pkiFactory = factoryManager.getPkiFactory();
        this.dtlsFactory = factoryManager.getDtlsFactory();

        this.aclFactory = factoryManager.getAclFactory();
        this.macAuthFactory = factoryManager.getMacAuthFactory();
        this.dot1xFactory = factoryManager.getDot1xFactory();

        this.netstatFactory = factoryManager.getNetstatFactory();
        this.ipconfigFactory = factoryManager.getIpconfigFactory();
        this.routePrintFactory = factoryManager.getRoutePrintFactory();
        this.nslookupFactory = factoryManager.getNslookupFactory();
        this.arpCommandFactory = factoryManager.getArpCommandFactory();
        this.telnetClientFactory = factoryManager.getTelnetClientFactory();
        this.curlFactory = factoryManager.getCurlFactory();
        this.wgetFactory = factoryManager.getWgetFactory();

        // ===================== 初始化 20 个核心工厂 =====================
        this.socketFactory = factoryManager.getSocketFactory();
        this.tcpStateMachineFactory = factoryManager.getTcpStateMachineFactory();
        this.macTableFactory = factoryManager.getMacTableFactory();
        this.camTableFactory = factoryManager.getCamTableFactory();
        this.forwardingEngineFactory = factoryManager.getForwardingEngineFactory();
        this.sessionTableFactory = factoryManager.getSessionTableFactory();
        this.flowFactory = factoryManager.getFlowFactory();
        this.loadBalancerFactory = factoryManager.getLoadBalancerFactory();
        this.schedulerFactory = factoryManager.getSchedulerFactory();
        this.dnsZoneFactory = factoryManager.getDnsZoneFactory();
        this.dhcpLeaseFactory = factoryManager.getDhcpLeaseFactory();
        this.arpTableFactory = factoryManager.getArpTableFactory();
        this.neighborTableFactory = factoryManager.getNeighborTableFactory();
        this.multicastRoutingFactory = factoryManager.getMulticastRoutingFactory();
        this.mplsLabelFactory = factoryManager.getMplsLabelFactory();
        this.certificateStoreFactory = factoryManager.getCertificateStoreFactory();
        this.eventFactory = factoryManager.getEventFactory();
        this.statisticsFactory = factoryManager.getStatisticsFactory();
        this.logFactory = factoryManager.getLogFactory();
        this.packetCaptureFactory = factoryManager.getPacketCaptureFactory();

// 初始化日志工厂，记录启动事件
        if (logFactory != null) {
            logFactory.log("模拟器启动，网络设备工厂初始化完成");
        }
        if (statisticsFactory != null) {
            statisticsFactory.reset();
        }

        stateTimerWatchdog = System.currentTimeMillis();
        try {
            initCrypto();
        } catch (Exception e) {
            throw new RuntimeException("加密组件初始化失败", e);
        }
        initMap();
        initZoneMapping();
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

//        canvas = new GameCanvas();
//        add(canvas, BorderLayout.CENTER);
        // 找到创建 canvas 的位置
        canvas = new GameCanvas();
        canvas.setPreferredSize(new Dimension(MAP_COLS * TILE_SIZE, MAP_ROWS * TILE_SIZE));
        canvas.setBackground(new Color(18, 20, 26));
// 创建带滚动条的面板
        JScrollPane mapScrollPane = new JScrollPane(canvas);
        mapScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mapScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mapScrollPane.getHorizontalScrollBar().setUnitIncrement(40);
        mapScrollPane.getVerticalScrollBar().setUnitIncrement(40);
// 添加鼠标拖拽监听器到 canvas
        // 添加鼠标监听器到 canvas
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
                        // 没有拖拽，是点击事件，处理建筑放置
                        // 关键：使用 getLogicalX/Y 方法，传入鼠标屏幕坐标
                        int logicalX = canvas.getLogicalX(e.getX());
                        int logicalY = canvas.getLogicalY(e.getY());

                        if (logicalX >= 0 && logicalY >= 0) {
                            int col = logicalX / TILE_SIZE;
                            int row = logicalY / TILE_SIZE;
                            if (row >= 0 && row < MAP_ROWS && col >= 0 && col < MAP_COLS) {
                                DataCart cart = getDataCartAtPoint(new Point(logicalX, logicalY));
                                if (cart != null) return;

                                String existing = buildingLayout[row][col];
                                if (!existing.equals("NONE") || mapLayout[row][col] == 9) return;

                                if (selectedBuilding.startsWith("MINER_")) {
                                    int reqType = selectedBuilding.equals("MINER_H") ? 1 : 2;
                                    if (mapLayout[row][col] == reqType && funds >= PRICE_MINER) {
                                        funds -= PRICE_MINER;
                                        buildingLayout[row][col] = selectedBuilding;
                                        appendToConsole(String.format("【🏗️ 建造】: 放置 %s 矿机，花费 %d", selectedBuilding, PRICE_MINER));
                                    }
                                } else {
                                    if (mapLayout[row][col] == 0 && funds >= PRICE_MACHINE) {
                                        funds -= PRICE_MACHINE;
                                        buildingLayout[row][col] = selectedBuilding;
                                        appendToConsole(String.format("【🏗️ 建造】: 放置 %s，花费 %d", selectedBuilding, PRICE_MACHINE));
                                    }
                                }
                                updateTopLabel();
                                canvas.repaint();
                            }
                        }
                    }
                    isDragging = false;
                    canvas.setCursor(Cursor.getDefaultCursor());
                }

                // 右键删除建筑
                if (SwingUtilities.isRightMouseButton(e)) {
                    // 关键修复：使用 getLogicalX/Y 方法
                    int logicalX = canvas.getLogicalX(e.getX());
                    int logicalY = canvas.getLogicalY(e.getY());

                    if (logicalX >= 0 && logicalY >= 0) {
                        int col = logicalX / TILE_SIZE;
                        int row = logicalY / TILE_SIZE;
                        if (row >= 0 && row < MAP_ROWS && col >= 0 && col < MAP_COLS) {
                            String existing = buildingLayout[row][col];
                            if (!existing.equals("NONE") && !existing.equals("PC_FACTORY") && !existing.equals("RX_ST") && !existing.equals("DHCP_SERVER") && mapLayout[row][col] != 9) {
                                int refund = existing.startsWith("MINER") ? PRICE_MINER / 2 : PRICE_MACHINE / 2;
                                funds += refund;
                                buildingLayout[row][col] = "NONE";
                                canvas.repaint();
                                updateTopLabel();
                                appendToConsole(String.format("【🗑️ 拆除】: 移除 %s，返还 %d 资金", existing, refund));
                            }
                        }
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int logicalX = canvas.getLogicalX(e.getX());
                    int logicalY = canvas.getLogicalY(e.getY());
                    DataCart cart = getDataCartAtPoint(new Point(logicalX, logicalY));
                    if (cart != null) {
                        showPacketDetails(cart);
                    }
                }
            }
        });

        canvas.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isDragging) {
                    int deltaX = e.getX() - lastDragX;
                    int deltaY = e.getY() - lastDragY;

                    // 更新视图偏移
                    viewOffsetX += deltaX;
                    viewOffsetY += deltaY;

                    // 可选：添加上下限限制（防止拖出边界）
                    // viewOffsetX = Math.min(Math.max(viewOffsetX, -maxOffsetX), maxOffsetX);

                    lastDragX = e.getX();
                    lastDragY = e.getY();
                    canvas.repaint();
                }
            }
        });

// 添加滚轮缩放监听器
        canvas.addMouseWheelListener(e -> {
            if (e.isControlDown()) {
                double oldScale = canvasScale;
                int rotation = e.getWheelRotation();
                if (rotation < 0) {
                    canvasScale = Math.min(MAX_SCALE, canvasScale + SCALE_STEP);
                } else {
                    canvasScale = Math.max(MIN_SCALE, canvasScale - SCALE_STEP);
                }

                if (oldScale != canvasScale) {
                    // 缩放时，调整偏移量以保持鼠标位置为中心的缩放效果
                    if (e.getSource() instanceof Component) {
                        Point mousePos = e.getPoint();
                        // 计算缩放前鼠标指向的逻辑坐标
                        double logicalX = (mousePos.x - viewOffsetX) / oldScale;
                        double logicalY = (mousePos.y - viewOffsetY) / oldScale;
                        // 缩放后，使相同的逻辑坐标仍位于鼠标位置
                        viewOffsetX = (int) (mousePos.x - logicalX * canvasScale);
                        viewOffsetY = (int) (mousePos.y - logicalY * canvasScale);
                    }

                    // 更新画布尺寸（可选，如果不需要自动滚动条可以跳过）
                    int newWidth = (int) (MAP_COLS * TILE_SIZE * canvasScale);
                    int newHeight = (int) (MAP_ROWS * TILE_SIZE * canvasScale);
                    canvas.setPreferredSize(new Dimension(newWidth, newHeight));
                    canvas.revalidate();
                    canvas.repaint();

                    appendToConsole(String.format("【🔍 画布缩放】: %.1f倍 (Ctrl+滚轮)", canvasScale));
                }
            }
        });

// ===== 新增：添加鼠标滚轮缩放监听 =====
        mapScrollPane.addMouseWheelListener(e -> {
            if (e.isControlDown()) {  // Ctrl + 滚轮缩放（常见习惯）
                double oldScale = canvasScale;
                int rotation = e.getWheelRotation();
                if (rotation < 0) {
                    // 向上滚动，放大
                    canvasScale = Math.min(MAX_SCALE, canvasScale + SCALE_STEP);
                } else {
                    // 向下滚动，缩小
                    canvasScale = Math.max(MIN_SCALE, canvasScale - SCALE_STEP);
                }

                if (oldScale != canvasScale) {
                    // 更新画布大小
                    int newWidth = (int) (MAP_COLS * TILE_SIZE * canvasScale);
                    int newHeight = (int) (MAP_ROWS * TILE_SIZE * canvasScale);
                    canvas.setPreferredSize(new Dimension(newWidth, newHeight));
                    canvas.revalidate();
                    canvas.repaint();

                    // 可选：在控制台显示当前缩放比例
                    appendToConsole(String.format("【🔍 画布缩放】: %.1f倍 (Ctrl+滚轮)", canvasScale));
                }
            }
        });

// 添加提示标签（可选）
        JPanel mapPanelWithHint = new JPanel(new BorderLayout());
        mapPanelWithHint.add(mapScrollPane, BorderLayout.CENTER);
        JLabel hintLabel = new JLabel("  💡 提示：按住 Ctrl 键 + 滚动鼠标滚轮可缩放地图", JLabel.LEFT);
        hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        hintLabel.setForeground(Color.GRAY);
        mapPanelWithHint.add(hintLabel, BorderLayout.SOUTH);

// 替换原来的 add(mapScrollPane, BorderLayout.CENTER)
        add(mapPanelWithHint, BorderLayout.CENTER);
// 使用 JScrollPane 包裹 canvas
        mapScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        mapScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        mapScrollPane.getHorizontalScrollBar().setUnitIncrement(40);
        mapScrollPane.getVerticalScrollBar().setUnitIncrement(40);
        mapScrollPane.setWheelScrollingEnabled(true);  // 允许滚动条滚动

// 可选：添加提示标签
        mapPanelWithHint.add(mapScrollPane, BorderLayout.CENTER);
        hintLabel.setFont(new Font("微软雅黑", Font.PLAIN, 11));
        hintLabel.setForeground(Color.GRAY);
        mapPanelWithHint.add(hintLabel, BorderLayout.SOUTH);

        add(mapPanelWithHint, BorderLayout.CENTER);
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
        JButton copyConsoleButton = new JButton("📋 复制日志");
        copyConsoleButton.addActionListener(e -> copyConsoleLog());
        copyConsoleButton.setToolTipText("复制控制台所有日志到剪贴板");
        JButton pingButton = new JButton("📡 PING");
        pingButton.addActionListener(e -> sendPing());
        JButton tracerouteButton = new JButton("🔎 TRACEROUTE");
        tracerouteButton.addActionListener(e -> startTraceroute());

        // ========== 新增 FTP 复选框 ==========
        JCheckBox cbFtp = new JCheckBox("📁 FTP 演示", false);
        cbFtp.addActionListener(e -> {
            ftpDemoEnabled = cbFtp.isSelected();
            if (ftpDemoEnabled) {
                // 如果启用FTP，自动禁用HTTP演示避免冲突
                if (cbHttp.isSelected()) {
                    cbHttp.setSelected(false);
                    httpDemoEnabled = false;
                }
                appendToConsole("【📁 FTP】: 启用FTP演示模式");
                resetTcpSession();
            } else {
                appendToConsole("【📁 FTP】: 关闭FTP演示模式");
                resetTcpSession();
            }
        });
// ========== FTP 复选框结束 ==========
        btnPanel.add(cbFtp);//添加FTP
        btnPanel.add(resetButton);
        btnPanel.add(clearArpButton);
        btnPanel.add(clearDnsButton);
        btnPanel.add(clearConsoleButton);
        btnPanel.add(copyConsoleButton);  // 新增复制按钮
        btnPanel.add(pingButton);
        btnPanel.add(tracerouteButton);
        // HTTP 子工厂测试按钮
        JButton httpSubTestBtn = new JButton("🧪 HTTP 子工厂");
        httpSubTestBtn.addActionListener(e -> {
            if (!httpSubDemoActive) {
                httpSubDemoActive = true;
                httpSubDemoIndex = 0;
                appendToConsole("【🧪 HTTP 子工厂测试】: 开始遍历 Stage 166-176");
                String[] httpTags = {"HTTP_REQ","HTTP_RES","HTTP_HDR","HTTP_BODY","HTTP_CK",
                                     "HTTP_AUTH","HTTP_CACHE","HTTP_CHUNK","HTTP2_FRAME","HTTP2_SET","HTTP2_STR"};
                javax.swing.Timer demoStep = new javax.swing.Timer(1000, null);
                demoStep.addActionListener(ev -> {
                    if (httpSubDemoIndex <= 10) {
                        int stage = 166 + httpSubDemoIndex;
                        DataCart hc = new DataCart(pcFactory.x, pcFactory.y, "HTTP_DEMO", 0);
                        hc.stage = stage;
                        pendingDataCarts.add(hc);
                        appendToConsole("  -> 派出 cart, stage=" + stage + " (" + httpTags[httpSubDemoIndex] + ")");
                        httpSubDemoIndex++;
                    } else {
                        httpSubDemoActive = false;
                        httpSubDemoIndex = 0;
                        appendToConsole("【🧪 HTTP 子工厂测试】: 全部 Stage 遍历完成!");
                        ((javax.swing.Timer) ev.getSource()).stop();
                    }
                });
                demoStep.start();
            }
        });
        btnPanel.add(httpSubTestBtn);
        btnPanel.add(rbTcp);
        btnPanel.add(rbUdp);
        btnPanel.add(cbHttp);
        btnPanel.add(cbTls);
        bottomPanel.add(btnPanel, BorderLayout.SOUTH);
        add(bottomPanel, BorderLayout.SOUTH);

        buildShopUI();

// 在 canvas 的 MouseAdapter 中修改坐标处理
        canvas.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 右键删除建筑（需要正确转换坐标）
                if (SwingUtilities.isRightMouseButton(e)) {
                    // 关键修复：使用 getLogicalX/Y 方法转换坐标
                    // 注意：这里需要传入缩放后的实际屏幕坐标
                    int logicalX = canvas.getLogicalX(e.getX());
                    int logicalY = canvas.getLogicalY(e.getY());

                    // 边界检查
                    if (logicalX < 0 || logicalY < 0) return;

                    int col = logicalX / TILE_SIZE;
                    int row = logicalY / TILE_SIZE;

                    if (row >= 0 && row < MAP_ROWS && col >= 0 && col < MAP_COLS) {
                        String existing = buildingLayout[row][col];
                        // 不能删除 PC 工厂、服务器和网关边界
                        if (!existing.equals("NONE") && !existing.equals("PC_FACTORY") && !existing.equals("RX_ST") && !existing.equals("DHCP_SERVER")) {
                            // 检查是否是网关边界（mapLayout[row][col] == 9）
                            if (mapLayout[row][col] != 9) {
                                int refund = 0;
                                if (existing.startsWith("MINER_H") || existing.startsWith("MINER_S")) {
                                    refund = PRICE_MINER / 2;
                                } else {
                                    refund = PRICE_MACHINE / 2;
                                }
                                funds += refund;
                                buildingLayout[row][col] = "NONE";
                                canvas.repaint();
                                updateTopLabel();
                                appendToConsole(String.format("【🗑️ 拆除】: 移除 %s，返还 %d 资金", existing, refund));
                            }
                        }
                    }
                    return;
                }

                // 左键 - 延迟判断拖拽，建筑放置在 mouseReleased 中
                if (SwingUtilities.isLeftMouseButton(e)) {
                    // 不做任何事，让 mouseReleased 处理
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && !isDragging) {
                    // 没有拖拽，是点击事件，处理建筑放置
                    int logicalX = canvas.getLogicalX(e.getX());
                    int logicalY = canvas.getLogicalY(e.getY());

                    if (logicalX < 0 || logicalY < 0) return;

                    int col = logicalX / TILE_SIZE;
                    int row = logicalY / TILE_SIZE;
                    if (row >= 0 && row < MAP_ROWS && col >= 0 && col < MAP_COLS) {
                        // 获取数据包（优先处理，避免在数据包上建建筑）
                        DataCart cart = getDataCartAtPoint(new Point(logicalX, logicalY));
                        if (cart != null) {
                            return;
                        }

                        String existing = buildingLayout[row][col];
                        // 不能建在已有建筑上，也不能建在网关边界
                        if (!existing.equals("NONE") || mapLayout[row][col] == 9) return;

                        if (selectedBuilding.startsWith("MINER_")) {
                            int reqType = selectedBuilding.equals("MINER_H") ? 1 : 2;
                            if (mapLayout[row][col] == reqType && funds >= PRICE_MINER) {
                                funds -= PRICE_MINER;
                                buildingLayout[row][col] = selectedBuilding;
                                appendToConsole(String.format("【🏗️ 建造】: 放置 %s 矿机，花费 %d", selectedBuilding, PRICE_MINER));
                            }
                        } else {
                            if (mapLayout[row][col] == 0 && funds >= PRICE_MACHINE) {
                                funds -= PRICE_MACHINE;
                                buildingLayout[row][col] = selectedBuilding;
                                appendToConsole(String.format("【🏗️ 建造】: 放置 %s，花费 %d", selectedBuilding, PRICE_MACHINE));
                            }
                        }
                        updateTopLabel();
                        canvas.repaint();
                    }
                }
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int logicalX = canvas.getLogicalX(e.getX());
                    int logicalY = canvas.getLogicalY(e.getY());
                    DataCart cart = getDataCartAtPoint(new Point(logicalX, logicalY));
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

    // 添加一个重置视图的方法（可选）
    private void resetView() {
        canvasScale = 1.0;
        viewOffsetX = 0;
        viewOffsetY = 0;
        canvas.setPreferredSize(new Dimension(MAP_COLS * TILE_SIZE, MAP_ROWS * TILE_SIZE));
        canvas.revalidate();
        canvas.repaint();
        appendToConsole("【🔄 视图重置】: 缩放比例恢复100%，偏移归零");
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
        // 使用 factoryManager 的 arpCache
        factoryManager.getArpCache().getCache().forEach((ip, entry) -> {
            if (!entry.isExpired(300000)) {
                arpSb.append(String.format("%s → %s\n", entry.getIpAddress(), entry.getMacAddress()));
            }
        });
        txtArpDisplay.setText(arpSb.toString());
    }


    private void updateDnsDisplay() {
        StringBuilder dnsSb = new StringBuilder();
        // 使用 factoryManager 的 dnsCache
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

        String[][] categories = {{"【1. 内网采矿与原始数据】", "MINER_H", "🔷 Hello 采矿机", "MINER_S", "🟩 Say 采矿机"}, {"【2. 应用层】", "TX_APP", "📦 应用数据载荷"}, {"【3. DNS 解析】", "DNS_CLIENT", "🔍 DNS 客户端", "DNS_LOCAL", "📡 本地 DNS", "DNS_ROOT", "🌐 根 DNS", "DNS_AUTH", "🏢 权威 DNS"}, {"【4. DHCP 客户端】", "DHCP_DISC", "🔎 Discover", "DHCP_OFFER", "📥 Offer", "DHCP_REQ", "📤 Request", "DHCP_ACK", "✅ ACK"}, {"【5. 传输层 - TCP 封装】", "T_SP", "🔩 源端口", "T_DP", "🎯 目的端口", "T_SEQ", "🔢 序列号", "T_ACK", "📜 确认号", "T_CTL", "🚩 控制位", "T_WIN", "🌊 滑动窗口", "T_CHK", "🔥 校验和", "T_CORE", "🟧 TCP 段总装"}, {"【6. 网络层 - IP 封装】", "TX_IPH", "📦 IP 首部", "TX_IP_FRAG", "✂️ IP 分片器"}, {"【7. 链路层 - Ethernet II】", "TX_ARP", "🔍 ARP 解析", "ETH_DST", "🟦 目的 MAC", "ETH_SRC", "🟦 源 MAC", "ETH_TYPE", "🟦 EtherType", "TX_LLC", "🟩 LLC", "TX_FCS", "🟩 FCS"}, {"【8. 边界网关】", "R_LAN", "🎛️ LAN 拆包", "R_TAB", "🔀 路由查表", "R_NAT", "🌍 NAT 转换", "R_WAN", "🛠️ WAN 封装"}, {"【9. 公网路由器】", "ROUTER1", "📡 Router1", "ROUTER2", "📡 Router2", "ROUTER3", "📡 Router3"}, {"【10. 接收端解封装】", "RX_LLC", "🔓 链路层解封", "RX_IP", "💛 网络层解封", "RX_TCP", "🧡 传输层解封", "RX_APP", "💚 应用层交付"}, {"【11. 网络安全设备】", "FW_IN", "🔥 入站防火墙", "FW_OUT", "🔥 出站防火墙", "IDS", "🛡️ IDS"},
                {"【12. 队列与缓冲】", "Q_IN", "📋 入队", "Q_OUT", "📋 出队", "Q_DROP", "📋 丢包器"},
                {"【13. 拥塞控制】", "CC_SLOW", "🐢 慢启动", "CC_AVOID", "🐌 拥塞避免", "CC_FAST", "⚡ 快速重传"},
                {"【14. 网络设备】", "SWITCH", "🔌 交换机", "HUB", "🔌 集线器", "BRIDGE", "🌉 网桥"},
                {"【15. 子网与链路】", "SUBNET_A", "🌐 子网A", "SUBNET_B", "🌐 子网B", "LINK_UP", "🔗 上行链路", "LINK_DOWN", "🔗 下行链路"}, {"【16. 高级协议选项 Stage 46-61】",
                "TCP_OPTION", "🔧 TCP 选项(46)",
                "IP_OPTION", "🔧 IP 选项(47)",
                "ETH_PADDING", "📦 以太网填充(48)",
                "UDP_CHECKSUM", "🔢 UDP校验和(49)",
                "ICMP_ERROR", "⚠️ ICMP错误(50)",
                "IP_FORWARD", "🔄 IP转发(51)",
                "TCP_WINDOW", "📊 TCP窗口(52)",
                "TCP_TIMER", "⏱️ TCP定时器(53)",
                "VLAN_TAG", "🏷️ VLAN(54)",
                "TUNNEL_GRE", "🔄 GRE隧道(55)",
                "IGMP_MCAST", "📡 IGMP组播(56)",
                "NDP_DISC", "📡 NDP发现(57)",
                "DNS_RECURSIVE", "🌐 DNS递归(58)",
                "DHCP_FULL", "📡 DHCP完整(59)",
                "TLS_HANDSHAKE", "🔒 TLS握手(60)",
                "SERIALIZE", "💾 序列化(61)"}, {"【20. 物理层】",
                "BIT_STREAM", "📡 比特流生成(62)",
                "PHY_CHANNEL", "📡 物理信道(63)"},
                {"【21. 链路层增强】",
                        "PPPOE", "🔌 PPPoE封装(64)",
                        "MACSEC", "🔐 MACsec加密(65)"},
                {"【22. 高级路由协议】",
                        "OSPF", "🌐 OSPF路由(66)",
                        "BGP", "🌐 BGP路由(67)"},
                {"【23. 服务质量与转换】",
                        "QOS", "🎯 QoS标记(68)",
                        "NAT64", "🌍 NAT64转换(69)"},
                {"【24. 传输层增强与安全】",
                        "TCP_REASSEMBLY", "🔧 TCP重组(70)",
                        "ATTACK", "⚠️ 攻击检测(71)",
                        "IPSEC", "🔒 IPsec安全(75)"},
                {"【25. 应用层协议】",
                        "NTP", "🕐 NTP时间同步(72)",
                        "SNMP", "📊 SNMP管理(73)",
                        "HTTP23", "📡 HTTP/2.3协议(74)"}, {"【26. IPv6 协议栈】", "IPV6", "🌐 IPv6 封装(76)", "IPV6_FRAG", "✂️ IPv6分片(77)", "IPV6_OPTION", "🔧 IPv6选项(78)", "IPV6_ND", "📡 IPv6邻居发现(79)"},
                {"【27. TCP 高级特性】", "TCP_KEEPALIVE", "🔁 Keep-Alive(80)", "TCP_SACK", "📊 SACK(81)", "TCP_ECN", "⚠️ ECN(82)", "TCP_FASTOPEN", "🚀 FastOpen(83)"},
                {"【28. 应用层协议】",
                        "FTP_AUTH", "🔐 FTP认证(161)",
                        "FTP_COMMAND", "📝 FTP命令(162)",
                        "FTP_CHANNEL", "🔌 FTP数据通道(163)",
                        "FTP_RESPONSE", "📋 FTP响应(164)",
                        "FTP_MAIN", "📁 FTP主工厂(165)",
                        "SMTP", "📧 SMTP(85)",
                        "POP3", "📬 POP3(86)",
                        "IMAP", "📨 IMAP(87)",
                        "SSH", "🔐 SSH(88)",
                        "TELNET", "💻 Telnet(89)",
                        "RTP", "🎵 RTP(90)",
                        "SIP", "📞 SIP(91)",
                        "RADIUS", "🔑 RADIUS(92)"},
                {"【29. 安全防护】", "DPI", "🔍 DPI深度检测(96)", "WAF", "🛡️ WAF防火墙(97)", "DDOS", "💥 DDoS防护(98)", "RATELIMIT", "⏱️ 速率限制(99)", "ACL", "🚫 访问控制(100)"}
                , {"【30. 核心网络服务 Stage 101-120】",
                "SOCKET", "🔌 Socket(101)",
                "TCP_STATE", "📊 TCP状态机(102)",
                "MAC_TABLE", "🔌 MAC表(103)",
                "CAM_TABLE", "📋 CAM表(104)",
                "FIB", "🔀 FIB转发表(105)",
                "SESSION_TABLE", "💬 会话表(106)",
                "FLOW", "📊 NetFlow(107)",
                "LOAD_BALANCER", "⚖️ 负载均衡(108)",
                "SCHEDULER", "🎯 QoS调度(109)",
                "DNS_ZONE", "🌐 DNS区域(110)",
                "DHCP_LEASE", "📝 DHCP租约(111)",
                "ARP_TABLE", "📋 ARP表(112)",
                "NEIGHBOR_TABLE", "📡 邻居表(113)",
                "MCAST_ROUTE", "📡 组播路由(114)",
                "MPLS_LABEL", "🏷️ MPLS标签(115)",
                "CERT_STORE", "🔐 证书库(116)",
                "EVENT", "🎬 事件引擎(117)",
                "STATS", "📈 统计收集(118)",
                "LOG", "📝 日志记录(119)",
                "PCAP", "📦 PCAP抓包(120)"}
                , {"【31. 链路层增强 Stage 121-124】",
                "LLDP", "🔍 LLDP链路发现(121)",
                "STP", "🌲 STP生成树(122)",
                "LACP", "🔗 LACP链路聚合(123)",
                "MPLS", "🏷️ MPLS标签交换(124)"},

                {"【32. 多播路由 Stage 125-127】",
                        "PIM_SM", "📡 PIM-SM组播(125)",
                        "MLD", "📢 MLD组播发现(126)",
                        "DVMRP", "🗺️ DVMRP组播路由(127)"},

                {"【33. 网络监控 Stage 128-132】",
                        "NETFLOW", "📊 NetFlow流量(128)",
                        "SFLOW", "📈 sFlow采样(129)",
                        "IPFIX", "📋 IPFIX流导出(130)",
                        "ICMP_PING", "📡 ICMP Ping(131)",
                        "ICMP_TRACE", "🔎 ICMP Traceroute(132)"},

                {"【34. 加密证书 Stage 133-137】",
                        "X509", "📜 X.509证书(133)",
                        "CRL", "🚫 CRL吊销列表(134)",
                        "OCSP", "🔍 OCSP在线状态(135)",
                        "PKI", "🔐 PKI基础设施(136)",
                        "DTLS", "🔄 DTLS数据报TLS(137)"},

                {"【35. 访问控制 Stage 138-140】",
                        "MAC_AUTH", "🔑 MAC地址认证(138)",
                        "DOT1X", "🔌 802.1X端口认证(139)"},

                {"【36. 诊断工具 Stage 141-148】",
                        "NETSTAT", "📊 netstat连接(141)",
                        "IPCONFIG", "⚙️ ipconfig配置(142)",
                        "ROUTEPRINT", "🗺️ route路由表(143)",
                        "NSLOOKUP", "🔍 nslookup解析(144)",
                        "ARPCMD", "📋 arp缓存(145)",
                        "CURL", "🌐 curl请求(146)",
                        "WGET", "⬇️ wget下载(147)",
                        "TELNET_CLIENT", "💻 telnet客户端(148)"},

                {"【37. NAT增强 Stage 149-152】",
                        "NAT_HAIRPIN", "↩️ NAT发夹(149)",
                        "NAT_HOLE", "🕳️ NAT穿透(150)",
                        "UPNP", "🔌 UPnP映射(151)",
                        "PCP", "📡 PCP端口控制(152)"},

                {"【38. VPN隧道 Stage 153-159】",
                        "IPSEC_IKE", "🔑 IKE密钥交换(153)",
                        "IPSEC_ESP", "🔒 ESP封装(154)",
                        "IPSEC_AH", "🔐 AH认证(155)",
                        "OPENVPN", "🔓 OpenVPN隧道(156)",
                        "WIREGUARD", "🔒 WireGuard隧道(157)",
                        "L2TP", "🔌 L2TP隧道(158)",
                        "SSTP", "🌐 SSTP隧道(159)"},

                {"【39. 安全增强 Stage 160】",
                        "IPS", "🛡️ IPS入侵防御(160)"}
                , {"【40. HTTP 子工厂 Stage 166-176】",
                        "HTTP_REQ", "📤 HTTP请求(166)",
                        "HTTP_RES", "📥 HTTP响应(167)",
                        "HTTP_HDR", "📋 HTTP头部(168)",
                        "HTTP_BODY", "📦 HTTP消息体(169)",
                        "HTTP_CK", "🍪 HTTP Cookie(170)",
                        "HTTP_AUTH", "🔑 HTTP认证(171)",
                        "HTTP_CACHE", "💾 HTTP缓存(172)",
                        "HTTP_CHUNK", "🧩 HTTP分块(173)",
                        "HTTP2_FRAME", "📡 HTTP/2帧(174)",
                        "HTTP2_SET", "⚙️ HTTP/2设置(175)",
                        "HTTP2_STR", "🌊 HTTP/2流(176)"}
                ,};

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

    // 在 buildShopUI() 方法之后、initMap() 方法之前添加新方法
    private void copyConsoleLog() {
        String consoleText = txtHexDisplay.getText();
        if (consoleText == null || consoleText.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "控制台日志为空，无可复制内容",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // 复制到系统剪贴板
        java.awt.datatransfer.StringSelection selection =
                new java.awt.datatransfer.StringSelection(consoleText);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);

        // 显示复制成功提示
        JOptionPane.showMessageDialog(this,
                String.format("✅ 已复制 %d 行日志到剪贴板",
                        consoleText.split("\n").length),
                "复制成功",
                JOptionPane.INFORMATION_MESSAGE);
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

        // ========== 新增 stage 46-61 对应的建筑 ==========
        int newRow = MAP_ROWS / 2 - 9;
        buildingLayout[newRow][5] = "TCP_OPTION";      // TCP 选项
        buildingLayout[newRow][6] = "IP_OPTION";       // IP 选项
        buildingLayout[newRow][7] = "ETH_PADDING";     // 以太网填充
        buildingLayout[newRow][8] = "UDP_CHECKSUM";    // UDP 校验和
        buildingLayout[newRow][9] = "ICMP_ERROR";      // ICMP 错误
        buildingLayout[newRow][10] = "IP_FORWARD";     // IP 转发
        buildingLayout[newRow][11] = "TCP_WINDOW";     // TCP 窗口
        buildingLayout[newRow][12] = "TCP_TIMER";      // TCP 定时器
        buildingLayout[newRow][13] = "VLAN_TAG";       // VLAN
        buildingLayout[newRow][14] = "TUNNEL_GRE";     // GRE 隧道
        buildingLayout[newRow][15] = "IGMP_MCAST";     // IGMP
        buildingLayout[newRow][16] = "NDP_DISC";       // NDP
        buildingLayout[newRow][17] = "DNS_RECURSIVE";  // DNS 递归
        buildingLayout[newRow][18] = "DHCP_FULL";      // DHCP 完整
        buildingLayout[newRow][19] = "TLS_HANDSHAKE";  // TLS 握手
        buildingLayout[newRow][20] = "SERIALIZE";      // 序列化

        // ===================== 新增 14 个工厂建筑 (stage 62-75) =====================
        int newRow2 = MAP_ROWS / 2 - 10;
        buildingLayout[newRow2][2] = "BIT_STREAM";       // 比特流生成器
        buildingLayout[newRow2][3] = "PHY_CHANNEL";      // 物理信道
        buildingLayout[newRow2][4] = "PPPOE";            // PPPoE 封装
        buildingLayout[newRow2][5] = "MACSEC";           // MACsec 加密
        buildingLayout[newRow2][6] = "OSPF";             // OSPF 路由
        buildingLayout[newRow2][7] = "BGP";              // BGP 路由
        buildingLayout[newRow2][8] = "QOS";              // QoS 流量标记
        buildingLayout[newRow2][9] = "NAT64";            // NAT64 转换
        buildingLayout[newRow2][10] = "TCP_REASSEMBLY";  // TCP 重组
        buildingLayout[newRow2][11] = "ATTACK";          // 攻击检测
        buildingLayout[newRow2][12] = "NTP";             // NTP 时间同步
        buildingLayout[newRow2][13] = "SNMP";            // SNMP 管理
        buildingLayout[newRow2][14] = "HTTP23";          // HTTP/2.3
        buildingLayout[newRow2][15] = "IPSEC";           // IPsec 安全

        // ===================== IPv6 建筑（放在行 2-3）=====================
        int ipv6Row = 2;
        buildingLayout[ipv6Row][30] = "IPV6";
        buildingLayout[ipv6Row][31] = "IPV6_FRAG";
        buildingLayout[ipv6Row + 1][30] = "IPV6_OPTION";
        buildingLayout[ipv6Row + 1][31] = "IPV6_ND";

        // ===================== TCP 增强建筑（放在行 3-4）=====================
        int tcpRow = 3;
        buildingLayout[tcpRow][33] = "TCP_KEEPALIVE";
        buildingLayout[tcpRow][34] = "TCP_SACK";
        buildingLayout[tcpRow + 1][33] = "TCP_ECN";
        buildingLayout[tcpRow + 1][34] = "TCP_FASTOPEN";

        // ===================== 应用层协议建筑（放在行 12-14）=====================
        int appRow = 12;
        buildingLayout[appRow][25] = "FTP";
        buildingLayout[appRow][26] = "SMTP";
        buildingLayout[appRow][27] = "POP3";
        buildingLayout[appRow][28] = "IMAP";
        buildingLayout[appRow + 1][25] = "SSH";
        buildingLayout[appRow + 1][26] = "TELNET";
        buildingLayout[appRow + 1][27] = "RTP";
        buildingLayout[appRow + 1][28] = "SIP";
        buildingLayout[appRow + 2][25] = "RADIUS";
        buildingLayout[appRow + 2][26] = "DIAMETER";
        buildingLayout[appRow + 2][27] = "LDAP";

        // ===================== 安全防护建筑（放在行 15-17）=====================
        int secRow = 15;
        buildingLayout[secRow][30] = "DPI";
        buildingLayout[secRow][31] = "WAF";
        buildingLayout[secRow + 1][30] = "DDOS";
        buildingLayout[secRow + 1][31] = "RATELIMIT";
        buildingLayout[secRow + 2][30] = "ACL";

        // ===================== NAT 增强建筑（放在行 17-18）=====================
        int natRow = 17;
        buildingLayout[natRow][35] = "NAT_HAIRPIN";
        buildingLayout[natRow][36] = "NAT_HOLE";
        buildingLayout[natRow + 1][35] = "UPNP";
        buildingLayout[natRow + 1][36] = "PCP";

        // ===================== 负载均衡建筑（放在行 4-5）=====================
        int lbRow = 4;
        buildingLayout[lbRow][36] = "LB_RR";
        buildingLayout[lbRow][37] = "LB_LC";
        buildingLayout[lbRow + 1][36] = "LB_IPHASH";
        buildingLayout[lbRow + 1][37] = "LB_HC";

        // ===================== VPN 隧道建筑（放在行 18-19）=====================
        // 使用列 41-43 避免与诊断工具冲突
        int vpnRow = 18;
        buildingLayout[vpnRow][41] = "IPSEC_IKE";
        buildingLayout[vpnRow][42] = "IPSEC_ESP";
        buildingLayout[vpnRow][43] = "OPENVPN";
        buildingLayout[vpnRow + 1][41] = "WIREGUARD";
        buildingLayout[vpnRow + 1][42] = "L2TP";
        buildingLayout[vpnRow + 1][43] = "SSTP";

        // ===================== 诊断工具建筑（放在行 5-7）=====================
        // 使用列 41-43 避开 VPN（已移到行18-19的41-43，但行不同，可以共用列）
        // 注意：不同行相同列是允许的，每个格子独立
        int toolRow = 5;
        buildingLayout[toolRow][41] = "NETSTAT";
        buildingLayout[toolRow][42] = "IPCONFIG";
        buildingLayout[toolRow][43] = "ROUTEPRINT";
        buildingLayout[toolRow + 1][41] = "NSLOOKUP";
        buildingLayout[toolRow + 1][42] = "ARPCMD";
        buildingLayout[toolRow + 1][43] = "CURL";
        buildingLayout[toolRow + 2][41] = "WGET";
        buildingLayout[toolRow + 2][42] = "TELNET_CLIENT";

        // ===================== 新增 20 个核心工厂建筑 (stage 101-120) =====================
        // 放在行 19（最后一行），列 30-49（右侧区域）
        int coreRow = 19;
        buildingLayout[coreRow][30] = "SOCKET";
        buildingLayout[coreRow][31] = "TCP_STATE";
        buildingLayout[coreRow][32] = "MAC_TABLE";
        buildingLayout[coreRow][33] = "CAM_TABLE";
        buildingLayout[coreRow][34] = "FIB";
        buildingLayout[coreRow][35] = "SESSION_TABLE";
        buildingLayout[coreRow][36] = "FLOW";
        buildingLayout[coreRow][37] = "LOAD_BALANCER";
        buildingLayout[coreRow][38] = "SCHEDULER";
        buildingLayout[coreRow][39] = "DNS_ZONE";
        buildingLayout[coreRow][40] = "DHCP_LEASE";
        buildingLayout[coreRow][41] = "ARP_TABLE";
        buildingLayout[coreRow][42] = "NEIGHBOR_TABLE";
        buildingLayout[coreRow][43] = "MCAST_ROUTE";
        buildingLayout[coreRow][44] = "MPLS_LABEL";
        buildingLayout[coreRow][45] = "CERT_STORE";
        buildingLayout[coreRow][46] = "EVENT";
        buildingLayout[coreRow][47] = "STATS";
        buildingLayout[coreRow][48] = "LOG";
        buildingLayout[coreRow][49] = "PCAP";
        // ===================== 新增 Stage 121-160 建筑位置 =====================
// 使用行 20? 但 MAP_ROWS=20，需要复用已有行或使用列扩展
// 由于行数有限(0-19)，使用已有行的空闲列

// 放在行 0 的空闲列 (列 16-30 空闲)
        int stageRow = 0;
        buildingLayout[stageRow][16] = "LLDP";
        buildingLayout[stageRow][17] = "STP";
        buildingLayout[stageRow][18] = "LACP";
        buildingLayout[stageRow][19] = "MPLS";
        buildingLayout[stageRow][20] = "PIM_SM";
        buildingLayout[stageRow][21] = "MLD";
        buildingLayout[stageRow][22] = "DVMRP";
        buildingLayout[stageRow][23] = "NETFLOW";
        buildingLayout[stageRow][24] = "SFLOW";
        buildingLayout[stageRow][25] = "IPFIX";
        buildingLayout[stageRow][26] = "ICMP_PING";
        buildingLayout[stageRow][27] = "ICMP_TRACE";

// 放在行 1 的空闲列
        int stageRow2 = 1;
        buildingLayout[stageRow2][21] = "X509";
        buildingLayout[stageRow2][22] = "CRL";
        buildingLayout[stageRow2][23] = "OCSP";
        buildingLayout[stageRow2][24] = "PKI";
        buildingLayout[stageRow2][25] = "DTLS";
        buildingLayout[stageRow2][26] = "MAC_AUTH";
        buildingLayout[stageRow2][27] = "DOT1X";

// 放在行 2 的空闲列 (列 32-49 空闲)
        int stageRow3 = 2;
        buildingLayout[stageRow3][32] = "NETSTAT";
        buildingLayout[stageRow3][33] = "IPCONFIG";
        buildingLayout[stageRow3][34] = "ROUTEPRINT";
        buildingLayout[stageRow3][35] = "NSLOOKUP";
        buildingLayout[stageRow3][36] = "ARPCMD";
        buildingLayout[stageRow3][37] = "CURL";
        buildingLayout[stageRow3][38] = "WGET";
        buildingLayout[stageRow3][39] = "TELNET_CLIENT";

// 放在行 3 的空闲列
        int stageRow4 = 3;
        buildingLayout[stageRow4][35] = "NAT_HAIRPIN";
        buildingLayout[stageRow4][36] = "NAT_HOLE";
        buildingLayout[stageRow4][37] = "UPNP";
        buildingLayout[stageRow4][38] = "PCP";

// 放在行 4 的空闲列
        int stageRow5 = 4;
        buildingLayout[stageRow5][38] = "IPSEC_IKE";
        buildingLayout[stageRow5][39] = "IPSEC_ESP";
        buildingLayout[stageRow5][40] = "IPSEC_AH";
        buildingLayout[stageRow5][41] = "OPENVPN";
        buildingLayout[stageRow5][42] = "WIREGUARD";
        buildingLayout[stageRow5][43] = "L2TP";
        buildingLayout[stageRow5][44] = "SSTP";

// 放在行 5 的空闲列
        int stageRow6 = 5;
        buildingLayout[stageRow6][44] = "IPS";

        int ftpRow = 19;  // 使用最后一行（与 IPS 同行）
        int ftpStartCol = 50;  // 从第 50 列开始（避开其他建筑）

        buildingLayout[ftpRow][ftpStartCol] = "FTP_AUTH";
        buildingLayout[ftpRow][ftpStartCol + 1] = "FTP_COMMAND";
        buildingLayout[ftpRow][ftpStartCol + 2] = "FTP_CHANNEL";
        buildingLayout[ftpRow][ftpStartCol + 3] = "FTP_RESPONSE";
        buildingLayout[ftpRow][ftpStartCol + 4] = "FTP_MAIN";

        // ========== HTTP 子工厂 Stage 166-176 ==========
        int httpRow = 18;  // 倒数第二行
        int httpStartCol = 38;
        buildingLayout[httpRow][httpStartCol] = "HTTP_REQ";
        buildingLayout[httpRow][httpStartCol + 1] = "HTTP_RES";
        buildingLayout[httpRow][httpStartCol + 2] = "HTTP_HDR";
        buildingLayout[httpRow][httpStartCol + 3] = "HTTP_BODY";
        buildingLayout[httpRow][httpStartCol + 4] = "HTTP_CK";
        buildingLayout[httpRow][httpStartCol + 5] = "HTTP_AUTH";
        buildingLayout[httpRow][httpStartCol + 6] = "HTTP_CACHE";
        buildingLayout[httpRow][httpStartCol + 7] = "HTTP_CHUNK";
        buildingLayout[httpRow][httpStartCol + 8] = "HTTP2_FRAME";
        buildingLayout[httpRow][httpStartCol + 9] = "HTTP2_SET";
        buildingLayout[httpRow][httpStartCol + 10] = "HTTP2_STR";
    }

    // 在 DataCartFactoryGame() 构造函数中，initMap() 之后添加
    private void initZoneMapping() {
        // 采矿/数据源区域
        buildingZoneMap.put("MINER_H", BuildingZone.ZONE_MINING);
        buildingZoneMap.put("MINER_S", BuildingZone.ZONE_MINING);
        buildingZoneMap.put("PC_FACTORY", BuildingZone.ZONE_MINING);
        buildingZoneMap.put("RX_ST", BuildingZone.ZONE_MINING);
        buildingZoneMap.put("TX_APP", BuildingZone.ZONE_APPLICATION);

        // 应用层协议
        buildingZoneMap.put("FTP", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("SMTP", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("POP3", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("IMAP", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("SSH", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("TELNET", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("RTP", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("SIP", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("RADIUS", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("DIAMETER", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("LDAP", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("HTTP23", BuildingZone.ZONE_APPLICATION);
        buildingZoneMap.put("NTP", BuildingZone.ZONE_APPLICATION);
        buildingZoneMap.put("SNMP", BuildingZone.ZONE_APPLICATION);

        // 传输层
        buildingZoneMap.put("T_SP", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_DP", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_SEQ", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_ACK", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_CTL", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_WIN", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_CHK", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("T_CORE", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("UDP_CHECKSUM", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("TCP_OPTION", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("TCP_WINDOW", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("TCP_TIMER", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("TCP_REASSEMBLY", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("TCP_KEEPALIVE", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("TCP_SACK", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("TCP_ECN", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("TCP_FASTOPEN", BuildingZone.ZONE_TRANSPORT);

        // 网络层
        buildingZoneMap.put("TX_IPH", BuildingZone.ZONE_NETWORK);
        buildingZoneMap.put("TX_IP_FRAG", BuildingZone.ZONE_NETWORK);
        buildingZoneMap.put("RX_IP", BuildingZone.ZONE_NETWORK);
        buildingZoneMap.put("RX_FRAG", BuildingZone.ZONE_NETWORK);
        buildingZoneMap.put("IP_OPTION", BuildingZone.ZONE_NETWORK);
        buildingZoneMap.put("IP_FORWARD", BuildingZone.ZONE_NETWORK);
        buildingZoneMap.put("ICMP_ERROR", BuildingZone.ZONE_NETWORK);
        buildingZoneMap.put("ICMP_PING", BuildingZone.ZONE_MONITOR);
        buildingZoneMap.put("ICMP_TRACE", BuildingZone.ZONE_MONITOR);

        // 链路层
        buildingZoneMap.put("ETH_DST", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("ETH_SRC", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("ETH_TYPE", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("TX_LLC", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("TX_FCS", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("RX_LLC", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("RX_ETH", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("RX_FCS", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("TX_ARP", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("RX_ARP", BuildingZone.ZONE_LINK);
        buildingZoneMap.put("ETH_PADDING", BuildingZone.ZONE_LINK);

        // 链路增强
        buildingZoneMap.put("LLDP", BuildingZone.ZONE_LINK_ENHANCE);
        buildingZoneMap.put("STP", BuildingZone.ZONE_LINK_ENHANCE);
        buildingZoneMap.put("LACP", BuildingZone.ZONE_LINK_ENHANCE);
        buildingZoneMap.put("MPLS", BuildingZone.ZONE_LINK_ENHANCE);
        buildingZoneMap.put("VLAN_TAG", BuildingZone.ZONE_LINK_ENHANCE);
        buildingZoneMap.put("TUNNEL_GRE", BuildingZone.ZONE_LINK_ENHANCE);

        // 物理层
        buildingZoneMap.put("BIT_STREAM", BuildingZone.ZONE_PHYSICAL);
        buildingZoneMap.put("PHY_CHANNEL", BuildingZone.ZONE_PHYSICAL);
        buildingZoneMap.put("PPPOE", BuildingZone.ZONE_PHYSICAL);
        buildingZoneMap.put("MACSEC", BuildingZone.ZONE_SECURITY);

        // 安全防护
        buildingZoneMap.put("FW_IN", BuildingZone.ZONE_SECURITY);
        buildingZoneMap.put("FW_OUT", BuildingZone.ZONE_SECURITY);
        buildingZoneMap.put("IDS", BuildingZone.ZONE_SECURITY);
        buildingZoneMap.put("IPS", BuildingZone.ZONE_SECURITY);
        buildingZoneMap.put("DPI", BuildingZone.ZONE_SECURITY);
        buildingZoneMap.put("WAF", BuildingZone.ZONE_SECURITY);
        buildingZoneMap.put("DDOS", BuildingZone.ZONE_SECURITY);
        buildingZoneMap.put("RATELIMIT", BuildingZone.ZONE_SECURITY);
        buildingZoneMap.put("ACL", BuildingZone.ZONE_ACCESS_CONTROL);
        buildingZoneMap.put("MAC_AUTH", BuildingZone.ZONE_ACCESS_CONTROL);
        buildingZoneMap.put("DOT1X", BuildingZone.ZONE_ACCESS_CONTROL);
        buildingZoneMap.put("IPSEC", BuildingZone.ZONE_SECURITY);
        buildingZoneMap.put("ATTACK", BuildingZone.ZONE_SECURITY);

        // 网络设备
        buildingZoneMap.put("SWITCH", BuildingZone.ZONE_NETWORK_DEVICE);
        buildingZoneMap.put("HUB", BuildingZone.ZONE_NETWORK_DEVICE);
        buildingZoneMap.put("BRIDGE", BuildingZone.ZONE_NETWORK_DEVICE);
        buildingZoneMap.put("SUBNET_A", BuildingZone.ZONE_NETWORK_DEVICE);
        buildingZoneMap.put("SUBNET_B", BuildingZone.ZONE_NETWORK_DEVICE);
        buildingZoneMap.put("LINK_UP", BuildingZone.ZONE_NETWORK_DEVICE);
        buildingZoneMap.put("LINK_DOWN", BuildingZone.ZONE_NETWORK_DEVICE);

        // QoS/拥塞控制
        buildingZoneMap.put("QOS", BuildingZone.ZONE_QOS);
        buildingZoneMap.put("Q_IN", BuildingZone.ZONE_QOS);
        buildingZoneMap.put("Q_OUT", BuildingZone.ZONE_QOS);
        buildingZoneMap.put("Q_DROP", BuildingZone.ZONE_QOS);
        buildingZoneMap.put("CC_SLOW", BuildingZone.ZONE_QOS);
        buildingZoneMap.put("CC_AVOID", BuildingZone.ZONE_QOS);
        buildingZoneMap.put("CC_FAST", BuildingZone.ZONE_QOS);
        buildingZoneMap.put("SCHEDULER", BuildingZone.ZONE_QOS);

        // 网关/NAT
        buildingZoneMap.put("R_LAN", BuildingZone.ZONE_GATEWAY);
        buildingZoneMap.put("R_TAB", BuildingZone.ZONE_GATEWAY);
        buildingZoneMap.put("R_NAT", BuildingZone.ZONE_GATEWAY);
        buildingZoneMap.put("R_WAN", BuildingZone.ZONE_GATEWAY);
        buildingZoneMap.put("BW_CTRL", BuildingZone.ZONE_GATEWAY);
        buildingZoneMap.put("NAT64", BuildingZone.ZONE_GATEWAY);
        buildingZoneMap.put("NAT_HAIRPIN", BuildingZone.ZONE_NAT_ENHANCE);
        buildingZoneMap.put("NAT_HOLE", BuildingZone.ZONE_NAT_ENHANCE);
        buildingZoneMap.put("UPNP", BuildingZone.ZONE_NAT_ENHANCE);
        buildingZoneMap.put("PCP", BuildingZone.ZONE_NAT_ENHANCE);

        // 路由协议
        buildingZoneMap.put("ROUTER1", BuildingZone.ZONE_ROUTER);
        buildingZoneMap.put("ROUTER2", BuildingZone.ZONE_ROUTER);
        buildingZoneMap.put("ROUTER3", BuildingZone.ZONE_ROUTER);
        buildingZoneMap.put("OSPF", BuildingZone.ZONE_ROUTER);
        buildingZoneMap.put("BGP", BuildingZone.ZONE_ROUTER);

        // DHCP
        buildingZoneMap.put("DHCP_DISC", BuildingZone.ZONE_DHCP);
        buildingZoneMap.put("DHCP_SERVER", BuildingZone.ZONE_DHCP);
        buildingZoneMap.put("DHCP_OFFER", BuildingZone.ZONE_DHCP);
        buildingZoneMap.put("DHCP_REQ", BuildingZone.ZONE_DHCP);
        buildingZoneMap.put("DHCP_ACK", BuildingZone.ZONE_DHCP);
        buildingZoneMap.put("DHCP_FULL", BuildingZone.ZONE_DHCP);
        buildingZoneMap.put("DHCP_LEASE", BuildingZone.ZONE_DHCP);

        // DNS
        buildingZoneMap.put("DNS_CLIENT", BuildingZone.ZONE_DNS);
        buildingZoneMap.put("DNS_LOCAL", BuildingZone.ZONE_DNS);
        buildingZoneMap.put("DNS_ROOT", BuildingZone.ZONE_DNS);
        buildingZoneMap.put("DNS_AUTH", BuildingZone.ZONE_DNS);
        buildingZoneMap.put("DNS_RECURSIVE", BuildingZone.ZONE_DNS);
        buildingZoneMap.put("DNS_ZONE", BuildingZone.ZONE_DNS);

        // IPv6
        buildingZoneMap.put("IPV6", BuildingZone.ZONE_IPV6);
        buildingZoneMap.put("IPV6_FRAG", BuildingZone.ZONE_IPV6);
        buildingZoneMap.put("IPV6_OPTION", BuildingZone.ZONE_IPV6);
        buildingZoneMap.put("IPV6_ND", BuildingZone.ZONE_IPV6);
        buildingZoneMap.put("NDP_DISC", BuildingZone.ZONE_IPV6);

        // VPN
        buildingZoneMap.put("IPSEC_IKE", BuildingZone.ZONE_VPN);
        buildingZoneMap.put("IPSEC_ESP", BuildingZone.ZONE_VPN);
        buildingZoneMap.put("IPSEC_AH", BuildingZone.ZONE_VPN);
        buildingZoneMap.put("OPENVPN", BuildingZone.ZONE_VPN);
        buildingZoneMap.put("WIREGUARD", BuildingZone.ZONE_VPN);
        buildingZoneMap.put("L2TP", BuildingZone.ZONE_VPN);
        buildingZoneMap.put("SSTP", BuildingZone.ZONE_VPN);

        // 加密证书
        buildingZoneMap.put("X509", BuildingZone.ZONE_ENCRYPTION);
        buildingZoneMap.put("CRL", BuildingZone.ZONE_ENCRYPTION);
        buildingZoneMap.put("OCSP", BuildingZone.ZONE_ENCRYPTION);
        buildingZoneMap.put("PKI", BuildingZone.ZONE_ENCRYPTION);
        buildingZoneMap.put("DTLS", BuildingZone.ZONE_ENCRYPTION);
        buildingZoneMap.put("TLS_HANDSHAKE", BuildingZone.ZONE_ENCRYPTION);
        buildingZoneMap.put("CERT_STORE", BuildingZone.ZONE_ENCRYPTION);

        // 监控管理
        buildingZoneMap.put("NETFLOW", BuildingZone.ZONE_MONITOR);
        buildingZoneMap.put("SFLOW", BuildingZone.ZONE_MONITOR);
        buildingZoneMap.put("IPFIX", BuildingZone.ZONE_MONITOR);
        buildingZoneMap.put("FLOW", BuildingZone.ZONE_MONITOR);
        buildingZoneMap.put("STATS", BuildingZone.ZONE_MONITOR);
        buildingZoneMap.put("PCAP", BuildingZone.ZONE_MONITOR);

        // 组播路由
        buildingZoneMap.put("PIM_SM", BuildingZone.ZONE_MULTICAST);
        buildingZoneMap.put("MLD", BuildingZone.ZONE_MULTICAST);
        buildingZoneMap.put("DVMRP", BuildingZone.ZONE_MULTICAST);
        buildingZoneMap.put("MCAST_ROUTE", BuildingZone.ZONE_MULTICAST);
        buildingZoneMap.put("IGMP_MCAST", BuildingZone.ZONE_MULTICAST);

        // 诊断工具
        buildingZoneMap.put("NETSTAT", BuildingZone.ZONE_DIAGNOSTIC);
        buildingZoneMap.put("IPCONFIG", BuildingZone.ZONE_DIAGNOSTIC);
        buildingZoneMap.put("ROUTEPRINT", BuildingZone.ZONE_DIAGNOSTIC);
        buildingZoneMap.put("NSLOOKUP", BuildingZone.ZONE_DIAGNOSTIC);
        buildingZoneMap.put("ARPCMD", BuildingZone.ZONE_DIAGNOSTIC);
        buildingZoneMap.put("CURL", BuildingZone.ZONE_DIAGNOSTIC);
        buildingZoneMap.put("WGET", BuildingZone.ZONE_DIAGNOSTIC);
        buildingZoneMap.put("TELNET_CLIENT", BuildingZone.ZONE_DIAGNOSTIC);

        // 核心服务
        buildingZoneMap.put("SOCKET", BuildingZone.ZONE_CORE);
        buildingZoneMap.put("TCP_STATE", BuildingZone.ZONE_CORE);
        buildingZoneMap.put("MAC_TABLE", BuildingZone.ZONE_CORE);
        buildingZoneMap.put("CAM_TABLE", BuildingZone.ZONE_CORE);
        buildingZoneMap.put("FIB", BuildingZone.ZONE_CORE);
        buildingZoneMap.put("SESSION_TABLE", BuildingZone.ZONE_CORE);
        buildingZoneMap.put("SESSION", BuildingZone.ZONE_CORE);
        buildingZoneMap.put("FIVETUPLE", BuildingZone.ZONE_CORE);
        buildingZoneMap.put("LOAD_BALANCER", BuildingZone.ZONE_LOAD_BALANCE);
        buildingZoneMap.put("LB_RR", BuildingZone.ZONE_LOAD_BALANCE);
        buildingZoneMap.put("LB_LC", BuildingZone.ZONE_LOAD_BALANCE);
        buildingZoneMap.put("LB_IPHASH", BuildingZone.ZONE_LOAD_BALANCE);
        buildingZoneMap.put("LB_HC", BuildingZone.ZONE_LOAD_BALANCE);
        buildingZoneMap.put("EVENT", BuildingZone.ZONE_CORE);
        buildingZoneMap.put("LOG", BuildingZone.ZONE_CORE);
        buildingZoneMap.put("SERIALIZE", BuildingZone.ZONE_CORE);

        // 其他
        buildingZoneMap.put("RX_TCP", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("RX_APP", BuildingZone.ZONE_APPLICATION);
        buildingZoneMap.put("RX_PORT", BuildingZone.ZONE_TRANSPORT);
        buildingZoneMap.put("MPLS_LABEL", BuildingZone.ZONE_LINK_ENHANCE);

        // FTP 相关建筑映射
        buildingZoneMap.put("FTP_AUTH", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("FTP_COMMAND", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("FTP_CHANNEL", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("FTP_RESPONSE", BuildingZone.ZONE_APPLICATION_PROTOCOL);
        buildingZoneMap.put("FTP_MAIN", BuildingZone.ZONE_APPLICATION_PROTOCOL);
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

    private void startFtpSession() {
        currentTcpState = TcpState.SYN_SENT;
        stateTimerWatchdog = System.currentTimeMillis();
        ftpLoginSent = false;
        ftpPassSent = false;
        ftpLoggedIn = false;
        ftpDataPort = 0;

        // FTP使用端口21
        DataCart syn = new DataCart(pcFactory.x, pcFactory.y, "SYN", 0);
        syn.sequenceNumber = 100;
        syn.dstPort = 21;  // FTP控制端口
        syn.ttl = 64;
        pendingDataCarts.add(syn);
        appendToConsole("【📁 FTP】: 开始连接服务器端口21 (控制连接)");
    }

    private void startDnsResolution() {
        // 更新 watchdog，防止在 DNS 解析期间超时
        stateTimerWatchdog = System.currentTimeMillis();
        if (!pcIpAssigned) {
            appendToConsole("【⛔ DNS 阻断】: PC 未获取 IP，等待 DHCP 完成...");
            return;
        }
        if (isDnsResolved) {
            appendToConsole("【📚 DNS 已解析】: " + targetDomain + " → " + resolvedServerIp);
            if (!useUdp && currentTcpState == TcpState.CLOSED) {
                performArpResolution(resolvedServerIp);
                startTcpHandshake();
            } else if (useUdp && !udpActive) {
                performArpResolution(resolvedServerIp);
                startUdpTransmission();
            }
            return;
        }
        // 在 startDnsResolution 方法中，修改超时处理部分
        if (isDnsResolving) {
            // 检查超时
            if (System.currentTimeMillis() - lastDnsQueryTime > DNS_TIMEOUT) {
                dnsRetryCount++;
                appendToConsole("【⚠️ DNS 超时】: 重试 (" + dnsRetryCount + "/3)");
                if (dnsRetryCount >= 3) {
                    appendToConsole("【❌ DNS 失败】: 无法解析域名 " + targetDomain + "，使用默认 IP");
                    resolvedServerIp = "10.0.0.1";
                    isDnsResolved = true;
                    isDnsResolving = false;
                    dnsRetryCount = 0;
                    // 直接添加到 DNS 缓存
                    dnsCache.put(targetDomain, new DnsEntry(targetDomain, resolvedServerIp, 3600000));
                    updateDnsDisplay();
                    performArpResolution(resolvedServerIp);
                    if (!useUdp && currentTcpState == TcpState.CLOSED) {
                        startTcpHandshake();
                    } else if (useUdp && !udpActive) {
                        startUdpTransmission();
                    }
                    return;
                }
                isDnsResolving = false;
                startDnsResolution();
            } else {
                appendToConsole("【⏳ DNS 解析中】: 请稍候...");
            }
            return;
        }

        isDnsResolving = true;
        lastDnsQueryTime = System.currentTimeMillis();
        appendToConsole("【🌐 DNS 解析开始】: 查询域名 " + targetDomain);

        String cached = factoryManager.getDnsCache().resolve(targetDomain);
        if (cached != null) {
            resolvedServerIp = cached;
            isDnsResolved = true;
            isDnsResolving = false;
            dnsRetryCount = 0;
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
        if (mac == null || mac.equals("null")) {
            String newMac = String.format("00:1A:2B:%02X:%02X:%02X",
                    new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
            factoryManager.getArpCache().addEntry(targetIp, newMac);
            mac = newMac;
            appendToConsole("【📥 ARP 响应】: " + targetIp + " → " + mac);
        } else {
            appendToConsole("【✅ ARP 缓存命中】: " + targetIp + " → " + mac);
        }
        // 同时更新本地 arpCache
        arpCache.put(targetIp, new ArpEntry(targetIp, mac));
        updateArpDisplay();
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
        udpPacketsSent = 0;  // 新增：记录已发送数量
        appendToConsole("【🚀 UDP 模式】: 跳过握手，直接发送数据");
    }

    private long lastResourceTick = 0;

    // 在 gameTick 中添加重传清理逻辑
    private void cleanupRetransmissionTasks() {
        // 清理已确认的任务
        List<RetransmissionTask> completed = new ArrayList<>();
        for (RetransmissionTask task : activeTimers) {
            if (task.isAcked) {
                completed.add(task);
            }
        }
        activeTimers.removeAll(completed);

        // 清理超时的任务（超过 5 次重传）
        List<RetransmissionTask> failed = new ArrayList<>();
        for (RetransmissionTask task : activeTimers) {
            if (task.retryCount >= 5) {
                failed.add(task);
            }
        }
        activeTimers.removeAll(failed);
    }

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

        // 修改超时处理，增加状态检查避免重复重置
        if (!useUdp && now - stateTimerWatchdog > 300000) {
            appendToConsole("【🔍 超时检查】: watchdog=" + stateTimerWatchdog +
                    ", now=" + now + ", diff=" + (now - stateTimerWatchdog) +
                    ", isDnsResolving=" + isDnsResolving);
            if (isDnsResolving) {
                appendToConsole("【⏰ DNS 超时】: 重新尝试 DNS 解析");
                isDnsResolving = false;
                startDnsResolution();
            } else {
                appendToConsole("【⏰ 连接超时】: 重置会话");
                resetTcpSession();
            }
        }

        if (serverBufferCount > 0 && (now - lastServerConsumeTime >= serverDecodeDelay)) {
            serverBufferCount--;
            // 确保不超过 totalDataToTransmit
            if (serverReceivedCount < totalDataToTransmit) {
                serverReceivedCount++;
            }
            lastServerConsumeTime = now;
            rwnd = SERVER_BUFFER_MAX - serverBufferCount;
            updateTopLabel();
            appendToConsole(String.format("【📥 服务器处理】: 已处理 %d/%d 个数据包",
                    Math.min(serverReceivedCount, totalDataToTransmit), totalDataToTransmit));
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

        // 在 gameTick 方法中，修改公网队列计数
        int currentCartsInWan = 0;
        for (DataCart c : dataCarts) {
            int col = (int) (c.x / TILE_SIZE);
            if (!c.isReturnTrip && c.stage >= 25 && c.stage <= 31 && col >= 21 && col <= 34) {
                // DNS 和 DHCP 包不计入公网队列计数
                boolean isControlPacket = c.cartType != null &&
                        (c.cartType.startsWith("DNS_") || c.cartType.startsWith("DHCP_"));
                if (!isControlPacket) {
                    currentCartsInWan++;
                }
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
            if (pcIpAssigned && !demoCompleted) {
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

            // ========== 在这段代码后面添加 FTP 触发逻辑 ==========
// FTP 专用逻辑
            // FTP 专用逻辑 - 修改为更清晰的流程
            if (ftpDemoEnabled && pcIpAssigned && !useUdp && currentTcpState == TcpState.ESTABLISHED) {
                if (!ftpLoginSent) {
                    // 发送 USER 命令
                    DataCart ftpUser = new DataCart(pcFactory.x, pcFactory.y, "FTP_USER", 0);
                    ftpUser.stage = 161;  // 明确设置 stage
                    ftpUser.dstPort = 21;
                    ftpUser.ftpCommand = "USER anonymous";
                    pendingDataCarts.add(ftpUser);
                    ftpLoginSent = true;
                    appendToConsole("【📁 FTP】: 创建并发送 USER 命令 (stage=161, port=21)");
                } else if (ftpLoginSent && !ftpPassSent && ftpUserAcked) {
                    // 等待 USER 命令的响应后再发送 PASS
                    DataCart ftpPass = new DataCart(pcFactory.x, pcFactory.y, "FTP_PASS", 0);
                    ftpPass.stage = 161;
                    ftpPass.dstPort = 21;
                    ftpPass.ftpCommand = "PASS anonymous@example.com";
                    pendingDataCarts.add(ftpPass);
                    ftpPassSent = true;
                    appendToConsole("【📁 FTP】: 创建并发送 PASS 命令");
                } else if (ftpPassSent && !ftpPasvMode && ftpPassAcked) {
                    // PASS 成功后发送 PASV
                    DataCart ftpPasv = new DataCart(pcFactory.x, pcFactory.y, "FTP_PASV", 0);
                    ftpPasv.stage = 161;
                    ftpPasv.dstPort = 21;
                    ftpPasv.ftpCommand = "PASV";
                    pendingDataCarts.add(ftpPasv);
                    ftpPasvMode = true;
                    appendToConsole("【📁 FTP】: 创建并发送 PASV 命令");
                }
                // ========== 新增：发送 LIST 命令 ==========
                else if (ftpDemoEnabled && ftpLoggedIn && ftpPasvMode && ftpDataPort > 0 && !ftpListSent) {
                    DataCart ftpList = new DataCart(pcFactory.x, pcFactory.y, "FTP_LIST", 0);
                    ftpList.stage = 161;
                    ftpList.dstPort = 21;
                    ftpList.ftpCommand = "LIST";
                    pendingDataCarts.add(ftpList);
                    ftpListSent = true;
                    appendToConsole("【📁 FTP】: 创建并发送 LIST 命令");
                }
            }
// ========== FTP 触发逻辑结束 ==========

            // UDP 数据发送
            // UDP 数据发送 - 只发送 totalDataToTransmit 个包
            if (udpActive && udpPacketsSent < totalDataToTransmit) {
                if (now - lastUdpSendTime > 200) {
                    DataCart udpData = new DataCart(pcFactory.x, pcFactory.y, "UDP_DATA", udpSeqToSend++);
                    udpData.stage = 5;
                    udpData.ttl = 64;
                    pendingDataCarts.add(udpData);
                    lastUdpSendTime = now;
                    udpPacketsSent++;  // 递增已发送计数
                    appendToConsole(String.format("【📤 UDP 发送】: SEQ=%d (无 ACK), 已发送 %d/%d",
                            udpData.sequenceNumber, udpPacketsSent, totalDataToTransmit));
                }
            }

            // 在 gameTick() 中添加
            if (udpActive && serverReceivedCount >= totalDataToTransmit && !udpCompleted) {
                udpCompleted = true;
                udpActive = false;
                appendToConsole("【🎉 UDP 传输完成】: 共发送 " + totalDataToTransmit + " 个数据包");
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
            // 如果传输完成后等待超过 10 秒，强制关闭
            if (serverReceivedCount >= totalDataToTransmit && !useUdp && !httpDemoEnabled) {
                if (activeTimers.isEmpty() && serverBufferCount == 0) {
                    // 正常关闭 - 发送 FIN
                    if (currentTcpState != TcpState.FIN_WAIT_1 &&
                            currentTcpState != TcpState.TIME_WAIT) {
                        currentTcpState = TcpState.FIN_WAIT_1;
                        stateTimerWatchdog = System.currentTimeMillis();
                        DataCart fin = new DataCart(pcFactory.x, pcFactory.y, "FIN_PC", 0);
                        fin.ttl = 64;
                        pendingDataCarts.add(fin);
                        appendToConsole("【🏁 数据传输完成】: 发送 FIN，开始四次挥手");
                    }
                } else if (now - stateTimerWatchdog > 10000 && serverReceivedCount >= totalDataToTransmit) {
                    // 传输完成后 10 秒，显示成功并重置
                    appendToConsole("【🎉 传输完成】: 共传输 " + totalDataToTransmit + " 个数据包");
                    JOptionPane.showMessageDialog(this,
                            "✅ TCP 数据传输完成！\n\n" +
                                    "共传输 " + totalDataToTransmit + " 个数据包\n" +
                                    "演示了: TCP 三次握手、滑动窗口、拥塞控制、IP 分片、NAT、Ethernet II 封装",
                            "传输成功", JOptionPane.INFORMATION_MESSAGE);
                    resetTcpSession();
                    demoCompleted = true;
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
            // 只有普通数据包才受公网队列限制，DNS/DHCP/ARP/ICMP 等控制包不受限
            boolean isControlPacket = cart.cartType != null &&
                    (cart.cartType.startsWith("DNS_") ||
                            cart.cartType.startsWith("DHCP_") ||
                            cart.cartType.startsWith("ARP_") ||
                            cart.cartType.startsWith("ICMP_") ||
                            cart.cartType.equals("SYN") ||
                            cart.cartType.equals("SYN_ACK") ||
                            cart.cartType.equals("ACK_PC") ||
                            cart.cartType.equals("FIN_PC") ||
                            cart.cartType.equals("DATA_ACK"));

            if (!cart.isReturnTrip && cart.stage >= 25 && cart.stage <= 31 && col >= 21 && col <= 34) {
                if (currentCartsInWan > WAN_BOTTLE_NECK_MAX && !isForemostCartInWan(cart) && !isControlPacket) {
                    cart.waitInQueueTimer++;
                    if (cart.waitInQueueTimer > 60) {  // 减少超时时间从120到60
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
        cleanupRetransmissionTasks();
        dnsCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
        prgNetwork.setValue((int) (((double) serverReceivedCount / totalDataToTransmit) * 100));
        canvas.repaint();
    }

    // 替换原有的 isForemostCartInWan 方法
    private boolean isForemostCartInWan(DataCart target) {
        // DNS 查询包不应该被公网队列限制
        if (target.cartType != null && target.cartType.startsWith("DNS_")) {
            return true;  // DNS 包总是被视为"最前面"，不会被排队
        }

        double maxProgressX = 0;
        DataCart foremost = null;
        for (DataCart c : dataCarts) {
            int col = (int) (c.x / TILE_SIZE);
            // 只统计非返回且在公网区域的数据包
            if (!c.isReturnTrip && c.stage >= 25 && c.stage <= 31 && col >= 21 && col <= 34) {
                // 同样排除 DNS 包
                if (c.cartType != null && c.cartType.startsWith("DNS_")) {
                    continue;
                }
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
                // 修改 DNS_RESPONSE 的处理
                case "DNS_RESPONSE":
                    resolvedServerIp = cart.resolvedIp;
                    isDnsResolved = true;
                    isDnsResolving = false;

                    // 关键：更新 watchdog
                    stateTimerWatchdog = System.currentTimeMillis();

                    dnsCache.put(targetDomain, new DnsEntry(targetDomain, resolvedServerIp, 3600000));
                    updateDnsDisplay();
                    appendToConsole("【 DNS 解析成功】: " + targetDomain + " → " + resolvedServerIp);
                    performArpResolution(resolvedServerIp);

                    // 清除之前的定时器，避免立即重置
                    // 取消之前的 Timer（如果有）

                    // 直接启动 TCP 握手，不加延迟
                    if (!useUdp && currentTcpState == TcpState.CLOSED) {
                        startTcpHandshake();
                    } else if (useUdp && !udpActive) {
                        startUdpTransmission();
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

                    // 如果启用了 HTTP 演示，自动启动 DNS 解析
                    if (httpDemoEnabled && !isDnsResolved && !isDnsResolving) {
                        appendToConsole("【🔄 自动启动】: 开始 DNS 解析");
                        startDnsResolution();
                    }
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

                // 关键修复：增加已接收计数
                if (serverReceivedCount < totalDataToTransmit) {
                    serverReceivedCount++;
                }

                appendToConsole(String.format("【📦 UDP 数据】: SEQ=%d 已接收（无 ACK）, 进度 %d/%d",
                        cart.sequenceNumber, serverReceivedCount, totalDataToTransmit));
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
                case "FTP_DATA":
                    appendToConsole("【🔍 FTP_DATA】: FTP 数据包到达, SEQ=" + cart.sequenceNumber +
                            ", dstPort=" + cart.dstPort + ", srcPort=" + cart.srcPort);

                    if (cart.isArrived && !cart.isReturnTrip) {
                        // FTP 命令处理逻辑
                        String ftpCommandStr = null;

                        if (cart.ftpPayload != null && cart.ftpPayload.length > 0) {
                            ftpCommandStr = new String(cart.ftpPayload, java.nio.charset.StandardCharsets.ISO_8859_1).trim();
                        } else if (cart.ftpCommand != null && !cart.ftpCommand.isEmpty()) {
                            ftpCommandStr = cart.ftpCommand;
                        }

                        if (ftpCommandStr != null && !ftpCommandStr.isEmpty()) {
                            appendToConsole("【📁 FTP 服务器】: 收到命令 - " + ftpCommandStr);

                            // ========== 在这里定义 cmdUpper 变量 ==========
                            String cmdUpper = ftpCommandStr.toUpperCase();
                            // ==========================================

                            // 生成 FTP 响应
                            DataCart ftpResponse = new DataCart(serverPos.x, serverPos.y, "FTP_RESPONSE", 0);
                            ftpResponse.isReturnTrip = true;
                            ftpResponse.stage = -1;
                            ftpResponse.dstPort = cart.srcPort;
                            ftpResponse.srcPort = 21;
                            ftpResponse.sequenceNumber = cart.ackNumber;
                            ftpResponse.ackNumber = cart.sequenceNumber + 1;

                            FtpPacketFactory ftpFactory = factoryManager.getFtpPacketFactory();

                            if (cmdUpper.startsWith("USER")) {
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(331, "User name okay, need password");
                                appendToConsole("【📁 FTP 服务器】: 响应 331 - 需要密码");
                            } else if (cmdUpper.startsWith("PASS")) {
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(230, "User logged in successfully");
                                appendToConsole("【📁 FTP 服务器】: 响应 230 - 登录成功");
                                ftpLoggedIn = true;
                            } else if (cmdUpper.equals("PASV")) {
                                int dataPort = 60000 + (int)(Math.random() * 1000);
                                int p1 = dataPort / 256;
                                int p2 = dataPort % 256;
                                String pasvResp = String.format("227 Entering Passive Mode (192,168,1,100,%d,%d)", p1, p2);
                                ftpResponse.ftpPayload = pasvResp.getBytes();
                                appendToConsole(String.format("【📁 FTP 服务器】: 响应 227 - 进入被动模式，端口=%d", dataPort));
                                ftpDataPort = dataPort;
                            } else if (cmdUpper.equals("LIST")) {
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(150, "File status okay; about to open data connection");
                                appendToConsole("【📁 FTP 服务器】: 响应 150 - 准备打开数据连接");

                                Timer listTimer = new Timer(500, e -> {
                                    DataCart listComplete = new DataCart(serverPos.x, serverPos.y, "FTP_RESPONSE", 0);
                                    listComplete.isReturnTrip = true;
                                    listComplete.stage = -1;
                                    listComplete.dstPort = cart.srcPort;
                                    listComplete.srcPort = 21;
                                    listComplete.ftpPayload = ftpFactory.buildFtpResponse(226, "Closing data connection, directory listing sent");
                                    pendingDataCarts.add(listComplete);
                                    appendToConsole("【📁 FTP 服务器】: 响应 226 - 目录列表发送完成");
                                });
                                listTimer.setRepeats(false);
                                listTimer.start();
                            } else if (cmdUpper.equals("QUIT")) {
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(221, "Service closing control connection");
                                appendToConsole("【📁 FTP 服务器】: 响应 221 - 关闭连接");
                            } else {
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(500, "Command not recognized");
                                appendToConsole("【📁 FTP 服务器】: 响应 500 - 未知命令: " + ftpCommandStr);
                            }

                            pendingDataCarts.add(ftpResponse);
                            return;
                        }
                    }
                    break;
                case "SYN":
                case "DATA":
                    // 添加调试信息
                    appendToConsole("【🔍 DEBUG】: DATA包到达, cartType=" + cart.cartType +
                            ", SEQ=" + cart.sequenceNumber +
                            ", dstPort=" + cart.dstPort +
                            ", srcPort=" + cart.srcPort +
                            ", ftpPayload=" + (cart.ftpPayload != null ? "存在" : "null"));

                    // 如果是 FTP_DATA 类型，也可以在这里处理
                    if ("FTP_DATA".equals(cart.cartType)) {
                        appendToConsole("【📁 FTP】: 这是 FTP_DATA 类型的数据包");
                    }

                    if (cart.isArrived && !cart.isReturnTrip) {
                        // ========== FTP 命令处理（服务器端） ==========
                        // 检查目的端口是否是 FTP 端口 21
                        boolean isFtpPort = (cart.dstPort == 21);

                        // 添加调试信息
                        if (isFtpPort) {
                            appendToConsole("【🔍 FTP 调试】: 检测到 FTP 端口 21 的数据包");
                        }

                        // 尝试从不同来源获取 FTP 命令
                        String ftpCommandStr = null;

                        // 1. 优先从 ftpPayload 获取
                        if (cart.ftpPayload != null && cart.ftpPayload.length > 0) {
                            ftpCommandStr = new String(cart.ftpPayload, java.nio.charset.StandardCharsets.ISO_8859_1).trim();
                            appendToConsole("【📁 FTP 调试】: 从 ftpPayload 获取命令: " + ftpCommandStr);
                        }
                        // 2. 从 ftpCommand 获取
                        else if (cart.ftpCommand != null && !cart.ftpCommand.isEmpty()) {
                            ftpCommandStr = cart.ftpCommand;
                            appendToConsole("【📁 FTP 调试】: 从 ftpCommand 获取命令: " + ftpCommandStr);
                        }
                        // 3. 如果是 FTP 端口，根据 SEQ 号模拟命令（因为 ftpPayload 可能在传输中丢失）
                        else if (isFtpPort) {
                            if (cart.sequenceNumber == 100) {
                                ftpCommandStr = "USER anonymous";
                                appendToConsole("【📁 FTP 调试】: 根据 SEQ=100 模拟 USER 命令");
                            } else if (cart.sequenceNumber == 101) {
                                ftpCommandStr = "PASS anonymous@example.com";
                                appendToConsole("【📁 FTP 调试】: 根据 SEQ=101 模拟 PASS 命令");
                            } else if (cart.sequenceNumber == 102) {
                                ftpCommandStr = "PASV";
                                appendToConsole("【📁 FTP 调试】: 根据 SEQ=102 模拟 PASV 命令");
                            } else if (cart.sequenceNumber == 103) {
                                ftpCommandStr = "LIST";
                                appendToConsole("【📁 FTP 调试】: 根据 SEQ=103 模拟 LIST 命令");
                            } else if (cart.sequenceNumber == 104) {
                                ftpCommandStr = "QUIT";
                                appendToConsole("【📁 FTP 调试】: 根据 SEQ=104 模拟 QUIT 命令");
                            }
                        }

                        // 如果是 FTP 端口且有命令，处理 FTP
                        if (isFtpPort && ftpCommandStr != null && !ftpCommandStr.isEmpty()) {
                            appendToConsole("【📁 FTP 服务器】: 收到命令 - " + ftpCommandStr);

                            // 生成 FTP 响应
                            DataCart ftpResponse = new DataCart(serverPos.x, serverPos.y, "FTP_RESPONSE", 0);
                            ftpResponse.isReturnTrip = true;
                            ftpResponse.stage = -1;
                            ftpResponse.dstPort = cart.srcPort;  // 响应发送回客户端
                            ftpResponse.srcPort = 21;
                            ftpResponse.sequenceNumber = cart.ackNumber;  // 使用正确的序列号
                            ftpResponse.ackNumber = cart.sequenceNumber + 1;

                            FtpPacketFactory ftpFactory = factoryManager.getFtpPacketFactory();
                            String cmdUpper = ftpCommandStr.toUpperCase();

                            if (cmdUpper.startsWith("USER")) {
                                String username = ftpCommandStr.substring(4).trim();
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(331, "User name okay, need password for " + username);
                                appendToConsole("【📁 FTP 服务器】: 响应 331 - 需要密码");
                            }
                            else if (cmdUpper.startsWith("PASS")) {
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(230, "User logged in successfully");
                                appendToConsole("【📁 FTP 服务器】: 响应 230 - 登录成功");
                                // 更新 FTP 登录状态
                                ftpLoggedIn = true;
                            }
                            else if (cmdUpper.equals("PASV")) {
                                int dataPort = 60000 + (int)(Math.random() * 1000);
                                int p1 = dataPort / 256;
                                int p2 = dataPort % 256;
                                String pasvResp = String.format("227 Entering Passive Mode (192,168,1,100,%d,%d)", p1, p2);
                                ftpResponse.ftpPayload = pasvResp.getBytes();
                                appendToConsole(String.format("【📁 FTP 服务器】: 响应 227 - 进入被动模式，端口=%d", dataPort));
                                // 保存数据端口供后续使用
                                ftpDataPort = dataPort;
                            }
                            else if (cmdUpper.equals("LIST")) {
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(150, "File status okay; about to open data connection");
                                appendToConsole("【📁 FTP 服务器】: 响应 150 - 准备打开数据连接");

                                // 延迟发送 226 响应（模拟数据传输完成）
                                Timer listTimer = new Timer(500, e -> {
                                    DataCart listComplete = new DataCart(serverPos.x, serverPos.y, "FTP_RESPONSE", 0);
                                    listComplete.isReturnTrip = true;
                                    listComplete.stage = -1;
                                    listComplete.dstPort = cart.srcPort;
                                    listComplete.srcPort = 21;
                                    listComplete.ftpPayload = ftpFactory.buildFtpResponse(226, "Closing data connection, directory listing sent");
                                    pendingDataCarts.add(listComplete);
                                    appendToConsole("【📁 FTP 服务器】: 响应 226 - 目录列表发送完成");
                                });
                                listTimer.setRepeats(false);
                                listTimer.start();
                            }
                            else if (cmdUpper.equals("QUIT")) {
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(221, "Service closing control connection");
                                appendToConsole("【📁 FTP 服务器】: 响应 221 - 关闭连接");
                            }
                            else if (cmdUpper.startsWith("TYPE")) {
                                String type = cmdUpper.substring(4).trim();
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(200, "Type set to " + type);
                                appendToConsole("【📁 FTP 服务器】: 响应 200 - 传输类型设置为 " + type);
                            }
                            else if (cmdUpper.equals("SYST")) {
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(215, "UNIX Type: L8");
                                appendToConsole("【📁 FTP 服务器】: 响应 215 - 系统类型");
                            }
                            else if (cmdUpper.equals("NOOP")) {
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(200, "NOOP command successful");
                                appendToConsole("【📁 FTP 服务器】: 响应 200 - NOOP");
                            }
                            else if (cmdUpper.equals("PWD") || cmdUpper.equals("XPWD")) {
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(257, "\"/\" is current directory");
                                appendToConsole("【📁 FTP 服务器】: 响应 257 - 当前目录");
                            }
                            else {
                                ftpResponse.ftpPayload = ftpFactory.buildFtpResponse(500, "Command not recognized");
                                appendToConsole("【📁 FTP 服务器】: 响应 500 - 未知命令: " + ftpCommandStr);
                            }

                            pendingDataCarts.add(ftpResponse);
                            return;  // FTP 命令处理完毕，不继续执行下面的 ACK 逻辑
                        }
                        // ========== FTP 命令处理结束 ==========

                        // 记录接收统计
                        if (statisticsFactory != null) {
                            statisticsFactory.rx(1500);
                        }

                        if (serverBufferCount < SERVER_BUFFER_MAX) {
                            serverBufferCount++;
                            if (serverBufferCount == 1) lastServerConsumeTime = now;
                            rwnd = SERVER_BUFFER_MAX - serverBufferCount;

                            int receivedSeq = cart.sequenceNumber;
                            // 关键修复：只有 SEQ > 0 且未被确认过才处理
                            if (receivedSeq > 0 && !ackedSeq.contains(receivedSeq)) {
                                ackedSeq.add(receivedSeq);
                                DataCart dataAck = new DataCart(serverPos.x, serverPos.y, "DATA_ACK", receivedSeq);
                                dataAck.advertisedWindow = rwnd;
                                dataAck.isReturnTrip = true;
                                pendingDataCarts.add(dataAck);
                                funds += 500;
                                appendToConsole(String.format("【📦 数据交付】: SEQ=%d 已接收，回复 ACK (rwnd=%d)", receivedSeq, rwnd));
                            } else if (ackedSeq.contains(receivedSeq)) {
                                appendToConsole("【⚠️ 重复包】: SEQ=" + receivedSeq + " 已处理过");
                            } else if (receivedSeq <= 0) {
                                appendToConsole("【❌ 无效SEQ】: SEQ=" + receivedSeq + " 被忽略");
                            }
                        } else {
                            appendToConsole(String.format("【💥 缓冲区溢出】: SEQ=%d 丢失", cart.sequenceNumber));
                        }
                    }
                    break;
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
                    }
                    // ========== 新增：FTP 演示模式 ==========
                    else if (!useUdp && ftpDemoEnabled) {
                        // FTP 模式：开始 FTP 登录流程
                        appendToConsole("【📁 FTP】: 连接建立，开始 FTP 登录流程");
                        // FTP 登录流程将在 gameTick 中通过状态标志触发
                    }
                    // ========== 新增结束 ==========
                    else if (!useUdp && !httpDemoEnabled && !ftpDemoEnabled) {
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
                    appendToConsole("【🔍 ACK收到】: SEQ=" + cart.sequenceNumber + ", advertisedWindow=" + cart.advertisedWindow);
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

                            appendToConsole(String.format("【🔍 ACK处理】: cwnd=%d, rwnd=%d, effectiveWin=%d, unackedCount=%d, canSend=%d, 已发送=%d, 已确认=%d",
                                    cwnd, rwnd, effectiveWin, unackedCount, canSend, sentSeq.size(), ackedSeq.size()));

                            if (canSend > 0 && serverReceivedCount < totalDataToTransmit) {
                                sendDataPackets();
                            } else if (serverReceivedCount >= totalDataToTransmit) {
                                appendToConsole("【🏁 数据已发送完毕】: 等待确认");
                            } else {
                                appendToConsole("【⏸️ 无法发送】: canSend=" + canSend);
                            }
                        }
                        // 在handleCartArrival方法中，找到TCP ESTABLISHED后的数据处理部分
                        if (currentTcpState == TcpState.ESTABLISHED && !useUdp && !httpDemoEnabled) {
                            if (ftpDemoEnabled && !ftpLoggedIn) {
                                // FTP登录流程
                                if (!ftpLoginSent) {
                                    // 发送USER命令
                                    DataCart ftpUser = new DataCart(pcFactory.x, pcFactory.y, "FTP_USER", 0);
                                    ftpUser.stage = 5;
                                    ftpUser.ftpCommand = "USER anonymous";
                                    pendingDataCarts.add(ftpUser);
                                    ftpLoginSent = true;
                                    appendToConsole("【📁 FTP】: 发送 USER anonymous");
                                } else if (ftpLoginSent && !ftpPassSent) {
                                    // 发送PASS命令
                                    DataCart ftpPass = new DataCart(pcFactory.x, pcFactory.y, "FTP_PASS", 0);
                                    ftpPass.stage = 5;
                                    ftpPass.ftpCommand = "PASS anonymous@example.com";
                                    pendingDataCarts.add(ftpPass);
                                    ftpPassSent = true;
                                    appendToConsole("【📁 FTP】: 发送 PASS 命令");
                                }
                            } else if (ftpDemoEnabled && ftpLoggedIn && !ftpPasvMode) {
                                // 登录成功后进入PASV模式
                                DataCart ftpPasv = new DataCart(pcFactory.x, pcFactory.y, "FTP_PASV", 0);
                                ftpPasv.stage = 5;
                                ftpPasv.ftpCommand = "PASV";
                                pendingDataCarts.add(ftpPasv);
                                ftpPasvMode = true;
                                appendToConsole("【📁 FTP】: 请求被动模式 PASV");
                            } else if (ftpDemoEnabled && ftpLoggedIn && ftpPasvMode && ftpDataPort > 0) {
                                // PASV模式成功后，发送LIST命令
                                DataCart ftpList = new DataCart(pcFactory.x, pcFactory.y, "FTP_LIST", 0);
                                ftpList.stage = 5;
                                ftpList.ftpCommand = "LIST";
                                pendingDataCarts.add(ftpList);
                                appendToConsole("【📁 FTP】: 发送 LIST 命令");
                                ftpPasvMode = false; // 防止重复发送
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
                            demoCompleted = true;   // 标记本次演示结束，不再自动重启
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
                // ========== 新增：FTP 响应处理 ==========
                case "FTP_RESPONSE":
                    if (cart.isReturnTrip && cart.isArrived && cart.ftpPayload != null) {
                        FtpPacketFactory ftpFactory = factoryManager.getFtpPacketFactory();
                        int responseCode = ftpFactory.getResponseCode(cart.ftpPayload);

                        appendToConsole(String.format("【📁 FTP响应】: %d", responseCode));

                        if (responseCode == 331) {
                            // 收到 USER 命令的响应，可以发送 PASS
                            ftpUserAcked = true;
                            appendToConsole("【📁 FTP】: USER 命令确认，可以发送 PASS");
                        } else if (responseCode == 230) {
                            ftpLoggedIn = true;
                            ftpPassAcked = true;
                            appendToConsole("【📁 FTP】: 登录成功！");
                        } else if (responseCode == 227) {
                            int dataPort = ftpFactory.parsePassivePort(cart.ftpPayload);
                            ftpDataPort = dataPort;
                            appendToConsole(String.format("【📁 FTP】: PASV 响应，数据端口=%d", dataPort));
                        }
                    }
                    break;
            }
        }

    }

    private void resetTcpSession() {
        dnsRetryCount = 0;
        lastDnsQueryTime = 0;
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
        demoCompleted = false;
        factoryManager.reset();

        updateTopLabel();
        canvas.repaint();
    }

    private void sendDataPackets() {
        // 计算还能发送多少个包
        int sent = sentSeq.size();
        int acked = ackedSeq.size();
        int unacked = sent - acked;
        int window = Math.min(cwnd, rwnd);
        int canSend = window - unacked;

        if (canSend <= 0) {
            appendToConsole("【⏸️ 窗口已满】: cwnd=" + cwnd + ", rwnd=" + rwnd + ", unacked=" + unacked);
            return;
        }

        int packetsToSend = Math.min(canSend, totalDataToTransmit - serverReceivedCount);
        if (packetsToSend <= 0) {
            return;
        }

        appendToConsole(String.format("【📤 发送数据包】: cwnd=%d, rwnd=%d, sent=%d, acked=%d, unacked=%d, canSend=%d, 待发送=%d",
                cwnd, rwnd, sent, acked, unacked, canSend, packetsToSend));

        for (int i = 0; i < packetsToSend; i++) {
            int seq = nextSeqNum++;
            DataCart data = new DataCart(pcFactory.x, pcFactory.y, "DATA", seq);
            data.ttl = 64;
            data.advertisedWindow = rwnd;
            data.c_SEQ = true;  // 跳过 SEQ 重新分配
            pendingDataCarts.add(data);
            sentSeq.add(seq);

            RetransmissionTask task = new RetransmissionTask(seq, System.currentTimeMillis());
            activeTimers.add(task);

            appendToConsole(String.format("【📤 TCP 发送】: SEQ=%d", seq));
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
        // ========== FTP 相关字段 ==========
        private String ftpCommand;      // FTP命令字符串，如 "USER anonymous"
        private byte[] ftpPayload;      // FTP命令载荷（构建后的字节数组）
        private int ftpResponseCode;    // FTP响应码（用于接收端）
        private int ftpDataPort;        // FTP数据端口（PASV模式返回的端口）
        // ===================== 新增 20 个核心工厂引用 =====================
        private transient SocketFactory socketFactory;
        private transient TcpStateMachineFactory tcpStateMachineFactory;
        private transient MacTableFactory macTableFactory;
        private transient CamTableFactory camTableFactory;
        private transient ForwardingEngineFactory forwardingEngineFactory;
        private transient SessionTableFactory sessionTableFactory;
        private transient FlowFactory flowFactory;
        private transient LoadBalancerFactory loadBalancerFactory;
        private transient SchedulerFactory schedulerFactory;
        private transient DnsZoneFactory dnsZoneFactory;
        private transient DhcpLeaseFactory dhcpLeaseFactory;
        private transient ArpTableFactory arpTableFactory;
        private transient NeighborTableFactory neighborTableFactory;
        private transient MulticastRoutingFactory multicastRoutingFactory;
        private transient MplsLabelFactory mplsLabelFactory;
        private transient CertificateStoreFactory certificateStoreFactory;
        private transient EventFactory eventFactory;
        private transient StatisticsFactory statisticsFactory;
        private transient LogFactory logFactory;
        private transient PacketCaptureFactory packetCaptureFactory;

        // 新增状态标志
        private boolean hasSocket = false;
        private boolean hasMacLearning = false;
        private boolean hasFibLookup = false;
        private boolean hasFlowRecord = false;
        private boolean hasMplsLabel = false;
        private boolean hasDnsZone = false;
        private boolean hasDhcpLease = false;
        private boolean hasArpTable = false;

        // ===================== 新增 14 个工厂相关字段 =====================
        private boolean hasBitStream = false;      // 比特流已生成
        private boolean hasPhysicalEncoding = false; // 物理层编码已完成
        private boolean hasPppoe = false;          // PPPoE 已封装
        private boolean hasMacSec = false;         // MACsec 已加密
        private boolean hasOspf = false;           // OSPF 已处理
        private boolean hasBgp = false;            // BGP 已处理
        private boolean hasQos = false;            // QoS 已标记
        private boolean hasNat64 = false;          // NAT64 已转换
        private boolean isReassembled = false;     // TCP 重组已完成
        private boolean isAttackPacket = false;    // 是否为攻击包
        private boolean hasNtp = false;            // NTP 已处理
        private boolean hasSnmp = false;           // SNMP 已处理
        private boolean hasHttp23 = false;         // HTTP/2.3 已处理
        // ===================== HTTP 1.1 子工厂标志 =====================
        private boolean hasHttpReq = false;         // HTTP 请求工厂已处理
        private boolean hasHttpRes = false;         // HTTP 响应工厂已处理
        private boolean hasHttpHdr = false;         // HTTP 头部工厂已处理
        private boolean hasHttpBody = false;        // HTTP 消息体工厂已处理
        private boolean hasHttpCookie = false;      // HTTP Cookie 工厂已处理
        private boolean hasHttpAuth = false;        // HTTP 认证工厂已处理
        private boolean hasHttpCache = false;       // HTTP 缓存工厂已处理
        private boolean hasHttpChunked = false;     // HTTP 分块传输工厂已处理
        // ===================== HTTP/2 子工厂标志 =====================
        private boolean hasHttp2Frame = false;      // HTTP/2 帧工厂已处理
        private boolean hasHttp2Settings = false;   // HTTP/2 设置工厂已处理
        private boolean hasHttp2Stream = false;     // HTTP/2 流工厂已处理
        private boolean hasIpsec = false;          // IPsec 已加密
        // ===================== 新增：IPv6 相关字段 =====================
        private boolean hasIpv6 = false;
        private boolean hasIpv6Fragment = false;
        private boolean hasIpv6Option = false;
        private boolean hasIpv6Nd = false;
        private byte[] ipv6SrcAddr = new byte[16];
        private byte[] ipv6DstAddr = new byte[16];

        // ===================== 新增：TCP 增强字段 =====================
        private boolean hasKeepAlive = false;
        private boolean hasSack = false;
        private boolean hasEcn = false;
        private boolean hasFastOpen = false;

        // ===================== 新增：应用层协议字段 =====================
        private boolean hasFtp = false;
        private boolean hasSmtp = false;
        private boolean hasPop3 = false;
        private boolean hasImap = false;
        private boolean hasSsh = false;
        private boolean hasTelnet = false;
        private boolean hasRtp = false;
        private boolean hasSip = false;
        private boolean hasRadius = false;
        private String ftpUser = null;
        private String smtpFrom = null;
        private String smtpTo = null;
        private int rtpSeq = 0;

        // ===================== 新增：安全相关字段 =====================
        private boolean hasDpi = false;
        private boolean isBlockedByWaf = false;
        private boolean isRateLimited = false;
        private boolean isIpsecSecured = false;

        // ===================== 新增：VPN/隧道字段 =====================
        private boolean hasIpsecIke = false;
        private boolean hasIpsecEsp = false;
        private boolean hasOpenVpn = false;
        private boolean hasWireguard = false;
        private boolean hasL2tp = false;

        // ===================== 新增：IPv6 协议栈工厂 =====================
        private transient Ipv6PacketFactory ipv6PacketFactory;
        private transient Ipv6FragmentFactory ipv6FragmentFactory;
        private transient Ipv6OptionFactory ipv6OptionFactory;
        private transient Ipv6NeighborDiscovery ipv6NeighborDiscovery;

        // ===================== 新增：多播路由工厂 =====================
        private transient PimSmFactory pimSmFactory;
        private transient MldFactory mldFactory;
        private transient DvmrpFactory dvmrpFactory;

        // ===================== 新增：TCP 增强工厂 =====================
        private transient TcpKeepAliveFactory tcpKeepAliveFactory;
        private transient TcpSackFactory tcpSackFactory;
        private transient TcpEcnFactory tcpEcnFactory;
        private transient TcpFastOpenFactory tcpFastOpenFactory;

        // ===================== 新增：链路层增强工厂 =====================
        private transient LldpFactory lldpFactory;
        private transient StpFactory stpFactory;
        private transient LACPFactory lacpFactory;
        private transient MplsFactory mplsFactory;

        // ===================== 新增：应用层协议工厂 =====================
        private transient FtpPacketFactory ftpPacketFactory;
        private transient SmtpPacketFactory smtpPacketFactory;
        private transient Pop3PacketFactory pop3PacketFactory;
        private transient ImapPacketFactory imapPacketFactory;
        private transient SshPacketFactory sshPacketFactory;
        private transient TelnetPacketFactory telnetPacketFactory;
        private transient RtpPacketFactory rtpPacketFactory;
        private transient RtcpPacketFactory rtcpPacketFactory;
        private transient SipPacketFactory sipPacketFactory;
        private transient RadiusPacketFactory radiusPacketFactory;
        private transient DiameterPacketFactory diameterPacketFactory;
        private transient LdapPacketFactory ldapPacketFactory;

        // ===================== 新增：NAT 增强工厂 =====================
        private transient NatHairpinningFactory natHairpinningFactory;
        private transient NatHolePunchFactory natHolePunchFactory;
        private transient UpnpFactory upnpFactory;
        private transient PcpFactory pcpFactory;

        // ===================== 新增：负载均衡工厂 =====================
        private transient LbRoundRobinFactory lbRoundRobinFactory;
        private transient LbLeastConnFactory lbLeastConnFactory;
        private transient LbIpHashFactory lbIpHashFactory;
        private transient LbHealthCheckFactory lbHealthCheckFactory;

        // ===================== 新增：监控管理工厂 =====================
        private transient NetFlowFactory netFlowFactory;
        private transient SflowFactory sflowFactory;
        private transient IpfixFactory ipfixFactory;
        private transient IcmpPingFactory icmpPingFactory;
        private transient IcmpTracerouteFactory icmpTracerouteFactory;

        // ===================== 新增：VPN 隧道工厂 =====================
        private transient IpsecIkeFactory ipsecIkeFactory;
        private transient IpsecEspFactory ipsecEspFactory;
        private transient IpsecAhFactory ipsecAhFactory;
        private transient OpenVpnFactory openVpnFactory;
        private transient WireguardFactory wireguardFactory;
        private transient L2tpFactory l2tpFactory;
        private transient SstpFactory sstpFactory;

        // ===================== 新增：安全防火墙工厂 =====================
        private transient DpiFactory dpiFactory;
        private transient IpsFactory ipsFactory;
        private transient WafFactory wafFactory;
        private transient DdosMitigationFactory ddosMitigationFactory;
        private transient RateLimitFactory rateLimitFactory;

        // ===================== 新增：加密证书工厂 =====================
        private transient X509Factory x509Factory;
        private transient CrlFactory crlFactory;
        private transient OcspFactory ocspFactory;
        private transient PkiFactory pkiFactory;
        private transient DtlsFactory dtlsFactory;

        // ===================== 新增：访问控制工厂 =====================
        private transient AclFactory aclFactory;
        private transient MacAuthFactory macAuthFactory;
        private transient Dot1xFactory dot1xFactory;

        // ===================== 新增：诊断工具工厂 =====================
        private transient NetstatFactory netstatFactory;
        private transient IpconfigFactory ipconfigFactory;
        private transient RoutePrintFactory routePrintFactory;
        private transient NslookupFactory nslookupFactory;
        private transient ArpCommandFactory arpCommandFactory;
        private transient TelnetClientFactory telnetClientFactory;
        private transient CurlFactory curlFactory;
        private transient WgetFactory wgetFactory;

        // 新工厂实例引用
        private transient BitStreamFactory bitStreamFactory;
        private transient PhysicalChannelFactory physicalChannelFactory;
        private transient PppoeFactory pppoeFactory;
        private transient MacSecFactory macSecFactory;
        private transient OspfPacketFactory ospfPacketFactory;
        private transient BgpPacketFactory bgpPacketFactory;
        private transient QosTrafficFactory qosTrafficFactory;
        private transient Nat64Factory nat64Factory;
        private transient TcpReassemblyFactory tcpReassemblyFactory;
        private transient TransportAttackFactory transportAttackFactory;
        private transient NtpPacketFactory ntpPacketFactory;
        private transient SnmpPacketFactory snmpPacketFactory;
        private transient Http23PacketFactory http23PacketFactory;
        private transient IpsecFactory ipsecFactory;
        // === 新增工厂相关字段 ===
        private boolean hasIpOption = false;      // IP 选项是否已添加
        private boolean hasTcpOption = false;     // TCP 选项是否已添加
        private boolean hasUdpChecksum = false;   // UDP 校验和是否已计算
        private boolean hasEtherPadding = false;  // 以太网填充是否已添加
        private boolean hasTunnel = false;        // 隧道封装是否已添加
        private boolean hasVlan = false;          // VLAN 标签是否已添加
        private boolean hasIgmp = false;          // IGMP 是否已处理
        private boolean hasNdp = false;           // NDP 是否已处理
        private boolean isEncapsulated = false;   // 是否已隧道封装
        private int vlanId = 0;                  // VLAN ID
        //        -=-------------------
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

        public enum PacketClass {
            CONTROL_FAST,      // DNS、DHCP、ICMP 控制包 - 最快路径
            TCP_CONTROL,       // SYN、ACK、FIN - 中等路径
            TCP_DATA,          // 普通数据 - 完整路径
            UDP_DATA           // UDP 数据 - 跳过 TCP 层
        }

        private PacketClass packetClass;
        // 在 DataCart 类中添加视觉反馈相关字段
        private static class VisualFeedback {
            String label;
            Color color;
            long timestamp;

            VisualFeedback(String label, Color color) {
                this.label = label;
                this.color = color;
                this.timestamp = System.currentTimeMillis();
            }
        }
        private List<VisualFeedback> visualFeedbacks = new CopyOnWriteArrayList<>();
        private long lastVisualUpdate = 0;
        private static final long VISUAL_FEEDBACK_DURATION = 2000; // 2秒
        // 添加视觉反馈方法
        public void addVisualFeedback(String label, Color color) {
            visualFeedbacks.add(new VisualFeedback(label, color));
            lastVisualUpdate = System.currentTimeMillis();
        }

        public List<VisualFeedback> getVisualFeedbacks() {
            // 清理过期的反馈
            long now = System.currentTimeMillis();
            visualFeedbacks.removeIf(f -> now - f.timestamp > VISUAL_FEEDBACK_DURATION);
            return visualFeedbacks;
        }


        /**
         * IP 地址字符串转 int
         */
        private int ipToInt(String ip) {
            if (ip == null || ip.isEmpty()) return 0;
            try {
                String[] parts = ip.split("\\.");
                if (parts.length != 4) return 0;
                int result = 0;
                for (int i = 0; i < 4; i++) {
                    int part = Integer.parseInt(parts[i]);
                    result |= (part << (24 - (8 * i)));
                }
                return result;
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        /**
         * int 转 IP 地址字符串
         */
        private String intToIp(int ip) {
            return ((ip >> 24) & 0xFF) + "." +
                    ((ip >> 16) & 0xFF) + "." +
                    ((ip >> 8) & 0xFF) + "." +
                    (ip & 0xFF);
        }

        private void initPacketClass() {
            // 添加空值检查，防止 NullPointerException
            if (cartType == null) {
                packetClass = PacketClass.TCP_DATA; // 默认值
                return;
            }

            if (cartType.startsWith("DNS_") || cartType.startsWith("DHCP_") ||
                    cartType.equals("ICMP_ECHO_REQ") || cartType.equals("ICMP_ECHO_REPLY") ||
                    cartType.equals("ICMP_TIMEEXCEEDED")) {
                packetClass = PacketClass.CONTROL_FAST;
            } else if (cartType.equals("SYN") || cartType.equals("SYN_ACK") ||
                    cartType.equals("ACK_PC") || cartType.equals("FIN_PC") ||
                    cartType.equals("FIN_ACK_SRV") || cartType.equals("FIN_SRV") ||
                    cartType.equals("LAST_ACK_PC") || cartType.equals("ZWP")) {
                packetClass = PacketClass.TCP_CONTROL;
            } else if (cartType.equals("DATA") || cartType.equals("HTTP_GET") ||
                    cartType.equals("HTTP_200_OK") || cartType.startsWith("TLS_")) {
                packetClass = PacketClass.TCP_DATA;
            } else if (cartType.equals("UDP_DATA")) {
                packetClass = PacketClass.UDP_DATA;
            } else {
                packetClass = PacketClass.TCP_DATA; // 默认值
            }
        }

        // 在 DataCart 构造函数中，修改 UDP/TLS 的 stage
        public DataCart(double sx, double sy, String type, int seq) {

            this.srcPort = 1234;  // 默认源端口
            this.dstPort = 443;   // 默认目的端口
            this.protocol = "TCP";
            this.x = sx;
            this.y = sy;
            this.cartType = type;
            this.sequenceNumber = seq;
            initPacketClass();

            // DATA 包特殊处理：保留 stage 和 sequenceNumber
            if (type.equals("DATA")) {
                this.stage = 1;
                // 关键：不要覆盖 sequenceNumber
                return;
            }
            // DNS 相关包的特殊处理 - 走 DNS 专用路径
            if (type.equals("DNS_QUERY") || type.equals("DNS_RESPONSE") ||
                    type.equals("DNS_RECURSION_ROOT") || type.equals("DNS_ROOT_TO_LOCAL") ||
                    type.equals("DNS_LOCAL_TO_AUTH") || type.equals("DNS_AUTH_TO_LOCAL") ||
                    type.equals("DNS_RECURSION_AUTH") || type.equals("DNS_RECURSION_AUTH_RESP")) {
                this.stage = 1;  // 从 DNS 客户端开始
                this.protocol = "DNS";
                return;
            }

            if (type.equals("ICMP_TIMEEXCEEDED") || type.equals("ICMP_ECHO_REPLY") || type.equals("HTTP_200_OK")) {
                this.isReturnTrip = true;
                this.stage = -1;
            } else if (isControlFrame(type)) {
                this.stage = 2;
            } else if (type.startsWith("TLS_") || type.equals("HTTP_GET") || type.equals("UDP_DATA")) {
                this.stage = 5;
            } else {
                this.stage = 1;
            }
// ===================== 初始化 14 个新工厂引用 =====================
            if (factoryManager != null) {
                this.bitStreamFactory = factoryManager.getBitStreamFactory();
                this.physicalChannelFactory = factoryManager.getPhysicalChannelFactory();
                this.pppoeFactory = factoryManager.getPppoeFactory();
                this.macSecFactory = factoryManager.getMacSecFactory();
                this.ospfPacketFactory = factoryManager.getOspfPacketFactory();
                this.bgpPacketFactory = factoryManager.getBgpPacketFactory();
                this.qosTrafficFactory = factoryManager.getQosTrafficFactory();
                this.nat64Factory = factoryManager.getNat64Factory();
                this.tcpReassemblyFactory = factoryManager.getTcpReassemblyFactory();
                this.transportAttackFactory = factoryManager.getTransportAttackFactory();
                this.ntpPacketFactory = factoryManager.getNtpPacketFactory();
                this.snmpPacketFactory = factoryManager.getSnmpPacketFactory();
                this.http23PacketFactory = factoryManager.getHttp23PacketFactory();
                this.ipsecFactory = factoryManager.getIpsecFactory();
            }
            // ===================== 初始化新增工厂引用 =====================
            if (factoryManager != null) {
                this.ipv6PacketFactory = factoryManager.getIpv6PacketFactory();
                this.ipv6FragmentFactory = factoryManager.getIpv6FragmentFactory();
                this.ipv6OptionFactory = factoryManager.getIpv6OptionFactory();
                this.ipv6NeighborDiscovery = factoryManager.getIpv6NeighborDiscovery();

                this.pimSmFactory = factoryManager.getPimSmFactory();
                this.mldFactory = factoryManager.getMldFactory();
                this.dvmrpFactory = factoryManager.getDvmrpFactory();

                this.tcpKeepAliveFactory = factoryManager.getTcpKeepAliveFactory();
                this.tcpSackFactory = factoryManager.getTcpSackFactory();
                this.tcpEcnFactory = factoryManager.getTcpEcnFactory();
                this.tcpFastOpenFactory = factoryManager.getTcpFastOpenFactory();

                this.lldpFactory = factoryManager.getLldpFactory();
                this.stpFactory = factoryManager.getStpFactory();
                this.lacpFactory = factoryManager.getLacpFactory();
                this.mplsFactory = factoryManager.getMplsFactory();

                this.ftpPacketFactory = factoryManager.getFtpPacketFactory();
                this.smtpPacketFactory = factoryManager.getSmtpPacketFactory();
                this.pop3PacketFactory = factoryManager.getPop3PacketFactory();
                this.imapPacketFactory = factoryManager.getImapPacketFactory();
                this.sshPacketFactory = factoryManager.getSshPacketFactory();
                this.telnetPacketFactory = factoryManager.getTelnetPacketFactory();
                this.rtpPacketFactory = factoryManager.getRtpPacketFactory();
                this.rtcpPacketFactory = factoryManager.getRtcpPacketFactory();
                this.sipPacketFactory = factoryManager.getSipPacketFactory();
                this.radiusPacketFactory = factoryManager.getRadiusPacketFactory();
                this.diameterPacketFactory = factoryManager.getDiameterPacketFactory();
                this.ldapPacketFactory = factoryManager.getLdapPacketFactory();

                this.natHairpinningFactory = factoryManager.getNatHairpinningFactory();
                this.natHolePunchFactory = factoryManager.getNatHolePunchFactory();
                this.upnpFactory = factoryManager.getUpnpFactory();
                this.pcpFactory = factoryManager.getPcpFactory();

                this.lbRoundRobinFactory = factoryManager.getLbRoundRobinFactory();
                this.lbLeastConnFactory = factoryManager.getLbLeastConnFactory();
                this.lbIpHashFactory = factoryManager.getLbIpHashFactory();
                this.lbHealthCheckFactory = factoryManager.getLbHealthCheckFactory();

                this.netFlowFactory = factoryManager.getNetFlowFactory();
                this.sflowFactory = factoryManager.getSflowFactory();
                this.ipfixFactory = factoryManager.getIpfixFactory();
                this.icmpPingFactory = factoryManager.getIcmpPingFactory();
                this.icmpTracerouteFactory = factoryManager.getIcmpTracerouteFactory();

                this.ipsecIkeFactory = factoryManager.getIpsecIkeFactory();
                this.ipsecEspFactory = factoryManager.getIpsecEspFactory();
                this.ipsecAhFactory = factoryManager.getIpsecAhFactory();
                this.openVpnFactory = factoryManager.getOpenVpnFactory();
                this.wireguardFactory = factoryManager.getWireguardFactory();
                this.l2tpFactory = factoryManager.getL2tpFactory();
                this.sstpFactory = factoryManager.getSstpFactory();

                this.dpiFactory = factoryManager.getDpiFactory();
                this.ipsFactory = factoryManager.getIpsFactory();
                this.wafFactory = factoryManager.getWafFactory();
                this.ddosMitigationFactory = factoryManager.getDdosMitigationFactory();
                this.rateLimitFactory = factoryManager.getRateLimitFactory();

                this.x509Factory = factoryManager.getX509Factory();
                this.crlFactory = factoryManager.getCrlFactory();
                this.ocspFactory = factoryManager.getOcspFactory();
                this.pkiFactory = factoryManager.getPkiFactory();
                this.dtlsFactory = factoryManager.getDtlsFactory();

                this.aclFactory = factoryManager.getAclFactory();
                this.macAuthFactory = factoryManager.getMacAuthFactory();
                this.dot1xFactory = factoryManager.getDot1xFactory();

                this.netstatFactory = factoryManager.getNetstatFactory();
                this.ipconfigFactory = factoryManager.getIpconfigFactory();
                this.routePrintFactory = factoryManager.getRoutePrintFactory();
                this.nslookupFactory = factoryManager.getNslookupFactory();
                this.arpCommandFactory = factoryManager.getArpCommandFactory();
                this.telnetClientFactory = factoryManager.getTelnetClientFactory();
                this.curlFactory = factoryManager.getCurlFactory();
                this.wgetFactory = factoryManager.getWgetFactory();
            }
// ===================== 初始化 20 个核心工厂引用 =====================
            if (factoryManager != null) {
                this.socketFactory = factoryManager.getSocketFactory();
                this.tcpStateMachineFactory = factoryManager.getTcpStateMachineFactory();
                this.macTableFactory = factoryManager.getMacTableFactory();
                this.camTableFactory = factoryManager.getCamTableFactory();
                this.forwardingEngineFactory = factoryManager.getForwardingEngineFactory();
                this.sessionTableFactory = factoryManager.getSessionTableFactory();
                this.flowFactory = factoryManager.getFlowFactory();
                this.loadBalancerFactory = factoryManager.getLoadBalancerFactory();
                this.schedulerFactory = factoryManager.getSchedulerFactory();
                this.dnsZoneFactory = factoryManager.getDnsZoneFactory();
                this.dhcpLeaseFactory = factoryManager.getDhcpLeaseFactory();
                this.arpTableFactory = factoryManager.getArpTableFactory();
                this.neighborTableFactory = factoryManager.getNeighborTableFactory();
                this.multicastRoutingFactory = factoryManager.getMulticastRoutingFactory();
                this.mplsLabelFactory = factoryManager.getMplsLabelFactory();
                this.certificateStoreFactory = factoryManager.getCertificateStoreFactory();
                this.eventFactory = factoryManager.getEventFactory();
                this.statisticsFactory = factoryManager.getStatisticsFactory();
                this.logFactory = factoryManager.getLogFactory();
                this.packetCaptureFactory = factoryManager.getPacketCaptureFactory();
            }
            // 在 DataCart 构造函数中修改 FTP 相关类型的 stage
            // 在 DataCart 构造函数中修改 FTP 相关类型的处理

            if (type.equals("FTP_USER")) {
                this.stage = 161;              // 从 FTP_AUTH 开始
                this.protocol = "FTP";
                this.dstPort = 21;             // FTP 控制端口 21
                this.srcPort = 1234;           // 源端口
                this.ftpCommand = "USER anonymous";
                this.cartType = "FTP_DATA";
                this.packetClass = PacketClass.TCP_DATA;
                appendToConsole("【📁 FTP】: 创建 FTP_USER 包, stage=161, dstPort=21");
            } else if (type.equals("FTP_PASS")) {
                this.stage = 161;
                this.protocol = "FTP";
                this.dstPort = 21;
                this.srcPort = 1234;
                this.ftpCommand = "PASS anonymous@example.com";
                this.cartType = "FTP_DATA";
                this.packetClass = PacketClass.TCP_DATA;
            } else if (type.equals("FTP_PASV")) {
                this.stage = 161;
                this.protocol = "FTP";
                this.dstPort = 21;
                this.srcPort = 1234;
                this.ftpCommand = "PASV";
                this.cartType = "FTP_DATA";
                this.packetClass = PacketClass.TCP_DATA;
            } else if (type.equals("FTP_LIST")) {
                this.stage = 161;
                this.protocol = "FTP";
                this.dstPort = 21;
                this.srcPort = 1234;
                this.ftpCommand = "LIST";
                this.cartType = "FTP_DATA";
                this.packetClass = PacketClass.TCP_DATA;
            } else if (type.equals("FTP_QUIT")) {
                this.stage = 161;
                this.protocol = "FTP";
                this.dstPort = 21;
                this.srcPort = 1234;
                this.ftpCommand = "QUIT";
                this.cartType = "FTP_DATA";
                this.packetClass = PacketClass.TCP_DATA;
            }
        }

        private String getSrcIp() {
            return srcIp != null ? srcIp : (pcIpAddress != null ? pcIpAddress : "192.168.1.100");
        }

        private String getDstIp() {
            return dstIp != null ? dstIp : (resolvedServerIp != null ? resolvedServerIp : "10.0.0.1");
        }

        // 替换原有的 isControlFrame 方法
        public boolean isControlFrame(String type) {
            // DNS 查询和响应不是 TCP 控制帧，应该走完整协议栈
            if (type.equals("DNS_QUERY") || type.equals("DNS_RESPONSE") ||
                    type.equals("DNS_RECURSION_ROOT") || type.equals("DNS_ROOT_TO_LOCAL") ||
                    type.equals("DNS_LOCAL_TO_AUTH") || type.equals("DNS_AUTH_TO_LOCAL") ||
                    type.equals("DNS_RECURSION_AUTH") || type.equals("DNS_RECURSION_AUTH_RESP")) {
                return false;
            }
            return type.equals("SYN") || type.equals("SYN_ACK") || type.equals("ACK_PC") ||
                    type.equals("FIN_PC") || type.equals("FIN_ACK_SRV") || type.equals("FIN_SRV") ||
                    type.equals("DATA_ACK") || type.equals("LAST_ACK_PC") || type.equals("ZWP") ||
                    type.equals("DHCP_DISCOVER") || type.equals("DHCP_OFFER") ||
                    type.equals("DHCP_REQUEST") || type.equals("DHCP_ACK");
        }

        public void update() {
            // DNS 包走专用快速路径，完全不经过公网队列
            if (cartType != null && cartType.startsWith("DNS_") && !isReturnTrip) {
                Point target = findTargetMachine(stage, cartType);
                if (target != null) {
                    double dx = target.x - x;
                    double dy = target.y - y;
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    if (dist <= 8) {  // 移动速度快
                        x = target.x;
                        y = target.y;
                        processStageCraft();
                        if (cartType.equals("DNS_QUERY")) {
                            if (stage < 4) {
                                timer = 0;
                                stage++;
                            } else {
                                isArrived = true;
                            }
                        } else if (cartType.equals("DNS_RESPONSE")) {
                            isArrived = true;
                        } else {
                            if (stage < 2) {
                                timer = 0;
                                stage++;
                            } else {
                                isArrived = true;
                            }
                        }
                    } else {
                        x += (dx / dist) * 8;
                        y += (dy / dist) * 8;
                    }
                    return;
                }
            }

            // 原有代码保持不变...
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
                    if (!isReturnTrip || isDnsOrDhcp()) {
                        processStageCraft();

                        // ========== FTP 子工厂特殊处理（stage 161-165）==========
                        if (ftpCommand != null && !ftpCommand.isEmpty() && stage >= 161 && stage <= 165) {
                            if (stage < 165) {
                                timer = 1;
                                stage++;
                                appendToConsole("【📁 FTP】: stage " + (stage - 1) + " → " + stage);
                                return;
                            } else if (stage == 165) {
                                if (timer == 0) {
                                    timer = 1;
                                }
                                return;
                            }
                        }

                        // ========== 原有的 NAT 和 TTL 处理 ==========
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

                        // ========== 常规 stage 递增 ==========
                        if (!isDnsOrDhcp()) {
                            if (stage < 160) {
                                timer = 1;
                                stage++;
                            } else {
                                isArrived = true;
                            }
                        } else {
                            // DNS/DHCP 处理
                            int maxStage = 0;
                            if (cartType.equals("DNS_QUERY")) {
                                maxStage = 4;
                            } else if (cartType.startsWith("DNS_RECURSION")) {
                                maxStage = 2;
                            } else if (cartType.startsWith("DHCP")) {
                                maxStage = 2;
                            } else {
                                maxStage = 2;
                            }
                            if (stage < maxStage) {
                                timer = 1;
                                stage++;
                            } else {
                                isArrived = true;
                            }
                        }
                    }else {
                        isArrived = true;
                    }
                } else {
                    x += (dx / dist) * speed;
                    y += (dy / dist) * speed;
                }
                return;
            }

            if (isDnsOrDhcp()) {
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
                if (!isReturnTrip || isDnsOrDhcp()) {
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
                    // ========== FTP 子工厂特殊处理（stage 161-165）==========
                    if (ftpCommand != null && !ftpCommand.isEmpty() && stage >= 161 && stage <= 165) {
                        if (stage < 165) {
                            timer = 1;
                            stage++;
                            appendToConsole("【📁 FTP】: stage " + (stage - 1) + " → " + stage);
                            return;
                        } else if (stage == 165) {
                            stage = 5;
                            appendToConsole("【📁 FTP】: FTP 完成，进入应用层 stage=5");
                        }
                    }
                    // ========== NAT 和 TTL 处理 ==========
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
                    // 在 update 方法中找到处理 stage 递增的部分，修改为：
                    if (!isDnsOrDhcp()) {
                        if (stage < 160) {
                            timer = 1;
                            stage++;
                        } else {
                            isArrived = true;
                        }
                    } else {
                        // DNS 和 DHCP 包的最大 stage 不同
                        int maxStage = 0;
                        if (cartType.equals("DNS_QUERY")) {
                            maxStage = 4;  // 经过 DNS_CLIENT -> DNS_LOCAL -> DNS_ROOT -> DNS_AUTH
                        } else if (cartType.equals("DNS_RESPONSE")) {
                            maxStage = 1;
                        } else if (cartType.startsWith("DNS_RECURSION")) {
                            maxStage = 2;
                        } else if (cartType.startsWith("DHCP")) {
                            maxStage = 2;
                        } else {
                            maxStage = 2;
                        }
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
            // 避免重复创建 NAT 映射
            if (isNatted) return;

            String insideIp = pcIpAddress != null ? pcIpAddress : "192.168.1.100";
            int insidePort = srcPort > 0 ? srcPort : 1234;

            String key = insideIp + ":" + insidePort;

            // 使用 factoryManager 的 natFactory
            if (!natTable.containsKey(key)) {
                NatMappingFactory.NatEntry factoryEntry = factoryManager.getNatFactory().createMapping(insideIp, insidePort);
                NatEntry localEntry = new NatEntry(factoryEntry.getInsideIp(), factoryEntry.getInsidePort(),
                        factoryEntry.getPublicIp(), factoryEntry.getPublicPort());
                natTable.put(key, localEntry);
            }

            NatEntry entry = natTable.get(key);
            this.isNatted = true;
            this.natPublicIp = entry.publicIp;
            this.natPublicPort = entry.publicPort;
        }


        // 替换原有的 isDHCP 方法
        private boolean isDnsOrDhcp() {
            return cartType.startsWith("DHCP") || cartType.startsWith("DNS_");
        }

        // 在 DataCart 类中，修改 stage 对应的 tag 映射
        private Point findTargetMachine(int s, String type) {
// 确保 TCP 控制包的 stage 映射正确
            if (type.equals("SYN") || type.equals("SYN_ACK") || type.equals("ACK_PC")) {
                if (s == 13) return findBuildingCoords("T_CORE");
                if (s == 14) return findBuildingCoords("TX_IPH");
                if (s == 15) return findBuildingCoords("TX_ARP");
                if (s == 16) return findBuildingCoords("ETH_DST");
                if (s == 17) return findBuildingCoords("ETH_SRC");
                if (s == 18) return findBuildingCoords("ETH_TYPE");
                if (s == 19) return findBuildingCoords("TX_FCS");
                if (s == 20) return findBuildingCoords("R_LAN");
                if (s == 21) return findBuildingCoords("R_TAB");
                if (s == 22) return findBuildingCoords("R_NAT");
                if (s == 23) return findBuildingCoords("R_WAN");
                if (s == 24) return findBuildingCoords("ROUTER1");
                if (s == 25) return findBuildingCoords("ROUTER2");
                if (s == 26) return findBuildingCoords("ROUTER3");
                if (s == 27) return findBuildingCoords("RX_ETH");
                if (s == 28) return findBuildingCoords("RX_IP");
                if (s == 29) return findBuildingCoords("RX_TCP");
                if (s == 30) return findBuildingCoords("RX_APP");
            }
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
            if (type.equals("DNS_QUERY")) {
                switch (s) {
                    case 1:
                        return findBuildingCoords("DNS_CLIENT");
                    case 2:
                        return findBuildingCoords("DNS_LOCAL");
                    case 3:
                        return findBuildingCoords("DNS_ROOT");
                    case 4:
                        return findBuildingCoords("DNS_AUTH");
                    default:
                        return null;
                }
            }
            if (type.equals("DNS_RESPONSE")) {
                // 响应直接返回 PC
                return findBuildingCoords("PC_FACTORY");
            }
            if (type.equals("DNS_RECURSION_ROOT") || type.equals("DNS_RECURSION_AUTH")) {
                // 递归查询转发
                return findBuildingCoords("DNS_ROOT");
            }
            if (type.equals("DNS_ROOT_TO_LOCAL") || type.equals("DNS_AUTH_TO_LOCAL")) {
                return findBuildingCoords("DNS_LOCAL");
            }
            if (type.equals("DNS_LOCAL_TO_AUTH")) {
                return findBuildingCoords("DNS_AUTH");
            }
            if (type.equals("DNS_RECURSION_ROOT_RESP") || type.equals("DNS_RECURSION_AUTH_RESP")) {
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
                    case 1:
                        return findBuildingCoords("DHCP_DISC");
                    case 2:
                        return findBuildingCoords("DHCP_SERVER");
                }
            } else if (type.equals("DHCP_OFFER")) {
                switch (s) {
                    case 1:
                        return findBuildingCoords("DHCP_OFFER");
                    case 2:
                        return findBuildingCoords("PC_FACTORY");
                }
            } else if (type.equals("DHCP_REQUEST")) {
                switch (s) {
                    case 1:
                        return findBuildingCoords("DHCP_REQ");
                    case 2:
                        return findBuildingCoords("DHCP_SERVER");
                }
            } else if (type.equals("DHCP_ACK")) {
                switch (s) {
                    case 1:
                        return findBuildingCoords("DHCP_ACK");
                    case 2:
                        return findBuildingCoords("PC_FACTORY");
                }
            }

            // ========== 完整的 stage 到建筑映射 ==========
            String tag = "NONE";
            switch (s) {
                // DNS 解析路径
                case 1:
                    tag = "DNS_CLIENT";
                    break;
                case 2:
                    tag = "DNS_LOCAL";
                    break;
                case 3:
                    tag = "DNS_ROOT";
                    break;
                case 4:
                    tag = "DNS_AUTH";
                    break;

                // 应用层
                case 5:
                    tag = "TX_APP";
                    break;

                // 传输层封装
                case 6:
                    tag = "T_SP";
                    break;
                case 7:
                    tag = "T_DP";
                    break;
                case 8:
                    tag = "T_SEQ";
                    break;
                case 9:
                    tag = "T_ACK";
                    break;
                case 10:
                    tag = "T_CTL";
                    break;
                case 11:
                    tag = "T_WIN";
                    break;
                case 12:
                    tag = "T_CHK";
                    break;
                case 13:
                    tag = "T_CORE";
                    break;

                // 网络层封装
                case 14:
                    tag = "TX_IPH";
                    break;
                case 15:
                    tag = "TX_IP_FRAG";
                    break;
                case 16:
                    tag = "TX_ARP";
                    break;

                // 链路层封装
                case 17:
                    tag = "ETH_DST";
                    break;
                case 18:
                    tag = "ETH_SRC";
                    break;
                case 19:
                    tag = "ETH_TYPE";
                    break;
                case 20:
                    tag = "TX_LLC";
                    break;
                case 21:
                    tag = "TX_FCS";
                    break;

                // 五元组和会话（新增加）
                case 22:
                    tag = "FIVETUPLE";
                    break;
                case 23:
                    tag = "SESSION";
                    break;

                // 边界网关
                case 24:
                    tag = "R_LAN";
                    break;
                case 25:
                    tag = "R_TAB";
                    break;
                case 26:
                    tag = "R_NAT";
                    break;
                case 27:
                    tag = "BW_CTRL";
                    break;      // 带宽控制
                case 28:
                    tag = "R_WAN";
                    break;

                // 防火墙（新增加）
                case 29:
                    tag = "FW_OUT";
                    break;       // 出站防火墙
                case 30:
                    tag = "FW_IN";
                    break;        // 入站防火墙

                // 公网路由器
                case 31:
                    tag = "ROUTER1";
                    break;
                case 32:
                    tag = "ROUTER2";
                    break;
                case 33:
                    tag = "ROUTER3";
                    break;

                // 队列（新增加）
                case 34:
                    tag = "Q_IN";
                    break;
                case 35:
                    tag = "Q_OUT";
                    break;
                case 36:
                    tag = "Q_DROP";
                    break;

                // 接收端链路层解封
                case 37:
                    tag = "RX_ETH";
                    break;
                case 38:
                    tag = "RX_LLC";
                    break;
                case 39:
                    tag = "RX_FCS";
                    break;
                case 40:
                    tag = "RX_ARP";
                    break;

                // 接收端网络层解封
                case 41:
                    tag = "RX_FRAG";
                    break;
                case 42:
                    tag = "RX_IP";
                    break;

                // 接收端传输层解封
                case 43:
                    tag = "RX_PORT";
                    break;
                case 44:
                    tag = "RX_TCP";
                    break;

                // 接收端应用层交付
                case 45:
                    tag = "RX_APP";
                    break;
                case 46:
                    tag = "TCP_OPTION";
                    break;     // TCP 选项处理
                case 47:
                    tag = "IP_OPTION";
                    break;      // IP 选项处理
                case 48:
                    tag = "ETH_PADDING";
                    break;    // 以太网填充
                case 49:
                    tag = "UDP_CHECKSUM";
                    break;   // UDP 校验和
                case 50:
                    tag = "ICMP_ERROR";
                    break;     // ICMP 错误生成
                case 51:
                    tag = "IP_FORWARD";
                    break;     // IP 路由转发
                case 52:
                    tag = "TCP_WINDOW";
                    break;     // TCP 窗口管理
                case 53:
                    tag = "TCP_TIMER";
                    break;      // TCP 定时器
                case 54:
                    tag = "VLAN_TAG";
                    break;       // VLAN 标签
                case 55:
                    tag = "TUNNEL_GRE";
                    break;     // GRE 隧道
                case 56:
                    tag = "IGMP_MCAST";
                    break;     // IGMP 组播
                case 57:
                    tag = "NDP_DISC";
                    break;       // NDP 发现
                case 58:
                    tag = "DNS_RECURSIVE";
                    break;  // DNS 递归
                case 59:
                    tag = "DHCP_FULL";
                    break;      // DHCP 完整报文
                case 60:
                    tag = "TLS_HANDSHAKE";
                    break;  // TLS 握手
                case 61:
                    tag = "SERIALIZE";
                    break;      // 序列化
                // 在 switch 语句中添加新的 case
                case 62:
                    tag = "BIT_STREAM";
                    break;      // 比特流
                case 63:
                    tag = "PHY_CHANNEL";
                    break;     // 物理信道
                case 64:
                    tag = "PPPOE";
                    break;           // PPPoE
                case 65:
                    tag = "MACSEC";
                    break;          // MACsec
                case 66:
                    tag = "OSPF";
                    break;            // OSPF
                case 67:
                    tag = "BGP";
                    break;             // BGP
                case 68:
                    tag = "QOS";
                    break;             // QoS
                case 69:
                    tag = "NAT64";
                    break;           // NAT64
                case 70:
                    tag = "TCP_REASSEMBLY";
                    break;  // TCP重组
                case 71:
                    tag = "ATTACK";
                    break;          // 攻击检测
                case 72:
                    tag = "NTP";
                    break;             // NTP
                case 73:
                    tag = "SNMP";
                    break;            // SNMP
                case 74:
                    tag = "HTTP23";
                    break;          // HTTP/2.3
                case 75:
                    tag = "IPSEC";
                    break;           // IPsec
                case 76:
                    tag = "IPV6";
                    break;
                case 77:
                    tag = "IPV6_FRAG";
                    break;
                case 78:
                    tag = "IPV6_OPTION";
                    break;
                case 79:
                    tag = "IPV6_ND";
                    break;
                case 80:
                    tag = "TCP_KEEPALIVE";
                    break;
                case 81:
                    tag = "TCP_SACK";
                    break;
                case 82:
                    tag = "TCP_ECN";
                    break;
                case 83:
                    tag = "TCP_FASTOPEN";
                    break;
                case 84:
                    tag = "FTP";
                    break;
                case 85:
                    tag = "SMTP";
                    break;
                case 86:
                    tag = "POP3";
                    break;
                case 87:
                    tag = "IMAP";
                    break;
                case 88:
                    tag = "SSH";
                    break;
                case 89:
                    tag = "TELNET";
                    break;
                case 90:
                    tag = "RTP";
                    break;
                case 91:
                    tag = "SIP";
                    break;
                case 92:
                    tag = "RADIUS";
                    break;
                case 96:
                    tag = "DPI";
                    break;
                case 97:
                    tag = "WAF";
                    break;
                case 98:
                    tag = "DDOS";
                    break;
                case 99:
                    tag = "RATELIMIT";
                    break;
                case 100:
                    tag = "ACL";
                    break;
                case 101:
                    tag = "SOCKET";
                    break;
                case 102:
                    tag = "TCP_STATE";
                    break;
                case 103:
                    tag = "MAC_TABLE";
                    break;
                case 104:
                    tag = "CAM_TABLE";
                    break;
                case 105:
                    tag = "FIB";
                    break;
                case 106:
                    tag = "SESSION_TABLE";
                    break;
                case 107:
                    tag = "FLOW";
                    break;
                case 108:
                    tag = "LOAD_BALANCER";
                    break;
                case 109:
                    tag = "SCHEDULER";
                    break;
                case 110:
                    tag = "DNS_ZONE";
                    break;
                case 111:
                    tag = "DHCP_LEASE";
                    break;
                case 112:
                    tag = "ARP_TABLE";
                    break;
                case 113:
                    tag = "NEIGHBOR_TABLE";
                    break;
                case 114:
                    tag = "MCAST_ROUTE";
                    break;
                case 115:
                    tag = "MPLS_LABEL";
                    break;
                case 116:
                    tag = "CERT_STORE";
                    break;
                case 117:
                    tag = "EVENT";
                    break;
                case 118:
                    tag = "STATS";
                    break;
                case 119:
                    tag = "LOG";
                    break;
                case 120:
                    tag = "PCAP";
                    break;
                case 121:
                    tag = "LLDP";
                    break;
                case 122:
                    tag = "STP";
                    break;
                case 123:
                    tag = "LACP";
                    break;
                case 124:
                    tag = "MPLS";
                    break;
                case 125:
                    tag = "PIM_SM";
                    break;
                case 126:
                    tag = "MLD";
                    break;
                case 127:
                    tag = "DVMRP";
                    break;
                case 128:
                    tag = "NETFLOW";
                    break;
                case 129:
                    tag = "SFLOW";
                    break;
                case 130:
                    tag = "IPFIX";
                    break;
                case 131:
                    tag = "ICMP_PING";
                    break;
                case 132:
                    tag = "ICMP_TRACE";
                    break;
                case 133:
                    tag = "X509";
                    break;
                case 134:
                    tag = "CRL";
                    break;
                case 135:
                    tag = "OCSP";
                    break;
                case 136:
                    tag = "PKI";
                    break;
                case 137:
                    tag = "DTLS";
                    break;
                case 138:
                    tag = "MAC_AUTH";
                    break;
                case 139:
                    tag = "DOT1X";
                    break;
                case 141:
                    tag = "NETSTAT";
                    break;
                case 142:
                    tag = "IPCONFIG";
                    break;
                case 143:
                    tag = "ROUTEPRINT";
                    break;
                case 144:
                    tag = "NSLOOKUP";
                    break;
                case 145:
                    tag = "ARPCMD";
                    break;
                case 146:
                    tag = "CURL";
                    break;
                case 147:
                    tag = "WGET";
                    break;
                case 148:
                    tag = "TELNET_CLIENT";
                    break;
                case 149:
                    tag = "NAT_HAIRPIN";
                    break;
                case 150:
                    tag = "NAT_HOLE";
                    break;
                case 151:
                    tag = "UPNP";
                    break;
                case 152:
                    tag = "PCP";
                    break;
                case 153:
                    tag = "IPSEC_IKE";
                    break;
                case 154:
                    tag = "IPSEC_ESP";
                    break;
                case 155:
                    tag = "IPSEC_AH";
                    break;
                case 156:
                    tag = "OPENVPN";
                    break;
                case 157:
                    tag = "WIREGUARD";
                    break;
                case 158:
                    tag = "L2TP";
                    break;
                case 159:
                    tag = "SSTP";
                    break;
                case 160:
                    tag = "IPS";
                    break;
// ========== FTP 子工厂 Stage 映射 (使用 161-165) ==========
                case 161:  // FTP 认证工厂
                    tag = "FTP_AUTH";
                    break;
                case 162:  // FTP 命令工厂
                    tag = "FTP_COMMAND";
                    break;
                case 163:  // FTP 数据通道工厂
                    tag = "FTP_CHANNEL";
                    break;
                case 164:  // FTP 响应解析器
                    tag = "FTP_RESPONSE";
                    break;
                case 165:  // FTP 主工厂（门面）
                    tag = "FTP_MAIN";
                    break;
// ========== HTTP 子工厂 Stage 映射 (使用 166-176) ==========
                case 166:
                    tag = "HTTP_REQ";
                    break;
                case 167:
                    tag = "HTTP_RES";
                    break;
                case 168:
                    tag = "HTTP_HDR";
                    break;
                case 169:
                    tag = "HTTP_BODY";
                    break;
                case 170:
                    tag = "HTTP_CK";
                    break;
                case 171:
                    tag = "HTTP_AUTH";
                    break;
                case 172:
                    tag = "HTTP_CACHE";
                    break;
                case 173:
                    tag = "HTTP_CHUNK";
                    break;
                case 174:
                    tag = "HTTP2_FRAME";
                    break;
                case 175:
                    tag = "HTTP2_SET";
                    break;
                case 176:
                    tag = "HTTP2_STR";
                    break;
                default:
                    return null;
            }
            return findBuildingCoords(tag);
        }

        private void processStageCraft() {

            // 控制类包快速通过，不做任何封装处理
            if (packetClass == PacketClass.CONTROL_FAST) {
                return;
            }

            // UDP 数据包跳过 TCP 相关 stage
            if (packetClass == PacketClass.UDP_DATA) {
                processUdpStage();
                return;
            }

            // TCP 控制包只经过必要的 stage
            if (packetClass == PacketClass.TCP_CONTROL) {
                processTcpControlStage();
                return;
            }
            // TCP 数据包走完整路径
//            processTcpDataStage();
            if (cartType != null && cartType.startsWith("DNS_")) {
                // DNS 包不需要任何封装处理
                return;
            }

            if (cartType.startsWith("DHCP")) return;

            switch (stage) {
                // ========== 应用层 ==========
                case 5: // 应用层
                    if (!hasApp) {
                        hasApp = true;

                        // FTP 命令处理（优先级最高）
                        if (ftpCommand != null && !ftpCommand.isEmpty()) {
                            FtpPacketFactory ftpFactory = factoryManager.getFtpPacketFactory();
                            byte[] ftpData = null;

                            if (ftpCommand.startsWith("USER")) {
                                String username = ftpCommand.substring(4).trim();
                                ftpData = ftpFactory.buildUserCommand(username);
                                appendToConsole("【📁 FTP】: USER " + username);
                            } else if (ftpCommand.startsWith("PASS")) {
                                ftpData = ftpFactory.buildPassCommand(ftpCommand.substring(4).trim());
                                appendToConsole("【📁 FTP】: PASS ****");
                            } else if (ftpCommand.equals("PASV")) {
                                ftpData = ftpFactory.buildPasvCommand();
                                appendToConsole("【📁 FTP】: PASV");
                            } else if (ftpCommand.equals("LIST")) {
                                ftpData = ftpFactory.buildListCommand();
                                appendToConsole("【📁 FTP】: LIST");
                            } else if (ftpCommand.startsWith("RETR")) {
                                ftpData = ftpFactory.buildRetrCommand(ftpCommand.substring(4).trim());
                                appendToConsole("【📁 FTP】: RETR");
                            } else if (ftpCommand.startsWith("STOR")) {
                                ftpData = ftpFactory.buildStorCommand(ftpCommand.substring(4).trim());
                                appendToConsole("【📁 FTP】: STOR");
                            } else if (ftpCommand.equals("QUIT")) {
                                ftpData = ftpFactory.buildQuitCommand();
                                appendToConsole("【📁 FTP】: QUIT");
                            } else if (ftpCommand.equals("TYPE I")) {
                                ftpData = ftpFactory.buildTypeBinaryCommand();
                                appendToConsole("【📁 FTP】: TYPE I");
                            } else if (ftpCommand.equals("TYPE A")) {
                                ftpData = ftpFactory.buildTypeAsciiCommand();
                                appendToConsole("【📁 FTP】: TYPE A");
                            } else {
                                ftpData = ftpFactory.buildFtpCommand(ftpCommand);
                                appendToConsole("【📁 FTP】: " + ftpCommand);
                            }

                            if (ftpData != null) {
                                this.ftpPayload = ftpData;
                            }
                        }
                        // HTTP 请求
                        else if (cartType.equals("HTTP_GET")) {
                            httpBody = factoryManager.getHttpFactory().createGetRequest("/index.html").getBody();
                            appendToConsole("【📦 HTTP】: GET /index.html");
                        }
                        // TLS Client Hello
                        else if (cartType.equals("TLS_CLIENT_HELLO")) {
                            httpBody = factoryManager.getTlsFactory().createClientHello(new byte[32]).getTlsMessageType();
                            appendToConsole("【🔒 TLS】: Client Hello");
                        }
                        // UDP 数据
                        else if (cartType.equals("UDP_DATA")) {
                            appendToConsole("【📦 UDP】: 数据载荷");
                        }
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
                        // 如果已经是 FTP 端口，不要覆盖
                        if (dstPort != 21) {
                            dstPort = 443;
                            portFactory.reservePort(dstPort);
                        }
                        appendToConsole("【🎯 目的端口】: 目标端口 " + dstPort);
                    }
                    break;
                case 8: // SEQ
                    if (!c_SEQ) {
                        c_SEQ = true;
                        // 如果已经是 DATA 包且有 sequenceNumber，不要覆盖
                        if (cartType.equals("DATA") && sequenceNumber > 0) {
                            appendToConsole("【🔢 序列号】: SEQ=" + sequenceNumber + " (保留)");
                        } else {
                            sequenceNumber = tcpFactory.getNextSeq();
                            appendToConsole("【🔢 序列号】: SEQ=" + sequenceNumber);
                        }
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
                        byte[] tcpData = new byte[20];
                        // 使用 factoryManager 的 checksumFactory
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
                case 22: // 五元组
                    if (!hasFiveTuple) {
                        hasFiveTuple = true;
                        protocol = useUdp ? "UDP" : "TCP";
                        // 确保 IP 不为 null
                        String fiveSrcIp = srcIp != null ? srcIp : (pcIpAddress != null ? pcIpAddress : "192.168.1.100");
                        String fiveDstIp = dstIp != null ? dstIp : (resolvedServerIp != null ? resolvedServerIp : "10.0.0.1");
                        factoryManager.getFiveTupleFactory().extract(fiveSrcIp, fiveDstIp, srcPort, dstPort, protocol);
                        appendToConsole(String.format("【🔢 五元组】: %s %s:%d → %s:%d",
                                protocol, fiveSrcIp, srcPort, fiveDstIp, dstPort));
                    }
                    break;

                case 23: // 会话管理
                    if (!hasSession) {
                        hasSession = true;
                        // 使用 factoryManager 的 sessionFactory
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
                        addVisualFeedback(String.format("🌍 NAT: %s:%d → %s:%d",
                                        srcIp, srcPort, natPublicIp, natPublicPort),
                                new Color(255, 165, 0));
                        appendToConsole("【🌍 NAT 转换】: " + srcIp + ":" + srcPort + " → " + natPublicIp + ":" + natPublicPort);
                        updateNatDisplay();
                    }
                    break;
                // 修改 case 27 中的带宽控制调用
                case 27: // 带宽控制
                    // 使用 factoryManager 的 bandwidthFactory
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
                        // 使用 factoryManager 的 firewallFactory
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
                        // 使用 factoryManager 的 congestionControl
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
                case 46: // TCP 选项处理
                    if (!hasTcpOption && cartType.equals("DATA") && factoryManager.getTcpOptionFactory() != null) {
                        hasTcpOption = true;
                        byte[] mssOpt = factoryManager.getTcpOptionFactory().mss(1460);
                        byte[] wsOpt = factoryManager.getTcpOptionFactory().windowScale(7);
                        byte[] sackOpt = factoryManager.getTcpOptionFactory().sackPerm();
                        byte[] combined = factoryManager.getTcpOptionFactory().combine(mssOpt, wsOpt, sackOpt);
                        appendToConsole(String.format("【🔧 TCP 选项】: MSS+窗口缩放+SACK 已添加 (%d字节)", combined.length));
                    }
                    break;
                case 47: // IP 选项处理
                    if (!hasIpOption && factoryManager.getIpOptionFactory() != null) {
                        hasIpOption = true;
                        List<Integer> routeIps = new ArrayList<>();
                        routeIps.add(ipToInt("8.8.8.8"));
                        byte[] lsrOpt = factoryManager.getIpOptionFactory().createLooseSourceRouteOption(routeIps);
                        byte[] padded = factoryManager.getIpOptionFactory().padTo4Bytes(lsrOpt);
                        appendToConsole(String.format("【🔧 IP 选项】: LSR 已添加 (%d字节)", padded.length));
                    }
                    break;
                case 48: // 以太网填充
                    if (!hasEtherPadding && factoryManager.getEthernetPaddingFactory() != null) {
                        hasEtherPadding = true;
                        byte[] testPayload = new byte[30];
                        byte[] padded = factoryManager.getEthernetPaddingFactory().pad(testPayload);
                        appendToConsole(String.format("【📦 以太网填充】: %d → %d 字节", testPayload.length, padded.length));
                    }
                    break;

                case 49: // UDP 校验和
                    if (useUdp && !hasUdpChecksum && factoryManager.getUdpChecksumFactory() != null && cartType.equals("UDP_DATA")) {
                        hasUdpChecksum = true;
                        int srcIpInt = ipToInt(getSrcIp());
                        int dstIpInt = ipToInt(getDstIp());
                        byte[] udpData = new byte[100];
                        int checksum = factoryManager.getUdpChecksumFactory().calculate(srcIpInt, dstIpInt, udpData.length, udpData);
                        appendToConsole(String.format("【🔢 UDP 校验和】: 0x%04X", checksum));
                    }
                    break;

                case 50: // ICMP 错误生成
                    if (factoryManager.getIcmpErrorFactory() != null && ttl <= 1 && cartType.equals("DATA")) {
                        byte[] originalIp = new byte[20];
                        byte[] timeExceeded = factoryManager.getIcmpErrorFactory().timeExceeded(originalIp);
                        appendToConsole("【⚠️ ICMP 错误】: Time Exceeded 已生成");
                    }
                    break;

                case 51: // IP 路由转发
                    if (factoryManager.getIpForwardFactory() != null && stage >= 51 && stage <= 58) {
                        byte[] forwarded = factoryManager.getIpForwardFactory().forward(new byte[20], "eth0", ipToInt(getDstIp()));
                        appendToConsole("【🔄 IP 转发】: TTL 递减，校验和更新");
                    }
                    break;

                case 52: // TCP 窗口管理
                    if (!useUdp && factoryManager.getTcpWindowFactory() != null && cartType.equals("DATA")) {
                        long effectiveWin = factoryManager.getTcpWindowFactory().effectiveWindow();
                        appendToConsole(String.format("【📊 TCP 窗口】: 有效窗口=%d", effectiveWin));
                    }
                    break;

                case 53: // TCP 定时器
                    if (!useUdp && factoryManager.getTcpTimerFactory() != null && !isReturnTrip) {
                        long rto = factoryManager.getTcpTimerFactory().getRto();
                        appendToConsole(String.format("【⏱️ TCP 定时器】: RTO=%dms", rto));
                    }
                    break;

                case 54: // VLAN 标签
                    if (!hasVlan && factoryManager.getVlanFactory() != null && isReturnTrip) {
                        hasVlan = true;
                        vlanId = 100;
                        byte[] taggedFrame = factoryManager.getVlanFactory().addVlan(new byte[64], vlanId);
                        appendToConsole(String.format("【🏷️ VLAN 802.1Q】: 添加 VLAN ID=%d", vlanId));
                    }
                    break;

                case 55: // GRE 隧道
                    if (!hasTunnel && factoryManager.getTunnelFactory() != null && cartType.equals("DATA")) {
                        hasTunnel = true;
                        isEncapsulated = true;
                        byte[] innerPacket = new byte[100];
                        byte[] grePacket = factoryManager.getTunnelFactory().greEncapsulate(innerPacket);
                        appendToConsole(String.format("【🔄 GRE 隧道】: 封装完成 (%d字节)", grePacket.length));
                    }
                    break;

                case 56: // IGMP 组播
                    if (!hasIgmp && factoryManager.getIgmpFactory() != null && cartType.equals("IGMP_JOIN")) {
                        hasIgmp = true;
                        int groupIp = ipToInt("224.0.0.1");
                        byte[] joinMsg = factoryManager.getIgmpFactory().joinGroup(groupIp);
                        appendToConsole("【📡 IGMP】: 加入组播组 224.0.0.1");
                    }
                    break;

                case 57: // NDP 发现
                    if (!hasNdp && factoryManager.getNdpFactory() != null && resolvedServerIp != null && resolvedServerIp.contains(":")) {
                        hasNdp = true;
                        byte[] ns = factoryManager.getNdpFactory().neighborSolicitation(resolvedServerIp.getBytes());
                        appendToConsole("【📡 NDP】: 发送邻居请求 (IPv6)");
                    }
                    break;

                case 58: // DNS 递归
                    if (factoryManager.getDnsRecursiveFactory() != null && cartType.equals("DNS_QUERY")) {
                        String resolved = factoryManager.getDnsRecursiveFactory().resolve(domain);
                        appendToConsole(String.format("【🌐 DNS 递归】: %s → %s", domain, resolved));
                    }
                    break;

                case 59: // DHCP 完整报文
                    if (factoryManager.getDhcpFullPacketFactory() != null && cartType.startsWith("DHCP")) {
                        byte[] dhcpPacket = null;
                        switch (cartType) {
                            case "DHCP_DISCOVER":
                                dhcpPacket = factoryManager.getDhcpFullPacketFactory().discover();
                                break;
                            case "DHCP_OFFER":
                                dhcpPacket = factoryManager.getDhcpFullPacketFactory().offer();
                                break;
                            case "DHCP_REQUEST":
                                dhcpPacket = factoryManager.getDhcpFullPacketFactory().request();
                                break;
                            case "DHCP_ACK":
                                dhcpPacket = factoryManager.getDhcpFullPacketFactory().ack();
                                break;
                        }
                        if (dhcpPacket != null) {
                            appendToConsole(String.format("【📡 DHCP 完整报文】: %s (%d字节)", cartType, dhcpPacket.length));
                        }
                    }
                    break;

                case 60: // TLS 握手完成
                    if (factoryManager.getTlsHandshakeFactory() != null && tlsEnabled && tlsState == TlsState.FINISHED) {
                        byte[] finishedMsg = factoryManager.getTlsHandshakeFactory().finished();
                        appendToConsole(String.format("【🔒 TLS 握手】: Finished 消息 (%d字节)", finishedMsg.length));
                    }
                    break;

                case 61: // 序列化
                    if (factoryManager.getPacketSerializerFactory() != null && serverReceivedCount >= totalDataToTransmit) {
                        byte[] serialized = factoryManager.getPacketSerializerFactory().serialize(this);
                        appendToConsole(String.format("【💾 序列化】: 数据包已序列化 (%d字节)", serialized.length));
                    }
                    break;
                // ===================== 新增 14 个工厂处理 stage 62-75 =====================

                case 62: // 物理层 - 比特流生成
                    if (!hasBitStream && bitStreamFactory != null) {
                        hasBitStream = true;
                        byte[] bitStream = bitStreamFactory.toBitStream(ethernetFrameData != null ? ethernetFrameData : new byte[64]);
                        appendToConsole(String.format("【📡 比特流】: 生成 %d 字节物理层比特流", bitStream.length));
                    }
                    break;

                case 63: // 物理层 - 信道噪声模拟
                    if (!hasPhysicalEncoding && physicalChannelFactory != null) {
                        hasPhysicalEncoding = true;
                        double ber = physicalChannelFactory.getBER();
                        int jitter = physicalChannelFactory.getJitterBufferSize();
                        long delay = physicalChannelFactory.getPropagationDelayNano(1000);
                        appendToConsole(String.format("【📡 物理信道】: BER=%.1e, Jitter=%d, 传播时延=%dns", ber, jitter, delay));
                    }
                    break;

                case 64: // PPPoE 封装
                    if (!hasPppoe && pppoeFactory != null && !isReturnTrip) {
                        hasPppoe = true;
                        byte[] padi = pppoeFactory.buildPADI(100);
                        byte[] lcp = pppoeFactory.buildLCPRequest();
                        appendToConsole(String.format("【🔌 PPPoE】: PADI + LCP 请求 (%d+%d字节)", padi.length, lcp.length));
                    }
                    break;

                case 65: // MACsec 安全加密
                    if (!hasMacSec && macSecFactory != null && isReturnTrip) {
                        hasMacSec = true;
                        long sci = 0x001A2B3C4D5EL;
                        int pn = 1;
                        byte[] secTag = macSecFactory.buildSecTAG(sci, pn);
                        byte[] icv = macSecFactory.buildICV();
                        appendToConsole(String.format("【🔐 MACsec】: SecTAG + ICV (%d+%d字节)", secTag.length, icv.length));
                    }
                    break;

                case 66: // OSPF 路由协议
                    if (!hasOspf && ospfPacketFactory != null && cartType.equals("OSPF_HELLO")) {
                        hasOspf = true;
                        int routerId = ipToInt(getSrcIp());
                        int areaId = 0;
                        byte[] ospfHello = ospfPacketFactory.buildHello(routerId, areaId);
                        appendToConsole(String.format("【🌐 OSPF】: Hello 报文 (RouterID=%d)", routerId));
                    }
                    break;

                case 67: // BGP 路由协议
                    if (!hasBgp && bgpPacketFactory != null && cartType.equals("BGP_OPEN")) {
                        hasBgp = true;
                        int myAs = 64512;
                        byte[] bgpOpen = bgpPacketFactory.buildOpen(myAs);
                        byte[] bgpUpdate = bgpPacketFactory.buildUpdate();
                        appendToConsole(String.format("【🌐 BGP】: OPEN + UPDATE (AS=%d)", myAs));
                    }
                    break;

                case 68: // QoS 流量标记
                    if (!hasQos && qosTrafficFactory != null) {
                        hasQos = true;
                        int dscp = 46;  // EF 优先级
                        byte[] dummyIpPkt = new byte[20];
                        int markedDscp = qosTrafficFactory.setDSCP(dummyIpPkt, dscp);
                        boolean allowed = qosTrafficFactory.tokenBucketAllow(1000000, 10000);
                        appendToConsole(String.format("【🎯 QoS】: DSCP=%d, 令牌桶允许=%s", markedDscp, allowed));
                    }
                    break;

                case 69: // NAT64 转换 (IPv6 <-> IPv4)
                    if (!hasNat64 && nat64Factory != null && cartType.equals("NAT64")) {
                        hasNat64 = true;
                        byte[] ipv4 = {8, 8, 8, 8};
                        byte[] ipv6 = nat64Factory.convertIpv4ToIpv6(ipv4);
                        appendToConsole(String.format("【🌍 NAT64】: 8.8.8.8 → 64:FF9B::808:808"));
                    }
                    break;

                case 70: // TCP 重组 (分片重组)
                    if (!isReassembled && tcpReassemblyFactory != null && !isReturnTrip) {
                        isReassembled = true;
                        long seqNum = sequenceNumber;
                        byte[] fakeData = new byte[100];
                        tcpReassemblyFactory.addSegment(seqNum, fakeData);
                        byte[] reassembled = tcpReassemblyFactory.reassemble(seqNum);
                        appendToConsole(String.format("【🔧 TCP 重组】: SEQ=%d, 重组完成", seqNum));
                    }
                    break;

                case 71: // 传输层攻击模拟 (SYN Flood / Land Attack)
                    if (!isAttackPacket && transportAttackFactory != null && cartType.equals("ATTACK")) {
                        isAttackPacket = true;
                        int fakeSrcIp = ipToInt("1.2.3.4");
                        byte[] synFlood = transportAttackFactory.buildSynFlood(fakeSrcIp);
                        byte[] landAttack = transportAttackFactory.buildLandAttack(ipToInt(getDstIp()));
                        appendToConsole(String.format("【⚠️ 攻击检测】: SYN Flood + Land Attack 已模拟"));
                    }
                    break;

                case 72: // NTP 时间同步
                    if (!hasNtp && ntpPacketFactory != null && cartType.equals("NTP_REQUEST")) {
                        hasNtp = true;
                        int stratum = 2;
                        byte[] ntpReq = ntpPacketFactory.buildNtpRequest(stratum);
                        appendToConsole(String.format("【🕐 NTP】: 时间同步请求 (Stratum=%d)", stratum));
                    }
                    break;

                case 73: // SNMP 网络管理
                    if (!hasSnmp && snmpPacketFactory != null && cartType.equals("SNMP_GET")) {
                        hasSnmp = true;
                        String community = "public";
                        byte[] snmpGet = snmpPacketFactory.buildGetRequest(community);
                        appendToConsole(String.format("【📊 SNMP】: GET 请求 (Community=%s)", community));
                    }
                    break;

                case 74: // HTTP/2.3 高级协议
                    if (!hasHttp23 && http23PacketFactory != null && cartType.equals("HTTP2_GET")) {
                        hasHttp23 = true;
                        int streamId = 1;
                        byte[] http2Frame = http23PacketFactory.buildHttp2Frame(streamId);
                        // 同时使用新的 Http2FrameFactory 展示更多帧类型
                        Http2FrameFactory h2ff = factoryManager.getHttp2FrameFactory();
                        if (h2ff != null) {
                            byte[] goaway = h2ff.buildGoAwayFrame(0, 0x00, "OK");
                            appendToConsole(String.format("【📡 HTTP/2.3】: HEADERS 帧 (StreamID=%d), GOAWAY(%dB)", streamId, goaway.length));
                        } else {
                            appendToConsole(String.format("【📡 HTTP/2.3】: HEADERS 帧 (StreamID=%d)", streamId));
                        }
                    }
                    break;

                case 75: // IPsec 安全封装
                    if (!hasIpsec && ipsecFactory != null && !isReturnTrip) {
                        hasIpsec = true;
                        int spi = 0x12345678;
                        byte[] espHeader = ipsecFactory.buildESP(spi);
                        appendToConsole(String.format("【🔒 IPsec】: ESP 头部 (SPI=0x%08X)", spi));
                    }
                    break;
                // ===================== 新增：IPv6 协议栈处理 stage 76-79 =====================
                case 76: // IPv6 数据包封装
                    if (!hasIpv6 && ipv6PacketFactory != null) {
                        hasIpv6 = true;
                        byte[] ipv6Packet = ipv6PacketFactory.buildIpv6Packet(new byte[64]);
                        appendToConsole("【🌐 IPv6】: IPv6 数据包封装完成");
                    }
                    break;

                case 77: // IPv6 分片
                    if (!hasIpv6Fragment && ipv6FragmentFactory != null && cartType.equals("DATA")) {
                        hasIpv6Fragment = true;
                        List<byte[]> fragments = ipv6FragmentFactory.fragmentPacket(new byte[1500]);
                        appendToConsole(String.format("【✂️ IPv6分片】: 分包 %d 片", fragments.size()));
                    }
                    break;

                case 78: // IPv6 扩展选项
                    if (!hasIpv6Option && ipv6OptionFactory != null) {
                        hasIpv6Option = true;
                        ipv6OptionFactory.addOption(0x03, new byte[]{0x01, 0x02});
                        byte[] options = ipv6OptionFactory.buildOptions();
                        appendToConsole(String.format("【🔧 IPv6选项】: %d 字节", options.length));
                    }
                    break;

                case 79: // IPv6 邻居发现
                    if (!hasIpv6Nd && ipv6NeighborDiscovery != null && resolvedServerIp != null && resolvedServerIp.contains(":")) {
                        hasIpv6Nd = true;
                        ipv6NeighborDiscovery.addNeighbor(resolvedServerIp, "00:11:22:33:44:55");
                        appendToConsole("【📡 IPv6 ND】: 邻居发现完成");
                    }
                    break;

// ===================== 新增：TCP 增强处理 stage 80-83 =====================
                case 80: // TCP Keep-Alive
                    if (!hasKeepAlive && tcpKeepAliveFactory != null && !useUdp) {
                        hasKeepAlive = true;
                        byte[] keepAlive = tcpKeepAliveFactory.buildKeepAliveOption();
                        appendToConsole("【🔁 TCP Keep-Alive】: 保活选项已添加");
                    }
                    break;

                case 81: // TCP SACK
                    if (!hasSack && tcpSackFactory != null && !useUdp) {
                        hasSack = true;
                        tcpSackFactory.addBlock(1000, 1200);
                        byte[] sackOpt = tcpSackFactory.buildSackOption();
                        appendToConsole(String.format("【📊 TCP SACK】: 选择性确认选项 (%d字节)", sackOpt.length));
                    }
                    break;

                case 82: // TCP ECN
                    if (!hasEcn && tcpEcnFactory != null && !useUdp) {
                        hasEcn = true;
                        byte[] ecnFlag = tcpEcnFactory.buildEcnFlag();
                        appendToConsole("【⚠️ TCP ECN】: 显式拥塞通知已启用");
                    }
                    break;

                case 83: // TCP Fast Open
                    if (!hasFastOpen && tcpFastOpenFactory != null && !useUdp) {
                        hasFastOpen = true;
                        byte[] tfo = tcpFastOpenFactory.buildTfoOption();
                        appendToConsole("【🚀 TCP Fast Open】: TFO 选项已添加");
                    }
                    break;

// ===================== 新增：应用层协议处理 stage 84-95 =====================
                case 84: // FTP
                    if (!hasFtp && ftpPacketFactory != null && cartType.equals("FTP")) {
                        hasFtp = true;
//                        byte[] userCmd = ftpPacketFactory.buildFtpCommand("USER anonymous");
//                        byte[] passCmd = ftpPacketFactory.buildFtpCommand("PASS test@example.com");
                        appendToConsole("【📁 FTP】: USER/PASS 命令已发送");
                    }
                    break;

                case 85: // SMTP
                    if (!hasSmtp && smtpPacketFactory != null && cartType.equals("SMTP")) {
                        hasSmtp = true;
                        byte[] ehlo = smtpPacketFactory.buildEhlo("mail.example.com");
                        byte[] mailFrom = smtpPacketFactory.buildMailFrom("sender@example.com");
                        byte[] rcptTo = smtpPacketFactory.buildRcptTo("receiver@example.com");
                        appendToConsole("【📧 SMTP】: EHLO/MAIL FROM/RCPT TO 已发送");
                    }
                    break;

                case 86: // POP3
                    if (!hasPop3 && pop3PacketFactory != null && cartType.equals("POP3")) {
                        hasPop3 = true;
                        byte[] user = pop3PacketFactory.buildUserCmd("testuser");
                        byte[] pass = pop3PacketFactory.buildPassCmd("password");
                        byte[] list = pop3PacketFactory.buildListCmd();
                        appendToConsole("【📬 POP3】: USER/PASS/LIST 命令已发送");
                    }
                    break;

                case 87: // IMAP
                    if (!hasImap && imapPacketFactory != null && cartType.equals("IMAP")) {
                        hasImap = true;
                        byte[] login = imapPacketFactory.buildLogin("testuser", "password");
                        appendToConsole("【📨 IMAP】: LOGIN 命令已发送");
                    }
                    break;

                case 88: // SSH
                    if (!hasSsh && sshPacketFactory != null && cartType.equals("SSH")) {
                        hasSsh = true;
                        byte[] banner = sshPacketFactory.buildVersionBanner();
                        byte[] kex = sshPacketFactory.buildKexInit();
                        appendToConsole("【🔐 SSH】: 版本协商 + KEX 初始化");
                    }
                    break;

                case 89: // Telnet
                    if (!hasTelnet && telnetPacketFactory != null && cartType.equals("TELNET")) {
                        hasTelnet = true;
                        byte[] neg = telnetPacketFactory.buildNegotiate();
                        appendToConsole("【💻 Telnet】: 选项协商完成");
                    }
                    break;

                case 90: // RTP/RTCP
                    if (!hasRtp && rtpPacketFactory != null && cartType.equals("RTP")) {
                        hasRtp = true;
                        byte[] rtpData = rtpPacketFactory.buildRtpPacket("Audio Data".getBytes());
                        byte[] rtcpData = rtcpPacketFactory.buildSenderReport(12345);
                        appendToConsole(String.format("【🎵 RTP】: RTP包+RTCP报告 (%d+%d字节)", rtpData.length, rtcpData.length));
                    }
                    break;

                case 91: // SIP
                    if (!hasSip && sipPacketFactory != null && cartType.equals("SIP")) {
                        hasSip = true;
                        byte[] invite = sipPacketFactory.buildInvite("sip:user@example.com");
                        appendToConsole("【📞 SIP】: INVITE 请求已发送");
                    }
                    break;

                case 92: // RADIUS
                    if (!hasRadius && radiusPacketFactory != null && cartType.equals("RADIUS")) {
                        hasRadius = true;
                        byte[] radiusReq = radiusPacketFactory.buildAccessRequest("testuser", "password");
                        appendToConsole("【🔑 RADIUS】: Access-Request 已发送");
                    }
                    break;

// ===================== 新增：安全防护处理 stage 96-100 =====================
                case 96: // DPI 深度包检测
                    if (!hasDpi && dpiFactory != null) {
                        hasDpi = true;
                        DpiFactory.AppProto proto = dpiFactory.detectProtocol(new byte[64]);
                        appendToConsole(String.format("【🔍 DPI】: 检测到协议 %s", proto));
                    }
                    break;

                case 97: // WAF 检测
                    if (wafFactory != null && cartType.equals("HTTP_GET")) {
                        if (!wafFactory.checkHttpContent(httpBody != null ? httpBody : "")) {
                            isBlockedByWaf = true;
                            appendToConsole("【🛡️ WAF】: SQL注入/XSS 攻击被拦截！");
                            this.isDropped = true;
                            return;
                        }
                        appendToConsole("【🛡️ WAF】: HTTP 内容检查通过");
                    }
                    break;

                case 98: // DDoS 缓解
                    if (ddosMitigationFactory != null && getSrcIp() != null) {
                        if (!ddosMitigationFactory.checkTraffic(getSrcIp())) {
                            appendToConsole("【💥 DDoS缓解】: 源IP " + getSrcIp() + " 流量超限，已限流");
                            this.isDropped = true;
                            return;
                        }
                    }
                    break;

                case 99: // 速率限制
                    if (rateLimitFactory != null) {
                        // DATA 包是核心数据流，不应被速率限制
                        if (cartType != null && (cartType.equals("DATA") || cartType.equals("IP_FRAGMENT"))) {
                            break;  // 直接跳过速率限制
                        }
                        String key = getSrcIp() + ":" + cartType;
                        if (!rateLimitFactory.allow(key)) {
                            appendToConsole("【⏱️ 速率限制】: " + key + " 超过限制，已丢弃");
                            this.isDropped = true;
                            return;
                        }
                    }
                    break;

                case 100: // ACL 访问控制
                    if (aclFactory != null && getSrcIp() != null && getDstIp() != null) {
                        boolean permit = aclFactory.match(getSrcIp(), getDstIp(), protocol);
                        if (!permit) {
                            appendToConsole("【🚫 ACL】: " + getSrcIp() + " → " + getDstIp() + " 被拒绝");
                            this.isDropped = true;
                            return;
                        }
                    }
                    break;
// ===================== 新增 20 个核心工厂处理 stage 101-120 =====================

                case 101: // Socket 创建（应用层到传输层桥梁）
                    if (!hasSocket && socketFactory != null && !isReturnTrip) {
                        hasSocket = true;
                        Socket sock = socketFactory.createSocket(getSrcIp(), srcPort, getDstIp(), dstPort);
                        sock.connect();
                        appendToConsole(String.format("【🔌 Socket】: 创建 Socket %s:%d → %s:%d",
                                getSrcIp(), srcPort, getDstIp(), dstPort));
                        if (statisticsFactory != null) {
                            statisticsFactory.tx(64);
                        }
                    }
                    break;

                case 102: // TCP 状态机
                    if (tcpStateMachineFactory != null && !useUdp) {
                        TcpStateMachineFactory.TcpState state = tcpStateMachineFactory.getState();
                        appendToConsole(String.format("【📊 TCP状态机】: 当前状态 %s", state));
                        if (cartType.equals("SYN")) {
                            tcpStateMachineFactory.sendSyn();
                        } else if (cartType.equals("FIN_PC")) {
                            tcpStateMachineFactory.timeWait();
                        }
                    }
                    break;

                case 103: // MAC 地址表学习（交换机）
                    if (!hasMacLearning && macTableFactory != null && !isReturnTrip) {
                        hasMacLearning = true;
                        String inPort = "port_" + (stage % 10);
                        macTableFactory.learn(srcMac, inPort);
                        String outPort = macTableFactory.getPort(dstMac);
                        appendToConsole(String.format("【🔌 MAC表】: 学习 %s → %s, 出口 %s", srcMac, inPort, outPort));
                    }
                    break;

                case 104: // CAM 表（Cisco 交换机）
                    if (camTableFactory != null && !isReturnTrip) {
                        int vlanId = 1;
                        camTableFactory.add(vlanId, dstMac, "Gi0/" + (stage % 24 + 1));
                        CamTableFactory.CamEntry entry = camTableFactory.get(dstMac);
                        if (entry != null) {
                            appendToConsole(String.format("【📋 CAM表】: MAC %s → VLAN%d, 端口 %s", dstMac, entry.vlan, entry.port));
                        }
                    }
                    break;

                case 105: // FIB 转发表（CEF 快速转发）
                    if (!hasFibLookup && forwardingEngineFactory != null) {
                        hasFibLookup = true;
                        forwardingEngineFactory.addFibEntry("0.0.0.0/0", "10.0.0.1");
                        String nextHop = forwardingEngineFactory.forward(getDstIp());
                        appendToConsole(String.format("【🔀 FIB转发】: %s → 下一跳 %s", getDstIp(), nextHop));
                    }
                    break;

                case 106: // 五元组会话表
                    if (sessionTableFactory != null && !isReturnTrip) {
                        String proto = useUdp ? "UDP" : "TCP";
                        sessionTableFactory.createSession(getSrcIp(), srcPort, getDstIp(), dstPort, proto);
                        boolean exists = sessionTableFactory.exists(getSrcIp(), srcPort, getDstIp(), dstPort, proto);
                        appendToConsole(String.format("【💬 会话表】: %s:%d → %s:%d (%s) 存在:%s",
                                getSrcIp(), srcPort, getDstIp(), dstPort, proto, exists));
                    }
                    break;

                case 107: // NetFlow 流记录
                    if (flowFactory != null && !isReturnTrip) {
                        String flowId = getSrcIp() + ":" + srcPort + "→" + getDstIp() + ":" + dstPort;
                        int bytes = 1500;
                        flowFactory.updateFlow(flowId, bytes);
                        FlowFactory.Flow flow = flowFactory.getFlow(flowId);
                        appendToConsole(String.format("【📊 NetFlow】: 流 %s, 包数=%d, 字节=%d",
                                flowId.substring(0, Math.min(20, flowId.length())), flow.packets, flow.bytes));
                    }
                    break;

                case 108: // 负载均衡器
                    if (loadBalancerFactory != null && !isReturnTrip) {
                        List<String> servers = Arrays.asList("10.0.0.1", "10.0.0.2", "10.0.0.3");
                        loadBalancerFactory.setAlgorithm(LoadBalancerFactory.Algorithm.RR);
                        String selected = loadBalancerFactory.select(servers, getSrcIp());
                        appendToConsole(String.format("【⚖️ 负载均衡】: 选择服务器 %s", selected));
                    }
                    break;

                case 109: // QoS 调度器
                    if (schedulerFactory != null) {
                        schedulerFactory.setType(SchedulerFactory.Type.WRR);
                        appendToConsole("【🎯 QoS调度】: 启用加权轮询调度");
                    }
                    break;

                case 110: // DNS 区域记录
                    if (!hasDnsZone && dnsZoneFactory != null && cartType.equals("DNS_QUERY")) {
                        hasDnsZone = true;
                        dnsZoneFactory.addRecord(domain, "A", "10.0.0.1");
                        dnsZoneFactory.addRecord(domain, "AAAA", "2001:db8::1");
                        DnsZoneFactory.DnsRecord record = dnsZoneFactory.query(domain, "A");
                        if (record != null) {
                            appendToConsole(String.format("【🌐 DNS区域】: %s A记录 → %s", domain, record.value));
                        }
                    }
                    break;

                case 111: // DHCP 租约管理
                    if (dhcpLeaseFactory != null && cartType.startsWith("DHCP")) {
                        dhcpLeaseFactory.offer(srcMac, "192.168.1.100");
                        String leasedIp = dhcpLeaseFactory.getIp(srcMac);
                        appendToConsole(String.format("【📝 DHCP租约】: MAC %s → IP %s", srcMac, leasedIp));
                    }
                    break;

                case 112: // ARP 表（带老化）
                    if (!hasArpTable && arpTableFactory != null && resolvedServerIp != null) {
                        hasArpTable = true;
                        arpTableFactory.addDynamic(resolvedServerIp, dstMac);
                        String resolvedMac = arpTableFactory.resolve(resolvedServerIp);
                        appendToConsole(String.format("【📋 ARP表】: %s → %s", resolvedServerIp, resolvedMac));
                    }
                    break;

                case 113: // IPv6 邻居表
                    if (neighborTableFactory != null && resolvedServerIp != null && resolvedServerIp.contains(":")) {
                        neighborTableFactory.add(resolvedServerIp, "00:11:22:33:44:55");
                        String ndMac = neighborTableFactory.getMac(resolvedServerIp);
                        appendToConsole(String.format("【📡 NDP邻居】: %s → %s", resolvedServerIp, ndMac));
                    }
                    break;

                case 114: // 组播路由表 (S,G) / (*,G)
                    if (multicastRoutingFactory != null && cartType.equals("IGMP_JOIN")) {
                        multicastRoutingFactory.addStarG("224.0.0.1");
                        boolean hasRoute = multicastRoutingFactory.hasRoute(getSrcIp(), "224.0.0.1");
                        appendToConsole(String.format("【📡 组播路由】: (*,224.0.0.1) 存在=%s", hasRoute));
                    }
                    break;

                case 115: // MPLS 标签栈操作
                    if (!hasMplsLabel && mplsLabelFactory != null && !isReturnTrip) {
                        hasMplsLabel = true;
                        mplsLabelFactory.push(100);
                        mplsLabelFactory.swap(200);
                        Integer top = mplsLabelFactory.top();
                        appendToConsole(String.format("【🏷️ MPLS】: 标签栈顶=%d", top));
                        mplsLabelFactory.pop();
                    }
                    break;

                case 116: // 证书存储
                    if (certificateStoreFactory != null && tlsEnabled) {
                        certificateStoreFactory.addKey("server-key", "RSA-2048-PRIVATE");
                        certificateStoreFactory.addTrustCert("ca-cert", "CA-CERT-DATA");
                        boolean trusted = certificateStoreFactory.isTrusted("CA-CERT-DATA");
                        appendToConsole(String.format("【🔐 证书库】: 证书可信=%s", trusted));
                    }
                    break;

                case 117: // 事件记录（动画核心）
                    if (eventFactory != null) {
                        eventFactory.emit(EventFactory.Type.SEND, "发送 " + cartType + " SEQ=" + sequenceNumber);
                        appendToConsole("【🎬 事件】: 已记录发送事件");
                    }
                    break;

                case 118: // 统计收集
                    if (statisticsFactory != null) {
                        if (!isReturnTrip) {
                            statisticsFactory.tx(ttl > 0 ? 1500 : 64);
                        } else {
                            statisticsFactory.rx(1500);
                        }
                        appendToConsole(String.format("【📈 统计】: TX=%d, RX=%d, 丢包=%d",
                                statisticsFactory.packetsTx, statisticsFactory.packetsRx, statisticsFactory.loss));
                    }
                    break;

                case 119: // 日志记录
                    if (logFactory != null) {
                        logFactory.log(String.format("%s TTL=%d %s:%d→%s:%d",
                                cartType, ttl, getSrcIp(), srcPort, getDstIp(), dstPort));
                        appendToConsole("【📝 日志】: 已记录到日志系统");
                    }
                    break;

                case 120: // PCAP 抓包
                    if (packetCaptureFactory != null && ethernetFrameData != null) {
                        packetCaptureFactory.capture(ethernetFrameData);
                        appendToConsole(String.format("【📦 PCAP】: 抓取 %d 字节数据包", ethernetFrameData.length));
                        if (stage == 120 && !isReturnTrip && serverReceivedCount >= totalDataToTransmit) {
                            List<PacketCaptureFactory.PcapPacket> replay = packetCaptureFactory.replay();
                            appendToConsole(String.format("【🔄 回放】: 共 %d 个包可回放", replay.size()));
                        }
                    }
                    break;
                // ===================== 补充缺失的工厂处理 stage 121-160 =====================

// ===================== 链路层增强 (121-124) =====================
                case 121: // LLDP 链路层发现协议
                    if (lldpFactory != null && !isReturnTrip) {
                        byte[] lldpFrame = lldpFactory.buildLldpFrame();
                        appendToConsole(String.format("【🔍 LLDP】: 发送链路发现报文 (%d字节)", lldpFrame.length));
                    }
                    break;

                case 122: // STP 生成树协议
                    if (stpFactory != null && !isReturnTrip) {
                        byte[] bpdu = stpFactory.buildBpdu();
                        appendToConsole(String.format("【🌲 STP】: 发送 BPDU 报文，根桥 %s", stpFactory.rootBridge));
                    }
                    break;

                case 123: // LACP 链路聚合
                    if (lacpFactory != null && !isReturnTrip) {
                        byte[] lacpPdu = lacpFactory.buildLacpPdu();
                        appendToConsole(String.format("【🔗 LACP】: 发送链路聚合报文，Actor Key=%d", lacpFactory.actorKey));
                    }
                    break;

                case 124: // MPLS 多协议标签交换
                    if (mplsFactory != null && !isReturnTrip) {
                        byte[] mplsPacket = mplsFactory.addMplsHeader(new byte[64]);
                        appendToConsole(String.format("【🏷️ MPLS】: 添加标签 %d, TTL=%d", mplsFactory.label, mplsFactory.ttl));
                    }
                    break;

// ===================== 多播路由 (125-127) =====================
                case 125: // PIM-SM 稀疏模式组播
                    if (pimSmFactory != null && cartType.equals("IGMP_JOIN")) {
                        pimSmFactory.joinGroup("224.0.0.1");
                        appendToConsole("【📡 PIM-SM】: 加入组播组 224.0.0.1");
                    }
                    break;

                case 126: // MLD 组播监听发现 (IPv6)
                    if (mldFactory != null && resolvedServerIp != null && resolvedServerIp.contains(":")) {
                        byte[] mldReport = mldFactory.buildMldReport("ff02::1");
                        appendToConsole("【📢 MLD】: 发送 MLD 报告报文");
                    }
                    break;

                case 127: // DVMRP 距离矢量组播路由
                    if (dvmrpFactory != null && !isReturnTrip) {
                        dvmrpFactory.addRoute("224.0.0.0/4", "10.0.0.1");
                        String upstream = dvmrpFactory.getUpstream("224.0.0.0/4");
                        appendToConsole(String.format("【🗺️ DVMRP】: 组播路由上游 %s", upstream));
                    }
                    break;

// ===================== 监控管理 (128-132) =====================
                case 128: // NetFlow 流量采集
                    if (netFlowFactory != null && !isReturnTrip) {
                        netFlowFactory.addRecord(getSrcIp(), getDstIp(), srcPort, dstPort, 1);
                        appendToConsole(String.format("【📊 NetFlow】: 流记录 %s:%d→%s:%d",
                                getSrcIp(), srcPort, getDstIp(), dstPort));
                    }
                    break;

                case 129: // sFlow 采样流
                    if (sflowFactory != null && !isReturnTrip) {
                        boolean sampled = sflowFactory.doSample(cartId);
                        byte[] sflowPacket = sflowFactory.buildSflowPacket(new byte[64]);
                        appendToConsole(String.format("【📈 sFlow】: 采样 %s, 报文大小=%d", sampled ? "是" : "否", sflowPacket.length));
                    }
                    break;

                case 130: // IPFIX 流导出
                    if (ipfixFactory != null && !isReturnTrip) {
                        byte[] ipfixPacket = ipfixFactory.buildIpfixPacket(new byte[32]);
                        appendToConsole(String.format("【📋 IPFIX】: 导出流数据 (%d字节)", ipfixPacket.length));
                    }
                    break;

                case 131: // ICMP Ping
                    if (icmpPingFactory != null && cartType.equals("ICMP_ECHO_REQ")) {
                        byte[] pingReq = icmpPingFactory.buildEchoRequest();
                        appendToConsole(String.format("【📡 PING】: Echo Request, seq=%d", icmpPingFactory.seq));
                    }
                    break;

                case 132: // ICMP Traceroute
                    if (icmpTracerouteFactory != null && tracerouteActive) {
                        byte[] tracePacket = icmpTracerouteFactory.buildTracePacket();
                        appendToConsole(String.format("【🔎 Traceroute】: TTL=%d 探测包", icmpTracerouteFactory.ttl));
                    }
                    break;

// ===================== 加密证书 (133-137) =====================
                case 133: // X.509 证书
                    if (x509Factory != null && tlsEnabled) {
                        byte[] cert = x509Factory.buildCert();
                        appendToConsole(String.format("【📜 X.509】: 证书主体 %s", x509Factory.getSubject()));
                    }
                    break;

                case 134: // CRL 证书吊销列表
                    if (crlFactory != null && tlsEnabled) {
                        crlFactory.revokeCert("SN-12345");
                        boolean revoked = crlFactory.isRevoked("SN-12345");
                        appendToConsole(String.format("【🚫 CRL】: 证书吊销状态 %s", revoked ? "已吊销" : "有效"));
                    }
                    break;

                case 135: // OCSP 在线证书状态
                    if (ocspFactory != null && tlsEnabled) {
                        byte[] ocspReq = ocspFactory.buildRequest("SN-12345");
                        byte[] ocspResp = ocspFactory.buildResponse(true);
                        appendToConsole("【🔍 OCSP】: 证书状态查询完成");
                    }
                    break;

                case 136: // PKI 公钥基础设施
                    if (pkiFactory != null && tlsEnabled) {
                        boolean valid = pkiFactory.verifyChain(new String[]{"cert1", "cert2"});
                        appendToConsole(String.format("【🔐 PKI】: 证书链验证 %s", valid ? "通过" : "失败"));
                    }
                    break;

                case 137: // DTLS 数据报 TLS
                    if (dtlsFactory != null && useUdp && tlsEnabled) {
                        byte[] dtlsRecord = dtlsFactory.buildDtlsRecord("Hello".getBytes());
                        appendToConsole(String.format("【🔄 DTLS】: DTLS 记录 (%d字节)", dtlsRecord.length));
                    }
                    break;

// ===================== 访问控制 (138-140) =====================
                case 138: // MAC 地址认证
                    if (macAuthFactory != null && srcMac != null) {
                        macAuthFactory.allowMac(srcMac);
                        boolean allowed = macAuthFactory.check(srcMac);
                        appendToConsole(String.format("【🔑 MAC认证】: MAC %s 认证 %s", srcMac, allowed ? "通过" : "拒绝"));
                    }
                    break;

                case 139: // 802.1X 端口认证
                    if (dot1xFactory != null && !isReturnTrip) {
                        byte[] eapStart = dot1xFactory.buildEapStart();
                        dot1xFactory.setAuthResult(true);
                        appendToConsole(String.format("【🔌 802.1X】: 认证状态 %s", dot1xFactory.isAuthenticated() ? "已认证" : "未认证"));
                    }
                    break;

// ===================== 诊断工具 (141-148) =====================
                case 141: // netstat
                    if (netstatFactory != null && !isReturnTrip) {
                        netstatFactory.addConn(useUdp ? "UDP" : "TCP",
                                getSrcIp() + ":" + srcPort,
                                getDstIp() + ":" + dstPort,
                                useUdp ? "ESTABLISHED" : currentTcpState.toString());
                        appendToConsole(String.format("【📊 netstat】: 连接数 %d", netstatFactory.getConnList().size()));
                    }
                    break;

                case 142: // ipconfig
                    if (ipconfigFactory != null && pcIpAssigned) {
                        String config = ipconfigFactory.getConfigInfo();
                        appendToConsole(String.format("【⚙️ ipconfig】: %s", config));
                    }
                    break;

                case 143: // route print
                    if (routePrintFactory != null) {
                        routePrintFactory.addRoute("0.0.0.0", "0.0.0.0", "192.168.1.1", "eth0");
                        appendToConsole(String.format("【🗺️ route】: 路由表条目数 %d", routePrintFactory.getRoutes().size()));
                    }
                    break;

                case 144: // nslookup
                    if (nslookupFactory != null && domain != null) {
                        nslookupFactory.addDnsRecord(domain, resolvedServerIp != null ? resolvedServerIp : "10.0.0.1");
                        String result = nslookupFactory.query(domain);
                        appendToConsole(String.format("【🔍 nslookup】: %s → %s", domain, result));
                    }
                    break;

                case 145: // arp 命令
                    if (arpCommandFactory != null && resolvedServerIp != null) {
                        arpCommandFactory.addEntry(resolvedServerIp, dstMac);
                        String mac = arpCommandFactory.getMac(resolvedServerIp);
                        appendToConsole(String.format("【📋 arp】: %s → %s", resolvedServerIp, mac));
                    }
                    break;

                case 146: // curl
                    if (curlFactory != null && cartType.equals("HTTP_GET")) {
                        byte[] getRequest = curlFactory.buildGetRequest("/index.html");
                        appendToConsole(String.format("【🌐 curl】: HTTP GET 请求 (%d字节)", getRequest.length));
                    }
                    break;

                case 147: // wget
                    if (wgetFactory != null && cartType.equals("HTTP_GET")) {
                        byte[] download = wgetFactory.buildDownloadRequest("/file.zip");
                        appendToConsole(String.format("【⬇️ wget】: 下载请求，保存路径 %s", wgetFactory.savePath));
                    }
                    break;

                case 148: // telnet client
                    if (telnetClientFactory != null && cartType.equals("TELNET")) {
                        telnetClientFactory.connect(getDstIp(), 23);
                        byte[] telnetData = telnetClientFactory.sendData("ls\r\n");
                        appendToConsole(String.format("【💻 telnet】: 连接到 %s:%d", getDstIp(), 23));
                    }
                    break;

// ===================== NAT 增强 (149-152) =====================
                case 149: // NAT 发夹
                    if (natHairpinningFactory != null && isNatted) {
                        natHairpinningFactory.addMapping(natPublicIp, getSrcIp());
                        String translated = natHairpinningFactory.hairpinTranslate(natPublicIp, getSrcIp());
                        appendToConsole(String.format("【↩️ NAT发夹】: %s → %s", natPublicIp, translated));
                    }
                    break;

                case 150: // NAT 穿透 (打洞)
                    if (natHolePunchFactory != null && !isReturnTrip) {
                        boolean punched = natHolePunchFactory.doHolePunch(getDstIp() + ":" + dstPort);
                        appendToConsole(String.format("【🕳️ NAT穿透】: %s:%d 打洞 %s", getDstIp(), dstPort, punched ? "成功" : "失败"));
                    }
                    break;

                case 151: // UPnP 端口映射
                    if (upnpFactory != null && !isReturnTrip) {
                        upnpFactory.addPortMap(8080, 80, getSrcIp(), "TCP");
                        appendToConsole(String.format("【🔌 UPnP】: 端口映射 8080→%s:80", getSrcIp()));
                    }
                    break;

                case 152: // PCP 端口控制协议
                    if (pcpFactory != null && !isReturnTrip) {
                        byte[] pcpMap = pcpFactory.buildMapRequest(80, getSrcIp());
                        appendToConsole(String.format("【📡 PCP】: 端口控制请求，生存时间=%ds", pcpFactory.lifetime));
                    }
                    break;

// ===================== VPN 隧道 (153-159) =====================
                case 153: // IKE 密钥交换
                    if (ipsecIkeFactory != null && !isReturnTrip) {
                        byte[] ikeSa = ipsecIkeFactory.buildIkeSaInit();
                        appendToConsole("【🔑 IKE】: IKE SA 初始化");
                    }
                    break;

                case 154: // ESP 封装安全载荷
                    if (ipsecEspFactory != null && !isReturnTrip) {
                        byte[] espPacket = ipsecEspFactory.wrapEsp(new byte[100]);
                        appendToConsole(String.format("【🔒 ESP】: ESP 封装, SPI=0x%08X", ipsecEspFactory.spi));
                    }
                    break;

                case 155: // AH 认证头
                    if (ipsecAhFactory != null && !isReturnTrip) {
                        byte[] ahPacket = ipsecAhFactory.wrapAh(new byte[100]);
                        appendToConsole(String.format("【🔐 AH】: AH 认证, SPI=0x%08X", ipsecAhFactory.spi));
                    }
                    break;

                case 156: // OpenVPN
                    if (openVpnFactory != null && !isReturnTrip) {
                        byte[] ovpnPacket = openVpnFactory.buildOpenVpnPacket(new byte[64]);
                        appendToConsole(String.format("【🔓 OpenVPN】: OpenVPN 数据包 (%d字节)", ovpnPacket.length));
                    }
                    break;

                case 157: // WireGuard
                    if (wireguardFactory != null && !isReturnTrip) {
                        byte[] wgPacket = wireguardFactory.buildWgPacket(new byte[64]);
                        appendToConsole("【🔒 WireGuard】: WireGuard 加密隧道");
                    }
                    break;

                case 158: // L2TP
                    if (l2tpFactory != null && !isReturnTrip) {
                        byte[] l2tpPacket = l2tpFactory.buildL2tpPacket(new byte[64]);
                        appendToConsole(String.format("【🔌 L2TP】: L2TP 隧道, TunnelID=%d", l2tpFactory.tunnelId));
                    }
                    break;

                case 159: // SSTP
                    if (sstpFactory != null && !isReturnTrip) {
                        byte[] sstpControl = sstpFactory.buildSstpControl();
                        appendToConsole("【🌐 SSTP】: SSTP 控制报文");
                    }
                    break;

// ===================== 安全增强 (160) =====================
                case 160: // IPS 入侵防御
                    if (ipsFactory != null && !isReturnTrip) {
                        boolean isAttack = ipsFactory.isAttack(getSrcIp(), new byte[64]);
                        if (isAttack) {
                            appendToConsole(String.format("【🛡️ IPS】: 检测到攻击来自 %s，已阻断", getSrcIp()));
                            this.isDropped = true;
                            return;
                        }
                        appendToConsole("【🛡️ IPS】: 流量正常");
                    }
                    break;
// 在 processStageCraft 方法的 switch 中修改

// ========== FTP 子工厂处理 Stage 161-165 ==========
                case 161: // FTP_AUTH - 认证工厂
                    if (ftpCommand != null && (ftpCommand.startsWith("USER") || ftpCommand.startsWith("PASS"))) {
                        FtpAuthFactory authFactory = factoryManager.getFtpAuthFactory();
                        if (authFactory != null) {
                            if (ftpCommand.startsWith("USER")) {
                                String username = ftpCommand.substring(4).trim();
                                appendToConsole("【🔐 FTP_AUTH(161)】: 处理 USER " + username);
                            } else if (ftpCommand.startsWith("PASS")) {
                                authFactory.setAuthenticated(true);
                                appendToConsole("【🔐 FTP_AUTH(161)】: 认证成功");
                                this.ftpResponseCode = 230;
                            }
                        }
                    }
                    break;

                case 162: // FTP_COMMAND - 命令工厂
                    if (ftpCommand != null && !ftpCommand.isEmpty()) {
                        FtpCommandFactory cmdFactory = factoryManager.getFtpCommandFactory();
                        if (cmdFactory != null) {
                            appendToConsole("【📝 FTP_COMMAND(162)】: 验证命令 - " + ftpCommand);

                            if (ftpCommand.startsWith("USER")) {
                                this.ftpResponseCode = 331;
                                appendToConsole("【📝 FTP_COMMAND(162)】: 响应 331 - 需要密码");
                            } else if (ftpCommand.startsWith("PASS")) {
                                this.ftpResponseCode = 230;
                                appendToConsole("【📝 FTP_COMMAND(162)】: 响应 230 - 登录成功");
                            } else if (ftpCommand.equals("PASV")) {
                                this.ftpResponseCode = 227;
                                appendToConsole("【📝 FTP_COMMAND(162)】: 响应 227 - 进入被动模式");
                            } else if (ftpCommand.equals("LIST")) {
                                this.ftpResponseCode = 150;
                                appendToConsole("【📝 FTP_COMMAND(162)】: 响应 150 - 准备打开数据连接");
                            } else if (ftpCommand.equals("QUIT")) {
                                this.ftpResponseCode = 221;
                                appendToConsole("【📝 FTP_COMMAND(162)】: 响应 221 - 服务关闭");
                            }
                        }
                    }
                    break;

                case 163: // FTP_CHANNEL - 数据通道工厂
                    FtpDataChannelFactory channelFactory = factoryManager.getFtpDataChannelFactory();
                    if (channelFactory != null && ftpCommand != null) {
                        if (ftpCommand.equals("PASV")) {
                            int dataPort = 60000 + (int)(Math.random() * 1000);
                            channelFactory.setPassiveMode(true);
                            this.ftpDataPort = dataPort;
                            appendToConsole(String.format("【🔌 FTP_CHANNEL(163)】: 被动模式，数据端口=%d", dataPort));
                        } else if (ftpCommand.equals("LIST") || ftpCommand.startsWith("RETR") || ftpCommand.startsWith("STOR")) {
                            appendToConsole("【🔌 FTP_CHANNEL(163)】: 数据通道准备就绪");
                        } else {
                            // 其他命令（如 USER、PASS）不需要数据通道
                            appendToConsole("【🔌 FTP_CHANNEL(163)】: 控制命令，无需数据通道");
                        }
                    }
                    break;

                case 164: // FTP_RESPONSE - 响应解析器
                    if (ftpPayload != null && ftpPayload.length > 0) {
                        FtpResponseParser parser = factoryManager.getFtpResponseParser();
                        if (parser != null) {
                            FtpResponseParser.FtpResponse response = parser.parse(ftpPayload);
                            if (response != null) {
                                this.ftpResponseCode = response.getCode();
                                appendToConsole(String.format("【📋 FTP_RESPONSE(164)】: 解析响应 %d", response.getCode()));
                            }
                        }
                    } else {
                        // 对于 USER 命令，模拟响应
                        appendToConsole("【📋 FTP_RESPONSE(164)】: 等待服务器响应");
                    }
                    break;

                case 165: // FTP_MAIN - 主工厂（门面）
                    FtpPacketFactory mainFactory = factoryManager.getFtpPacketFactory();
                    if (mainFactory != null && ftpCommand != null && !ftpCommand.isEmpty()) {
                        byte[] finalCommand = null;

                        if (ftpCommand.startsWith("USER")) {
                            finalCommand = mainFactory.buildUserCommand(ftpCommand.substring(4).trim());
                        } else if (ftpCommand.startsWith("PASS")) {
                            finalCommand = mainFactory.buildPassCommand(ftpCommand.substring(4).trim());
                        } else if (ftpCommand.equals("PASV")) {
                            finalCommand = mainFactory.buildPasvCommand();
                        } else if (ftpCommand.equals("LIST")) {
                            finalCommand = mainFactory.buildListCommand();
                        } else if (ftpCommand.equals("QUIT")) {
                            finalCommand = mainFactory.buildQuitCommand();
                        } else if (ftpCommand.equals("TYPE I")) {
                            finalCommand = mainFactory.buildTypeBinaryCommand();
                        } else if (ftpCommand.equals("TYPE A")) {
                            finalCommand = mainFactory.buildTypeAsciiCommand();
                        }

                        if (finalCommand != null) {
                            this.ftpPayload = finalCommand;
                            appendToConsole(String.format("【📁 FTP_MAIN(165)】: 构建 %d 字节命令", finalCommand.length));
                            this.stage = 5;
                            this.timer = 1;
                        }
                    }
                    break;
                // ========== HTTP/1.1 子工厂处理 Stage 166-173 ==========
                case 166: // HTTP_REQ - 请求工厂
                    if (!hasHttpReq) {
                        hasHttpReq = true;
                        HttpRequestFactory reqFactory = factoryManager.getHttpRequestFactory();
                        if (reqFactory != null) {
                            byte[] getLine = reqFactory.buildGet("/index.html");
                            byte[] postLine = reqFactory.buildPost("/api/data");
                            byte[] putLine = reqFactory.buildPut("/api/update");
                            byte[] delLine = reqFactory.buildDelete("/api/remove");
                            appendToConsole(String.format("【📤 HTTP_REQ(166)】: GET(%dB) POST(%dB) PUT(%dB) DELETE(%dB)",
                                    getLine.length, postLine.length, putLine.length, delLine.length));
                            appendToConsole("【📤 HTTP_REQ(166)】: 支持 GET/POST/PUT/DELETE/PATCH/HEAD/OPTIONS/CONNECT/TRACE");
                        }
                    }
                    break;

                case 167: // HTTP_RES - 响应工厂
                    if (!hasHttpRes) {
                        hasHttpRes = true;
                        HttpResponseFactory resFactory = factoryManager.getHttpResponseFactory();
                        if (resFactory != null) {
                            byte[] ok200 = resFactory.buildOk();
                            byte[] nf404 = resFactory.buildNotFound();
                            byte[] err500 = resFactory.buildServerError();
                            byte[] redirect = resFactory.buildStatusLine("HTTP/1.1", 302);
                            appendToConsole(String.format("【📥 HTTP_RES(167)】: 200OK(%dB) 404(%dB) 500(%dB) 302(%dB)",
                                    ok200.length, nf404.length, err500.length, redirect.length));
                            appendToConsole("【📥 HTTP_RES(167)】: 支持 60+ 标准状态码(1xx~5xx)");
                        }
                    }
                    break;

                case 168: // HTTP_HDR - 头部工厂
                    if (!hasHttpHdr) {
                        hasHttpHdr = true;
                        HttpHeaderFactory hdrFactory = factoryManager.getHttpHeaderFactory();
                        if (hdrFactory != null) {
                            Map<String, String> reqHeaders = hdrFactory.buildDefaultRequestHeaders("example.com");
                            Map<String, String> resHeaders = hdrFactory.buildDefaultResponseHeaders("text/html", 1024);
                            byte[] hdrBytes = hdrFactory.buildHeaders(reqHeaders);
                            appendToConsole(String.format("【📋 HTTP_HDR(168)】: 请求头 %d 个/%dB 响应头 %d 个",
                                    reqHeaders.size(), hdrBytes.length, resHeaders.size()));
                            appendToConsole("【📋 HTTP_HDR(168)】: 支持 30+ 标准头字段");
                        }
                    }
                    break;

                case 169: // HTTP_BODY - 消息体工厂
                    if (!hasHttpBody) {
                        hasHttpBody = true;
                        HttpBodyFactory bodyFactorySub = factoryManager.getHttpBodyFactory();
                        if (bodyFactorySub != null) {
                            byte[] json = bodyFactorySub.buildJsonBody("{\"key\":\"value\"}");
                            byte[] form = bodyFactorySub.buildFormBodyFromMap(Map.of("user", "admin", "pass", "123"));
                            byte[] html = bodyFactorySub.buildHtmlBody("<html><body>OK</body></html>");
                            appendToConsole(String.format("【📦 HTTP_BODY(169)】: JSON(%dB) FORM(%dB) HTML(%dB)",
                                    json.length, form.length, html.length));
                            appendToConsole("【📦 HTTP_BODY(169)】: 支持 JSON/XML/Form/Multipart/HTML/Text");
                        }
                    }
                    break;

                case 170: // HTTP_CK - Cookie 工厂
                    if (!hasHttpCookie) {
                        hasHttpCookie = true;
                        HttpCookieFactory ckFactory = factoryManager.getHttpCookieFactory();
                        if (ckFactory != null) {
                            HttpCookieFactory.Cookie sessionCookie = new HttpCookieFactory.Cookie("session", "abc123");
                            sessionCookie.setDomain("example.com");
                            sessionCookie.setPath("/");
                            sessionCookie.setMaxAge(3600);
                            sessionCookie.setHttpOnly(true);
                            sessionCookie.setSecure(true);
                            sessionCookie.setSameSite("Lax");
                            String setCookie = ckFactory.buildSetCookieHeader(sessionCookie);
                            String cookieHeader = ckFactory.buildCookieHeader(java.util.List.of(sessionCookie));
                            appendToConsole(String.format("【🍪 HTTP_CK(170)】: Set-Cookie: %s", setCookie));
                            appendToConsole(String.format("【🍪 HTTP_CK(170)】: Cookie: %s", cookieHeader));
                        }
                    }
                    break;

                case 171: // HTTP_AUTH - 认证工厂
                    if (!hasHttpAuth) {
                        hasHttpAuth = true;
                        HttpAuthFactory authFactorySub = factoryManager.getHttpAuthFactory();
                        if (authFactorySub != null) {
                            String basic = authFactorySub.buildBasicAuth("admin", "secret");
                            String bearer = authFactorySub.buildBearerAuth("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMjM0NTYifQ");
                            String digestChallenge = authFactorySub.buildDigestChallenge("api@example.com", false);
                            appendToConsole(String.format("【🔑 HTTP_AUTH(171)】: Basic: %s", basic.substring(0, 20) + "..."));
                            appendToConsole(String.format("【🔑 HTTP_AUTH(171)】: Bearer: %s...", bearer.substring(0, 20)));
                            appendToConsole("【🔑 HTTP_AUTH(171)】: 支持 Basic/Digest/Bearer 三种认证");
                        }
                    }
                    break;

                case 172: // HTTP_CACHE - 缓存工厂
                    if (!hasHttpCache) {
                        hasHttpCache = true;
                        HttpCacheFactory cacheFactorySub = factoryManager.getHttpCacheFactory();
                        if (cacheFactorySub != null) {
                            String cc = cacheFactorySub.buildResponseCacheControl(3600, true, true);
                            String etag = cacheFactorySub.buildEtag("Hello World".getBytes(java.nio.charset.StandardCharsets.UTF_8));
                            String lm = cacheFactorySub.buildLastModified(System.currentTimeMillis());
                            appendToConsole(String.format("【💾 HTTP_CACHE(172)】: Cache-Control: %s", cc));
                            appendToConsole(String.format("【💾 HTTP_CACHE(172)】: ETag: %s", etag));
                            appendToConsole(String.format("【💾 HTTP_CACHE(172)】: Last-Modified: %s", lm));
                        }
                    }
                    break;

                case 173: // HTTP_CHUNK - 分块传输工厂
                    if (!hasHttpChunked) {
                        hasHttpChunked = true;
                        HttpChunkedFactory chunkedFactorySub = factoryManager.getHttpChunkedFactory();
                        if (chunkedFactorySub != null) {
                            byte[] chunk1 = chunkedFactorySub.encodeChunk("Hello ".getBytes(java.nio.charset.StandardCharsets.UTF_8));
                            byte[] chunk2 = chunkedFactorySub.encodeChunk("World".getBytes(java.nio.charset.StandardCharsets.UTF_8));
                            byte[] last = chunkedFactorySub.encodeLastChunk();
                            int totalSize = chunk1.length + chunk2.length + last.length;
                            byte[] combined = new byte[totalSize];
                            System.arraycopy(chunk1, 0, combined, 0, chunk1.length);
                            System.arraycopy(chunk2, 0, combined, chunk1.length, chunk2.length);
                            System.arraycopy(last, 0, combined, chunk1.length + chunk2.length, last.length);
                            byte[] decoded = chunkedFactorySub.decodeAndJoin(combined);
                            appendToConsole(String.format("【🧩 HTTP_CHUNK(173)】: 分块编码 %d 字节, 解码=%s",
                                    totalSize, new String(decoded, java.nio.charset.StandardCharsets.UTF_8)));
                        }
                    }
                    break;

                // ========== HTTP/2 子工厂处理 Stage 174-176 ==========
                case 174: // HTTP2_FRAME - HTTP/2 帧工厂
                    if (!hasHttp2Frame) {
                        hasHttp2Frame = true;
                        Http2FrameFactory h2FrameFactory = factoryManager.getHttp2FrameFactory();
                        if (h2FrameFactory != null) {
                            byte[] headers = h2FrameFactory.buildHeadersFrame(1, new byte[]{0x00}, true, true);
                            byte[] data = h2FrameFactory.buildDataFrame(1, "Hello HTTP/2".getBytes(java.nio.charset.StandardCharsets.UTF_8), true);
                            byte[] settings = h2FrameFactory.buildSettingsFrame(new byte[]{0x00, 0x03, 0x00, 0x00, 0x00, 0x64}, false);
                            byte[] goaway = h2FrameFactory.buildGoAwayFrame(0, 0x00, "OK");
                            appendToConsole(String.format("【📡 HTTP2_FRAME(174)】: HEADERS(%dB) DATA(%dB) SETTINGS(%dB) GOAWAY(%dB)",
                                    headers.length, data.length, settings.length, goaway.length));
                            appendToConsole("【📡 HTTP2_FRAME(174)】: 支持 10 种帧类型");
                        }
                    }
                    break;

                case 175: // HTTP2_SET - HTTP/2 设置工厂
                    if (!hasHttp2Settings) {
                        hasHttp2Settings = true;
                        Http2SettingsFactory.Http2Settings h2Settings = new Http2SettingsFactory.Http2Settings();
                        h2Settings.setMaxConcurrentStreams(128);
                        h2Settings.setInitialWindowSize(1048576);
                        h2Settings.setMaxFrameSize(65536);
                        byte[] payload = h2Settings.serialize();
                        appendToConsole(String.format("【⚙️ HTTP2_SET(175)】: 序列化 %d 字节, 并发流=%d, 窗口=%d",
                                payload.length, h2Settings.getMaxConcurrentStreams(), h2Settings.getInitialWindowSize()));
                    }
                    break;

                case 176: // HTTP2_STR - HTTP/2 流管理工厂
                    if (!hasHttp2Stream) {
                        hasHttp2Stream = true;
                        Http2StreamFactory h2StreamFactory = factoryManager.getHttp2StreamFactory();
                        if (h2StreamFactory != null) {
                            h2StreamFactory.createStream(h2StreamFactory.allocateClientStreamId());
                            h2StreamFactory.createStream(h2StreamFactory.allocateClientStreamId());
                            h2StreamFactory.createStream(h2StreamFactory.allocateClientStreamId());
                            long active = h2StreamFactory.getActiveStreamCount();
                            appendToConsole(String.format("【🌊 HTTP2_STR(176)】: 已创建 3 个流, 活跃 %d", active));
                            h2StreamFactory.reset();
                            appendToConsole("【🌊 HTTP2_STR(176)】: 流已重置, 支持 IDLE/OPEN/HALF_CLOSED/CLOSED 状态机");
                        }
                    }
                    break;
            }
        }

        // UDP 专用 stage 处理
        private void processUdpStage() {
            switch (stage) {
                case 5:  // 应用层
                    if (!hasApp) {
                        hasApp = true;
                        appendToConsole("【📦 UDP】: 应用层数据准备");
                    }
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:
                case 13:
                    // UDP 跳过 TCP 层，直接进入 IP 层
                    stage = 13;
                    break;
                case 14:  // IP 层
                    if (!hasIp) {
                        hasIp = true;
                        srcIp = pcIpAddress != null ? pcIpAddress : "192.168.1.100";
                        dstIp = resolvedServerIp != null ? resolvedServerIp : "10.0.0.1";
                        appendToConsole("【📦 UDP IP首部】: " + srcIp + " → " + dstIp);
                    }
                    break;
                case 49:  // UDP 校验和
                    if (!hasUdpChecksum) {
                        hasUdpChecksum = true;
                        appendToConsole("【🔢 UDP校验和】: 已计算");
                    }
                    break;
            }
        }

        // TCP 控制包专用 stage 处理
        private void processTcpControlStage() {
            switch (stage) {
                case 5:  // 应用层 - 控制包跳过
                    stage = 12;
                    break;
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:
                case 11:
                case 12:  // TCP 必要字段
                    if (!c_SP && stage == 6) {
                        c_SP = true;
                        srcPort = 1234;
                    }
                    if (!c_DP && stage == 7) {
                        c_DP = true;
                        dstPort = 443;
                    }
                    if (!c_SEQ && stage == 8 && cartType.equals("SYN")) {
                        c_SEQ = true;
                        sequenceNumber = 100;
                    }
                    if (!c_CTL && stage == 10) {
                        c_CTL = true;
                    }
                    break;
                case 14:  // IP 层
                    if (!hasIp) {
                        hasIp = true;
                        srcIp = pcIpAddress != null ? pcIpAddress : "192.168.1.100";
                        dstIp = resolvedServerIp != null ? resolvedServerIp : "10.0.0.1";
                        appendToConsole("【📦 IP首部】: " + srcIp + " → " + dstIp + ", TTL=" + ttl);
                    }
                    break;
                // 控制包不需要分片、ARP 等完整处理
                case 15:
                case 16:
                case 17:
                case 18:
                case 19:
                case 20:
                case 21:
                    stage = 21;
                    break;
            }
        }
    }

    private class GameCanvas extends JPanel {
        public GameCanvas() {
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
            return (int) ((canvasX - viewOffsetX) / canvasScale);
        }

        public int getLogicalY(int screenY) {
            // 获取当前滚动条位置
            JScrollPane scrollPane = (JScrollPane) getParent().getParent();
            JScrollBar vBar = scrollPane.getVerticalScrollBar();
            int scrollY = vBar.getValue();

            // 计算实际画布上的坐标（考虑滚动条偏移）
            double canvasY = screenY + scrollY;

            // 再转换为逻辑坐标（考虑缩放和视图偏移）
            return (int) ((canvasY - viewOffsetY) / canvasScale);
        }

        // 获取屏幕坐标（用于绘制）
        private int toScreenX(int logicalX) {
            return (int) (logicalX * canvasScale) + viewOffsetX;
        }

        private int toScreenY(int logicalY) {
            return (int) (logicalY * canvasScale) + viewOffsetY;
        }

        // 获取绘制区域尺寸（缩放后的实际尺寸）
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(
                    (int) (MAP_COLS * TILE_SIZE * canvasScale),
                    (int) (MAP_ROWS * TILE_SIZE * canvasScale)
            );
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // 应用平移和缩放
            g2.translate(viewOffsetX, viewOffsetY);
            g2.scale(canvasScale, canvasScale);
// ===== 新增：按区域绘制背景 =====
            // 用于记录每个区域的范围
            Map<BuildingZone, Rectangle> zoneBounds = new HashMap<>();

            for (int r = 0; r < MAP_ROWS; r++) {
                for (int c = 0; c < MAP_COLS; c++) {
                    String tag = buildingLayout[r][c];
                    if (tag.equals("NONE")) continue;

                    BuildingZone zone = buildingZoneMap.get(tag);
                    if (zone != null) {
                        int x = c * TILE_SIZE;
                        int y = r * TILE_SIZE;
                        Rectangle rect = zoneBounds.get(zone);
                        if (rect == null) {
                            rect = new Rectangle(x, y, TILE_SIZE, TILE_SIZE);
                            zoneBounds.put(zone, rect);
                        } else {
                            rect.setBounds(
                                    Math.min(rect.x, x),
                                    Math.min(rect.y, y),
                                    Math.max(rect.x + rect.width, x + TILE_SIZE) - Math.min(rect.x, x),
                                    Math.max(rect.y + rect.height, y + TILE_SIZE) - Math.min(rect.y, y)
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
            // 绘制网格和建筑（原有代码保持不变）
            // 在 paintComponent 方法中，替换建筑绘制部分
            for (int r = 0; r < MAP_ROWS; r++) {
                for (int c = 0; c < MAP_COLS; c++) {
                    int x = c * TILE_SIZE, y = r * TILE_SIZE;
                    String tag = buildingLayout[r][c];
                    if (tag.equals("NONE")) continue;

                    // 获取建筑所属区域
                    BuildingZone zone = buildingZoneMap.get(tag);
                    Color baseColor = zone != null ? zone.borderColor : new Color(45, 48, 58);

                    // 建筑背景使用区域颜色的半透明版本
                    g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 80));
                    g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                    g2.setColor(baseColor);
                    g2.drawRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                    g2.setFont(new Font("Consolas", Font.BOLD, 8));
                    g2.setColor(Color.WHITE);

                    // 特殊建筑保持原有颜色逻辑（PC、服务器等）
                    if (tag.equals("PC_FACTORY")) {
                        g2.setColor(new Color(0, 130, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[PC] 源PC", x + 4, y + 24);
                    } else if (tag.equals("RX_ST")) {
                        g2.setColor(new Color(190, 30, 50));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.YELLOW);
                        g2.drawString("[SRV] 服务器", x + 2, y + 24);
                        for (int b = 0; b < serverBufferCount; b++) {
                            g2.setColor(Color.RED);
                            g2.fillRect(x + 4 + (b * 6), y + 4, 5, 6);
                        }
                    }
                    // 矿机特殊处理
                    else if (tag.startsWith("MINER_H")) {
                        g2.setColor(new Color(0, 200, 200, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.CYAN);
                        g2.drawString("[H] 矿机", x + 4, y + 24);
                    } else if (tag.startsWith("MINER_S")) {
                        g2.setColor(new Color(0, 255, 0, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
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
            g2.fillRect(21 * TILE_SIZE, 0, 14 * TILE_SIZE, MAP_ROWS * TILE_SIZE);

            int wanCarCount = 0;
            for (DataCart c : dataCarts) {
                int col = (int) (c.x / TILE_SIZE);
                if (!c.isReturnTrip && c.stage >= 25 && c.stage <= 31 && col >= 21 && col <= 34) wanCarCount++;
            }
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("微软雅黑", Font.BOLD, 14));
            g2.drawString("[CAR] 公网车辆: " + wanCarCount + "/" + WAN_BOTTLE_NECK_MAX, 22 * TILE_SIZE, 30);

            // 绘制建筑
            for (int r = 0; r < MAP_ROWS; r++) {
                for (int c = 0; c < MAP_COLS; c++) {
                    int x = c * TILE_SIZE, y = r * TILE_SIZE;
                    String tag = buildingLayout[r][c];
                    if (tag.equals("NONE")) continue;

                    // 建筑背景
                    g2.setColor(new Color(45, 48, 58));
                    g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                    g2.setColor(Color.GRAY);
                    g2.drawRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                    g2.setFont(new Font("Consolas", Font.BOLD, 8));
                    g2.setColor(Color.WHITE);

                    // PC 工厂
                    if (tag.equals("PC_FACTORY")) {
                        g2.setColor(new Color(0, 130, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[PC] 源PC", x + 4, y + 24);
                    }
                    // 服务器
                    else if (tag.equals("RX_ST")) {
                        g2.setColor(new Color(190, 30, 50));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.YELLOW);
                        g2.drawString("[SRV] 服务器", x + 2, y + 24);
                        for (int b = 0; b < serverBufferCount; b++) {
                            g2.setColor(Color.RED);
                            g2.fillRect(x + 4 + (b * 6), y + 4, 5, 6);
                        }
                    }
                    // DHCP 相关
                    else if (tag.startsWith("DHCP_")) {
                        g2.setColor(new Color(100, 100, 255));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString(tag, x + 3, y + 24);
                    }
                    // DNS 相关
                    else if (tag.equals("DNS_CLIENT") || tag.equals("DNS_LOCAL") || tag.equals("DNS_ROOT") || tag.equals("DNS_AUTH")) {
                        g2.setColor(new Color(0, 200, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString(tag, x + 3, y + 24);
                    }
                    // 以太网相关
                    else if (tag.startsWith("ETH_")) {
                        g2.setColor(new Color(0, 160, 255));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString(tag, x + 3, y + 24);
                    }
                    // 路由器
                    else if (tag.startsWith("ROUTER")) {
                        g2.setColor(new Color(200, 100, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
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
                        g2.drawRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
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
            for (OreCart c : oreCarts) {
                g2.setColor(c.oreType.equals("HELLO") ? Color.CYAN : Color.GREEN);
                g2.fillOval((int) c.x - 5, (int) c.y - 5, 10, 10);
            }

            // 绘制数据包
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

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        SwingUtilities.invokeLater(() -> new DataCartFactoryGame().setVisible(true));
    }
}