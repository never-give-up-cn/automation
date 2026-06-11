package com.never_give_up.automation.demo1.dto.request;


import lombok.Data;

@Data
public class ReviewItemActionRequest {
    private String action;
    private String status;
    private String reason;
    private String operator;
}