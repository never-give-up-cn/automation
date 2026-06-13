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
import com.never_give_up.automation.demo.model.*;
import java.util.Random;

    @Data
    public class DataCart {

    private final GameContext context;
    private transient FactoryManager factoryManager;
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
        public DataCart(double sx, double sy, String type, int seq, GameContext context) {
            this.context = context;
            this.factoryManager = context.getFactoryManager();

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
                context.getAppendToConsole().accept("【📁 FTP】: 创建 FTP_USER 包, stage=161, dstPort=21");
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
            return srcIp != null ? srcIp : (context.getPcIpAddress() != null ? context.getPcIpAddress() : "192.168.1.100");
        }

        private String getDstIp() {
            return dstIp != null ? dstIp : (context.getResolvedServerIp() != null ? context.getResolvedServerIp() : "10.0.0.1");
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
                Point target = context.getFindBuildingCoords().apply("RX_ST");
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
                target = context.getPcFactory();
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
                                context.getAppendToConsole().accept("【📁 FTP】: stage " + (stage - 1) + " → " + stage);
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
                if (target == null) target = isReturnTrip ? context.getPcFactory() : context.getFindBuildingCoords().apply("DHCP_SERVER");
            } else {
                target = isReturnTrip ? context.getPcFactory() : findTargetMachine(stage, cartType);
            }

            if (target == null) {
                target = context.getPcFactory();
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
                                DataCart fragment = new DataCart(x, y, "IP_FRAGMENT", 0, context);
                                fragment.identification = context.getIpIdentifierCounter();
                                fragment.fragmentOffset = i * (MTU / 8);
                                fragment.moreFragments = (i < fragCount - 1);
                                fragment.fragmentData = new byte[Math.min(MTU, packetSize - i * MTU)];
                                fragment.stage = 16; // 从 IP 分片器继续后续封装
                                fragment.ttl = this.ttl;
                                context.getPendingDataCarts().add(fragment);
                            }
                            context.setIpIdentifierCounter(context.getIpIdentifierCounter() + 1);
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
                            context.getAppendToConsole().accept("【📁 FTP】: stage " + (stage - 1) + " → " + stage);
                            return;
                        } else if (stage == 165) {
                            stage = 5;
                            context.getAppendToConsole().accept("【📁 FTP】: FTP 完成，进入应用层 stage=5");
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

            String insideIp = context.getPcIpAddress() != null ? context.getPcIpAddress() : "192.168.1.100";
            int insidePort = srcPort > 0 ? srcPort : 1234;

            String key = insideIp + ":" + insidePort;

            // 使用 factoryManager 的 natFactory
            if (!context.getNatTable().containsKey(key)) {
                NatMappingFactory.NatEntry factoryEntry = factoryManager.getNatFactory().createMapping(insideIp, insidePort);
                NatEntry localEntry = new NatEntry(factoryEntry.getInsideIp(), factoryEntry.getInsidePort(),
                        factoryEntry.getPublicIp(), factoryEntry.getPublicPort());
                context.getNatTable().put(key, localEntry);
            }

            NatEntry entry = context.getNatTable().get(key);
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
                if (s == 13) return context.getFindBuildingCoords().apply("T_CORE");
                if (s == 14) return context.getFindBuildingCoords().apply("TX_IPH");
                if (s == 15) return context.getFindBuildingCoords().apply("TX_ARP");
                if (s == 16) return context.getFindBuildingCoords().apply("ETH_DST");
                if (s == 17) return context.getFindBuildingCoords().apply("ETH_SRC");
                if (s == 18) return context.getFindBuildingCoords().apply("ETH_TYPE");
                if (s == 19) return context.getFindBuildingCoords().apply("TX_FCS");
                if (s == 20) return context.getFindBuildingCoords().apply("R_LAN");
                if (s == 21) return context.getFindBuildingCoords().apply("R_TAB");
                if (s == 22) return context.getFindBuildingCoords().apply("R_NAT");
                if (s == 23) return context.getFindBuildingCoords().apply("R_WAN");
                if (s == 24) return context.getFindBuildingCoords().apply("ROUTER1");
                if (s == 25) return context.getFindBuildingCoords().apply("ROUTER2");
                if (s == 26) return context.getFindBuildingCoords().apply("ROUTER3");
                if (s == 27) return context.getFindBuildingCoords().apply("RX_ETH");
                if (s == 28) return context.getFindBuildingCoords().apply("RX_IP");
                if (s == 29) return context.getFindBuildingCoords().apply("RX_TCP");
                if (s == 30) return context.getFindBuildingCoords().apply("RX_APP");
            }
            // 为 ACK_PC 添加快速路由
            if (type.equals("ACK_PC")) {
                if (s == 2) {
                    return context.getFindBuildingCoords().apply("T_CORE");
                } else if (s >= 3) {
                    return context.getFindBuildingCoords().apply("RX_ST");
                }
                return null;
            }

            // DNS 递归查询专用路由
            if (type.equals("DNS_QUERY")) {
                switch (s) {
                    case 1:
                        return context.getFindBuildingCoords().apply("DNS_CLIENT");
                    case 2:
                        return context.getFindBuildingCoords().apply("DNS_LOCAL");
                    case 3:
                        return context.getFindBuildingCoords().apply("DNS_ROOT");
                    case 4:
                        return context.getFindBuildingCoords().apply("DNS_AUTH");
                    default:
                        return null;
                }
            }
            if (type.equals("DNS_RESPONSE")) {
                // 响应直接返回 PC
                return context.getFindBuildingCoords().apply("PC_FACTORY");
            }
            if (type.equals("DNS_RECURSION_ROOT") || type.equals("DNS_RECURSION_AUTH")) {
                // 递归查询转发
                return context.getFindBuildingCoords().apply("DNS_ROOT");
            }
            if (type.equals("DNS_ROOT_TO_LOCAL") || type.equals("DNS_AUTH_TO_LOCAL")) {
                return context.getFindBuildingCoords().apply("DNS_LOCAL");
            }
            if (type.equals("DNS_LOCAL_TO_AUTH")) {
                return context.getFindBuildingCoords().apply("DNS_AUTH");
            }
            if (type.equals("DNS_RECURSION_ROOT_RESP") || type.equals("DNS_RECURSION_AUTH_RESP")) {
                return context.getFindBuildingCoords().apply("DNS_LOCAL");
            }


            // TLS 和 HTTP 演示专用路由
            if (type.equals("TLS_CLIENT_HELLO") || type.equals("HTTP_GET") ||
                    type.equals("TLS_CLIENT_KEY_EXCHANGE") || type.equals("TLS_CLIENT_FINISHED")) {
                if (s == 5) return context.getFindBuildingCoords().apply("TX_APP");
                return null;
            }
            if (type.equals("TLS_SERVER_HELLO_CERT") || type.equals("HTTP_200_OK") ||
                    type.equals("TLS_SERVER_FINISHED")) {
                if (s == -1) return context.getFindBuildingCoords().apply("PC_FACTORY");
                return null;
            }

            // DHCP 专用路由
            if (type.equals("DHCP_DISCOVER")) {
                switch (s) {
                    case 1:
                        return context.getFindBuildingCoords().apply("DHCP_DISC");
                    case 2:
                        return context.getFindBuildingCoords().apply("DHCP_SERVER");
                }
            } else if (type.equals("DHCP_OFFER")) {
                switch (s) {
                    case 1:
                        return context.getFindBuildingCoords().apply("DHCP_OFFER");
                    case 2:
                        return context.getFindBuildingCoords().apply("PC_FACTORY");
                }
            } else if (type.equals("DHCP_REQUEST")) {
                switch (s) {
                    case 1:
                        return context.getFindBuildingCoords().apply("DHCP_REQ");
                    case 2:
                        return context.getFindBuildingCoords().apply("DHCP_SERVER");
                }
            } else if (type.equals("DHCP_ACK")) {
                switch (s) {
                    case 1:
                        return context.getFindBuildingCoords().apply("DHCP_ACK");
                    case 2:
                        return context.getFindBuildingCoords().apply("PC_FACTORY");
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
            return context.getFindBuildingCoords().apply(tag);
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
                                context.getAppendToConsole().accept("【📁 FTP】: USER " + username);
                            } else if (ftpCommand.startsWith("PASS")) {
                                ftpData = ftpFactory.buildPassCommand(ftpCommand.substring(4).trim());
                                context.getAppendToConsole().accept("【📁 FTP】: PASS ****");
                            } else if (ftpCommand.equals("PASV")) {
                                ftpData = ftpFactory.buildPasvCommand();
                                context.getAppendToConsole().accept("【📁 FTP】: PASV");
                            } else if (ftpCommand.equals("LIST")) {
                                ftpData = ftpFactory.buildListCommand();
                                context.getAppendToConsole().accept("【📁 FTP】: LIST");
                            } else if (ftpCommand.startsWith("RETR")) {
                                ftpData = ftpFactory.buildRetrCommand(ftpCommand.substring(4).trim());
                                context.getAppendToConsole().accept("【📁 FTP】: RETR");
                            } else if (ftpCommand.startsWith("STOR")) {
                                ftpData = ftpFactory.buildStorCommand(ftpCommand.substring(4).trim());
                                context.getAppendToConsole().accept("【📁 FTP】: STOR");
                            } else if (ftpCommand.equals("QUIT")) {
                                ftpData = ftpFactory.buildQuitCommand();
                                context.getAppendToConsole().accept("【📁 FTP】: QUIT");
                            } else if (ftpCommand.equals("TYPE I")) {
                                ftpData = ftpFactory.buildTypeBinaryCommand();
                                context.getAppendToConsole().accept("【📁 FTP】: TYPE I");
                            } else if (ftpCommand.equals("TYPE A")) {
                                ftpData = ftpFactory.buildTypeAsciiCommand();
                                context.getAppendToConsole().accept("【📁 FTP】: TYPE A");
                            } else {
                                ftpData = ftpFactory.buildFtpCommand(ftpCommand);
                                context.getAppendToConsole().accept("【📁 FTP】: " + ftpCommand);
                            }

                            if (ftpData != null) {
                                this.ftpPayload = ftpData;
                            }
                        }
                        // HTTP 请求
                        else if (cartType.equals("HTTP_GET")) {
                            httpBody = factoryManager.getHttpFactory().createGetRequest("/index.html").getBody();
                            context.getAppendToConsole().accept("【📦 HTTP】: GET /index.html");
                        }
                        // TLS Client Hello
                        else if (cartType.equals("TLS_CLIENT_HELLO")) {
                            httpBody = factoryManager.getTlsFactory().createClientHello(new byte[32]).getTlsMessageType();
                            context.getAppendToConsole().accept("【🔒 TLS】: Client Hello");
                        }
                        // UDP 数据
                        else if (cartType.equals("UDP_DATA")) {
                            context.getAppendToConsole().accept("【📦 UDP】: 数据载荷");
                        }
                    }
                    break;

                // ========== 传输层封装 ==========
                case 6: // 源端口
                    if (!c_SP) {
                        c_SP = true;
                        srcPort = factoryManager.getPortFactory().allocateEphemeralPort();
                        context.getAppendToConsole().accept("【🔩 源端口】: 分配端口 " + srcPort);
                    }
                    break;
                case 7: // 目的端口
                    if (!c_DP) {
                        c_DP = true;
                        // 如果已经是 FTP 端口，不要覆盖
                        if (dstPort != 21) {
                            dstPort = 443;
                            factoryManager.getPortFactory().reservePort(dstPort);
                        }
                        context.getAppendToConsole().accept("【🎯 目的端口】: 目标端口 " + dstPort);
                    }
                    break;
                case 8: // SEQ
                    if (!c_SEQ) {
                        c_SEQ = true;
                        // 如果已经是 DATA 包且有 sequenceNumber，不要覆盖
                        if (cartType.equals("DATA") && sequenceNumber > 0) {
                            context.getAppendToConsole().accept("【🔢 序列号】: SEQ=" + sequenceNumber + " (保留)");
                        } else {
                            sequenceNumber = factoryManager.getTcpPacketFactory().getNextSeq();
                            context.getAppendToConsole().accept("【🔢 序列号】: SEQ=" + sequenceNumber);
                        }
                    }
                    break;
                case 9: // ACK
                    if (!c_ACK) {
                        c_ACK = true;
                        context.getAppendToConsole().accept("【📜 确认号】: ACK=" + ackNumber);
                    }
                    break;
                case 10: // CTL
                    if (!c_CTL) {
                        c_CTL = true;
                        context.getAppendToConsole().accept("【🚩 控制位】: " + cartType);
                    }
                    break;
                case 11: // WIN
                    if (!c_WIN) {
                        c_WIN = true;
                        factoryManager.getTcpPacketFactory().setWindowSize(advertisedWindow);
                        context.getAppendToConsole().accept("【🌊 滑动窗口】: win=" + advertisedWindow);
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
                        context.getAppendToConsole().accept("【🔥 校验和】: 0x" + Integer.toHexString(checksum));
                    }
                    break;
                case 13: // TCP 段完成
                    if (!hasTcp) {
                        hasTcp = true;
                        factoryManager.getTcpPacketFactory().produce();
                        context.getAppendToConsole().accept("【🟧 TCP 段】: 传输层封装完成");
                    }
                    break;

                // ========== 网络层封装 ==========
                case 14: // IP 首部
                    if (!hasIp) {
                        hasIp = true;
                        srcIp = context.getPcIpAddress() != null ? context.getPcIpAddress() : "192.168.1.100";
                        dstIp = context.getResolvedServerIp() != null ? context.getResolvedServerIp() : "10.0.0.1";
                        factoryManager.getIpPacketFactory().createTcpPacket(srcIp, dstIp, new byte[0]);
                        context.getAppendToConsole().accept("【📦 IP 首部】: " + srcIp + " → " + dstIp + ", TTL=" + ttl);
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
                            context.getAppendToConsole().accept("【✂️ IP 分片】: 分包 " + ((packetSize + MTU - 1) / MTU) + " 片");
                        }
                    }
                    break;
                case 16: // ARP 解析
                    if (!hasArp && context.getResolvedServerIp() != null && context.getPcIpAddress() != null) {
                        hasArp = true;
                        String mac = factoryManager.getArpCache().getMac(context.getResolvedServerIp());
                        if (mac == null) {
                            context.getAppendToConsole().accept("【🔍 ARP 请求】: 谁拥有 " + context.getResolvedServerIp() + "?");
                            String newMac = String.format("00:1A:2B:%02X:%02X:%02X",
                                    new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
                            factoryManager.getArpCache().addEntry(context.getResolvedServerIp(), newMac);
                            context.getAppendToConsole().accept("【📥 ARP 响应】: " + context.getResolvedServerIp() + " → " + newMac);
                            context.getUpdateArpDisplay().run();
                        } else {
                            context.getAppendToConsole().accept("【✅ ARP 缓存】: " + context.getResolvedServerIp() + " → " + mac);
                        }
                    }
                    break;

                case 17: // Ethernet DST MAC
                    if (!hasEther && context.getPcIpAddress() != null && context.getResolvedServerIp() != null) {
                        dstMac = factoryManager.getArpCache().getMac(context.getResolvedServerIp());
                        if (dstMac == null || dstMac.isEmpty()) {
                            dstMac = "00:1A:2B:3C:4D:60";  // 默认网关 MAC
                        }
                        context.getAppendToConsole().accept("【🟦 目的 MAC】: " + dstMac);
                    }
                    break;
                case 18: // Ethernet SRC MAC
                    if (!hasEther && context.getPcIpAddress() != null) {
                        srcMac = factoryManager.getArpCache().getMac(context.getPcIpAddress());
                        if (srcMac == null || srcMac.isEmpty()) {
                            srcMac = "00:1A:2B:3C:4D:5F";  // 默认 PC MAC
                        }
                        context.getAppendToConsole().accept("【🟦 源 MAC】: " + srcMac);
                    }
                    break;
                case 19: // EtherType
                    if (!hasEther) {
                        hasEther = true;
                        factoryManager.getEthernetFactory().createIpFrame(srcMac, dstMac);
                        context.getAppendToConsole().accept("【🟦 EtherType】: 0x0800 (IPv4)");
                    }
                    break;
                case 20: // LLC（可选）
                    if (!hasLlc) {
                        hasLlc = true;
                        factoryManager.getLinkLayerFactory().setLlcHeader();
                        LlcHeader llc = factoryManager.getLinkLayerFactory().getLlcHeader();
                        context.getAppendToConsole().accept("【🔗 LLC】: " + llc.toString());
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
                        context.getAppendToConsole().accept(String.format("【🔒 FCS 计算】: CRC32 = %02X%02X%02X%02X",
                                fcs[0] & 0xFF, fcs[1] & 0xFF, fcs[2] & 0xFF, fcs[3] & 0xFF));
                        context.getAppendToConsole().accept(String.format("【📦 完整帧】: %d 字节", completeFrame.length));
                    }
                    break;

                // ========== 五元组和会话（新增加） ==========
                case 22: // 五元组
                    if (!hasFiveTuple) {
                        hasFiveTuple = true;
                        protocol = context.isUseUdp() ? "UDP" : "TCP";
                        // 确保 IP 不为 null
                        String fiveSrcIp = srcIp != null ? srcIp : (context.getPcIpAddress() != null ? context.getPcIpAddress() : "192.168.1.100");
                        String fiveDstIp = dstIp != null ? dstIp : (context.getResolvedServerIp() != null ? context.getResolvedServerIp() : "10.0.0.1");
                        factoryManager.getFiveTupleFactory().extract(fiveSrcIp, fiveDstIp, srcPort, dstPort, protocol);
                        context.getAppendToConsole().accept(String.format("【🔢 五元组】: %s %s:%d → %s:%d",
                                protocol, fiveSrcIp, srcPort, fiveDstIp, dstPort));
                    }
                    break;

                case 23: // 会话管理
                    if (!hasSession) {
                        hasSession = true;
                        // 使用 factoryManager 的 sessionFactory
                        factoryManager.getSessionFactory().createSession(srcIp, dstIp, srcPort, dstPort);
                        context.getAppendToConsole().accept("【💬 会话】: 创建会话 " + srcIp + ":" + srcPort);
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
                                context.getAppendToConsole().accept(info);
                            } else {
                                this.setFcsVerified(false);
                                context.getAppendToConsole().accept("【⚠️ FCS 校验失败】: 帧损坏");
                                this.isDropped = true;
                                return;
                            }
                        }
                        factoryManager.getLinkLayerFactory().resetLinkLayer();
                        context.getAppendToConsole().accept("【🎛️ LAN 拆包】: 链路层解封完成");
                    }
                    break;
                case 25: // 路由查表
                    factoryManager.getRouteTable().lookup(context.getResolvedServerIp());
                    context.getAppendToConsole().accept("【🔀 路由查表】: 目标 " + context.getResolvedServerIp() + " 下一跳 8.8.8.8");
                    break;
                case 26: // NAT 转换
                    if (!isNatted && !isReturnTrip) {
                        applyNatMapping();
                        addVisualFeedback(String.format("🌍 NAT: %s:%d → %s:%d",
                                        srcIp, srcPort, natPublicIp, natPublicPort),
                                new Color(255, 165, 0));
                        context.getAppendToConsole().accept("【🌍 NAT 转换】: " + srcIp + ":" + srcPort + " → " + natPublicIp + ":" + natPublicPort);
                        context.getUpdateNatDisplay().run();
                    }
                    break;
                // 修改 case 27 中的带宽控制调用
                case 27: // 带宽控制
                    // 使用 factoryManager 的 bandwidthFactory
                    if (factoryManager.getBandwidthFactory() != null &&
                            factoryManager.getBandwidthFactory().shouldDropPacket()) {
                        context.getAppendToConsole().accept("【💥 带宽限制】: 公网丢包，数据包被丢弃");
                        this.isDropped = true;
                        return;
                    }
                    context.getAppendToConsole().accept("【📊 带宽控制】: 通过");
                    break;
                case 28: // WAN 封装
                    context.getAppendToConsole().accept("【🛠️ WAN 封装】: 进入公网传输");
                    break;

                // ========== 防火墙 ==========
                case 29: // 出站防火墙
                    if (srcIp != null && dstIp != null) {
                        // 使用 factoryManager 的 firewallFactory
                        boolean allowed = factoryManager.getFirewallFactory()
                                .allowOutbound(srcIp, dstIp, srcPort, dstPort, protocol);
                        if (!allowed) {
                            context.getAppendToConsole().accept("【🔥 防火墙】: 出站包被阻断 " + srcIp + " → " + dstIp);
                            this.isDropped = true;
                            return;
                        } else {
                            context.getAppendToConsole().accept("【🔥 防火墙】: 出站规则通过 ✅");
                        }
                    }
                    break;
                case 30: // 入站防火墙
                    if (isReturnTrip && dstIp != null && srcIp != null) {
                        if (!factoryManager.getFirewallFactory().allowInbound(srcIp, dstIp, srcPort, dstPort, Integer.parseInt(protocol))) {
                            context.getAppendToConsole().accept("【🔥 防火墙】: 入站包被阻断 " + srcIp + " → " + dstIp);
                            this.isDropped = true;
                            return;
                        }
                        context.getAppendToConsole().accept("【🔥 防火墙】: 入站规则通过");
                    }
                    break;

                // ========== 公网路由器 ==========
                case 31: // ROUTER1
                    ttl--;
                    context.getAppendToConsole().accept("【📡 ROUTER1】: TTL=" + ttl);
                    if (ttl <= 0) {
                        context.getAppendToConsole().accept("【⚠️ TTL 超时】: 数据包被丢弃");
                        this.isDropped = true;
                        return;
                    }
                    break;
                case 32: // ROUTER2
                    ttl--;
                    context.getAppendToConsole().accept("【📡 ROUTER2】: TTL=" + ttl);
                    if (ttl <= 0) {
                        this.isDropped = true;
                        return;
                    }
                    break;
                case 33: // ROUTER3
                    ttl--;
                    context.getAppendToConsole().accept("【📡 ROUTER3】: TTL=" + ttl);
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
                        context.getAppendToConsole().accept("【📋 入队】: 数据包进入队列");
                    }
                    break;

                case 35: // 队列出队
                    if (hasQueue && factoryManager.getQueueFactory() != null) {
                        factoryManager.getQueueFactory().dequeue();
                        context.getAppendToConsole().accept("【📋 出队】: 数据包离开队列");
                        hasQueue = false;
                    }
                    break;

                case 36: // 拥塞控制
                    if (cartType.equals("DATA") && !context.isUseUdp()) {
                        // 使用 factoryManager 的 congestionControl
                        factoryManager.getCongestionControl().congestionAvoidance(context.getCwnd());
                        context.getAppendToConsole().accept("【🐌 拥塞控制】: 调整窗口");
                    }
                    break;

                // ========== 接收端链路层解封 ==========
                case 37: // RX_ETH - 以太网解封
                    hasEther = false;
                    context.getAppendToConsole().accept("【📡 RX_ETH】: 移除以太网头部");
                    break;
                case 38: // RX_LLC - LLC 解封
                    hasLlc = false;
                    context.getAppendToConsole().accept("【📡 RX_LLC】: 移除 LLC 头部");
                    break;
                case 39: // RX_FCS - FCS 校验
                    if (!fcsVerified && this.getEthernetFrameData() != null) {
                        boolean valid = factoryManager.getLinkLayerFactory()
                                .verifyFcs(this.getEthernetFrameData());
                        if (!valid) {
                            context.getAppendToConsole().accept("【⚠️ RX_FCS】: FCS 校验失败，帧损坏");
                            this.isDropped = true;
                            return;
                        }
                        this.setFcsVerified(true);
                    }
                    context.getAppendToConsole().accept("【✓ RX_FCS】: 校验通过");
                    break;
                case 40: // RX_ARP - ARP 解析完成
                    context.getAppendToConsole().accept("【📡 RX_ARP】: ARP 解析完成");
                    break;

                // ========== 接收端网络层解封 ==========
                case 41: // RX_FRAG - IP 分片重组
                    if (isFragmented) {
                        context.getAppendToConsole().accept("【🧩 RX_FRAG】: IP 分片重组中");
                        // 这里可以添加实际的重组逻辑
                    }
                    break;
                case 42: // RX_IP - IP 层解封
                    hasIp = false;
                    if (cartType.equals("DATA") || cartType.equals("HTTP_200_OK")) {
                        IpPacket ipPacket = factoryManager.getIpPacketFactory().produce();
                        // 🔥 修复：不要传空数组，传一个标准IP头长度的字节数组
                        ipPacket.deserialize(new byte[ProtocolConst.IP_HEADER_SIZE]);
                        context.getAppendToConsole().accept("【💛 RX_IP】: 网络层解封完成");
                    }
                    break;

                // ========== 接收端传输层解封 ==========
                case 43: // RX_PORT - 端口解封
                    context.getAppendToConsole().accept("【🔌 RX_PORT】: 端口解封完成");
                    break;
                case 44: // RX_TCP - TCP 层解封
                    hasTcp = false;
                    if (cartType.equals("DATA") || cartType.equals("HTTP_200_OK")) {
                        TcpPacket tcpPacket = factoryManager.getTcpPacketFactory().produce();
                        tcpPacket.deserialize(new byte[0]);
                        context.getAppendToConsole().accept("【🧡 RX_TCP】: 传输层解封完成");
                    }
                    break;

                // ========== 接收端应用层交付 ==========
                case 45: // RX_APP - 应用层交付
                    hasApp = false;
                    if (cartType.equals("HTTP_200_OK")) {
                        HttpPacket httpPacket = factoryManager.getHttpFactory()
                                .createResponse(200, context.getHttpResponseContent());
                        context.setHttpResponseContent(httpPacket.getBody());
                        context.getAppendToConsole().accept("【💚 RX_APP】: 应用层交付完成");
                    }
                    break;
                case 46: // TCP 选项处理
                    if (!hasTcpOption && cartType.equals("DATA") && factoryManager.getTcpOptionFactory() != null) {
                        hasTcpOption = true;
                        byte[] mssOpt = factoryManager.getTcpOptionFactory().mss(1460);
                        byte[] wsOpt = factoryManager.getTcpOptionFactory().windowScale(7);
                        byte[] sackOpt = factoryManager.getTcpOptionFactory().sackPerm();
                        byte[] combined = factoryManager.getTcpOptionFactory().combine(mssOpt, wsOpt, sackOpt);
                        context.getAppendToConsole().accept(String.format("【🔧 TCP 选项】: MSS+窗口缩放+SACK 已添加 (%d字节)", combined.length));
                    }
                    break;
                case 47: // IP 选项处理
                    if (!hasIpOption && factoryManager.getIpOptionFactory() != null) {
                        hasIpOption = true;
                        List<Integer> routeIps = new ArrayList<>();
                        routeIps.add(ipToInt("8.8.8.8"));
                        byte[] lsrOpt = factoryManager.getIpOptionFactory().createLooseSourceRouteOption(routeIps);
                        byte[] padded = factoryManager.getIpOptionFactory().padTo4Bytes(lsrOpt);
                        context.getAppendToConsole().accept(String.format("【🔧 IP 选项】: LSR 已添加 (%d字节)", padded.length));
                    }
                    break;
                case 48: // 以太网填充
                    if (!hasEtherPadding && factoryManager.getEthernetPaddingFactory() != null) {
                        hasEtherPadding = true;
                        byte[] testPayload = new byte[30];
                        byte[] padded = factoryManager.getEthernetPaddingFactory().pad(testPayload);
                        context.getAppendToConsole().accept(String.format("【📦 以太网填充】: %d → %d 字节", testPayload.length, padded.length));
                    }
                    break;

                case 49: // UDP 校验和
                    if (context.isUseUdp() && !hasUdpChecksum && factoryManager.getUdpChecksumFactory() != null && cartType.equals("UDP_DATA")) {
                        hasUdpChecksum = true;
                        int srcIpInt = ipToInt(getSrcIp());
                        int dstIpInt = ipToInt(getDstIp());
                        byte[] udpData = new byte[100];
                        int checksum = factoryManager.getUdpChecksumFactory().calculate(srcIpInt, dstIpInt, udpData.length, udpData);
                        context.getAppendToConsole().accept(String.format("【🔢 UDP 校验和】: 0x%04X", checksum));
                    }
                    break;

                case 50: // ICMP 错误生成
                    if (factoryManager.getIcmpErrorFactory() != null && ttl <= 1 && cartType.equals("DATA")) {
                        byte[] originalIp = new byte[20];
                        byte[] timeExceeded = factoryManager.getIcmpErrorFactory().timeExceeded(originalIp);
                        context.getAppendToConsole().accept("【⚠️ ICMP 错误】: Time Exceeded 已生成");
                    }
                    break;

                case 51: // IP 路由转发
                    if (factoryManager.getIpForwardFactory() != null && stage >= 51 && stage <= 58) {
                        byte[] forwarded = factoryManager.getIpForwardFactory().forward(new byte[20], "eth0", ipToInt(getDstIp()));
                        context.getAppendToConsole().accept("【🔄 IP 转发】: TTL 递减，校验和更新");
                    }
                    break;

                case 52: // TCP 窗口管理
                    if (!context.isUseUdp() && factoryManager.getTcpWindowFactory() != null && cartType.equals("DATA")) {
                        long effectiveWin = factoryManager.getTcpWindowFactory().effectiveWindow();
                        context.getAppendToConsole().accept(String.format("【📊 TCP 窗口】: 有效窗口=%d", effectiveWin));
                    }
                    break;

                case 53: // TCP 定时器
                    if (!context.isUseUdp() && factoryManager.getTcpTimerFactory() != null && !isReturnTrip) {
                        long rto = factoryManager.getTcpTimerFactory().getRto();
                        context.getAppendToConsole().accept(String.format("【⏱️ TCP 定时器】: RTO=%dms", rto));
                    }
                    break;

                case 54: // VLAN 标签
                    if (!hasVlan && factoryManager.getVlanFactory() != null && isReturnTrip) {
                        hasVlan = true;
                        vlanId = 100;
                        byte[] taggedFrame = factoryManager.getVlanFactory().addVlan(new byte[64], vlanId);
                        context.getAppendToConsole().accept(String.format("【🏷️ VLAN 802.1Q】: 添加 VLAN ID=%d", vlanId));
                    }
                    break;

                case 55: // GRE 隧道
                    if (!hasTunnel && factoryManager.getTunnelFactory() != null && cartType.equals("DATA")) {
                        hasTunnel = true;
                        isEncapsulated = true;
                        byte[] innerPacket = new byte[100];
                        byte[] grePacket = factoryManager.getTunnelFactory().greEncapsulate(innerPacket);
                        context.getAppendToConsole().accept(String.format("【🔄 GRE 隧道】: 封装完成 (%d字节)", grePacket.length));
                    }
                    break;

                case 56: // IGMP 组播
                    if (!hasIgmp && factoryManager.getIgmpFactory() != null && cartType.equals("IGMP_JOIN")) {
                        hasIgmp = true;
                        int groupIp = ipToInt("224.0.0.1");
                        byte[] joinMsg = factoryManager.getIgmpFactory().joinGroup(groupIp);
                        context.getAppendToConsole().accept("【📡 IGMP】: 加入组播组 224.0.0.1");
                    }
                    break;

                case 57: // NDP 发现
                    if (!hasNdp && factoryManager.getNdpFactory() != null && context.getResolvedServerIp() != null && context.getResolvedServerIp().contains(":")) {
                        hasNdp = true;
                        byte[] ns = factoryManager.getNdpFactory().neighborSolicitation(context.getResolvedServerIp().getBytes());
                        context.getAppendToConsole().accept("【📡 NDP】: 发送邻居请求 (IPv6)");
                    }
                    break;

                case 58: // DNS 递归
                    if (factoryManager.getDnsRecursiveFactory() != null && cartType.equals("DNS_QUERY")) {
                        String resolved = factoryManager.getDnsRecursiveFactory().resolve(domain);
                        context.getAppendToConsole().accept(String.format("【🌐 DNS 递归】: %s → %s", domain, resolved));
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
                            context.getAppendToConsole().accept(String.format("【📡 DHCP 完整报文】: %s (%d字节)", cartType, dhcpPacket.length));
                        }
                    }
                    break;

                case 60: // TLS 握手完成
                    if (factoryManager.getTlsHandshakeFactory() != null && context.isTlsEnabled() && context.setTlsState(= TlsState.FINISHED) {
                        byte[] finishedMsg = factoryManager.getTlsHandshakeFactory().finished());
                        context.getAppendToConsole().accept(String.format("【🔒 TLS 握手】: Finished 消息 (%d字节)", finishedMsg.length));
                    }
                    break;

                case 61: // 序列化
                    if (factoryManager.getPacketSerializerFactory() != null && context.getServerReceivedCount() >= context.getTotalDataToTransmit()) {
                        byte[] serialized = factoryManager.getPacketSerializerFactory().serialize(this);
                        context.getAppendToConsole().accept(String.format("【💾 序列化】: 数据包已序列化 (%d字节)", serialized.length));
                    }
                    break;
                // ===================== 新增 14 个工厂处理 stage 62-75 =====================

                case 62: // 物理层 - 比特流生成
                    if (!hasBitStream && bitStreamFactory != null) {
                        hasBitStream = true;
                        byte[] bitStream = bitStreamFactory.toBitStream(ethernetFrameData != null ? ethernetFrameData : new byte[64]);
                        context.getAppendToConsole().accept(String.format("【📡 比特流】: 生成 %d 字节物理层比特流", bitStream.length));
                    }
                    break;

                case 63: // 物理层 - 信道噪声模拟
                    if (!hasPhysicalEncoding && physicalChannelFactory != null) {
                        hasPhysicalEncoding = true;
                        double ber = physicalChannelFactory.getBER();
                        int jitter = physicalChannelFactory.getJitterBufferSize();
                        long delay = physicalChannelFactory.getPropagationDelayNano(1000);
                        context.getAppendToConsole().accept(String.format("【📡 物理信道】: BER=%.1e, Jitter=%d, 传播时延=%dns", ber, jitter, delay));
                    }
                    break;

                case 64: // PPPoE 封装
                    if (!hasPppoe && pppoeFactory != null && !isReturnTrip) {
                        hasPppoe = true;
                        byte[] padi = pppoeFactory.buildPADI(100);
                        byte[] lcp = pppoeFactory.buildLCPRequest();
                        context.getAppendToConsole().accept(String.format("【🔌 PPPoE】: PADI + LCP 请求 (%d+%d字节)", padi.length, lcp.length));
                    }
                    break;

                case 65: // MACsec 安全加密
                    if (!hasMacSec && macSecFactory != null && isReturnTrip) {
                        hasMacSec = true;
                        long sci = 0x001A2B3C4D5EL;
                        int pn = 1;
                        byte[] secTag = macSecFactory.buildSecTAG(sci, pn);
                        byte[] icv = macSecFactory.buildICV();
                        context.getAppendToConsole().accept(String.format("【🔐 MACsec】: SecTAG + ICV (%d+%d字节)", secTag.length, icv.length));
                    }
                    break;

                case 66: // OSPF 路由协议
                    if (!hasOspf && ospfPacketFactory != null && cartType.equals("OSPF_HELLO")) {
                        hasOspf = true;
                        int routerId = ipToInt(getSrcIp());
                        int areaId = 0;
                        byte[] ospfHello = ospfPacketFactory.buildHello(routerId, areaId);
                        context.getAppendToConsole().accept(String.format("【🌐 OSPF】: Hello 报文 (RouterID=%d)", routerId));
                    }
                    break;

                case 67: // BGP 路由协议
                    if (!hasBgp && bgpPacketFactory != null && cartType.equals("BGP_OPEN")) {
                        hasBgp = true;
                        int myAs = 64512;
                        byte[] bgpOpen = bgpPacketFactory.buildOpen(myAs);
                        byte[] bgpUpdate = bgpPacketFactory.buildUpdate();
                        context.getAppendToConsole().accept(String.format("【🌐 BGP】: OPEN + UPDATE (AS=%d)", myAs));
                    }
                    break;

                case 68: // QoS 流量标记
                    if (!hasQos && qosTrafficFactory != null) {
                        hasQos = true;
                        int dscp = 46;  // EF 优先级
                        byte[] dummyIpPkt = new byte[20];
                        int markedDscp = qosTrafficFactory.setDSCP(dummyIpPkt, dscp);
                        boolean allowed = qosTrafficFactory.tokenBucketAllow(1000000, 10000);
                        context.getAppendToConsole().accept(String.format("【🎯 QoS】: DSCP=%d, 令牌桶允许=%s", markedDscp, allowed));
                    }
                    break;

                case 69: // NAT64 转换 (IPv6 <-> IPv4)
                    if (!hasNat64 && nat64Factory != null && cartType.equals("NAT64")) {
                        hasNat64 = true;
                        byte[] ipv4 = {8, 8, 8, 8};
                        byte[] ipv6 = nat64Factory.convertIpv4ToIpv6(ipv4);
                        context.getAppendToConsole().accept(String.format("【🌍 NAT64】: 8.8.8.8 → 64:FF9B::808:808"));
                    }
                    break;

                case 70: // TCP 重组 (分片重组)
                    if (!isReassembled && tcpReassemblyFactory != null && !isReturnTrip) {
                        isReassembled = true;
                        long seqNum = sequenceNumber;
                        byte[] fakeData = new byte[100];
                        tcpReassemblyFactory.addSegment(seqNum, fakeData);
                        byte[] reassembled = tcpReassemblyFactory.reassemble(seqNum);
                        context.getAppendToConsole().accept(String.format("【🔧 TCP 重组】: SEQ=%d, 重组完成", seqNum));
                    }
                    break;

                case 71: // 传输层攻击模拟 (SYN Flood / Land Attack)
                    if (!isAttackPacket && transportAttackFactory != null && cartType.equals("ATTACK")) {
                        isAttackPacket = true;
                        int fakeSrcIp = ipToInt("1.2.3.4");
                        byte[] synFlood = transportAttackFactory.buildSynFlood(fakeSrcIp);
                        byte[] landAttack = transportAttackFactory.buildLandAttack(ipToInt(getDstIp()));
                        context.getAppendToConsole().accept(String.format("【⚠️ 攻击检测】: SYN Flood + Land Attack 已模拟"));
                    }
                    break;

                case 72: // NTP 时间同步
                    if (!hasNtp && ntpPacketFactory != null && cartType.equals("NTP_REQUEST")) {
                        hasNtp = true;
                        int stratum = 2;
                        byte[] ntpReq = ntpPacketFactory.buildNtpRequest(stratum);
                        context.getAppendToConsole().accept(String.format("【🕐 NTP】: 时间同步请求 (Stratum=%d)", stratum));
                    }
                    break;

                case 73: // SNMP 网络管理
                    if (!hasSnmp && snmpPacketFactory != null && cartType.equals("SNMP_GET")) {
                        hasSnmp = true;
                        String community = "public";
                        byte[] snmpGet = snmpPacketFactory.buildGetRequest(community);
                        context.getAppendToConsole().accept(String.format("【📊 SNMP】: GET 请求 (Community=%s)", community));
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
                            context.getAppendToConsole().accept(String.format("【📡 HTTP/2.3】: HEADERS 帧 (StreamID=%d), GOAWAY(%dB)", streamId, goaway.length));
                        } else {
                            context.getAppendToConsole().accept(String.format("【📡 HTTP/2.3】: HEADERS 帧 (StreamID=%d)", streamId));
                        }
                    }
                    break;

                case 75: // IPsec 安全封装
                    if (!hasIpsec && ipsecFactory != null && !isReturnTrip) {
                        hasIpsec = true;
                        int spi = 0x12345678;
                        byte[] espHeader = ipsecFactory.buildESP(spi);
                        context.getAppendToConsole().accept(String.format("【🔒 IPsec】: ESP 头部 (SPI=0x%08X)", spi));
                    }
                    break;
                // ===================== 新增：IPv6 协议栈处理 stage 76-79 =====================
                case 76: // IPv6 数据包封装
                    if (!hasIpv6 && ipv6PacketFactory != null) {
                        hasIpv6 = true;
                        byte[] ipv6Packet = ipv6PacketFactory.buildIpv6Packet(new byte[64]);
                        context.getAppendToConsole().accept("【🌐 IPv6】: IPv6 数据包封装完成");
                    }
                    break;

                case 77: // IPv6 分片
                    if (!hasIpv6Fragment && ipv6FragmentFactory != null && cartType.equals("DATA")) {
                        hasIpv6Fragment = true;
                        List<byte[]> fragments = ipv6FragmentFactory.fragmentPacket(new byte[1500]);
                        context.getAppendToConsole().accept(String.format("【✂️ IPv6分片】: 分包 %d 片", fragments.size()));
                    }
                    break;

                case 78: // IPv6 扩展选项
                    if (!hasIpv6Option && ipv6OptionFactory != null) {
                        hasIpv6Option = true;
                        ipv6OptionFactory.addOption(0x03, new byte[]{0x01, 0x02});
                        byte[] options = ipv6OptionFactory.buildOptions();
                        context.getAppendToConsole().accept(String.format("【🔧 IPv6选项】: %d 字节", options.length));
                    }
                    break;

                case 79: // IPv6 邻居发现
                    if (!hasIpv6Nd && ipv6NeighborDiscovery != null && context.getResolvedServerIp() != null && context.getResolvedServerIp().contains(":")) {
                        hasIpv6Nd = true;
                        ipv6NeighborDiscovery.addNeighbor(context.getResolvedServerIp(), "00:11:22:33:44:55");
                        context.getAppendToConsole().accept("【📡 IPv6 ND】: 邻居发现完成");
                    }
                    break;

// ===================== 新增：TCP 增强处理 stage 80-83 =====================
                case 80: // TCP Keep-Alive
                    if (!hasKeepAlive && tcpKeepAliveFactory != null && !context.isUseUdp()) {
                        hasKeepAlive = true;
                        byte[] keepAlive = tcpKeepAliveFactory.buildKeepAliveOption();
                        context.getAppendToConsole().accept("【🔁 TCP Keep-Alive】: 保活选项已添加");
                    }
                    break;

                case 81: // TCP SACK
                    if (!hasSack && tcpSackFactory != null && !context.isUseUdp()) {
                        hasSack = true;
                        tcpSackFactory.addBlock(1000, 1200);
                        byte[] sackOpt = tcpSackFactory.buildSackOption();
                        context.getAppendToConsole().accept(String.format("【📊 TCP SACK】: 选择性确认选项 (%d字节)", sackOpt.length));
                    }
                    break;

                case 82: // TCP ECN
                    if (!hasEcn && tcpEcnFactory != null && !context.isUseUdp()) {
                        hasEcn = true;
                        byte[] ecnFlag = tcpEcnFactory.buildEcnFlag();
                        context.getAppendToConsole().accept("【⚠️ TCP ECN】: 显式拥塞通知已启用");
                    }
                    break;

                case 83: // TCP Fast Open
                    if (!hasFastOpen && tcpFastOpenFactory != null && !context.isUseUdp()) {
                        hasFastOpen = true;
                        byte[] tfo = tcpFastOpenFactory.buildTfoOption();
                        context.getAppendToConsole().accept("【🚀 TCP Fast Open】: TFO 选项已添加");
                    }
                    break;

// ===================== 新增：应用层协议处理 stage 84-95 =====================
                case 84: // FTP
                    if (!hasFtp && ftpPacketFactory != null && cartType.equals("FTP")) {
                        hasFtp = true;
//                        byte[] userCmd = ftpPacketFactory.buildFtpCommand("USER anonymous");
//                        byte[] passCmd = ftpPacketFactory.buildFtpCommand("PASS test@example.com");
                        context.getAppendToConsole().accept("【📁 FTP】: USER/PASS 命令已发送");
                    }
                    break;

                case 85: // SMTP
                    if (!hasSmtp && smtpPacketFactory != null && cartType.equals("SMTP")) {
                        hasSmtp = true;
                        byte[] ehlo = smtpPacketFactory.buildEhlo("mail.example.com");
                        byte[] mailFrom = smtpPacketFactory.buildMailFrom("sender@example.com");
                        byte[] rcptTo = smtpPacketFactory.buildRcptTo("receiver@example.com");
                        context.getAppendToConsole().accept("【📧 SMTP】: EHLO/MAIL FROM/RCPT TO 已发送");
                    }
                    break;

                case 86: // POP3
                    if (!hasPop3 && pop3PacketFactory != null && cartType.equals("POP3")) {
                        hasPop3 = true;
                        byte[] user = pop3PacketFactory.buildUserCmd("testuser");
                        byte[] pass = pop3PacketFactory.buildPassCmd("password");
                        byte[] list = pop3PacketFactory.buildListCmd();
                        context.getAppendToConsole().accept("【📬 POP3】: USER/PASS/LIST 命令已发送");
                    }
                    break;

                case 87: // IMAP
                    if (!hasImap && imapPacketFactory != null && cartType.equals("IMAP")) {
                        hasImap = true;
                        byte[] login = imapPacketFactory.buildLogin("testuser", "password");
                        context.getAppendToConsole().accept("【📨 IMAP】: LOGIN 命令已发送");
                    }
                    break;

                case 88: // SSH
                    if (!hasSsh && sshPacketFactory != null && cartType.equals("SSH")) {
                        hasSsh = true;
                        byte[] banner = sshPacketFactory.buildVersionBanner();
                        byte[] kex = sshPacketFactory.buildKexInit();
                        context.getAppendToConsole().accept("【🔐 SSH】: 版本协商 + KEX 初始化");
                    }
                    break;

                case 89: // Telnet
                    if (!hasTelnet && telnetPacketFactory != null && cartType.equals("TELNET")) {
                        hasTelnet = true;
                        byte[] neg = telnetPacketFactory.buildNegotiate();
                        context.getAppendToConsole().accept("【💻 Telnet】: 选项协商完成");
                    }
                    break;

                case 90: // RTP/RTCP
                    if (!hasRtp && rtpPacketFactory != null && cartType.equals("RTP")) {
                        hasRtp = true;
                        byte[] rtpData = rtpPacketFactory.buildRtpPacket("Audio Data".getBytes());
                        byte[] rtcpData = rtcpPacketFactory.buildSenderReport(12345);
                        context.getAppendToConsole().accept(String.format("【🎵 RTP】: RTP包+RTCP报告 (%d+%d字节)", rtpData.length, rtcpData.length));
                    }
                    break;

                case 91: // SIP
                    if (!hasSip && sipPacketFactory != null && cartType.equals("SIP")) {
                        hasSip = true;
                        byte[] invite = sipPacketFactory.buildInvite("sip:user@example.com");
                        context.getAppendToConsole().accept("【📞 SIP】: INVITE 请求已发送");
                    }
                    break;

                case 92: // RADIUS
                    if (!hasRadius && radiusPacketFactory != null && cartType.equals("RADIUS")) {
                        hasRadius = true;
                        byte[] radiusReq = radiusPacketFactory.buildAccessRequest("testuser", "password");
                        context.getAppendToConsole().accept("【🔑 RADIUS】: Access-Request 已发送");
                    }
                    break;

// ===================== 新增：安全防护处理 stage 96-100 =====================
                case 96: // DPI 深度包检测
                    if (!hasDpi && dpiFactory != null) {
                        hasDpi = true;
                        DpiFactory.AppProto proto = dpiFactory.detectProtocol(new byte[64]);
                        context.getAppendToConsole().accept(String.format("【🔍 DPI】: 检测到协议 %s", proto));
                    }
                    break;

                case 97: // WAF 检测
                    if (wafFactory != null && cartType.equals("HTTP_GET")) {
                        if (!wafFactory.checkHttpContent(httpBody != null ? httpBody : "")) {
                            isBlockedByWaf = true;
                            context.getAppendToConsole().accept("【🛡️ WAF】: SQL注入/XSS 攻击被拦截！");
                            this.isDropped = true;
                            return;
                        }
                        context.getAppendToConsole().accept("【🛡️ WAF】: HTTP 内容检查通过");
                    }
                    break;

                case 98: // DDoS 缓解
                    if (ddosMitigationFactory != null && getSrcIp() != null) {
                        if (!ddosMitigationFactory.checkTraffic(getSrcIp())) {
                            context.getAppendToConsole().accept("【💥 DDoS缓解】: 源IP " + getSrcIp() + " 流量超限，已限流");
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
                            context.getAppendToConsole().accept("【⏱️ 速率限制】: " + key + " 超过限制，已丢弃");
                            this.isDropped = true;
                            return;
                        }
                    }
                    break;

                case 100: // ACL 访问控制
                    if (aclFactory != null && getSrcIp() != null && getDstIp() != null) {
                        boolean permit = aclFactory.match(getSrcIp(), getDstIp(), protocol);
                        if (!permit) {
                            context.getAppendToConsole().accept("【🚫 ACL】: " + getSrcIp() + " → " + getDstIp() + " 被拒绝");
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
                        context.getAppendToConsole().accept(String.format("【🔌 Socket】: 创建 Socket %s:%d → %s:%d",
                                getSrcIp(), srcPort, getDstIp(), dstPort));
                        if (statisticsFactory != null) {
                            statisticsFactory.tx(64);
                        }
                    }
                    break;

                case 102: // TCP 状态机
                    if (tcpStateMachineFactory != null && !context.isUseUdp()) {
                        TcpStateMachineFactory.TcpState state = tcpStateMachineFactory.getState();
                        context.getAppendToConsole().accept(String.format("【📊 TCP状态机】: 当前状态 %s", state));
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
                        context.getAppendToConsole().accept(String.format("【🔌 MAC表】: 学习 %s → %s, 出口 %s", srcMac, inPort, outPort));
                    }
                    break;

                case 104: // CAM 表（Cisco 交换机）
                    if (camTableFactory != null && !isReturnTrip) {
                        int vlanId = 1;
                        camTableFactory.add(vlanId, dstMac, "Gi0/" + (stage % 24 + 1));
                        CamTableFactory.CamEntry entry = camTableFactory.get(dstMac);
                        if (entry != null) {
                            context.getAppendToConsole().accept(String.format("【📋 CAM表】: MAC %s → VLAN%d, 端口 %s", dstMac, entry.vlan, entry.port));
                        }
                    }
                    break;

                case 105: // FIB 转发表（CEF 快速转发）
                    if (!hasFibLookup && forwardingEngineFactory != null) {
                        hasFibLookup = true;
                        forwardingEngineFactory.addFibEntry("0.0.0.0/0", "10.0.0.1");
                        String nextHop = forwardingEngineFactory.forward(getDstIp());
                        context.getAppendToConsole().accept(String.format("【🔀 FIB转发】: %s → 下一跳 %s", getDstIp(), nextHop));
                    }
                    break;

                case 106: // 五元组会话表
                    if (sessionTableFactory != null && !isReturnTrip) {
                        String proto = context.isUseUdp() ? "UDP" : "TCP";
                        sessionTableFactory.createSession(getSrcIp(), srcPort, getDstIp(), dstPort, proto);
                        boolean exists = sessionTableFactory.exists(getSrcIp(), srcPort, getDstIp(), dstPort, proto);
                        context.getAppendToConsole().accept(String.format("【💬 会话表】: %s:%d → %s:%d (%s) 存在:%s",
                                getSrcIp(), srcPort, getDstIp(), dstPort, proto, exists));
                    }
                    break;

                case 107: // NetFlow 流记录
                    if (flowFactory != null && !isReturnTrip) {
                        String flowId = getSrcIp() + ":" + srcPort + "→" + getDstIp() + ":" + dstPort;
                        int bytes = 1500;
                        flowFactory.updateFlow(flowId, bytes);
                        FlowFactory.Flow flow = flowFactory.getFlow(flowId);
                        context.getAppendToConsole().accept(String.format("【📊 NetFlow】: 流 %s, 包数=%d, 字节=%d",
                                flowId.substring(0, Math.min(20, flowId.length())), flow.packets, flow.bytes));
                    }
                    break;

                case 108: // 负载均衡器
                    if (loadBalancerFactory != null && !isReturnTrip) {
                        List<String> servers = Arrays.asList("10.0.0.1", "10.0.0.2", "10.0.0.3");
                        loadBalancerFactory.setAlgorithm(LoadBalancerFactory.Algorithm.RR);
                        String selected = loadBalancerFactory.select(servers, getSrcIp());
                        context.getAppendToConsole().accept(String.format("【⚖️ 负载均衡】: 选择服务器 %s", selected));
                    }
                    break;

                case 109: // QoS 调度器
                    if (schedulerFactory != null) {
                        schedulerFactory.setType(SchedulerFactory.Type.WRR);
                        context.getAppendToConsole().accept("【🎯 QoS调度】: 启用加权轮询调度");
                    }
                    break;

                case 110: // DNS 区域记录
                    if (!hasDnsZone && dnsZoneFactory != null && cartType.equals("DNS_QUERY")) {
                        hasDnsZone = true;
                        dnsZoneFactory.addRecord(domain, "A", "10.0.0.1");
                        dnsZoneFactory.addRecord(domain, "AAAA", "2001:db8::1");
                        DnsZoneFactory.DnsRecord record = dnsZoneFactory.query(domain, "A");
                        if (record != null) {
                            context.getAppendToConsole().accept(String.format("【🌐 DNS区域】: %s A记录 → %s", domain, record.value));
                        }
                    }
                    break;

                case 111: // DHCP 租约管理
                    if (dhcpLeaseFactory != null && cartType.startsWith("DHCP")) {
                        dhcpLeaseFactory.offer(srcMac, "192.168.1.100");
                        String leasedIp = dhcpLeaseFactory.getIp(srcMac);
                        context.getAppendToConsole().accept(String.format("【📝 DHCP租约】: MAC %s → IP %s", srcMac, leasedIp));
                    }
                    break;

                case 112: // ARP 表（带老化）
                    if (!hasArpTable && arpTableFactory != null && context.getResolvedServerIp() != null) {
                        hasArpTable = true;
                        arpTableFactory.addDynamic(context.getResolvedServerIp(), dstMac);
                        String resolvedMac = arpTableFactory.resolve(context.getResolvedServerIp());
                        context.getAppendToConsole().accept(String.format("【📋 ARP表】: %s → %s", context.getResolvedServerIp(), resolvedMac));
                    }
                    break;

                case 113: // IPv6 邻居表
                    if (neighborTableFactory != null && context.getResolvedServerIp() != null && context.getResolvedServerIp().contains(":")) {
                        neighborTableFactory.add(context.getResolvedServerIp(), "00:11:22:33:44:55");
                        String ndMac = neighborTableFactory.getMac(context.getResolvedServerIp());
                        context.getAppendToConsole().accept(String.format("【📡 NDP邻居】: %s → %s", context.getResolvedServerIp(), ndMac));
                    }
                    break;

                case 114: // 组播路由表 (S,G) / (*,G)
                    if (multicastRoutingFactory != null && cartType.equals("IGMP_JOIN")) {
                        multicastRoutingFactory.addStarG("224.0.0.1");
                        boolean hasRoute = multicastRoutingFactory.hasRoute(getSrcIp(), "224.0.0.1");
                        context.getAppendToConsole().accept(String.format("【📡 组播路由】: (*,224.0.0.1) 存在=%s", hasRoute));
                    }
                    break;

                case 115: // MPLS 标签栈操作
                    if (!hasMplsLabel && mplsLabelFactory != null && !isReturnTrip) {
                        hasMplsLabel = true;
                        mplsLabelFactory.push(100);
                        mplsLabelFactory.swap(200);
                        Integer top = mplsLabelFactory.top();
                        context.getAppendToConsole().accept(String.format("【🏷️ MPLS】: 标签栈顶=%d", top));
                        mplsLabelFactory.pop();
                    }
                    break;

                case 116: // 证书存储
                    if (certificateStoreFactory != null && context.isTlsEnabled()) {
                        certificateStoreFactory.addKey("server-key", "RSA-2048-PRIVATE");
                        certificateStoreFactory.addTrustCert("ca-cert", "CA-CERT-DATA");
                        boolean trusted = certificateStoreFactory.isTrusted("CA-CERT-DATA");
                        context.getAppendToConsole().accept(String.format("【🔐 证书库】: 证书可信=%s", trusted));
                    }
                    break;

                case 117: // 事件记录（动画核心）
                    if (eventFactory != null) {
                        eventFactory.emit(EventFactory.Type.SEND, "发送 " + cartType + " SEQ=" + sequenceNumber);
                        context.getAppendToConsole().accept("【🎬 事件】: 已记录发送事件");
                    }
                    break;

                case 118: // 统计收集
                    if (statisticsFactory != null) {
                        if (!isReturnTrip) {
                            statisticsFactory.tx(ttl > 0 ? 1500 : 64);
                        } else {
                            statisticsFactory.rx(1500);
                        }
                        context.getAppendToConsole().accept(String.format("【📈 统计】: TX=%d, RX=%d, 丢包=%d",
                                statisticsFactory.packetsTx, statisticsFactory.packetsRx, statisticsFactory.loss));
                    }
                    break;

                case 119: // 日志记录
                    if (logFactory != null) {
                        logFactory.log(String.format("%s TTL=%d %s:%d→%s:%d",
                                cartType, ttl, getSrcIp(), srcPort, getDstIp(), dstPort));
                        context.getAppendToConsole().accept("【📝 日志】: 已记录到日志系统");
                    }
                    break;

                case 120: // PCAP 抓包
                    if (packetCaptureFactory != null && ethernetFrameData != null) {
                        packetCaptureFactory.capture(ethernetFrameData);
                        context.getAppendToConsole().accept(String.format("【📦 PCAP】: 抓取 %d 字节数据包", ethernetFrameData.length));
                        if (stage == 120 && !isReturnTrip && context.getServerReceivedCount() >= context.getTotalDataToTransmit()) {
                            List<PacketCaptureFactory.PcapPacket> replay = packetCaptureFactory.replay();
                            context.getAppendToConsole().accept(String.format("【🔄 回放】: 共 %d 个包可回放", replay.size()));
                        }
                    }
                    break;
                // ===================== 补充缺失的工厂处理 stage 121-160 =====================

// ===================== 链路层增强 (121-124) =====================
                case 121: // LLDP 链路层发现协议
                    if (lldpFactory != null && !isReturnTrip) {
                        byte[] lldpFrame = lldpFactory.buildLldpFrame();
                        context.getAppendToConsole().accept(String.format("【🔍 LLDP】: 发送链路发现报文 (%d字节)", lldpFrame.length));
                    }
                    break;

                case 122: // STP 生成树协议
                    if (stpFactory != null && !isReturnTrip) {
                        byte[] bpdu = stpFactory.buildBpdu();
                        context.getAppendToConsole().accept(String.format("【🌲 STP】: 发送 BPDU 报文，根桥 %s", stpFactory.rootBridge));
                    }
                    break;

                case 123: // LACP 链路聚合
                    if (lacpFactory != null && !isReturnTrip) {
                        byte[] lacpPdu = lacpFactory.buildLacpPdu();
                        context.getAppendToConsole().accept(String.format("【🔗 LACP】: 发送链路聚合报文，Actor Key=%d", lacpFactory.actorKey));
                    }
                    break;

                case 124: // MPLS 多协议标签交换
                    if (mplsFactory != null && !isReturnTrip) {
                        byte[] mplsPacket = mplsFactory.addMplsHeader(new byte[64]);
                        context.getAppendToConsole().accept(String.format("【🏷️ MPLS】: 添加标签 %d, TTL=%d", mplsFactory.label, mplsFactory.ttl));
                    }
                    break;

// ===================== 多播路由 (125-127) =====================
                case 125: // PIM-SM 稀疏模式组播
                    if (pimSmFactory != null && cartType.equals("IGMP_JOIN")) {
                        pimSmFactory.joinGroup("224.0.0.1");
                        context.getAppendToConsole().accept("【📡 PIM-SM】: 加入组播组 224.0.0.1");
                    }
                    break;

                case 126: // MLD 组播监听发现 (IPv6)
                    if (mldFactory != null && context.getResolvedServerIp() != null && context.getResolvedServerIp().contains(":")) {
                        byte[] mldReport = mldFactory.buildMldReport("ff02::1");
                        context.getAppendToConsole().accept("【📢 MLD】: 发送 MLD 报告报文");
                    }
                    break;

                case 127: // DVMRP 距离矢量组播路由
                    if (dvmrpFactory != null && !isReturnTrip) {
                        dvmrpFactory.addRoute("224.0.0.0/4", "10.0.0.1");
                        String upstream = dvmrpFactory.getUpstream("224.0.0.0/4");
                        context.getAppendToConsole().accept(String.format("【🗺️ DVMRP】: 组播路由上游 %s", upstream));
                    }
                    break;

// ===================== 监控管理 (128-132) =====================
                case 128: // NetFlow 流量采集
                    if (netFlowFactory != null && !isReturnTrip) {
                        netFlowFactory.addRecord(getSrcIp(), getDstIp(), srcPort, dstPort, 1);
                        context.getAppendToConsole().accept(String.format("【📊 NetFlow】: 流记录 %s:%d→%s:%d",
                                getSrcIp(), srcPort, getDstIp(), dstPort));
                    }
                    break;

                case 129: // sFlow 采样流
                    if (sflowFactory != null && !isReturnTrip) {
                        boolean sampled = sflowFactory.doSample(cartId);
                        byte[] sflowPacket = sflowFactory.buildSflowPacket(new byte[64]);
                        context.getAppendToConsole().accept(String.format("【📈 sFlow】: 采样 %s, 报文大小=%d", sampled ? "是" : "否", sflowPacket.length));
                    }
                    break;

                case 130: // IPFIX 流导出
                    if (ipfixFactory != null && !isReturnTrip) {
                        byte[] ipfixPacket = ipfixFactory.buildIpfixPacket(new byte[32]);
                        context.getAppendToConsole().accept(String.format("【📋 IPFIX】: 导出流数据 (%d字节)", ipfixPacket.length));
                    }
                    break;

                case 131: // ICMP Ping
                    if (icmpPingFactory != null && cartType.equals("ICMP_ECHO_REQ")) {
                        byte[] pingReq = icmpPingFactory.buildEchoRequest();
                        context.getAppendToConsole().accept(String.format("【📡 PING】: Echo Request, seq=%d", icmpPingFactory.seq));
                    }
                    break;

                case 132: // ICMP Traceroute
                    if (icmpTracerouteFactory != null && context.isTracerouteActive()) {
                        byte[] tracePacket = icmpTracerouteFactory.buildTracePacket();
                        context.getAppendToConsole().accept(String.format("【🔎 Traceroute】: TTL=%d 探测包", icmpTracerouteFactory.ttl));
                    }
                    break;

// ===================== 加密证书 (133-137) =====================
                case 133: // X.509 证书
                    if (x509Factory != null && context.isTlsEnabled()) {
                        byte[] cert = x509Factory.buildCert();
                        context.getAppendToConsole().accept(String.format("【📜 X.509】: 证书主体 %s", x509Factory.getSubject()));
                    }
                    break;

                case 134: // CRL 证书吊销列表
                    if (crlFactory != null && context.isTlsEnabled()) {
                        crlFactory.revokeCert("SN-12345");
                        boolean revoked = crlFactory.isRevoked("SN-12345");
                        context.getAppendToConsole().accept(String.format("【🚫 CRL】: 证书吊销状态 %s", revoked ? "已吊销" : "有效"));
                    }
                    break;

                case 135: // OCSP 在线证书状态
                    if (ocspFactory != null && context.isTlsEnabled()) {
                        byte[] ocspReq = ocspFactory.buildRequest("SN-12345");
                        byte[] ocspResp = ocspFactory.buildResponse(true);
                        context.getAppendToConsole().accept("【🔍 OCSP】: 证书状态查询完成");
                    }
                    break;

                case 136: // PKI 公钥基础设施
                    if (pkiFactory != null && context.isTlsEnabled()) {
                        boolean valid = pkiFactory.verifyChain(new String[]{"cert1", "cert2"});
                        context.getAppendToConsole().accept(String.format("【🔐 PKI】: 证书链验证 %s", valid ? "通过" : "失败"));
                    }
                    break;

                case 137: // DTLS 数据报 TLS
                    if (dtlsFactory != null && context.isUseUdp() && context.isTlsEnabled()) {
                        byte[] dtlsRecord = dtlsFactory.buildDtlsRecord("Hello".getBytes());
                        context.getAppendToConsole().accept(String.format("【🔄 DTLS】: DTLS 记录 (%d字节)", dtlsRecord.length));
                    }
                    break;

// ===================== 访问控制 (138-140) =====================
                case 138: // MAC 地址认证
                    if (macAuthFactory != null && srcMac != null) {
                        macAuthFactory.allowMac(srcMac);
                        boolean allowed = macAuthFactory.check(srcMac);
                        context.getAppendToConsole().accept(String.format("【🔑 MAC认证】: MAC %s 认证 %s", srcMac, allowed ? "通过" : "拒绝"));
                    }
                    break;

                case 139: // 802.1X 端口认证
                    if (dot1xFactory != null && !isReturnTrip) {
                        byte[] eapStart = dot1xFactory.buildEapStart();
                        dot1xFactory.setAuthResult(true);
                        context.getAppendToConsole().accept(String.format("【🔌 802.1X】: 认证状态 %s", dot1xFactory.isAuthenticated() ? "已认证" : "未认证"));
                    }
                    break;

// ===================== 诊断工具 (141-148) =====================
                case 141: // netstat
                    if (netstatFactory != null && !isReturnTrip) {
                        netstatFactory.addConn(context.isUseUdp() ? "UDP" : "TCP",
                                getSrcIp() + ":" + srcPort,
                                getDstIp() + ":" + dstPort,
                                context.isUseUdp() ? "ESTABLISHED" : context.getCurrentTcpState().toString());
                        context.getAppendToConsole().accept(String.format("【📊 netstat】: 连接数 %d", netstatFactory.getConnList().size()));
                    }
                    break;

                case 142: // ipconfig
                    if (ipconfigFactory != null && context.isPcIpAssigned()) {
                        String config = ipconfigFactory.getConfigInfo();
                        context.getAppendToConsole().accept(String.format("【⚙️ ipconfig】: %s", config));
                    }
                    break;

                case 143: // route print
                    if (routePrintFactory != null) {
                        routePrintFactory.addRoute("0.0.0.0", "0.0.0.0", "192.168.1.1", "eth0");
                        context.getAppendToConsole().accept(String.format("【🗺️ route】: 路由表条目数 %d", routePrintFactory.getRoutes().size()));
                    }
                    break;

                case 144: // nslookup
                    if (nslookupFactory != null && domain != null) {
                        nslookupFactory.addDnsRecord(domain, context.getResolvedServerIp() != null ? context.getResolvedServerIp() : "10.0.0.1");
                        String result = nslookupFactory.query(domain);
                        context.getAppendToConsole().accept(String.format("【🔍 nslookup】: %s → %s", domain, result));
                    }
                    break;

                case 145: // arp 命令
                    if (arpCommandFactory != null && context.getResolvedServerIp() != null) {
                        arpCommandFactory.addEntry(context.getResolvedServerIp(), dstMac);
                        String mac = arpCommandFactory.getMac(context.getResolvedServerIp());
                        context.getAppendToConsole().accept(String.format("【📋 arp】: %s → %s", context.getResolvedServerIp(), mac));
                    }
                    break;

                case 146: // curl
                    if (curlFactory != null && cartType.equals("HTTP_GET")) {
                        byte[] getRequest = curlFactory.buildGetRequest("/index.html");
                        context.getAppendToConsole().accept(String.format("【🌐 curl】: HTTP GET 请求 (%d字节)", getRequest.length));
                    }
                    break;

                case 147: // wget
                    if (wgetFactory != null && cartType.equals("HTTP_GET")) {
                        byte[] download = wgetFactory.buildDownloadRequest("/file.zip");
                        context.getAppendToConsole().accept(String.format("【⬇️ wget】: 下载请求，保存路径 %s", wgetFactory.savePath));
                    }
                    break;

                case 148: // telnet client
                    if (telnetClientFactory != null && cartType.equals("TELNET")) {
                        telnetClientFactory.connect(getDstIp(), 23);
                        byte[] telnetData = telnetClientFactory.sendData("ls\r\n");
                        context.getAppendToConsole().accept(String.format("【💻 telnet】: 连接到 %s:%d", getDstIp(), 23));
                    }
                    break;

// ===================== NAT 增强 (149-152) =====================
                case 149: // NAT 发夹
                    if (natHairpinningFactory != null && isNatted) {
                        natHairpinningFactory.addMapping(natPublicIp, getSrcIp());
                        String translated = natHairpinningFactory.hairpinTranslate(natPublicIp, getSrcIp());
                        context.getAppendToConsole().accept(String.format("【↩️ NAT发夹】: %s → %s", natPublicIp, translated));
                    }
                    break;

                case 150: // NAT 穿透 (打洞)
                    if (natHolePunchFactory != null && !isReturnTrip) {
                        boolean punched = natHolePunchFactory.doHolePunch(getDstIp() + ":" + dstPort);
                        context.getAppendToConsole().accept(String.format("【🕳️ NAT穿透】: %s:%d 打洞 %s", getDstIp(), dstPort, punched ? "成功" : "失败"));
                    }
                    break;

                case 151: // UPnP 端口映射
                    if (upnpFactory != null && !isReturnTrip) {
                        upnpFactory.addPortMap(8080, 80, getSrcIp(), "TCP");
                        context.getAppendToConsole().accept(String.format("【🔌 UPnP】: 端口映射 8080→%s:80", getSrcIp()));
                    }
                    break;

                case 152: // PCP 端口控制协议
                    if (pcpFactory != null && !isReturnTrip) {
                        byte[] pcpMap = pcpFactory.buildMapRequest(80, getSrcIp());
                        context.getAppendToConsole().accept(String.format("【📡 PCP】: 端口控制请求，生存时间=%ds", pcpFactory.lifetime));
                    }
                    break;

// ===================== VPN 隧道 (153-159) =====================
                case 153: // IKE 密钥交换
                    if (ipsecIkeFactory != null && !isReturnTrip) {
                        byte[] ikeSa = ipsecIkeFactory.buildIkeSaInit();
                        context.getAppendToConsole().accept("【🔑 IKE】: IKE SA 初始化");
                    }
                    break;

                case 154: // ESP 封装安全载荷
                    if (ipsecEspFactory != null && !isReturnTrip) {
                        byte[] espPacket = ipsecEspFactory.wrapEsp(new byte[100]);
                        context.getAppendToConsole().accept(String.format("【🔒 ESP】: ESP 封装, SPI=0x%08X", ipsecEspFactory.spi));
                    }
                    break;

                case 155: // AH 认证头
                    if (ipsecAhFactory != null && !isReturnTrip) {
                        byte[] ahPacket = ipsecAhFactory.wrapAh(new byte[100]);
                        context.getAppendToConsole().accept(String.format("【🔐 AH】: AH 认证, SPI=0x%08X", ipsecAhFactory.spi));
                    }
                    break;

                case 156: // OpenVPN
                    if (openVpnFactory != null && !isReturnTrip) {
                        byte[] ovpnPacket = openVpnFactory.buildOpenVpnPacket(new byte[64]);
                        context.getAppendToConsole().accept(String.format("【🔓 OpenVPN】: OpenVPN 数据包 (%d字节)", ovpnPacket.length));
                    }
                    break;

                case 157: // WireGuard
                    if (wireguardFactory != null && !isReturnTrip) {
                        byte[] wgPacket = wireguardFactory.buildWgPacket(new byte[64]);
                        context.getAppendToConsole().accept("【🔒 WireGuard】: WireGuard 加密隧道");
                    }
                    break;

                case 158: // L2TP
                    if (l2tpFactory != null && !isReturnTrip) {
                        byte[] l2tpPacket = l2tpFactory.buildL2tpPacket(new byte[64]);
                        context.getAppendToConsole().accept(String.format("【🔌 L2TP】: L2TP 隧道, TunnelID=%d", l2tpFactory.tunnelId));
                    }
                    break;

                case 159: // SSTP
                    if (sstpFactory != null && !isReturnTrip) {
                        byte[] sstpControl = sstpFactory.buildSstpControl();
                        context.getAppendToConsole().accept("【🌐 SSTP】: SSTP 控制报文");
                    }
                    break;

// ===================== 安全增强 (160) =====================
                case 160: // IPS 入侵防御
                    if (ipsFactory != null && !isReturnTrip) {
                        boolean isAttack = ipsFactory.isAttack(getSrcIp(), new byte[64]);
                        if (isAttack) {
                            context.getAppendToConsole().accept(String.format("【🛡️ IPS】: 检测到攻击来自 %s，已阻断", getSrcIp()));
                            this.isDropped = true;
                            return;
                        }
                        context.getAppendToConsole().accept("【🛡️ IPS】: 流量正常");
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
                                context.getAppendToConsole().accept("【🔐 FTP_AUTH(161)】: 处理 USER " + username);
                            } else if (ftpCommand.startsWith("PASS")) {
                                authFactory.setAuthenticated(true);
                                context.getAppendToConsole().accept("【🔐 FTP_AUTH(161)】: 认证成功");
                                this.ftpResponseCode = 230;
                            }
                        }
                    }
                    break;

                case 162: // FTP_COMMAND - 命令工厂
                    if (ftpCommand != null && !ftpCommand.isEmpty()) {
                        FtpCommandFactory cmdFactory = factoryManager.getFtpCommandFactory();
                        if (cmdFactory != null) {
                            context.getAppendToConsole().accept("【📝 FTP_COMMAND(162)】: 验证命令 - " + ftpCommand);

                            if (ftpCommand.startsWith("USER")) {
                                this.ftpResponseCode = 331;
                                context.getAppendToConsole().accept("【📝 FTP_COMMAND(162)】: 响应 331 - 需要密码");
                            } else if (ftpCommand.startsWith("PASS")) {
                                this.ftpResponseCode = 230;
                                context.getAppendToConsole().accept("【📝 FTP_COMMAND(162)】: 响应 230 - 登录成功");
                            } else if (ftpCommand.equals("PASV")) {
                                this.ftpResponseCode = 227;
                                context.getAppendToConsole().accept("【📝 FTP_COMMAND(162)】: 响应 227 - 进入被动模式");
                            } else if (ftpCommand.equals("LIST")) {
                                this.ftpResponseCode = 150;
                                context.getAppendToConsole().accept("【📝 FTP_COMMAND(162)】: 响应 150 - 准备打开数据连接");
                            } else if (ftpCommand.equals("QUIT")) {
                                this.ftpResponseCode = 221;
                                context.getAppendToConsole().accept("【📝 FTP_COMMAND(162)】: 响应 221 - 服务关闭");
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
                            context.getAppendToConsole().accept(String.format("【🔌 FTP_CHANNEL(163)】: 被动模式，数据端口=%d", dataPort));
                        } else if (ftpCommand.equals("LIST") || ftpCommand.startsWith("RETR") || ftpCommand.startsWith("STOR")) {
                            context.getAppendToConsole().accept("【🔌 FTP_CHANNEL(163)】: 数据通道准备就绪");
                        } else {
                            // 其他命令（如 USER、PASS）不需要数据通道
                            context.getAppendToConsole().accept("【🔌 FTP_CHANNEL(163)】: 控制命令，无需数据通道");
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
                                context.getAppendToConsole().accept(String.format("【📋 FTP_RESPONSE(164)】: 解析响应 %d", response.getCode()));
                            }
                        }
                    } else {
                        // 对于 USER 命令，模拟响应
                        context.getAppendToConsole().accept("【📋 FTP_RESPONSE(164)】: 等待服务器响应");
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
                            context.getAppendToConsole().accept(String.format("【📁 FTP_MAIN(165)】: 构建 %d 字节命令", finalCommand.length));
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
                            context.getAppendToConsole().accept(String.format("【📤 HTTP_REQ(166)】: GET(%dB) POST(%dB) PUT(%dB) DELETE(%dB)",
                                    getLine.length, postLine.length, putLine.length, delLine.length));
                            context.getAppendToConsole().accept("【📤 HTTP_REQ(166)】: 支持 GET/POST/PUT/DELETE/PATCH/HEAD/OPTIONS/CONNECT/TRACE");
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
                            context.getAppendToConsole().accept(String.format("【📥 HTTP_RES(167)】: 200OK(%dB) 404(%dB) 500(%dB) 302(%dB)",
                                    ok200.length, nf404.length, err500.length, redirect.length));
                            context.getAppendToConsole().accept("【📥 HTTP_RES(167)】: 支持 60+ 标准状态码(1xx~5xx)");
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
                            context.getAppendToConsole().accept(String.format("【📋 HTTP_HDR(168)】: 请求头 %d 个/%dB 响应头 %d 个",
                                    reqHeaders.size(), hdrBytes.length, resHeaders.size()));
                            context.getAppendToConsole().accept("【📋 HTTP_HDR(168)】: 支持 30+ 标准头字段");
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
                            context.getAppendToConsole().accept(String.format("【📦 HTTP_BODY(169)】: JSON(%dB) FORM(%dB) HTML(%dB)",
                                    json.length, form.length, html.length));
                            context.getAppendToConsole().accept("【📦 HTTP_BODY(169)】: 支持 JSON/XML/Form/Multipart/HTML/Text");
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
                            context.getAppendToConsole().accept(String.format("【🍪 HTTP_CK(170)】: Set-Cookie: %s", setCookie));
                            context.getAppendToConsole().accept(String.format("【🍪 HTTP_CK(170)】: Cookie: %s", cookieHeader));
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
                            context.getAppendToConsole().accept(String.format("【🔑 HTTP_AUTH(171)】: Basic: %s", basic.substring(0, 20) + "..."));
                            context.getAppendToConsole().accept(String.format("【🔑 HTTP_AUTH(171)】: Bearer: %s...", bearer.substring(0, 20)));
                            context.getAppendToConsole().accept("【🔑 HTTP_AUTH(171)】: 支持 Basic/Digest/Bearer 三种认证");
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
                            context.getAppendToConsole().accept(String.format("【💾 HTTP_CACHE(172)】: Cache-Control: %s", cc));
                            context.getAppendToConsole().accept(String.format("【💾 HTTP_CACHE(172)】: ETag: %s", etag));
                            context.getAppendToConsole().accept(String.format("【💾 HTTP_CACHE(172)】: Last-Modified: %s", lm));
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
                            context.getAppendToConsole().accept(String.format("【🧩 HTTP_CHUNK(173)】: 分块编码 %d 字节, 解码=%s",
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
                            context.getAppendToConsole().accept(String.format("【📡 HTTP2_FRAME(174)】: HEADERS(%dB) DATA(%dB) SETTINGS(%dB) GOAWAY(%dB)",
                                    headers.length, data.length, settings.length, goaway.length));
                            context.getAppendToConsole().accept("【📡 HTTP2_FRAME(174)】: 支持 10 种帧类型");
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
                        context.getAppendToConsole().accept(String.format("【⚙️ HTTP2_SET(175)】: 序列化 %d 字节, 并发流=%d, 窗口=%d",
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
                            context.getAppendToConsole().accept(String.format("【🌊 HTTP2_STR(176)】: 已创建 3 个流, 活跃 %d", active));
                            h2StreamFactory.reset();
                            context.getAppendToConsole().accept("【🌊 HTTP2_STR(176)】: 流已重置, 支持 IDLE/OPEN/HALF_CLOSED/CLOSED 状态机");
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
                        context.getAppendToConsole().accept("【📦 UDP】: 应用层数据准备");
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
                        srcIp = context.getPcIpAddress() != null ? context.getPcIpAddress() : "192.168.1.100";
                        dstIp = context.getResolvedServerIp() != null ? context.getResolvedServerIp() : "10.0.0.1";
                        context.getAppendToConsole().accept("【📦 UDP IP首部】: " + srcIp + " → " + dstIp);
                    }
                    break;
                case 49:  // UDP 校验和
                    if (!hasUdpChecksum) {
                        hasUdpChecksum = true;
                        context.getAppendToConsole().accept("【🔢 UDP校验和】: 已计算");
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
                        srcIp = context.getPcIpAddress() != null ? context.getPcIpAddress() : "192.168.1.100";
                        dstIp = context.getResolvedServerIp() != null ? context.getResolvedServerIp() : "10.0.0.1";
                        context.getAppendToConsole().accept("【📦 IP首部】: " + srcIp + " → " + dstIp + ", TTL=" + ttl);
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
