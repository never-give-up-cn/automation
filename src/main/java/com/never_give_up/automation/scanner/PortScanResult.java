package com.never_give_up.automation.scanner;

import java.util.ArrayList;
import java.util.List;

/**
 * 端口扫描结果模型
 */
public class PortScanResult {

    /** 目标IP地址 */
    private String ip;

    /** 主机是否存活（ping通或有开放端口） */
    private boolean alive;

    /** ping响应时间（毫秒），-1表示超时 */
    private long pingTimeMs;

    /** 开放端口列表 */
    private List<PortInfo> openPorts = new ArrayList<>();

    /** 总扫描端口数 */
    private int totalPortsScanned;

    /** 扫描持续时长（毫秒） */
    private long scanDurationMs;

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }

    public long getPingTimeMs() { return pingTimeMs; }
    public void setPingTimeMs(long pingTimeMs) { this.pingTimeMs = pingTimeMs; }

    public List<PortInfo> getOpenPorts() { return openPorts; }
    public void setOpenPorts(List<PortInfo> openPorts) { this.openPorts = openPorts; }

    public int getTotalPortsScanned() { return totalPortsScanned; }
    public void setTotalPortsScanned(int totalPortsScanned) { this.totalPortsScanned = totalPortsScanned; }

    public long getScanDurationMs() { return scanDurationMs; }
    public void setScanDurationMs(long scanDurationMs) { this.scanDurationMs = scanDurationMs; }

    public void addPort(PortInfo port) {
        this.openPorts.add(port);
    }

    /**
     * 单个端口信息
     */
    public static class PortInfo {
        /** 端口号 */
        private int port;

        /** 传输层协议（TCP / UDP） */
        private String protocol;

        /** 服务名称（如 ssh, http, https, mysql 等） */
        private String serviceName;

        /** 服务产品/版本信息（从 banner 或指纹识别得来） */
        private String serviceProduct;

        /** 原始 banner 信息 */
        private String banner;

        /** 连接耗时（毫秒） */
        private long connectTimeMs;

        /** 端口状态（open / filtered / closed） */
        private String state;

        public int getPort() { return port; }
        public void setPort(int port) { this.port = port; }

        public String getProtocol() { return protocol; }
        public void setProtocol(String protocol) { this.protocol = protocol; }

        public String getServiceName() { return serviceName; }
        public void setServiceName(String serviceName) { this.serviceName = serviceName; }

        public String getServiceProduct() { return serviceProduct; }
        public void setServiceProduct(String serviceProduct) { this.serviceProduct = serviceProduct; }

        public String getBanner() { return banner; }
        public void setBanner(String banner) { this.banner = banner; }

        public long getConnectTimeMs() { return connectTimeMs; }
        public void setConnectTimeMs(long connectTimeMs) { this.connectTimeMs = connectTimeMs; }

        public String getState() { return state; }
        public void setState(String state) { this.state = state; }
    }
}
