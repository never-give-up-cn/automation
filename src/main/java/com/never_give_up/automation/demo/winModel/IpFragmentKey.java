package com.never_give_up.automation.demo.winModel;


import lombok.Data;

import java.util.Objects;

@Data
public class IpFragmentKey {
    private int identification;
    private String srcIp;
    private String dstIp;
    private String protocol;

    public IpFragmentKey(int id, String src, String dst, String proto) {
        this.identification = id;
        this.srcIp = src;
        this.dstIp = dst;
        this.protocol = proto;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpFragmentKey that = (IpFragmentKey) o;
        return identification == that.identification &&
                Objects.equals(srcIp, that.srcIp) &&
                Objects.equals(dstIp, that.dstIp) &&
                Objects.equals(protocol, that.protocol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identification, srcIp, dstIp, protocol);
    }
}
