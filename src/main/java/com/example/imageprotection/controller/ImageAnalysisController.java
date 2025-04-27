package com.example.imageprotection.controller;

import com.example.imageprotection.model.Detection;
import com.example.imageprotection.model.DetectionRecord;
import com.example.imageprotection.repository.DetectionRecordRepository;
import com.example.imageprotection.service.AzureOcrService;
import com.example.imageprotection.service.SensitiveDataDetector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controlador REST que expone endpoint para analizar imágenes.
 * Recibe una imagen vía multipart/form-data, extrae texto, detecta datos sensibles y guarda registros.
 */
@RestController
@RequestMapping("/api")
public class ImageAnalysisController {

    private static final Logger logger = LoggerFactory.getLogger(ImageAnalysisController.class);

    private final AzureOcrService azureOcrService;
    private final SensitiveDataDetector sensitiveDataDetector;
    private final DetectionRecordRepository detectionRecordRepository;

    @Autowired
    public ImageAnalysisController(AzureOcrService azureOcrService,
                                   SensitiveDataDetector sensitiveDataDetector,
                                   DetectionRecordRepository detectionRecordRepository) {
        this.azureOcrService = azureOcrService;
        this.sensitiveDataDetector = sensitiveDataDetector;
        this.detectionRecordRepository = detectionRecordRepository;
    }

    /**
     * Analiza la imagen enviada y detecta datos sensibles.
     *
     * @param file Archivo de imagen subido por el usuario.
     * @return ResponseEntity con lista de detecciones o mensaje de error.
     */
    @PostMapping(value = "/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> analyze(@RequestParam("file") MultipartFile file) {
        try {
            // Obtener bytes de la imagen
            byte[] imageBytes = file.getBytes();
            // Calcular hash SHA-256 para identificar la imagen
            String hash = computeHash(imageBytes);

            // Llamar al servicio OCR de Azure para extraer texto
            List<String> lines = azureOcrService.extractText(imageBytes);
            // Detectar datos sensibles en el texto extraído
            List<Detection> detections = sensitiveDataDetector.detect(lines);

            // Crear entidades para la base de datos con timestamp y hash de imagen
            List<DetectionRecord> records = detections.stream()
                    .map(d -> new DetectionRecord(d.getTipoDato(), d.getValor(), LocalDateTime.now(), hash))
                    .collect(Collectors.toList());
            // Guardar registros en la base de datos
            detectionRecordRepository.saveAll(records);

            // Retornar la lista de detecciones al frontend
            return ResponseEntity.ok(detections);
        } catch (Exception e) {
            // Registro del error y retorno de mensaje al cliente
            logger.error("Error processing image", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo procesar la imagen"));
        }
    }

    /**
     * Calcula el hash SHA-256 de un arreglo de bytes.
     *
     * @param data Arreglo de bytes de la imagen.
     * @return Cadena hexadecimal del hash calculado.
     * @throws Exception si ocurre un error al obtener el algoritmo o en el proceso.
     */
    private String computeHash(byte[] data) throws Exception {
        // Instanciar algoritmo SHA-256
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(data);
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            // Convertir cada byte a hex y concatenar
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
