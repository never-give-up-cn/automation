package com.never_give_up.automation.demo1.dto;

import lombok.Data;

@Data
public class ApiResponse<T> {
    private Integer code;
    private String message;
    private T data;
}