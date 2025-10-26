package com.example.androidtictactoe_tutorial2.api;

import java.util.List;

/**
 * Respuesta para realizar un movimiento
 */
public class MakeMoveResponse extends ApiResponse {
    private String mensaje;
    private List<String> tablero_nuevo;
    private String turno_siguiente;
    private String resultado_final;

    public MakeMoveResponse() {}

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public List<String> getTablero_nuevo() {
        return tablero_nuevo;
    }

    public void setTablero_nuevo(List<String> tablero_nuevo) {
        this.tablero_nuevo = tablero_nuevo;
    }

    public String getTurno_siguiente() {
        return turno_siguiente;
    }

    public void setTurno_siguiente(String turno_siguiente) {
        this.turno_siguiente = turno_siguiente;
    }

    public String getResultado_final() {
        return resultado_final;
    }

    public void setResultado_final(String resultado_final) {
        this.resultado_final = resultado_final;
    }
}
