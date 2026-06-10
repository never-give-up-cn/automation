package com.never_give_up.automation.demo.factory.tool;

import java.util.ArrayList;
import java.util.List;

/** 路由表查看 route print */
public class RoutePrintFactory {
    public static class RouteItem {
        String dest;
        String mask;
        String gateway;
        String iface;
    }

    private final List<RouteItem> routeList = new ArrayList<>();

    public void addRoute(String dest, String mask, String gw, String iface) {
        RouteItem item = new RouteItem();
        item.dest = dest;
        item.mask = mask;
        item.gateway = gw;
        item.iface = iface;
        routeList.add(item);
    }

    public List<RouteItem> getRoutes() {
        return routeList;
    }

    public void reset() {
        routeList.clear();
    }
}