package com.never_give_up.automation.demo.adapter;

import com.never_give_up.automation.demo.factory.address.FiveTupleFactory;
import com.never_give_up.automation.demo.factory.address.IpAddressFactory;
import com.never_give_up.automation.demo.factory.address.MacAddressFactory;
import com.never_give_up.automation.demo.factory.address.PortFactory;
import com.never_give_up.automation.demo.factory.application.*;
import com.never_give_up.automation.demo.factory.application.http.*;
import com.never_give_up.automation.demo.factory.application.dhcp.DhcpLeaseFactory;
import com.never_give_up.automation.demo.factory.application.dns.DnsZoneFactory;
import com.never_give_up.automation.demo.factory.application.ftp.*;
import com.never_give_up.automation.demo.factory.attack.TransportAttackFactory;
import com.never_give_up.automation.demo.factory.balance.*;
import com.never_give_up.automation.demo.factory.capture.PacketCaptureFactory;
import com.never_give_up.automation.demo.factory.checksum.UdpChecksumFactory;
import com.never_give_up.automation.demo.factory.control.BandwidthFactory;
import com.never_give_up.automation.demo.factory.control.CongestionControlFactory;
import com.never_give_up.automation.demo.factory.control.SessionFactory;
import com.never_give_up.automation.demo.factory.device.NetworkDeviceFactory;
import com.never_give_up.automation.demo.factory.dhcp.DhcpFullPacketFactory;
import com.never_give_up.automation.demo.factory.dns.DnsRecursiveFactory;
import com.never_give_up.automation.demo.factory.event.EventFactory;
import com.never_give_up.automation.demo.factory.flow.FlowFactory;
import com.never_give_up.automation.demo.factory.function.*;
import com.never_give_up.automation.demo.factory.icmp.IcmpErrorFactory;
import com.never_give_up.automation.demo.factory.interfacee.NetworkInterfaceFactory;
import com.never_give_up.automation.demo.factory.link.*;
import com.never_give_up.automation.demo.factory.log.LogFactory;
import com.never_give_up.automation.demo.factory.multicast.*;
import com.never_give_up.automation.demo.factory.nd.NdpFactory;
import com.never_give_up.automation.demo.factory.network.IcmpPacketFactory;
import com.never_give_up.automation.demo.factory.network.IpFragmentFactory;
import com.never_give_up.automation.demo.factory.network.IpPacketFactory;
import com.never_give_up.automation.demo.factory.network.arp.ArpTableFactory;
import com.never_give_up.automation.demo.factory.network.ipv6.*;
import com.never_give_up.automation.demo.factory.option.IpOptionFactory;
import com.never_give_up.automation.demo.factory.option.TcpOptionFactory;
import com.never_give_up.automation.demo.factory.physical.BitStreamFactory;
import com.never_give_up.automation.demo.factory.physical.PhysicalChannelFactory;
import com.never_give_up.automation.demo.factory.qos.QosTrafficFactory;
import com.never_give_up.automation.demo.factory.qos.SchedulerFactory;
import com.never_give_up.automation.demo.factory.queue.PacketQueueFactory;
import com.never_give_up.automation.demo.factory.route.BgpPacketFactory;
import com.never_give_up.automation.demo.factory.route.ForwardingEngineFactory;
import com.never_give_up.automation.demo.factory.route.IpForwardFactory;
import com.never_give_up.automation.demo.factory.route.OspfPacketFactory;
import com.never_give_up.automation.demo.factory.security.FirewallRuleFactory;
import com.never_give_up.automation.demo.factory.security.crypto.*;
import com.never_give_up.automation.demo.factory.security.ipsec.IpsecFactory;
import com.never_give_up.automation.demo.factory.serialize.PacketSerializerFactory;
import com.never_give_up.automation.demo.factory.session.SessionTableFactory;
import com.never_give_up.automation.demo.factory.socket.SocketFactory;
import com.never_give_up.automation.demo.factory.stat.StatisticsFactory;
import com.never_give_up.automation.demo.factory.timer.TcpTimerFactory;
import com.never_give_up.automation.demo.factory.topology.LinkFactory;
import com.never_give_up.automation.demo.factory.topology.SubnetFactory;
import com.never_give_up.automation.demo.factory.transition.Nat64Factory;
import com.never_give_up.automation.demo.factory.transport.TcpPacketFactory;
import com.never_give_up.automation.demo.factory.transport.TcpReassemblyFactory;
import com.never_give_up.automation.demo.factory.transport.UdpPacketFactory;
import com.never_give_up.automation.demo.factory.transport.tcp.*;
import com.never_give_up.automation.demo.factory.tunnel.TunnelFactory;
import com.never_give_up.automation.demo.factory.vlan.VlanFactory;
import com.never_give_up.automation.demo.factory.window.TcpWindowFactory;
import com.never_give_up.automation.demo.factory.connection.TcpConnectionFactory;
import com.never_give_up.automation.demo.factory.tls.TlsHandshakeFactory;

// ===================== 本次新增：IPv6 协议栈 =====================
import com.never_give_up.automation.demo.factory.network.ipv6.Ipv6PacketFactory;
import com.never_give_up.automation.demo.factory.network.ipv6.Ipv6FragmentFactory;
import com.never_give_up.automation.demo.factory.network.ipv6.Ipv6OptionFactory;
import com.never_give_up.automation.demo.factory.network.ipv6.Ipv6NeighborDiscovery;

// ===================== 本次新增：多播路由协议 =====================
import com.never_give_up.automation.demo.factory.multicast.PimSmFactory;
import com.never_give_up.automation.demo.factory.multicast.MldFactory;
import com.never_give_up.automation.demo.factory.multicast.DvmrpFactory;

// ===================== 本次新增：TCP 状态机增强 =====================
import com.never_give_up.automation.demo.factory.transport.tcp.TcpKeepAliveFactory;
import com.never_give_up.automation.demo.factory.transport.tcp.TcpSackFactory;
import com.never_give_up.automation.demo.factory.transport.tcp.TcpEcnFactory;
import com.never_give_up.automation.demo.factory.transport.tcp.TcpFastOpenFactory;

// ===================== 本次新增：链路层协议增强 =====================
import com.never_give_up.automation.demo.factory.link.LldpFactory;
import com.never_give_up.automation.demo.factory.link.StpFactory;
import com.never_give_up.automation.demo.factory.link.LACPFactory;
import com.never_give_up.automation.demo.factory.link.MplsFactory;

// ===================== 本次新增：应用层协议全量 =====================
import com.never_give_up.automation.demo.factory.application.mail.SmtpPacketFactory;
import com.never_give_up.automation.demo.factory.application.mail.Pop3PacketFactory;
import com.never_give_up.automation.demo.factory.application.mail.ImapPacketFactory;
import com.never_give_up.automation.demo.factory.application.ssh.SshPacketFactory;
import com.never_give_up.automation.demo.factory.application.telnet.TelnetPacketFactory;
import com.never_give_up.automation.demo.factory.application.rtp.RtpPacketFactory;
import com.never_give_up.automation.demo.factory.application.rtp.RtcpPacketFactory;
import com.never_give_up.automation.demo.factory.application.sip.SipPacketFactory;
import com.never_give_up.automation.demo.factory.application.auth.RadiusPacketFactory;
import com.never_give_up.automation.demo.factory.application.auth.DiameterPacketFactory;
import com.never_give_up.automation.demo.factory.application.ldap.LdapPacketFactory;

// ===================== 本次新增：NAT 功能增强 =====================
import com.never_give_up.automation.demo.factory.nat.NatHairpinningFactory;
import com.never_give_up.automation.demo.factory.nat.NatHolePunchFactory;
import com.never_give_up.automation.demo.factory.nat.UpnpFactory;
import com.never_give_up.automation.demo.factory.nat.PcpFactory;

// ===================== 本次新增：负载均衡 =====================
import com.never_give_up.automation.demo.factory.balance.LbRoundRobinFactory;
import com.never_give_up.automation.demo.factory.balance.LbLeastConnFactory;
import com.never_give_up.automation.demo.factory.balance.LbIpHashFactory;
import com.never_give_up.automation.demo.factory.balance.LbHealthCheckFactory;

// ===================== 本次新增：网络监控与管理 =====================
import com.never_give_up.automation.demo.factory.monitor.NetFlowFactory;
import com.never_give_up.automation.demo.factory.monitor.SflowFactory;
import com.never_give_up.automation.demo.factory.monitor.IpfixFactory;
import com.never_give_up.automation.demo.factory.icmp.IcmpPingFactory;
import com.never_give_up.automation.demo.factory.icmp.IcmpTracerouteFactory;

// ===================== 本次新增：VPN 与隧道协议 =====================
import com.never_give_up.automation.demo.factory.security.ipsec.IpsecIkeFactory;
import com.never_give_up.automation.demo.factory.security.ipsec.IpsecEspFactory;
import com.never_give_up.automation.demo.factory.security.ipsec.IpsecAhFactory;
import com.never_give_up.automation.demo.factory.vpn.OpenVpnFactory;
import com.never_give_up.automation.demo.factory.vpn.WireguardFactory;
import com.never_give_up.automation.demo.factory.vpn.L2tpFactory;
import com.never_give_up.automation.demo.factory.vpn.SstpFactory;

// ===================== 本次新增：防火墙与安全增强 =====================
import com.never_give_up.automation.demo.factory.security.DpiFactory;
import com.never_give_up.automation.demo.factory.security.IpsFactory;
import com.never_give_up.automation.demo.factory.security.WafFactory;
import com.never_give_up.automation.demo.factory.security.DdosMitigationFactory;
import com.never_give_up.automation.demo.factory.security.RateLimitFactory;

// ===================== 本次新增：加密与证书 =====================
import com.never_give_up.automation.demo.factory.security.crypto.X509Factory;
import com.never_give_up.automation.demo.factory.security.crypto.CrlFactory;
import com.never_give_up.automation.demo.factory.security.crypto.OcspFactory;
import com.never_give_up.automation.demo.factory.security.crypto.PkiFactory;
import com.never_give_up.automation.demo.factory.security.tls.DtlsFactory;

// ===================== 本次新增：访问控制 =====================
import com.never_give_up.automation.demo.factory.security.AclFactory;
import com.never_give_up.automation.demo.factory.security.MacAuthFactory;
import com.never_give_up.automation.demo.factory.security.Dot1xFactory;

// ===================== 本次新增：网络诊断工具 =====================
import com.never_give_up.automation.demo.factory.tool.NetstatFactory;
import com.never_give_up.automation.demo.factory.tool.IpconfigFactory;
import com.never_give_up.automation.demo.factory.tool.RoutePrintFactory;
import com.never_give_up.automation.demo.factory.tool.NslookupFactory;
import com.never_give_up.automation.demo.factory.tool.ArpCommandFactory;
import com.never_give_up.automation.demo.factory.tool.TelnetClientFactory;
import com.never_give_up.automation.demo.factory.tool.CurlFactory;
import com.never_give_up.automation.demo.factory.tool.WgetFactory;

import lombok.Data;

@Data
public class FactoryManager {
    // ===================== 原有基础工厂 =====================
    private ChecksumFactory checksumFactory;
    private final IpAddressFactory ipAddressFactory;
    private final MacAddressFactory macFactory;
    private final PortFactory portFactory;
    private final TcpPacketFactory tcpFactory;
    private final UdpPacketFactory udpFactory;
    private final IpPacketFactory ipPacketFactory;
    private final EthernetFactory etherFactory;
    private final ArpPacketFactory arpFactory;
    private final DnsPacketFactory dnsFactory;
    private final DhcpPacketFactory dhcpFactory;
    private final HttpPacketFactory httpFactory;
    private final TlsPacketFactory tlsFactory;
    private final ArpCacheFactory arpCache;
    private final DnsCacheFactory dnsCache;
    private final NatMappingFactory natFactory;
    private final RouteTableFactory routeTable;
    private final CongestionControlFactory congestionControl;
    private final IpFragmentFactory ipFragmentFactory;
    private final IcmpPacketFactory icmpFactory;
    private final FiveTupleFactory fiveTupleFactory;
    private final BandwidthFactory bandwidthFactory;
    private final SessionFactory sessionFactory;
    private final NetworkDeviceFactory deviceFactory;
    private final LinkFactory linkFactory;
    private final SubnetFactory subnetFactory;
    private final PacketQueueFactory queueFactory;
    private final FirewallRuleFactory firewallFactory;
    private LinkLayerFactory linkLayerFactory;

    // ===================== 原有前18个扩展工厂 =====================
    private final IpOptionFactory ipOptionFactory;
    private final TcpOptionFactory tcpOptionFactory;
    private final UdpChecksumFactory udpChecksumFactory;
    private final IcmpErrorFactory icmpErrorFactory;
    private final EthernetPaddingFactory ethernetPaddingFactory;
    private final IpForwardFactory ipForwardFactory;
    private final TcpTimerFactory tcpTimerFactory;
    private final TcpWindowFactory tcpWindowFactory;
    private final TcpConnectionFactory tcpConnectionFactory;
    private final DnsRecursiveFactory dnsRecursiveFactory;
    private final DhcpFullPacketFactory dhcpFullPacketFactory;
    private final TlsHandshakeFactory tlsHandshakeFactory;
    private final PacketSerializerFactory packetSerializerFactory;
    private final NetworkInterfaceFactory networkInterfaceFactory;
    private final VlanFactory vlanFactory;
    private final TunnelFactory tunnelFactory;
    private final IgmpFactory igmpFactory;
    private final NdpFactory ndpFactory;

    // ===================== 原有新增14个超级工厂 =====================
    private final BitStreamFactory bitStreamFactory;
    private final PhysicalChannelFactory physicalChannelFactory;
    private final PppoeFactory pppoeFactory;
    private final MacSecFactory macSecFactory;
    private final OspfPacketFactory ospfPacketFactory;
    private final BgpPacketFactory bgpPacketFactory;
    private final QosTrafficFactory qosTrafficFactory;
    private final Nat64Factory nat64Factory;
    private final TcpReassemblyFactory tcpReassemblyFactory;
    private final TransportAttackFactory transportAttackFactory;
    private final NtpPacketFactory ntpPacketFactory;
    private final SnmpPacketFactory snmpPacketFactory;
    private final Http23PacketFactory http23PacketFactory;
    private final IpsecFactory ipsecFactory;

    // ===================== 本次新增：IPv6 协议栈 =====================
    private final Ipv6PacketFactory ipv6PacketFactory;
    private final Ipv6FragmentFactory ipv6FragmentFactory;
    private final Ipv6OptionFactory ipv6OptionFactory;
    private final Ipv6NeighborDiscovery ipv6NeighborDiscovery;

    // ===================== 本次新增：多播路由协议 =====================
    private final PimSmFactory pimSmFactory;
    private final MldFactory mldFactory;
    private final DvmrpFactory dvmrpFactory;

    // ===================== 本次新增：TCP 状态机增强 =====================
    private final TcpKeepAliveFactory tcpKeepAliveFactory;
    private final TcpSackFactory tcpSackFactory;
    private final TcpEcnFactory tcpEcnFactory;
    private final TcpFastOpenFactory tcpFastOpenFactory;

    // ===================== 本次新增：链路层协议增强 =====================
    private final LldpFactory lldpFactory;
    private final StpFactory stpFactory;
    private final LACPFactory lacpFactory;
    private final MplsFactory mplsFactory;

    // ===================== 本次新增：应用层协议全量 =====================
    private final FtpPacketFactory ftpPacketFactory;
    private final SmtpPacketFactory smtpPacketFactory;
    private final Pop3PacketFactory pop3PacketFactory;
    private final ImapPacketFactory imapPacketFactory;
    private final SshPacketFactory sshPacketFactory;
    private final TelnetPacketFactory telnetPacketFactory;
    private final RtpPacketFactory rtpPacketFactory;
    private final RtcpPacketFactory rtcpPacketFactory;
    private final SipPacketFactory sipPacketFactory;
    private final RadiusPacketFactory radiusPacketFactory;
    private final DiameterPacketFactory diameterPacketFactory;
    private final LdapPacketFactory ldapPacketFactory;

    // ===================== 本次新增：NAT 功能增强 =====================
    private final NatHairpinningFactory natHairpinningFactory;
    private final NatHolePunchFactory natHolePunchFactory;
    private final UpnpFactory upnpFactory;
    private final PcpFactory pcpFactory;

    // ===================== 本次新增：负载均衡 =====================
    private final LbRoundRobinFactory lbRoundRobinFactory;
    private final LbLeastConnFactory lbLeastConnFactory;
    private final LbIpHashFactory lbIpHashFactory;
    private final LbHealthCheckFactory lbHealthCheckFactory;

    // ===================== 本次新增：网络监控与管理 =====================
    private final NetFlowFactory netFlowFactory;
    private final SflowFactory sflowFactory;
    private final IpfixFactory ipfixFactory;
    private final IcmpPingFactory icmpPingFactory;
    private final IcmpTracerouteFactory icmpTracerouteFactory;

    // ===================== 本次新增：VPN 与隧道协议 =====================
    private final IpsecIkeFactory ipsecIkeFactory;
    private final IpsecEspFactory ipsecEspFactory;
    private final IpsecAhFactory ipsecAhFactory;
    private final OpenVpnFactory openVpnFactory;
    private final WireguardFactory wireguardFactory;
    private final L2tpFactory l2tpFactory;
    private final SstpFactory sstpFactory;

    // ===================== 本次新增：防火墙与安全增强 =====================
    private final DpiFactory dpiFactory;
    private final IpsFactory ipsFactory;
    private final WafFactory wafFactory;
    private final DdosMitigationFactory ddosMitigationFactory;
    private final RateLimitFactory rateLimitFactory;

    // ===================== 本次新增：加密与证书 =====================
    private final X509Factory x509Factory;
    private final CrlFactory crlFactory;
    private final OcspFactory ocspFactory;
    private final PkiFactory pkiFactory;
    private final DtlsFactory dtlsFactory;

    // ===================== 本次新增：访问控制 =====================
    private final AclFactory aclFactory;
    private final MacAuthFactory macAuthFactory;
    private final Dot1xFactory dot1xFactory;

    // ===================== 本次新增：网络诊断工具 =====================
    private final NetstatFactory netstatFactory;
    private final IpconfigFactory ipconfigFactory;
    private final RoutePrintFactory routePrintFactory;
    private final NslookupFactory nslookupFactory;
    private final ArpCommandFactory arpCommandFactory;
    private final TelnetClientFactory telnetClientFactory;
    private final CurlFactory curlFactory;
    private final WgetFactory wgetFactory;

    // ===================== 你新增的 20 个核心工厂 =====================
    private final SocketFactory socketFactory;
    private final TcpStateMachineFactory tcpStateMachineFactory;
    private final MacTableFactory macTableFactory;
    private final CamTableFactory camTableFactory;
    private final ForwardingEngineFactory forwardingEngineFactory;
    private final SessionTableFactory sessionTableFactory;
    private final FlowFactory flowFactory;
    private final LoadBalancerFactory loadBalancerFactory;
    private final SchedulerFactory schedulerFactory;
    private final DnsZoneFactory dnsZoneFactory;
    private final DhcpLeaseFactory dhcpLeaseFactory;
    private final ArpTableFactory arpTableFactory;
    private final NeighborTableFactory neighborTableFactory;
    private final MulticastRoutingFactory multicastRoutingFactory;
    private final MplsLabelFactory mplsLabelFactory;
    private final CertificateStoreFactory certificateStoreFactory;
    private final EventFactory eventFactory;
    private final StatisticsFactory statisticsFactory;
    private final LogFactory logFactory;
    private final PacketCaptureFactory packetCaptureFactory;

    public FactoryManager() {
        // ===================== 原有基础工厂初始化 =====================
        this.ipAddressFactory = new IpAddressFactory();
        this.macFactory = new MacAddressFactory();
        this.portFactory = new PortFactory();
        this.ipFragmentFactory = new IpFragmentFactory();
        this.checksumFactory = new ChecksumFactory();
        this.icmpFactory = new IcmpPacketFactory();
        this.fiveTupleFactory = new FiveTupleFactory(ipAddressFactory, portFactory);
        this.bandwidthFactory = new BandwidthFactory(1000, 50, 0.01);
        this.sessionFactory = new SessionFactory();
        this.tcpFactory = new TcpPacketFactory();
        this.udpFactory = new UdpPacketFactory();
        this.ipPacketFactory = new IpPacketFactory();
        this.etherFactory = new EthernetFactory();
        this.arpFactory = new ArpPacketFactory();
        this.dnsFactory = new DnsPacketFactory();
        this.dhcpFactory = new DhcpPacketFactory();
        this.httpFactory = new HttpPacketFactory();
        this.tlsFactory = new TlsPacketFactory();
        this.arpCache = new ArpCacheFactory();
        this.dnsCache = new DnsCacheFactory();
        this.natFactory = new NatMappingFactory("8.8.8.8");
        this.routeTable = new RouteTableFactory();
        this.congestionControl = new CongestionControlFactory();
        this.deviceFactory = new NetworkDeviceFactory();
        this.linkFactory = new LinkFactory();
        this.subnetFactory = new SubnetFactory();
        this.queueFactory = new PacketQueueFactory();
        this.firewallFactory = new FirewallRuleFactory();
        linkLayerFactory = new LinkLayerFactory();

        // ===================== 原有前18个工厂初始化 =====================
        this.ipOptionFactory = new IpOptionFactory();
        this.tcpOptionFactory = new TcpOptionFactory();
        this.udpChecksumFactory = new UdpChecksumFactory();
        this.icmpErrorFactory = new IcmpErrorFactory();
        this.ethernetPaddingFactory = new EthernetPaddingFactory();
        this.ipForwardFactory = new IpForwardFactory();
        this.tcpTimerFactory = new TcpTimerFactory();
        this.tcpWindowFactory = new TcpWindowFactory();
        this.tcpConnectionFactory = new TcpConnectionFactory();
        this.dnsRecursiveFactory = new DnsRecursiveFactory();
        this.dhcpFullPacketFactory = new DhcpFullPacketFactory();
        this.tlsHandshakeFactory = new TlsHandshakeFactory();
        this.packetSerializerFactory = new PacketSerializerFactory();
        this.networkInterfaceFactory = new NetworkInterfaceFactory();
        this.vlanFactory = new VlanFactory();
        this.tunnelFactory = new TunnelFactory();
        this.igmpFactory = new IgmpFactory();
        this.ndpFactory = new NdpFactory();

        // ===================== 原有新增14个工厂初始化 =====================
        this.bitStreamFactory = new BitStreamFactory();
        this.physicalChannelFactory = new PhysicalChannelFactory();
        this.pppoeFactory = new PppoeFactory();
        this.macSecFactory = new MacSecFactory();
        this.ospfPacketFactory = new OspfPacketFactory();
        this.bgpPacketFactory = new BgpPacketFactory();
        this.qosTrafficFactory = new QosTrafficFactory();
        this.nat64Factory = new Nat64Factory();
        this.tcpReassemblyFactory = new TcpReassemblyFactory();
        this.transportAttackFactory = new TransportAttackFactory();
        this.ntpPacketFactory = new NtpPacketFactory();
        this.snmpPacketFactory = new SnmpPacketFactory();
        this.http23PacketFactory = new Http23PacketFactory();
        this.ipsecFactory = new IpsecFactory();

        // ===================== 本次新增：IPv6 协议栈初始化 =====================
        this.ipv6PacketFactory = new Ipv6PacketFactory();
        this.ipv6FragmentFactory = new Ipv6FragmentFactory();
        this.ipv6OptionFactory = new Ipv6OptionFactory();
        this.ipv6NeighborDiscovery = new Ipv6NeighborDiscovery();

        // ===================== 本次新增：多播路由初始化 =====================
        this.pimSmFactory = new PimSmFactory();
        this.mldFactory = new MldFactory();
        this.dvmrpFactory = new DvmrpFactory();

        // ===================== 本次新增：TCP 增强初始化 =====================
        this.tcpKeepAliveFactory = new TcpKeepAliveFactory();
        this.tcpSackFactory = new TcpSackFactory();
        this.tcpEcnFactory = new TcpEcnFactory();
        this.tcpFastOpenFactory = new TcpFastOpenFactory();

        // ===================== 本次新增：链路层增强初始化 =====================
        this.lldpFactory = new LldpFactory();
        this.stpFactory = new StpFactory();
        this.lacpFactory = new LACPFactory();
        this.mplsFactory = new MplsFactory();

        // ===================== 本次新增：应用层协议初始化 =====================
        this.ftpPacketFactory = new FtpPacketFactory();
        this.smtpPacketFactory = new SmtpPacketFactory();
        this.pop3PacketFactory = new Pop3PacketFactory();
        this.imapPacketFactory = new ImapPacketFactory();
        this.sshPacketFactory = new SshPacketFactory();
        this.telnetPacketFactory = new TelnetPacketFactory();
        this.rtpPacketFactory = new RtpPacketFactory();
        this.rtcpPacketFactory = new RtcpPacketFactory();
        this.sipPacketFactory = new SipPacketFactory();
        this.radiusPacketFactory = new RadiusPacketFactory();
        this.diameterPacketFactory = new DiameterPacketFactory();
        this.ldapPacketFactory = new LdapPacketFactory();

        // ===================== 本次新增：NAT 增强初始化 =====================
        this.natHairpinningFactory = new NatHairpinningFactory();
        this.natHolePunchFactory = new NatHolePunchFactory();
        this.upnpFactory = new UpnpFactory();
        this.pcpFactory = new PcpFactory();

        // ===================== 本次新增：负载均衡初始化 =====================
        this.lbRoundRobinFactory = new LbRoundRobinFactory();
        this.lbLeastConnFactory = new LbLeastConnFactory();
        this.lbIpHashFactory = new LbIpHashFactory();
        this.lbHealthCheckFactory = new LbHealthCheckFactory();

        // ===================== 本次新增：监控管理初始化 =====================
        this.netFlowFactory = new NetFlowFactory();
        this.sflowFactory = new SflowFactory();
        this.ipfixFactory = new IpfixFactory();
        this.icmpPingFactory = new IcmpPingFactory();
        this.icmpTracerouteFactory = new IcmpTracerouteFactory();

        // ===================== 本次新增：VPN 隧道初始化 =====================
        this.ipsecIkeFactory = new IpsecIkeFactory();
        this.ipsecEspFactory = new IpsecEspFactory();
        this.ipsecAhFactory = new IpsecAhFactory();
        this.openVpnFactory = new OpenVpnFactory();
        this.wireguardFactory = new WireguardFactory();
        this.l2tpFactory = new L2tpFactory();
        this.sstpFactory = new SstpFactory();

        // ===================== 本次新增：安全防火墙初始化 =====================
        this.dpiFactory = new DpiFactory();
        this.ipsFactory = new IpsFactory();
        this.wafFactory = new WafFactory();
        this.ddosMitigationFactory = new DdosMitigationFactory();
        this.rateLimitFactory = new RateLimitFactory();

        // ===================== 本次新增：加密证书初始化 =====================
        this.x509Factory = new X509Factory();
        this.crlFactory = new CrlFactory();
        this.ocspFactory = new OcspFactory();
        this.pkiFactory = new PkiFactory();
        this.dtlsFactory = new DtlsFactory();

        // ===================== 本次新增：访问控制初始化 =====================
        this.aclFactory = new AclFactory();
        this.macAuthFactory = new MacAuthFactory();
        this.dot1xFactory = new Dot1xFactory();

        // ===================== 本次新增：诊断工具初始化 =====================
        this.netstatFactory = new NetstatFactory();
        this.ipconfigFactory = new IpconfigFactory();
        this.routePrintFactory = new RoutePrintFactory();
        this.nslookupFactory = new NslookupFactory();
        this.arpCommandFactory = new ArpCommandFactory();
        this.telnetClientFactory = new TelnetClientFactory();
        this.curlFactory = new CurlFactory();
        this.wgetFactory = new WgetFactory();

        this.socketFactory = new SocketFactory();
        this.tcpStateMachineFactory = new TcpStateMachineFactory();
        this.macTableFactory = new MacTableFactory();
        this.camTableFactory = new CamTableFactory();
        this.forwardingEngineFactory = new ForwardingEngineFactory();
        this.sessionTableFactory = new SessionTableFactory();
        this.flowFactory = new FlowFactory();
        this.loadBalancerFactory = new LoadBalancerFactory();
        this.schedulerFactory = new SchedulerFactory();
        this.dnsZoneFactory = new DnsZoneFactory();
        this.dhcpLeaseFactory = new DhcpLeaseFactory();
        this.arpTableFactory = new ArpTableFactory();
        this.neighborTableFactory = new NeighborTableFactory();
        this.multicastRoutingFactory = new MulticastRoutingFactory();
        this.mplsLabelFactory = new MplsLabelFactory();
        this.certificateStoreFactory = new CertificateStoreFactory();
        this.eventFactory = new EventFactory();
        this.statisticsFactory = new StatisticsFactory();
        this.logFactory = new LogFactory();
        this.packetCaptureFactory = new PacketCaptureFactory();
    }

    /**
     * 重置所有工厂状态（测试用）
     */
    public void reset() {
        tcpFactory.reset();
        ipPacketFactory.reset();
        arpCache.clear();
        dnsCache.clear();
        natFactory.clear();
        routeTable.clear();
        congestionControl.reset();
        deviceFactory.reset();
        linkFactory.reset();
        subnetFactory.reset();
        queueFactory.reset();
    }
// 在 FactoryManager.java 中添加

    // ===================== FTP 子工厂 Getter =====================
    public FtpCommandFactory getFtpCommandFactory() {
        return ftpPacketFactory != null ? ftpPacketFactory.getCommandFactory() : null;
    }

    public FtpResponseParser getFtpResponseParser() {
        return ftpPacketFactory != null ? ftpPacketFactory.getResponseParser() : null;
    }

    public FtpDataChannelFactory getFtpDataChannelFactory() {
        return ftpPacketFactory != null ? ftpPacketFactory.getDataChannelFactory() : null;
    }

    public FtpAuthFactory getFtpAuthFactory() {
        return ftpPacketFactory != null ? ftpPacketFactory.getAuthFactory() : null;
    }
    // ===================== Getter 方法 =====================

    public ChecksumFactory getChecksumFactory() {
        return checksumFactory;
    }

    public IpAddressFactory getIpAddressFactory() {
        return ipAddressFactory;
    }

    public MacAddressFactory getMacFactory() {
        return macFactory;
    }

    public PortFactory getPortFactory() {
        return portFactory;
    }

    public TcpPacketFactory getTcpPacketFactory() {
        return tcpFactory;
    }

    public UdpPacketFactory getUdpPacketFactory() {
        return udpFactory;
    }

    public IpPacketFactory getIpPacketFactory() {
        return ipPacketFactory;
    }

    public EthernetFactory getEthernetFactory() {
        return etherFactory;
    }

    public ArpPacketFactory getArpFactory() {
        return arpFactory;
    }

    public DnsPacketFactory getDnsFactory() {
        return dnsFactory;
    }

    public DhcpPacketFactory getDhcpFactory() {
        return dhcpFactory;
    }

    public HttpPacketFactory getHttpFactory() {
        return httpFactory;
    }

    // ===================== HTTP 子工厂 Getter =====================
    public HttpRequestFactory getHttpRequestFactory() {
        return httpFactory != null ? httpFactory.getRequestFactory() : null;
    }

    public HttpResponseFactory getHttpResponseFactory() {
        return httpFactory != null ? httpFactory.getResponseFactory() : null;
    }

    public HttpHeaderFactory getHttpHeaderFactory() {
        return httpFactory != null ? httpFactory.getHeaderFactory() : null;
    }

    public HttpBodyFactory getHttpBodyFactory() {
        return httpFactory != null ? httpFactory.getBodyFactory() : null;
    }

    public HttpCookieFactory getHttpCookieFactory() {
        return httpFactory != null ? httpFactory.getCookieFactory() : null;
    }

    public HttpAuthFactory getHttpAuthFactory() {
        return httpFactory != null ? httpFactory.getAuthFactory() : null;
    }

    public HttpCacheFactory getHttpCacheFactory() {
        return httpFactory != null ? httpFactory.getCacheFactory() : null;
    }

    public HttpChunkedFactory getHttpChunkedFactory() {
        return httpFactory != null ? httpFactory.getChunkedFactory() : null;
    }

    public Http2FrameFactory getHttp2FrameFactory() {
        return httpFactory != null ? httpFactory.getHttp2FrameFactory() : null;
    }

    public Http2SettingsFactory getHttp2SettingsFactory() {
        return httpFactory != null ? httpFactory.getHttp2SettingsFactory() : null;
    }

    public Http2StreamFactory getHttp2StreamFactory() {
        return httpFactory != null ? httpFactory.getHttp2StreamFactory() : null;
    }

    public TlsPacketFactory getTlsFactory() {
        return tlsFactory;
    }

    public ArpCacheFactory getArpCache() {
        return arpCache;
    }

    public DnsCacheFactory getDnsCache() {
        return dnsCache;
    }

    public NatMappingFactory getNatFactory() {
        return natFactory;
    }

    public RouteTableFactory getRouteTable() {
        return routeTable;
    }

    public CongestionControlFactory getCongestionControl() {
        return congestionControl;
    }

    public IpFragmentFactory getIpFragmentFactory() {
        return ipFragmentFactory;
    }

    public IcmpPacketFactory getIcmpFactory() {
        return icmpFactory;
    }

    public FiveTupleFactory getFiveTupleFactory() {
        return fiveTupleFactory;
    }

    public BandwidthFactory getBandwidthFactory() {
        return bandwidthFactory;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public NetworkDeviceFactory getDeviceFactory() {
        return deviceFactory;
    }

    public LinkFactory getLinkFactory() {
        return linkFactory;
    }

    public SubnetFactory getSubnetFactory() {
        return subnetFactory;
    }

    public PacketQueueFactory getQueueFactory() {
        return queueFactory;
    }

    public FirewallRuleFactory getFirewallFactory() {
        return firewallFactory;
    }

    public LinkLayerFactory getLinkLayerFactory() {
        return linkLayerFactory;
    }

    public IpOptionFactory getIpOptionFactory() {
        return ipOptionFactory;
    }

    public TcpOptionFactory getTcpOptionFactory() {
        return tcpOptionFactory;
    }

    public UdpChecksumFactory getUdpChecksumFactory() {
        return udpChecksumFactory;
    }

    public IcmpErrorFactory getIcmpErrorFactory() {
        return icmpErrorFactory;
    }

    public EthernetPaddingFactory getEthernetPaddingFactory() {
        return ethernetPaddingFactory;
    }

    public IpForwardFactory getIpForwardFactory() {
        return ipForwardFactory;
    }

    public TcpTimerFactory getTcpTimerFactory() {
        return tcpTimerFactory;
    }

    public TcpWindowFactory getTcpWindowFactory() {
        return tcpWindowFactory;
    }

    public TcpConnectionFactory getTcpConnectionFactory() {
        return tcpConnectionFactory;
    }

    public DnsRecursiveFactory getDnsRecursiveFactory() {
        return dnsRecursiveFactory;
    }

    public DhcpFullPacketFactory getDhcpFullPacketFactory() {
        return dhcpFullPacketFactory;
    }

    public TlsHandshakeFactory getTlsHandshakeFactory() {
        return tlsHandshakeFactory;
    }

    public PacketSerializerFactory getPacketSerializerFactory() {
        return packetSerializerFactory;
    }

    public NetworkInterfaceFactory getNetworkInterfaceFactory() {
        return networkInterfaceFactory;
    }

    public VlanFactory getVlanFactory() {
        return vlanFactory;
    }

    public TunnelFactory getTunnelFactory() {
        return tunnelFactory;
    }

    public IgmpFactory getIgmpFactory() {
        return igmpFactory;
    }

    public NdpFactory getNdpFactory() {
        return ndpFactory;
    }

// ===================== 14个新工厂 Getter =====================

    public BitStreamFactory getBitStreamFactory() {
        return bitStreamFactory;
    }

    public PhysicalChannelFactory getPhysicalChannelFactory() {
        return physicalChannelFactory;
    }

    public PppoeFactory getPppoeFactory() {
        return pppoeFactory;
    }

    public MacSecFactory getMacSecFactory() {
        return macSecFactory;
    }

    public OspfPacketFactory getOspfPacketFactory() {
        return ospfPacketFactory;
    }

    public BgpPacketFactory getBgpPacketFactory() {
        return bgpPacketFactory;
    }

    public QosTrafficFactory getQosTrafficFactory() {
        return qosTrafficFactory;
    }

    public Nat64Factory getNat64Factory() {
        return nat64Factory;
    }

    public TcpReassemblyFactory getTcpReassemblyFactory() {
        return tcpReassemblyFactory;
    }

    public TransportAttackFactory getTransportAttackFactory() {
        return transportAttackFactory;
    }

    public NtpPacketFactory getNtpPacketFactory() {
        return ntpPacketFactory;
    }

    public SnmpPacketFactory getSnmpPacketFactory() {
        return snmpPacketFactory;
    }

    public Http23PacketFactory getHttp23PacketFactory() {
        return http23PacketFactory;
    }

    public IpsecFactory getIpsecFactory() {
        return ipsecFactory;
    }

// ===================== IPv6 协议栈 Getter =====================

    public Ipv6PacketFactory getIpv6PacketFactory() {
        return ipv6PacketFactory;
    }

    public Ipv6FragmentFactory getIpv6FragmentFactory() {
        return ipv6FragmentFactory;
    }

    public Ipv6OptionFactory getIpv6OptionFactory() {
        return ipv6OptionFactory;
    }

    public Ipv6NeighborDiscovery getIpv6NeighborDiscovery() {
        return ipv6NeighborDiscovery;
    }

// ===================== 多播路由 Getter =====================

    public PimSmFactory getPimSmFactory() {
        return pimSmFactory;
    }

    public MldFactory getMldFactory() {
        return mldFactory;
    }

    public DvmrpFactory getDvmrpFactory() {
        return dvmrpFactory;
    }

// ===================== TCP 增强 Getter =====================

    public TcpKeepAliveFactory getTcpKeepAliveFactory() {
        return tcpKeepAliveFactory;
    }

    public TcpSackFactory getTcpSackFactory() {
        return tcpSackFactory;
    }

    public TcpEcnFactory getTcpEcnFactory() {
        return tcpEcnFactory;
    }

    public TcpFastOpenFactory getTcpFastOpenFactory() {
        return tcpFastOpenFactory;
    }

// ===================== 链路层增强 Getter =====================

    public LldpFactory getLldpFactory() {
        return lldpFactory;
    }

    public StpFactory getStpFactory() {
        return stpFactory;
    }

    public LACPFactory getLacpFactory() {
        return lacpFactory;
    }

    public MplsFactory getMplsFactory() {
        return mplsFactory;
    }

// ===================== 应用层协议 Getter =====================

    public FtpPacketFactory getFtpPacketFactory() {
        return ftpPacketFactory;
    }

    public SmtpPacketFactory getSmtpPacketFactory() {
        return smtpPacketFactory;
    }

    public Pop3PacketFactory getPop3PacketFactory() {
        return pop3PacketFactory;
    }

    public ImapPacketFactory getImapPacketFactory() {
        return imapPacketFactory;
    }

    public SshPacketFactory getSshPacketFactory() {
        return sshPacketFactory;
    }

    public TelnetPacketFactory getTelnetPacketFactory() {
        return telnetPacketFactory;
    }

    public RtpPacketFactory getRtpPacketFactory() {
        return rtpPacketFactory;
    }

    public RtcpPacketFactory getRtcpPacketFactory() {
        return rtcpPacketFactory;
    }

    public SipPacketFactory getSipPacketFactory() {
        return sipPacketFactory;
    }

    public RadiusPacketFactory getRadiusPacketFactory() {
        return radiusPacketFactory;
    }

    public DiameterPacketFactory getDiameterPacketFactory() {
        return diameterPacketFactory;
    }

    public LdapPacketFactory getLdapPacketFactory() {
        return ldapPacketFactory;
    }

// ===================== NAT 增强 Getter =====================

    public NatHairpinningFactory getNatHairpinningFactory() {
        return natHairpinningFactory;
    }

    public NatHolePunchFactory getNatHolePunchFactory() {
        return natHolePunchFactory;
    }

    public UpnpFactory getUpnpFactory() {
        return upnpFactory;
    }

    public PcpFactory getPcpFactory() {
        return pcpFactory;
    }

// ===================== 负载均衡 Getter =====================

    public LbRoundRobinFactory getLbRoundRobinFactory() {
        return lbRoundRobinFactory;
    }

    public LbLeastConnFactory getLbLeastConnFactory() {
        return lbLeastConnFactory;
    }

    public LbIpHashFactory getLbIpHashFactory() {
        return lbIpHashFactory;
    }

    public LbHealthCheckFactory getLbHealthCheckFactory() {
        return lbHealthCheckFactory;
    }

// ===================== 监控管理 Getter =====================

    public NetFlowFactory getNetFlowFactory() {
        return netFlowFactory;
    }

    public SflowFactory getSflowFactory() {
        return sflowFactory;
    }

    public IpfixFactory getIpfixFactory() {
        return ipfixFactory;
    }

    public IcmpPingFactory getIcmpPingFactory() {
        return icmpPingFactory;
    }

    public IcmpTracerouteFactory getIcmpTracerouteFactory() {
        return icmpTracerouteFactory;
    }

// ===================== VPN 隧道 Getter =====================

    public IpsecIkeFactory getIpsecIkeFactory() {
        return ipsecIkeFactory;
    }

    public IpsecEspFactory getIpsecEspFactory() {
        return ipsecEspFactory;
    }

    public IpsecAhFactory getIpsecAhFactory() {
        return ipsecAhFactory;
    }

    public OpenVpnFactory getOpenVpnFactory() {
        return openVpnFactory;
    }

    public WireguardFactory getWireguardFactory() {
        return wireguardFactory;
    }

    public L2tpFactory getL2tpFactory() {
        return l2tpFactory;
    }

    public SstpFactory getSstpFactory() {
        return sstpFactory;
    }

// ===================== 安全防火墙 Getter =====================

    public DpiFactory getDpiFactory() {
        return dpiFactory;
    }

    public IpsFactory getIpsFactory() {
        return ipsFactory;
    }

    public WafFactory getWafFactory() {
        return wafFactory;
    }

    public DdosMitigationFactory getDdosMitigationFactory() {
        return ddosMitigationFactory;
    }

    public RateLimitFactory getRateLimitFactory() {
        return rateLimitFactory;
    }

// ===================== 加密证书 Getter =====================

    public X509Factory getX509Factory() {
        return x509Factory;
    }

    public CrlFactory getCrlFactory() {
        return crlFactory;
    }

    public OcspFactory getOcspFactory() {
        return ocspFactory;
    }

    public PkiFactory getPkiFactory() {
        return pkiFactory;
    }

    public DtlsFactory getDtlsFactory() {
        return dtlsFactory;
    }

// ===================== 访问控制 Getter =====================

    public AclFactory getAclFactory() {
        return aclFactory;
    }

    public MacAuthFactory getMacAuthFactory() {
        return macAuthFactory;
    }

    public Dot1xFactory getDot1xFactory() {
        return dot1xFactory;
    }

// ===================== 诊断工具 Getter =====================

    public NetstatFactory getNetstatFactory() {
        return netstatFactory;
    }

    public IpconfigFactory getIpconfigFactory() {
        return ipconfigFactory;
    }

    public RoutePrintFactory getRoutePrintFactory() {
        return routePrintFactory;
    }

    public NslookupFactory getNslookupFactory() {
        return nslookupFactory;
    }

    public ArpCommandFactory getArpCommandFactory() {
        return arpCommandFactory;
    }

    public TelnetClientFactory getTelnetClientFactory() {
        return telnetClientFactory;
    }

    public CurlFactory getCurlFactory() {
        return curlFactory;
    }

    public WgetFactory getWgetFactory() {
        return wgetFactory;
    }

// ===================== 20个核心工厂 Getter =====================

    public SocketFactory getSocketFactory() {
        return socketFactory;
    }

    public TcpStateMachineFactory getTcpStateMachineFactory() {
        return tcpStateMachineFactory;
    }

    public MacTableFactory getMacTableFactory() {
        return macTableFactory;
    }

    public CamTableFactory getCamTableFactory() {
        return camTableFactory;
    }

    public ForwardingEngineFactory getForwardingEngineFactory() {
        return forwardingEngineFactory;
    }

    public SessionTableFactory getSessionTableFactory() {
        return sessionTableFactory;
    }

    public FlowFactory getFlowFactory() {
        return flowFactory;
    }

    public LoadBalancerFactory getLoadBalancerFactory() {
        return loadBalancerFactory;
    }

    public SchedulerFactory getSchedulerFactory() {
        return schedulerFactory;
    }

    public DnsZoneFactory getDnsZoneFactory() {
        return dnsZoneFactory;
    }

    public DhcpLeaseFactory getDhcpLeaseFactory() {
        return dhcpLeaseFactory;
    }

    public ArpTableFactory getArpTableFactory() {
        return arpTableFactory;
    }

    public NeighborTableFactory getNeighborTableFactory() {
        return neighborTableFactory;
    }

    public MulticastRoutingFactory getMulticastRoutingFactory() {
        return multicastRoutingFactory;
    }

    public MplsLabelFactory getMplsLabelFactory() {
        return mplsLabelFactory;
    }

    public CertificateStoreFactory getCertificateStoreFactory() {
        return certificateStoreFactory;
    }

    public EventFactory getEventFactory() {
        return eventFactory;
    }

    public StatisticsFactory getStatisticsFactory() {
        return statisticsFactory;
    }

    public LogFactory getLogFactory() {
        return logFactory;
    }

    public PacketCaptureFactory getPacketCaptureFactory() {
        return packetCaptureFactory;
    }
}