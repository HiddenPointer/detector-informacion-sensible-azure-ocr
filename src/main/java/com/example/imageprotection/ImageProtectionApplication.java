package com.example.imageprotection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Spring Boot.
 * Punto de entrada que arranca el contexto de Spring y despliega el servidor.
 */
@SpringBootApplication
public class ImageProtectionApplication {
    /**
     * Método main que inicia la aplicación.
     * @param args Argumentos de línea de comando (no utilizados).
     */
    public static void main(String[] args) {
        SpringApplication.run(ImageProtectionApplication.class, args);
    }
}
