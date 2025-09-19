package com.never_give_up.automation.Entity;

import lombok.Data;

/**
 * Home Assistant 上下文实体（操作溯源信息）
 */
@Data
public class HaContext {
    // 上下文ID（唯一标识）
    private String id;

    // 父上下文ID（若为子操作，如手动触发的状态变更）
    private String parent_id;

    // 操作用户ID（若为用户触发，系统操作则为null）
    private String user_id;
}
