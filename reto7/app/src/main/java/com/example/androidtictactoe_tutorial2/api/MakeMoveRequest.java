package com.example.androidtictactoe_tutorial2.api;

/**
 * Request para realizar un movimiento
 */
public class MakeMoveRequest {
    private String jugador_id;
    private int posicion;

    public MakeMoveRequest() {}

    public MakeMoveRequest(String jugadorId, int posicion) {
        this.jugador_id = jugadorId;
        this.posicion = posicion;
    }

    public String getJugador_id() {
        return jugador_id;
    }

    public void setJugador_id(String jugador_id) {
        this.jugador_id = jugador_id;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }
}
