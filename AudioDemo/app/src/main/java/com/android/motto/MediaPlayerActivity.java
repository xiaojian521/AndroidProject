package com.android.motto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MediaPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "xjtest";

    private Context mContext;
    private Button btn_start;
    private Button btn_stop;

    private AudioFocusLisener mAudioFocusLisener;
    private AudioManager mAudioManager;

    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mediaplayer);

        mContext = getApplicationContext();
        btn_start = (Button)findViewById(R.id.button);
        btn_start.setOnClickListener(this);
        btn_stop = (Button)findViewById(R.id.button2);
        btn_stop.setOnClickListener(this);

        mAudioManager= (AudioManager)mContext.getSystemService(mContext.AUDIO_SERVICE);
        mAudioFocusLisener = new AudioFocusLisener();

        mMediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor fd = mContext.getAssets().openFd("daji_noon.wav");
            mMediaPlayer.setAudioStreamType(11);
            mMediaPlayer.setDataSource(fd.getFileDescriptor(),fd.getStartOffset(),fd.getLength());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.d(TAG,"mp is = " + mp + ", media player play complete");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                int resutl = mAudioManager.requestAudioFocus(mAudioFocusLisener,11,1);
                Log.d(TAG,"request result is = " + resutl);
                mMediaPlayer.start();
                break;
            case R.id.button2:
                mMediaPlayer.release();
                mMediaPlayer = null;
                rePrepare();
                mAudioManager.abandonAudioFocus(mAudioFocusLisener);
        }
    }

    private class AudioFocusLisener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int var1) {
            Log.d(TAG, "AudioFocusLisener::onAudioFocusChange result is = " + var1);
        }
    }

    private void rePrepare() {
        mMediaPlayer = new MediaPlayer();
        try {
            AssetFileDescriptor fd = mContext.getAssets().openFd("wy_hi.wav");
            mMediaPlayer.setDataSource(fd.getFileDescriptor(),fd.getStartOffset(),fd.getLength());
            mMediaPlayer.setAudioStreamType(11);
            mMediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}