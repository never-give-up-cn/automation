package com.never_give_up.automation.demo1.dto.request;

import lombok.Data;

@Data
public class StandardizationFilesRequest {
    private String month;
    private java.util.List<String> formats;
    private String operator;
}