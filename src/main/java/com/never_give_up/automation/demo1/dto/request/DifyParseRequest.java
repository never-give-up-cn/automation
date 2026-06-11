package com.never_give_up.automation.demo1.dto.request;


import lombok.Data;

@Data
public class DifyParseRequest {
    private String parseSource;
    private String operator;
    private Boolean includeResult;
    private Boolean asyncMode;
}