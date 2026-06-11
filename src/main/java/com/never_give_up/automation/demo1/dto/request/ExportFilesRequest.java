package com.never_give_up.automation.demo1.dto.request;


import lombok.Data;

@Data
public class ExportFilesRequest {
    private java.util.List<String> formats;
    private String operator;
}