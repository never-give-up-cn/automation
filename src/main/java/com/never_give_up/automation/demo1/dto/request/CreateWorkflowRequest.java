package com.never_give_up.automation.demo1.dto.request;

import lombok.Data;

@Data
public class CreateWorkflowRequest {
    private String enterpriseName;
    private String industry;
    private String createdBy;
    private String externalTaskId;
    private String externalUserId;
}