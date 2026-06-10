package com.never_give_up.automation.demo.factory.tool;

import java.util.ArrayList;
import java.util.List;

/** Netstat 网络连接统计 */
public class NetstatFactory {
    public static class ConnItem {
        String proto;
        String localAddr;
        String remoteAddr;
        String state;
    }

    private final List<ConnItem> connList = new ArrayList<>();

    public void addConn(String proto, String local, String remote, String state) {
        ConnItem item = new ConnItem();
        item.proto = proto;
        item.localAddr = local;
        item.remoteAddr = remote;
        item.state = state;
        connList.add(item);
    }

    public List<ConnItem> getConnList() {
        return connList;
    }

    public void reset() {
        connList.clear();
    }
}