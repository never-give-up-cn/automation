package com.never_give_up.automation.demo.engine;

import com.never_give_up.automation.demo.winModel.DataPacket;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 传送带产线模型。
 * <p>
 * 把协议栈的每个节点看成产线上的「组装机」，节点之间的传送带构成一棵
 * 树：主干道从源 PC 出发，依次经过应用层 / 传输层 / 网络层 / 链路层 /
 * 网关 / 路由器 / 接收端解封装，最后抵达接收服务器；DHCP 走一条支线；
 * 46 阶段之后的扩展工厂挂在接收端应用层旁边。
 * </p>
 * <p>
 * 数据包不再「直线瞬移」到下一个节点，而是沿传送带折线逐段行进，
 * 途经的每一个组装机都会被点亮、加工、弹出下一份报文。
 * </p>
 */
public class ProductionLine {

    /** 一段传送带：由若干折点组成，方向为 from → to */
    public static class Belt {
        private final List<Point> points;
        private final String fromTag;
        private final String toTag;
        private final boolean forward;

        Belt(List<Point> points, String fromTag, String toTag, boolean forward) {
            this.points = points;
            this.fromTag = fromTag;
            this.toTag = toTag;
            this.forward = forward;
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

        /** 是否是正向（去程）传送带，回程走同一条带子但方向相反 */
        public boolean isForward() {
            return forward;
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
            for (int i = 1; i < chain.size(); i++) {
                String fromTag = chain.get(i - 1);
                String toTag = chain.get(i);
                Point a = positions.get(fromTag);
                Point b = positions.get(toTag);
                if (a == null || b == null) {
                    continue;
                }
                belts.add(new Belt(segment(a, b), fromTag, toTag, true));
            }
        }
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

    // ===================== 路径查询 =====================

    /**
     * 计算从 fromTag 走到 toTag 的传送带路径（不含起点，包含终点）。
     *
     * @return 折点列表；两点不在同一棵树上时返回 null
     */
    public List<Point> route(String fromTag, String toTag) {
        if (fromTag == null || toTag == null) {
            return null;
        }
        if (fromTag.equals(toTag)) {
            Point p = positions.get(toTag);
            return p == null ? null : new ArrayList<>(Collections.singletonList(p));
        }

        List<String> up = ancestors(fromTag);
        List<String> down = ancestors(toTag);
        if (up.isEmpty() || down.isEmpty()) {
            return null;
        }

        // 找最近公共祖先：从 to 一路往上走，第一个在 from 祖先链上的节点即是
        Map<String, Integer> upIndex = new LinkedHashMap<>();
        for (int i = 0; i < up.size(); i++) {
            upIndex.put(up.get(i), i);
        }
        int j = -1;
        for (int k = 0; k < down.size(); k++) {
            if (upIndex.containsKey(down.get(k))) {
                j = k;
                break;
            }
        }
        if (j < 0) {
            return null;
        }
        int i = upIndex.get(down.get(j));

        // from → 公共祖先 → to
        List<String> tagPath = new ArrayList<>(up.subList(0, i + 1));
        List<String> tail = new ArrayList<>(down.subList(0, j));
        Collections.reverse(tail);
        tagPath.addAll(tail);

        List<Point> pts = new ArrayList<>();
        for (int k = 1; k < tagPath.size(); k++) {
            Point a = positions.get(tagPath.get(k - 1));
            Point b = positions.get(tagPath.get(k));
            if (a == null || b == null) {
                continue;
            }
            List<Point> seg = segment(a, b);
            for (int m = 1; m < seg.size(); m++) {
                pts.add(seg.get(m));
            }
        }
        return pts;
    }

    private List<String> ancestors(String tag) {
        List<String> list = new ArrayList<>();
        String cur = tag;
        int guard = 0;
        while (cur != null && guard++ < 512) {
            list.add(cur);
            cur = parent.get(cur);
        }
        return list;
    }

    // ===================== 对外接口 =====================

    public List<Belt> getBelts() {
        return belts;
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
