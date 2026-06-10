package com.never_give_up.automation.demo.factory.multicast;

import java.util.HashSet;
import java.util.Set;

public class PimSmFactory {
    private final Set<String> groups = new HashSet<>();

    public void joinGroup(String groupIp) {
        groups.add(groupIp);
    }

    public void leaveGroup(String groupIp) {
        groups.remove(groupIp);
    }

    public boolean isMember(String groupIp) {
        return groups.contains(groupIp);
    }

    public void reset() {
        groups.clear();
    }
}