package com.example.androidtictactoe_tutorial2.api;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Administrador de sesión para el juego online.
 * Maneja la configuración del servidor y IDs únicos de jugador.
 */
public class GameSessionManager {
    private static final String TAG = "GameSessionManager";
    private static final String PREFS_NAME = "triqui_online_prefs";

    // Keys para SharedPreferences
    private static final String KEY_SERVER_IP = "server_ip";
    private static final String KEY_SERVER_PORT = "server_port";
    private static final String KEY_PLAYER_ID = "player_id";
    private static final String KEY_PLAYER_NAME = "player_name";

    // Valores por defecto
    private static final String DEFAULT_SERVER_IP = "192.168.1.10";
    private static final int DEFAULT_SERVER_PORT = 8080;

    private static GameSessionManager instance;
    private final SharedPreferences preferences;
    private GameApiClient apiClient;

    private GameSessionManager(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Inicializar valores por defecto si no existen
        if (!preferences.contains(KEY_SERVER_IP)) {
            setServerIp(DEFAULT_SERVER_IP);
        }
        if (!preferences.contains(KEY_SERVER_PORT)) {
            setServerPort(DEFAULT_SERVER_PORT);
        }
        if (!preferences.contains(KEY_PLAYER_ID)) {
            setPlayerId(UUID.randomUUID().toString());
        }

        // Crear el cliente API con la configuración actual
        updateApiClient();
    }

    public static synchronized GameSessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new GameSessionManager(context);
        }
        return instance;
    }

    /**
     * Actualiza el cliente API con la configuración actual del servidor
     */
    private void updateApiClient() {
        String serverIp = getServerIp();
        int serverPort = getServerPort();
        apiClient = new GameApiClient(serverIp, serverPort);
    }

    // Getters y setters para configuración del servidor

    public String getServerIp() {
        return preferences.getString(KEY_SERVER_IP, DEFAULT_SERVER_IP);
    }

    public void setServerIp(String serverIp) {
        preferences.edit().putString(KEY_SERVER_IP, serverIp).apply();
        updateApiClient();
    }

    public int getServerPort() {
        return preferences.getInt(KEY_SERVER_PORT, DEFAULT_SERVER_PORT);
    }

    public void setServerPort(int serverPort) {
        preferences.edit().putInt(KEY_SERVER_PORT, serverPort).apply();
        updateApiClient();
    }

    // Getters y setters para información del jugador

    public String getPlayerId() {
        return preferences.getString(KEY_PLAYER_ID, "");
    }

    private void setPlayerId(String playerId) {
        preferences.edit().putString(KEY_PLAYER_ID, playerId).apply();
    }

    public String getPlayerName() {
        return preferences.getString(KEY_PLAYER_NAME, "Jugador");
    }

    public void setPlayerName(String playerName) {
        preferences.edit().putString(KEY_PLAYER_NAME, playerName).apply();
    }

    /**
     * Genera un nuevo ID único para el jugador
     */
    public void generateNewPlayerId() {
        setPlayerId(UUID.randomUUID().toString());
    }

    /**
     * Obtiene el cliente API configurado
     */
    public GameApiClient getApiClient() {
        return apiClient;
    }

    /**
     * Verifica si el servidor está accesible
     */
    public boolean isServerReachable() {
        return apiClient != null && apiClient.isServerHealthy();
    }

    /**
     * Obtiene la URL completa del servidor
     */
    public String getServerUrl() {
        return "http://" + getServerIp() + ":" + getServerPort();
    }

    /**
     * Reinicia la configuración a valores por defecto
     */
    public void resetToDefaults() {
        setServerIp(DEFAULT_SERVER_IP);
        setServerPort(DEFAULT_SERVER_PORT);
        generateNewPlayerId();
        setPlayerName("Jugador");
    }
}
