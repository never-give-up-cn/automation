package com.never_give_up.automation.Entity;

import lombok.Data;

/**
 * Home Assistant 错误信息实体
 */
@Data
public class HaError {
    // 错误码（如 "invalid_format"、"auth_invalid"）
    private String code;

    // 错误描述
    private String message;
}
