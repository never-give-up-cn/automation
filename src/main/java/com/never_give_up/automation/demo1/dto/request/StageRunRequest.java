package com.never_give_up.automation.demo1.dto.request;

import lombok.Data;

@Data
public class StageRunRequest {
    private String userAction;
    private String userInstruction;
    private String targetObjectId;
    private String targetObjectType;
    private String operator;
}