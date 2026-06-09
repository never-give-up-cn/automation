package com.never_give_up.automation.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FiveTuple {
    private String sourceIp;
    private String destinationIp;
    private int sourcePort;
    private int destinationPort;
    private int protocol;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FiveTuple)) return false;
        FiveTuple that = (FiveTuple) o;
        return sourceIp.equals(that.sourceIp) &&
               destinationIp.equals(that.destinationIp) &&
               sourcePort == that.sourcePort &&
               destinationPort == that.destinationPort &&
               protocol == that.protocol;
    }

    @Override
    public int hashCode() {
        int result = sourceIp.hashCode();
        result = 31 * result + destinationIp.hashCode();
        result = 31 * result + sourcePort;
        result = 31 * result + destinationPort;
        result = 31 * result + protocol;
        return result;
    }
}
