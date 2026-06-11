package com.never_give_up.automation.demo1.dto.request;

import lombok.Data;

@Data
public class UpdateWorkflowRequest {
    private String enterpriseName;
    private String industry;
    private String status;
}