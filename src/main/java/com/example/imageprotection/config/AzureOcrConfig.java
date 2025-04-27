package com.example.imageprotection.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "azure.ocr")
// Binds properties azure.ocr.endpoint and azure.ocr.key from application.properties
public class AzureOcrConfig {
    // Azure Computer Vision OCR API endpoint URL
    private String endpoint;
    // Azure subscription key for OCR service
    private String key;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
