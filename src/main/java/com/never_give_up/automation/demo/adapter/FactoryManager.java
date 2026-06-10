package com.never_give_up.automation.demo.adapter;

import com.never_give_up.automation.demo.factory.address.FiveTupleFactory;
import com.never_give_up.automation.demo.factory.address.IpAddressFactory;
import com.never_give_up.automation.demo.factory.address.MacAddressFactory;
import com.never_give_up.automation.demo.factory.address.PortFactory;
import com.never_give_up.automation.demo.factory.application.*;
import com.never_give_up.automation.demo.factory.attack.TransportAttackFactory;
import com.never_give_up.automation.demo.factory.checksum.UdpChecksumFactory;
import com.never_give_up.automation.demo.factory.control.BandwidthFactory;
import com.never_give_up.automation.demo.factory.control.CongestionControlFactory;
import com.never_give_up.automation.demo.factory.control.SessionFactory;
import com.never_give_up.automation.demo.factory.device.NetworkDeviceFactory;
import com.never_give_up.automation.demo.factory.dhcp.DhcpFullPacketFactory;
import com.never_give_up.automation.demo.factory.dns.DnsRecursiveFactory;
import com.never_give_up.automation.demo.factory.function.*;
import com.never_give_up.automation.demo.factory.icmp.IcmpErrorFactory;
import com.never_give_up.automation.demo.factory.interfacee.NetworkInterfaceFactory;
import com.never_give_up.automation.demo.factory.link.*;
import com.never_give_up.automation.demo.factory.multicast.IgmpFactory;
import com.never_give_up.automation.demo.factory.nd.NdpFactory;
import com.never_give_up.automation.demo.factory.network.IcmpPacketFactory;
import com.never_give_up.automation.demo.factory.network.IpFragmentFactory;
import com.never_give_up.automation.demo.factory.network.IpPacketFactory;
import com.never_give_up.automation.demo.factory.option.IpOptionFactory;
import com.never_give_up.automation.demo.factory.option.TcpOptionFactory;
import com.never_give_up.automation.demo.factory.physical.BitStreamFactory;
import com.never_give_up.automation.demo.factory.physical.PhysicalChannelFactory;
import com.never_give_up.automation.demo.factory.qos.QosTrafficFactory;
import com.never_give_up.automation.demo.factory.queue.PacketQueueFactory;
import com.never_give_up.automation.demo.factory.route.BgpPacketFactory;
import com.never_give_up.automation.demo.factory.route.IpForwardFactory;
import com.never_give_up.automation.demo.factory.route.OspfPacketFactory;
import com.never_give_up.automation.demo.factory.security.FirewallRuleFactory;
import com.never_give_up.automation.demo.factory.security.ipsec.IpsecFactory;
import com.never_give_up.automation.demo.factory.serialize.PacketSerializerFactory;
import com.never_give_up.automation.demo.factory.timer.TcpTimerFactory;
import com.never_give_up.automation.demo.factory.topology.LinkFactory;
import com.never_give_up.automation.demo.factory.topology.SubnetFactory;
import com.never_give_up.automation.demo.factory.transition.Nat64Factory;
import com.never_give_up.automation.demo.factory.transport.TcpPacketFactory;
import com.never_give_up.automation.demo.factory.transport.TcpReassemblyFactory;
import com.never_give_up.automation.demo.factory.transport.UdpPacketFactory;
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
import com.never_give_up.automation.demo.factory.application.ftp.FtpPacketFactory;
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
}