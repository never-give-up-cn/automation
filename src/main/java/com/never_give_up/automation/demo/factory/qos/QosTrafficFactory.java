package com.never_give_up.automation.demo.factory.qos;
public class QosTrafficFactory {
    public int setDSCP(byte[] ipPkt, int dscp) {
        ipPkt[1] = (byte)((dscp<<2) | (ipPkt[1]&0x03));
        return dscp;
    }
    public boolean tokenBucketAllow(long cir, long bucket) {
        return bucket > 0;
    }
}