package com.example.androidtictactoe_tutorial2.api;

/**
 * Respuesta para crear un nuevo juego
 */
public class CreateGameResponse extends ApiResponse {
    private String game_id;
    private String jugador_simbolo;
    private String mensaje;

    public CreateGameResponse() {}

    public String getGame_id() {
        return game_id;
    }

    public void setGame_id(String game_id) {
        this.game_id = game_id;
    }

    public String getJugador_simbolo() {
        return jugador_simbolo;
    }

    public void setJugador_simbolo(String jugador_simbolo) {
        this.jugador_simbolo = jugador_simbolo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
