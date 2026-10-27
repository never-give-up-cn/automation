package com.never_give_up.automation.demo.engine;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 产线上的物料类型。
 * <p>
 * 产线的规矩：<b>一条传送带只运输一种物料</b>。
 * 主干传送带 C2 运的是工件本体（PAYLOAD），每条供给传送带 C1 运一种原料块
 * （TCP / IP / ETH 协议头）。设备（组装机）声明自己需要哪种原料，
 * 机械臂按需求去对应那条带上取料，送进机器。
 * </p>
 */
public final class Material {

    /** 工件本体（数据包净荷）：主干传送带 C2 上的货 */
    public static final String PAYLOAD = "PAYLOAD";
    /** TCP 段首部块 */
    public static final String TCP = "TCP";
    /** IP 首部块 */
    public static final String IP = "IP";
    /** 以太网首部块 */
    public static final String ETH = "ETH";

    /** 全部可选物料 */
    public static final List<String> ALL = Collections.unmodifiableList(
            Arrays.asList(PAYLOAD, TCP, IP, ETH));

    /** 展示用名称 */
    public static String displayName(String material) {
        if (material == null) {
            return "-";
        }
        switch (material) {
            case PAYLOAD:
                return "工件";
            case TCP:
                return "TCP 头";
            case IP:
                return "IP 头";
            case ETH:
                return "ETH 头";
            default:
                return material;
        }
    }

    /** 是否是协议头块（可被装到工件上 / 从工件上拆下来） */
    public static boolean isHeader(String material) {
        return TCP.equals(material) || IP.equals(material) || ETH.equals(material);
    }

    private Material() {
    }
}
