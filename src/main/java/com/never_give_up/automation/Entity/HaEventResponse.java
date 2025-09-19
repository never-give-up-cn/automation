package com.never_give_up.automation.Entity;

import lombok.Data;

/**
 * Home Assistant 事件推送响应（如 state_changed）
 */
@Data
public class HaEventResponse {
    private Integer id;
    private String type; // 固定为 "event"
    private HaEvent event; // 事件详情
}