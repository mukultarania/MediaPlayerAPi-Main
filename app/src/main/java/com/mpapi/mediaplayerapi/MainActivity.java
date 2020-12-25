package com.mpapi.mediaplayerapi;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import static androidx.core.content.ContextCompat.getSystemService;

public class MainActivity extends AppCompatActivity {

    private Button play, pause, stop;
    private MediaPlayer mediaPlayer;
    private AudioManager audioManager;
    AudioManager.OnAudioFocusChangeListener afChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        // Permanent loss of audio focus
                        // Pause playback immediately
                        releaseMedia();
                    }
                    else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        // Pause playback
                        mediaPlayer.pause();
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // Your app has been granted audio focus again
                        // Raise volume to normal, restart playback if necessary
                        mediaPlayer.start();
                    }
                }
            };

    //OnComplete Listener work when song ends
    private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMedia();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        play = findViewById(R.id.play);
        stop = findViewById(R.id.stop);
        pause = findViewById(R.id.pause);

        //Audio Manager Get service
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //Releasing Media Player
        releaseMedia();
        mediaPlayer = MediaPlayer.create(MainActivity.this, R.raw.harmanem);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int result = audioManager.requestAudioFocus(afChangeListener,
                        // Use the music stream.
                        AudioManager.STREAM_MUSIC,
                        // Request permanent focus.
                        AudioManager.AUDIOFOCUS_GAIN);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    // Start playback
                    //Taking Audio Focus Gain
                    mediaPlayer.start();
                }

            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.reset();
            }
        });

        mediaPlayer.setOnCompletionListener(onCompletionListener);
    }

    @Override
    protected void onDestroy() {
        releaseMedia();
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMedia();
    }

    //Cleaning Media Player Resources
    private void releaseMedia(){
        if(mediaPlayer != null){
        mediaPlayer.release();
        mediaPlayer = null;

        //Abandon Audio Focus
        audioManager.abandonAudioFocus(afChangeListener);
        }
    }
}