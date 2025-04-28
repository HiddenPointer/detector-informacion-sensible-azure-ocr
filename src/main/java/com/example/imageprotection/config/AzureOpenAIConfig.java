package com.example.imageprotection.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración para Azure OpenAI.
 * @param endpoint URL de Azure OpenAI resource
 * @param key Clave de acceso
 * @param deploymentName Nombre del deployment (e.g., gpt-35-turbo)
 */
@Configuration
@ConfigurationProperties(prefix = "azure.openai")
public class AzureOpenAIConfig {
    private String endpoint;
    private String key;
    private String deploymentName;

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

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }
}
