package com.never_give_up.automation.Entity;


import lombok.Data;
import java.util.Map;

/**
 * Home Assistant 设备状态实体（result 数组元素）
 */
@Data
public class HaEntityState {
    // 设备唯一ID（如 "sensor.xiaomi_cn_blt_3_1mggdb23g4g00_mini_temperature"）
    private String entity_id;

    // 设备当前状态（如 "on"、"off"、"28.7"、"rainy"）
    private String state;

    // 设备属性（动态字段，不同设备属性不同，用 Map 兼容）
    private Map<String, Object> attributes;

    // 状态最后变更时间
    private String last_changed;

    // 状态最后上报时间
    private String last_reported;

    // 状态最后更新时间
    private String last_updated;

    // 上下文信息（操作来源、用户ID等）
    private HaContext context;
}