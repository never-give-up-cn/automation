package com.never_give_up.automation.demo.engine;

import com.never_give_up.automation.demo.adapter.FactoryManager;
import com.never_give_up.automation.demo.winModel.DataPacket;
import com.never_give_up.automation.demo.winModel.TcpState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 网络协议栈动画流程的无界面回归测试。
 * <p>
 * 复现并锁定「数据包不流动」的问题：数据包必须按照
 * DHCP 四步 → DNS 递归查询 → TCP 三次握手 → 数据交付 的顺序依次抵达，
 * 而不是创建后立刻「原地消失」。
 * </p>
 */
class NetworkFlowAnimationTest {

    /** 无界面的回调实现：不依赖 Swing，只收集报文与日志 */
    private static class TestCallback implements GameEngine.GameCallback {
        private final MapManager mapManager = new MapManager();
        private final List<DataPacket> pending = new ArrayList<>();
        private final List<String> logs = new ArrayList<>();
        private final List<String> arrivedTypes = new ArrayList<>();
        private final List<DataPacket> arrivedPackets = new ArrayList<>();
        private final Set<String> distinctArrived = new LinkedHashSet<>();

        @Override
        public void onLog(String message) {
            logs.add(message);
        }

        @Override
        public void onUpdate() {
        }

        @Override
        public void onPacketArrived(DataPacket packet) {
        }

        @Override
        public void addPendingPacket(DataPacket packet) {
            pending.add(packet);
        }

        @Override
        public Point getPcFactory() {
            return mapManager.getPcFactory();
        }

        @Override
        public Point findBuildingCoords(String tag) {
            return mapManager.findBuildingCoords(tag);
        }

        @Override
        public List<Point> getBeltPath(String fromTag, String toTag) {
            return mapManager.getProductionLine().route(fromTag, toTag);
        }

        @Override
        public String findTagAt(Point point) {
            return mapManager.findTagAt(point);
        }

        void recordArrival(DataPacket packet) {
            arrivedTypes.add(packet.getCartType());
            arrivedPackets.add(packet);
            distinctArrived.add(packet.getCartType());
        }

        boolean sawLog(String keyword) {
            return logs.stream().anyMatch(l -> l.contains(keyword));
        }
    }

    private TestCallback callback;
    private GameEngine engine;
    private final List<DataPacket> active = new ArrayList<>();

    @BeforeEach
    void setUp() {
        callback = new TestCallback();
        engine = new GameEngine(new FactoryManager());
        engine.setCallback(callback);
        active.clear();
    }

    /** 模拟 UI 的 30ms 定时器：推进所有报文，直到指定 tick 数用完 */
    private void run(int maxTicks) {
        run(maxTicks, true);
    }

    /**
     * @param drainServer 是否让服务器按帧消费缓冲区。
     *                    UI 里是按真实时间节流解包的，无界面测试里改成按帧解包，
     *                    否则接收缓冲区会一直满着，滑动窗口被关闭后流程就停了。
     */
    private void run(int maxTicks, boolean drainServer) {
        for (int i = 0; i < maxTicks; i++) {
            engine.tick();

            if (drainServer && i % 10 == 0 && engine.getServerBufferCount() > 0) {
                engine.consumeServerBuffer();
            }

            if (!callback.pending.isEmpty()) {
                active.addAll(callback.pending);
                callback.pending.clear();
            }

            for (DataPacket packet : new ArrayList<>(active)) {
                packet.update();
                if (packet.isArrived() && !packet.isArrivalNotified()) {
                    packet.setArrivalNotified(true);
                    callback.recordArrival(packet);
                    engine.handlePacketArrival(packet);
                }
            }
            active.removeIf(p -> p.isDropped() || p.isArrived());
        }
    }

    @Test
    @DisplayName("数据包必须真实移动，而不是创建后立刻原地消失")
    void packetShouldActuallyMove() {
        Point pc = callback.getPcFactory();
        Point disc = callback.findBuildingCoords("DHCP_DISC");

        DataPacket discover = engine.createDataPacket(pc.x, pc.y, "DHCP_DISCOVER", 0);
        discover.setStage(1);
        callback.addPendingPacket(discover);

        // 第一个 tick：报文朝 DHCP_DISC 移动，而不是直接抵达
        active.add(discover);
        active.get(0).update();

        assertFalse(active.get(0).isArrived(), "DHCP Discover 不应该在第一帧就抵达");
        assertTrue(active.get(0).getX() != pc.x || active.get(0).getY() != pc.y,
                "DHCP Discover 应该朝 DHCP_DISC 移动");
        assertTrue(Math.hypot(active.get(0).getX() - disc.x, active.get(0).getY() - disc.y)
                        < Math.hypot(pc.x - disc.x, pc.y - disc.y),
                "DHCP Discover 应该离目标更近了");
    }

    @Test
    @DisplayName("DHCP 四步完整走通并分配 IP")
    void dhcpFourStepCompletes() {
        run(600);

        assertTrue(callback.distinctArrived.contains("DHCP_DISCOVER"), "缺少 DHCP Discover");
        assertTrue(callback.distinctArrived.contains("DHCP_OFFER"), "缺少 DHCP Offer");
        assertTrue(callback.distinctArrived.contains("DHCP_REQUEST"), "缺少 DHCP Request");
        assertTrue(callback.distinctArrived.contains("DHCP_ACK"), "缺少 DHCP ACK");

        assertTrue(engine.isPcIpAssigned(), "DHCP 完成后 PC 应该拿到 IP");
        assertEquals("192.168.1.100", engine.getPcIpAddress());
        assertFalse(engine.isDhcpInProgress(), "DHCP 结束后 inProgress 应该复位");
    }

    @Test
    @DisplayName("DNS 递归查询按顺序抵达各权威服务器")
    void dnsResolutionFollowsProtocolOrder() {
        run(1500);

        assertTrue(callback.distinctArrived.contains("DNS_QUERY"), "缺少 DNS 查询");
        assertTrue(callback.distinctArrived.contains("DNS_RECURSION_ROOT"), "缺少向根 DNS 的递归查询");
        assertTrue(callback.distinctArrived.contains("DNS_ROOT_TO_LOCAL"), "缺少根 DNS → 本地 DNS");
        assertTrue(callback.distinctArrived.contains("DNS_LOCAL_TO_AUTH"), "缺少本地 DNS → 权威 DNS");
        assertTrue(callback.distinctArrived.contains("DNS_AUTH_TO_LOCAL"), "缺少权威 DNS → 本地 DNS");
        assertTrue(callback.distinctArrived.contains("DNS_RESPONSE"), "缺少 DNS 响应");

        assertTrue(engine.isDnsResolved(), "DNS 应该解析成功");
        assertEquals("10.0.0.1", engine.getResolvedServerIp());

        // 顺序校验：查询必须早于响应
        int queryIdx = callback.arrivedTypes.indexOf("DNS_QUERY");
        int respIdx = callback.arrivedTypes.indexOf("DNS_RESPONSE");
        assertTrue(queryIdx >= 0 && respIdx > queryIdx, "DNS 响应必须晚于查询");
    }

    @Test
    @DisplayName("TCP 三次握手成功后数据能够交付到接收端")
    void tcpHandshakeAndDataDelivery() {
        run(6000);

        assertTrue(callback.distinctArrived.contains("SYN"), "缺少 SYN");
        assertTrue(callback.distinctArrived.contains("SYN_ACK"), "缺少 SYN-ACK");
        assertTrue(callback.distinctArrived.contains("ACK_PC"), "缺少第三个握手报文 ACK");
        assertTrue(callback.distinctArrived.contains("DATA"), "数据报文没有抵达接收端");

        assertEquals(TcpState.ESTABLISHED, engine.getCurrentTcpState(), "TCP 应该已经建立连接");
        assertTrue(engine.getServerReceivedCount() > 0, "接收端应该已经处理过数据报文");
        assertTrue(engine.getUnackedCount() >= 0);
        assertTrue(callback.sawLog("三次握手完成"), "缺少三次握手完成日志");
    }

    @Test
    @DisplayName("交付的数据报文序号必须唯一，不能被 TCP 工厂覆盖")
    void deliveredDataSequenceNumbersAreUnique() {
        run(6000);

        List<Integer> dataSeqs = new ArrayList<>();
        for (DataPacket packet : callback.arrivedPackets) {
            if ("DATA".equals(packet.getCartType())) {
                dataSeqs.add(packet.getSequenceNumber());
            }
        }
        assertTrue(dataSeqs.size() > 0, "应该有数据报文抵达接收端");
        assertEquals(dataSeqs.size(), dataSeqs.stream().distinct().count(),
                "交付的数据报文序号出现了重复: " + dataSeqs);

        // 引擎侧的已发送 / 已确认序号也必须唯一
        assertTrue(engine.getSentSeq().size() > 0, "应该已经发送过数据");
        assertEquals(engine.getSentSeq().size(),
                engine.getSentSeq().stream().distinct().count(), "发送序号出现了重复");
        assertEquals(engine.getAckedSeq().size(),
                engine.getAckedSeq().stream().distinct().count(), "已确认序号出现了重复");
    }

    @Test
    @DisplayName("服务器消费缓冲区后计入已交付数量")
    void serverConsumesBufferedPackets() {
        // 不消费缓冲区：让报文先在接收端排队，才能稳定地检查消费逻辑
        run(3000, false);
        assertTrue(engine.getServerBufferCount() > 0, "接收端缓冲区应该已经收到数据");

        int receivedBefore = engine.getServerReceivedCount();
        int bufferBefore = engine.getServerBufferCount();
        engine.consumeServerBuffer();
        assertEquals(receivedBefore + 1, engine.getServerReceivedCount());
        assertEquals(bufferBefore - 1, engine.getServerBufferCount(), "消费一个包后缓冲区应减少");
    }

    @Test
    @DisplayName("报文沿传送带走线行进，不会脱离传送带直线穿场")
    void packetsFollowTheConveyorBelt() {
        Point pc = callback.getPcFactory();
        // DHCP 的发现报文会依次经过 DHCP_DISC → DHCP_SERVER，取整段路径
        List<Point> belt = callback.mapManager.getProductionLine().route("PC_FACTORY", "DHCP_SERVER");
        assertTrue(belt != null && !belt.isEmpty(), "PC 到 DHCP_SERVER 应该有传送带路径");

        DataPacket discover = engine.createDataPacket(pc.x, pc.y, "DHCP_DISCOVER", 0);
        discover.setStage(1);
        callback.addPendingPacket(discover);
        active.add(discover);

        double maxDeviation = 0;
        int steps = 0;
        boolean passedDisc = false;
        for (int i = 0; i < 600 && !discover.isArrived(); i++) {
            discover.update();
            steps++;
            passedDisc |= "DHCP_DISC".equals(discover.getCurrentTag());
            maxDeviation = Math.max(maxDeviation,
                    distanceToPath(discover.getX(), discover.getY(), pc, belt));
        }

        assertTrue(steps > 5, "报文应该分多帧沿传送带行进");
        assertTrue(passedDisc, "报文应该先经过 DHCP_DISCOVER 再去 DHCP 服务器");
        assertTrue(discover.isArrived(), "报文应该能沿传送带抵达 DHCP_SERVER");
        assertTrue(maxDeviation <= 40,
                "报文偏离传送带 " + String.format("%.1f", maxDeviation) + "px，不应该直线穿场");
    }

    /** 计算点到折线路径的最短距离（用于校验报文是否走在传送带上） */
    private double distanceToPath(double px, double py, Point start, List<Point> path) {
        double best = Double.MAX_VALUE;
        Point prev = start;
        for (Point p : path) {
            best = Math.min(best, distanceToSegment(px, py, prev.x, prev.y, p.x, p.y));
            prev = p;
        }
        return best;
    }

    private double distanceToSegment(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1, dy = y2 - y1;
        double len2 = dx * dx + dy * dy;
        if (len2 == 0) {
            return Math.hypot(px - x1, py - y1);
        }
        double t = Math.max(0, Math.min(1, ((px - x1) * dx + (py - y1) * dy) / len2));
        return Math.hypot(px - (x1 + t * dx), py - (y1 + t * dy));
    }
}
