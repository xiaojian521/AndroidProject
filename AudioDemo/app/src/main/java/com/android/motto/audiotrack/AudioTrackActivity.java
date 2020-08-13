package com.android.motto.audiotrack;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.motto.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AudioTrackActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int MY_PERMISSIONS_REQUEST = 1001;
    private static final String TAG = "xjtest";

    private Button mBtnControl;
    private Button mBtnPlay;
    private Button mBtnstop;
    private Button mBtnpause;
    private Button mBtnflush;
    private Button mBtnstart;

    /**
     * 采样率，现在能够保证在所有设备上使用的采样率是44100Hz, 但是其他的采样率（22050, 16000, 11025）在一些设备上也可以使用。
     */
    public static final int SAMPLE_RATE_INHZ = 44100;

    /**
     * 声道数。CHANNEL_IN_MONO and CHANNEL_IN_STEREO. 其中CHANNEL_IN_MONO是可以保证在所有设备能够使用的。
     */
    public static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    /**
     * 返回的音频数据的格式。 ENCODING_PCM_8BIT, ENCODING_PCM_16BIT, and ENCODING_PCM_FLOAT.
     */
    public static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;



    /**
     * 需要申请的运行时权限
     */
    private String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    /**
     * 被用户拒绝的权限列表
     */
    private List<String> mPermissionList = new ArrayList<>();
    private boolean isRecording;
    private AudioRecord audioRecord;
    private Button mBtnConvert;
    private AudioTrack audioTrack;
    private byte[] audioData;
    private FileInputStream fileInputStream;
    private AudioManager mAudioManager;
    private Context mContext;
    private AudioFocusLisener myAudioFocusLisener;
    private static boolean isFirst = true;

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Object lock= new Object();
    private volatile boolean isQuit = false;
    private int m_minBufferSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audiotrack);
        mContext = getApplicationContext();
        mBtnControl = (Button) findViewById(R.id.btn_control);
        mBtnControl.setOnClickListener(this);
        mBtnConvert = (Button) findViewById(R.id.btn_convert);
        mBtnConvert.setOnClickListener(this);
        mBtnstop = (Button) findViewById(R.id.btn_stop);
        mBtnstop.setOnClickListener(this);
        mBtnpause = (Button) findViewById(R.id.btn_pause);
        mBtnpause.setOnClickListener(this);
        mBtnflush = (Button) findViewById(R.id.btn_flush);
        mBtnflush.setOnClickListener(this);
        mBtnstart = (Button) findViewById(R.id.btn_start);
        mBtnstart.setOnClickListener(this);

        mBtnPlay = (Button) findViewById(R.id.btn_play);
        mBtnPlay.setOnClickListener(this);
        mAudioManager= (AudioManager)mContext.getSystemService(mContext.AUDIO_SERVICE);
        mHandlerThread = new HandlerThread("AudioTracmThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        checkPermissions();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_control:
                Button button = (Button) view;
                if (button.getText().toString().equals(getString(R.string.start_record))) {
                    button.setText(getString(R.string.stop_record));
                    startRecord();
                } else {
                    button.setText(getString(R.string.start_record));
                    stopRecord();
                }
                break;
            case R.id.btn_convert:
                PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
                File pcmFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test_xj.pcm");
                File wavFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test_xj.wav");
                if (!wavFile.mkdirs()) {
                    Log.e(TAG, "wavFile Directory not created");
                }
                if (wavFile.exists()) {
                    wavFile.delete();
                }
                pcmToWavUtil.pcmToWav(pcmFile.getAbsolutePath(), wavFile.getAbsolutePath());

                break;
            case R.id.btn_play:
                isQuit = true;
                Button btn = (Button) view;
                String string = btn.getText().toString();
                if (string.equals(getString(R.string.start_play))) {
                    myAudioFocusLisener = new AudioFocusLisener();
                    int resutl = mAudioManager.requestAudioFocus(myAudioFocusLisener,409,1);
                    playInModeStream();
                    writeData();
                    //playInModeStatic();
                }
                break;
            case R.id.btn_stop:
                stopTrack();
                break;
            case R.id.btn_pause:
                pauseTrack();
                break;
            case R.id.btn_flush:
                flushTrack();
                break;
            case R.id.btn_start:
                playTrack();
                break;
            default:
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, permissions[i] + " 权限被用户禁止！");
                }
            }
            // 运行时权限的申请不是本demo的重点，所以不再做更多的处理，请同意权限申请。
        }
    }


    public void startRecord() {
        //获取最小缓存区大小
        final int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_INHZ, CHANNEL_CONFIG, AUDIO_FORMAT);
        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_INHZ,
                CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);

        final byte data[] = new byte[minBufferSize];
        final File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test_xj.pcm");
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created");
        }
        if (file.exists()) {
            file.delete();
        }

        audioRecord.startRecording();
        isRecording = true;

        // TODO: 2018/3/10 pcm数据无法直接播放，保存为WAV格式。

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
                        int read = audioRecord.read(data, 0, minBufferSize);
                        // 如果读取音频数据没有出现错误，就将数据写入到文件
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

    //
    public void stopRecord() {
        isRecording = false;
        // 释放资源
        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
            //recordingThread = null;
        }
    }


    private void checkPermissions() {
        // Marshmallow开始才用申请运行时权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (int i = 0; i < permissions.length; i++) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) !=
                        PackageManager.PERMISSION_GRANTED) {
                    mPermissionList.add(permissions[i]);
                }
            }
            if (!mPermissionList.isEmpty()) {
                String[] permissions = mPermissionList.toArray(new String[mPermissionList.size()]);
                ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);
            }
        }
    }


    /**
     * 播放，使用stream模式
     */
    @SuppressLint("Range")
    private void playInModeStream() {
        /*
         * SAMPLE_RATE_INHZ 对应pcm音频的采样率
         * channelConfig 对应pcm音频的声道
         * AUDIO_FORMAT 对应pcm音频的格式
         * */

        int channelConfig = AudioFormat.CHANNEL_OUT_MONO;
        final int minBufferSize = AudioTrack.getMinBufferSize(16000, channelConfig, AUDIO_FORMAT);
        m_minBufferSize = minBufferSize + 20;

        audioTrack = new AudioTrack.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setLegacyStreamType(409)
                        .build())
                .setAudioFormat(new AudioFormat.Builder().setSampleRate(16000)
                        .setEncoding(AUDIO_FORMAT)
                        .setChannelMask(channelConfig)
                        .build())
                .setBufferSizeInBytes(minBufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .setSessionId(AudioManager.AUDIO_SESSION_ID_GENERATE)
        .build();
//        audioTrack = new AudioTrack(
//                new AudioAttributes.Builder()
//                        .setLegacyStreamType(11)
//                        .build(),
//                new AudioFormat.Builder().setSampleRate(16000)
//                        .setEncoding(AUDIO_FORMAT)
//                        .setChannelMask(channelConfig)
//                        .build(),
//                minBufferSize,
//                AudioTrack.MODE_STREAM,
//                AudioManager.AUDIO_SESSION_ID_GENERATE);
        audioTrack.play();
    }

    private void writeData() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG,"AudioTrackActivity::writeData()");
                try {
//                    File file = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test_xj.pcm");
                    AssetFileDescriptor fd = mContext.getAssets().openFd("test_xj.pcm");
                    try {
//                        fileInputStream = new FileInputStream(file);
                        fileInputStream = fd.createInputStream();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    byte[] tempBuffer = new byte[m_minBufferSize];
                    while (fileInputStream.available() > 0) {
                        synchronized (lock) {
                            if (isQuit) {
                                int readCount = fileInputStream.read(tempBuffer);
                                if (readCount == AudioTrack.ERROR_INVALID_OPERATION ||
                                        readCount == AudioTrack.ERROR_BAD_VALUE) {
                                    continue;
                                }
                                if (readCount != 0 && readCount != -1) {
                                    audioTrack.write(tempBuffer, 0, readCount);
                                }
                            } else {
                                fileInputStream.close();
                                return;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 播放，使用static模式
     */
    private void playInModeStatic() {
        // static模式，需要将音频数据一次性write到AudioTrack的内部缓冲区

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    InputStream in = getResources().openRawResource(R.raw.ding);
                    try {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        for (int b; (b = in.read()) != -1; ) {
                            out.write(b);
                        }
                        Log.d(TAG, "Got the data");
                        audioData = out.toByteArray();
                    } finally {
                        in.close();
                    }
                } catch (IOException e) {
                    Log.wtf(TAG, "Failed to read", e);
                }
                return null;
            }


            @Override
            protected void onPostExecute(Void v) {
                Log.i(TAG, "Creating track...audioData.length = " + audioData.length);

                // R.raw.ding铃声文件的相关属性为 22050Hz, 8-bit, Mono
                audioTrack = new AudioTrack(
                        new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                                .build(),
                        new AudioFormat.Builder().setSampleRate(22050)
                                .setEncoding(AudioFormat.ENCODING_PCM_8BIT)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build(),
                        audioData.length,
                        AudioTrack.MODE_STATIC,
                        AudioManager.AUDIO_SESSION_ID_GENERATE);
                Log.d(TAG, "Writing audio data...");
                audioTrack.write(audioData, 0, audioData.length);
                Log.d(TAG, "Starting playback");
                audioTrack.play();
                Log.d(TAG, "Playing");
            }

        }.execute();

    }

    /**
     * 停止播放
     */
    private void stopPlay() {
        synchronized (lock) {
            if (audioTrack != null) {
                audioTrack.stop();
                audioTrack.release();
            }
        }
    }

    //停止播放
    private void stopTrack() {
        synchronized (lock) {
            isQuit = false;
            if(audioTrack != null) {
                audioTrack.stop();
                audioTrack.flush();
                audioTrack.release();
            }
        }
    }

    private void pauseTrack() {
        synchronized (lock) {
            isQuit = false;
            if(audioTrack != null) {
                Log.e(TAG,"AudioTrackActivity::pauseTrack()");
                audioTrack.pause();
                audioTrack.flush();
            }
        }
    }

    private void flushTrack() {
        synchronized (lock) {
            isQuit = false;
            if(audioTrack != null) {
                Log.e(TAG,"AudioTrackActivity::flushTrack()");
                audioTrack.flush();
            }
        }
    }

    private void playTrack() {
        synchronized (lock) {
            isQuit = true;
            if(audioTrack != null) {
                Log.e(TAG,"AudioTrackActivity::flushTrack()");
                audioTrack.play();
            }
        }
        writeData();
    }

    private class AudioFocusLisener implements AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int var1) {
            Log.e(TAG, "AudioFocusLisener::onAudioFocusChange result is = " + var1);
        }
    }
}
