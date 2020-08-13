package com.android.motto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

/**
 * 如果在Assets目录添加.pcm文件需要在build.gradle中添加如下字段.表示不让appt压缩该后缀的文件
 *     aaptOptions {
 *         noCompress "pcm"  //表示不让aapt压缩的文件后缀
 *     }
 */

/**
 * SoundPool默认是AUDIO_OUTPUT_FLAG_FAST属性的直接走FastMixer线程
 * 如果播放文件的采样率和硬件默认采样率不同.则不会直接走FastMixer线程,会先走MixerThread进行重采样
 */

public class  SoundPoolActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "xjtest";

    private Context mContext;
    private Button btn_reqFocus;
    private Button btn_abandFocus;

    private SoundPool mSoundPool;
    private int sourceid;

    private AudioManager mAudioManager;
    private AudioFocusLisener mAudioFocusLisener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soundpool);

        mContext = getApplicationContext();
        mAudioManager= (AudioManager)mContext.getSystemService(mContext.AUDIO_SERVICE);
        mAudioFocusLisener = new AudioFocusLisener();

        SoundPool.Builder builder = new SoundPool.Builder();
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(11);
        builder.setAudioAttributes(attrBuilder.build());
        mSoundPool= builder.build();
        try {
            AssetFileDescriptor fd = mContext.getAssets().openFd("wy_hi.wav");
            sourceid = mSoundPool.load(fd,0);
            Log.d(TAG,"sourceid is = "  + sourceid);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if ( sourceid == sampleId && status == 0) {
                    Log.d(TAG,"setOnLoadCompleteListener sampleId is =" + sampleId + ", status is = " + status);
                }
            }
        });

        btn_reqFocus = (Button)findViewById(R.id.button);
        btn_reqFocus.setOnClickListener(this);
        btn_abandFocus = (Button)findViewById(R.id.button2);
        btn_abandFocus.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                int resutl = mAudioManager.requestAudioFocus(mAudioFocusLisener,11,1);
                Log.d(TAG,"request result is = " + resutl);
                mSoundPool.play(sourceid, 1, 1, 1, 0, 1);
                break;
            case R.id.button2:
                mAudioManager.abandonAudioFocus(mAudioFocusLisener);
                break;
        }
    }

    private class AudioFocusLisener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int var1) {
            Log.d(TAG, "AudioFocusLisener::onAudioFocusChange result is = " + var1);
        }
    }
}