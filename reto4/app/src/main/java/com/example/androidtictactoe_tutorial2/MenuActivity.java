package com.example.androidtictactoe_tutorial2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    
    private TextView textViewStats;
    private SharedPreferences preferences;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        
        preferences = getSharedPreferences("TicTacToeStats", MODE_PRIVATE);
        
        textViewStats = findViewById(R.id.text_view_stats);
        
        Button buttonTwoPlayers = findViewById(R.id.button_two_players);
        Button buttonVsComputer = findViewById(R.id.button_vs_computer);
        Button buttonResetStats = findViewById(R.id.button_reset_stats);
        
        buttonTwoPlayers.setOnClickListener(v -> startGame("TWO_PLAYERS"));
        buttonVsComputer.setOnClickListener(v -> startGame("VS_COMPUTER"));
        buttonResetStats.setOnClickListener(v -> resetStats());
        
        updateStats();
    }
    
    private void startGame(String gameMode) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("GAME_MODE", gameMode);
        startActivity(intent);
    }
    
    private void resetStats() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
        updateStats();
    }
    
    private void updateStats() {
        int twoPlayerWins1 = preferences.getInt("two_player_wins_1", 0);
        int twoPlayerWins2 = preferences.getInt("two_player_wins_2", 0);
        int vsComputerWins = preferences.getInt("vs_computer_wins", 0);
        int vsComputerLosses = preferences.getInt("vs_computer_losses", 0);
        
        String stats = "ESTADÍSTICAS:\n\n" +
                "Dos Jugadores:\n" +
                "Jugador 1: " + twoPlayerWins1 + " victorias\n" +
                "Jugador 2: " + twoPlayerWins2 + " victorias\n\n" +
                "Contra Máquina:\n" +
                "Tú: " + vsComputerWins + " victorias\n" +
                "Máquina: " + vsComputerLosses + " victorias";
        
        textViewStats.setText(stats);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateStats();
    }
}
