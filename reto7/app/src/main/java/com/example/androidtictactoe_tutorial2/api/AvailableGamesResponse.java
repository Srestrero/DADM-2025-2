package com.example.androidtictactoe_tutorial2.api;

import java.util.List;

/**
 * Respuesta para listar juegos disponibles
 */
public class AvailableGamesResponse extends ApiResponse {
    private List<AvailableGame> available_games;

    public AvailableGamesResponse() {}

    public List<AvailableGame> getAvailable_games() {
        return available_games;
    }

    public void setAvailable_games(List<AvailableGame> available_games) {
        this.available_games = available_games;
    }
}
