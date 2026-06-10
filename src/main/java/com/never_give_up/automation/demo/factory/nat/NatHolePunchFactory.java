package com.never_give_up.automation.demo.factory.nat;

import java.util.HashSet;
import java.util.Set;

/** NAT 穿透 / 打洞 */
public class NatHolePunchFactory {
    private final Set<String> punchedPeers = new HashSet<>();

    public boolean doHolePunch(String peerAddr) {
        punchedPeers.add(peerAddr);
        return true;
    }

    public boolean isPunched(String peerAddr) {
        return punchedPeers.contains(peerAddr);
    }

    public void reset() {
        punchedPeers.clear();
    }
}