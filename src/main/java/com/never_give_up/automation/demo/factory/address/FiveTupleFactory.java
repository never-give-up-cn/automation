package com.never_give_up.automation.demo.factory.address;

/**
 * 五元组工厂
 */
public class FiveTupleFactory {
    private String srcIp;
    private String dstIp;
    private int srcPort;
    private int dstPort;
    private String protocol;

    public FiveTupleFactory(IpAddressFactory ipFactory, PortFactory portFactory) {
        // 初始化
    }

    public void extract(String srcIp, String dstIp, int srcPort, int dstPort, String protocol) {
        this.srcIp = srcIp;
        this.dstIp = dstIp;
        this.srcPort = srcPort;
        this.dstPort = dstPort;
        this.protocol = protocol;
        System.out.println(String.format("【五元组】: %s | %s:%d → %s:%d",
                protocol, srcIp, srcPort, dstIp, dstPort));
    }

    public String getSrcIp() { return srcIp; }
    public String getDstIp() { return dstIp; }
    public int getSrcPort() { return srcPort; }
    public int getDstPort() { return dstPort; }
    public String getProtocol() { return protocol; }
}