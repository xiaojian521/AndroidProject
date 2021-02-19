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
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
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

import com.xj.demosets.util.PcmToWavUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRecordActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "xjtest";

    private Button mBtnRecord;
    private Button mBtnStop;
    private Button mBtnSetVolume;
    private TextView mTextView;

    private Context mContext;
    private AudioRecord mAudioRecord;

    private boolean isRecording;
    private String pcmfilepath = "/data/data/com.xj.demosets/music.pcm";
    private String wavfilepath = "/data/data/com.xj.demosets/music.wav";
    public static final int SAMPLE_RATE_INHZ = 44100;
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiorecord);

        mContext = getApplicationContext();
        initWidget();
    }

    public void initWidget() {
        mBtnRecord = (Button) findViewById(R.id.btn_record);
        mBtnRecord.setOnClickListener(this);
        mBtnStop = (Button) findViewById(R.id.btn_stop);
        mBtnStop.setOnClickListener(this);
        mBtnSetVolume = (Button) findViewById(R.id.btn_format);
        mBtnSetVolume.setOnClickListener(this);
        mTextView = (TextView)findViewById(R.id.textView);
        mTextView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_record:
                startRecord();
                break;
            case R.id.btn_stop:
                stopRecord();
                break;
            case R.id.btn_format:
                PcmToWav();
                break;
        }
    }


    public void startRecord() {
        final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
        Log.d(TAG,"startRecord() minBufferSize is = " + minBufferSize);
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ,
                CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);

        mAudioRecord.startRecording();

        int recordState = mAudioRecord.getRecordingState();
        if(recordState == AudioRecord.RECORDSTATE_STOPPED) {
            StringBuilder textstate = new StringBuilder();
            textstate.append("当前录音状态：RECORDSTATE_STOPPED. startRecording失败，请抢占录音焦点！");
            textstate.append("\n").append(mTextView.getText().toString());
            mTextView.setText(textstate);
            return;
        }

        isRecording = true;
        final byte data[] = new byte[minBufferSize];
        //final File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test_xj.pcm");
        final File file = new File(pcmfilepath);
        if (file.exists()) {
            file.mkdirs();
        }
        StringBuilder text = new StringBuilder();
        text.append("创建录音文件成功，文件路径：").append(pcmfilepath).append("开始录音").append("minBufferSize is = ").append(minBufferSize);
        text.append("\n").append(mTextView.getText().toString());
        mTextView.setText(text);

        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream os = null;
                try {
                    os = new FileOutputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (null != os) {
                    while (isRecording) {
                        int read = mAudioRecord.read(data, 0, minBufferSize);
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                            try {
                                os.write(data);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    try {
                        Log.i(TAG, "run: close file output stream !");
                        os.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void stopRecord() {
        isRecording = false;
        // 释放资源
        if (null != mAudioRecord) {
            mAudioRecord.stop();
            mAudioRecord.release();
            StringBuilder text = new StringBuilder();
            text.append("录音结束");
            text.append("\n").append(mTextView.getText().toString());
            mTextView.setText(text);
            mAudioRecord = null;
        }
    }

    private void PcmToWav() {
        PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
        File pcmFile = new File(pcmfilepath);
        File wavFile = new File(wavfilepath);
        if (wavFile.exists() || pcmFile.exists()) {
            wavFile.mkdirs();
            pcmFile.mkdirs();
        }
        pcmToWavUtil.pcmToWav(pcmFile.getAbsolutePath(), wavFile.getAbsolutePath());
        StringBuilder text = new StringBuilder();
        text.append("转换成功.");
        text.append("\n").append(mTextView.getText().toString());
        mTextView.setText(text);
    }

}
