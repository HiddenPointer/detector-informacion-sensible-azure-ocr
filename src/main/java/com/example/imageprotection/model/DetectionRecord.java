package com.example.imageprotection.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un registro de detección de dato sensible en la base de datos.
 * Almacena tipo de dato, valor detectado, timestamp de detección y hash de la imagen.
 */
@Entity
public class DetectionRecord {
    // Identificador único generado por la base de datos.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Tipo de dato detectado (p.ej., "dni", "placa", "direccion").
    private String tipoDato;
    // Valor exacto encontrado en la imagen.
    private String valor;
    // Fecha y hora cuando se realizó la detección.
    private LocalDateTime timestamp;
    // Hash SHA-256 de los bytes de la imagen procesada.
    private String imageHash;

    public DetectionRecord() {}

    /**
     * Constructor para inicializar todos los campos de la entidad.
     */
    public DetectionRecord(String tipoDato, String valor, LocalDateTime timestamp, String imageHash) {
        this.tipoDato = tipoDato;
        this.valor = valor;
        this.timestamp = timestamp;
        this.imageHash = imageHash;
    }

    public Long getId() { return id; }
    public String getTipoDato() { return tipoDato; }
    public void setTipoDato(String tipoDato) { this.tipoDato = tipoDato; }
    public String getValor() { return valor; }
    public void setValor(String valor) { this.valor = valor; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public String getImageHash() { return imageHash; }
    public void setImageHash(String imageHash) { this.imageHash = imageHash; }
}
