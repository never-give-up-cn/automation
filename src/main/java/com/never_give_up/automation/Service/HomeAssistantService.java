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
//        entityStateMap.clear(); // 清空旧数据
        allStates.forEach(state -> entityStateMap.put(state.getEntity_id(), state));
        System.out.println("全量状态已同步到服务类，共 " + entityStateMap.size() + " 个设备");
    }


    /**
     * 同步单个设备的实时变更状态（客户端收到事件推送时调用）
     */
    public void updateEntityState(HaEntityState newState) {
        if (newState != null && newState.getEntity_id() != null) {
            entityStateMap.put(newState.getEntity_id(), newState);
            System.out.println("entityStateMap：" + entityStateMap);
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

    // -------------------------- 新增：空调控制方法 --------------------------

    /**
     * 控制空调开关（打开/关闭）
     * @param acEntityId 空调设备ID（如 "climate.hzyk_cn_2003157372_kt5s01"）
     * @param turnOn 是否打开（true-打开，false-关闭）
     */
    public void controlAcPower(String acEntityId, boolean turnOn) {
        HaEntityState state = getEntityState(acEntityId);
        if (!checkAcAvailability(state, acEntityId)) {
            return;
        }

        String targetMode = turnOn ? "cool" : "off"; // 打开默认制冷模式，可根据需求修改
        String currentMode = state.getState();

        if (currentMode.equals(targetMode)) {
            System.out.println("空调已" + (turnOn ? "开启" : "关闭") + "，无需操作：" + acEntityId);
            return;
        }

        // 调用Home Assistant的set_hvac_mode服务切换模式（off即为关闭）
        webSocketClient.callServiceWithData(
                "climate",
                "set_hvac_mode",
                acEntityId,
                Map.of("hvac_mode", targetMode)
        );
        System.out.println("已发送空调" + (turnOn ? "开启" : "关闭") + "指令：" + acEntityId);
    }
// 提取为独立方法，便于复用
    private double getTemperatureAttribute(Map<String, Object> attributes, String key, double defaultValue) {
        Object value = attributes.getOrDefault(key, defaultValue);
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        } else if (value instanceof Double) {
            return (Double) value;
        } else {
            // 处理字符串或其他类型
            try {
                return Double.parseDouble(value.toString());
            } catch (NumberFormatException e) {
                System.err.println("温度属性[" + key + "]格式异常：" + value + "，使用默认值：" + defaultValue);
                return defaultValue;
            }
        }
    }
    /**
     * 调节空调温度
     * @param acEntityId 空调设备ID
     * @param temperature 目标温度（需在设备支持的范围内，如16-30°C）
     */
    public void setAcTemperature(String acEntityId, double temperature) {
        HaEntityState state = getEntityState(acEntityId);
        if (!checkAcAvailability(state, acEntityId)) {
            return;
        }

        // 检查设备支持的温度范围
        Map<String, Object> attributes = state.getAttributes();
        double minTemp = getTemperatureAttribute(attributes, "min_temp", 16.0);
        double maxTemp = getTemperatureAttribute(attributes, "max_temp", 30.0);

        if (temperature < minTemp || temperature > maxTemp) {
            System.err.println("温度超出范围[" + minTemp + "-" + maxTemp + "°C]：" + temperature);
            return;
        }

        // 调用set_temperature服务
        webSocketClient.callServiceWithData(
                "climate",
                "set_temperature",
                acEntityId,
                Map.of("temperature", temperature)
        );
        System.out.println("已发送空调调温指令：" + acEntityId + " -> " + temperature + "°C");
    }

    /**
     * 切换空调运行模式（制冷/制热/自动等）
     * @param acEntityId 空调设备ID
     * @param mode 目标模式（需在设备支持的hvac_modes中，如"cool"、"heat"、"auto"）
     */
    public void setAcMode(String acEntityId, String mode) {
        HaEntityState state = getEntityState(acEntityId);
        if (!checkAcAvailability(state, acEntityId)) {
            return;
        }

        // 检查模式是否在设备支持的列表中
        Map<String, Object> attributes = state.getAttributes();
        List<String> supportedModes = (List<String>) attributes.getOrDefault("hvac_modes", List.of());
        if (!supportedModes.contains(mode)) {
            System.err.println("设备不支持模式[" + mode + "]，支持的模式：" + supportedModes);
            return;
        }

        // 调用set_hvac_mode服务
        webSocketClient.callServiceWithData(
                "climate",
                "set_hvac_mode",
                acEntityId,
                Map.of("hvac_mode", mode)
        );
        System.out.println("已发送空调模式切换指令：" + acEntityId + " -> " + mode);
    }

    /**
     * 检查空调是否可用（辅助方法）
     */
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

    /**
     * 带参数的服务调用（供内部或外部调用）
     * @param domain 服务域（如"climate"）
     * @param service 服务名称（如"set_fan_mode"）
     * @param entityId 设备ID
     * @param data 额外参数
     */
    public void callServiceWithData(String domain, String service, String entityId, Map<String, Object> data) {
        if (isConnected) {
            webSocketClient.callServiceWithData(domain, service, entityId, data);
        } else {
            System.err.println("未连接到Home Assistant，无法调用服务：" + domain + "." + service);
            reconnect();
        }
    }
}
