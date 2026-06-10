package com.never_give_up.automation.demo.factory.route;
//6. IP 路由转发工厂（IpForwardFactory）
public class IpForwardFactory {
    public byte[] forward(byte[] ipPacket, String outgoingIface, int dstIp) {
        ipPacket[8]--;
        int checksum = calculateChecksum(ipPacket, 20);
        ipPacket[10] = (byte) (checksum >> 8);
        ipPacket[11] = (byte) checksum;
        return ipPacket;
    }

    private int calculateChecksum(byte[] buf, int len) {
        int sum = 0;
        for (int i = 0; i < len; i += 2) {
            sum += ((buf[i] & 0xFF) << 8) | (buf[i + 1] & 0xFF);
        }
        while (sum >> 16 != 0) sum = (sum & 0xFFFF) + (sum >> 16);
        return ~sum & 0xFFFF;
    }
}