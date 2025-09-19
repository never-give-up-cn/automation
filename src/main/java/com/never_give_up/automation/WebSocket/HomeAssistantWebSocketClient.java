package com.never_give_up.automation.WebSocket;

import cn.hutool.json.JSONUtil;
import com.never_give_up.automation.Entity.HaEntityState;
import com.never_give_up.automation.Entity.HaError;
import com.never_give_up.automation.Entity.HaWebSocketResponse;
import com.never_give_up.automation.Service.HomeAssistantService;
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
    private HomeAssistantService service;

    // 构造函数，接收URI和头信息
    public HomeAssistantWebSocketClient(URI serverUri, Map<String, String> headers) {
        super(serverUri, headers);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to Home Assistant WebSocket API");
        // 通知服务类连接已建立
        if (service != null) {
            service.setConnected(true);
        }
        // 发送认证消息
        authenticate();
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Received message: " + message);
        try {
            // 1. 使用 Hutool 将 JSON 字符串转为顶层实体类（第三个参数 true 表示支持下划线转驼峰）
            HaWebSocketResponse response = JSONUtil.toBean(
                    message,
                    HaWebSocketResponse.class,
                    true // 关键：自动处理 JSON 中的下划线字段（如 entity_id）与实体类驼峰字段的映射
            );

            // 2. 根据消息类型和状态做差异化处理
            if ("result".equals(response.getType())) {
                System.out.println("------------"+response);
                handleResultResponse(response); // 处理 result 类型消息（核心设备状态数据）
            } else if ("auth_invalid".equals(response.getType())) {
                System.err.println("认证失败：" + response.getError().getMessage());
                if (service != null) {
                    service.setConnected(false);
                }
            } else if ("state_changed".equals(response.getType())) {
                // 若需要处理实时状态变更（如灯从关到开），可新增状态变更实体类并解析
                System.out.println("设备状态实时变更，需单独解析（可参考上述实体类扩展）");
            }

        } catch (Exception e) {
            System.err.println("JSON 解析失败，原始消息：" + message);
            e.printStackTrace();
        }
    }

    /**
     * 处理 type="result" 类型的响应（核心设备状态数据）
     */
    private void handleResultResponse(HaWebSocketResponse response) {
        if (response.getSuccess()) {
            // 2.1 操作成功：获取设备状态列表，做业务处理
            List<HaEntityState> entityStates = response.getResult();
            if (entityStates != null && !entityStates.isEmpty()) {
                System.out.println("获取到 " + entityStates.size() + " 个设备状态");

                // 示例1：筛选温湿度传感器（entity_id 包含 "temperature" 或 "humidity"）
                List<HaEntityState> sensorStates = entityStates.stream()
                        .filter(state -> state.getEntity_id().contains("temperature")
                                || state.getEntity_id().contains("humidity"))
                        .collect(Collectors.toList());
                System.out.println("温湿度传感器数量：" + sensorStates.size());

                // 示例2：获取某个具体设备的状态（如室内温度传感器）
                HaEntityState indoorTempSensor = entityStates.stream()
                        .filter(state -> "sensor.xiaomi_cn_blt_3_1mggp6l144g01_mini_temperature".equals(state.getEntity_id()))
                        .findFirst()
                        .orElse(null);
                if (indoorTempSensor != null) {
                    String temp = indoorTempSensor.getState(); // 温度值（如 "28.7"）
                    String friendlyName = indoorTempSensor.getAttributes().get("friendly_name").toString(); // 友好名称
                    System.out.println(friendlyName + " 当前温度：" + temp + "°C");
                }

            } else {
                System.out.println("操作成功，但无设备状态数据");
            }
        } else {
            // 2.2 操作失败：打印错误信息
            HaError error = response.getError();
            System.err.println("操作失败，错误码：" + error.getCode() + "，描述：" + error.getMessage());
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

    // 设置服务引用
    public void setService(HomeAssistantService service) {
        this.service = service;
    }
}
