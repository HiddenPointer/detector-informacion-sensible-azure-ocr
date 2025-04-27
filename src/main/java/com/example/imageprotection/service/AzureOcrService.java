package com.example.imageprotection.service;

import com.example.imageprotection.config.AzureOcrConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que llama a Azure Computer Vision OCR para extraer texto de imágenes.
 * Utiliza RestTemplate para enviar la imagen y parsear la respuesta JSON.
 */
@Service
public class AzureOcrService {
    private final AzureOcrConfig config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public AzureOcrService(AzureOcrConfig config, RestTemplate restTemplate) {
        this.config = config;
        this.restTemplate = restTemplate;
    }

    /**
     * Envía la imagen al servicio Azure OCR y retorna las líneas de texto extraídas.
     *
     * @param imageBytes Arreglo de bytes de la imagen a procesar.
     * @return Lista de cadenas, cada una es una línea de texto detectada.
     * @throws Exception si ocurre un error en la petición HTTP o en el parseo.
     */
    public List<String> extractText(byte[] imageBytes) throws Exception {
        URI uri = URI.create(config.getEndpoint());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set("Ocp-Apim-Subscription-Key", config.getKey());
        HttpEntity<byte[]> entity = new HttpEntity<>(imageBytes, headers);
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
        if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.ACCEPTED) {
            throw new RuntimeException("Error from OCR service: " + response.getStatusCode());
        }
        String body = response.getBody();
        JsonNode root = objectMapper.readTree(body);
        List<String> lines = new ArrayList<>();
        JsonNode regions = root.path("regions");
        if (regions.isArray()) {
            for (JsonNode region : regions) {
                JsonNode regionLines = region.path("lines");
                if (regionLines.isArray()) {
                    for (JsonNode line : regionLines) {
                        JsonNode words = line.path("words");
                        StringBuilder sb = new StringBuilder();
                        if (words.isArray()) {
                            for (JsonNode word : words) {
                                sb.append(word.path("text").asText()).append(" ");
                            }
                            lines.add(sb.toString().trim());
                        }
                    }
                }
            }
        }
        return lines;
    }
}
