package com.example.imageprotection.model;

/**
 * DTO para representar una detección de dato sensible en una imagen.
 * Contiene el tipo de dato detectado (e.g., "dni", "placa", "direccion")
 * y el valor exacto encontrado en la imagen.
 */
public class Detection {
    // Tipo de dato detectado, p.ej., "dni" o "placa"
    private String tipoDato;
    // Valor exacto detectado en la imagen
    private String valor;

    public Detection() {}

    /**
     * Constructor con campos para inicializar la detección.
     * @param tipoDato Tipo de dato detectado.
     * @param valor Valor encontrado.
     */
    public Detection(String tipoDato, String valor) {
        this.tipoDato = tipoDato;
        this.valor = valor;
    }

    /**
     * Obtiene el tipo de dato detectado.
     */
    public String getTipoDato() {
        return tipoDato;
    }

    /**
     * Establece el tipo de dato detectado.
     * @param tipoDato Nuevo tipo de dato.
     */
    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    /**
     * Obtiene el valor detectado.
     */
    public String getValor() {
        return valor;
    }

    /**
     * Establece el valor detectado.
     * @param valor Nuevo valor.
     */
    public void setValor(String valor) {
        this.valor = valor;
    }
}
