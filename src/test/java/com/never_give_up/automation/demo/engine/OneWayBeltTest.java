package com.never_give_up.automation.demo.engine;

import com.never_give_up.automation.demo.adapter.FactoryManager;
import com.never_give_up.automation.demo.winModel.DataPacket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 「传送带只能单向运输」的回归测试。
 * <p>
 * 产线上的每条传送带都有固定方向：去程带把工件往下游送，回程带在去程带
 * 旁边错开一条车道，把响应报文送回上游。报文绝不允许逆着箭头跑。
 * </p>
 */
class OneWayBeltTest {

    private MapManager mapManager;
    private ProductionLine line;

    @BeforeEach
    void setUp() {
        mapManager = new MapManager();
        line = mapManager.getProductionLine();
    }

    /** 把所有传送带的有向线段登记下来：线段方向 = 允许的运输方向 */
    private Map<String, ProductionLine.Belt> directedSegments() {
        Map<String, ProductionLine.Belt> segs = new HashMap<>();
        for (ProductionLine.Belt belt : line.getBelts()) {
            List<Point> pts = belt.getPoints();
            for (int i = 1; i < pts.size(); i++) {
                segs.put(key(pts.get(i - 1), pts.get(i)), belt);
            }
        }
        return segs;
    }

    private static String key(Point a, Point b) {
        return a.x + "," + a.y + ">" + b.x + "," + b.y;
    }

    /** 报文在带上行进的每一步，都必须落在某条传送带允许的方向上 */
    private void assertNeverAgainstArrows(String fromTag, String toTag) {
        List<Point> path = line.route(fromTag, toTag);
        assertNotNull(path, fromTag + " → " + toTag + " 应该有一条单向通路");
        assertFalse(path.isEmpty(), fromTag + " → " + toTag + " 的路径不该是空的");

        Map<String, ProductionLine.Belt> segs = directedSegments();
        Point cur = line.getPositions().get(fromTag);
        assertNotNull(cur, fromTag + " 不在产线上");
        for (Point p : path) {
            String k = key(cur, p);
            assertTrue(segs.containsKey(k),
                    "报文从 " + cur + " 走到 " + p + " 是逆着传送带箭头的（" + fromTag + " → " + toTag + "）");
            cur = p;
        }
    }

    @Test
    @DisplayName("去程带只能把工件往下游送，回程带只能往上游送")
    void forwardAndReturnBeltsRunOppositeWays() {
        int forward = 0;
        int back = 0;
        for (ProductionLine.Belt belt : line.getBelts()) {
            if (belt.isForward()) {
                forward++;
            } else {
                back++;
            }
        }
        assertTrue(forward > 60, "去程带数量不对：" + forward);
        assertTrue(back > 60, "回程带数量不对：" + back);
    }

    @Test
    @DisplayName("主干道去程：从 PC 到接收服务器顺着去程带走")
    void mainLineForwardUsesForwardBelts() {
        assertNeverAgainstArrows("PC_FACTORY", "RX_ST");
        List<Point> path = line.route("PC_FACTORY", "RX_ST");
        // 去程带就在三行主干道上，y 只能是 336 / 720 / 1104
        for (Point p : path) {
            assertTrue(p.y == 336 || p.y == 720 || p.y == 1104,
                    "去程路径上的点应该落在主干道上，实际 " + p);
        }
    }

    @Test
    @DisplayName("回程：从接收服务器回到 PC 走的是错开的回程带")
    void mainLineReturnUsesReturnBelts() {
        assertNeverAgainstArrows("RX_ST", "PC_FACTORY");
        List<Point> path = line.route("RX_ST", "PC_FACTORY");
        // 回程带错开一条车道，永远不会和去程带重合
        Set<Integer> ys = new HashSet<>();
        for (Point p : path) {
            ys.add(p.y);
        }
        assertTrue(ys.contains(384), "回程带应该错开在 384 这一行，实际 " + ys);
        assertTrue(ys.contains(768), "回程带应该错开在 768 这一行，实际 " + ys);
        assertTrue(ys.contains(1152), "回程带应该错开在 1152 这一行，实际 " + ys);
    }

    @Test
    @DisplayName("DHCP 的 Offer / ACK 回程走 DHCP 回程带，不逆着去程带跑")
    void dhcpReturnUsesItsOwnLane() {
        assertNeverAgainstArrows("DHCP_OFFER", "PC_FACTORY");
        assertNeverAgainstArrows("DHCP_ACK", "PC_FACTORY");
        List<Point> path = line.route("DHCP_OFFER", "PC_FACTORY");
        for (Point p : path) {
            assertTrue(p.y == 576 || p.y == 528 || p.y == 384 || p.y == 336,
                    "DHCP 回程路径上的点不该跑到别的地方去，实际 " + p);
        }
    }

    @Test
    @DisplayName("DNS 应答从权威服务器回到本地服务器，顺着回程带")
    void dnsResponseGoesBackOnTheReturnLane() {
        assertNeverAgainstArrows("DNS_AUTH", "DNS_LOCAL");
        assertNeverAgainstArrows("DNS_ROOT", "DNS_LOCAL");
    }

    @Test
    @DisplayName("去程带没有反向边：往回走只能走错开的回程带")
    void forwardBeltsHaveNoBackwardEdge() {
        Map<String, Set<String>> forwardEdges = new HashMap<>();
        for (ProductionLine.Belt belt : line.getBelts()) {
            if (!belt.isForward()) {
                continue;
            }
            forwardEdges.computeIfAbsent(belt.getFromTag(), k -> new HashSet<>()).add(belt.getToTag());
        }
        // 去程带 A → B 存在时，回程带 B → A 也必须存在（两条带子各走各的）
        for (ProductionLine.Belt belt : line.getBelts()) {
            if (!belt.isForward()) {
                continue;
            }
            Set<String> outs = forwardEdges.get(belt.getFromTag());
            assertTrue(outs != null && outs.contains(belt.getToTag()));
        }

        // 往回走的路径里，绝不能出现某条去程带被反向走过的线段
        Map<String, Set<String>> backward = new HashMap<>();
        for (ProductionLine.Belt belt : line.getBelts()) {
            List<Point> pts = belt.getPoints();
            for (int i = 1; i < pts.size(); i++) {
                backward.computeIfAbsent(key(pts.get(i), pts.get(i - 1)), k -> new HashSet<>())
                        .add(belt.getFromTag() + ">" + belt.getToTag());
            }
        }
        for (ProductionLine.Belt belt : line.getBelts()) {
            if (!belt.isForward()) {
                continue;
            }
            List<Point> back = line.route(belt.getToTag(), belt.getFromTag());
            if (back == null) {
                continue;
            }
            Point cur = line.getPositions().get(belt.getToTag());
            for (Point p : back) {
                Set<String> owners = backward.get(key(cur, p));
                assertTrue(owners == null || !owners.contains(belt.getFromTag() + ">" + belt.getToTag()),
                        "去程带 " + belt.getFromTag() + " → " + belt.getToTag()
                                + " 被逆着走了（" + cur + " → " + p + "）");
                cur = p;
            }
        }
    }

    @Test
    @DisplayName("产线上任意两个工位之间，单向通路都不会逆着箭头走")
    void everyStationPairFollowsTheArrows() {
        List<String> tags = line.getAllTags();
        Map<String, ProductionLine.Belt> segs = directedSegments();
        for (String from : tags) {
            for (String to : tags) {
                if (from.equals(to)) {
                    continue;
                }
                List<Point> path = line.route(from, to);
                if (path == null) {
                    continue;
                }
                Point cur = line.getPositions().get(from);
                for (Point p : path) {
                    assertTrue(segs.containsKey(key(cur, p)),
                            from + " → " + to + " 这一步 " + cur + " → " + p + " 逆着传送带箭头");
                    cur = p;
                }
            }
        }
    }

    @Test
    @DisplayName("响应报文骑在回程带上行进，不会直线穿场")
    void returnTrafficStaysOnItsBelt() {
        Point offer = mapManager.findBuildingCoords("DHCP_OFFER");
        DataPacket packet = new DataPacket(offer.x, offer.y, "DHCP_OFFER", 0, new FactoryManager());
        packet.setCartType("DHCP_OFFER");
        packet.setStage(2);
        packet.setX(offer.x);
        packet.setY(offer.y);
        packet.setCurrentTag("DHCP_OFFER");
        packet.setCoordsLookup(mapManager::findBuildingCoords);
        packet.setBeltRouter((a, b) -> line.route(a, b));

        List<Point> belt = line.route("DHCP_OFFER", "PC_FACTORY");
        assertNotNull(belt);

        double maxDeviation = 0;
        int steps = 0;
        for (int i = 0; i < 600 && !packet.isArrived(); i++) {
            packet.update();
            steps++;
            maxDeviation = Math.max(maxDeviation,
                    distanceToPath(packet.getX(), packet.getY(), offer, belt));
        }
        assertTrue(steps > 5, "报文应该分多帧沿回程带行进");
        assertTrue(packet.isArrived(), "报文应该能顺着回程带回到 PC");
        assertTrue(maxDeviation <= 40,
                "报文偏离回程带 " + String.format("%.1f", maxDeviation) + "px，不应该直线穿场");
    }

    /** 计算点到折线路径的最短距离 */
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
