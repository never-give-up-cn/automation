package com.never_give_up.automation.Entity;

import lombok.Data;

/**
 * 事件详情（包含事件类型和变更数据）
 */
@Data
public class HaEvent {
    private String event_type; // 事件类型（如 "state_changed"）
    private HaStateChangedData data; // 状态变更的具体数据
    private HaContext context; // 上下文信息
}
