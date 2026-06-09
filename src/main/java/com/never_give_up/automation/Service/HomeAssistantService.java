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
    private volatile boolean isConnected = false; // volatile保证线程可见性
    private final Map<String, HaEntityState> entityStateMap = new ConcurrentHashMap<>();
    private volatile boolean isReconnecting = false;

    public HomeAssistantService(HomeAssistantWebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    @PostConstruct
    public void init() {
        webSocketClient.setService(this);
        try {
            // 首次连接：先关闭可能存在的旧连接
            if (webSocketClient.isOpen()) {
                webSocketClient.close(1000, "初始化前清理旧连接");
            }
            // 发起首次连接
            webSocketClient.connect();
            // 等待连接+认证完成（最多15秒，包含连接超时+认证耗时）
            int waitCount = 0;
            while (!isConnected && waitCount < 30) { // 30*500ms=15秒
                Thread.sleep(500);
                waitCount++;
            }
            if (!isConnected) {
                System.err.println("Home Assistant 初始化超时（15秒），请检查连接配置");
                // 超时后主动触发一次重连
                reconnect();
            } else {
                System.out.println("Home Assistant 初始化成功，已连接");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("初始化中断：" + e.getMessage());
        }
    }

    // -------------------------- 关键优化1：重连逻辑状态同步+超时保护 --------------------------
    public void reconnect() {
        // 避免并发重连
        if (isReconnecting) {
            System.out.println("已在重连中，跳过本次请求");
            return;
        }
        synchronized (this) {
            if (isReconnecting) {
                return;
            }
            isReconnecting = true;
        }

        System.out.println("开始重连 Home Assistant...");
        try {
            // 步骤1：关闭旧连接（确保状态干净）
            if (webSocketClient.isOpen()) {
                System.out.println("关闭现有连接...");
                webSocketClient.close(1000, "主动重连");
                // 等待关闭完成（最多3秒）
                int closeWait = 0;
                while (webSocketClient.isOpen() && closeWait < 6) {
                    Thread.sleep(500);
                    closeWait++;
                }
                if (webSocketClient.isOpen()) {
                    System.err.println("旧连接关闭超时，强制关闭");
                    webSocketClient.closeConnection(4003, "关闭超时");
                }
            }

            // 步骤2：指数退避重连（5次重试，避免频繁请求）
            int maxRetries = 5;
            long baseDelay = 1000; // 1s → 2s → 4s → 8s → 16s
            for (int i = 0; i < maxRetries; i++) {
                try {
                    System.out.println("第" + (i + 1) + "次重连尝试...");
                    // 发起新连接（客户端已设置10秒连接超时）
                    webSocketClient.connect();

                    // 等待连接+认证完成（最多15秒）
                    int waitCount = 0;
                    while (!isConnected && waitCount < 30) {
                        Thread.sleep(500);
                        waitCount++;
                    }

                    // 重连成功：跳出循环
                    if (isConnected) {
                        System.out.println("第" + (i + 1) + "次重连成功！");
                        // 重连后主动拉取全量状态（双重保障）
                        getStates();
                        return;
                    } else {
                        System.err.println("第" + (i + 1) + "次重连超时（15秒），未完成认证");
                    }
                } catch (Exception e) {
                    System.err.println("第" + (i + 1) + "次重连异常：" + e.getMessage());
                }

                // 退避延迟（最后一次重试后不延迟）
                if (i < maxRetries - 1) {
                    long delay = baseDelay * (1 << i);
                    System.out.println("重连失败，等待" + delay + "ms后重试...");
                    Thread.sleep(delay);
                }
            }

            // 步骤3：重连失败处理
            System.err.println("达到最大重连次数（" + maxRetries + "次），请检查：1.网络 2.Token 3.HA服务状态");
        } catch (Exception e) {
            System.err.println("重连过程异常：" + e.getMessage());
        } finally {
            isReconnecting = false;
        }
    }

    // -------------------------- 其他方法保持不变（状态同步、设备控制等） --------------------------
    public void setAllEntityStates(List<HaEntityState> allStates) {
        allStates.forEach(state -> entityStateMap.put(state.getEntity_id(), state));
        System.out.println("全量状态同步完成，共 " + entityStateMap.size() + " 个设备");
    }

    public void updateEntityState(HaEntityState newState) {
        if (newState != null && newState.getEntity_id() != null) {
            entityStateMap.put(newState.getEntity_id(), newState);
            // 注释掉全量打印（避免日志冗余，按需开启）
            // System.out.println("设备状态更新：" + newState.getEntity_id() + " -> " + newState.getState());
        }
    }

    public HaEntityState getEntityState(String entityId) {
        if (!isConnected) {
            throw new RuntimeException("Home Assistant 未连接，请检查网络或Token");
        }
        return entityStateMap.get(entityId);
    }

    public Map<String, HaEntityState> getAllEntityStates() {
        if (!isConnected) {
            throw new RuntimeException("Home Assistant 未连接，请检查网络或Token");
        }
        return entityStateMap;
    }

    public void getStates() {
        if (isConnected && webSocketClient.isOpen()) {
            webSocketClient.getStates();
        } else {
            System.err.println("未连接到Home Assistant，无法获取状态");
            reconnect();
        }
    }

    public void callService(String domain, String service, String entityId) {
        if (isConnected && webSocketClient.isOpen()) {
            webSocketClient.callService(domain, service, entityId);
        } else {
            System.err.println("未连接到Home Assistant，无法调用服务");
            reconnect();
        }
    }

    public void setConnected(boolean connected) {
        this.isConnected = connected;
    }

    public boolean isConnected() {
        return isConnected;
    }

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
        webSocketClient.callService("switch", "turn_off", entityId);
    }

    public void controlAcPower(String acEntityId, boolean turnOn) {
        HaEntityState state = getEntityState(acEntityId);
        if (!checkAcAvailability(state, acEntityId)) {
            return;
        }

        String targetMode = turnOn ? "cool" : "off";
        String currentMode = state.getState();
        if (currentMode.equals(targetMode)) {
            System.out.println("空调已" + (turnOn ? "开启" : "关闭") + "，无需操作：" + acEntityId);
            return;
        }

        webSocketClient.callServiceWithData(
                "climate",
                "set_hvac_mode",
                acEntityId,
                Map.of("hvac_mode", targetMode)
        );
        System.out.println("已发送空调" + (turnOn ? "开启" : "关闭") + "指令：" + acEntityId);
    }

    private double getTemperatureAttribute(Map<String, Object> attributes, String key, double defaultValue) {
        Object value = attributes.getOrDefault(key, defaultValue);
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        } else {
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
                System.err.println("温度属性[" + key + "]格式异常：" + value + "，使用默认值：" + defaultValue);
                return defaultValue;
            }
        }
    }

    public void setAcTemperature(String acEntityId, double temperature) {
        HaEntityState state = getEntityState(acEntityId);
        if (!checkAcAvailability(state, acEntityId)) {
            return;
        }

        Map<String, Object> attributes = state.getAttributes();
        double minTemp = getTemperatureAttribute(attributes, "min_temp", 16.0);
        double maxTemp = getTemperatureAttribute(attributes, "max_temp", 30.0);

        if (temperature < minTemp || temperature > maxTemp) {
            System.err.println("温度超出范围[" + minTemp + "-" + maxTemp + "°C]：" + temperature);
            return;
        }

        webSocketClient.callServiceWithData(
                "climate",
                "set_temperature",
                acEntityId,
                Map.of("temperature", temperature)
        );
        System.out.println("已发送空调调温指令：" + acEntityId + " -> " + temperature + "°C");
    }

    public void setAcMode(String acEntityId, String mode) {
        HaEntityState state = getEntityState(acEntityId);
        if (!checkAcAvailability(state, acEntityId)) {
            return;
        }

        Map<String, Object> attributes = state.getAttributes();
        List<String> supportedModes = (List<String>) attributes.getOrDefault("hvac_modes", List.of());
        if (!supportedModes.contains(mode)) {
            System.err.println("设备不支持模式[" + mode + "]，支持的模式：" + supportedModes);
            return;
        }

        webSocketClient.callServiceWithData(
                "climate",
                "set_hvac_mode",
                acEntityId,
                Map.of("hvac_mode", mode)
        );
        System.out.println("已发送空调模式切换指令：" + acEntityId + " -> " + mode);
    }

    private boolean checkAcAvailability(HaEntityState state, String entityId) {
        if (state == null) {
            System.err.println("空调设备不存在：" + entityId);
            return false;
        }
        if ("unavailable".equals(state.getState())) {
            System.err.println("空调设备离线：" + entityId);
            return false;
        }
        if (!entityId.startsWith("climate.")) {
            System.err.println("设备ID不是空调类型：" + entityId);
            return false;
        }
        return true;
    }

    public void callServiceWithData(String domain, String service, String entityId, Map<String, Object> data) {
        if (isConnected && webSocketClient.isOpen()) {
            webSocketClient.callServiceWithData(domain, service, entityId, data);
        } else {
            System.err.println("未连接到Home Assistant，无法调用服务：" + domain + "." + service);
            reconnect();
        }
    }
}