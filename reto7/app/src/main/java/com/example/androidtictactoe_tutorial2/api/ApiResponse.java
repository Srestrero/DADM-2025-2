package com.example.androidtictactoe_tutorial2.api;

/**
 * Clase base para respuestas de la API
 */
public class ApiResponse {
    private String detail;

    public ApiResponse() {}

    public ApiResponse(String detail) {
        this.detail = detail;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
