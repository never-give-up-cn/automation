package com.never_give_up.automation.demo.factory.monitor;

/** IPFIX 流导出协议 */
public class IpfixFactory {
    private int templateId = 256;

    public byte[] buildIpfixPacket(byte[] flowData) {
        byte[] ipfix = new byte[16 + flowData.length];
        ipfix[0] = 0x00;
        ipfix[1] = 0x0a; // Version 10
        ipfix[4] = (byte) (templateId >> 8);
        ipfix[5] = (byte) templateId;
        System.arraycopy(flowData, 0, ipfix, 16, flowData.length);
        return ipfix;
    }

    public void reset() {
        templateId = 256;
    }
}