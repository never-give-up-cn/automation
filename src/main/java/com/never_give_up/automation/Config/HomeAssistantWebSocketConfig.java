package com.never_give_up.automation.Config;

import com.never_give_up.automation.Service.HomeAssistantService;
import com.never_give_up.automation.WebSocket.HomeAssistantWebSocketClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class HomeAssistantWebSocketConfig {

    private static final String HASS_WS_URL = "ws://homeassistant.local:8123/api/websocket";
    private static final String HASS_ORIGIN = "http://homeassistant.local:8123/";

    @Bean(destroyMethod = "close")
    public HomeAssistantWebSocketClient homeAssistantWebSocketClient() throws URISyntaxException {
        // 创建头信息
        Map<String, String> headers = new HashMap<>();
        headers.put("Origin", HASS_ORIGIN);

        // 创建客户端实例
        HomeAssistantWebSocketClient client = new HomeAssistantWebSocketClient(
                new URI(HASS_WS_URL),
                headers
        );

        return client;
    }

    @Bean
    public HomeAssistantService homeAssistantService(HomeAssistantWebSocketClient client) {
        HomeAssistantService service = new HomeAssistantService(client);
        // 建立双向关联
        client.setService(service);
        return service;
    }
}
