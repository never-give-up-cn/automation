package com.never_give_up.automation.Entity;
import cn.hutool.core.annotation.Alias;
import lombok.Data;
import java.util.List;

/**
 * Home Assistant WebSocket 响应顶层实体
 */
@Data // Lombok 注解，自动生成 getter/setter/toString，需引入 lombok 依赖（或手动写）
public class HaWebSocketResponse {
    // 消息ID（可能为null，如错误消息）
    private Integer id;

    // 消息类型（如 "result"、"auth_ok"、"state_changed"）
    private String type;

    // 操作是否成功（仅 result 类型消息有）
    private Boolean success;

    // 核心数据：设备状态数组（仅 success=true 时有）
    private List<HaEntityState> result;

    // 错误信息（仅 success=false 时有）
    private HaError error;
}