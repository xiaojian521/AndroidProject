package vr.suntec.net.vrapp.vrService.audioManager;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.AudioFormat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.os.Environment;


public class VrAudioRecord implements VrAudioRecordInterface {
    //-------------------参数设置-------------------//
    //输入设备(MIC)
    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    //private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    //采样频率(16K)
    private static int AUDIO_SAMPLE_RATE = 16000;
    //声道(2)
    private static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
    //编码(16)
    private static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    //-------------------参数设置-------------------//

    //录音对象(AudioRecord)
    public AudioRecord mAudioRecord;
    //锁
    private Lock mlock = new ReentrantLock();

    //缓冲区
    private byte[] mBuffer;
    //缓冲区字节大小(16/8*16000/1000*10(ms) = 320)两路640
    private int bufferSizeInByte = 640;
    //缓冲区字节大小
    private int minBufferSize = 0;
    //录音状态
    private boolean mIsRecording = true;
    //是否送出
    //private boolean m_writeToOutSide = false;
    //文件名
    //private String fileName;
    private FileOutputStream mFileOutputStream;
    private File mAudioRecordFile;
    //参考音
    private FileOutputStream mFileOutputStreamRef;
    private File mAudioRecordFileRef;
    //record+ref
    private FileOutputStream mFileOutputStreamAll;
    private File mAudioRecordFileAll;
    //线程池
    private ExecutorService mExecutorService;
    //gloable instance
    private static VrAudioRecord m_instance = null;
    //m_audioswitch true is sphinx , false is baiduengine
    private static String m_audioswitch;
    //test
    private final static int AUDIO_CHANNELSTEREO = AudioFormat.CHANNEL_IN_STEREO;
    private final static int AUDIO_CHANNELMONO = AudioFormat.CHANNEL_IN_MONO;
    private int bufferSizeInByteOne = 320;
    private int bufferSizeInByteTwo = 640;

    public static synchronized VrAudioRecord GetInstance() {
        if(m_instance == null) {
            m_instance = new VrAudioRecord();
            return m_instance;
        }
        return m_instance;
    }
    //change audio owner
    public static synchronized void setAudioswitch(String audioswitch) {
        m_audioswitch = audioswitch;
    }
    //get audio onwer
    public static synchronized String getAudioswitch() {
        return m_audioswitch;
    }

    public VrAudioRecord() {
        mExecutorService = Executors.newCachedThreadPool();
    }

    //初始化
    public void init(paramsStruct param) {
        try {
            Log.d("VrAudioRecord", "VrAudioRecord::init");
            if (param.getChannel() == 1) {
                AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
                bufferSizeInByte = 320;
            } else {
                AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
                bufferSizeInByte = 640;
            }
            if (param.getSampleRate() == 16000) {
                AUDIO_SAMPLE_RATE = 16000;
            } else {
                AUDIO_SAMPLE_RATE = param.getSampleRate();
            }
            if (param.getEncoding() == 16) {
                AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
            } else if (param.getEncoding() == 8) {
                AUDIO_ENCODING = AudioFormat.ENCODING_PCM_8BIT;
            }

            // 获得缓冲区字节大小
            minBufferSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                    AUDIO_CHANNEL, AUDIO_ENCODING);
            // 创建record对象
            //mAudioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, Math.max(bufferSizeInByte, minBufferSize));
            mAudioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, 16, AUDIO_ENCODING,  Math.max(bufferSizeInByte, minBufferSize));
            // 创建buffer
            mBuffer = new byte[bufferSizeInByte];

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("VrAudioRecord", "init failed");
            return;
        }

    }

    public void startRecord() {
        mAudioRecord.startRecording();
    }

    public int read(byte[] buffer) {
        int readnum = 0;
        mlock.lock();
        try {
            readnum = mAudioRecord.read(buffer, 0, buffer.length);
        } finally {
            mlock.unlock();
            return readnum;
        }
    }

    public int read(short[] buffer) {
        mlock.lock();
        int readnum = 0;
        try {
            readnum = mAudioRecord.read(buffer, 0, buffer.length);
        } finally {
            mlock.unlock();
            return readnum;
        }
    }

    //停止录音
    public void stopRecord() {
        Log.d("VrAudioRecord", "VrAudioRecord::stopRecord");
        mAudioRecord.stop();
        mAudioRecord.release();
        mIsRecording = false;
        mAudioRecord = null;
    }

    public void stop() {
        mAudioRecord.stop();
    }

    public void release() {
        mAudioRecord.release();
        //mAudioRecord = null;
    }

    //    //uninit
    public void uninit() {
        Log.d("VrAudioRecord", "VrAudioRecord::uninit");
        if (mIsRecording) {
            stopRecord();
        }
        if (mExecutorService != null) {
            mExecutorService.shutdownNow();
        }
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
    }

    public int getState() {
        return mAudioRecord.getState();
    }

    public int getRecordingState() {
        return mAudioRecord.getRecordingState();
    }

    //初始化test
    public void initTest(int channels) {
        try {
            Log.d("VrAudioRecord", "VrAudioRecord::initTest");
            //创建录音文件
            mAudioRecordFile = new File( "/mnt/sdcard/" + "record" + System.currentTimeMillis() + ".pcm");
            if (!mAudioRecordFile.getParentFile().exists()) {
                mAudioRecordFile.getParentFile().mkdirs();
            }
            mAudioRecordFile.createNewFile();
            //创建文件输出流
            mFileOutputStream = new FileOutputStream(mAudioRecordFile);

            //创建参考音文件
            mAudioRecordFileRef = new File("/mnt/sdcard/" + "recordRef" + System.currentTimeMillis() + ".pcm");
            if (!mAudioRecordFileRef.getParentFile().exists()) {
                mAudioRecordFileRef.getParentFile().mkdirs();
            }
            mAudioRecordFileRef.createNewFile();
            mFileOutputStreamRef = new FileOutputStream(mAudioRecordFileRef);

            //创建双路音频文件
            mAudioRecordFileAll = new File("/mnt/sdcard/" + "recordAll" + System.currentTimeMillis() + ".pcm");
            if (!mAudioRecordFileAll.getParentFile().exists()) {
                mAudioRecordFileAll.getParentFile().mkdirs();
            }
            mAudioRecordFileAll.createNewFile();
            mFileOutputStreamAll = new FileOutputStream(mAudioRecordFileAll);

            if (channels == 1) {
                // 获得缓冲区字节大小
                minBufferSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                        AUDIO_CHANNELMONO, AUDIO_ENCODING);
                // 创建record对象
                mAudioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNELMONO, AUDIO_ENCODING, Math.max(bufferSizeInByteOne, minBufferSize));
                // 创建buffer
                mBuffer = new byte[bufferSizeInByteOne];
            }
            else if (channels == 2) {
                // 获得缓冲区字节大小
                minBufferSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE,
                        AUDIO_CHANNELSTEREO, AUDIO_ENCODING);
                // 创建record对象
                mAudioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNELSTEREO, AUDIO_ENCODING, Math.max(bufferSizeInByteTwo, minBufferSize));
                // 创建buffer
                mBuffer = new byte[bufferSizeInByteTwo];
            }
            else {
                Log.d("VrAudioRecord", "init failed: wrong param(channels), channels must be 1 or 2.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("VrAudioRecord", "initTest failed");
            return;
        }
    }

    //开始录音Test
    public void startRecordTest() {
        mIsRecording = true;
        // 开启音频文件写入线程
        new Thread(new AudioRecordThread()).start();
    }

    class AudioRecordThread implements Runnable {
        @Override
        public void run() {
            try {
                Log.d("VrAudioRecord", "VrAudioRecord::startRecord");
                mAudioRecord.startRecording();
                while(true) {
                    if (!mIsRecording) {
                        continue;
                    }
                    int read = mAudioRecord.read(mBuffer, 0, bufferSizeInByteTwo);
                    if(read<=0) {
                        return;
                    }
                    else {
                        mFileOutputStreamAll.write(mBuffer, 0, read);
                        byte tempBufferByte [] = new byte[1];
                        for (int i=0; i<bufferSizeInByteTwo; i++) {
                            tempBufferByte[0] = mBuffer[i];
                            switch (i%4){
                                case 0 :
                                    mFileOutputStream.write(tempBufferByte, 0, 1);
                                    break;
                                case 1 :
                                    mFileOutputStream.write(tempBufferByte, 0, 1);
                                    break;
                                case 2 :
                                    mFileOutputStreamRef.write(tempBufferByte, 0, 1);
                                    break;
                                case 3 :
                                    mFileOutputStreamRef.write(tempBufferByte, 0, 1);
                                    break;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }

    //uninit
//    public void uninit() {
//        Log.d("VrAudioRecord", "VrAudioRecord::uninit");
//        if (mIsRecording) {
//            stopRecord();
//        }
//        if (mExecutorService != null) {
//            mExecutorService.shutdownNow();
//        }
//        if (mAudioRecord != null) {
//            mAudioRecord.stop();
//            mAudioRecord.release();
//            mAudioRecord = null;
//        }
//    }

}
