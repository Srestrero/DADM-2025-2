package com.example.androidtictactoe_tutorial2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
    
    // The computer's difficulty levels
    public enum DifficultyLevel {Easy, Harder, Expert};
    
    // Current difficulty level
    private DifficultyLevel mDifficultyLevel = DifficultyLevel.Expert;
    
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
        
        // Setup menu permanente
        Button menuNewGame = findViewById(R.id.menu_new_game);
        Button menuDifficulty = findViewById(R.id.menu_difficulty);
        Button menuAbout = findViewById(R.id.menu_about);
        Button menuQuit = findViewById(R.id.menu_quit);
        
        menuNewGame.setOnClickListener(v -> startNewGame());
        menuDifficulty.setOnClickListener(v -> showDifficultyDialog());
        menuAbout.setOnClickListener(v -> showAboutDialog());
        menuQuit.setOnClickListener(v -> showQuitDialog());
        
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
    
    private void startNewGame() {
        resetGame();
    }
    
    private void showDifficultyDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.difficulty_choose);
        
        final CharSequence[] levels = {
            getResources().getString(R.string.difficulty_easy),
            getResources().getString(R.string.difficulty_harder),
            getResources().getString(R.string.difficulty_expert)};
        
        // Set selected based on current difficulty level
        int selected = 0;
        if (mDifficultyLevel == DifficultyLevel.Easy)
            selected = 0;
        else if (mDifficultyLevel == DifficultyLevel.Harder)
            selected = 1;
        else if (mDifficultyLevel == DifficultyLevel.Expert)
            selected = 2;
            
        builder.setSingleChoiceItems(levels, selected,
            new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    dialog.dismiss(); // Close dialog
                    // Set the difficulty level based on which item was selected
                    switch(item) {
                        case 0:
                            mDifficultyLevel = DifficultyLevel.Easy;
                            break;
                        case 1:
                            mDifficultyLevel = DifficultyLevel.Harder;
                            break;
                        case 2:
                            mDifficultyLevel = DifficultyLevel.Expert;
                            break;
                    }
                    // Display the selected difficulty level
                    Toast.makeText(getApplicationContext(), levels[item],
                        Toast.LENGTH_SHORT).show();
                }
            });
        builder.create().show();
    }
    
    private void showQuitDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.quit_confirm)
            .setCancelable(false)
            .setPositiveButton(R.string.quit_yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.this.finish();
                    }
                })
            .setNegativeButton(R.string.quit_no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        builder.create().show();
    }
    
    private void showAboutDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.about_title)
            .setMessage(R.string.about_message)
            .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
        builder.create().show();
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
        String[][] field = new String[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                field[i][j] = buttons[i][j].getText().toString();
            }
        }
        
        if (mDifficultyLevel == DifficultyLevel.Easy) {
            // Easy: Always make random moves
            return getRandomMove();
        } else if (mDifficultyLevel == DifficultyLevel.Harder) {
            // Harder: Try to win, otherwise random
            int[] winningMove = getWinningMove(field);
            if (winningMove[0] != -1) {
                return winningMove;
            }
            return getRandomMove();
        } else {
            // Expert: Try to win, block, then strategic moves
            // 1. Intentar ganar
            int[] winningMove = getWinningMove(field);
            if (winningMove[0] != -1) {
                return winningMove;
            }
            
            // 2. Bloquear al oponente
            int[] blockingMove = getBlockingMove(field);
            if (blockingMove[0] != -1) {
                return blockingMove;
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
            return getRandomMove();
        }
    }
    
    private int[] getRandomMove() {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().toString().equals("")) {
                    if (random.nextInt(9) == 0) { // Random chance to pick this spot
                        return new int[]{i, j};
                    }
                }
            }
        }
        // If no spot was randomly selected, pick first available
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (buttons[i][j].getText().toString().equals("")) {
                    return new int[]{i, j};
                }
            }
        }
        return new int[]{-1, -1};
    }
    
    private int[] getWinningMove(String[][] field) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j].equals("")) {
                    field[i][j] = "O";
                    if (checkWinForPlayer(field, "O")) {
                        field[i][j] = ""; // Reset
                        return new int[]{i, j};
                    }
                    field[i][j] = ""; // Reset
                }
            }
        }
        return new int[]{-1, -1};
    }
    
    private int[] getBlockingMove(String[][] field) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (field[i][j].equals("")) {
                    field[i][j] = "X";
                    if (checkWinForPlayer(field, "X")) {
                        field[i][j] = ""; // Reset
                        return new int[]{i, j};
                    }
                    field[i][j] = ""; // Reset
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
