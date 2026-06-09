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
import com.never_give_up.automation.demo.factory.function.ArpCacheFactory;
import com.never_give_up.automation.demo.factory.function.DnsCacheFactory;
import com.never_give_up.automation.demo.factory.function.NatMappingFactory;
import com.never_give_up.automation.demo.factory.function.RouteTableFactory;
import com.never_give_up.automation.demo.factory.link.ArpPacketFactory;
import com.never_give_up.automation.demo.factory.link.EthernetFactory;
import com.never_give_up.automation.demo.factory.network.IcmpPacketFactory;
import com.never_give_up.automation.demo.factory.network.IpFragmentFactory;
import com.never_give_up.automation.demo.factory.network.IpPacketFactory;
import com.never_give_up.automation.demo.factory.transport.TcpPacketFactory;
import com.never_give_up.automation.demo.factory.transport.UdpPacketFactory;
import lombok.Data;

@Data
public class FactoryManager {
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

    public FactoryManager() {
        this.ipAddressFactory = new IpAddressFactory();
        this.macFactory = new MacAddressFactory();
        this.portFactory = new PortFactory();
        this.ipFragmentFactory = new IpFragmentFactory();
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
    }

    public void reset() {
        tcpFactory.reset();
        ipPacketFactory.reset();
        arpCache.clear();
        dnsCache.clear();
        natFactory.clear();
        routeTable.clear();
        congestionControl.reset();
    }

    // Getters for all factories
    public IpAddressFactory getIpAddressFactory() {
        return ipAddressFactory;
    }

    public MacAddressFactory getMacFactory() {
        return macFactory;
    }

    public PortFactory getPortFactory() {
        return portFactory;
    }

    public TcpPacketFactory getTcpFactory() {
        return tcpFactory;
    }

    public UdpPacketFactory getUdpFactory() {
        return udpFactory;
    }

    public IpPacketFactory getIpPacketFactory() {
        return ipPacketFactory;
    }

    public EthernetFactory getEtherFactory() {
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

    // 添加 getter 方法
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
}
