package com.never_give_up.automation.Service;

import com.never_give_up.automation.Entity.HaEntityState;
import com.never_give_up.automation.WebSocket.HomeAssistantWebSocketClient;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HomeAssistantService {

    private final HomeAssistantWebSocketClient webSocketClient;
    private boolean isConnected = false;
    // 用线程安全的 Map 存储实时状态（key：设备ID，value：设备状态）
    private final Map<String, HaEntityState> entityStateMap = new ConcurrentHashMap<>();

    public HomeAssistantService(HomeAssistantWebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }


    // 初始化方法
    @PostConstruct
    public void init() {
        // 关联服务到客户端（让客户端能同步状态到服务类）
        webSocketClient.setService(this);
        try {
            if (!webSocketClient.isOpen()) {
                webSocketClient.connect();
            }
            // 等待连接初始化（最多5秒）
            int retry = 0;
            while (!isConnected && retry < 10) {
                Thread.sleep(500);
                retry++;
            }
            if (!isConnected) {
                System.err.println("Home Assistant 连接初始化超时");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("初始化中断：" + e.getMessage());
        }
    }

    // -------------------------- 新增：状态同步方法（供客户端调用） --------------------------

    /**
     * 同步全量设备状态（连接成功后首次拉取时调用）
     */
    public void setAllEntityStates(List<HaEntityState> allStates) {
        entityStateMap.clear(); // 清空旧数据
        allStates.forEach(state -> entityStateMap.put(state.getEntity_id(), state));
        System.out.println("全量状态已同步到服务类，共 " + entityStateMap.size() + " 个设备");
    }


    /**
     * 同步单个设备的实时变更状态（客户端收到事件推送时调用）
     */
    public void updateEntityState(HaEntityState newState) {
        if (newState != null && newState.getEntity_id() != null) {
            entityStateMap.put(newState.getEntity_id(), newState);
        }
    }

    // -------------------------- 新增：外部查询接口（供Controller调用） --------------------------

    /**
     * 外部获取单个设备的实时状态（如Controller通过HTTP接口查询）
     */
    public HaEntityState getEntityState(String entityId) {
        if (!isConnected) {
            throw new RuntimeException("Home Assistant 未连接");
        }
        return entityStateMap.get(entityId);
    }

    /**
     * 外部获取所有设备的实时状态
     */
    public Map<String, HaEntityState> getAllEntityStates() {
        if (!isConnected) {
            throw new RuntimeException("Home Assistant 未连接");
        }
        return entityStateMap;
    }

    // 供外部调用的获取状态方法
    public void getStates() {
        if (isConnected) {
            webSocketClient.getStates();
        } else {
            System.err.println("未连接到Home Assistant，无法获取状态");
            reconnect();
        }
    }

    // 供外部调用的服务调用方法
    public void callService(String domain, String service, String entityId) {
        if (isConnected) {
            webSocketClient.callService(domain, service, entityId);
        } else {
            System.err.println("未连接到Home Assistant，无法调用服务");
            reconnect();
        }
    }

    // 重连方法
    public void reconnect() {
        try {
            if (webSocketClient.isOpen()) {
                webSocketClient.close();
            }
            webSocketClient.connect();

            // 等待重连
            int retryCount = 0;
            while (!isConnected && retryCount < 10) {
                Thread.sleep(500);
                retryCount++;
            }
        } catch (Exception e) {
            System.err.println("重连Home Assistant失败: " + e.getMessage());
        }
    }

    // 供WebSocket客户端回调更新连接状态
    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }

    // 提供连接状态查询
    public boolean isConnected() {
        return isConnected;
    }

    // 在 HomeAssistantService 中添加关闭设备的方法
    public void turnOffDevice(String entityId) {
        HaEntityState state = getEntityState(entityId);
        if (state == null || "unavailable".equals(state.getState())) {
            System.err.println("设备离线或不存在：" + entityId);
            return;
        }
        if ("off".equals(state.getState())) {
            System.out.println("设备已关闭，无需重复操作：" + entityId);
            return;
        }
        // 仅在设备在线且状态为on时发送指令
        webSocketClient.callService("switch", "turn_off", entityId);
    }
}
