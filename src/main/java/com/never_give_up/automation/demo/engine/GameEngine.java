package com.never_give_up.automation.demo.engine;

import com.never_give_up.automation.demo.adapter.FactoryManager;
import com.never_give_up.automation.demo.config.NetworkConfig;

import com.never_give_up.automation.demo.winModel.IpFragment;
import com.never_give_up.automation.demo.winModel.IpFragmentKey;
import com.never_give_up.automation.demo.winModel.*;
import lombok.Getter;
import lombok.Setter;

import java.awt.Point;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class GameEngine {
    // 游戏状态
    @Getter private int funds = 3000;
    @Getter private int helloStock = 0;
    @Getter private int sayStock = 0;
    @Getter private int serverReceivedCount = 0;
    @Getter private int serverBufferCount = 0;

    // TCP状态
    @Getter @Setter private TcpState currentTcpState = TcpState.CLOSED;
    @Getter private int cwnd = NetworkConfig.INITIAL_CWND;
    @Getter private int ssthresh = NetworkConfig.INITIAL_SSTHRESH;
    @Getter private int rwnd = 3;

    // 网络状态
    @Getter @Setter private boolean pcIpAssigned = false;
    @Getter @Setter private String pcIpAddress = null;
    @Getter @Setter private String resolvedServerIp = null;
    @Getter private boolean dnsResolved = false;
    @Getter private boolean dnsResolving = false;

    @Getter @Setter private boolean useUdp = false;
    @Getter @Setter private boolean httpDemoEnabled = false;
    @Getter @Setter private boolean tlsEnabled = false;
    @Getter @Setter private TlsState tlsState = TlsState.IDLE;
    @Getter @Setter private boolean dhcpInProgress = false;
    @Getter private boolean udpActive = false;
    @Getter @Setter private int udpSeqToSend = 0;
    @Getter @Setter private long lastUdpSendTime = 0;
    @Getter @Setter private boolean httpSent = false;
    @Getter @Setter private String httpResponseContent = "";
    @Getter @Setter private int packetsAckedSinceLastIncrease = 0;
    @Getter @Setter private long stateTimerWatchdog = System.currentTimeMillis();
    @Getter @Setter private int dnsRetryCount = 0;
    @Getter @Setter private long lastDnsQueryTime = 0;
    @Getter @Setter private boolean tracerouteActive = false;
    @Getter @Setter private boolean tracerouteWaitReply = false;
    @Getter @Setter private int tracerouteNextTTL = 1;

    // 缓存
    private final Map<String, ArpEntry> arpCache = new ConcurrentHashMap<>();
    private final Map<String, DnsEntry> dnsCache = new ConcurrentHashMap<>();
    private final Map<String, NatEntry> natTable = new ConcurrentHashMap<>();
    private final Map<IpFragmentKey, List<IpFragment>> fragmentBuffer = new ConcurrentHashMap<>();
    private final List<RetransmissionTask> activeTimers = new CopyOnWriteArrayList<>();
    private final Set<Integer> ackedSeq = ConcurrentHashMap.newKeySet();
    private final Set<Integer> sentSeq = ConcurrentHashMap.newKeySet();

    // 其他
    private final AtomicInteger natPortCounter = new AtomicInteger(50001);
    private int ipIdentifierCounter = 2000;
    private int nextSeqNum = 100;
    private long lastServerConsumeTime = 0;
    private int serverDecodeDelay = 600;
    private boolean demoCompleted = false;

    // 工厂管理器
    private FactoryManager factoryManager;

    // 回调接口
    private GameCallback callback;

    public interface GameCallback {
        void onLog(String message);
        void onUpdate();
        void onPacketArrived(DataPacket packet);
        void addPendingPacket(DataPacket packet);
        Point getPcFactory();           // 添加这个方法
        Point findBuildingCoords(String tag);  // 添加这个方法
    }

    public GameEngine(FactoryManager factoryManager) {
        this.factoryManager = factoryManager;
    }

    public void setCallback(GameCallback callback) {
        this.callback = callback;
    }

    // 创建数据包的便捷方法
    // 在 GameEngine.java 中，替换 createDataPacket 方法

    public DataPacket createDataPacket(double x, double y, String type, int seq) {
        DataPacket packet = new DataPacket(x, y, type, seq, factoryManager);

        // ========== 关键：设置外部引用 ==========
        if (callback != null) {
            packet.setExternalReferences(
                    callback.getPcFactory(),
                    this.natTable,
                    this.arpCache,
                    this.dnsCache,
                    this::log,
                    this::addPendingPacketToCallback,
                    this.ipIdentifierCounter,
                    this.useUdp,
                    this.tlsEnabled,
                    this.tlsState,
                    this.serverReceivedCount,
                    NetworkConfig.TOTAL_DATA_TO_TRANSMIT,
                    this.cwnd,
                    this.packetsAckedSinceLastIncrease,
                    this.serverBufferCount,
                    NetworkConfig.SERVER_BUFFER_MAX,
                    this.lastServerConsumeTime,
                    null, null, null, null, false,
                    this.pcIpAddress,
                    this.resolvedServerIp,
                    this.currentTcpState.toString(),
                    this.tracerouteActive,
                    this.tracerouteNextTTL,
                    callback::onLog,      // appendToConsole
                    (s) -> { if (callback != null) callback.onUpdate(); },  // updateArpDisplay
                    (s) -> { if (callback != null) callback.onUpdate(); },  // updateNatDisplay
                    (s) -> { if (callback != null) callback.onUpdate(); },  // updateDnsDisplay
                    (s) -> { if (callback != null) callback.onUpdate(); },  // updateTopLabel
                    null, null, null,
                    null,
                    NetworkConfig.WAN_BOTTLE_NECK_MAX,
                    null
            );
        }

        return packet;
    }

    // 添加辅助方法
    private void addPendingPacketToCallback(DataPacket packet) {
        if (callback != null) callback.addPendingPacket(packet);
    }


    public void addFunds(int amount) {
        funds += amount;
        if (callback != null) callback.onUpdate();
    }

    public boolean deductFunds(int amount) {
        if (funds >= amount) {
            funds -= amount;
            if (callback != null) callback.onUpdate();
            return true;
        }
        return false;
    }

    public void addHelloStock() { helloStock++; }
    public void addSayStock() { sayStock++; }
    public void consumeHelloStock(int count) { helloStock -= count; }
    public void consumeSayStock(int count) { sayStock -= count; }

    public void incrementServerReceived() {
        serverReceivedCount++;
    }

    public void incrementServerBuffer() {
        serverBufferCount++;
    }

    public void decrementServerBuffer() {
        serverBufferCount--;
        lastServerConsumeTime = System.currentTimeMillis();
        rwnd = NetworkConfig.SERVER_BUFFER_MAX - serverBufferCount;
    }

    public boolean canSendData() {
        return !demoCompleted && serverReceivedCount < NetworkConfig.TOTAL_DATA_TO_TRANSMIT;
    }

    public boolean isTransmissionComplete() {
        return serverReceivedCount >= NetworkConfig.TOTAL_DATA_TO_TRANSMIT;
    }

    public void startDnsResolution(String targetDomain) {
        if (dnsResolved || dnsResolving) return;

        dnsResolving = true;
        DnsEntry cached = dnsCache.get(targetDomain);
        if (cached != null && !cached.isExpired()) {
            resolvedServerIp = cached.getIpAddress();
            dnsResolved = true;
            dnsResolving = false;
            log("【📚 DNS缓存命中】: " + targetDomain + " → " + resolvedServerIp);
            if (callback != null) callback.onUpdate();
            return;
        }

        log("【🌐 DNS解析开始】: 查询域名 " + targetDomain);
    }

    public void onDnsResponse(String domain, String ip) {
        resolvedServerIp = ip;
        dnsResolved = true;
        dnsResolving = false;
        dnsCache.put(domain, new DnsEntry(domain, ip, 3600000));
        log("【✅ DNS解析成功】: " + domain + " → " + ip);
        if (callback != null) callback.onUpdate();
    }

    public boolean performArpResolution(String targetIp) {
        ArpEntry entry = arpCache.get(targetIp);
        if (entry == null) {
            String newMac = String.format("00:1A:2B:%02X:%02X:%02X",
                    new Random().nextInt(256), new Random().nextInt(256), new Random().nextInt(256));
            arpCache.put(targetIp, new ArpEntry(targetIp, newMac));
            log("【📥 ARP响应】: " + targetIp + " → " + newMac);
            return false;
        }
        log("【✅ ARP缓存命中】: " + targetIp + " → " + entry.getMacAddress());
        return true;
    }

    public String getMacAddress(String ip) {
        ArpEntry entry = arpCache.get(ip);
        return entry != null ? entry.getMacAddress() : null;
    }

    public void applyNatMapping(String insideIp, int insidePort, DataPacket packet) {
        String key = insideIp + ":" + insidePort;
        if (!natTable.containsKey(key)) {
            int publicPort = natPortCounter.getAndIncrement();
            NatEntry entry = new NatEntry(insideIp, insidePort, "203.0.113.1", publicPort);
            natTable.put(key, entry);
        }
        NatEntry entry = natTable.get(key);
        packet.setNatted(true);
        packet.setNatPublicIp(entry.getPublicIp());
        packet.setNatPublicPort(entry.getPublicPort());
        log("【🌍 NAT转换】: " + insideIp + ":" + insidePort + " → " +
                entry.getPublicIp() + ":" + entry.getPublicPort());
    }

    public int getNextSequenceNumber() {
        return nextSeqNum++;
    }

    public void addRetransmissionTask(int seqNum) {
        activeTimers.add(new RetransmissionTask(seqNum, System.currentTimeMillis()));
        sentSeq.add(seqNum);
    }

    public void ackRetransmissionTask(int seqNum) {
        for (RetransmissionTask task : activeTimers) {
            if (task.getSeqNum() == seqNum) {
                task.setAcked(true);
                ackedSeq.add(seqNum);
                break;
            }
        }
    }

    public void checkRetransmissionTimeout() {
        long now = System.currentTimeMillis();
        List<RetransmissionTask> toRemove = new ArrayList<>();

        for (RetransmissionTask task : activeTimers) {
            if (task.isAcked()) {
                toRemove.add(task);
                continue;
            }

            if (now - task.getSendTime() > NetworkConfig.RTO_TIMEOUT_MS) {
                task.setRetryCount(task.getRetryCount() + 1);
                if (task.getRetryCount() >= 5) {
                    log("【💀 连接崩溃】: SEQ=" + task.getSeqNum() + " 重传失败");
                    resetSession();
                    return;
                }
                task.setSendTime(now);
                ssthresh = Math.max(2, cwnd / 2);
                cwnd = 1;
                log("【⚠️ 超时重传】: SEQ=" + task.getSeqNum() +
                        " (第" + task.getRetryCount() + "次), ssthresh=" + ssthresh);

                // 创建重传数据包 - 需要获取 PC 工厂坐标
                if (callback != null) {
                    Point pcFactory = callback.getPcFactory();
                    if (pcFactory != null) {
                        DataPacket retransmit = new DataPacket(pcFactory.x, pcFactory.y, "DATA", task.getSeqNum(), factoryManager);
                        retransmit.setRetransmission(true);
                        retransmit.setTtl(64);
                        callback.addPendingPacket(retransmit);
                    }
                }

                if (callback != null) callback.onUpdate();
            }
        }

        activeTimers.removeAll(toRemove);
    }

    public void updateCongestionControl() {
        if (cwnd < ssthresh) {
            cwnd++;
            log("【📈 慢启动】: cwnd=" + cwnd + ", ssthresh=" + ssthresh);
        } else {
            cwnd++;
            log("【🐌 拥塞避免】: cwnd=" + cwnd);
        }
    }

    public void resetSession() {
        currentTcpState = TcpState.CLOSED;
        serverReceivedCount = 0;
        serverBufferCount = 0;
        cwnd = NetworkConfig.INITIAL_CWND;
        ssthresh = NetworkConfig.INITIAL_SSTHRESH;
        rwnd = 3;
        nextSeqNum = 100;
        sentSeq.clear();
        ackedSeq.clear();
        activeTimers.clear();
        fragmentBuffer.clear();
        dnsResolved = false;
        dnsResolving = false;
        resolvedServerIp = null;
        demoCompleted = false;

        if (callback != null) callback.onUpdate();
    }

    public void markDemoCompleted() {
        demoCompleted = true;
    }

    public int getEffectiveWindow() {
        return Math.min(cwnd, rwnd);
    }

    public int getUnackedCount() {
        return sentSeq.size() - ackedSeq.size();
    }

    public boolean serverBufferFull() {
        return serverBufferCount >= NetworkConfig.SERVER_BUFFER_MAX;
    }

    public long getLastServerConsumeTime() {
        return lastServerConsumeTime;
    }

    public int getServerDecodeDelay() {
        return serverDecodeDelay;
    }

    public void upgradeServer() {
        if (serverDecodeDelay > 200) {
            serverDecodeDelay = Math.max(200, serverDecodeDelay - 300);
            log("【⚡ 硬件升级】: 服务器超频成功！解包延迟降至 " + serverDecodeDelay + "ms");
        }
    }

    public int getNextIpIdentifier() {
        return ipIdentifierCounter++;
    }

    private void log(String message) {
        if (callback != null) callback.onLog(message);
    }

    // Getter方法
    public Map<String, ArpEntry> getArpCache() { return arpCache; }
    public Map<String, DnsEntry> getDnsCache() { return dnsCache; }
    public Map<String, NatEntry> getNatTable() { return natTable; }
    public Set<Integer> getAckedSeq() { return ackedSeq; }
    public Set<Integer> getSentSeq() { return sentSeq; }
    public List<RetransmissionTask> getActiveTimers() { return activeTimers; }
    public FactoryManager getFactoryManager() { return factoryManager; }
    // 添加以下方法

    public void setUseUdp(boolean useUdp) {
        this.useUdp = useUdp;
    }

    public void setHttpDemoEnabled(boolean httpDemoEnabled) {
        this.httpDemoEnabled = httpDemoEnabled;
    }

    public void setTlsEnabled(boolean tlsEnabled) {
        this.tlsEnabled = tlsEnabled;
    }

    public void setTlsState(TlsState tlsState) {
        this.tlsState = tlsState;
    }

    public void setDhcpInProgress(boolean dhcpInProgress) {
        this.dhcpInProgress = dhcpInProgress;
    }

    public void setUdpActive(boolean udpActive) {
        this.udpActive = udpActive;
    }

    public void setUdpSeqToSend(int udpSeqToSend) {
        this.udpSeqToSend = udpSeqToSend;
    }

    public void setLastUdpSendTime(long lastUdpSendTime) {
        this.lastUdpSendTime = lastUdpSendTime;
    }

    public void setHttpSent(boolean httpSent) {
        this.httpSent = httpSent;
    }

    public void setHttpResponseContent(String httpResponseContent) {
        this.httpResponseContent = httpResponseContent;
    }

    public void setPacketsAckedSinceLastIncrease(int packetsAckedSinceLastIncrease) {
        this.packetsAckedSinceLastIncrease = packetsAckedSinceLastIncrease;
    }

    public void setStateTimerWatchdog(long stateTimerWatchdog) {
        this.stateTimerWatchdog = stateTimerWatchdog;
    }

    public void setDnsRetryCount(int dnsRetryCount) {
        this.dnsRetryCount = dnsRetryCount;
    }

    public void setLastDnsQueryTime(long lastDnsQueryTime) {
        this.lastDnsQueryTime = lastDnsQueryTime;
    }

    public void setTracerouteActive(boolean tracerouteActive) {
        this.tracerouteActive = tracerouteActive;
    }

    public void setTracerouteWaitReply(boolean tracerouteWaitReply) {
        this.tracerouteWaitReply = tracerouteWaitReply;
    }

    public void setTracerouteNextTTL(int tracerouteNextTTL) {
        this.tracerouteNextTTL = tracerouteNextTTL;
    }

    public void startDhcpIfNeeded(Point pcFactory) {
        if (!pcIpAssigned && !dhcpInProgress) {
            dhcpInProgress = true;
            DataPacket discover = createDataPacket(pcFactory.x, pcFactory.y, "DHCP_DISCOVER", 0);
            discover.setStage(1);
            if (callback != null) callback.addPendingPacket(discover);
            log("【🔎 DHCP】: 发送 DHCP Discover (PC 尚无 IP)");
            if (callback != null) callback.onUpdate();
        }
    }

    public void startTcpHandshake(Point pcFactory, String resolvedServerIp) {
        currentTcpState = TcpState.SYN_SENT;
        stateTimerWatchdog = System.currentTimeMillis();
        sentSeq.clear();
        ackedSeq.clear();

        DataPacket syn = createDataPacket(pcFactory.x, pcFactory.y, "SYN", 0);
        syn.setSequenceNumber(100);
        syn.setTtl(64);
        if (callback != null) callback.addPendingPacket(syn);
        log("【🤝 三次握手开始】: 发送 SYN (seq=100) 到 " + resolvedServerIp + " (TTL=64)");
        if (callback != null) callback.onUpdate();
    }

    public void startUdpTransmission() {
        udpActive = true;
        udpSeqToSend = 0;
        lastUdpSendTime = 0;
        serverReceivedCount = 0;
        serverBufferCount = 0;
        log("【🚀 UDP 模式】: 跳过握手，直接发送数据");
    }

    public void sendDataPackets(Point pcFactory) {
        int packetsToSend = Math.min(cwnd, NetworkConfig.TOTAL_DATA_TO_TRANSMIT - serverReceivedCount);
        for (int i = 0; i < packetsToSend; i++) {
            DataPacket data = createDataPacket(pcFactory.x, pcFactory.y, "DATA", nextSeqNum++);
            data.setTtl(64);
            data.setAdvertisedWindow(rwnd);
            if (callback != null) callback.addPendingPacket(data);
            sentSeq.add(data.getSequenceNumber());

            RetransmissionTask task = new RetransmissionTask(data.getSequenceNumber(), System.currentTimeMillis());
            activeTimers.add(task);

            log(String.format("【📤 TCP 发送】: SEQ=%d (cwnd=%d)", data.getSequenceNumber(), cwnd));
        }
    }

    public void sendHttpGet(Point pcFactory) {
        httpSent = true;
        stateTimerWatchdog = System.currentTimeMillis();
        DataPacket get = createDataPacket(pcFactory.x, pcFactory.y, "HTTP_GET", 0);
        get.setStage(5);
        if (callback != null) callback.addPendingPacket(get);
        log("【📡 HTTP】: 发送 GET /index.html HTTP/1.1");
    }

    public void sendTlsClientHello(Point pcFactory) {
        tlsState = TlsState.CLIENT_HELLO_SENT;
        DataPacket hello = createDataPacket(pcFactory.x, pcFactory.y, "TLS_CLIENT_HELLO", 0);
        hello.setStage(5);
        if (callback != null) callback.addPendingPacket(hello);
        log("【🔒 TLS】: 发送 Client Hello");
    }

    public void sendPing(Point pcFactory, String targetDomain) {
        if (!pcIpAssigned) {
            log("【⚠️ PING 失败】: PC 尚未获取 IP 地址，请等待 DHCP 完成");
            return;
        }
        DataPacket pingReq = createDataPacket(pcFactory.x, pcFactory.y, "ICMP_ECHO_REQ", 0);
        pingReq.setEchoSendTimestamp(System.currentTimeMillis());
        pingReq.setTtl(64);
        if (callback != null) callback.addPendingPacket(pingReq);
        log("【📡 PING】: 发送 ICMP Echo Request (TTL=64) 到 " + targetDomain);
    }

    public void startTraceroute(Point pcFactory, String targetDomain) {
        if (!pcIpAssigned) {
            log("【⚠️ TRACEROUTE 失败】: PC 尚未获取 IP 地址");
            return;
        }
        if (tracerouteActive) {
            log("【⚠️ TRACEROUTE】: 上一次追踪仍在进行中");
            return;
        }
        tracerouteActive = true;
        tracerouteNextTTL = 1;
        tracerouteWaitReply = false;
        log("【🔎 TRACEROUTE】: 开始追踪路由到 " + targetDomain);
        sendNextTracerouteProbe(pcFactory);
    }

    public void sendNextTracerouteProbe(Point pcFactory) {
        if (!tracerouteActive) return;
        DataPacket probe = createDataPacket(pcFactory.x, pcFactory.y, "ICMP_ECHO_REQ", 0);
        probe.setEchoSendTimestamp(System.currentTimeMillis());
        probe.setTtl(tracerouteNextTTL);
        if (callback != null) callback.addPendingPacket(probe);
        tracerouteWaitReply = true;
        log(String.format("【🔎 Traceroute】: 发送探测包 TTL=%d", tracerouteNextTTL));
    }

    // 辅助方法
    public void setCwnd(int cwnd) {
        this.cwnd = cwnd;
    }

    public void setSsthresh(int ssthresh) {
        this.ssthresh = ssthresh;
    }

    public void setRwnd(int rwnd) {
        this.rwnd = rwnd;
    }

    public void setResolvedServerIp(String resolvedServerIp) {
        this.resolvedServerIp = resolvedServerIp;
    }

    public void setDnsResolved(boolean dnsResolved) {
        this.dnsResolved = dnsResolved;
    }

    public void setDnsResolving(boolean dnsResolving) {
        this.dnsResolving = dnsResolving;
    }

    public void setDemoCompleted(boolean demoCompleted) {
        this.demoCompleted = demoCompleted;
    }

    public void setServerDecodeDelay(int serverDecodeDelay) {
        this.serverDecodeDelay = serverDecodeDelay;
    }

    public void setServerBufferCount(int serverBufferCount) {
        this.serverBufferCount = serverBufferCount;
    }

    public void setServerReceivedCount(int serverReceivedCount) {
        this.serverReceivedCount = serverReceivedCount;
    }

    public void setNextSeqNum(int nextSeqNum) {
        this.nextSeqNum = nextSeqNum;
    }

    public void setFunds(int funds) {
        this.funds = funds;
    }

    public void setHelloStock(int helloStock) {
        this.helloStock = helloStock;
    }

    public void setSayStock(int sayStock) {
        this.sayStock = sayStock;
    }
}