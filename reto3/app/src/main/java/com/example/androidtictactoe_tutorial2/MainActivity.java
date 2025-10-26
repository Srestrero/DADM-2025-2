package com.example.androidtictactoe_tutorial2;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    
    private Button[][] buttons = new Button[3][3];
    private boolean player1Turn = true;
    private int roundCount = 0;
    private TextView textViewPlayer;
    private String gameMode;
    private boolean isComputerTurn = false;
    private SharedPreferences preferences;
    private Random random;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        preferences = getSharedPreferences("TicTacToeStats", MODE_PRIVATE);
        random = new Random();
        
        // Obtener el modo de juego
        gameMode = getIntent().getStringExtra("GAME_MODE");
        if (gameMode == null) {
            gameMode = "TWO_PLAYERS";
        }
        
        textViewPlayer = findViewById(R.id.text_view_player);
        
        // Inicializar los botones
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String buttonID = "button_" + i + j;
                int resID = getResources().getIdentifier(buttonID, "id", getPackageName());
                buttons[i][j] = findViewById(resID);
                buttons[i][j].setOnClickListener(this::onButtonClick);
            }
        }
        
        Button buttonReset = findViewById(R.id.button_reset);
        buttonReset.setOnClickListener(v -> resetGame());
        
        Button buttonMenu = findViewById(R.id.button_menu);
        buttonMenu.setOnClickListener(v -> finish());
        
        // Selección aleatoria del primer jugador
        if (random.nextBoolean()) {
            player1Turn = true;
        } else {
            player1Turn = false;
        }
        
        updatePlayerText();
        
        // Si es modo contra máquina y la máquina empieza
        if (gameMode.equals("VS_COMPUTER") && !player1Turn) {
            isComputerTurn = true;
            makeComputerMove();
        }
    }
    
    public void onButtonClick(View view) {
        if (!((Button) view).getText().toString().equals("")) {
            return;
        }
        
        // En modo contra máquina, solo permitir movimientos del jugador humano
        if (gameMode.equals("VS_COMPUTER") && isComputerTurn) {
            return;
        }
        
        if (player1Turn) {
            ((Button) view).setText("X");
            ((Button) view).setTextColor(getResources().getColor(R.color.player1_color));
        } else {
            ((Button) view).setText("O");
            ((Button) view).setTextColor(getResources().getColor(R.color.player2_color));
        }
        
        roundCount++;
        
        if (checkForWin()) {
            if (player1Turn) {
                player1Wins();
            } else {
                player2Wins();
            }
        } else if (roundCount == 9) {
            draw();
        } else {
            player1Turn = !player1Turn;
            updatePlayerText();
            
            // En modo contra máquina, hacer que la máquina juegue
            if (gameMode.equals("VS_COMPUTER") && !player1Turn) {
                isComputerTurn = true;
                new Handler().postDelayed(this::makeComputerMove, 500); // Delay para mejor UX
            }
        }
    }
    
    private boolean checkForWin() {
        String[][] field = new String[3][3];
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }
        
        // Verificar filas
        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(field[i][1]) && field[i][0].equals(field[i][2]) && !field[i][0].equals("")) {
                return true;
            }
        }
        
        // Verificar columnas
        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(field[1][i]) && field[0][i].equals(field[2][i]) && !field[0][i].equals("")) {
                return true;
            }
        }
        
        // Verificar diagonales
        if (field[0][0].equals(field[1][1]) && field[0][0].equals(field[2][2]) && !field[0][0].equals("")) {
            return true;
        }
        
        if (field[0][2].equals(field[1][1]) && field[0][2].equals(field[2][0]) && !field[0][2].equals("")) {
            return true;
        }
        
        return false;
    }
    
    private void player1Wins() {
        String message;
        if (gameMode.equals("VS_COMPUTER")) {
            message = "¡Ganaste!";
            updateStats("vs_computer_wins");
        } else {
            message = "¡Jugador 1 (X) gana!";
            updateStats("two_player_wins_1");
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        textViewPlayer.setText(message);
        disableButtons();
    }
    
    private void player2Wins() {
        String message;
        if (gameMode.equals("VS_COMPUTER")) {
            message = "¡La máquina gana!";
            updateStats("vs_computer_losses");
        } else {
            message = "¡Jugador 2 (O) gana!";
            updateStats("two_player_wins_2");
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        textViewPlayer.setText(message);
        disableButtons();
    }
    
    private void draw() {
        Toast.makeText(this, "¡Empate!", Toast.LENGTH_SHORT).show();
        textViewPlayer.setText("¡Empate!");
        disableButtons();
    }
    
    private void updatePlayerText() {
        if (gameMode.equals("VS_COMPUTER")) {
            if (player1Turn) {
                textViewPlayer.setText("Tu turno (X)");
            } else {
                textViewPlayer.setText("Turno de la máquina (O)");
            }
        } else {
            if (player1Turn) {
                textViewPlayer.setText("Turno: Jugador 1 (X)");
            } else {
                textViewPlayer.setText("Turno: Jugador 2 (O)");
            }
        }
    }
    
    private void disableButtons() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }
    
    private void resetGame() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
            }
        }
        
        // Selección aleatoria del primer jugador
        player1Turn = random.nextBoolean();
        roundCount = 0;
        isComputerTurn = false;
        
        updatePlayerText();
        
        // Si es modo contra máquina y la máquina empieza
        if (gameMode.equals("VS_COMPUTER") && !player1Turn) {
            isComputerTurn = true;
            makeComputerMove();
        }
    }
    
    private void makeComputerMove() {
        int[] move = getBestMove();
        if (move[0] != -1 && move[1] != -1) {
            buttons[move[0]][move[1]].setText("O");
            buttons[move[0]][move[1]].setTextColor(getResources().getColor(R.color.player2_color));
            roundCount++;
            
            if (checkForWin()) {
                player2Wins();
            } else if (roundCount == 9) {
                draw();
            } else {
                player1Turn = true;
                isComputerTurn = false;
                updatePlayerText();
            }
        }
    }
    
    private int[] getBestMove() {
        // Estrategia simple de IA:
        // 1. Intentar ganar
        // 2. Bloquear al oponente
        // 3. Tomar el centro si está libre
        // 4. Tomar una esquina si está libre
        // 5. Tomar cualquier casilla libre
        
        String[][] field = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }
        
        // 1. Intentar ganar
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j].equals("")) {
                    field[i][j] = "O";
                    if (checkWinForPlayer(field, "O")) {
                        return new int[]{i, j};
                    }
                    field[i][j] = "";
                }
            }
        }
        
        // 2. Bloquear al oponente
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j].equals("")) {
                    field[i][j] = "X";
                    if (checkWinForPlayer(field, "X")) {
                        return new int[]{i, j};
                    }
                    field[i][j] = "";
                }
            }
        }
        
        // 3. Tomar el centro
        if (field[1][1].equals("")) {
            return new int[]{1, 1};
        }
        
        // 4. Tomar una esquina
        int[][] corners = {{0, 0}, {0, 2}, {2, 0}, {2, 2}};
        for (int[] corner : corners) {
            if (field[corner[0]][corner[1]].equals("")) {
                return corner;
            }
        }
        
        // 5. Tomar cualquier casilla libre
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j].equals("")) {
                    return new int[]{i, j};
                }
            }
        }
        
        return new int[]{-1, -1};
    }
    
    private boolean checkWinForPlayer(String[][] field, String player) {
        // Verificar filas
        for (int i = 0; i < 3; i++) {
            if (field[i][0].equals(player) && field[i][1].equals(player) && field[i][2].equals(player)) {
                return true;
            }
        }
        
        // Verificar columnas
        for (int i = 0; i < 3; i++) {
            if (field[0][i].equals(player) && field[1][i].equals(player) && field[2][i].equals(player)) {
                return true;
            }
        }
        
        // Verificar diagonales
        if (field[0][0].equals(player) && field[1][1].equals(player) && field[2][2].equals(player)) {
            return true;
        }
        
        if (field[0][2].equals(player) && field[1][1].equals(player) && field[2][0].equals(player)) {
            return true;
        }
        
        return false;
    }
    
    private void updateStats(String statKey) {
        SharedPreferences.Editor editor = preferences.edit();
        int currentValue = preferences.getInt(statKey, 0);
        editor.putInt(statKey, currentValue + 1);
        editor.apply();
    }
}
