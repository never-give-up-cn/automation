package com.never_give_up.automation.demo.winModel;

import com.never_give_up.automation.demo.DataCartFactoryGame;
import com.never_give_up.automation.demo.adapter.FactoryManager;
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
import javax.crypto.spec.SecretKeySpec;
import java.awt.Point;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Data
public class DataPacket {
    private static final AtomicInteger cartIdGenerator = new AtomicInteger(0);
    public final int cartId = cartIdGenerator.getAndIncrement();

    // ===================== 所有工厂引用 =====================
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
    private transient com.never_give_up.automation.demo.factory.transport.TcpPacketFactory tcpFactory;
    private transient com.never_give_up.automation.demo.factory.network.IpPacketFactory ipFactory;
    private transient EthernetFactory ethernetFactory;
    private transient com.never_give_up.automation.demo.factory.address.PortFactory portFactory;
    private transient Ipv6PacketFactory ipv6PacketFactory;
    private transient Ipv6FragmentFactory ipv6FragmentFactory;
    private transient Ipv6OptionFactory ipv6OptionFactory;
    private transient Ipv6NeighborDiscovery ipv6NeighborDiscovery;
    private transient PimSmFactory pimSmFactory;
    private transient MldFactory mldFactory;
    private transient DvmrpFactory dvmrpFactory;
    private transient TcpKeepAliveFactory tcpKeepAliveFactory;
    private transient TcpSackFactory tcpSackFactory;
    private transient TcpEcnFactory tcpEcnFactory;
    private transient TcpFastOpenFactory tcpFastOpenFactory;
    private transient LldpFactory lldpFactory;
    private transient StpFactory stpFactory;
    private transient LACPFactory lacpFactory;
    private transient MplsFactory mplsFactory;
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
    private transient NatHairpinningFactory natHairpinningFactory;
    private transient NatHolePunchFactory natHolePunchFactory;
    private transient UpnpFactory upnpFactory;
    private transient PcpFactory pcpFactory;
    private transient LbRoundRobinFactory lbRoundRobinFactory;
    private transient LbLeastConnFactory lbLeastConnFactory;
    private transient LbIpHashFactory lbIpHashFactory;
    private transient LbHealthCheckFactory lbHealthCheckFactory;
    private transient NetFlowFactory netFlowFactory;
    private transient SflowFactory sflowFactory;
    private transient IpfixFactory ipfixFactory;
    private transient IcmpPingFactory icmpPingFactory;
    private transient IcmpTracerouteFactory icmpTracerouteFactory;
    private transient IpsecIkeFactory ipsecIkeFactory;
    private transient IpsecEspFactory ipsecEspFactory;
    private transient IpsecAhFactory ipsecAhFactory;
    private transient OpenVpnFactory openVpnFactory;
    private transient WireguardFactory wireguardFactory;
    private transient L2tpFactory l2tpFactory;
    private transient SstpFactory sstpFactory;
    private transient DpiFactory dpiFactory;
    private transient IpsFactory ipsFactory;
    private transient WafFactory wafFactory;
    private transient DdosMitigationFactory ddosMitigationFactory;
    private transient RateLimitFactory rateLimitFactory;
    private transient X509Factory x509Factory;
    private transient CrlFactory crlFactory;
    private transient OcspFactory ocspFactory;
    private transient PkiFactory pkiFactory;
    private transient DtlsFactory dtlsFactory;
    private transient AclFactory aclFactory;
    private transient MacAuthFactory macAuthFactory;
    private transient Dot1xFactory dot1xFactory;
    private transient NetstatFactory netstatFactory;
    private transient IpconfigFactory ipconfigFactory;
    private transient RoutePrintFactory routePrintFactory;
    private transient NslookupFactory nslookupFactory;
    private transient ArpCommandFactory arpCommandFactory;
    private transient TelnetClientFactory telnetClientFactory;
    private transient CurlFactory curlFactory;
    private transient WgetFactory wgetFactory;
    private transient FactoryManager factoryManager;

    // 状态标志
    private boolean hasSocket = false, hasMacLearning = false, hasFibLookup = false;
    private boolean hasFlowRecord = false, hasMplsLabel = false, hasDnsZone = false;
    private boolean hasDhcpLease = false, hasArpTable = false, hasBitStream = false;
    private boolean hasPhysicalEncoding = false, hasPppoe = false, hasMacSec = false;
    private boolean hasOspf = false, hasBgp = false, hasQos = false, hasNat64 = false;
    private boolean isReassembled = false, isAttackPacket = false, hasNtp = false;
    private boolean hasSnmp = false, hasHttp23 = false, hasIpsec = false, hasIpv6 = false;
    private boolean hasIpv6Fragment = false, hasIpv6Option = false, hasIpv6Nd = false;
    private boolean hasKeepAlive = false, hasSack = false, hasEcn = false, hasFastOpen = false;
    private boolean hasFtp = false, hasSmtp = false, hasPop3 = false, hasImap = false;
    private boolean hasSsh = false, hasTelnet = false, hasRtp = false, hasSip = false;
    private boolean hasRadius = false, hasDpi = false, isBlockedByWaf = false;
    private boolean isRateLimited = false, isIpsecSecured = false, hasIpsecIke = false;
    private boolean hasIpsecEsp = false, hasOpenVpn = false, hasWireguard = false, hasL2tp = false;
    private boolean hasIpOption = false, hasTcpOption = false, hasUdpChecksum = false;
    private boolean hasEtherPadding = false, hasTunnel = false, hasVlan = false;
    private boolean hasIgmp = false, hasNdp = false, isEncapsulated = false;
    private String httpResponseContent = "";
    // 基础字段
    private byte[] ethernetFrameData;
    private boolean fcsVerified = false;
    private boolean hasFiveTuple = false, hasSession = false, hasQueue = false, hasArp = false;
    private String srcMac, dstMac, srcIp, dstIp;
    private int srcPort = 1234, dstPort = 443;
    private String protocol = "TCP";
    private int identification, fragmentOffset;
    private boolean moreFragments;
    private byte[] fragmentData, serverCertificate, encryptedData;
    private double x, y;
    private double speed = 12.0;
    private int stage, timer = 0;
    private boolean arrived = false, dropped = false, returnTrip = false, retransmission = false;
    private String cartType, currentLayerStatus = "";
    private int sequenceNumber = 0, ackNumber = 0, advertisedWindow = 3;
    private int waitInQueueTimer = 0, ttl = 64;
    private String domain, resolvedIp;
    private long echoSendTimestamp = 0;
    private String droppedAtRouterTag = null;
    private Point droppedAtPosition = null;
    private boolean hasPayload = true;
    private boolean hasApp = false, hasTcp = false, hasIp = false, hasEther = false, hasLlc = false, hasFcs = false;
    private boolean c_Payload = false, c_SP = false, c_DP = false, c_SEQ = false, c_ACK = false, c_CTL = false, c_WIN = false, c_CHK = false;
    private boolean isFragmented = false, isNatted = false;
    private String natPublicIp = null;
    private int natPublicPort = 0;
    private String httpBody = null;
    private int vlanId = 0;
    private byte[] ipv6SrcAddr = new byte[16], ipv6DstAddr = new byte[16];
    private String ftpUser = null, smtpFrom = null, smtpTo = null;
    private int rtpSeq = 0;


    // 在字段区域添加
    private boolean pcIpAssigned = false;

    // 添加 getter
    public boolean isPcIpAssigned() {
        return pcIpAssigned;
    }

    // 外部引用
    private transient Point pcFactory;
    private transient Map<String, NatEntry> natTable;
    private transient Map<String, ArpEntry> arpCache;
    private transient Map<String, DnsEntry> dnsCache;
    private transient Consumer<String> logConsumer;
    private transient Consumer<DataPacket> addPendingPacketConsumer;
    private transient int ipIdentifierCounter;
    private transient boolean useUdp, tlsEnabled;
    private transient TlsState tlsState;
    private transient int serverReceivedCount, totalDataToTransmit, cwnd, packetsAckedSinceLastIncrease;
    private transient int serverBufferCount, SERVER_BUFFER_MAX;
    private transient long lastServerConsumeTime;
    private transient Cipher rsaCipher;
    private transient java.security.KeyPair serverRsaKeyPair;
    private transient javax.crypto.SecretKey sessionKey;
    private transient Cipher aesCipher;
    private transient boolean tlsCipherReady;
    private transient String pcIpAddress, resolvedServerIp;
    private transient String currentTcpState;
    private transient boolean tracerouteActive;
    private transient int tracerouteNextTTL;
    private transient java.util.function.Consumer<String> appendToConsole;
    private transient java.util.function.Consumer<String> updateArpDisplay;
    private transient java.util.function.Consumer<String> updateNatDisplay;
    private transient java.util.function.Consumer<String> updateDnsDisplay;
    private transient java.util.function.Consumer<String> updateTopLabel;
    private transient java.util.function.BiConsumer<Integer, Integer> setCwndAndSsthresh;
    private transient java.util.function.Consumer<Integer> setPacketsAckedSinceLastIncrease;
    private transient java.util.function.BiConsumer<String, String> setDnsResolvedAndIp;
    private transient java.util.function.Consumer<DataPacket> handleCartArrival;
    private transient int WAN_BOTTLE_NECK_MAX;
    private transient java.util.function.BiFunction<DataPacket, Boolean, Boolean> isForemostInWan;

    public DataPacket(double sx, double sy, String type, int seq, FactoryManager factoryManager) {
        this.x = sx;
        this.y = sy;
        this.cartType = type;
        this.sequenceNumber = seq;
        this.factoryManager = factoryManager;
        if (type.startsWith("DHCP")) {
            if (type.equals("DHCP_DISCOVER")) {
                this.stage = 1;  // 从1开始，先移动到 DHCP_DISC
            } else if (type.equals("DHCP_OFFER") || type.equals("DHCP_ACK")) {
                this.stage = 1;  // 从1开始，先移动到对应的建筑
            } else if (type.equals("DHCP_REQUEST")) {
                this.stage = 1;
            }
            this.protocol = "DHCP";
            return;
        }
        if (type.equals("DNS_QUERY") || type.equals("DNS_RESPONSE") ||
                type.equals("DNS_RECURSION_ROOT") || type.equals("DNS_ROOT_TO_LOCAL") ||
                type.equals("DNS_LOCAL_TO_AUTH") || type.equals("DNS_AUTH_TO_LOCAL") ||
                type.equals("DNS_RECURSION_AUTH") || type.equals("DNS_RECURSION_AUTH_RESP")) {
            this.stage = 1;
            this.protocol = "DNS";
            return;
        }

        if (type.equals("ICMP_TIMEEXCEEDED") || type.equals("ICMP_ECHO_REPLY") || type.equals("HTTP_200_OK")) {
            this.returnTrip = true;
            this.stage = -1;
        } else if (isControlFrame(type)) {
            this.stage = 2;
        } else if (type.startsWith("TLS_") || type.equals("HTTP_GET") || type.equals("UDP_DATA")) {
            this.stage = 5;
        } else {
            this.stage = 1;
        }
    }

    public void setExternalReferences(
            Point pcFactory, Map<String, NatEntry> natTable, Map<String, ArpEntry> arpCache,
            Map<String, DnsEntry> dnsCache, Consumer<String> logConsumer,
            Consumer<DataPacket> addPendingPacketConsumer, int ipIdentifierCounter,
            boolean useUdp, boolean tlsEnabled, TlsState tlsState,
            int serverReceivedCount, int totalDataToTransmit, int cwnd,
            int packetsAckedSinceLastIncrease, int serverBufferCount, int SERVER_BUFFER_MAX,
            long lastServerConsumeTime, Cipher rsaCipher, java.security.KeyPair serverRsaKeyPair,
            javax.crypto.SecretKey sessionKey, Cipher aesCipher, boolean tlsCipherReady,
            String pcIpAddress, String resolvedServerIp, String currentTcpState,
            boolean tracerouteActive, int tracerouteNextTTL,
            java.util.function.Consumer<String> appendToConsole,
            java.util.function.Consumer<String> updateArpDisplay,
            java.util.function.Consumer<String> updateNatDisplay,
            java.util.function.Consumer<String> updateDnsDisplay,
            java.util.function.Consumer<String> updateTopLabel,
            java.util.function.BiConsumer<Integer, Integer> setCwndAndSsthresh,
            java.util.function.Consumer<Integer> setPacketsAckedSinceLastIncrease,
            java.util.function.BiConsumer<String, String> setDnsResolvedAndIp,
            java.util.function.Consumer<DataPacket> handleCartArrival,
            int WAN_BOTTLE_NECK_MAX,
            java.util.function.BiFunction<DataPacket, Boolean, Boolean> isForemostInWan
    ) {
        this.pcFactory = pcFactory;
        this.natTable = natTable;
        this.arpCache = arpCache;
        this.dnsCache = dnsCache;
        this.logConsumer = logConsumer;
        this.addPendingPacketConsumer = addPendingPacketConsumer;
        this.ipIdentifierCounter = ipIdentifierCounter;
        this.useUdp = useUdp;
        this.tlsEnabled = tlsEnabled;
        this.tlsState = tlsState;
        this.serverReceivedCount = serverReceivedCount;
        this.totalDataToTransmit = totalDataToTransmit;
        this.cwnd = cwnd;
        this.packetsAckedSinceLastIncrease = packetsAckedSinceLastIncrease;
        this.serverBufferCount = serverBufferCount;
        this.SERVER_BUFFER_MAX = SERVER_BUFFER_MAX;
        this.lastServerConsumeTime = lastServerConsumeTime;
        this.rsaCipher = rsaCipher;
        this.serverRsaKeyPair = serverRsaKeyPair;
        this.sessionKey = sessionKey;
        this.aesCipher = aesCipher;
        this.tlsCipherReady = tlsCipherReady;
        this.pcIpAddress = pcIpAddress;
        this.resolvedServerIp = resolvedServerIp;
        this.currentTcpState = currentTcpState;
        this.tracerouteActive = tracerouteActive;
        this.tracerouteNextTTL = tracerouteNextTTL;
        this.appendToConsole = appendToConsole;
        this.updateArpDisplay = updateArpDisplay;
        this.updateNatDisplay = updateNatDisplay;
        this.updateDnsDisplay = updateDnsDisplay;
        this.updateTopLabel = updateTopLabel;
        this.setCwndAndSsthresh = setCwndAndSsthresh;
        this.setPacketsAckedSinceLastIncrease = setPacketsAckedSinceLastIncrease;
        this.setDnsResolvedAndIp = setDnsResolvedAndIp;
        this.handleCartArrival = handleCartArrival;
        this.WAN_BOTTLE_NECK_MAX = WAN_BOTTLE_NECK_MAX;
        this.isForemostInWan = isForemostInWan;

        initFactories();
    }

    private void initFactories() {
        if (factoryManager == null) return;
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
        this.tcpFactory = factoryManager.getTcpPacketFactory();
        this.ipFactory = factoryManager.getIpPacketFactory();
        this.ethernetFactory = factoryManager.getEthernetFactory();
        this.portFactory = factoryManager.getPortFactory();
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

    public boolean isControlFrame(String type) {
        if (type.equals("DNS_QUERY") || type.equals("DNS_RESPONSE")) return false;
        return type.equals("SYN") || type.equals("SYN_ACK") || type.equals("ACK_PC") ||
                type.equals("FIN_PC") || type.equals("FIN_ACK_SRV") || type.equals("FIN_SRV") ||
                type.equals("DATA_ACK") || type.equals("LAST_ACK_PC") || type.equals("ZWP") ||
                type.startsWith("DHCP");
    }

    private boolean isDnsOrDhcp() {
        return cartType.startsWith("DHCP") || cartType.startsWith("DNS_");
    }

    private void log(String message) {
        if (logConsumer != null) logConsumer.accept(message);
    }

    private void addPendingPacket(DataPacket cart) {
        if (addPendingPacketConsumer != null) addPendingPacketConsumer.accept(cart);
    }

    private void applyNatMapping() {
        String insideIp = pcIpAddress != null ? pcIpAddress : "192.168.1.100";
        int insidePort = 1234;
        String key = insideIp + ":" + insidePort;
        if (natTable != null && !natTable.containsKey(key)) {
            NatEntry localEntry = new NatEntry(insideIp, insidePort, "203.0.113.1", 50001);
            natTable.put(key, localEntry);
        }
        if (natTable != null && natTable.containsKey(key)) {
            NatEntry entry = natTable.get(key);
            this.isNatted = true;
            this.natPublicIp = entry.getPublicIp();
            this.natPublicPort = entry.getPublicPort();
        }
    }

    private String getSrcIp() {
        return srcIp != null ? srcIp : (pcIpAddress != null ? pcIpAddress : "192.168.1.100");
    }

    private String getDstIp() {
        return dstIp != null ? dstIp : (resolvedServerIp != null ? resolvedServerIp : "10.0.0.1");
    }

    private int ipToInt(String ip) {
        if (ip == null || ip.isEmpty()) return 0;
        try {
            String[] parts = ip.split("\\.");
            if (parts.length != 4) return 0;
            int result = 0;
            for (int i = 0; i < 4; i++) {
                result |= (Integer.parseInt(parts[i]) << (24 - (8 * i)));
            }
            return result;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Point findBuildingCoords(String tag) {
        if (pcFactory == null) return new Point(0, 0);
        return pcFactory;
    }

    // 在 DataPacket.java 中，替换 findTargetMachine 方法

    private Point findTargetMachine(int s, String type) {
        if (type.startsWith("DHCP")) {
            System.out.println("findTargetMachine called for " + type + ", stage=" + s);
        }
        if (pcFactory == null) return null;

        // ACK_PC 处理
        if (type.equals("ACK_PC")) {
            if (s == 2) return findBuildingCoords("T_CORE");
            else if (s >= 3) return findBuildingCoords("RX_ST");
            return null;
        }

        // DNS 处理
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
            return findBuildingCoords("PC_FACTORY");
        }
        if (type.equals("DNS_RECURSION_ROOT") || type.equals("DNS_RECURSION_AUTH")) {
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

        // DHCP 处理 - 完全重写
        if (type.startsWith("DHCP")) {
            System.out.println("Processing DHCP: " + type + ", stage=" + s);

            if (type.equals("DHCP_DISCOVER")) {
                if (s == 1) {
                    Point p = findBuildingCoords("DHCP_DISC");
                    System.out.println("DHCP_DISCOVER stage 1 -> DHCP_DISC: " + p);
                    if (p != null) return p;
                } else if (s == 2) {
                    Point p = findBuildingCoords("DHCP_SERVER");
                    System.out.println("DHCP_DISCOVER stage 2 -> DHCP_SERVER: " + p);
                    if (p != null) return p;
                }
            }
            else if (type.equals("DHCP_OFFER")) {
                if (s == 1) {
                    Point p = findBuildingCoords("DHCP_OFFER");
                    System.out.println("DHCP_OFFER stage 1 -> DHCP_OFFER: " + p);
                    if (p != null) return p;
                } else if (s == 2) {
                    Point p = findBuildingCoords("PC_FACTORY");
                    System.out.println("DHCP_OFFER stage 2 -> PC_FACTORY: " + p);
                    if (p != null) return p;
                }
            }
            else if (type.equals("DHCP_REQUEST")) {
                if (s == 1) {
                    Point p = findBuildingCoords("DHCP_REQ");
                    System.out.println("DHCP_REQUEST stage 1 -> DHCP_REQ: " + p);
                    if (p != null) return p;
                } else if (s == 2) {
                    Point p = findBuildingCoords("DHCP_SERVER");
                    System.out.println("DHCP_REQUEST stage 2 -> DHCP_SERVER: " + p);
                    if (p != null) return p;
                }
            }
            else if (type.equals("DHCP_ACK")) {
                if (s == 1) {
                    Point p = findBuildingCoords("DHCP_ACK");
                    System.out.println("DHCP_ACK stage 1 -> DHCP_ACK: " + p);
                    if (p != null) return p;
                } else if (s == 2) {
                    Point p = findBuildingCoords("PC_FACTORY");
                    System.out.println("DHCP_ACK stage 2 -> PC_FACTORY: " + p);
                    if (p != null) return p;
                }
            }
            return null;
        }

        // TLS 和 HTTP 处理
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

        // 普通数据包 - 使用 getTagForStage 获取目标建筑
        String tag = getTagForStage(s);
        if (tag != null && !tag.equals("NONE")) {
            Point coords = findBuildingCoords(tag);
            if (coords != null) return coords;
        }

        // 默认返回 PC_FACTORY
        return findBuildingCoords("PC_FACTORY");
    }

    private String getTagForStage(int s) {
        switch (s) {
            case 1:
                return "DNS_CLIENT";
            case 2:
                return "DNS_LOCAL";
            case 3:
                return "DNS_ROOT";
            case 4:
                return "DNS_AUTH";
            case 5:
                return "TX_APP";
            case 6:
                return "T_SP";
            case 7:
                return "T_DP";
            case 8:
                return "T_SEQ";
            case 9:
                return "T_ACK";
            case 10:
                return "T_CTL";
            case 11:
                return "T_WIN";
            case 12:
                return "T_CHK";
            case 13:
                return "T_CORE";
            case 14:
                return "TX_IPH";
            case 15:
                return "TX_IP_FRAG";
            case 16:
                return "TX_ARP";
            case 17:
                return "ETH_DST";
            case 18:
                return "ETH_SRC";
            case 19:
                return "ETH_TYPE";
            case 20:
                return "TX_LLC";
            case 21:
                return "TX_FCS";
            case 22:
                return "FIVETUPLE";
            case 23:
                return "SESSION";
            case 24:
                return "R_LAN";
            case 25:
                return "R_TAB";
            case 26:
                return "R_NAT";
            case 27:
                return "BW_CTRL";
            case 28:
                return "R_WAN";
            case 29:
                return "FW_OUT";
            case 30:
                return "FW_IN";
            case 31:
                return "ROUTER1";
            case 32:
                return "ROUTER2";
            case 33:
                return "ROUTER3";
            case 34:
                return "Q_IN";
            case 35:
                return "Q_OUT";
            case 36:
                return "Q_DROP";
            case 37:
                return "RX_ETH";
            case 38:
                return "RX_LLC";
            case 39:
                return "RX_FCS";
            case 40:
                return "RX_ARP";
            case 41:
                return "RX_FRAG";
            case 42:
                return "RX_IP";
            case 43:
                return "RX_PORT";
            case 44:
                return "RX_TCP";
            case 45:
                return "RX_APP";
            case 46:
                return "TCP_OPTION";
            case 47:
                return "IP_OPTION";
            case 48:
                return "ETH_PADDING";
            case 49:
                return "UDP_CHECKSUM";
            case 50:
                return "ICMP_ERROR";
            case 51:
                return "IP_FORWARD";
            case 52:
                return "TCP_WINDOW";
            case 53:
                return "TCP_TIMER";
            case 54:
                return "VLAN_TAG";
            case 55:
                return "TUNNEL_GRE";
            case 56:
                return "IGMP_MCAST";
            case 57:
                return "NDP_DISC";
            case 58:
                return "DNS_RECURSIVE";
            case 59:
                return "DHCP_FULL";
            case 60:
                return "TLS_HANDSHAKE";
            case 61:
                return "SERIALIZE";
            case 62:
                return "BIT_STREAM";
            case 63:
                return "PHY_CHANNEL";
            case 64:
                return "PPPOE";
            case 65:
                return "MACSEC";
            case 66:
                return "OSPF";
            case 67:
                return "BGP";
            case 68:
                return "QOS";
            case 69:
                return "NAT64";
            case 70:
                return "TCP_REASSEMBLY";
            case 71:
                return "ATTACK";
            case 72:
                return "NTP";
            case 73:
                return "SNMP";
            case 74:
                return "HTTP23";
            case 75:
                return "IPSEC";
            case 76:
                return "IPV6";
            case 77:
                return "IPV6_FRAG";
            case 78:
                return "IPV6_OPTION";
            case 79:
                return "IPV6_ND";
            case 80:
                return "TCP_KEEPALIVE";
            case 81:
                return "TCP_SACK";
            case 82:
                return "TCP_ECN";
            case 83:
                return "TCP_FASTOPEN";
            case 84:
                return "FTP";
            case 85:
                return "SMTP";
            case 86:
                return "POP3";
            case 87:
                return "IMAP";
            case 88:
                return "SSH";
            case 89:
                return "TELNET";
            case 90:
                return "RTP";
            case 91:
                return "SIP";
            case 92:
                return "RADIUS";
            case 96:
                return "DPI";
            case 97:
                return "WAF";
            case 98:
                return "DDOS";
            case 99:
                return "RATELIMIT";
            case 100:
                return "ACL";
            case 101:
                return "SOCKET";
            case 102:
                return "TCP_STATE";
            case 103:
                return "MAC_TABLE";
            case 104:
                return "CAM_TABLE";
            case 105:
                return "FIB";
            case 106:
                return "SESSION_TABLE";
            case 107:
                return "FLOW";
            case 108:
                return "LOAD_BALANCER";
            case 109:
                return "SCHEDULER";
            case 110:
                return "DNS_ZONE";
            case 111:
                return "DHCP_LEASE";
            case 112:
                return "ARP_TABLE";
            case 113:
                return "NEIGHBOR_TABLE";
            case 114:
                return "MCAST_ROUTE";
            case 115:
                return "MPLS_LABEL";
            case 116:
                return "CERT_STORE";
            case 117:
                return "EVENT";
            case 118:
                return "STATS";
            case 119:
                return "LOG";
            case 120:
                return "PCAP";
            case 121:
                return "LLDP";
            case 122:
                return "STP";
            case 123:
                return "LACP";
            case 124:
                return "MPLS";
            case 125:
                return "PIM_SM";
            case 126:
                return "MLD";
            case 127:
                return "DVMRP";
            case 128:
                return "NETFLOW";
            case 129:
                return "SFLOW";
            case 130:
                return "IPFIX";
            case 131:
                return "ICMP_PING";
            case 132:
                return "ICMP_TRACE";
            case 133:
                return "X509";
            case 134:
                return "CRL";
            case 135:
                return "OCSP";
            case 136:
                return "PKI";
            case 137:
                return "DTLS";
            case 138:
                return "MAC_AUTH";
            case 139:
                return "DOT1X";
            case 141:
                return "NETSTAT";
            case 142:
                return "IPCONFIG";
            case 143:
                return "ROUTEPRINT";
            case 144:
                return "NSLOOKUP";
            case 145:
                return "ARPCMD";
            case 146:
                return "CURL";
            case 147:
                return "WGET";
            case 148:
                return "TELNET_CLIENT";
            case 149:
                return "NAT_HAIRPIN";
            case 150:
                return "NAT_HOLE";
            case 151:
                return "UPNP";
            case 152:
                return "PCP";
            case 153:
                return "IPSEC_IKE";
            case 154:
                return "IPSEC_ESP";
            case 155:
                return "IPSEC_AH";
            case 156:
                return "OPENVPN";
            case 157:
                return "WIREGUARD";
            case 158:
                return "L2TP";
            case 159:
                return "SSTP";
            case 160:
                return "IPS";
            default:
                return "NONE";
        }
    }

    // ===================== 核心移动方法 =====================

    // 在 DataPacket.java 中，替换整个 update() 方法

    public void update() {
        // 在 update() 方法开头添加
        // 调试：打印目标位置
        // 在 update() 方法的 DHCP 处理部分
        if (cartType.startsWith("DHCP") && !arrived && !dropped) {
            System.out.println("Updating " + cartType + " - pos: (" + x + "," + y +
                    "), stage: " + stage + ", timer: " + timer);
        }

        if (cartType.startsWith("DHCP")) {
            Point target = findTargetMachine(stage, cartType);
            if (target != null) {
                double dx = target.x - x;
                double dy = target.y - y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist <= speed) {
                    x = target.x;
                    y = target.y;
                    processStageCraft();

                    // 阶段推进逻辑
                    if (cartType.equals("DHCP_DISCOVER")) {
                        if (stage == 1) {
                            stage = 2;  // 下一站：DHCP_SERVER
                            timer = 5;
                        } else if (stage == 2) {
                            arrived = true;
                            System.out.println("DHCP_DISCOVER 到达服务器");
                        }
                    } else if (cartType.equals("DHCP_OFFER")) {
                        if (stage == 1) {
                            stage = 2;  // 下一站：PC_FACTORY
                            timer = 5;
                        } else if (stage == 2) {
                            arrived = true;
                            System.out.println("DHCP_OFFER 到达 PC");
                        }
                    } else if (cartType.equals("DHCP_REQUEST")) {
                        if (stage == 1) {
                            stage = 2;  // 下一站：DHCP_SERVER
                            timer = 5;
                        } else if (stage == 2) {
                            arrived = true;
                        }
                    } else if (cartType.equals("DHCP_ACK")) {
                        if (stage == 1) {
                            stage = 2;  // 下一站：PC_FACTORY
                            timer = 5;
                        } else if (stage == 2) {
                            arrived = true;
                            // DHCP 完成，标记 IP 已分配
//                            if (pcIpAssigned != null) {
//                                // 可以通过回调通知 GameEngine
//                            }
                        }
                    }
                    return;
                } else {
                    x += (dx / dist) * speed;
                    y += (dy / dist) * speed;
                    return;
                }
            } else {
                System.out.println("ERROR: Target is NULL for " + cartType + " stage=" + stage);
                arrived = true;
                return;
            }
        }
        // 特殊处理：DNS 包
        if (cartType != null && (cartType.startsWith("DNS_") || cartType.startsWith("DHCP"))) {
            Point target = findTargetMachine(stage, cartType);
            if (target != null) {
                double dx = target.x - x;
                double dy = target.y - y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist <= speed) {
                    x = target.x;
                    y = target.y;
                    processStageCraft();
                    // 根据包类型决定是否前进到下一阶段
                    if (cartType.equals("DNS_QUERY")) {
                        if (stage < 4) {
                            stage++;
                            timer = 2;  // 增加等待时间，让用户能看到
                        } else {
                            arrived = true;
                        }
                    } else if (cartType.equals("DNS_RESPONSE") ||
                            cartType.equals("DNS_RECURSION_ROOT_RESP") ||
                            cartType.equals("DNS_RECURSION_AUTH_RESP")) {
                        arrived = true;
                    } else if (cartType.startsWith("DHCP")) {
                        // DHCP 包处理
                        int maxStage = 1;
                        if (cartType.equals("DHCP_DISCOVER")) maxStage = 1;
                        else if (cartType.equals("DHCP_OFFER")) maxStage = 2;
                        else if (cartType.equals("DHCP_REQUEST")) maxStage = 1;
                        else if (cartType.equals("DHCP_ACK")) maxStage = 2;

                        if (stage < maxStage) {
                            stage++;
                            timer = 2;
                        } else {
                            arrived = true;
                        }
                    } else {
                        if (stage < 2) {
                            stage++;
                            timer = 2;
                        } else {
                            arrived = true;
                        }
                    }
                    return;
                } else {
                    x += (dx / dist) * speed;
                    y += (dy / dist) * speed;
                    return;
                }
            }
            return;
        }

        // ACK_PC 特殊处理
        if (cartType.equals("ACK_PC") && !returnTrip && stage == 2) {
            Point target = findBuildingCoords("RX_ST");
            if (target != null) {
                double dx = target.x - x;
                double dy = target.y - y;
                double dist = Math.sqrt(dx * dx + dy * dy);
                if (dist <= speed) {
                    x = target.x;
                    y = target.y;
                    arrived = true;
                    return;
                } else {
                    x += (dx / dist) * speed;
                    y += (dy / dist) * speed;
                    return;
                }
            }
        }

        // 正常数据包移动逻辑
        if (timer > 0) {
            timer--;
            return;
        }

        Point target;
        if (stage == -1) {
            target = pcFactory;
        } else {
            target = returnTrip ? pcFactory : findTargetMachine(stage, cartType);
        }

        if (target == null) {
            target = pcFactory;
        }
        if (target == null) return;

        double dx = target.x - x;
        double dy = target.y - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist <= speed) {
            x = target.x;
            y = target.y;

            // IP 分片处理
            if (stage == 15 && !returnTrip && !isControlFrame(cartType) && cartType.equals("DATA")) {
                final int MTU = 500;
                int packetSize = 1000;
                if (packetSize > MTU) {
                    int fragCount = (packetSize + MTU - 1) / MTU;
                    for (int i = 0; i < fragCount; i++) {
                        DataPacket fragment = new DataPacket(x, y, "IP_FRAGMENT", 0, factoryManager);
                        fragment.identification = ipIdentifierCounter;
                        fragment.fragmentOffset = i * (MTU / 8);
                        fragment.moreFragments = (i < fragCount - 1);
                        fragment.fragmentData = new byte[Math.min(MTU, packetSize - i * MTU)];
                        fragment.stage = 16;
                        fragment.ttl = this.ttl;
                        addPendingPacket(fragment);
                    }
                    ipIdentifierCounter++;
                    this.arrived = true;
                    return;
                }
            }

            processStageCraft();

            // 阶段推进
            if (!isDnsOrDhcp()) {
                // 根据包类型决定最大阶段
                int maxStage = 160;
                if (cartType.equals("SYN") || cartType.equals("SYN_ACK") ||
                        cartType.equals("ACK_PC") || cartType.equals("FIN_PC") ||
                        cartType.equals("FIN_SRV") || cartType.equals("FIN_ACK_SRV") ||
                        cartType.equals("LAST_ACK_PC") || cartType.equals("DATA_ACK")) {
                    // 控制帧，阶段较少
                    maxStage = 45;  // 到接收端应用层为止
                }

                if (stage < maxStage) {
                    stage++;
                    timer = 2;  // 短暂停留，让用户能看到
                } else {
                    arrived = true;
                }
            }
        } else {
            x += (dx / dist) * speed;
            y += (dy / dist) * speed;
        }
    }

    // ===================== processStageCraft 核心处理 =====================

    private void processStageCraft() {
        if (cartType != null && cartType.startsWith("DNS_")) {
            // DNS 包不需要任何封装处理
            return;
        }

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
                if (!returnTrip) {  // 修改这里：returnTrip -> returnTrip
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
                            this.dropped = true;  // 修改这里：isDropped -> dropped
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
                if (!isNatted && !returnTrip) {  // 修改这里：returnTrip -> returnTrip
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
                    this.dropped = true;  // 修改这里：isDropped -> dropped
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
                    boolean allowed = factoryManager.getFirewallFactory()
                            .allowOutbound(srcIp, dstIp, srcPort, dstPort, protocol);
                    if (!allowed) {
                        appendToConsole("【🔥 防火墙】: 出站包被阻断 " + srcIp + " → " + dstIp);
                        this.dropped = true;  // 修改这里
                        return;
                    } else {
                        appendToConsole("【🔥 防火墙】: 出站规则通过 ✅");
                    }
                }
                break;
            // ========== 公网路由器 ==========
            case 31: // ROUTER1
                ttl--;
                appendToConsole("【📡 ROUTER1】: TTL=" + ttl);
                if (ttl <= 0) {
                    appendToConsole("【⚠️ TTL 超时】: 数据包被丢弃");
                    this.dropped = true;
                    return;
                }
                break;
            case 32: // ROUTER2
                ttl--;
                appendToConsole("【📡 ROUTER2】: TTL=" + ttl);
                if (ttl <= 0) {
                    this.dropped = true;
                    return;
                }
                break;
            case 33: // ROUTER3
                ttl--;
                appendToConsole("【📡 ROUTER3】: TTL=" + ttl);
                if (ttl <= 0) {
                    this.dropped = true;
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
                        this.dropped = true;
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
                if (!useUdp && factoryManager.getTcpTimerFactory() != null && !returnTrip) {  // 改这里
                    long rto = factoryManager.getTcpTimerFactory().getRto();
                    appendToConsole(String.format("【⏱️ TCP 定时器】: RTO=%dms", rto));
                }
                break;

            case 54: // VLAN 标签
                if (!hasVlan && factoryManager.getVlanFactory() != null && returnTrip) {
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
                if (factoryManager.getTlsHandshakeFactory() != null && tlsEnabled && tlsState == TlsState.FINISHED) {  // 修改这里
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
                if (!hasPppoe && pppoeFactory != null && !returnTrip) {
                    hasPppoe = true;
                    byte[] padi = pppoeFactory.buildPADI(100);
                    byte[] lcp = pppoeFactory.buildLCPRequest();
                    appendToConsole(String.format("【🔌 PPPoE】: PADI + LCP 请求 (%d+%d字节)", padi.length, lcp.length));
                }
                break;

            case 65: // MACsec 安全加密
                if (!hasMacSec && macSecFactory != null && returnTrip) {
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
                if (!isReassembled && tcpReassemblyFactory != null && !returnTrip) {
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
                if (!hasIpsec && ipsecFactory != null && !returnTrip) {
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
//                    byte[] userCmd = ftpPacketFactory.buildFtpCommand("USER anonymous");
//                    byte[] passCmd = ftpPacketFactory.buildFtpCommand("PASS test@example.com");
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
                        this.dropped = true;  // 修改这里
                        return;
                    }
                    appendToConsole("【🛡️ WAF】: HTTP 内容检查通过");
                }
                break;

            case 98: // DDoS 缓解
                if (ddosMitigationFactory != null && getSrcIp() != null) {
                    if (!ddosMitigationFactory.checkTraffic(getSrcIp())) {
                        appendToConsole("【💥 DDoS缓解】: 源IP " + getSrcIp() + " 流量超限，已限流");
                        this.dropped = true;
                        return;
                    }
                }
                break;

            case 99: // 速率限制
                if (rateLimitFactory != null) {
                    String key = getSrcIp() + ":" + cartType;
                    if (!rateLimitFactory.allow(key)) {
                        appendToConsole("【⏱️ 速率限制】: " + key + " 超过限制，已丢弃");
                        this.dropped = true;
                        return;
                    }
                }
                break;

            case 100: // ACL 访问控制
                if (aclFactory != null && getSrcIp() != null && getDstIp() != null) {
                    boolean permit = aclFactory.match(getSrcIp(), getDstIp(), protocol);
                    if (!permit) {
                        appendToConsole("【🚫 ACL】: " + getSrcIp() + " → " + getDstIp() + " 被拒绝");
                        this.dropped = true;
                        return;
                    }
                }
                break;
// ===================== 新增 20 个核心工厂处理 stage 101-120 =====================

            case 101: // Socket 创建（应用层到传输层桥梁）
                if (!hasSocket && socketFactory != null && !returnTrip) {
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
                if (!hasMacLearning && macTableFactory != null && !returnTrip) {
                    hasMacLearning = true;
                    String inPort = "port_" + (stage % 10);
                    macTableFactory.learn(srcMac, inPort);
                    String outPort = macTableFactory.getPort(dstMac);
                    appendToConsole(String.format("【🔌 MAC表】: 学习 %s → %s, 出口 %s", srcMac, inPort, outPort));
                }
                break;

            case 104: // CAM 表（Cisco 交换机）
                if (camTableFactory != null && !returnTrip) {
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
                if (sessionTableFactory != null && !returnTrip) {
                    String proto = useUdp ? "UDP" : "TCP";
                    sessionTableFactory.createSession(getSrcIp(), srcPort, getDstIp(), dstPort, proto);
                    boolean exists = sessionTableFactory.exists(getSrcIp(), srcPort, getDstIp(), dstPort, proto);
                    appendToConsole(String.format("【💬 会话表】: %s:%d → %s:%d (%s) 存在:%s",
                            getSrcIp(), srcPort, getDstIp(), dstPort, proto, exists));
                }
                break;

            case 107: // NetFlow 流记录
                if (flowFactory != null && !returnTrip) {
                    String flowId = getSrcIp() + ":" + srcPort + "→" + getDstIp() + ":" + dstPort;
                    int bytes = 1500;
                    flowFactory.updateFlow(flowId, bytes);
                    FlowFactory.Flow flow = flowFactory.getFlow(flowId);
                    appendToConsole(String.format("【📊 NetFlow】: 流 %s, 包数=%d, 字节=%d",
                            flowId.substring(0, Math.min(20, flowId.length())), flow.packets, flow.bytes));
                }
                break;

            case 108: // 负载均衡器
                if (loadBalancerFactory != null && !returnTrip) {
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
                if (!hasMplsLabel && mplsLabelFactory != null && !returnTrip) {
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
                    if (!returnTrip) {
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
                    if (stage == 120 && !returnTrip && serverReceivedCount >= totalDataToTransmit) {
                        List<PacketCaptureFactory.PcapPacket> replay = packetCaptureFactory.replay();
                        appendToConsole(String.format("【🔄 回放】: 共 %d 个包可回放", replay.size()));
                    }
                }
                break;
            // ===================== 补充缺失的工厂处理 stage 121-160 =====================

// ===================== 链路层增强 (121-124) =====================
            case 121: // LLDP 链路层发现协议
                if (lldpFactory != null && !returnTrip) {
                    byte[] lldpFrame = lldpFactory.buildLldpFrame();
                    appendToConsole(String.format("【🔍 LLDP】: 发送链路发现报文 (%d字节)", lldpFrame.length));
                }
                break;

            case 122: // STP 生成树协议
                if (stpFactory != null && !returnTrip) {
                    byte[] bpdu = stpFactory.buildBpdu();
                    appendToConsole(String.format("【🌲 STP】: 发送 BPDU 报文，根桥 %s", stpFactory.rootBridge));
                }
                break;

            case 123: // LACP 链路聚合
                if (lacpFactory != null && !returnTrip) {
                    byte[] lacpPdu = lacpFactory.buildLacpPdu();
                    appendToConsole(String.format("【🔗 LACP】: 发送链路聚合报文，Actor Key=%d", lacpFactory.actorKey));
                }
                break;

            case 124: // MPLS 多协议标签交换
                if (mplsFactory != null && !returnTrip) {
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
                if (dvmrpFactory != null && !returnTrip) {
                    dvmrpFactory.addRoute("224.0.0.0/4", "10.0.0.1");
                    String upstream = dvmrpFactory.getUpstream("224.0.0.0/4");
                    appendToConsole(String.format("【🗺️ DVMRP】: 组播路由上游 %s", upstream));
                }
                break;

// ===================== 监控管理 (128-132) =====================
            case 128: // NetFlow 流量采集
                if (netFlowFactory != null && !returnTrip) {
                    netFlowFactory.addRecord(getSrcIp(), getDstIp(), srcPort, dstPort, 1);
                    appendToConsole(String.format("【📊 NetFlow】: 流记录 %s:%d→%s:%d",
                            getSrcIp(), srcPort, getDstIp(), dstPort));
                }
                break;

            case 129: // sFlow 采样流
                if (sflowFactory != null && !returnTrip) {
                    boolean sampled = sflowFactory.doSample(cartId);
                    byte[] sflowPacket = sflowFactory.buildSflowPacket(new byte[64]);
                    appendToConsole(String.format("【📈 sFlow】: 采样 %s, 报文大小=%d", sampled ? "是" : "否", sflowPacket.length));
                }
                break;

            case 130: // IPFIX 流导出
                if (ipfixFactory != null && !returnTrip) {
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
                if (dot1xFactory != null && !returnTrip) {
                    byte[] eapStart = dot1xFactory.buildEapStart();
                    dot1xFactory.setAuthResult(true);
                    appendToConsole(String.format("【🔌 802.1X】: 认证状态 %s", dot1xFactory.isAuthenticated() ? "已认证" : "未认证"));
                }
                break;

// ===================== 诊断工具 (141-148) =====================
            case 141: // netstat
                if (netstatFactory != null && !returnTrip) {
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
                if (natHolePunchFactory != null && !returnTrip) {
                    boolean punched = natHolePunchFactory.doHolePunch(getDstIp() + ":" + dstPort);
                    appendToConsole(String.format("【🕳️ NAT穿透】: %s:%d 打洞 %s", getDstIp(), dstPort, punched ? "成功" : "失败"));
                }
                break;

            case 151: // UPnP 端口映射
                if (upnpFactory != null && !returnTrip) {
                    upnpFactory.addPortMap(8080, 80, getSrcIp(), "TCP");
                    appendToConsole(String.format("【🔌 UPnP】: 端口映射 8080→%s:80", getSrcIp()));
                }
                break;

            case 152: // PCP 端口控制协议
                if (pcpFactory != null && !returnTrip) {
                    byte[] pcpMap = pcpFactory.buildMapRequest(80, getSrcIp());
                    appendToConsole(String.format("【📡 PCP】: 端口控制请求，生存时间=%ds", pcpFactory.lifetime));
                }
                break;

// ===================== VPN 隧道 (153-159) =====================
            case 153: // IKE 密钥交换
                if (ipsecIkeFactory != null && !returnTrip) {
                    byte[] ikeSa = ipsecIkeFactory.buildIkeSaInit();
                    appendToConsole("【🔑 IKE】: IKE SA 初始化");
                }
                break;

            case 154: // ESP 封装安全载荷
                if (ipsecEspFactory != null && !returnTrip) {
                    byte[] espPacket = ipsecEspFactory.wrapEsp(new byte[100]);
                    appendToConsole(String.format("【🔒 ESP】: ESP 封装, SPI=0x%08X", ipsecEspFactory.spi));
                }
                break;

            case 155: // AH 认证头
                if (ipsecAhFactory != null && !returnTrip) {
                    byte[] ahPacket = ipsecAhFactory.wrapAh(new byte[100]);
                    appendToConsole(String.format("【🔐 AH】: AH 认证, SPI=0x%08X", ipsecAhFactory.spi));
                }
                break;

            case 156: // OpenVPN
                if (openVpnFactory != null && !returnTrip) {
                    byte[] ovpnPacket = openVpnFactory.buildOpenVpnPacket(new byte[64]);
                    appendToConsole(String.format("【🔓 OpenVPN】: OpenVPN 数据包 (%d字节)", ovpnPacket.length));
                }
                break;

            case 157: // WireGuard
                if (wireguardFactory != null && !returnTrip) {
                    byte[] wgPacket = wireguardFactory.buildWgPacket(new byte[64]);
                    appendToConsole("【🔒 WireGuard】: WireGuard 加密隧道");
                }
                break;

            case 158: // L2TP
                if (l2tpFactory != null && !returnTrip) {
                    byte[] l2tpPacket = l2tpFactory.buildL2tpPacket(new byte[64]);
                    appendToConsole(String.format("【🔌 L2TP】: L2TP 隧道, TunnelID=%d", l2tpFactory.tunnelId));
                }
                break;

            case 159: // SSTP
                if (sstpFactory != null && !returnTrip) {
                    byte[] sstpControl = sstpFactory.buildSstpControl();
                    appendToConsole("【🌐 SSTP】: SSTP 控制报文");
                }
                break;

// ===================== 安全增强 (160) =====================
            case 160: // IPS 入侵防御
                if (ipsFactory != null && !returnTrip) {
                    boolean isAttack = ipsFactory.isAttack(getSrcIp(), new byte[64]);
                    if (isAttack) {
                        appendToConsole(String.format("【🛡️ IPS】: 检测到攻击来自 %s，已阻断", getSrcIp()));
                        this.dropped = true;
                        return;
                    }
                    appendToConsole("【🛡️ IPS】: 流量正常");
                }
                break;
        }
    }

    private void appendToConsole(String s) {
        if (appendToConsole != null) {
            appendToConsole.accept(s);
        }
    }

    // 添加 updateArpDisplay 方法
    private void updateArpDisplay() {
        if (updateArpDisplay != null) {
            updateArpDisplay.accept("");
        }
    }

    // 添加 updateNatDisplay 方法
    private void updateNatDisplay() {
        if (updateNatDisplay != null) {
            updateNatDisplay.accept("");
        }
    }

    // 添加 updateDnsDisplay 方法
    private void updateDnsDisplay() {
        if (updateDnsDisplay != null) {
            updateDnsDisplay.accept("");
        }
    }

    // 添加 updateTopLabel 方法
    private void updateTopLabel() {
        if (updateTopLabel != null) {
            updateTopLabel.accept("");
        }
    }
}