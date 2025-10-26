package com.example.androidtictactoe_tutorial2;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.SoundPool;

public class SoundManager {
    private static final int MAX_STREAMS = 5;
    private SoundPool soundPool;
    private boolean soundEnabled = true;
    
    // Sound IDs
    private int soundMoveX = -1;
    private int soundMoveO = -1;
    private int soundWin = -1;
    private int soundLose = -1;
    private int soundDraw = -1;
    
    public SoundManager(Context context) {
        createSoundPool();
        loadSounds(context);
    }
    
    private void createSoundPool() {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
                
        soundPool = new SoundPool.Builder()
                .setMaxStreams(MAX_STREAMS)
                .setAudioAttributes(audioAttributes)
                .build();
    }
    
    private void loadSounds(Context context) {
        try {
            soundMoveX = soundPool.load(context, R.raw.move_x, 1);
            soundMoveO = soundPool.load(context, R.raw.move_o, 1);
            soundWin = soundPool.load(context, R.raw.win_sound, 1);
            soundLose = soundPool.load(context, R.raw.lose_sound, 1);
            soundDraw = soundPool.load(context, R.raw.draw_sound, 1);
        } catch (Exception e) {
            e.printStackTrace();
            // Si no se pueden cargar los sonidos, usar sonidos sintéticos
            generateSyntheticSounds();
        }
    }
    
    private void generateSyntheticSounds() {
        // Fallback: crear sonidos sintéticos si no hay archivos
        soundEnabled = true; // Mantener habilitado para tonos sintéticos
    }
    
    public void playMoveX() {
        if (soundEnabled && soundPool != null) {
            if (soundMoveX != -1) {
                soundPool.play(soundMoveX, 1.0f, 1.0f, 1, 0, 1.0f);
            } else {
                playTone(800, 150); // Tono agudo para X
            }
        }
    }
    
    public void playMoveO() {
        if (soundEnabled && soundPool != null) {
            if (soundMoveO != -1) {
                soundPool.play(soundMoveO, 1.0f, 1.0f, 1, 0, 1.0f);
            } else {
                playTone(400, 150); // Tono grave para O
            }
        }
    }
    
    public void playWin() {
        if (soundEnabled && soundPool != null) {
            if (soundWin != -1) {
                soundPool.play(soundWin, 1.0f, 1.0f, 1, 0, 1.0f);
            } else {
                // Secuencia de victoria: do-mi-sol
                playToneSequence(new int[]{523, 659, 784}, new int[]{200, 200, 400});
            }
        }
    }
    
    public void playLose() {
        if (soundEnabled && soundPool != null) {
            if (soundLose != -1) {
                soundPool.play(soundLose, 1.0f, 1.0f, 1, 0, 1.0f);
            } else {
                // Secuencia de derrota: sol-mi-do (descendente)
                playToneSequence(new int[]{784, 659, 523}, new int[]{300, 300, 500});
            }
        }
    }
    
    public void playDraw() {
        if (soundEnabled && soundPool != null) {
            if (soundDraw != -1) {
                soundPool.play(soundDraw, 1.0f, 1.0f, 1, 0, 1.0f);
            } else {
                playTone(600, 800); // Tono neutro largo
            }
        }
    }
    
    private void playTone(int frequency, int duration) {
        Thread toneThread = new Thread(() -> {
            try {
                int sampleRate = 44100;
                int numSamples = duration * sampleRate / 1000;
                double[] sample = new double[numSamples];
                byte[] generatedSnd = new byte[2 * numSamples];
                
                for (int i = 0; i < numSamples; ++i) {
                    sample[i] = Math.sin(2 * Math.PI * i / (sampleRate / frequency));
                }
                
                int idx = 0;
                for (double dVal : sample) {
                    short val = (short) ((dVal * 32767));
                    generatedSnd[idx++] = (byte) (val & 0x00ff);
                    generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
                }
                
                AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRate, 
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, 
                    generatedSnd.length,
                    AudioTrack.MODE_STATIC
                );
                
                audioTrack.write(generatedSnd, 0, generatedSnd.length);
                audioTrack.play();
                
                Thread.sleep(duration);
                audioTrack.stop();
                audioTrack.release();
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        toneThread.start();
    }
    
    private void playToneSequence(int[] frequencies, int[] durations) {
        Thread sequenceThread = new Thread(() -> {
            try {
                for (int i = 0; i < frequencies.length && i < durations.length; i++) {
                    playTone(frequencies[i], durations[i]);
                    Thread.sleep(durations[i] + 50); // Pequeña pausa entre tonos
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        sequenceThread.start();
    }
    
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
