package com.never_give_up.automation.demo.engine;

import com.never_give_up.automation.demo.winModel.DataPacket;

import java.awt.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 传送带产线模型。
 * <p>
 * 把协议栈的每个节点看成产线上的「组装机」，节点之间的传送带构成一条
 * 主干道：主干道从源 PC 出发，依次经过应用层 / 传输层 / 网络层 / 链路层 /
 * 网关 / 路由器 / 接收端解封装，最后抵达接收服务器；DHCP 走一条支线；
 * 46 阶段之后的扩展工厂挂在接收端应用层旁边。
 * </p>
 * <p>
 * <b>每条传送带都是单向的</b>：去程带把工件从上游送到下游，回程带在去程带
 * 旁边错开一条车道，把响应报文（DHCP Offer/ACK、DNS 应答、ICMP/HTTP 回程）
 * 沿着回程带送回上游，工件永远不会逆着箭头走。
 * </p>
 * <p>
 * 数据包不再「直线瞬移」到下一个节点，而是沿传送带折线逐段行进，
 * 途经的每一个组装机都会被点亮、加工、弹出下一份报文。
 * </p>
 */
public class ProductionLine {

    /** 回程带相对去程带的错开距离（行方向往下让出一条车道） */
    private static final int LANE_GAP_Y = 48;
    /** 回程带相对去程带的错开距离（列方向往右让出一条车道） */
    private static final int LANE_GAP_X = 64;

    /** 一段传送带：由若干折点组成，方向为 from → to */
    public static class Belt {
        private final List<Point> points;
        private final String fromTag;
        private final String toTag;
        private final boolean forward;
        /** 这条带子上运的物料：一条带子只运一种物料 */
        private final String material;

        Belt(List<Point> points, String fromTag, String toTag, boolean forward, String material) {
            this.points = points;
            this.fromTag = fromTag;
            this.toTag = toTag;
            this.forward = forward;
            this.material = material;
        }

        public List<Point> getPoints() {
            return points;
        }

        public String getFromTag() {
            return fromTag;
        }

        public String getToTag() {
            return toTag;
        }

        /** 这条传送带运输的物料 */
        public String getMaterial() {
            return material;
        }

        /** 是否是正向（去程）传送带 */
        public boolean isForward() {
            return forward;
        }

        /** 是否是回程传送带：回程带错开一条车道，单向地把响应报文送回上游 */
        public boolean isReturn() {
            return !forward;
        }

        /** 传送带总长（像素） */
        public double length() {
            double len = 0;
            for (int i = 1; i < points.size(); i++) {
                len += points.get(i).distance(points.get(i - 1));
            }
            return len;
        }
    }

    /** 产线根节点：源 PC */
    private static final String ROOT = "PC_FACTORY";

    /** 主干道经过的阶段区间 */
    private static final int MAIN_LAST_STAGE = 45;
    /** 扩展工厂的阶段区间 */
    private static final int EXT_LAST_STAGE = 75;

    private final Map<String, Point> positions;
    /** 传送带树：子工位 → 父工位 */
    private final Map<String, String> parent = new LinkedHashMap<>();
    /** 传送带链（每条链是一串首尾相连的工位） */
    private final List<List<String>> chains = new ArrayList<>();
    /** 所有传送带段，供画布绘制 */
    private final List<Belt> belts = new ArrayList<>();
    /** 有向图：起点 → 终点 → 传送带。传送带是单向的，路由只能顺着箭头走 */
    private final Map<String, Map<String, Belt>> lanes = new LinkedHashMap<>();

    public ProductionLine(Map<String, Point> positions) {
        this.positions = positions;
        buildChains();
        buildTree();
        buildBelts();
    }

    // ===================== 拓扑构建 =====================

    private void buildChains() {
        // 主干道：源 PC → 阶段 1..45 → 接收服务器
        List<String> main = new ArrayList<>();
        main.add(ROOT);
        for (int s = 1; s <= MAIN_LAST_STAGE; s++) {
            main.add(DataPacket.getTagForStage(s));
        }
        main.add("RX_ST");
        chains.add(main);

        // 扩展工厂：挂在接收端应用层旁边
        List<String> ext = new ArrayList<>();
        ext.add(DataPacket.getTagForStage(MAIN_LAST_STAGE));
        for (int s = MAIN_LAST_STAGE + 1; s <= EXT_LAST_STAGE; s++) {
            ext.add(DataPacket.getTagForStage(s));
        }
        chains.add(ext);

        // DHCP 支线：源 PC → Request → Discover → Server → Offer → ACK
        List<String> dhcp = new ArrayList<>();
        dhcp.add(ROOT);
        dhcp.add("DHCP_REQ");
        dhcp.add("DHCP_DISC");
        dhcp.add("DHCP_SERVER");
        dhcp.add("DHCP_OFFER");
        dhcp.add("DHCP_ACK");
        chains.add(dhcp);
    }

    private void buildTree() {
        for (List<String> chain : chains) {
            for (int i = 1; i < chain.size(); i++) {
                String child = chain.get(i);
                String par = chain.get(i - 1);
                if (!parent.containsKey(child)) {
                    parent.put(child, par);
                }
            }
        }
    }

    private void buildBelts() {
        for (List<String> chain : chains) {
            // 去程带：顺着链条一路向前
            for (int i = 1; i < chain.size(); i++) {
                addBelt(chain.get(i - 1), chain.get(i), true);
            }
            // 回程带：同一条链倒着走，带子错开一条车道，单向地把响应报文送回上游
            for (int i = chain.size() - 1; i >= 1; i--) {
                addBelt(chain.get(i), chain.get(i - 1), false);
            }
        }
    }

    /** 登记一段传送带，并把它加进有向图 */
    private void addBelt(String fromTag, String toTag, boolean forward) {
        Point a = positions.get(fromTag);
        Point b = positions.get(toTag);
        if (a == null || b == null) {
            return;
        }
        // 回程带用的是去程带的同一条走线，只是整条折线反过来、并错开一条车道
        List<Point> pts = forward ? segment(a, b) : returnLane(segment(b, a), toTag);
        Belt belt = new Belt(pts, fromTag, toTag, forward, Material.PAYLOAD);
        belts.add(belt);
        lanes.computeIfAbsent(fromTag, k -> new LinkedHashMap<>()).put(toTag, belt);
    }

    /**
     * 正交折线：先竖直后水平，模拟厂区里「走一段再拐弯」的传送带。
     */
    private static List<Point> segment(Point a, Point b) {
        List<Point> pts = new ArrayList<>();
        pts.add(a);
        if (a.x != b.x && a.y != b.y) {
            pts.add(new Point(a.x, b.y));
        }
        pts.add(b);
        return pts;
    }

    /**
     * 回程带的几何：在去程带旁边错开一条车道。
     * <p>把去程折线 [P0..Pn] 的每个折点往车道方向偏移，得到回程折线
     * [Pn, off(Pn), …, off(P0), P0]：工件沿车道往回走，每一站都从车道
     * 拐进工位停一下，再拐回车道继续往上游走。</p>
     */
    private static List<Point> returnLane(List<Point> fwd, String endTag) {
        int n = fwd.size();
        Point[] off = new Point[n];
        for (int i = 0; i < n; i++) {
            Point p = fwd.get(i);
            boolean prevH = i > 0 && fwd.get(i).y == fwd.get(i - 1).y;
            boolean prevV = i > 0 && fwd.get(i).x == fwd.get(i - 1).x;
            boolean nextH = i < n - 1 && fwd.get(i).y == fwd.get(i + 1).y;
            boolean nextV = i < n - 1 && fwd.get(i).x == fwd.get(i + 1).x;
            if ((prevH && nextV) || (prevV && nextH)) {
                // 转角：横车道和竖车道在这里斜接
                off[i] = new Point(p.x + LANE_GAP_X, p.y + LANE_GAP_Y);
            } else if (nextH || (i == n - 1 && prevH)) {
                off[i] = new Point(p.x, p.y + LANE_GAP_Y);
            } else if (nextV || (i == n - 1 && prevV)) {
                off[i] = new Point(p.x + LANE_GAP_X, p.y);
            } else {
                off[i] = new Point(p.x, p.y + LANE_GAP_Y);
            }
        }
        List<Point> pts = new ArrayList<>();
        pts.add(fwd.get(n - 1));
        for (int i = n - 1; i >= 0; i--) {
            pts.add(off[i]);
        }
        pts.add(fwd.get(0));
        // 回到源 PC 时，正对着主干道的那一段会压住去程带，改成斜着进站
        if (ROOT.equals(endTag) && pts.size() >= 2) {
            Point last = pts.get(pts.size() - 1);
            Point before = pts.get(pts.size() - 2);
            if (before.y == last.y) {
                pts.set(pts.size() - 2, new Point(last.x + LANE_GAP_X, last.y + LANE_GAP_Y));
            }
        }
        return pts;
    }

    // ===================== 路径查询 =====================

    /**
     * 计算从 fromTag 走到 toTag 的传送带路径（不含起点，包含终点）。
     * <p>
     * 传送带是单向的，所以这里在有向图上做一次广度优先搜索：
     * 只能顺着箭头走，响应报文会自动落到回程带上，绝不会逆着去程带跑。
     * </p>
     *
     * @return 折点列表；两点之间没有单向通路时返回 null
     */
    public List<Point> route(String fromTag, String toTag) {
        if (fromTag == null || toTag == null) {
            return null;
        }
        if (fromTag.equals(toTag)) {
            Point p = positions.get(toTag);
            return p == null ? null : new ArrayList<>(Collections.singletonList(p));
        }

        Map<String, Belt> cameBy = new LinkedHashMap<>();
        Map<String, String> cameFrom = new LinkedHashMap<>();
        Set<String> seen = new LinkedHashSet<>();
        Deque<String> queue = new ArrayDeque<>();
        seen.add(fromTag);
        queue.add(fromTag);

        boolean found = false;
        while (!queue.isEmpty() && !found) {
            String cur = queue.poll();
            Map<String, Belt> outs = lanes.get(cur);
            if (outs == null) {
                continue;
            }
            for (Map.Entry<String, Belt> e : outs.entrySet()) {
                String next = e.getKey();
                if (!seen.add(next)) {
                    continue;
                }
                cameBy.put(next, e.getValue());
                cameFrom.put(next, cur);
                if (next.equals(toTag)) {
                    found = true;
                    break;
                }
                queue.add(next);
            }
        }
        if (!found) {
            return null;
        }

        // 回溯出经过的传送带
        List<Belt> path = new ArrayList<>();
        String cur = toTag;
        int guard = 0;
        while (!cur.equals(fromTag) && guard++ < 1024) {
            Belt belt = cameBy.get(cur);
            if (belt == null) {
                return null;
            }
            path.add(0, belt);
            cur = cameFrom.get(cur);
        }

        List<Point> pts = new ArrayList<>();
        for (Belt belt : path) {
            List<Point> bp = belt.getPoints();
            for (int i = 1; i < bp.size(); i++) {
                pts.add(bp.get(i));
            }
        }
        return pts;
    }

    // ===================== 对外接口 =====================

    public List<Belt> getBelts() {
        return belts;
    }

    /** 所有回程带（单向运输响应报文的那几条） */
    public List<Belt> getReturnBelts() {
        List<Belt> list = new ArrayList<>();
        for (Belt belt : belts) {
            if (belt.isReturn()) {
                list.add(belt);
            }
        }
        return list;
    }

    /** 这两个工位之间有没有单向通路 */
    public boolean hasRoute(String fromTag, String toTag) {
        return route(fromTag, toTag) != null;
    }

    public Map<String, Point> getPositions() {
        return positions;
    }

    /** 判断某个工位是否挂在传送带上（用于决定要不要画机械臂） */
    public boolean isOnLine(String tag) {
        return parent.containsKey(tag) || ROOT.equals(tag);
    }

    /** 产线上的全部工位标签（按链顺序去重） */
    public List<String> getAllTags() {
        List<String> tags = new ArrayList<>();
        for (List<String> chain : chains) {
            for (String tag : chain) {
                if (!tags.contains(tag)) {
                    tags.add(tag);
                }
            }
        }
        return tags;
    }

    /**
     * 工位上游传送带的方向（单位向量，指向该工位）。
     * <p>排队等待机械臂的工件就沿着这个方向往回退，让它们留在传送带上。</p>
     *
     * @return 形如 {dx, dy}；没有上游时返回 null
     */
    public double[] getUpstreamDir(String tag) {
        String up = parent.get(tag);
        if (up == null) {
            return null;
        }
        for (Belt belt : belts) {
            if (!belt.isForward() || !belt.getToTag().equals(tag) || !belt.getFromTag().equals(up)) {
                continue;
            }
            List<Point> pts = belt.getPoints();
            if (pts.size() < 2) {
                continue;
            }
            double[] dir = unit(pts.get(pts.size() - 2), pts.get(pts.size() - 1));
            if (dir != null) {
                return dir;
            }
        }
        return null;
    }

    private static double[] unit(Point a, Point b) {
        double dx = b.x - a.x;
        double dy = b.y - a.y;
        double len = Math.hypot(dx, dy);
        if (len < 0.5) {
            return null;
        }
        return new double[]{dx / len, dy / len};
    }
}
