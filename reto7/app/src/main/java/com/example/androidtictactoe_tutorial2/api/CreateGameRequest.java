package com.example.androidtictactoe_tutorial2.api;

/**
 * Request para crear un nuevo juego
 */
public class CreateGameRequest {
    private String jugador_id;

    public CreateGameRequest() {}

    public CreateGameRequest(String jugadorId) {
        this.jugador_id = jugadorId;
    }

    public String getJugador_id() {
        return jugador_id;
    }

    public void setJugador_id(String jugador_id) {
        this.jugador_id = jugador_id;
    }
}
