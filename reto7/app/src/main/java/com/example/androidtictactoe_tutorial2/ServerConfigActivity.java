package com.example.androidtictactoe_tutorial2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.androidtictactoe_tutorial2.api.GameSessionManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Actividad para configurar la conexión al servidor y la información del jugador
 */
public class ServerConfigActivity extends AppCompatActivity {

    private TextInputEditText editServerIp;
    private TextInputEditText editServerPort;
    private TextInputEditText editPlayerName;
    private TextView textPlayerId;
    private Button buttonTestConnection;
    private Button buttonSaveConfig;

    private GameSessionManager sessionManager;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_config);

        sessionManager = GameSessionManager.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        initializeViews();
        loadCurrentConfig();
        setupListeners();
    }

    private void initializeViews() {
        editServerIp = findViewById(R.id.edit_server_ip);
        editServerPort = findViewById(R.id.edit_server_port);
        editPlayerName = findViewById(R.id.edit_player_name);
        textPlayerId = findViewById(R.id.text_player_id);
        buttonTestConnection = findViewById(R.id.button_test_connection);
        buttonSaveConfig = findViewById(R.id.button_save_config);
    }

    private void loadCurrentConfig() {
        editServerIp.setText(sessionManager.getServerIp());
        editServerPort.setText(String.valueOf(sessionManager.getServerPort()));
        editPlayerName.setText(sessionManager.getPlayerName());
        updatePlayerIdDisplay();
    }

    private void updatePlayerIdDisplay() {
        textPlayerId.setText("ID del jugador: " + sessionManager.getPlayerId());
    }

    private void setupListeners() {
        // Botón configuración rápida emulador
        findViewById(R.id.button_config_emulator).setOnClickListener(v -> {
            editServerIp.setText("10.0.2.2");
            editServerPort.setText("8080");
            Toast.makeText(this, "Configuración para EMULADOR aplicada", Toast.LENGTH_SHORT).show();
        });

        // Botón configuración rápida LAN
        findViewById(R.id.button_config_local_lan).setOnClickListener(v -> {
            editServerIp.setText("192.168.1.10");
            editServerPort.setText("8080");
            Toast.makeText(this, "Configuración para RED LAN aplicada", Toast.LENGTH_SHORT).show();
        });

        // Botón probar conexión
        buttonTestConnection.setOnClickListener(v -> testConnection());

        // Botón generar nuevo ID
        findViewById(R.id.button_generate_new_id).setOnClickListener(v -> {
            sessionManager.generateNewPlayerId();
            updatePlayerIdDisplay();
            Toast.makeText(this, "Nuevo ID generado", Toast.LENGTH_SHORT).show();
        });

        // Botón guardar configuración
        buttonSaveConfig.setOnClickListener(v -> saveConfiguration());

        // Botón restaurar valores por defecto
        findViewById(R.id.button_reset_defaults).setOnClickListener(v -> {
            sessionManager.resetToDefaults();
            loadCurrentConfig();
            Toast.makeText(this, "Configuración restaurada a valores por defecto", Toast.LENGTH_SHORT).show();
        });
    }

    private void testConnection() {
        String serverIp = editServerIp.getText().toString().trim();
        String serverPortStr = editServerPort.getText().toString().trim();

        if (serverIp.isEmpty() || serverPortStr.isEmpty()) {
            Toast.makeText(this, "Por favor complete la IP y puerto del servidor", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int serverPort = Integer.parseInt(serverPortStr);

            // Actualizar temporalmente la configuración para la prueba
            sessionManager.setServerIp(serverIp);
            sessionManager.setServerPort(serverPort);

            buttonTestConnection.setEnabled(false);
            buttonTestConnection.setText("Probando...");

            executorService.execute(() -> {
                String testUrl = "http://" + serverIp + ":" + serverPort + "/health";
                boolean isReachable = sessionManager.isServerReachable();

                new Handler(Looper.getMainLooper()).post(() -> {
                    buttonTestConnection.setEnabled(true);
                    buttonTestConnection.setText("Probar Conexión");

                    if (isReachable) {
                        Toast.makeText(this, "✅ Conexión exitosa al servidor\n" + testUrl, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "❌ No se pudo conectar\n" + testUrl + "\n\nVerifica:\n• Servidor corriendo\n• IP y puerto correctos\n• Firewall desactivado", Toast.LENGTH_LONG).show();
                    }
                });
            });

        } catch (NumberFormatException e) {
            Toast.makeText(this, "El puerto debe ser un número válido", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveConfiguration() {
        String serverIp = editServerIp.getText().toString().trim();
        String serverPortStr = editServerPort.getText().toString().trim();
        String playerName = editPlayerName.getText().toString().trim();

        if (serverIp.isEmpty() || serverPortStr.isEmpty()) {
            Toast.makeText(this, "Por favor complete la IP y puerto del servidor", Toast.LENGTH_SHORT).show();
            return;
        }

        if (playerName.isEmpty()) {
            playerName = "Jugador";
        }

        try {
            int serverPort = Integer.parseInt(serverPortStr);

            // Guardar configuración
            sessionManager.setServerIp(serverIp);
            sessionManager.setServerPort(serverPort);
            sessionManager.setPlayerName(playerName);

            Toast.makeText(this, "Configuración guardada", Toast.LENGTH_SHORT).show();

            // Ir a la lista de juegos
            Intent intent = new Intent(this, GameListActivity.class);
            startActivity(intent);
            finish();

        } catch (NumberFormatException e) {
            Toast.makeText(this, "El puerto debe ser un número válido", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
