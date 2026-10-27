package com.never_give_up.automation.demo.engine;

import com.never_give_up.automation.demo.adapter.FactoryManager;
import com.never_give_up.automation.demo.ui.GameCanvas;
import com.never_give_up.automation.demo.winModel.DataPacket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 「一条传送带只运一种物料 + 设备声明需求 + 机械臂筛选器」的回归测试。
 */
class ArmFilterAndDemandTest {

    private GameEngine engine;
    private MapManager mapManager;
    private List<DataPacket> onCanvas;
    private GameCanvas canvas;

    @BeforeEach
    void setUp() {
        engine = new GameEngine(new FactoryManager());
        mapManager = new MapManager();
        onCanvas = new ArrayList<>();
        canvas = new GameCanvas(engine, mapManager, onCanvas, new ArrayList<>());
    }

    /** 造一个停在某个工位上的工件 */
    private DataPacket workpieceAt(String tag) {
        Point pos = mapManager.findBuildingCoords(tag);
        DataPacket packet = engine.createDataPacket(pos.x, pos.y, "DATA", 0);
        packet.setStage(5);
        packet.setHasTcp(true);
        packet.setHasIp(true);
        packet.setHasEther(true);
        packet.setCurrentTag(tag);
        packet.setX(pos.x);
        packet.setY(pos.y);
        onCanvas.add(packet);
        return packet;
    }

    @Test
    @DisplayName("每个工位都声明了自己的设备需求")
    void everyStationDeclaresADemand() {
        List<String> tags = canvas.getStationTags();
        assertTrue(tags.size() >= 70, "产线上应该有七十多个工位，实际 " + tags.size());
        for (String tag : tags) {
            String demand = canvas.getStationDemand(tag);
            assertNotNull(demand, tag + " 没有声明需求");
            assertTrue(Material.ALL.contains(demand),
                    tag + " 的需求 " + demand + " 不是合法物料");
        }
    }

    @Test
    @DisplayName("协议头工位要的就是协议头原料")
    void headerStationsDemandTheirHeader() {
        assertEquals(Material.TCP, canvas.getStationDemand("T_CORE"));
        assertEquals(Material.IP, canvas.getStationDemand("TX_IPH"));
        assertEquals(Material.ETH, canvas.getStationDemand("ETH_TYPE"));
        // 接收端的拆包工位也要对应的协议头
        assertEquals(Material.TCP, canvas.getStationDemand("RX_TCP"));
        assertEquals(Material.IP, canvas.getStationDemand("RX_IP"));
        assertEquals(Material.ETH, canvas.getStationDemand("RX_ETH"));
    }

    @Test
    @DisplayName("设备可以改需求：普通节点改成需要 IP 头，供给带就改运 IP 头")
    void stationDemandCanBeChangedToAHeader() throws Exception {
        assertEquals(Material.PAYLOAD, canvas.getStationDemand("TX_APP"));
        canvas.setStationDemand("TX_APP", Material.IP);
        assertEquals(Material.IP, canvas.getStationDemand("TX_APP"));

        // 供给带只运这一种物料，跑一会儿就该有 IP 头料块
        long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline && canvas.getSupplyBacklog("TX_APP") == 0) {
            canvas.updateEffects();
            Thread.sleep(20);
        }
        assertTrue(canvas.getSupplyBacklog("TX_APP") > 0, "供给带上应该有 IP 头料块");
    }

    @Test
    @DisplayName("黑名单挡掉需求物料，机械臂就不取，工件直接过站")
    void blacklistedDemandMakesArmIgnoreTheInput() {
        canvas.setStationFilter("TX_APP", null, Collections.singletonList(Material.PAYLOAD));
        assertFalse(canvas.acceptsMaterial("TX_APP", Material.PAYLOAD));

        DataPacket packet = workpieceAt("TX_APP");
        canvas.updateEffects();

        assertFalse(packet.isWaitingForArm(), "被黑名单挡掉的物料，机械臂不该抓工件");
    }

    @Test
    @DisplayName("白名单放行需求物料，机械臂照常抓工件进机加工")
    void whitelistThatAdmitsTheDemandLetsTheArmWork() {
        canvas.setStationFilter("TX_APP", Collections.singletonList(Material.PAYLOAD), null);
        assertTrue(canvas.acceptsMaterial("TX_APP", Material.PAYLOAD));

        DataPacket packet = workpieceAt("TX_APP");
        canvas.updateEffects();

        assertTrue(packet.isWaitingForArm(), "白名单放行的物料，机械臂应该把工件抓进机器");
    }

    @Test
    @DisplayName("白名单里没有需求物料，机械臂照样不取")
    void whitelistThatExcludesTheDemandBlocksTheArm() {
        canvas.setStationFilter("TX_APP", Collections.singletonList(Material.TCP), null);
        assertFalse(canvas.acceptsMaterial("TX_APP", Material.PAYLOAD));

        DataPacket packet = workpieceAt("TX_APP");
        canvas.updateEffects();

        assertFalse(packet.isWaitingForArm(), "白名单不含的物料，机械臂不该抓工件");
    }

    @Test
    @DisplayName("筛选器只影响这台设备的机械臂，其它工位不受影响")
    void filterOnlyAffectsOneStation() {
        canvas.setStationFilter("TX_APP", null, Collections.singletonList(Material.PAYLOAD));
        assertFalse(canvas.acceptsMaterial("TX_APP", Material.PAYLOAD));
        assertTrue(canvas.acceptsMaterial("RX_APP", Material.PAYLOAD));
        assertTrue(canvas.acceptsMaterial("T_CORE", Material.TCP));
    }

    @Test
    @DisplayName("筛选器可以恢复默认")
    void filterCanBeReset() {
        canvas.setStationFilter("TX_APP", Collections.singletonList(Material.TCP),
                Collections.singletonList(Material.PAYLOAD));
        assertFalse(canvas.acceptsMaterial("TX_APP", Material.PAYLOAD));

        canvas.resetStationFilter("TX_APP");
        assertTrue(canvas.acceptsMaterial("TX_APP", Material.PAYLOAD));
        assertTrue(canvas.getStationWhitelist("TX_APP").isEmpty());
        assertTrue(canvas.getStationBlacklist("TX_APP").isEmpty());
    }

    @Test
    @DisplayName("封装工位的协议头被筛掉时，料块堆在供给带上，机械臂不去取")
    void filteredSupplyStaysOnTheBelt() throws Exception {
        canvas.setStationFilter("T_CORE", null, Collections.singletonList(Material.TCP));

        DataPacket packet = workpieceAt("T_CORE");
        canvas.updateEffects();
        assertFalse(packet.isWaitingForArm(), "TCP 头被筛掉，机械臂不该开工");

        long deadline = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < deadline && canvas.getSupplyBacklog("T_CORE") == 0) {
            canvas.updateEffects();
            Thread.sleep(20);
        }
        assertTrue(canvas.getSupplyBacklog("T_CORE") > 0, "被筛掉的 TCP 头应该堆在供给带上");
        assertFalse(canvas.getPacketLayers(packet).contains("TCP"), "没取到料就不该把 TCP 头钉到工件上");
    }

    @Test
    @DisplayName("白名单放行协议头时，机械臂照常把 TCP 头钉到工件上")
    void whitelistLetsHeaderBeStamped() throws Exception {
        canvas.setStationFilter("T_CORE", Collections.singletonList(Material.TCP), null);

        DataPacket packet = workpieceAt("T_CORE");
        canvas.updateEffects();
        assertTrue(packet.isWaitingForArm(), "白名单放行 TCP 头，机械臂应该开工");

        long deadline = System.currentTimeMillis() + 5000;
        while (System.currentTimeMillis() < deadline && packet.isWaitingForArm()) {
            canvas.updateEffects();
            Thread.sleep(20);
        }
        assertFalse(packet.isWaitingForArm(), "机械臂装完头之后工件应该被放行");
        assertTrue(canvas.getPacketLayers(packet).contains("TCP"), "TCP 头应该被钉到工件上");
    }

    @Test
    @DisplayName("白名单同时命中两种物料时，以白名单为准")
    void whitelistWinsOverBlacklist() {
        canvas.setStationFilter("TX_APP",
                Arrays.asList(Material.PAYLOAD, Material.TCP),
                Collections.singletonList(Material.TCP));
        assertFalse(canvas.acceptsMaterial("TX_APP", Material.TCP), "黑名单应该把 TCP 挡掉");
        assertTrue(canvas.acceptsMaterial("TX_APP", Material.PAYLOAD), "白名单里的工件应该放行");
    }

    @Test
    @DisplayName("普通节点要协议头原料时：机械臂先去供给带取料，再把工件抓进机器加工")
    void mixModeFetchesHeaderThenProcessesWorkpiece() throws Exception {
        canvas.setStationDemand("TX_APP", Material.IP);

        DataPacket packet = workpieceAt("TX_APP");
        canvas.updateEffects();
        assertTrue(packet.isWaitingForArm(), "机械臂应该开工");

        long deadline = System.currentTimeMillis() + 8000;
        while (System.currentTimeMillis() < deadline && packet.isWaitingForArm()) {
            canvas.updateEffects();
            Thread.sleep(20);
        }

        assertFalse(packet.isWaitingForArm(), "机械臂干完活之后工件应该被放行");
        assertTrue(canvas.getPacketLayers(packet).contains("IP"),
                "机械臂应该把从供给带上取来的 IP 头装到工件上");
    }

    @Test
    @DisplayName("普通节点的协议头原料被筛掉时，机械臂连工件也不加工")
    void mixModeRespectsFilter() {
        canvas.setStationDemand("TX_APP", Material.IP);
        canvas.setStationFilter("TX_APP", null, Collections.singletonList(Material.IP));

        DataPacket packet = workpieceAt("TX_APP");
        canvas.updateEffects();

        assertFalse(packet.isWaitingForArm(), "原料被筛掉，机械臂不该开工");
    }
}
