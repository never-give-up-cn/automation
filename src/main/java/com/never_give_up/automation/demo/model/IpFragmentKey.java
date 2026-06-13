package com.never_give_up.automation.demo.model;

import java.util.Objects;

public class IpFragmentKey {
    public int identification;
    public String srcIp;
    public String dstIp;
    public String protocol;

    public IpFragmentKey(int id, String src, String dst, String proto) {
        identification = id;
        srcIp = src;
        dstIp = dst;
        protocol = proto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpFragmentKey that = (IpFragmentKey) o;
        return identification == that.identification && Objects.equals(srcIp, that.srcIp) && Objects.equals(dstIp, that.dstIp) && Objects.equals(protocol, that.protocol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identification, srcIp, dstIp, protocol);
    }
}
