package com.never_give_up.automation.demo.adapter;

import com.never_give_up.automation.demo.factory.address.FiveTupleFactory;
import com.never_give_up.automation.demo.factory.address.IpAddressFactory;
import com.never_give_up.automation.demo.factory.address.MacAddressFactory;
import com.never_give_up.automation.demo.factory.address.PortFactory;
import com.never_give_up.automation.demo.factory.application.DhcpPacketFactory;
import com.never_give_up.automation.demo.factory.application.DnsPacketFactory;
import com.never_give_up.automation.demo.factory.application.HttpPacketFactory;
import com.never_give_up.automation.demo.factory.application.TlsPacketFactory;
import com.never_give_up.automation.demo.factory.control.BandwidthFactory;
import com.never_give_up.automation.demo.factory.control.CongestionControlFactory;
import com.never_give_up.automation.demo.factory.control.SessionFactory;
import com.never_give_up.automation.demo.factory.device.NetworkDeviceFactory;
import com.never_give_up.automation.demo.factory.function.*;
import com.never_give_up.automation.demo.factory.link.ArpPacketFactory;
import com.never_give_up.automation.demo.factory.link.EthernetFactory;
import com.never_give_up.automation.demo.factory.link.LinkLayerFactory;
import com.never_give_up.automation.demo.factory.network.IcmpPacketFactory;
import com.never_give_up.automation.demo.factory.network.IpFragmentFactory;
import com.never_give_up.automation.demo.factory.network.IpPacketFactory;
import com.never_give_up.automation.demo.factory.queue.PacketQueueFactory;
import com.never_give_up.automation.demo.factory.security.FirewallRuleFactory;
import com.never_give_up.automation.demo.factory.topology.LinkFactory;
import com.never_give_up.automation.demo.factory.topology.SubnetFactory;
import com.never_give_up.automation.demo.factory.transport.TcpPacketFactory;
import com.never_give_up.automation.demo.factory.transport.UdpPacketFactory;

// 前18个工厂
import com.never_give_up.automation.demo.factory.option.IpOptionFactory;
import com.never_give_up.automation.demo.factory.option.TcpOptionFactory;
import com.never_give_up.automation.demo.factory.checksum.UdpChecksumFactory;
import com.never_give_up.automation.demo.factory.icmp.IcmpErrorFactory;
import com.never_give_up.automation.demo.factory.link.EthernetPaddingFactory;
import com.never_give_up.automation.demo.factory.route.IpForwardFactory;
import com.never_give_up.automation.demo.factory.timer.TcpTimerFactory;
import com.never_give_up.automation.demo.factory.window.TcpWindowFactory;
import com.never_give_up.automation.demo.factory.connection.TcpConnectionFactory;
import com.never_give_up.automation.demo.factory.dns.DnsRecursiveFactory;
import com.never_give_up.automation.demo.factory.dhcp.DhcpFullPacketFactory;
import com.never_give_up.automation.demo.factory.tls.TlsHandshakeFactory;
import com.never_give_up.automation.demo.factory.serialize.PacketSerializerFactory;
import com.never_give_up.automation.demo.factory.interfacee.NetworkInterfaceFactory;
import com.never_give_up.automation.demo.factory.vlan.VlanFactory;
import com.never_give_up.automation.demo.factory.tunnel.TunnelFactory;
import com.never_give_up.automation.demo.factory.multicast.IgmpFactory;
import com.never_give_up.automation.demo.factory.nd.NdpFactory;

// ===================== 本次新增 14 个超级工厂 =====================
import com.never_give_up.automation.demo.factory.physical.BitStreamFactory;
import com.never_give_up.automation.demo.factory.physical.PhysicalChannelFactory;
import com.never_give_up.automation.demo.factory.link.PppoeFactory;
import com.never_give_up.automation.demo.factory.link.MacSecFactory;
import com.never_give_up.automation.demo.factory.route.OspfPacketFactory;
import com.never_give_up.automation.demo.factory.route.BgpPacketFactory;
import com.never_give_up.automation.demo.factory.qos.QosTrafficFactory;
import com.never_give_up.automation.demo.factory.transition.Nat64Factory;
import com.never_give_up.automation.demo.factory.transport.TcpReassemblyFactory;
import com.never_give_up.automation.demo.factory.attack.TransportAttackFactory;
import com.never_give_up.automation.demo.factory.application.NtpPacketFactory;
import com.never_give_up.automation.demo.factory.application.SnmpPacketFactory;
import com.never_give_up.automation.demo.factory.application.Http23PacketFactory;
import com.never_give_up.automation.demo.factory.security.ipsec.IpsecFactory;

import lombok.Data;

@Data
public class FactoryManager {
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

    // 前18个工厂
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

    // ===================== 本次新增 14 个工厂成员变量 =====================
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

    public FactoryManager() {
        // 基础初始化
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

        // 前18个工厂初始化
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

        // ===================== 新增14个工厂初始化 =====================
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
    }

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