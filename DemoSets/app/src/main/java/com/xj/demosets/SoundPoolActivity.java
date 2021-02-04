package com.xj.demosets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SoundPoolActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "xjtest";

    private Button mBtnPlay;
    private Button mBtnStop;
    private Button mBtnReqFocus;
    private Button mBtnAbadFocus;
    private Button mBtnSetVolume;
    private EditText mEditText;
    private TextView mTextView;

    private Context mContext;
    private SoundPool mSoundPool;
    private AudioManager mAudioManager;

    private int playid;
    private int sourceid;
    private String ediTxt = "-1";
    public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    public static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    public static final String EXTRA_VOLUME_STREAM_VALUE = "android.media.EXTRA_VOLUME_STREAM_VALUE";
    public static final String EXTRA_PREV_VOLUME_STREAM_VALUE = "android.media.EXTRA_PREV_VOLUME_STREAM_VALUE";
    public static final String EXTRA_VOLUME_STREAM_TYPE_ALIAS = "android.media.EXTRA_VOLUME_STREAM_TYPE_ALIAS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiotrack);

        initWidget();

        mContext = getApplicationContext();
        mAudioManager = (AudioManager)mContext.getSystemService(mContext.AUDIO_SERVICE);

        initSoundPool();

        registerVolumeChangeReceiver();
    }

    public void initWidget() {
        mBtnPlay = (Button) findViewById(R.id.btn_play);
        mBtnPlay.setOnClickListener(this);
        mBtnStop = (Button) findViewById(R.id.btn_stop);
        mBtnStop.setOnClickListener(this);
        mBtnReqFocus = (Button) findViewById(R.id.btn_reqfocus);
        mBtnReqFocus.setOnClickListener(this);
        mBtnAbadFocus = (Button) findViewById(R.id.btn_abdfocus);
        mBtnAbadFocus.setOnClickListener(this);
        mBtnSetVolume = (Button) findViewById(R.id.btn_setVolume);
        mBtnSetVolume.setOnClickListener(this);
        mEditText= (EditText)findViewById(R.id.editText);
        mEditText.setOnEditorActionListener(new EditorActionListener());
        mTextView = (TextView)findViewById(R.id.textView);
        mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    private void initSoundPool() {
        SoundPool.Builder builder = new SoundPool.Builder();
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        builder.setAudioAttributes(attrBuilder.build());
        mSoundPool= builder.build();
        try {
            AssetFileDescriptor fd = mContext.getAssets().openFd("wy_hi.wav");
            sourceid = mSoundPool.load(fd,0);
            StringBuilder text = new StringBuilder();
            text.append("音频文件加载的sourceID ：").append(sourceid);
            text.append("\n").append(mTextView.getText().toString());
            mTextView.setText(text);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if ( sourceid == sampleId && status == 0) {
                    StringBuilder text = new StringBuilder();
                    text.append("音频文件加载完成");
                    text.append("\n").append(mTextView.getText().toString());
                    mTextView.setText(text);
                }
            }
        });

    }



    public void registerVolumeChangeReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(VOLUME_CHANGED_ACTION);
        mContext.registerReceiver(new VolumeBroadcastReceiver(), filter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_reqfocus:
                requestAudioFocus();
                break;
            case R.id.btn_abdfocus:
                abandonAudioFocus();
                break;
            case R.id.btn_play:
                startPlay();
                break;
            case R.id.btn_stop:
                stopTrack();
                break;
            case R.id.btn_setVolume:
                setVolume();
                break;
        }
    }

    private void requestAudioFocus() {
        final AudioFocusRequest afr = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setWillPauseWhenDucked(true)
                .setAcceptsDelayedFocusGain(true)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build())
                .setOnAudioFocusChangeListener(new AudioFocusLisener())
                .build();
        int ret = mAudioManager.requestAudioFocus(afr);
        StringBuilder text = new StringBuilder();
        text.append("申请音频焦点：");
        if (ret == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
            // TBD 焦点申请失败 不执行播放
            text.append("失败");
        } else if (ret == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            // TBD 焦点申请成功 执行播放
            text.append("成功");
        } else if (ret == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
            // TBD 焦点申请delay 不执行播放（这种时候一般电话中，我们播放音乐会有这种状态，如果ret是delay，那么如果可以播放的时候会收到对应AUDIOFOCUS_GAIN的callback）
            text.append("被延迟");
        }
        text.append("\n").append(mTextView.getText().toString());
        mTextView.setText(text);

    }

    private void abandonAudioFocus() {
        final AudioFocusRequest afr = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setWillPauseWhenDucked(true)
                .setAcceptsDelayedFocusGain(true)
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .build())
                .setOnAudioFocusChangeListener(new AudioFocusLisener())
                .build();
        mAudioManager.abandonAudioFocusRequest(afr);
        StringBuilder text = new StringBuilder();
        text.append("释放音频焦点：成功");
        text.append("\n").append(mTextView.getText().toString());
        mTextView.setText(text);

    }


    private void startPlay() {
        playid = mSoundPool.play(sourceid, 1, 1, 1, 0, 1);
        StringBuilder text = new StringBuilder();
        text.append("播放音频：成功");
        text.append("\n").append(mTextView.getText().toString());
        mTextView.setText(text);

    }

    private void stopTrack() {
        if(mSoundPool != null) {
            mSoundPool.stop(playid);
            mSoundPool = null;
            StringBuilder text = new StringBuilder();
            text.append("暂停音频：成功");
            text.append("\n").append(mTextView.getText().toString());
            mTextView.setText(text);
        }
    }

    private void setVolume() {
        StringBuilder text = new StringBuilder();
        text.append("当前音量：").append(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        text.append(", 最大音量：").append(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        text.append(", 最小音量：").append(mAudioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC));
        if(!ediTxt.equals("-1")) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,new Integer(ediTxt), AudioManager.FLAG_PLAY_SOUND);
            text.append(", 设置音量成功").append(", 设置后音量：").append(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        } else {
            text.append(", 设置音量失败");
        }
        text.append("\n").append(mTextView.getText().toString());
        mTextView.setText(text);
    }


    private class AudioFocusLisener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int var1) {
            StringBuilder text = new StringBuilder();
            text.append("当前音频焦点变化：").append(var1);
            text.append("\n").append(mTextView.getText().toString());
            mTextView.setText(text);
        }
    }

    private class EditorActionListener implements TextView.OnEditorActionListener{

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            //监听回车键
            if (actionId != EditorInfo.IME_ACTION_UNSPECIFIED) {
                ediTxt = mEditText.getText().toString();
                StringBuilder text = new StringBuilder();
                text.append("指定音量变化值：").append(ediTxt);
                text.append("\n").append(mTextView.getText().toString());
                mTextView.setText(text);
            }
            return false;
        }
    }

    public class VolumeBroadcastReceiver extends BroadcastReceiver {
        public VolumeBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (VOLUME_CHANGED_ACTION.equals(intent.getAction())) {
                if(intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_MUSIC
                        && intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE_ALIAS, -1) == AudioManager.STREAM_MUSIC) {
                    int oldvolume = intent.getIntExtra(EXTRA_PREV_VOLUME_STREAM_VALUE,-1);
                    int newvolume = intent.getIntExtra(EXTRA_VOLUME_STREAM_VALUE,-1);
                    int streamType = intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE,-1);
                    StringBuilder text = new StringBuilder();
                    text.append("收到音量变化广播 变化前音量：").append(oldvolume);
                    text.append(", 当前音量音量：").append(newvolume);
                    text.append(", STREAM_TYPE：").append(streamType);
                }
            }
        }
    }

}
