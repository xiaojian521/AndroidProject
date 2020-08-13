package com.android.motto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class  RecordActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "xjtest";

    private Context mContext;
    private Button btn_record;
    private Button btn_stopRecord;

    public AudioRecord mAudioRecord;
    private File mAudioRecordFile;
    private FileOutputStream mFileOutputStream;
    private byte[] mBuffer;
    private int minBufferSize;

    private volatile boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        mContext = getApplicationContext();
        btn_record = (Button)findViewById(R.id.button);
        btn_record.setOnClickListener(this);
        btn_stopRecord = (Button)findViewById(R.id.button2);
        btn_stopRecord.setOnClickListener(this);

        //获取录音最小缓存空间大小
        minBufferSize = AudioRecord.getMinBufferSize(16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        Log.d(TAG,"RecordActivity::onCreate() minBufferSize is = " + minBufferSize);
        mBuffer = new byte[minBufferSize];
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,16000,AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT,Math.max(320, minBufferSize));
        mAudioRecordFile = new File("/data/AudioRecord.pcm");
        if (!mAudioRecordFile.getParentFile().exists()) {
            mAudioRecordFile.getParentFile().mkdirs();
        }
        try {
            mAudioRecordFile.createNewFile();
            mFileOutputStream = new FileOutputStream(mAudioRecordFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button:
                DumpRecordData();
                break;
            case R.id.button2:
                StopDump();
                break;
        }

    }

    private void DumpRecordData() {
        mAudioRecord.startRecording();
        while(true) {
            int read = mAudioRecord.read(mBuffer, 0, minBufferSize);
            if(read<=0) {
                Log.d(TAG,"read is < 0");
            } else {
                try {
                    mFileOutputStream.write(mBuffer,0,read);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void StopDump() {
        mAudioRecord.stop();
    }
}