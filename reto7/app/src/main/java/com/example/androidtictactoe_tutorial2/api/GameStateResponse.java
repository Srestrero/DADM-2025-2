package com.example.androidtictactoe_tutorial2.api;

import java.util.List;
import java.util.Map;

/**
 * Respuesta para obtener el estado del juego
 */
public class GameStateResponse extends ApiResponse {
    private String game_id;
    private String estado;
    private List<String> tablero;
    private String turno_actual;
    private Map<String, String> jugadores;
    private String resultado;
    private String fecha_creacion;

    public GameStateResponse() {}

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<String> getTablero() {
        return tablero;
    }

    public void setTablero(List<String> tablero) {
        this.tablero = tablero;
    }

    public String getTurno_actual() {
        return turno_actual;
    }

    public void setTurno_actual(String turno_actual) {
        this.turno_actual = turno_actual;
    }

    public Map<String, String> getJugadores() {
        return jugadores;
    }

    public void setJugadores(Map<String, String> jugadores) {
        this.jugadores = jugadores;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public String getFecha_creacion() {
        return fecha_creacion;
    }

    public void setFecha_creacion(String fecha_creacion) {
        this.fecha_creacion = fecha_creacion;
    }
}
