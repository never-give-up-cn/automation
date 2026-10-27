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
    /** UDP 模式下两个数据报之间的发送间隔 */
    private static final long UDP_SEND_INTERVAL_MS = 200;

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
    @Getter @Setter private String targetDomain = "www.demo.com";

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
        // 预置的 ARP 表项同步到引擎缓存，保证 ARP 缓存面板一开始就有内容
        if (factoryManager != null && factoryManager.getArpCache() != null) {
            factoryManager.getArpCache().getCache().forEach((ip, entry) ->
                    this.arpCache.put(ip, new ArpEntry(ip, entry.getMacAddress())));
        }
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
                    this::handlePacketArrival,
                    NetworkConfig.WAN_BOTTLE_NECK_MAX,
                    null
            );

            // 注入建筑坐标查找器：DataPacket → GameCallback → MapManager
            packet.setCoordsLookup(callback::findBuildingCoords);
        }

        return packet;
    }

    // 添加辅助方法
    private void addPendingPacketToCallback(DataPacket packet) {
        if (callback != null) callback.addPendingPacket(packet);
    }

    /** 把新产生的协议报文放进待发队列 */
    private void enqueue(DataPacket packet) {
        if (callback != null) callback.addPendingPacket(packet);
    }

    // ======================== 协议状态机 ========================

    /**
     * 数据包到达后的协议状态机。
     * <p>对应旧版 {@code DataCartFactoryGame.handleCartArrival}，驱动
     * DHCP → DNS → TCP 三次握手 → 数据传输 → 四次挥手 的完整流程。
     * </p>
     */
    public void handlePacketArrival(DataPacket packet) {
        if (packet == null || packet.getCartType() == null) return;

        Point pc = callback != null ? callback.getPcFactory() : null;
        Point server = callback != null ? callback.findBuildingCoords("RX_ST") : null;
        Point dhcpServer = callback != null ? callback.findBuildingCoords("DHCP_SERVER") : null;
        if (pc == null || server == null) return;

        String type = packet.getCartType();

        switch (type) {
            // ---------------- DHCP 四步 ----------------
            case "DHCP_DISCOVER":
                if (!packet.isReturnTrip() && packet.getStage() == 2) {
                    log("【🔎 DHCP】: Discover 到达 DHCP 服务器");
                    if (dhcpServer != null) {
                        DataPacket offer = createDataPacket(dhcpServer.x, dhcpServer.y, "DHCP_OFFER", 0);
                        offer.setReturnTrip(true);
                        offer.setStage(1);
                        enqueue(offer);
                    }
                    log("【📥 DHCP】: 服务器回复 Offer (192.168.1.100)");
                }
                break;

            case "DHCP_OFFER":
                if (packet.isReturnTrip()) {
                    log("【📥 DHCP】: Offer 到达客户端");
                    DataPacket request = createDataPacket(pc.x, pc.y, "DHCP_REQUEST", 0);
                    request.setStage(1);
                    enqueue(request);
                    log("【📤 DHCP】: 发送 Request");
                }
                break;

            case "DHCP_REQUEST":
                if (!packet.isReturnTrip() && packet.getStage() == 2) {
                    log("【📤 DHCP】: Request 到达 DHCP 服务器");
                    if (dhcpServer != null) {
                        DataPacket ack = createDataPacket(dhcpServer.x, dhcpServer.y, "DHCP_ACK", 0);
                        ack.setReturnTrip(true);
                        ack.setStage(1);
                        enqueue(ack);
                    }
                    log("【✅ DHCP】: 服务器回复 ACK，分配 IP 192.168.1.100");
                }
                break;

            case "DHCP_ACK":
                if (packet.isReturnTrip()) {
                    log("【✅ DHCP】: ACK 到达客户端，IP 分配成功");
                    pcIpAssigned = true;
                    pcIpAddress = "192.168.1.100";
                    dhcpInProgress = false;
                    funds += 200;
                    log("【🌐 网络就绪】: PC IP = " + pcIpAddress);
                    if (callback != null) callback.onUpdate();
                }
                break;

            // ---------------- DNS 递归查询 ----------------
            case "DNS_QUERY":
                if (!packet.isReturnTrip() && packet.getStage() >= 2) {
                    log("【📥 本地 DNS】: 无缓存，向根 DNS 发起递归查询");
                    DataPacket toRoot = createDataPacket(packet.getX(), packet.getY(), "DNS_RECURSION_ROOT", 0);
                    toRoot.setDomain(packet.getDomain());
                    toRoot.setReturnTrip(false);
                    enqueue(toRoot);
                }
                break;

            case "DNS_RECURSION_ROOT":
                if (!packet.isReturnTrip() && packet.isArrived()) {
                    log("【🌐 根 DNS】: 返回顶级域权威 DNS 地址 10.1.1.1");
                    DataPacket rootToLocal = createDataPacket(packet.getX(), packet.getY(), "DNS_ROOT_TO_LOCAL", 0);
                    rootToLocal.setDomain(packet.getDomain());
                    rootToLocal.setResolvedIp("10.1.1.1");
                    enqueue(rootToLocal);
                }
                break;

            case "DNS_ROOT_TO_LOCAL":
                if (!packet.isReturnTrip() && packet.isArrived()) {
                    log("【📡 本地 DNS】: 向权威 DNS 查询 " + packet.getDomain());
                    DataPacket toAuth = createDataPacket(packet.getX(), packet.getY(), "DNS_LOCAL_TO_AUTH", 0);
                    toAuth.setDomain(packet.getDomain());
                    enqueue(toAuth);
                }
                break;

            case "DNS_LOCAL_TO_AUTH":
                if (!packet.isReturnTrip() && packet.isArrived()) {
                    log("【🏢 权威 DNS】: " + packet.getDomain() + " → 10.0.0.1");
                    DataPacket authToLocal = createDataPacket(packet.getX(), packet.getY(), "DNS_AUTH_TO_LOCAL", 0);
                    authToLocal.setDomain(packet.getDomain());
                    authToLocal.setResolvedIp("10.0.0.1");
                    enqueue(authToLocal);
                }
                break;

            case "DNS_AUTH_TO_LOCAL":
                if (!packet.isReturnTrip() && packet.isArrived()) {
                    log("【📡 本地 DNS】: 缓存结果 " + packet.getDomain() + " → " + packet.getResolvedIp());
                    DataPacket finalResp = createDataPacket(packet.getX(), packet.getY(), "DNS_RESPONSE", 0);
                    finalResp.setDomain(packet.getDomain());
                    finalResp.setResolvedIp(packet.getResolvedIp());
                    finalResp.setReturnTrip(true);
                    finalResp.setStage(-1);
                    enqueue(finalResp);
                }
                break;

            case "DNS_RESPONSE":
                if (packet.isReturnTrip() && packet.isArrived()) {
                    onDnsResponse(packet.getDomain(), packet.getResolvedIp());
                    log("【✅ DNS 解析成功】: " + packet.getDomain() + " → " + packet.getResolvedIp());
                    performArpResolution(packet.getResolvedIp());
                    if (!useUdp && currentTcpState == TcpState.CLOSED) {
                        startTcpHandshake(pc, resolvedServerIp);
                    } else if (useUdp && !udpActive) {
                        startUdpTransmission();
                    }
                }
                break;

            // ---------------- TCP 三次握手 ----------------
            case "SYN":
                if (!packet.isReturnTrip() && packet.isArrived()) {
                    log("【🤝 三次握手】: 收到 SYN，回复 SYN-ACK (seq=200)");
                    DataPacket synAck = createDataPacket(server.x, server.y, "SYN_ACK", 0);
                    synAck.setReturnTrip(true);
                    synAck.setAckNumber(packet.getSequenceNumber() + 1);
                    synAck.setSequenceNumber(200);
                    enqueue(synAck);
                    stateTimerWatchdog = System.currentTimeMillis();
                }
                break;

            case "SYN_ACK":
                if (packet.isArrived()) {
                    currentTcpState = TcpState.ESTABLISHED;
                    stateTimerWatchdog = System.currentTimeMillis();
                    cwnd = 1;
                    ssthresh = 12;
                    packetsAckedSinceLastIncrease = 0;
                    log("【✅ 三次握手完成】: 收到 SYN-ACK，发送 ACK，连接建立！cwnd=1, ssthresh=12");

                    DataPacket finalAck = createDataPacket(pc.x, pc.y, "ACK_PC", 0);
                    finalAck.setAckNumber(packet.getSequenceNumber() + 1);
                    finalAck.setReturnTrip(false);
                    finalAck.setStage(2);
                    enqueue(finalAck);

                    if (httpDemoEnabled && !httpSent) {
                        if (tlsEnabled && tlsState == TlsState.IDLE) {
                            sendTlsClientHello(pc);
                        } else if (!tlsEnabled) {
                            sendHttpGet(pc);
                        }
                    }
                    if (callback != null) callback.onUpdate();
                }
                break;

            case "ACK_PC":
                // 三次握手的第三个报文，到达接收端即结束
                break;

            // ---------------- 数据传输 ----------------
            case "DATA":
                if (!packet.isReturnTrip() && packet.isArrived()) {
                    if (serverBufferCount < NetworkConfig.SERVER_BUFFER_MAX) {
                        serverBufferCount++;
                        if (serverBufferCount == 1) {
                            lastServerConsumeTime = System.currentTimeMillis();
                        }
                        rwnd = NetworkConfig.SERVER_BUFFER_MAX - serverBufferCount;

                        int receivedSeq = packet.getSequenceNumber();
                        if (receivedSeq > 0 && !ackedSeq.contains(receivedSeq)) {
                            ackedSeq.add(receivedSeq);
                            DataPacket dataAck = createDataPacket(server.x, server.y, "DATA_ACK", receivedSeq);
                            dataAck.setAdvertisedWindow(rwnd);
                            dataAck.setReturnTrip(true);
                            enqueue(dataAck);
                            funds += 500;
                            log(String.format("【📦 数据交付】: SEQ=%d 已接收，回复 ACK (rwnd=%d)", receivedSeq, rwnd));
                        } else {
                            log(String.format("【⚠️ 重复包】: SEQ=%d 已处理过", receivedSeq));
                        }
                    } else {
                        log(String.format("【💥 缓冲区溢出】: SEQ=%d 丢失", packet.getSequenceNumber()));
                    }
                    if (callback != null) callback.onUpdate();
                }
                break;

            case "UDP_DATA":
                if (!packet.isReturnTrip() && packet.isArrived()) {
                    if (serverBufferCount < NetworkConfig.SERVER_BUFFER_MAX) {
                        serverBufferCount++;
                        if (serverBufferCount == 1) {
                            lastServerConsumeTime = System.currentTimeMillis();
                        }
                        rwnd = NetworkConfig.SERVER_BUFFER_MAX - serverBufferCount;
                        log(String.format("【📦 UDP 交付】: 缓冲区 %d/%d", serverBufferCount, NetworkConfig.SERVER_BUFFER_MAX));
                    }
                }
                break;

            case "DATA_ACK":
                if (packet.isReturnTrip() && packet.isArrived()) {
                    rwnd = packet.getAdvertisedWindow();
                    if (packet.getSequenceNumber() > 0) {
                        ackRetransmissionTask(packet.getSequenceNumber());
                        if (cwnd < ssthresh) {
                            cwnd++;
                            log(String.format("【📈 慢启动】: cwnd=%d, ssthresh=%d", cwnd, ssthresh));
                        } else {
                            packetsAckedSinceLastIncrease++;
                            if (packetsAckedSinceLastIncrease >= cwnd) {
                                cwnd++;
                                packetsAckedSinceLastIncrease = 0;
                                log(String.format("【🐌 拥塞避免】: cwnd=%d", cwnd));
                            }
                        }
                    }
                    if (callback != null) callback.onUpdate();
                }
                break;

            // ---------------- HTTP 演示 ----------------
            case "HTTP_GET":
                if (packet.isArrived()) {
                    log("【📡 HTTP】: 服务器收到 GET，回复 200 OK");
                    DataPacket httpOk = createDataPacket(server.x, server.y, "HTTP_200_OK", 0);
                    httpOk.setReturnTrip(true);
                    httpOk.setStage(-1);
                    httpOk.setHttpBody("Hello World");
                    enqueue(httpOk);
                }
                break;

            case "HTTP_200_OK":
                if (packet.isReturnTrip() && packet.isArrived()) {
                    httpResponseContent = packet.getHttpBody();
                    log("【📡 HTTP】: 收到 200 OK, 内容: " + httpResponseContent);
                    currentTcpState = TcpState.FIN_WAIT_1;
                    stateTimerWatchdog = System.currentTimeMillis();
                    DataPacket fin = createDataPacket(pc.x, pc.y, "FIN_PC", 0);
                    fin.setTtl(64);
                    enqueue(fin);
                    log("【🏁 HTTP 完成】: 发送 FIN，开始四次挥手");
                }
                break;

            // ---------------- TCP 四次挥手 ----------------
            case "FIN_PC":
                if (!packet.isReturnTrip() && packet.isArrived()) {
                    currentTcpState = TcpState.FIN_WAIT_1;
                    log("【👋 四次挥手】: 收到 FIN，回复 FIN-ACK，发送 FIN");
                    DataPacket finAck = createDataPacket(server.x, server.y, "FIN_ACK_SRV", 0);
                    finAck.setReturnTrip(true);
                    enqueue(finAck);
                    DataPacket srvFin = createDataPacket(server.x, server.y, "FIN_SRV", 0);
                    srvFin.setReturnTrip(true);
                    enqueue(srvFin);
                    stateTimerWatchdog = System.currentTimeMillis();
                }
                break;

            case "FIN_ACK_SRV":
                if (currentTcpState == TcpState.FIN_WAIT_1) {
                    currentTcpState = TcpState.FIN_WAIT_2;
                    log("【👋 四次挥手】: 收到 FIN-ACK，进入 FIN-WAIT-2");
                    stateTimerWatchdog = System.currentTimeMillis();
                }
                break;

            case "FIN_SRV":
                if (currentTcpState == TcpState.FIN_WAIT_2 || currentTcpState == TcpState.FIN_WAIT_1) {
                    currentTcpState = TcpState.TIME_WAIT;
                    DataPacket lastAck = createDataPacket(pc.x, pc.y, "LAST_ACK_PC", 0);
                    lastAck.setReturnTrip(false);
                    enqueue(lastAck);
                    log("【✅ 四次挥手完成】: 连接进入 TIME-WAIT");
                }
                break;

            case "LAST_ACK_PC":
                demoCompleted = true;
                log("🎉 数据传输完成！共交付 " + serverReceivedCount + "/" + NetworkConfig.TOTAL_DATA_TO_TRANSMIT + " 个数据包");
                if (callback != null) callback.onUpdate();
                break;

            // ---------------- ICMP ----------------
            case "ICMP_ECHO_REQ":
                if (!packet.isReturnTrip() && packet.isArrived()) {
                    log("【📥 ICMP】: 服务器收到 Echo Request，回复 Echo Reply");
                    DataPacket echoReply = createDataPacket(server.x, server.y, "ICMP_ECHO_REPLY", 0);
                    echoReply.setReturnTrip(true);
                    echoReply.setEchoSendTimestamp(packet.getEchoSendTimestamp());
                    echoReply.setTtl(64);
                    echoReply.setStage(-1);
                    enqueue(echoReply);
                }
                break;

            default:
                break;
        }
    }

    /**
     * 服务器按真实时间节流地从接收缓冲区里取出一个已解包的数据包。
     */
    public void processServerBuffer(long now) {
        if (serverBufferCount > 0 && now - lastServerConsumeTime >= serverDecodeDelay) {
            consumeServerBuffer();
        }
    }

    /**
     * 服务器解包并处理一个数据包（与真实时间解耦，便于无界面测试）。
     */
    public void consumeServerBuffer() {
        if (serverBufferCount <= 0) return;
        decrementServerBuffer();
        incrementServerReceived();
        log(String.format("【📥 服务器处理】: 已处理 %d/%d 个数据包",
                serverReceivedCount, NetworkConfig.TOTAL_DATA_TO_TRANSMIT));
    }

    /**
     * 流程驱动：由 UI 的定时器周期调用，按协议顺序推进整个会话。
     * <p>DHCP → DNS → TCP 握手 → 数据发送</p>
     */
    public void tick() {
        if (callback == null) return;
        Point pc = callback.getPcFactory();
        if (pc == null) return;

        // 0) 服务器解包缓冲区
        processServerBuffer(System.currentTimeMillis());

        // 1) 还没有 IP → 发起 DHCP
        if (!pcIpAssigned) {
            if (!dhcpInProgress) {
                startDhcpIfNeeded(pc);
            }
            return;
        }

        // 2) IP 就绪 → 发起 DNS 解析（解析完成前不进入 TCP 阶段）
        if (!dnsResolved) {
            if (!dnsResolving) {
                startDnsResolution(targetDomain);
            }
            return;
        }

        // 3) UDP 模式：跳过握手，直接发数据
        if (useUdp) {
            if (!udpActive) {
                startUdpTransmission();
                return;
            }
            long now = System.currentTimeMillis();
            if (now - lastUdpSendTime >= UDP_SEND_INTERVAL_MS
                    && canSendData()
                    && serverBufferCount < NetworkConfig.SERVER_BUFFER_MAX) {
                lastUdpSendTime = now;
                DataPacket udp = createDataPacket(pc.x, pc.y, "UDP_DATA", nextSeqNum++);
                udp.setTtl(64);
                udp.setAdvertisedWindow(rwnd);
                enqueue(udp);
                sentSeq.add(udp.getSequenceNumber());
                log(String.format("【📤 UDP 发送】: SEQ=%d", udp.getSequenceNumber()));
            }
            return;
        }

        if (demoCompleted) {
            return;
        }

        // 4) TCP 模式：发起三次握手
        if (currentTcpState == TcpState.CLOSED) {
            startTcpHandshake(pc, resolvedServerIp);
            return;
        }

        // 5) 连接已建立 → 按滑动窗口发送数据
        if (currentTcpState == TcpState.ESTABLISHED && canSendData()) {
            int window = Math.min(cwnd, rwnd);
            int unacked = sentSeq.size() - ackedSeq.size();
            if (window - unacked > 0) {
                sendDataPackets(pc);
            }
        }
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

        if (!pcIpAssigned) {
            log("【⛔ DNS 阻断】: PC 尚未获取 IP 地址，等待 DHCP 完成");
            return;
        }
        Point pc = callback != null ? callback.getPcFactory() : null;
        if (pc == null) return;

        dnsResolving = true;
        lastDnsQueryTime = System.currentTimeMillis();

        DnsEntry cached = dnsCache.get(targetDomain);
        if (cached != null && !cached.isExpired()) {
            resolvedServerIp = cached.getIpAddress();
            dnsResolved = true;
            dnsResolving = false;
            log("【📚 DNS缓存命中】: " + targetDomain + " → " + resolvedServerIp);
            performArpResolution(resolvedServerIp);
            if (!useUdp && currentTcpState == TcpState.CLOSED) {
                startTcpHandshake(pc, resolvedServerIp);
            } else if (useUdp && !udpActive) {
                startUdpTransmission();
            }
            if (callback != null) callback.onUpdate();
            return;
        }

        log("【🌐 DNS解析开始】: 查询域名 " + targetDomain);
        DataPacket query = createDataPacket(pc.x, pc.y, "DNS_QUERY", 0);
        query.setDomain(targetDomain);
        enqueue(query);
        log("【📤 DNS 查询】: 发送请求到本地 DNS 服务器");
        if (callback != null) callback.onUpdate();
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
                        DataPacket retransmit = createDataPacket(pcFactory.x, pcFactory.y, "DATA", task.getSeqNum());
                        retransmit.setRetransmission(true);
                        retransmit.setTtl(64);
                        retransmit.setAdvertisedWindow(rwnd);
                        enqueue(retransmit);
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
        udpActive = false;
        httpSent = false;
        tlsState = TlsState.IDLE;

        // 注意：DHCP 租约（pcIpAssigned）保留，重置会话不会重新分配地址
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
        int window = Math.min(cwnd, rwnd);
        int unacked = sentSeq.size() - ackedSeq.size();
        int canSend = window - unacked;
        if (canSend <= 0) {
            log("【⏸️ 窗口已满】: cwnd=" + cwnd + ", rwnd=" + rwnd + ", unacked=" + unacked);
            return;
        }
        int packetsToSend = Math.min(canSend, NetworkConfig.TOTAL_DATA_TO_TRANSMIT - serverReceivedCount);
        for (int i = 0; i < packetsToSend; i++) {
            DataPacket data = createDataPacket(pcFactory.x, pcFactory.y, "DATA", nextSeqNum++);
            data.setTtl(64);
            data.setAdvertisedWindow(rwnd);
            enqueue(data);
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