package com.example.androidtictactoe_tutorial2.api;

/**
 * Request para unirse a un juego
 */
public class JoinGameRequest {
    private String jugador_id;

    public JoinGameRequest() {}

    public JoinGameRequest(String jugadorId) {
        this.jugador_id = jugadorId;
    }

    public String getJugador_id() {
        return jugador_id;
    }

    public void setJugador_id(String jugador_id) {
        this.jugador_id = jugador_id;
    }
}
