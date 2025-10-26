package com.example.androidtictactoe_tutorial2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidtictactoe_tutorial2.api.AvailableGame;
import com.example.androidtictactoe_tutorial2.api.AvailableGamesResponse;
import com.example.androidtictactoe_tutorial2.api.CreateGameResponse;
import com.example.androidtictactoe_tutorial2.api.GameSessionManager;
import com.example.androidtictactoe_tutorial2.api.JoinGameResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Actividad que muestra la lista de juegos disponibles y permite crear o unirse a juegos
 */
public class GameListActivity extends AppCompatActivity {

    private RecyclerView recyclerGames;
    private TextView textNoGames;
    private TextView textServerInfo;
    private ProgressBar progressLoading;
    private Button buttonCreateGame;
    private Button buttonRefresh;

    private GameSessionManager sessionManager;
    private ExecutorService executorService;
    private GamesAdapter gamesAdapter;
    private List<AvailableGame> availableGames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        sessionManager = GameSessionManager.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        availableGames = new ArrayList<>();

        initializeViews();
        setupRecyclerView();
        setupListeners();
        updateServerInfo();
        loadAvailableGames();
    }

    private void initializeViews() {
        recyclerGames = findViewById(R.id.recycler_games);
        textNoGames = findViewById(R.id.text_no_games);
        textServerInfo = findViewById(R.id.text_server_info);
        progressLoading = findViewById(R.id.progress_loading);
        buttonCreateGame = findViewById(R.id.button_create_game);
        buttonRefresh = findViewById(R.id.button_refresh);
    }

    private void setupRecyclerView() {
        gamesAdapter = new GamesAdapter();
        recyclerGames.setLayoutManager(new LinearLayoutManager(this));
        recyclerGames.setAdapter(gamesAdapter);
    }

    private void setupListeners() {
        // Botón crear juego
        buttonCreateGame.setOnClickListener(v -> createNewGame());

        // Botón actualizar
        buttonRefresh.setOnClickListener(v -> loadAvailableGames());

        // Botón configuración
        findViewById(R.id.button_settings).setOnClickListener(v -> {
            Intent intent = new Intent(this, ServerConfigActivity.class);
            startActivity(intent);
        });
    }

    private void updateServerInfo() {
        textServerInfo.setText("Servidor: " + sessionManager.getServerUrl() +
                              " | Jugador: " + sessionManager.getPlayerName());
    }

    private void loadAvailableGames() {
        setLoading(true);

        executorService.execute(() -> {
            try {
                AvailableGamesResponse response = sessionManager.getApiClient().getAvailableGames();

                new Handler(Looper.getMainLooper()).post(() -> {
                    setLoading(false);

                    if (response.getDetail() != null && !response.getDetail().isEmpty()) {
                        Toast.makeText(this, "Error: " + response.getDetail(), Toast.LENGTH_LONG).show();
                        showNoGames(true);
                    } else {
                        availableGames.clear();
                        if (response.getAvailable_games() != null) {
                            availableGames.addAll(response.getAvailable_games());
                        }
                        gamesAdapter.notifyDataSetChanged();
                        showNoGames(availableGames.isEmpty());
                    }
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    setLoading(false);
                    Toast.makeText(this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    showNoGames(true);
                });
            }
        });
    }

    private void createNewGame() {
        buttonCreateGame.setEnabled(false);
        buttonCreateGame.setText("Creando...");

        executorService.execute(() -> {
            try {
                CreateGameResponse response = sessionManager.getApiClient().createGame(sessionManager.getPlayerId());

                new Handler(Looper.getMainLooper()).post(() -> {
                    buttonCreateGame.setEnabled(true);
                    buttonCreateGame.setText("Crear Juego");

                    if (response.getDetail() != null && !response.getDetail().isEmpty()) {
                        Toast.makeText(this, "Error: " + response.getDetail(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, response.getMensaje(), Toast.LENGTH_SHORT).show();

                        // Ir al juego online
                        Intent intent = new Intent(this, OnlineGameActivity.class);
                        intent.putExtra("GAME_ID", response.getGame_id());
                        intent.putExtra("PLAYER_SYMBOL", response.getJugador_simbolo());
                        startActivity(intent);
                    }
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    buttonCreateGame.setEnabled(true);
                    buttonCreateGame.setText("Crear Juego");
                    Toast.makeText(this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void joinGame(String gameId) {
        executorService.execute(() -> {
            try {
                JoinGameResponse response = sessionManager.getApiClient().joinGame(gameId, sessionManager.getPlayerId());

                new Handler(Looper.getMainLooper()).post(() -> {
                    if (response.getDetail() != null && !response.getDetail().isEmpty()) {
                        Toast.makeText(this, "Error: " + response.getDetail(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, response.getMensaje(), Toast.LENGTH_SHORT).show();

                        // Ir al juego online
                        Intent intent = new Intent(this, OnlineGameActivity.class);
                        intent.putExtra("GAME_ID", response.getGame_id());
                        intent.putExtra("PLAYER_SYMBOL", response.getJugador_simbolo());
                        startActivity(intent);
                    }
                });

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void setLoading(boolean loading) {
        progressLoading.setVisibility(loading ? View.VISIBLE : View.GONE);
        buttonCreateGame.setEnabled(!loading);
        buttonRefresh.setEnabled(!loading);
    }

    private void showNoGames(boolean show) {
        textNoGames.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerGames.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServerInfo();
        // Recargar juegos cuando se regresa a esta actividad
        loadAvailableGames();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * Adapter para la lista de juegos disponibles
     */
    private class GamesAdapter extends RecyclerView.Adapter<GameViewHolder> {

        @NonNull
        @Override
        public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_game, parent, false);
            return new GameViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
            AvailableGame game = availableGames.get(position);
            holder.bind(game);
        }

        @Override
        public int getItemCount() {
            return availableGames.size();
        }
    }

    /**
     * ViewHolder para los items de juegos
     */
    private class GameViewHolder extends RecyclerView.ViewHolder {
        private final TextView textGameId;
        private final TextView textCreator;
        private final TextView textStatus;
        private final Button buttonJoinGame;

        public GameViewHolder(@NonNull View itemView) {
            super(itemView);
            textGameId = itemView.findViewById(R.id.text_game_id);
            textCreator = itemView.findViewById(R.id.text_creator);
            textStatus = itemView.findViewById(R.id.text_status);
            buttonJoinGame = itemView.findViewById(R.id.button_join_game);
        }

        public void bind(AvailableGame game) {
            textGameId.setText("Juego #" + game.getGame_id().substring(0, Math.min(6, game.getGame_id().length())));
            textCreator.setText("Creado por: " + game.getJugador_x_id());
            textStatus.setText("Esperando jugador 2");

            buttonJoinGame.setOnClickListener(v -> joinGame(game.getGame_id()));
        }
    }
}
