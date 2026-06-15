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
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.ActionEvent;
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
import com.never_give_up.automation.demo.model.ArpEntry;
import com.never_give_up.automation.demo.model.BuildingZone;
import com.never_give_up.automation.demo.model.DnsEntry;
import com.never_give_up.automation.demo.model.GameContext;
import com.never_give_up.automation.demo.model.IpFragment;
import com.never_give_up.automation.demo.model.IpFragmentKey;
import com.never_give_up.automation.demo.model.NatEntry;
import com.never_give_up.automation.demo.model.PacketClass;
import com.never_give_up.automation.demo.model.RetransmissionTask;
import com.never_give_up.automation.demo.model.TcpState;
import com.never_give_up.automation.demo.model.TlsState;
import com.never_give_up.automation.demo.model.VisualFeedback;
import com.never_give_up.automation.demo.model.BeltDirection;

public class DataCartFactoryGame extends JFrame {
    // 添加建筑到区域的映射
    public Map<String, BuildingZone> buildingZoneMap = new HashMap<>();

    private int udpPacketsSent = 0;  // UDP 已发送包计数
    // 在类的字段声明区域添加
    public double canvasScale = 1.0;      // 当前缩放比例
    private final double MIN_SCALE = 0.5;   // 最小缩放比例
    private final double MAX_SCALE = 3.0;   // 最大缩放比例
    private final double SCALE_STEP = 0.1;  // 每次滚轮缩放步长
    // ===== 新增：拖拽平移相关字段 =====
    public int viewOffsetX = 0;     // 视图水平偏移（逻辑坐标）
    public int viewOffsetY = 0;     // 视图垂直偏移（逻辑坐标）
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

    private int funds = 3000;
    private int helloStock = 0;
    private int sayStock = 0;
    private int serverReceivedCount = 0;
    private final int totalDataToTransmit = 15;

    private TcpState currentTcpState = TcpState.CLOSED;
    private int rwnd = 3;
    private int cwnd = 1;
    private int ssthresh = 12;
    public int serverBufferCount = 0;
    private final int SERVER_BUFFER_MAX = 5;
    private long lastServerConsumeTime = 0;
    private int serverDecodeDelay = 600;
    public final int WAN_BOTTLE_NECK_MAX = 20; // 增加公网容量
    public long stateTimerWatchdog = 0;
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

    public final int TILE_SIZE = 40;
    public final int MAP_COLS = 55;
    public final int MAP_ROWS = 20;

    private int[][] mapLayout = new int[MAP_ROWS][MAP_COLS];
    public String[][] buildingLayout = new String[MAP_ROWS][MAP_COLS];
    private Point pcFactory;
    public List<OreCart> oreCarts = new CopyOnWriteArrayList<>();
    public List<DataCart> dataCarts = new CopyOnWriteArrayList<>();
    private List<DataCart> pendingDataCarts = new CopyOnWriteArrayList<>();

    // ========== 传送带物流系统 ==========
    public final int BELT_PRICE = 5;
    public BeltDirection[][] beltGrid = new BeltDirection[MAP_ROWS][MAP_COLS];
    public List<com.never_give_up.automation.demo.model.BeltItem> beltItems = new CopyOnWriteArrayList<>();
    public boolean beltMode = false;
    public BeltDirection selectedBeltDir = BeltDirection.RIGHT;
    private com.never_give_up.automation.demo.conveyor.BeltNetwork beltNetwork;

    private GameCanvas canvas;
    private GameContext gameContext;
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

        // 创建 GameContext 用于 DataCart 依赖注入
        gameContext = new GameContext(
            pcFactory,
            pendingDataCarts,
            natTable,
            natPortCounter,
            ipIdentifierCounter,
            factoryManager,
            this::appendToConsole,
            this::findBuildingCoords,
            this::updateArpDisplay,
            this::updateNatDisplay,
            this::updateDnsDisplay
        );

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
        canvas = new GameCanvas(this);
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

                                // 传送带模式：放置传送带
                                if (beltMode) {
                                    if (beltGrid[row][col] == BeltDirection.NONE && funds >= BELT_PRICE) {
                                        funds -= BELT_PRICE;
                                        beltGrid[row][col] = selectedBeltDir;
                                        appendToConsole(String.format("【🔧 传送带】: 放置 %s 方向传送带，花费 %d",
                                                selectedBeltDir, BELT_PRICE));
                                    } else if (beltGrid[row][col] != BeltDirection.NONE) {
                                        // 已有传送带则更改方向
                                        beltGrid[row][col] = selectedBeltDir;
                                        appendToConsole(String.format("【🔧 传送带】: 更改方向为 %s", selectedBeltDir));
                                    }
                                    updateTopLabel();
                                    canvas.repaint();
                                    return;
                                }

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
                            // 优先删除传送带
                            if (beltGrid[row][col] != BeltDirection.NONE) {
                                beltGrid[row][col] = BeltDirection.NONE;
                                funds += BELT_PRICE / 2;
                                canvas.repaint();
                                updateTopLabel();
                                appendToConsole("【🔧 传送带】: 移除传送带，返还 " + (BELT_PRICE / 2) + " 资金");
                                return;
                            }
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
                        DataCart hc = new DataCart(pcFactory.x, pcFactory.y, "HTTP_DEMO", 0, gameContext);
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

        beltNetwork = new com.never_give_up.automation.demo.conveyor.BeltNetwork();

        // 添加键盘绑定 - B 键切换传送带模式
        getRootPane().getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('B'), "toggleBeltMode");
        getRootPane().getActionMap().put("toggleBeltMode", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                beltMode = !beltMode;
                appendToConsole("【🔧】传送带模式: " + (beltMode ? "开启" : "关闭") +
                        " | 方向: " + selectedBeltDir);
            }
        });

        // R 键循环切换传送带方向
        getRootPane().getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('R'), "cycleBeltDir");
        getRootPane().getActionMap().put("cycleBeltDir", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                switch (selectedBeltDir) {
                    case RIGHT: selectedBeltDir = BeltDirection.DOWN; break;
                    case DOWN: selectedBeltDir = BeltDirection.LEFT; break;
                    case LEFT: selectedBeltDir = BeltDirection.UP; break;
                    case UP: selectedBeltDir = BeltDirection.RIGHT; break;
                    default: selectedBeltDir = BeltDirection.RIGHT;
                }
                appendToConsole("【🔧】传送带方向: " + selectedBeltDir);
            }
        });

        // T 键触发传送带演示
        getRootPane().getInputMap(JPanel.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke('T'), "startBeltDemo");
        getRootPane().getActionMap().put("startBeltDemo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startBeltDemo();
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

        // 初始化传送带网格为 NONE
        for (int r = 0; r < MAP_ROWS; r++)
            for (int c = 0; c < MAP_COLS; c++)
                beltGrid[r][c] = BeltDirection.NONE;

        // ===== 通用传送带集成：所有主路径建筑瓦片设置传送带方向 =====
        // 发送封装路径 (row 7, cols 4-26)
        for (int c = 4; c <= 26; c++) beltGrid[7][c] = BeltDirection.RIGHT;
        // DHCP 路径 (row 6, cols 4-8)
        for (int c = 4; c <= 8; c++) beltGrid[6][c] = BeltDirection.RIGHT;
        // 网关路径 (row 10, cols 20-32，含间隙瓦片)
        for (int c = 20; c <= 32; c++) beltGrid[10][c] = BeltDirection.RIGHT;
        // 接收解封装路径 (row 8, cols 34-42)
        for (int c = 34; c <= 42; c++) beltGrid[8][c] = BeltDirection.RIGHT;
        // 防火墙 (row 5, cols 20-22)
        for (int c = 20; c <= 22; c++) beltGrid[5][c] = BeltDirection.RIGHT;
        // 队列 (row 9, cols 22-24)
        for (int c = 22; c <= 24; c++) beltGrid[9][c] = BeltDirection.RIGHT;
        // 拥塞控制 (row 11, cols 14-16)
        for (int c = 14; c <= 16; c++) beltGrid[11][c] = BeltDirection.RIGHT;
        // 新建筑行 (row 1, cols 5-20)
        for (int c = 5; c <= 20; c++) beltGrid[1][c] = BeltDirection.RIGHT;
        // 新建筑行 (row 0, cols 2-15)
        for (int c = 2; c <= 15; c++) beltGrid[0][c] = BeltDirection.RIGHT;
        // 新建筑行 (row 0, cols 16-27)
        for (int c = 16; c <= 27; c++) beltGrid[0][c] = BeltDirection.RIGHT;
        // 新建筑行 (row 1, cols 21-27)
        for (int c = 21; c <= 27; c++) beltGrid[1][c] = BeltDirection.RIGHT;
        // IPv6 行 (row 2, cols 30-31) + 扩展 (cols 32-39)
        for (int c = 30; c <= 39; c++) beltGrid[2][c] = BeltDirection.RIGHT;
        // TCP 增强 (row 3, cols 33-38)
        for (int c = 33; c <= 38; c++) beltGrid[3][c] = BeltDirection.RIGHT;
        // 负载均衡 + IPsec (row 4, cols 36-44)
        for (int c = 36; c <= 44; c++) beltGrid[4][c] = BeltDirection.RIGHT;
        // 工具 + IPS (row 5, cols 41-44)
        for (int c = 41; c <= 44; c++) beltGrid[5][c] = BeltDirection.RIGHT;
        // 诊断工具 (rows 6-7, cols 41-43)
        for (int c = 41; c <= 43; c++) { beltGrid[6][c] = BeltDirection.RIGHT; beltGrid[7][c] = BeltDirection.RIGHT; }
        // 应用层协议 (row 12-14, cols 25-28)
        for (int c = 25; c <= 28; c++) beltGrid[12][c] = BeltDirection.RIGHT;
        for (int c = 25; c <= 28; c++) beltGrid[13][c] = BeltDirection.RIGHT;
        for (int c = 25; c <= 27; c++) beltGrid[14][c] = BeltDirection.RIGHT;
        // 安全防护 (row 15-17, cols 30-31)
        for (int c = 30; c <= 31; c++) { beltGrid[15][c] = BeltDirection.RIGHT; beltGrid[16][c] = BeltDirection.RIGHT; beltGrid[17][c] = BeltDirection.RIGHT; }
        // NAT 增强 (row 17-18, cols 35-36)
        for (int c = 35; c <= 36; c++) { beltGrid[17][c] = BeltDirection.RIGHT; beltGrid[18][c] = BeltDirection.RIGHT; }
        // VPN (row 18-19, cols 41-43)
        for (int c = 41; c <= 43; c++) { beltGrid[17][c] = BeltDirection.RIGHT; beltGrid[18][c] = BeltDirection.RIGHT; }
        // HTTP 子工厂 (row 18, cols 38-48)
        for (int c = 38; c <= 48; c++) beltGrid[18][c] = BeltDirection.RIGHT;
        // 核心工厂 (row 19, cols 30-54)
        for (int c = 30; c <= 54; c++) beltGrid[19][c] = BeltDirection.RIGHT;
        // PC 和接收站连接
        beltGrid[10][3] = BeltDirection.RIGHT;    // PC_FACTORY
        beltGrid[10][52] = BeltDirection.RIGHT;   // RX_ST

        // ===== 传送带演示区 =====
        // 行15: 4个建筑间隔1格，中间铺设传送带
        buildingLayout[15][0] = "BELT_DEMO_IN";
        buildingLayout[15][2] = "BELT_DEMO_PROC1";
        buildingLayout[15][4] = "BELT_DEMO_PROC2";
        buildingLayout[15][6] = "BELT_DEMO_OUT";
        // 预置传送带连接演示建筑
        beltGrid[15][1] = BeltDirection.RIGHT;
        beltGrid[15][3] = BeltDirection.RIGHT;
        beltGrid[15][5] = BeltDirection.RIGHT;
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
        DataCart pingReq = new DataCart(pcFactory.x, pcFactory.y, "ICMP_ECHO_REQ", 0, gameContext);
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
        DataCart probe = new DataCart(pcFactory.x, pcFactory.y, "ICMP_ECHO_REQ", 0, gameContext);
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
            DataCart discover = new DataCart(pcFactory.x, pcFactory.y, "DHCP_DISCOVER", 0, gameContext);
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
        DataCart syn = new DataCart(pcFactory.x, pcFactory.y, "SYN", 0, gameContext);
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

        DataCart dnsQuery = new DataCart(pcFactory.x, pcFactory.y, "DNS_QUERY", 0, gameContext);
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

        DataCart syn = new DataCart(pcFactory.x, pcFactory.y, "SYN", 0, gameContext);
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
            DataCart fin = new DataCart(pcFactory.x, pcFactory.y, "FIN_PC", 0, gameContext);
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

                    DataCart retransmit = new DataCart(pcFactory.x, pcFactory.y, "DATA", task.seqNum, gameContext);
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
                        oreCarts.add(new OreCart(c * TILE_SIZE + TILE_SIZE / 2, r * TILE_SIZE + TILE_SIZE / 2, "HELLO", pcFactory));
                    if (buildingLayout[r][c].equals("MINER_S"))
                        oreCarts.add(new OreCart(c * TILE_SIZE + TILE_SIZE / 2, r * TILE_SIZE + TILE_SIZE / 2, "SAY", pcFactory));
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
                    DataCart hello = new DataCart(pcFactory.x, pcFactory.y, "TLS_CLIENT_HELLO", 0, gameContext);
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
                    DataCart ftpUser = new DataCart(pcFactory.x, pcFactory.y, "FTP_USER", 0, gameContext);
                    ftpUser.stage = 161;  // 明确设置 stage
                    ftpUser.dstPort = 21;
                    ftpUser.ftpCommand = "USER anonymous";
                    pendingDataCarts.add(ftpUser);
                    ftpLoginSent = true;
                    appendToConsole("【📁 FTP】: 创建并发送 USER 命令 (stage=161, port=21)");
                } else if (ftpLoginSent && !ftpPassSent && ftpUserAcked) {
                    // 等待 USER 命令的响应后再发送 PASS
                    DataCart ftpPass = new DataCart(pcFactory.x, pcFactory.y, "FTP_PASS", 0, gameContext);
                    ftpPass.stage = 161;
                    ftpPass.dstPort = 21;
                    ftpPass.ftpCommand = "PASS anonymous@example.com";
                    pendingDataCarts.add(ftpPass);
                    ftpPassSent = true;
                    appendToConsole("【📁 FTP】: 创建并发送 PASS 命令");
                } else if (ftpPassSent && !ftpPasvMode && ftpPassAcked) {
                    // PASS 成功后发送 PASV
                    DataCart ftpPasv = new DataCart(pcFactory.x, pcFactory.y, "FTP_PASV", 0, gameContext);
                    ftpPasv.stage = 161;
                    ftpPasv.dstPort = 21;
                    ftpPasv.ftpCommand = "PASV";
                    pendingDataCarts.add(ftpPasv);
                    ftpPasvMode = true;
                    appendToConsole("【📁 FTP】: 创建并发送 PASV 命令");
                }
                // ========== 新增：发送 LIST 命令 ==========
                else if (ftpDemoEnabled && ftpLoggedIn && ftpPasvMode && ftpDataPort > 0 && !ftpListSent) {
                    DataCart ftpList = new DataCart(pcFactory.x, pcFactory.y, "FTP_LIST", 0, gameContext);
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
                    DataCart udpData = new DataCart(pcFactory.x, pcFactory.y, "UDP_DATA", udpSeqToSend++, gameContext);
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
                        DataCart fin = new DataCart(pcFactory.x, pcFactory.y, "FIN_PC", 0, gameContext);
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
                DataCart zwp = new DataCart(pcFactory.x, pcFactory.y, "ZWP", 0, gameContext);
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

        // ========== 传送带物流更新 ==========
        if (beltNetwork != null) {
            long now2 = System.currentTimeMillis();
            beltNetwork.updateAll(beltItems, beltGrid, buildingLayout, this);
            beltItems.removeIf(item ->
                    item.consumed || (now2 - item.createdAt > com.never_give_up.automation.demo.model.BeltItem.MAX_LIFETIME));
        }

        // ===== 检测新建 DataCart 是否可以进入传送带 =====
        for (DataCart cart : dataCarts) {
            if (cart.isOnBelt) continue;
            if (cart.isArrived || cart.isDropped) continue;
            tryActivateBeltForCart(cart);
        }

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

            // 传送带模式：跳过飞行 update
            if (cart.isOnBelt) {
                if (cart.isArrived || cart.isDropped) {
                    toRemoveCarts.add(cart);
                    updateTopLabel();
                }
                continue;
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
                DataCart icmpTE = new DataCart(cart.droppedAtPosition.x, cart.droppedAtPosition.y, "ICMP_TIMEEXCEEDED", 0, gameContext);
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

    /**
     * 启动传送带演示
     */
    private void startBeltDemo() {
        int col = 0, row = 15;
        int x = col * TILE_SIZE + TILE_SIZE / 2;
        int y = row * TILE_SIZE + TILE_SIZE / 2;
        DataCart cart = new DataCart(x, y, "BELT_DEMO", 1, gameContext);
        pendingDataCarts.add(cart);
        appendToConsole("【🧪 传送带演示】: 启动 BELT_DEMO 数据包 (按 B 放置传送带, R 切换方向)");
    }

    /**
     * 尝试将 DataCart 从飞行模式切换到传送带模式
     */
    private void tryActivateBeltForCart(DataCart cart) {
        int col = (int) (cart.x / TILE_SIZE);
        int row = (int) (cart.y / TILE_SIZE);
        if (row < 0 || row >= MAP_ROWS || col < 0 || col >= MAP_COLS) return;
        if (buildingLayout[row][col].equals("NONE")) return; // 不在建筑上

        Point nextTarget = cart.getTargetBuilding();
        if (nextTarget == null) return;

        int nextCol = nextTarget.x / TILE_SIZE;
        int nextRow = nextTarget.y / TILE_SIZE;

        // 计算方向（使用符号，支持非相邻建筑之间的传送带链路）
        int dRow = (int) Math.signum(nextRow - row);
        int dCol = (int) Math.signum(nextCol - col);
        if (dRow == 0 && dCol == 0) return;

        BeltDirection dir = BeltDirection.fromDelta(dCol, dRow);
        if (dir == BeltDirection.NONE) return;

        // 检查当前建筑瓦片上的传送带方向是否匹配（通用集成：beltGrid 直接设在建筑瓦片上）
        if (beltGrid[row][col] != dir) return;

        // 上带！
        com.never_give_up.automation.demo.model.BeltItem spawned =
                beltNetwork.spawnBeltItemFromCart(cart, row, col, dir, this);
        if (spawned != null) {
            cart.isOnBelt = true;
            appendToConsole("【🔧 传送带】: " + cart.cartType + " 进入传送带 " + dir);
        }
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
        DataCart get = new DataCart(pcFactory.x, pcFactory.y, "HTTP_GET", 0, gameContext);
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
                        DataCart offer = new DataCart(dhcpServerPos.x, dhcpServerPos.y, "DHCP_OFFER", 0, gameContext);
                        offer.isReturnTrip = true;
                        offer.stage = 1;
                        pendingDataCarts.add(offer);
                        appendToConsole("【📥 DHCP】: 服务器回复 Offer (192.168.1.100)");
                    }
                    break;
                case "DHCP_REQUEST":
                    if (cart.stage == 2) {
                        appendToConsole("【📤 DHCP】: Request 到达服务器");
                        DataCart ack = new DataCart(dhcpServerPos.x, dhcpServerPos.y, "DHCP_ACK", 0, gameContext);
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
                    DataCart request = new DataCart(pcFactory.x, pcFactory.y, "DHCP_REQUEST", 0, gameContext);
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
            DataCart echoReply = new DataCart(serverPos.x, serverPos.y, "ICMP_ECHO_REPLY", 0, gameContext);
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
                            DataCart reassembled = new DataCart(serverPos.x, serverPos.y, "DATA", 0, gameContext);
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
                        DataCart toRoot = new DataCart(cart.x, cart.y, "DNS_RECURSION_ROOT", 0, gameContext);
                        toRoot.domain = cart.domain;
                        toRoot.isReturnTrip = false;
                        pendingDataCarts.add(toRoot);
                        return;
                    }
                    appendToConsole("【📥 DNS 查询】: 到达 DNS 服务器，正在递归查询...");
                    DataCart dnsResp = new DataCart(cart.x, cart.y, "DNS_RESPONSE", 0, gameContext);
                    dnsResp.domain = cart.domain;
                    dnsResp.resolvedIp = "10.0.0.1";
                    dnsResp.isReturnTrip = true;
                    pendingDataCarts.add(dnsResp);
                    break;
                case "DNS_RECURSION_ROOT":
                    if (!cart.isReturnTrip && cart.isArrived) {
                        // 到达根 DNS，返回顶级域权威 DNS 地址（模拟为 10.1.1.1）
                        DataCart rootResp = new DataCart(cart.x, cart.y, "DNS_ROOT_TO_LOCAL", 0, gameContext);
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
                        DataCart toAuth = new DataCart(cart.x, cart.y, "DNS_LOCAL_TO_AUTH", 0, gameContext);
                        toAuth.domain = cart.domain;
                        toAuth.isReturnTrip = false;
                        pendingDataCarts.add(toAuth);
                        appendToConsole("【 本地 DNS】: 向权威 DNS 查询 " + cart.domain);
                    }
                    break;
                case "DNS_LOCAL_TO_AUTH":
                    if (!cart.isReturnTrip && cart.isArrived) {
                        // 权威 DNS 响应最终 IP
                        DataCart authResp = new DataCart(cart.x, cart.y, "DNS_AUTH_TO_LOCAL", 0, gameContext);
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
                        DataCart finalResp = new DataCart(cart.x, cart.y, "DNS_RESPONSE", 0, gameContext);
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
                        DataCart toAuth = new DataCart(findBuildingCoords("DNS_AUTH").x, findBuildingCoords("DNS_AUTH").y, "DNS_RECURSION_AUTH", 0, gameContext);
                        toAuth.domain = cart.domain;
                        toAuth.isReturnTrip = false;
                        pendingDataCarts.add(toAuth);
                        appendToConsole("【📤 本地 DNS】: 向权威 DNS 查询 " + cart.domain);
                    }
                    break;
                case "DNS_RECURSION_AUTH":
                    if (!cart.isReturnTrip && cart.isArrived) {
                        // 权威 DNS 响应最终 IP
                        DataCart authResp = new DataCart(cart.x, cart.y, "DNS_RECURSION_AUTH_RESP", 0, gameContext);
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
                        DataCart finalResp = new DataCart(findBuildingCoords("DNS_LOCAL").x, findBuildingCoords("DNS_LOCAL").y, "DNS_RESPONSE", 0, gameContext);
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
                    DataCart request = new DataCart(pcFactory.x, pcFactory.y, "DHCP_REQUEST", 0, gameContext);
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
                            DataCart ftpResponse = new DataCart(serverPos.x, serverPos.y, "FTP_RESPONSE", 0, gameContext);
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
                                    DataCart listComplete = new DataCart(serverPos.x, serverPos.y, "FTP_RESPONSE", 0, gameContext);
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
                            DataCart ftpResponse = new DataCart(serverPos.x, serverPos.y, "FTP_RESPONSE", 0, gameContext);
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
                                    DataCart listComplete = new DataCart(serverPos.x, serverPos.y, "FTP_RESPONSE", 0, gameContext);
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
                                DataCart dataAck = new DataCart(serverPos.x, serverPos.y, "DATA_ACK", receivedSeq, gameContext);
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
                        DataCart shCert = new DataCart(serverPos.x, serverPos.y, "TLS_SERVER_HELLO_CERT", 0, gameContext);
                        shCert.isReturnTrip = true;
                        shCert.stage = -1;
                        shCert.serverCertificate = serverRsaKeyPair.getPublic().getEncoded();
                        pendingDataCarts.add(shCert);
                        appendToConsole("【 TLS】: 服务器回复 Server Hello + Certificate（RSA 公钥）");
                    }
                    return;
                case "TLS_CLIENT_FINISHED":
                    if (cart.isArrived) {
                        DataCart sf = new DataCart(serverPos.x, serverPos.y, "TLS_SERVER_FINISHED", 0, gameContext);
                        sf.isReturnTrip = true;
                        sf.stage = -1;
                        pendingDataCarts.add(sf);
                        appendToConsole("【🔒 TLS】: 服务器收到 Finished，回复 ChangeCipherSpec + Finished");
                    }
                    return;
                case "HTTP_GET":
                    if (cart.isArrived) {
                        DataCart httpOk = new DataCart(serverPos.x, serverPos.y, "HTTP_200_OK", 0, gameContext);
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
                        DataCart sf = new DataCart(serverPos.x, serverPos.y, "TLS_SERVER_FINISHED", 0, gameContext);
                        sf.isReturnTrip = true;
                        sf.stage = -1;
                        pendingDataCarts.add(sf);
                        appendToConsole("【🔒 TLS】: 服务器发送 ChangeCipherSpec + Finished");
                    }
                    return;
            }
            if (cart.cartType.equals("SYN") && !cart.isReturnTrip && cart.isArrived) {
                DataCart synAck = new DataCart(serverPos.x, serverPos.y, "SYN_ACK", 0, gameContext);
                synAck.isReturnTrip = true;
                synAck.ackNumber = cart.sequenceNumber + 1;
                synAck.sequenceNumber = 200;
                pendingDataCarts.add(synAck);
                appendToConsole("【🤝 三次握手】: 收到 SYN，回复 SYN-ACK (seq=200, ack=" + (cart.sequenceNumber + 1) + ")");
                stateTimerWatchdog = now;
                return;
            }
            if (cart.cartType.equals("FIN_PC") && !cart.isReturnTrip && cart.isArrived) {
                DataCart finAck = new DataCart(serverPos.x, serverPos.y, "FIN_ACK_SRV", 0, gameContext);
                finAck.isReturnTrip = true;
                pendingDataCarts.add(finAck);
                DataCart srvFin = new DataCart(serverPos.x, serverPos.y, "FIN_SRV", 0, gameContext);
                srvFin.isReturnTrip = true;
                pendingDataCarts.add(srvFin);
                appendToConsole("【👋 四次挥手】: 收到 FIN，回复 FIN-ACK，发送 FIN");
                stateTimerWatchdog = now;
                return;
            }
            if (cart.cartType.equals("ZWP") && !cart.isReturnTrip && cart.isArrived) {
                DataCart probeAck = new DataCart(serverPos.x, serverPos.y, "DATA_ACK", 0, gameContext);
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
                            DataCart cke = new DataCart(pcFactory.x, pcFactory.y, "TLS_CLIENT_KEY_EXCHANGE", 0, gameContext);
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
                    DataCart finalAck = new DataCart(pcFactory.x, pcFactory.y, "ACK_PC", 0, gameContext);
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
                            DataCart hello = new DataCart(pcFactory.x, pcFactory.y, "TLS_CLIENT_HELLO", 0, gameContext);
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
                                    DataCart ftpUser = new DataCart(pcFactory.x, pcFactory.y, "FTP_USER", 0, gameContext);
                                    ftpUser.stage = 5;
                                    ftpUser.ftpCommand = "USER anonymous";
                                    pendingDataCarts.add(ftpUser);
                                    ftpLoginSent = true;
                                    appendToConsole("【📁 FTP】: 发送 USER anonymous");
                                } else if (ftpLoginSent && !ftpPassSent) {
                                    // 发送PASS命令
                                    DataCart ftpPass = new DataCart(pcFactory.x, pcFactory.y, "FTP_PASS", 0, gameContext);
                                    ftpPass.stage = 5;
                                    ftpPass.ftpCommand = "PASS anonymous@example.com";
                                    pendingDataCarts.add(ftpPass);
                                    ftpPassSent = true;
                                    appendToConsole("【📁 FTP】: 发送 PASS 命令");
                                }
                            } else if (ftpDemoEnabled && ftpLoggedIn && !ftpPasvMode) {
                                // 登录成功后进入PASV模式
                                DataCart ftpPasv = new DataCart(pcFactory.x, pcFactory.y, "FTP_PASV", 0, gameContext);
                                ftpPasv.stage = 5;
                                ftpPasv.ftpCommand = "PASV";
                                pendingDataCarts.add(ftpPasv);
                                ftpPasvMode = true;
                                appendToConsole("【📁 FTP】: 请求被动模式 PASV");
                            } else if (ftpDemoEnabled && ftpLoggedIn && ftpPasvMode && ftpDataPort > 0) {
                                // PASV模式成功后，发送LIST命令
                                DataCart ftpList = new DataCart(pcFactory.x, pcFactory.y, "FTP_LIST", 0, gameContext);
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
                    DataCart lastAck = new DataCart(pcFactory.x, pcFactory.y, "LAST_ACK_PC", 0, gameContext);
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
                    DataCart fin = new DataCart(pcFactory.x, pcFactory.y, "FIN_PC", 0, gameContext);
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
            DataCart data = new DataCart(pcFactory.x, pcFactory.y, "DATA", seq, gameContext);
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

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        SwingUtilities.invokeLater(() -> new DataCartFactoryGame().setVisible(true));
    }
}