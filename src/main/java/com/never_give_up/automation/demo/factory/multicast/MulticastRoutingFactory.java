package com.never_give_up.automation.demo.factory.multicast;

import java.util.HashSet;
import java.util.Set;

public class MulticastRoutingFactory {
    private final Set<String> anySourceGroup = new HashSet<>();
    private final Set<String> sourceSpecific = new HashSet<>();

    public void addSG(String src, String group) {
        sourceSpecific.add(src + "_" + group);
    }

    public void addStarG(String group) {
        anySourceGroup.add(group);
    }

    public boolean hasRoute(String s, String g) {
        return anySourceGroup.contains(g) || sourceSpecific.contains(s + "_" + g);
    }

    public void reset() {
        anySourceGroup.clear();
        sourceSpecific.clear();
    }
}