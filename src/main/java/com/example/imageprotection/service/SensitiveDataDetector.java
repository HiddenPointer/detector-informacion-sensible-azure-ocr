package com.example.imageprotection.service;

import com.example.imageprotection.model.Detection;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servicio que detecta patrones de datos sensibles (DNI, placa, dirección) en texto.
 * Utiliza expresiones regulares para encontrar coincidencias en cada línea.
 */
@Service
public class SensitiveDataDetector {
    // Patrón para 8 dígitos de DNI
    private static final Pattern DNI_PATTERN = Pattern.compile("\\b\\d{8}\\b");
    // Patrón para placas de formato AAA999
    private static final Pattern PLATE_PATTERN = Pattern.compile("\\b[A-Z]{3}\\d{3}\\b");
    // Patrón para detectar direcciones con palabras clave (Calle, Av., Street)
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("\\b(?:Calle|Av\\.?|Avenida|Street|St\\.)\\s+\\w+\\b", Pattern.CASE_INSENSITIVE);

    public List<Detection> detect(List<String> lines) {
        // Lista donde se almacenan todas las detecciones encontradas
        List<Detection> detections = new ArrayList<>();
        for (String line : lines) {
            Matcher m = DNI_PATTERN.matcher(line);
            while (m.find()) {
                detections.add(new Detection("dni", m.group()));
            }
            m = PLATE_PATTERN.matcher(line);
            while (m.find()) {
                detections.add(new Detection("placa", m.group()));
            }
            // Buscar direcciones en la línea actual
            m = ADDRESS_PATTERN.matcher(line);
            while (m.find()) {
                detections.add(new Detection("direccion", m.group().trim()));
            }
        }
        return detections;
    }
}
