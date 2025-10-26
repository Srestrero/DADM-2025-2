package com.example.androidtictactoe_tutorial2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
    
    private BoardView boardView;
    private boolean player1Turn = true;
    private int roundCount = 0;
    private TextView textViewPlayer;
    private String gameMode;
    private boolean isComputerTurn = false;
    private SharedPreferences preferences;
    private Random random;
    private SoundManager soundManager;

    // Game state variables
    private boolean mGameOver = false;
    private int mHumanWins = 0;
    private int mComputerWins = 0;
    private int mTies = 0;
    private char mGoFirst = 'H'; // 'H' for human, 'C' for computer

    // UI elements for scores
    private TextView mHumanScoreTextView;
    private TextView mComputerScoreTextView;
    private TextView mTieScoreTextView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize basic components
        preferences = getSharedPreferences("ttt_prefs", MODE_PRIVATE);
        random = new Random();
        soundManager = new SoundManager(this);

        // Get game mode from intent
        gameMode = getIntent().getStringExtra("GAME_MODE");
        if (gameMode == null) {
            gameMode = "TWO_PLAYERS";
        }

        // Initialize views
        textViewPlayer = findViewById(R.id.text_view_player);
        boardView = findViewById(R.id.board_view);

        // Initialize score views
        mHumanScoreTextView = findViewById(R.id.human_score_text_view);
        mComputerScoreTextView = findViewById(R.id.computer_score_text_view);
        mTieScoreTextView = findViewById(R.id.tie_score_text_view);

        // Check if essential views exist
        if (boardView == null || textViewPlayer == null) {
            Toast.makeText(this, "Error: Essential views not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize basic game state
        player1Turn = true;
        roundCount = 0;
        mGameOver = false;

        // Set up basic UI
        updatePlayerText();
        displayScores();

        // Set up basic click listeners
        boardView.setOnCellClickListener(this::onCellClick);

        Button buttonReset = findViewById(R.id.button_reset);
        if (buttonReset != null) {
            buttonReset.setOnClickListener(v -> resetGame());
        }

        Button buttonMenu = findViewById(R.id.button_menu);
        if (buttonMenu != null) {
            buttonMenu.setOnClickListener(v -> finish());
        }

        // Set up menu buttons
        Button menuNewGame = findViewById(R.id.menu_new_game);
        if (menuNewGame != null) {
            menuNewGame.setOnClickListener(v -> resetGame());
        }

        Button menuDifficulty = findViewById(R.id.menu_difficulty);
        if (menuDifficulty != null) {
            menuDifficulty.setOnClickListener(v -> showDifficultyDialog());
        }

        Button menuAbout = findViewById(R.id.menu_about);
        if (menuAbout != null) {
            menuAbout.setOnClickListener(v -> showAboutDialog());
        }

        Button menuQuit = findViewById(R.id.menu_quit);
        if (menuQuit != null) {
            menuQuit.setOnClickListener(v -> showQuitDialog());
        }

        // Restore persistent scores and difficulty
        mHumanWins = preferences.getInt("mHumanWins", 0);
        mComputerWins = preferences.getInt("mComputerWins", 0);
        mTies = preferences.getInt("mTies", 0);
        int difficultyOrdinal = preferences.getInt("difficulty", DifficultyLevel.Expert.ordinal());
        mDifficultyLevel = DifficultyLevel.values()[difficultyOrdinal];

        // Check if there's a saved game to continue
        boolean hasSavedGame = preferences.getBoolean("saved_game_active", false);
        if (hasSavedGame && savedInstanceState == null) {
            showContinueGameDialog();
        }

        // Handle saved instance state (for orientation changes)
        if (savedInstanceState != null) {
            // Restore game state from saved instance
            boardView.setBoardState(savedInstanceState.getCharArray("board"));
            mGameOver = savedInstanceState.getBoolean("mGameOver");
            mHumanWins = savedInstanceState.getInt("mHumanWins");
            mComputerWins = savedInstanceState.getInt("mComputerWins");
            mTies = savedInstanceState.getInt("mTies");
            textViewPlayer.setText(savedInstanceState.getCharSequence("info"));
            mGoFirst = savedInstanceState.getChar("mGoFirst");
            player1Turn = savedInstanceState.getBoolean("player1Turn");
            roundCount = savedInstanceState.getInt("roundCount");
            isComputerTurn = savedInstanceState.getBoolean("isComputerTurn");
            gameMode = savedInstanceState.getString("gameMode");

            int difficultyIndex = savedInstanceState.getInt("difficulty");
            if (difficultyIndex >= 0 && difficultyIndex < DifficultyLevel.values().length) {
                mDifficultyLevel = DifficultyLevel.values()[difficultyIndex];
            } else {
                mDifficultyLevel = DifficultyLevel.Expert;
            }

            // Update UI with restored state
            updatePlayerText();
            displayScores();

            // If it's computer's turn to move after orientation change, make the move
            if (gameMode.equals("VS_COMPUTER") && isComputerTurn && !mGameOver) {
                new Handler().postDelayed(this::makeComputerMove, 500);
            }
        }
    }

    public void onCellClick(int row, int col) {
        if (!boardView.getCellValue(row, col).equals("")) {
            return;
        }
        
        // En modo contra máquina, solo permitir movimientos del jugador humano
        if (gameMode.equals("VS_COMPUTER") && isComputerTurn) {
            return;
        }
        
        if (player1Turn) {
            boardView.setCellValue(row, col, "X");
            soundManager.playMoveX();
        } else {
            boardView.setCellValue(row, col, "O");
            soundManager.playMoveO();
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
                field[i][j] = boardView.getCellValue(i, j);
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
            mHumanWins++;
            updateStats("vs_computer_wins");
            soundManager.playWin(); // Sonido de victoria
        } else {
            message = "¡Jugador 1 (X) gana!";
            updateStats("two_player_wins_1");
            soundManager.playWin(); // Sonido de victoria
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        textViewPlayer.setText(message);
        mGameOver = true;
        displayScores();
        disableButtons();

        // Clear any saved game since this one is finished
        SharedPreferences.Editor ed = preferences.edit();
        ed.putBoolean("saved_game_active", false);
        ed.apply();
    }
    
    private void player2Wins() {
        String message;
        if (gameMode.equals("VS_COMPUTER")) {
            message = "¡La máquina gana!";
            mComputerWins++;
            updateStats("vs_computer_losses");
            soundManager.playLose(); // Sonido de derrota
        } else {
            message = "¡Jugador 2 (O) gana!";
            updateStats("two_player_wins_2");
            soundManager.playWin(); // Sonido de victoria
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        textViewPlayer.setText(message);
        mGameOver = true;
        displayScores();
        disableButtons();

        // Clear any saved game since this one is finished
        SharedPreferences.Editor ed = preferences.edit();
        ed.putBoolean("saved_game_active", false);
        ed.apply();
    }
    
    private void draw() {
        Toast.makeText(this, "¡Empate!", Toast.LENGTH_SHORT).show();
        textViewPlayer.setText("¡Empate!");
        mTies++;
        soundManager.playDraw(); // Sonido de empate
        mGameOver = true;
        displayScores();
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
        // Disable the board by removing the click listener
        boardView.setOnCellClickListener(null);
    }
    
    private void resetGame() {
        boardView.clearBoard();
        boardView.setOnCellClickListener(this::onCellClick);

        // Reset game state variables
        mGameOver = false;
        roundCount = 0;
        isComputerTurn = false;

        // Selección aleatoria del primer jugador
        player1Turn = random.nextBoolean();
        mGoFirst = player1Turn ? 'H' : 'C';

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
            boardView.setCellValue(move[0], move[1], "O");
            soundManager.playMoveO(); // Sonido cuando la computadora mueve
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
                field[i][j] = boardView.getCellValue(i, j);
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
                if (boardView.getCellValue(i, j).equals("")) {
                    if (random.nextInt(9) == 0) { // Random chance to pick this spot
                        return new int[]{i, j};
                    }
                }
            }
        }
        // If no spot was randomly selected, pick first available
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (boardView.getCellValue(i, j).equals("")) {
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
    
    @Override
    protected void onStop() {
        super.onStop();
        // Save persistent data
        SharedPreferences.Editor ed = preferences.edit();
        ed.putInt("mHumanWins", mHumanWins);
        ed.putInt("mComputerWins", mComputerWins);
        ed.putInt("mTies", mTies);
        ed.putInt("difficulty", mDifficultyLevel.ordinal());

        // Save current game state if game is in progress
        if (!mGameOver && boardView != null) {
            ed.putString("saved_board", boardToString(boardView.getBoardState()));
            ed.putBoolean("saved_game_active", true);
            ed.putBoolean("saved_player1_turn", player1Turn);
            ed.putInt("saved_round_count", roundCount);
            ed.putBoolean("saved_is_computer_turn", isComputerTurn);
            ed.putString("saved_game_mode", gameMode);
            ed.putString("saved_player_text", textViewPlayer.getText().toString());
        } else {
            ed.putBoolean("saved_game_active", false);
        }

        ed.apply();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharArray("board", boardView.getBoardState());
        outState.putBoolean("mGameOver", mGameOver);
        outState.putInt("mHumanWins", mHumanWins);
        outState.putInt("mComputerWins", mComputerWins);
        outState.putInt("mTies", mTies);
        outState.putCharSequence("info", textViewPlayer.getText());
        outState.putChar("mGoFirst", mGoFirst);
        outState.putBoolean("player1Turn", player1Turn);
        outState.putInt("roundCount", roundCount);
        outState.putBoolean("isComputerTurn", isComputerTurn);
        outState.putString("gameMode", gameMode);
        outState.putInt("difficulty", mDifficultyLevel.ordinal());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (soundManager != null) {
            soundManager.release();
        }
    }

    private void displayScores() {
        if (mHumanScoreTextView != null) {
            mHumanScoreTextView.setText(Integer.toString(mHumanWins));
        }
        if (mComputerScoreTextView != null) {
            mComputerScoreTextView.setText(Integer.toString(mComputerWins));
        }
        if (mTieScoreTextView != null) {
            mTieScoreTextView.setText(Integer.toString(mTies));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.new_game) {
            startNewGame();
            return true;
        } else if (id == R.id.ai_difficulty) {
            showDifficultyDialog();
            return true;
        } else if (id == R.id.reset_scores) {
            resetScores();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void resetScores() {
        mHumanWins = 0;
        mComputerWins = 0;
        mTies = 0;
        displayScores();

        // Clear persistent scores
        SharedPreferences.Editor ed = preferences.edit();
        ed.putInt("mHumanWins", 0);
        ed.putInt("mComputerWins", 0);
        ed.putInt("mTies", 0);
        ed.apply();

        Toast.makeText(this, "Scores reset", Toast.LENGTH_SHORT).show();
    }

    private void showContinueGameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Juego Guardado")
                .setMessage("¿Quieres continuar el juego anterior?")
                .setPositiveButton("Continuar", (dialog, which) -> restoreSavedGame())
                .setNegativeButton("Nuevo Juego", (dialog, which) -> {
                    // Clear saved game
                    SharedPreferences.Editor ed = preferences.edit();
                    ed.putBoolean("saved_game_active", false);
                    ed.apply();
                    startNewGame();
                })
                .setCancelable(false)
                .show();
    }

    private void restoreSavedGame() {
        try {
            String savedBoard = preferences.getString("saved_board", "");
            if (!savedBoard.isEmpty()) {
                boardView.setBoardState(stringToBoard(savedBoard));
            }

            player1Turn = preferences.getBoolean("saved_player1_turn", true);
            roundCount = preferences.getInt("saved_round_count", 0);
            isComputerTurn = preferences.getBoolean("saved_is_computer_turn", false);
            gameMode = preferences.getString("saved_game_mode", "TWO_PLAYERS");
            String savedText = preferences.getString("saved_player_text", "");
            if (!savedText.isEmpty()) {
                textViewPlayer.setText(savedText);
            }

            mGameOver = false; // Game is being restored, so it's not over

            // Clear the saved game flag
            SharedPreferences.Editor ed = preferences.edit();
            ed.putBoolean("saved_game_active", false);
            ed.apply();

            // Update UI
            displayScores();

        } catch (Exception e) {
            // If restoration fails, start new game
            Toast.makeText(this, "Error al restaurar juego, empezando nuevo", Toast.LENGTH_SHORT).show();
            startNewGame();
        }
    }

    private String boardToString(char[] board) {
        StringBuilder sb = new StringBuilder();
        for (char c : board) {
            sb.append(c);
        }
        return sb.toString();
    }

    private char[] stringToBoard(String boardString) {
        char[] board = new char[9];
        for (int i = 0; i < Math.min(boardString.length(), 9); i++) {
            board[i] = boardString.charAt(i);
        }
        // Fill remaining with spaces if string is shorter
        for (int i = boardString.length(); i < 9; i++) {
            board[i] = ' ';
        }
        return board;
    }
}
