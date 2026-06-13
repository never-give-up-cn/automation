package com.never_give_up.automation.demo.model;

import java.awt.Point;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

import com.never_give_up.automation.demo.DataCart;
import com.never_give_up.automation.demo.adapter.FactoryManager;

/**
 * DataCart 依赖注入上下文，封装了从 DataCartFactoryGame 外类中提取的所有依赖。
 * DataCart 提取为独立类后，通过 GameContext 访问原本的外类字段和方法。
 */
public class GameContext {
    private final Point pcFactory;
    private String pcIpAddress;
    private String resolvedServerIp;
    private final List<DataCart> pendingDataCarts;
    private final Map<String, NatEntry> natTable;
    private final AtomicInteger natPortCounter;
    private int ipIdentifierCounter;
    private TcpState currentTcpState = TcpState.CLOSED;
    private boolean pcIpAssigned = false;
    private boolean tracerouteActive = false;
    private boolean useUdp = false;
    private boolean tlsEnabled = false;
    private int cwnd = 1;
    private String httpResponseContent = "";
    private TlsState tlsState = TlsState.IDLE;
    private int serverReceivedCount = 0;
    private final int totalDataToTransmit = 15;
    private final FactoryManager factoryManager;
    private final Consumer<String> appendToConsole;
    private final Function<String, Point> findBuildingCoords;
    private final Runnable updateArpDisplay;
    private final Runnable updateNatDisplay;
    private final Runnable updateDnsDisplay;

    public GameContext(Point pcFactory,
                       List<DataCart> pendingDataCarts,
                       Map<String, NatEntry> natTable,
                       AtomicInteger natPortCounter,
                       int ipIdentifierCounter,
                       FactoryManager factoryManager,
                       Consumer<String> appendToConsole,
                       Function<String, Point> findBuildingCoords,
                       Runnable updateArpDisplay,
                       Runnable updateNatDisplay,
                       Runnable updateDnsDisplay) {
        this.pcFactory = pcFactory;
        this.pendingDataCarts = pendingDataCarts;
        this.natTable = natTable;
        this.natPortCounter = natPortCounter;
        this.ipIdentifierCounter = ipIdentifierCounter;
        this.factoryManager = factoryManager;
        this.appendToConsole = appendToConsole;
        this.findBuildingCoords = findBuildingCoords;
        this.updateArpDisplay = updateArpDisplay;
        this.updateNatDisplay = updateNatDisplay;
        this.updateDnsDisplay = updateDnsDisplay;
    }

    // ========== Getters & Setters ==========

    public Point getPcFactory() { return pcFactory; }

    public String getPcIpAddress() { return pcIpAddress; }

    public void setPcIpAddress(String pcIpAddress) { this.pcIpAddress = pcIpAddress; }

    public String getResolvedServerIp() { return resolvedServerIp; }

    public void setResolvedServerIp(String resolvedServerIp) { this.resolvedServerIp = resolvedServerIp; }

    public List<DataCart> getPendingDataCarts() { return pendingDataCarts; }

    public Map<String, NatEntry> getNatTable() { return natTable; }

    public AtomicInteger getNatPortCounter() { return natPortCounter; }

    public int getIpIdentifierCounter() { return ipIdentifierCounter; }

    public void setIpIdentifierCounter(int ipIdentifierCounter) { this.ipIdentifierCounter = ipIdentifierCounter; }

    public TcpState getCurrentTcpState() { return currentTcpState; }

    public void setCurrentTcpState(TcpState currentTcpState) { this.currentTcpState = currentTcpState; }

    public boolean isPcIpAssigned() { return pcIpAssigned; }

    public void setPcIpAssigned(boolean pcIpAssigned) { this.pcIpAssigned = pcIpAssigned; }

    public boolean isTracerouteActive() { return tracerouteActive; }

    public void setTracerouteActive(boolean tracerouteActive) { this.tracerouteActive = tracerouteActive; }

    public boolean isUseUdp() { return useUdp; }

    public void setUseUdp(boolean useUdp) { this.useUdp = useUdp; }

    public boolean isTlsEnabled() { return tlsEnabled; }

    public void setTlsEnabled(boolean tlsEnabled) { this.tlsEnabled = tlsEnabled; }

    public int getCwnd() { return cwnd; }

    public void setCwnd(int cwnd) { this.cwnd = cwnd; }

    public String getHttpResponseContent() { return httpResponseContent; }

    public void setHttpResponseContent(String httpResponseContent) { this.httpResponseContent = httpResponseContent; }

    public TlsState getTlsState() { return tlsState; }

    public void setTlsState(TlsState tlsState) { this.tlsState = tlsState; }

    public int getServerReceivedCount() { return serverReceivedCount; }

    public void setServerReceivedCount(int serverReceivedCount) { this.serverReceivedCount = serverReceivedCount; }

    public int getTotalDataToTransmit() { return totalDataToTransmit; }

    public FactoryManager getFactoryManager() { return factoryManager; }

    public Consumer<String> getAppendToConsole() { return appendToConsole; }

    public Function<String, Point> getFindBuildingCoords() { return findBuildingCoords; }

    public Runnable getUpdateArpDisplay() { return updateArpDisplay; }

    public Runnable getUpdateNatDisplay() { return updateNatDisplay; }

    public Runnable getUpdateDnsDisplay() { return updateDnsDisplay; }
}
