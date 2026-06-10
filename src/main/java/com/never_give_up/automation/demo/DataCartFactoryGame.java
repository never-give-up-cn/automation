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
import com.never_give_up.automation.demo.factory.application.ftp.FtpPacketFactory;
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
    private com.never_give_up.automation.demo.factory.link.EthernetFactory ethernetFactory;
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
    private final int WAN_BOTTLE_NECK_MAX = 10;
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
                {"【28. 应用层协议】", "FTP", "📁 FTP(84)", "SMTP", "📧 SMTP(85)", "POP3", "📬 POP3(86)", "IMAP", "📨 IMAP(87)", "SSH", "🔐 SSH(88)", "TELNET", "💻 Telnet(89)", "RTP", "🎵 RTP(90)", "SIP", "📞 SIP(91)", "RADIUS", "🔑 RADIUS(92)"},
                {"【29. 安全防护】", "DPI", "🔍 DPI深度检测(96)", "WAF", "🛡️ WAF防火墙(97)", "DDOS", "💥 DDoS防护(98)", "RATELIMIT", "⏱️ 速率限制(99)", "ACL", "🚫 访问控制(100)"}
                ,{"【30. 核心网络服务 Stage 101-120】",
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
                ,{"【31. 链路层增强 Stage 121-124】",
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
            // 检查超时
            if (System.currentTimeMillis() - lastDnsQueryTime > DNS_TIMEOUT) {
                appendToConsole("【⚠️ DNS 超时】: 重试 (" + (dnsRetryCount + 1) + "/3)");
                dnsRetryCount++;
                if (dnsRetryCount >= 3) {
                    appendToConsole("【❌ DNS 失败】: 无法解析域名 " + targetDomain + "，使用默认 IP");
                    resolvedServerIp = "10.0.0.1";
                    isDnsResolved = true;
                    isDnsResolving = false;
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
        // 使用 factoryManager 的 arpCache
        String mac = factoryManager.getArpCache().getMac(targetIp);
        if (mac == null) {
            appendToConsole("【🔍 ARP 请求】: 谁拥有 " + targetIp + "？");
            String newMac = String.format("00:1A:2B:%02X:%02X:%02X",
                    new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
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

        // 修改超时处理，增加状态检查避免重复重置
        if (!useUdp && now - stateTimerWatchdog > 300000) {
            // 如果正在 DNS 解析中，不要重置，而是重试 DNS
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
                // 修改 DNS_RESPONSE 的处理
                case "DNS_RESPONSE":
                    resolvedServerIp = cart.resolvedIp;
                    isDnsResolved = true;
                    isDnsResolving = false;
                    dnsCache.put(targetDomain, new DnsEntry(targetDomain, resolvedServerIp, 3600000));
                    updateDnsDisplay();
                    appendToConsole("【 DNS 解析成功】: " + targetDomain + " → " + resolvedServerIp);
                    performArpResolution(resolvedServerIp);

                    // 修复：延迟一点再开始握手，避免状态混乱
                    Timer startTimer = new Timer(100, ev -> {
                        if (!useUdp && currentTcpState == TcpState.CLOSED) {
                            startTcpHandshake();
                        } else if (useUdp && !udpActive) {
                            startUdpTransmission();
                        }
                    });
                    startTimer.setRepeats(false);
                    startTimer.start();
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
        }

        private String getSrcIp() {
            return srcIp != null ? srcIp : (pcIpAddress != null ? pcIpAddress : "192.168.1.100");
        }

        private String getDstIp() {
            return dstIp != null ? dstIp : (resolvedServerIp != null ? resolvedServerIp : "10.0.0.1");
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
                        // 增加 stage 上限到 160
                        if (stage < 160) {
                            timer = 1;
                            stage++;
                        } else {
                            isArrived = true;
                        }
                    } else {
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

            // 使用 factoryManager 的 natFactory
            NatMappingFactory.NatEntry factoryEntry = factoryManager.getNatFactory().createMapping(insideIp, insidePort);

            // 同步到本地 NAT 表
            String key = insideIp + ":" + insidePort;
            if (!natTable.containsKey(key)) {
                NatEntry localEntry = new NatEntry(factoryEntry.getInsideIp(), factoryEntry.getInsidePort(),
                        factoryEntry.getPublicIp(), factoryEntry.getPublicPort());
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
                case 101: tag = "SOCKET"; break;
                case 102: tag = "TCP_STATE"; break;
                case 103: tag = "MAC_TABLE"; break;
                case 104: tag = "CAM_TABLE"; break;
                case 105: tag = "FIB"; break;
                case 106: tag = "SESSION_TABLE"; break;
                case 107: tag = "FLOW"; break;
                case 108: tag = "LOAD_BALANCER"; break;
                case 109: tag = "SCHEDULER"; break;
                case 110: tag = "DNS_ZONE"; break;
                case 111: tag = "DHCP_LEASE"; break;
                case 112: tag = "ARP_TABLE"; break;
                case 113: tag = "NEIGHBOR_TABLE"; break;
                case 114: tag = "MCAST_ROUTE"; break;
                case 115: tag = "MPLS_LABEL"; break;
                case 116: tag = "CERT_STORE"; break;
                case 117: tag = "EVENT"; break;
                case 118: tag = "STATS"; break;
                case 119: tag = "LOG"; break;
                case 120: tag = "PCAP"; break;
                case 121: tag = "LLDP"; break;
                case 122: tag = "STP"; break;
                case 123: tag = "LACP"; break;
                case 124: tag = "MPLS"; break;
                case 125: tag = "PIM_SM"; break;
                case 126: tag = "MLD"; break;
                case 127: tag = "DVMRP"; break;
                case 128: tag = "NETFLOW"; break;
                case 129: tag = "SFLOW"; break;
                case 130: tag = "IPFIX"; break;
                case 131: tag = "ICMP_PING"; break;
                case 132: tag = "ICMP_TRACE"; break;
                case 133: tag = "X509"; break;
                case 134: tag = "CRL"; break;
                case 135: tag = "OCSP"; break;
                case 136: tag = "PKI"; break;
                case 137: tag = "DTLS"; break;
                case 138: tag = "MAC_AUTH"; break;
                case 139: tag = "DOT1X"; break;
                case 141: tag = "NETSTAT"; break;
                case 142: tag = "IPCONFIG"; break;
                case 143: tag = "ROUTEPRINT"; break;
                case 144: tag = "NSLOOKUP"; break;
                case 145: tag = "ARPCMD"; break;
                case 146: tag = "CURL"; break;
                case 147: tag = "WGET"; break;
                case 148: tag = "TELNET_CLIENT"; break;
                case 149: tag = "NAT_HAIRPIN"; break;
                case 150: tag = "NAT_HOLE"; break;
                case 151: tag = "UPNP"; break;
                case 152: tag = "PCP"; break;
                case 153: tag = "IPSEC_IKE"; break;
                case 154: tag = "IPSEC_ESP"; break;
                case 155: tag = "IPSEC_AH"; break;
                case 156: tag = "OPENVPN"; break;
                case 157: tag = "WIREGUARD"; break;
                case 158: tag = "L2TP"; break;
                case 159: tag = "SSTP"; break;
                case 160: tag = "IPS"; break;
                default:
                    return null;
            }
            return findBuildingCoords(tag);
        }

        private void processStageCraft() {
            if (cartType.startsWith("DHCP")) return;

            switch (stage) {
                // ========== 应用层 ==========
                case 5:
                    if (!hasApp && cartType.equals("HTTP_GET")) {
                        hasApp = true;
                        // 使用 factoryManager 的 httpFactory
                        httpBody = factoryManager.getHttpFactory().createGetRequest("/index.html").getBody();
                        appendToConsole("【📦 应用层】: HTTP GET 请求封装");
                    } else if (!hasApp && cartType.equals("TLS_CLIENT_HELLO")) {
                        hasApp = true;
                        // 使用 factoryManager 的 tlsFactory
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
                        // 使用 factoryManager 的 fiveTupleFactory
                        factoryManager.getFiveTupleFactory().extract(srcIp, dstIp, srcPort, dstPort, protocol);
                        appendToConsole(String.format("【🔢 五元组】: %s %s:%d → %s:%d",
                                protocol, srcIp, srcPort, dstIp, dstPort));
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
                        appendToConsole(String.format("【📡 HTTP/2.3】: HEADERS 帧 (StreamID=%d)", streamId));
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
                        byte[] userCmd = ftpPacketFactory.buildFtpCommand("USER anonymous");
                        byte[] passCmd = ftpPacketFactory.buildFtpCommand("PASS test@example.com");
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
            g2.drawString("[CAR] 公网车辆: " + wanCarCount + "/" + WAN_BOTTLE_NECK_MAX, 22 * TILE_SIZE, 30);

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
                        g2.drawString("[H] 矿机", x + 4, y + 24);
                    } else if (tag.startsWith("MINER_S")) {
                        g2.setColor(Color.GREEN);
                        g2.drawString("[S] 矿机", x + 4, y + 24);
                    } else if (tag.equals("FW_IN") || tag.equals("FW_OUT")) {
                        g2.setColor(new Color(255, 80, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[FW] " + tag, x + 3, y + 24);
                    } else if (tag.equals("IDS")) {
                        g2.setColor(new Color(255, 165, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[IDS] IDS", x + 3, y + 24);
                    } else if (tag.equals("Q_IN") || tag.equals("Q_OUT") || tag.equals("Q_DROP")) {
                        g2.setColor(new Color(100, 100, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[Q] " + tag, x + 3, y + 24);
                    } else if (tag.startsWith("CC_")) {
                        g2.setColor(new Color(0, 200, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        String ccName = tag.equals("CC_SLOW") ? "[SS] 慢启动" :
                                (tag.equals("CC_AVOID") ? "[CA] 拥塞避免" : "[FR] 快速重传");
                        g2.drawString(ccName, x + 2, y + 24);
                    } else if (tag.equals("FIVETUPLE")) {
                        g2.setColor(new Color(200, 100, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[5T] 五元组", x + 2, y + 24);
                    } else if (tag.equals("SESSION")) {
                        g2.setColor(new Color(100, 200, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[SES] 会话", x + 4, y + 24);
                    } else if (tag.equals("BW_CTRL")) {
                        g2.setColor(new Color(200, 200, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[BW] 带宽", x + 4, y + 24);
                    } else if (tag.equals("SWITCH")) {
                        g2.setColor(new Color(0, 150, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[SW] 交换机", x + 2, y + 24);
                    } else if (tag.equals("HUB")) {
                        g2.setColor(new Color(150, 100, 50));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[HB] 集线器", x + 2, y + 24);
                    } else if (tag.equals("BRIDGE")) {
                        g2.setColor(new Color(100, 150, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[BR] 网桥", x + 4, y + 24);
                    } else if (tag.startsWith("SUBNET_")) {
                        g2.setColor(new Color(80, 80, 200, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.CYAN);
                        g2.drawString("[NET] " + tag, x + 2, y + 24);
                    } else if (tag.equals("LINK_UP")) {
                        g2.setColor(new Color(0, 255, 0, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.GREEN);
                        g2.drawString("[LINK] 链路↑", x + 4, y + 24);
                    } else if (tag.equals("LINK_DOWN")) {
                        g2.setColor(new Color(255, 0, 0, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.RED);
                        g2.drawString("[LINK] 链路↓", x + 4, y + 24);
                    } else if (tag.equals("RX_ETH")) {
                        g2.setColor(new Color(0, 100, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[RX] 解ETH", x + 4, y + 24);
                    } else if (tag.equals("RX_FCS")) {
                        g2.setColor(new Color(0, 150, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[OK] FCS", x + 6, y + 24);
                    } else if (tag.equals("RX_FRAG")) {
                        g2.setColor(new Color(200, 100, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[FR] 分片重组", x + 2, y + 24);
                    } else if (tag.equals("RX_PORT")) {
                        g2.setColor(new Color(150, 0, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[PT] 解端口", x + 4, y + 24);
                    } else if (tag.equals("TCP_OPTION")) {
                        g2.setColor(new Color(100, 100, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[TCP] TCP选项", x + 2, y + 24);
                    } else if (tag.equals("IP_OPTION")) {
                        g2.setColor(new Color(200, 100, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[IP] IP选项", x + 4, y + 24);
                    } else if (tag.equals("ETH_PADDING")) {
                        g2.setColor(new Color(100, 200, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[PAD] 填充", x + 6, y + 24);
                    } else if (tag.equals("UDP_CHECKSUM")) {
                        g2.setColor(new Color(0, 150, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[UDP] UDP校验", x + 2, y + 24);
                    } else if (tag.equals("IP_FORWARD")) {
                        g2.setColor(new Color(200, 150, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[FWD] IP转发", x + 4, y + 24);
                    } else if (tag.equals("ICMP_ERROR")) {
                        g2.setColor(new Color(200, 150, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("ICMP_ERROR", x + 4, y + 24);
                    } else if (tag.equals("TCP_WINDOW")) {
                        g2.setColor(new Color(0, 100, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[WIN] TCP窗口", x + 2, y + 24);
                    } else if (tag.equals("TCP_TIMER")) {
                        g2.setColor(new Color(150, 100, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[TMR] TCP定时", x + 2, y + 24);
                    } else if (tag.equals("DHCP_FULL")) {
                        g2.setColor(new Color(0, 200, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[DHCP] DHCP完整", x + 2, y + 24);
                    } else if (tag.equals("TLS_HANDSHAKE")) {
                        g2.setColor(new Color(200, 100, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[TLS] TLS握手", x + 4, y + 24);
                    } else if (tag.equals("SERIALIZE")) {
                        g2.setColor(new Color(150, 150, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[SER] 序列化", x + 6, y + 24);
                    } else if (tag.equals("BIT_STREAM")) {
                        g2.setColor(new Color(150, 150, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[BIT] 比特流", x + 4, y + 24);
                    } else if (tag.equals("PHY_CHANNEL")) {
                        g2.setColor(new Color(100, 100, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[PHY] 物理信道", x + 4, y + 24);
                    } else if (tag.equals("PPPOE")) {
                        g2.setColor(new Color(0, 150, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[PPPoE] PPPoE", x + 2, y + 24);
                    } else if (tag.equals("MACSEC")) {
                        g2.setColor(new Color(0, 200, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[MACsec] MACsec", x + 2, y + 24);
                    } else if (tag.equals("OSPF")) {
                        g2.setColor(new Color(0, 150, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[OSPF] OSPF", x + 4, y + 24);
                    } else if (tag.equals("BGP")) {
                        g2.setColor(new Color(0, 100, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[BGP] BGP", x + 6, y + 24);
                    } else if (tag.equals("QOS")) {
                        g2.setColor(new Color(200, 100, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[QoS] QoS", x + 8, y + 24);
                    } else if (tag.equals("NAT64")) {
                        g2.setColor(new Color(100, 100, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[NAT64] NAT64", x + 4, y + 24);
                    } else if (tag.equals("TCP_REASSEMBLY")) {
                        g2.setColor(new Color(150, 80, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[RE] TCP重组", x + 4, y + 24);
                    } else if (tag.equals("ATTACK")) {
                        g2.setColor(new Color(200, 50, 50));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[ATT] 攻击检测", x + 4, y + 24);
                    } else if (tag.equals("NTP")) {
                        g2.setColor(new Color(0, 200, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[NTP] NTP", x + 8, y + 24);
                    } else if (tag.equals("SNMP")) {
                        g2.setColor(new Color(0, 150, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[SNMP] SNMP", x + 4, y + 24);
                    } else if (tag.equals("HTTP23")) {
                        g2.setColor(new Color(50, 150, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[H2] HTTP/2.3", x + 2, y + 24);
                    } else if (tag.equals("IPSEC")) {
                        g2.setColor(new Color(50, 100, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[IPSEC] IPsec", x + 4, y + 24);
                    } else if (tag.equals("IPV6")) {
                        g2.setColor(new Color(80, 80, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[V6] IPv6", x + 6, y + 24);
                    } else if (tag.equals("IPV6_FRAG")) {
                        g2.setColor(new Color(80, 100, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[FRAG] IPv6分片", x + 2, y + 24);
                    } else if (tag.equals("IPV6_OPTION")) {
                        g2.setColor(new Color(100, 80, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[OPT] IPv6选项", x + 2, y + 24);
                    } else if (tag.equals("IPV6_ND")) {
                        g2.setColor(new Color(60, 100, 160));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[ND] IPv6 ND", x + 4, y + 24);
                    } else if (tag.equals("TCP_KEEPALIVE")) {
                        g2.setColor(new Color(100, 150, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[KA] KeepAlive", x + 2, y + 24);
                    } else if (tag.equals("TCP_SACK")) {
                        g2.setColor(new Color(120, 140, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[SACK] SACK", x + 4, y + 24);
                    } else if (tag.equals("TCP_ECN")) {
                        g2.setColor(new Color(140, 120, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[ECN] ECN", x + 8, y + 24);
                    } else if (tag.equals("TCP_FASTOPEN")) {
                        g2.setColor(new Color(160, 100, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[FO] FastOpen", x + 2, y + 24);
                    } else if (tag.equals("FTP")) {
                        g2.setColor(new Color(100, 100, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[FTP] FTP", x + 8, y + 24);
                    } else if (tag.equals("SMTP")) {
                        g2.setColor(new Color(120, 100, 160));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[SMTP] SMTP", x + 4, y + 24);
                    } else if (tag.equals("POP3")) {
                        g2.setColor(new Color(140, 100, 140));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[POP3] POP3", x + 4, y + 24);
                    } else if (tag.equals("IMAP")) {
                        g2.setColor(new Color(160, 100, 120));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[IMAP] IMAP", x + 4, y + 24);
                    } else if (tag.equals("SSH")) {
                        g2.setColor(new Color(100, 140, 140));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[SSH] SSH", x + 8, y + 24);
                    } else if (tag.equals("TELNET")) {
                        g2.setColor(new Color(120, 120, 120));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[TEL] Telnet", x + 4, y + 24);
                    } else if (tag.equals("RTP")) {
                        g2.setColor(new Color(100, 160, 120));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[RTP] RTP", x + 8, y + 24);
                    } else if (tag.equals("SIP")) {
                        g2.setColor(new Color(140, 140, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[SIP] SIP", x + 8, y + 24);
                    } else if (tag.equals("RADIUS")) {
                        g2.setColor(new Color(160, 120, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[RAD] RADIUS", x + 4, y + 24);
                    } else if (tag.equals("DIAMETER")) {
                        g2.setColor(new Color(180, 100, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[DIA] Diameter", x + 2, y + 24);
                    } else if (tag.equals("LDAP")) {
                        g2.setColor(new Color(100, 180, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[LDA] LDAP", x + 6, y + 24);
                    } else if (tag.equals("NAT_HAIRPIN")) {
                        g2.setColor(new Color(180, 140, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[NH] NAT发夹", x + 2, y + 24);
                    } else if (tag.equals("NAT_HOLE")) {
                        g2.setColor(new Color(160, 120, 60));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[HP] NAT穿透", x + 2, y + 24);
                    } else if (tag.equals("UPNP")) {
                        g2.setColor(new Color(140, 100, 40));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[UPnP] UPnP", x + 4, y + 24);
                    } else if (tag.equals("PCP")) {
                        g2.setColor(new Color(120, 80, 20));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[PCP] PCP", x + 8, y + 24);
                    } else if (tag.equals("LB_RR")) {
                        g2.setColor(new Color(80, 180, 140));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[RR] 轮询LB", x + 4, y + 24);
                    } else if (tag.equals("LB_LC")) {
                        g2.setColor(new Color(60, 160, 120));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[LC] 最少连接", x + 2, y + 24);
                    } else if (tag.equals("LB_IPHASH")) {
                        g2.setColor(new Color(40, 140, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[IPH] IP哈希", x + 4, y + 24);
                    } else if (tag.equals("LB_HC")) {
                        g2.setColor(new Color(20, 120, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[HC] 健康检查", x + 2, y + 24);
                    } else if (tag.equals("IPSEC_IKE")) {
                        g2.setColor(new Color(100, 80, 140));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[IKE] IKE", x + 8, y + 24);
                    } else if (tag.equals("IPSEC_ESP")) {
                        g2.setColor(new Color(120, 80, 160));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[ESP] ESP", x + 8, y + 24);
                    } else if (tag.equals("OPENVPN")) {
                        g2.setColor(new Color(100, 100, 160));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[OVPN] OpenVPN", x + 2, y + 24);
                    } else if (tag.equals("WIREGUARD")) {
                        g2.setColor(new Color(80, 120, 140));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[WG] WireGuard", x + 2, y + 24);
                    } else if (tag.equals("L2TP")) {
                        g2.setColor(new Color(60, 140, 120));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[L2] L2TP", x + 8, y + 24);
                    } else if (tag.equals("SSTP")) {
                        g2.setColor(new Color(40, 160, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[SSTP] SSTP", x + 6, y + 24);
                    } else if (tag.equals("DPI")) {
                        g2.setColor(new Color(180, 80, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[DPI] DPI", x + 8, y + 24);
                    } else if (tag.equals("WAF")) {
                        g2.setColor(new Color(200, 60, 60));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[WAF] WAF", x + 8, y + 24);
                    } else if (tag.equals("DDOS")) {
                        g2.setColor(new Color(220, 40, 40));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[DDoS] DDoS防护", x + 2, y + 24);
                    } else if (tag.equals("RATELIMIT")) {
                        g2.setColor(new Color(160, 100, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[RL] 限速", x + 8, y + 24);
                    } else if (tag.equals("ACL")) {
                        g2.setColor(new Color(140, 120, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[ACL] ACL", x + 8, y + 24);
                    } else if (tag.equals("NETSTAT")) {
                        g2.setColor(new Color(80, 140, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[STAT] netstat", x + 4, y + 24);
                    } else if (tag.equals("IPCONFIG")) {
                        g2.setColor(new Color(60, 120, 160));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[IPCFG] ipconfig", x + 2, y + 24);
                    } else if (tag.equals("ROUTEPRINT")) {
                        g2.setColor(new Color(40, 100, 140));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[RT] route", x + 8, y + 24);
                    } else if (tag.equals("NSLOOKUP")) {
                        g2.setColor(new Color(60, 100, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[NS] nslookup", x + 4, y + 24);
                    } else if (tag.equals("ARPCMD")) {
                        g2.setColor(new Color(40, 80, 160));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[ARP] arp", x + 8, y + 24);
                    } else if (tag.equals("CURL")) {
                        g2.setColor(new Color(80, 180, 160));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[CURL] curl", x + 6, y + 24);
                    } else if (tag.equals("WGET")) {
                        g2.setColor(new Color(60, 160, 140));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[WGET] wget", x + 6, y + 24);
                    } else if (tag.equals("TELNET_CLIENT")) {
                        g2.setColor(new Color(40, 140, 120));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[TEL] telnet", x + 6, y + 24);
                    } else if (tag.equals("LLDP")) {
                        g2.setColor(new Color(0, 180, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[LLDP] LLDP", x + 4, y + 24);
                    } else if (tag.equals("STP")) {
                        g2.setColor(new Color(0, 160, 160));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[STP] STP", x + 8, y + 24);
                    } else if (tag.equals("LACP")) {
                        g2.setColor(new Color(0, 140, 140));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[LACP] LACP", x + 4, y + 24);
                    } else if (tag.equals("MPLS")) {
                        g2.setColor(new Color(0, 120, 120));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[MPLS] MPLS", x + 4, y + 24);
                    } else if (tag.equals("PIM_SM")) {
                        g2.setColor(new Color(180, 100, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[PIM] PIM-SM", x + 4, y + 24);
                    } else if (tag.equals("MLD")) {
                        g2.setColor(new Color(160, 80, 160));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[MLD] MLD", x + 8, y + 24);
                    } else if (tag.equals("DVMRP")) {
                        g2.setColor(new Color(140, 60, 140));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[DVMRP] DVMRP", x + 2, y + 24);
                    } else if (tag.equals("NETFLOW")) {
                        g2.setColor(new Color(80, 180, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[NF] NetFlow", x + 4, y + 24);
                    } else if (tag.equals("SFLOW")) {
                        g2.setColor(new Color(60, 160, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[SF] sFlow", x + 8, y + 24);
                    } else if (tag.equals("IPFIX")) {
                        g2.setColor(new Color(40, 140, 160));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[IPFIX] IPFIX", x + 2, y + 24);
                    } else if (tag.equals("ICMP_PING")) {
                        g2.setColor(new Color(80, 200, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[PING] PING", x + 6, y + 24);
                    } else if (tag.equals("ICMP_TRACE")) {
                        g2.setColor(new Color(60, 180, 60));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[TR] Traceroute", x + 2, y + 24);
                    } else if (tag.equals("X509")) {
                        g2.setColor(new Color(180, 180, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[X509] X.509", x + 4, y + 24);
                    } else if (tag.equals("CRL")) {
                        g2.setColor(new Color(160, 160, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[CRL] CRL", x + 8, y + 24);
                    } else if (tag.equals("OCSP")) {
                        g2.setColor(new Color(140, 140, 60));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[OCSP] OCSP", x + 4, y + 24);
                    } else if (tag.equals("PKI")) {
                        g2.setColor(new Color(120, 120, 40));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[PKI] PKI", x + 8, y + 24);
                    } else if (tag.equals("DTLS")) {
                        g2.setColor(new Color(100, 100, 20));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[DTLS] DTLS", x + 4, y + 24);
                    } else if (tag.equals("IPS")) {
                        g2.setColor(new Color(200, 80, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[IPS] IPS", x + 8, y + 24);
                    } else if (tag.equals("MAC_AUTH")) {
                        g2.setColor(new Color(180, 100, 120));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[MAC] MAC认证", x + 2, y + 24);
                    } else if (tag.equals("DOT1X")) {
                        g2.setColor(new Color(160, 120, 140));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[1X] 802.1X", x + 4, y + 24);
                    } else if (tag.equals("VLAN_TAG")) {
                        g2.setColor(new Color(255, 140, 0));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[VLAN] VLAN", x + 6, y + 24);
                    } else if (tag.equals("TUNNEL_GRE")) {
                        g2.setColor(new Color(100, 200, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[GRE] GRE", x + 8, y + 24);
                    } else if (tag.equals("IGMP_MCAST")) {
                        g2.setColor(new Color(0, 200, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[IGMP] IGMP", x + 4, y + 24);
                    } else if (tag.equals("NDP_DISC")) {
                        g2.setColor(new Color(0, 150, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[NDP] NDP", x + 8, y + 24);
                    } else if (tag.equals("DNS_RECURSIVE")) {
                        g2.setColor(new Color(0, 100, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[RDNS] 递归DNS", x + 2, y + 24);
                    }
                    // ===================== 新增 20 个核心工厂渲染 =====================
                    else if (tag.equals("SOCKET")) {
                        g2.setColor(new Color(0, 180, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[SKT] Socket", x + 2, y + 24);
                    }
                    else if (tag.equals("TCP_STATE")) {
                        g2.setColor(new Color(100, 100, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[FSM] TCP状态", x + 2, y + 24);
                    }
                    else if (tag.equals("MAC_TABLE")) {
                        g2.setColor(new Color(100, 180, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[MAC] MAC表", x + 4, y + 24);
                    }
                    else if (tag.equals("CAM_TABLE")) {
                        g2.setColor(new Color(80, 200, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[CAM] CAM表", x + 4, y + 24);
                    }
                    else if (tag.equals("FIB")) {
                        g2.setColor(new Color(200, 180, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[FIB] 转发表", x + 4, y + 24);
                    }
                    else if (tag.equals("SESSION_TABLE")) {
                        g2.setColor(new Color(150, 100, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[SES] 会话表", x + 4, y + 24);
                    }
                    else if (tag.equals("FLOW")) {
                        g2.setColor(new Color(80, 200, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[FLOW] NetFlow", x + 2, y + 24);
                    }
                    else if (tag.equals("LOAD_BALANCER")) {
                        g2.setColor(new Color(200, 100, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[LB] 负载均衡", x + 2, y + 24);
                    }
                    else if (tag.equals("SCHEDULER")) {
                        g2.setColor(new Color(100, 150, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[QoS] 调度器", x + 4, y + 24);
                    }
                    else if (tag.equals("DNS_ZONE")) {
                        g2.setColor(new Color(0, 180, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[ZONE] DNS区域", x + 2, y + 24);
                    }
                    else if (tag.equals("DHCP_LEASE")) {
                        g2.setColor(new Color(100, 180, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[LEASE] DHCP租约", x + 2, y + 24);
                    }
                    else if (tag.equals("ARP_TABLE")) {
                        g2.setColor(new Color(80, 160, 200));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[ARP] ARP表", x + 6, y + 24);
                    }
                    else if (tag.equals("NEIGHBOR_TABLE")) {
                        g2.setColor(new Color(60, 140, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[ND] 邻居表", x + 6, y + 24);
                    }
                    else if (tag.equals("MCAST_ROUTE")) {
                        g2.setColor(new Color(180, 80, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[MCAST] 组播路由", x + 2, y + 24);
                    }
                    else if (tag.equals("MPLS_LABEL")) {
                        g2.setColor(new Color(200, 200, 80));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[MPLS] 标签栈", x + 4, y + 24);
                    }
                    else if (tag.equals("CERT_STORE")) {
                        g2.setColor(new Color(180, 180, 100));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[CERT] 证书库", x + 4, y + 24);
                    }
                    else if (tag.equals("EVENT")) {
                        g2.setColor(new Color(200, 100, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[EVT] 事件引擎", x + 2, y + 24);
                    }
                    else if (tag.equals("STATS")) {
                        g2.setColor(new Color(80, 180, 140));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[STAT] 统计", x + 8, y + 24);
                    }
                    else if (tag.equals("LOG")) {
                        g2.setColor(new Color(150, 150, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.BLACK);
                        g2.drawString("[LOG] 日志", x + 8, y + 24);
                    }
                    else if (tag.equals("PCAP")) {
                        g2.setColor(new Color(80, 100, 180));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[PCAP] 抓包", x + 6, y + 24);
                    } else if (tag.equals("IPSEC_AH")) {
                        g2.setColor(new Color(110, 90, 150));
                        g2.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                        g2.setColor(Color.WHITE);
                        g2.drawString("[AH] AH", x + 8, y + 24);
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