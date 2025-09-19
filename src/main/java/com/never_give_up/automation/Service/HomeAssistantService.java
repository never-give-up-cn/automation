package com.never_give_up.automation.Service;

import com.never_give_up.automation.WebSocket.HomeAssistantWebSocketClient;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

@Service
public class HomeAssistantService {

    private final HomeAssistantWebSocketClient webSocketClient;
    private boolean isConnected = false;

    // 构造函数注入WebSocket客户端
    public HomeAssistantService(HomeAssistantWebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    // 初始化方法
    @PostConstruct
    public void init() {
        try {
            // 确保连接建立
            if (!webSocketClient.isOpen()) {
                webSocketClient.connect();
            }

            // 等待连接建立（最多等待5秒）
            int retryCount = 0;
            while (!isConnected && retryCount < 10) {
                Thread.sleep(500);
                retryCount++;
            }

            if (isConnected) {
                System.out.println("Home Assistant WebSocket 连接成功");
            } else {
                System.err.println("Home Assistant WebSocket 连接超时");
                // 不要抛出异常，以免导致Spring上下文初始化失败
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("初始化Home Assistant连接时发生中断: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("初始化Home Assistant连接时发生错误: " + e.getMessage());
        }
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
}
