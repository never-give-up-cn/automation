package com.never_give_up.automation.demo.factory.checksum;
//3. UDP 校验和工厂（UdpChecksumFactory）
public class UdpChecksumFactory {
    public int calculate(int srcIp, int dstIp, int udpLen, byte[] data) {
        long sum = 0;
        sum += (srcIp >> 16) & 0xFFFF;
        sum += srcIp & 0xFFFF;
        sum += (dstIp >> 16) & 0xFFFF;
        sum += dstIp & 0xFFFF;
        sum += 17;
        sum += udpLen;

        int i = 0;
        while (i < data.length) {
            int word = ((data[i] & 0xFF) << 8) | ((i + 1 < data.length ? data[i + 1] : 0) & 0xFF);
            sum += word;
            i += 2;
        }
        while (sum >> 16 != 0) {
            sum = (sum & 0xFFFF) + (sum >> 16);
        }
        return (int) (~sum & 0xFFFF);
    }
}
