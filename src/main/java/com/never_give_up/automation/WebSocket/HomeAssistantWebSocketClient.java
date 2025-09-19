package com.never_give_up.automation.WebSocket;

import cn.hutool.json.JSONUtil;
import com.never_give_up.automation.Entity.*;
import com.never_give_up.automation.Service.HomeAssistantService;
import lombok.Setter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HomeAssistantWebSocketClient extends WebSocketClient {
    private static final String HASS_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhN2IyNDA4NjhmYzc0YjVjYTYzZDRlOTIxN2U4YmQwNyIsImlhdCI6MTc1ODI0ODMwOCwiZXhwIjoyMDczNjA4MzA4fQ.o49-lqV9HMCD9KJRfi4d_0w0c8yUSpmyPDiyoeMAxNk";
    private int messageId = 1;
    // 设置服务引用
    @Setter
    private HomeAssistantService service;

    // 构造函数，接收URI和头信息
    public HomeAssistantWebSocketClient(URI serverUri, Map<String, String> headers) {
        super(serverUri, headers);
    }

    // -------------------------- 核心改造：连接成功后初始化 --------------------------
    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to Home Assistant WebSocket API");
        if (service != null) {
            service.setConnected(true);
        }
        authenticate(); // 先认证
    }

    @Override
    public void onMessage(String message) {
        try {
            // 用 Hutool 解析为顶层实体类
            HaWebSocketResponse response = JSONUtil.toBean(message, HaWebSocketResponse.class, true);
            if (response == null || response.getType() == null) {
                System.out.println("无效消息：" + message);
                return;
            }

            // 按消息类型分支处理
            switch (response.getType()) {
                case "auth_required":
                    authenticate(); // 触发认证
                    break;
                case "auth_ok":
                    System.out.println("Authentication successful，开始初始化状态...");
                    // 认证成功后：1.拉取初始全量状态 2.订阅实时变更事件
                    getStates();
                    subscribeStateChangedEvent();
                    break;
                case "auth_invalid":
                    System.err.println("认证失败：" + (response.getError() != null ? response.getError().getMessage() : "未知错误"));
                    if (service != null) {
                        service.setConnected(false);
                    }
                    break;
                case "result":
                    // 处理主动请求的结果（如 get_states 的返回）
                    handleResultResponse(response);
                    break;
                case "event":
                    // 处理订阅的实时事件（如 state_changed）
                    handleEventResponse(message);
                    break;
                default:
                    System.out.println("未处理的消息类型：" + response.getType());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // -------------------------- 新增：订阅 state_changed 实时事件 --------------------------

    /**
     * 订阅设备状态变更事件（Home Assistant 会在设备状态变化时主动推送）
     */
    private void subscribeStateChangedEvent() {
        JSONObject subscribeMsg = new JSONObject();
        subscribeMsg.put("id", messageId++);
        subscribeMsg.put("type", "subscribe_events");
        subscribeMsg.put("event_type", "state_changed"); // 订阅状态变更事件
        // 可选：添加 entity_id 过滤（如只订阅温湿度传感器，减少推送量）
        // subscribeMsg.put("entity_id", "sensor.xiaomi_cn_blt_3_1mggdb23g4g00_mini_temperature");

        send(subscribeMsg.toString());
        System.out.println("已订阅设备状态变更事件（state_changed）");
    }

    // -------------------------- 新增：处理实时事件推送 --------------------------

    /**
     * 处理 Home Assistant 推送的实时事件（如设备状态变更）
     */
    private void handleEventResponse(String message) {
        // 解析事件数据（state_changed 事件格式特殊，需单独处理）
        HaEventResponse eventResponse = JSONUtil.toBean(message, HaEventResponse.class, true);
        if (eventResponse == null || eventResponse.getEvent() == null) {
            System.out.println("无效事件消息：" + message);
            return;
        }

        // 只处理 state_changed 事件
        if ("state_changed".equals(eventResponse.getEvent().getEvent_type())) {
            HaStateChangedData data = eventResponse.getEvent().getData();
            if (data != null && data.getNew_state() != null) {
                HaEntityState newState = data.getNew_state();
                // 打印实时变更信息（可替换为业务逻辑，如存储到数据库、触发告警等）
                System.out.println("\n=== 设备状态实时变更 ===");
                System.out.println("设备ID：" + newState.getEntity_id());
                System.out.println("设备属性：" + newState.getAttributes());
                System.out.println("旧状态：" + (data.getOld_state() != null ? data.getOld_state().getState() : "无"));
                System.out.println("新状态：" + newState.getState());
                System.out.println("变更时间：" + newState.getLast_changed());
                System.out.println("=======================\n");
//                service.turnOffDevice("switch.cuco_cn_944233652_v3_on_p_2_1");
                // 可选：将实时状态同步到服务类（供外部调用）
                if (service != null) {
                    service.updateEntityState(newState);
                }
            }
        }
    }

    /**
     * 处理 type="result" 类型的响应（核心设备状态数据）
     */
    private void handleResultResponse(HaWebSocketResponse response) {
        if (response.getSuccess()) {
            // 获取原始result对象（可能是列表、上下文对象或null）
            Object result = response.getResult();

            // 1. 判断result是否为设备状态列表（仅get_states接口返回）
            if (result instanceof List<?>) {
                try {
                    // 强制转换为设备状态列表（需要确保泛型类型正确）
                    List<HaEntityState> entityStates = (List<HaEntityState>) result;
                    if (entityStates != null && !entityStates.isEmpty()) {
                        System.out.println("获取到 " + entityStates.size() + " 个设备状态");

                        // 过滤掉entity_id为null的无效数据
                        List<HaEntityState> validStates = entityStates.stream()
                                .filter(state -> state != null && state.getEntity_id() != null)
                                .collect(Collectors.toList());

                        // 关键修复：调用服务类的方法同步全量状态到本地缓存
                        if (service != null) {
                            service.setAllEntityStates(validStates); // 这里是核心调用
                        }

                        // 示例1：筛选温湿度传感器
                        List<HaEntityState> sensorStates = validStates.stream()
                                .filter(state -> state.getEntity_id().matches(".*(temp|temperature|humid|humidity).*"))
                                .collect(Collectors.toList());
                        System.out.println("温湿度传感器数量：" + sensorStates.size());

                        // 示例2：获取某个具体设备的状态
                        HaEntityState indoorTempSensor = validStates.stream()
                                .filter(state -> "sensor.xiaomi_cn_blt_3_1mggp6l144g01_mini_temperature".equals(state.getEntity_id()))
                                .findFirst()
                                .orElse(null);
                        if (indoorTempSensor != null) {
                            String temp = indoorTempSensor.getState();
                            String friendlyName = indoorTempSensor.getAttributes()
                                    .getOrDefault("friendly_name", "未知设备").toString();
                            System.out.println(friendlyName + " 当前温度：" + temp + "°C");
                        }
                    } else {
                        System.out.println("操作成功，但无设备状态数据");
                    }
                } catch (ClassCastException e) {
                    System.err.println("设备状态列表解析失败，类型不匹配：" + e.getMessage());
                }
            }
            // 2. 处理操作结果上下文（如关闭设备、调用服务后的响应）
            else if (result instanceof Map<?, ?>) {
                Map<?, ?> resultMap = (Map<?, ?>) result;
                // 提取上下文信息
                Map<?, ?> context = (Map<?, ?>) resultMap.get("context");
                String operationId = context != null ? context.get("id").toString() : "未知";
                System.out.println("操作成功，操作ID：" + operationId);
                // 可在此处添加操作成功后的业务逻辑（如记录日志、更新本地状态）
            }
            // 3. 其他类型的result（如空结果）
            else {
                System.out.println("操作成功，无详细结果数据");
            }
        } else {
            // 操作失败处理
            HaError error = response.getError();
            if (error != null) {
                System.err.println("操作失败，错误码：" + error.getCode() + "，描述：" + error.getMessage());
            } else {
                System.err.println("操作失败，未知错误");
            }
        }
    }


    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Connection closed. Code: " + code + ", Reason: " + reason);
        // 通知服务类连接已关闭
        if (service != null) {
            service.setConnected(false);
        }
    }

    @Override
    public void onError(Exception ex) {
        System.err.println("Error occurred: " + ex.getMessage());
        ex.printStackTrace();
        // 发生错误时标记为未连接
        if (service != null) {
            service.setConnected(false);
        }
    }

    /**
     * 发送认证消息
     */
    private void authenticate() {
        JSONObject authMessage = new JSONObject();
        authMessage.put("type", "auth");
        authMessage.put("access_token", HASS_TOKEN);

        send(authMessage.toString());
        System.out.println("Sent authentication request");
    }

    /**
     * 获取所有实体状态
     */
    public void getStates() {
        JSONObject message = new JSONObject();
        message.put("id", messageId++);
        message.put("type", "get_states");

        send(message.toString());
        System.out.println("Sent get_states request");
    }

    /**
     * 调用服务
     */
    public void callService(String domain, String service, String entityId) {
        JSONObject message = new JSONObject();
        message.put("id", messageId++);
        message.put("type", "call_service");
        message.put("domain", domain);
        message.put("service", service);

        JSONObject serviceData = new JSONObject();
        serviceData.put("entity_id", entityId);
        message.put("service_data", serviceData);

        send(message.toString());
        System.out.println("Sent call_service request: " + domain + "." + service + " for " + entityId);
    }

    // -------------------------- 新增：带参数的服务调用方法 --------------------------

    /**
     * 调用Home Assistant服务并传递参数（支持空调温度调节、模式切换等）
     *
     * @param domain   服务域（如 "climate"、"switch"）
     * @param service  服务名称（如 "set_temperature"、"set_hvac_mode"）
     * @param entityId 设备ID
     * @param data     额外参数（如温度、模式等）
     */
    public void callServiceWithData(String domain, String service, String entityId, Map<String, Object> data) {
        try {
            JSONObject message = new JSONObject();
            message.put("id", messageId++); // 递增的消息ID
            message.put("type", "call_service");
            message.put("domain", domain);
            message.put("service", service);

            // 构建服务参数（包含设备ID和其他参数）
            JSONObject serviceData = new JSONObject();
            serviceData.put("entity_id", entityId); // 必须包含设备ID

            // 添加额外参数（如温度、模式）
            if (data != null && !data.isEmpty()) {
                for (Map.Entry<String, Object> entry : data.entrySet()) {
                    serviceData.put(entry.getKey(), entry.getValue());
                }
            }

            message.put("service_data", serviceData);

            // 发送消息
            String jsonStr = message.toString();
            System.out.println("发送带参数的服务调用：" + jsonStr);
            send(jsonStr);
        } catch (Exception e) {
            System.err.println("构建服务调用消息失败：" + e.getMessage());
            e.printStackTrace();
        }
    }

}
