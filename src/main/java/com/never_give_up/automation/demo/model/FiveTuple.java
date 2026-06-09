package com.never_give_up.automation.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FiveTuple {
    private String sourceIp;
    private String destinationIp;
    private int sourcePort;
    private int destinationPort;
    private int protocol;

    public String getProtocolName() {
        switch (protocol) {
            case 1: return "ICMP";
            case 6: return "TCP";
            case 17: return "UDP";
            default: return "Unknown";
        }
    }

    @Override
    public String toString() {
        return String.format("%s:%d -> %s:%d [%s]",
                sourceIp, sourcePort, destinationIp, destinationPort, getProtocolName());
    }

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
