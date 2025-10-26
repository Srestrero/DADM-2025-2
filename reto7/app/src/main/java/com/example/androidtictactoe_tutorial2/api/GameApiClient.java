package com.example.androidtictactoe_tutorial2.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Cliente para interactuar con la API REST del servidor de Triqui Online
 */
public class GameApiClient {
    private static final String TAG = "GameApiClient";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client;
    private final Gson gson;
    private final String baseUrl;

    public GameApiClient(String serverIp, int serverPort) {
        this.baseUrl = "http://" + serverIp + ":" + serverPort + "/api/v1";

        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .build();

        this.gson = new Gson();
    }

    /**
     * Crea un nuevo juego
     */
    public CreateGameResponse createGame(String playerId) throws IOException {
        CreateGameRequest request = new CreateGameRequest(playerId);
        String jsonRequest = gson.toJson(request);

        Request httpRequest = new Request.Builder()
                .url(baseUrl + "/games")
                .post(RequestBody.create(jsonRequest, JSON))
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body().string();
            Log.d(TAG, "Create game response: " + responseBody);

            if (response.isSuccessful()) {
                return gson.fromJson(responseBody, CreateGameResponse.class);
            } else {
                CreateGameResponse errorResponse = new CreateGameResponse();
                errorResponse.setDetail("Error: " + response.code() + " - " + responseBody);
                return errorResponse;
            }
        }
    }

    /**
     * Obtiene la lista de juegos disponibles
     */
    public AvailableGamesResponse getAvailableGames() throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/games/available")
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Log.d(TAG, "Available games response: " + responseBody);

            if (response.isSuccessful()) {
                Type responseType = new TypeToken<AvailableGamesResponse>(){}.getType();
                return gson.fromJson(responseBody, responseType);
            } else {
                AvailableGamesResponse errorResponse = new AvailableGamesResponse();
                errorResponse.setDetail("Error: " + response.code() + " - " + responseBody);
                return errorResponse;
            }
        }
    }

    /**
     * Se une a un juego existente
     */
    public JoinGameResponse joinGame(String gameId, String playerId) throws IOException {
        JoinGameRequest request = new JoinGameRequest(playerId);
        String jsonRequest = gson.toJson(request);

        Request httpRequest = new Request.Builder()
                .url(baseUrl + "/games/" + gameId + "/join")
                .put(RequestBody.create(jsonRequest, JSON))
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body().string();
            Log.d(TAG, "Join game response: " + responseBody);

            if (response.isSuccessful()) {
                return gson.fromJson(responseBody, JoinGameResponse.class);
            } else {
                JoinGameResponse errorResponse = new JoinGameResponse();
                errorResponse.setDetail("Error: " + response.code() + " - " + responseBody);
                return errorResponse;
            }
        }
    }

    /**
     * Realiza un movimiento en el juego
     */
    public MakeMoveResponse makeMove(String gameId, String playerId, int position) throws IOException {
        MakeMoveRequest request = new MakeMoveRequest(playerId, position);
        String jsonRequest = gson.toJson(request);

        Request httpRequest = new Request.Builder()
                .url(baseUrl + "/games/" + gameId + "/move")
                .post(RequestBody.create(jsonRequest, JSON))
                .build();

        try (Response response = client.newCall(httpRequest).execute()) {
            String responseBody = response.body().string();
            Log.d(TAG, "Make move response: " + responseBody);

            if (response.isSuccessful()) {
                Type responseType = new TypeToken<MakeMoveResponse>(){}.getType();
                return gson.fromJson(responseBody, responseType);
            } else {
                MakeMoveResponse errorResponse = new MakeMoveResponse();
                errorResponse.setDetail("Error: " + response.code() + " - " + responseBody);
                return errorResponse;
            }
        }
    }

    /**
     * Obtiene el estado actual del juego
     */
    public GameStateResponse getGameState(String gameId) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/games/" + gameId)
                .get()
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();
            Log.d(TAG, "Game state response: " + responseBody);

            if (response.isSuccessful()) {
                Type responseType = new TypeToken<GameStateResponse>(){}.getType();
                return gson.fromJson(responseBody, responseType);
            } else {
                GameStateResponse errorResponse = new GameStateResponse();
                errorResponse.setDetail("Error: " + response.code() + " - " + responseBody);
                return errorResponse;
            }
        }
    }

    /**
     * Verifica que el servidor esté funcionando (health check)
     */
    public boolean isServerHealthy() {
        try {
            // Extraer servidor IP y puerto del baseUrl
            String serverUrl = baseUrl.replace("/api/v1", "");
            String healthUrl = serverUrl + "/health";
            
            Log.d(TAG, "Testing connection to: " + healthUrl);
            
            Request request = new Request.Builder()
                    .url(healthUrl)
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                boolean isHealthy = response.isSuccessful();
                Log.d(TAG, "Health check result: " + isHealthy + " (code: " + response.code() + ")");
                return isHealthy;
            }
        } catch (Exception e) {
            Log.e(TAG, "Health check failed: " + e.getMessage(), e);
            return false;
        }
    }
}
