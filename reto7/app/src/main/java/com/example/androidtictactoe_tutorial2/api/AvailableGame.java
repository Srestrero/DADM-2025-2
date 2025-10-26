package com.example.androidtictactoe_tutorial2.api;

/**
 * Representa un juego disponible para unirse
 */
public class AvailableGame {
    private String game_id;
    private String jugador_x_id;

    public AvailableGame() {}

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getJugador_x_id() {
        return jugador_x_id;
    }

    public void setJugador_x_id(String jugador_x_id) {
        this.jugador_x_id = jugador_x_id;
    }
}
