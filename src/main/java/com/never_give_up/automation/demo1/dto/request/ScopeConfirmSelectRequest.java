package com.never_give_up.automation.demo1.dto.request;


import lombok.Data;

@Data
public class ScopeConfirmSelectRequest {
    private String scopeType;
    private java.util.List<String> selectedPlaceIds;
    private java.util.List<String> selectedEquipmentIds;
    private java.util.List<String> selectedActivityIds;
    private java.util.List<String> specialTopics;
    private String scopeDescription;
    private String operator;
}
