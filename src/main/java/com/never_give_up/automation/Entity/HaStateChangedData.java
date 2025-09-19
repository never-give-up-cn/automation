package com.never_give_up.automation.Entity;

import lombok.Data;

/**
 * state_changed 事件的具体数据（包含旧状态和新状态）
 */
@Data
public class HaStateChangedData {
    private HaEntityState old_state; // 变更前的状态（可能为null，如设备首次上线）
    private HaEntityState new_state; // 变更后的状态（核心数据）
}