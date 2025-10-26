package com.example.androidtictactoe_tutorial2;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidtictactoe_tutorial2.api.GameSessionManager;
import com.example.androidtictactoe_tutorial2.api.GameStateResponse;
import com.example.androidtictactoe_tutorial2.api.MakeMoveResponse;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Actividad para jugar Triqui online usando la API REST
 */
public class OnlineGameActivity extends AppCompatActivity {

    private BoardView boardView;
    private TextView textViewPlayer;
    private TextView textGameInfo;

    private GameSessionManager sessionManager;
    private ExecutorService executorService;
    private Handler mainHandler;

    private String gameId;
    private String playerSymbol;
    private String currentTurn;
    private boolean gameFinished = false;
    private boolean isPolling = false;

    // Polling configuration
    private static final int POLLING_INTERVAL = 2000; // 2 seconds
    private final Handler pollingHandler = new Handler(Looper.getMainLooper());
    private final Runnable pollingRunnable = new Runnable() {
        @Override
        public void run() {
            if (!gameFinished && !isFinishing()) {
                pollGameState();
                pollingHandler.postDelayed(this, POLLING_INTERVAL);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Reutilizar el mismo layout

        sessionManager = GameSessionManager.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        // Obtener datos del intent
        gameId = getIntent().getStringExtra("GAME_ID");
        playerSymbol = getIntent().getStringExtra("PLAYER_SYMBOL");

        if (gameId == null || playerSymbol == null) {
            Toast.makeText(this, "Error: Datos del juego no disponibles", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupGame();
        startPolling();
    }

    private void initializeViews() {
        boardView = findViewById(R.id.board_view);
        textViewPlayer = findViewById(R.id.text_view_player);
        textGameInfo = findViewById(R.id.text_view_player); // Reutilizar el mismo TextView

        // Configurar click listener del tablero
        boardView.setOnCellClickListener(this::onCellClick);

        // Configurar botones
        Button buttonReset = findViewById(R.id.button_reset);
        if (buttonReset != null) {
            buttonReset.setVisibility(View.GONE); // Ocultar botón reset en modo online
        }

        Button buttonMenu = findViewById(R.id.button_menu);
        if (buttonMenu != null) {
            buttonMenu.setOnClickListener(v -> finish()); // Volver a la lista de juegos
        }

        // Ocultar botones de menú que no aplican
        hideMenuButtons();
    }

    private void hideMenuButtons() {
        View menuNewGame = findViewById(R.id.menu_new_game);
        if (menuNewGame != null) menuNewGame.setVisibility(View.GONE);

        View menuDifficulty = findViewById(R.id.menu_difficulty);
        if (menuDifficulty != null) menuDifficulty.setVisibility(View.GONE);

        View menuAbout = findViewById(R.id.menu_about);
        if (menuAbout != null) menuAbout.setVisibility(View.GONE);

        View menuQuit = findViewById(R.id.menu_quit);
        if (menuQuit != null) menuQuit.setVisibility(View.GONE);
    }

    private void setupGame() {
        updateGameInfo();
        // El estado inicial se actualizará con el primer polling
    }

    private void updateGameInfo() {
        String info = "Juego: " + gameId.substring(0, Math.min(8, gameId.length())) +
                     " | Tú eres: " + playerSymbol;
        textViewPlayer.setText(info);
    }

    public void onCellClick(int row, int col) {
        if (gameFinished) {
            return;
        }

        // Verificar que sea nuestro turno
        if (!playerSymbol.equals(currentTurn)) {
            Toast.makeText(this, "No es tu turno", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar que la celda esté vacía
        if (!boardView.getCellValue(row, col).equals("")) {
            return;
        }

        // Calcular posición (0-8)
        int position = row * 3 + col;

        // Realizar movimiento
        makeMove(position);
    }

    private void makeMove(int position) {
        boardView.setOnCellClickListener(null); // Deshabilitar clicks durante la petición

        executorService.execute(() -> {
            try {
                MakeMoveResponse response = sessionManager.getApiClient().makeMove(gameId, sessionManager.getPlayerId(), position);

                mainHandler.post(() -> {
                    boardView.setOnCellClickListener(this::onCellClick); // Rehabilitar clicks

                    if (response.getDetail() != null && !response.getDetail().isEmpty()) {
                        Toast.makeText(this, "Error: " + response.getDetail(), Toast.LENGTH_LONG).show();
                    } else {
                        // Actualizar tablero con la respuesta del servidor
                        updateBoardFromServer(response.getTablero_nuevo());
                        currentTurn = response.getTurno_siguiente();

                        // Verificar si el juego terminó
                        if (response.getResultado_final() != null && !response.getResultado_final().equals("N/A")) {
                            handleGameEnd(response.getResultado_final(), response.getMensaje());
                        } else {
                            updateTurnDisplay();
                        }
                    }
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    boardView.setOnCellClickListener(this::onCellClick);
                    Toast.makeText(this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void pollGameState() {
        if (isPolling) return;
        isPolling = true;

        executorService.execute(() -> {
            try {
                GameStateResponse response = sessionManager.getApiClient().getGameState(gameId);

                mainHandler.post(() -> {
                    isPolling = false;

                    if (response.getDetail() != null && !response.getDetail().isEmpty()) {
                        // Error al obtener estado - continuar intentando
                        return;
                    }

                    // Actualizar tablero
                    updateBoardFromServer(response.getTablero());

                    // Actualizar turno actual
                    currentTurn = response.getTurno_actual();

                    // Verificar estado del juego
                    String estado = response.getEstado();
                    if ("Terminado".equals(estado)) {
                        handleGameEnd(response.getResultado(), "Juego terminado");
                    } else if ("En Progreso".equals(estado)) {
                        updateTurnDisplay();
                    }
                });

            } catch (Exception e) {
                mainHandler.post(() -> {
                    isPolling = false;
                    // Error de conexión - continuar intentando en el siguiente polling
                });
            }
        });
    }

    private void updateBoardFromServer(List<String> serverBoard) {
        if (serverBoard == null || serverBoard.size() != 9) return;

        for (int i = 0; i < 9; i++) {
            int row = i / 3;
            int col = i % 3;
            String serverValue = serverBoard.get(i);
            String currentValue = boardView.getCellValue(row, col);

            // Solo actualizar si el valor cambió
            if (!serverValue.equals(currentValue)) {
                boardView.setCellValue(row, col, serverValue);
            }
        }
    }

    private void updateTurnDisplay() {
        String turnText;
        if (playerSymbol.equals(currentTurn)) {
            turnText = "Tu turno (" + playerSymbol + ")";
        } else {
            turnText = "Turno del oponente (" + currentTurn + ")";
        }

        textViewPlayer.setText(turnText);
    }

    private void handleGameEnd(String resultado, String mensaje) {
        gameFinished = true;
        stopPolling();

        String endMessage;
        if ("X Gana".equals(resultado) && "X".equals(playerSymbol)) {
            endMessage = "¡Ganaste!";
        } else if ("O Gana".equals(resultado) && "O".equals(playerSymbol)) {
            endMessage = "¡Ganaste!";
        } else if ("Empate".equals(resultado)) {
            endMessage = "¡Empate!";
        } else {
            endMessage = "Perdiste";
        }

        textViewPlayer.setText(endMessage);
        Toast.makeText(this, mensaje != null ? mensaje : endMessage, Toast.LENGTH_LONG).show();

        // Deshabilitar el tablero
        boardView.setOnCellClickListener(null);
    }

    private void startPolling() {
        pollingHandler.post(pollingRunnable);
    }

    private void stopPolling() {
        pollingHandler.removeCallbacks(pollingRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!gameFinished) {
            startPolling();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPolling();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPolling();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
