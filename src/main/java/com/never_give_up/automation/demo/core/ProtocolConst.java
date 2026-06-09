package com.never_give_up.automation.demo.core;

public final class ProtocolConst {
    public static final int ETHERNET_MTU = 1500;
    public static final int IP_HEADER_SIZE = 20;
    public static final int TCP_HEADER_SIZE = 20;
    public static final int UDP_HEADER_SIZE = 8;

    public static final int DEFAULT_TTL = 64;
    public static final int IP_PROTO_ICMP = 1;
    public static final int IP_PROTO_TCP = 6;
    public static final int IP_PROTO_UDP = 17;

    public static final int PORT_HTTP = 80;
    public static final int PORT_HTTPS = 443;
    public static final int PORT_DNS = 53;
    public static final int PORT_DHCP_CLIENT = 68;
    public static final int PORT_DHCP_SERVER = 67;

    public static final String MAC_PREFIX = "00:1A:2B";

    private ProtocolConst() {
    }
}