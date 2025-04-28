package com.example.imageprotection.service;

import com.example.imageprotection.config.AzureOpenAIConfig;
import com.example.imageprotection.model.Detection;
import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import com.azure.ai.openai.models.ChatRequestSystemMessage;
import com.azure.core.credential.AzureKeyCredential;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Servicio que usa Azure OpenAI para detección avanzada de información sensible.
 */
@Service
public class AzureOpenAIService {
    private final OpenAIClient client;
    private final AzureOpenAIConfig config;
    private final ObjectMapper objectMapper;

    @Autowired
    public AzureOpenAIService(AzureOpenAIConfig config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.client = new OpenAIClientBuilder()
            .endpoint(config.getEndpoint())
            .credential(new AzureKeyCredential(config.getKey()))
            .buildClient();
    }

    /**
     * Envía el texto extraído a un agente IA (Chat Completions) para detectar datos sensibles.
     *
     * @param lines Líneas de texto extraídas por OCR.
     * @return Lista de objetos Detection con tipoDato y valor.
     * @throws IOException si falla la petición o el parseo de la respuesta.
     */
    public List<Detection> detectSensitiveData(List<String> lines) throws IOException {
        String text = String.join("\n", lines);
        ChatRequestSystemMessage systemMessage = new ChatRequestSystemMessage(
            "Eres un detector de datos sensibles. Extrae todos los datos sensibles " +
            "(DNI, placas, direcciones u otra información personal) del texto proporcionado. " +
            "Devuélvelos como JSON array de objetos con \"tipoDato\" y \"valor\".");
        ChatRequestUserMessage userMessage = new ChatRequestUserMessage(text);
        ChatCompletionsOptions options = new ChatCompletionsOptions(List.of(systemMessage, userMessage));
        ChatCompletions completions = client.getChatCompletions(config.getDeploymentName(), options);
        ChatChoice choice = completions.getChoices().get(0);
        String content = choice.getMessage().getContent();

        return objectMapper.readValue(content, new TypeReference<List<Detection>>() {});
    }
}
