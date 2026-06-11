package com.never_give_up.automation.demo1.dto.request;

import lombok.Data;

@Data
public class ParseMaterialRequest {
    private Boolean forceOcr;
    private Integer maxChars;
}