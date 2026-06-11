package com.never_give_up.automation.demo1.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "risk.assessment")
public class RiskAssessmentProperties {
    private String baseUrl;
    private String apiKey;
}