package vr.suntec.net.vrapp.vrService.audioManager;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import edu.cmu.pocketsphinx.Decoder;
import vr.suntec.net.vrapp.VRCommonDef;
import vr.suntec.net.vrapp.ui.MainActivity;
import vr.suntec.net.vrapp.vrService.awake.VrAwakeEngine;
import vr.suntec.net.vrapp.vrService.awake.VrSpeechRecognizer;

public class VrAudioManager {
    private VrAudioRecord m_recorder;
    private paramsStruct m_paramsStruct;
    private int sampleRate;
    private int m_buffersize;
    private static Thread recognizerThread;
    //test=============
//    private FileOutputStream mFileOutputStreamAll;
//    private File mAudioRecordFileAll;
//    //缓冲区字节大小
//    private int minBufferSize = 0;
//    private boolean flag = false;
//    private byte m_bufferbyte[];
    //test=============

    public static synchronized Thread GetThreadId() {
        return recognizerThread;
    }


    public void onCreate() {


        this.sampleRate = 16000;
        m_paramsStruct = new paramsStruct(1,sampleRate,2,16);
        if(m_paramsStruct.getChannel() == 2) {
            this.m_buffersize = 640;
        }
        else if (m_paramsStruct.getChannel() == 1) {
            this.m_buffersize = 320;
        }
        else {
            Log.d(VRCommonDef.VRLogTag, "Audio channel is not right");
        }
        m_recorder = VrAudioRecord.GetInstance();
        this.m_recorder.init(m_paramsStruct);
        if (this.m_recorder.getState() == 0) {
            this.m_recorder.release();
            Log.d(VRCommonDef.VRLogTag, "Failed to initialize recorder. Microphone might be already in use.");
        }
        //===============test=======================
        //创建双路音频文件
//        mAudioRecordFileAll = new File("/mnt/sdcard/" + "recordAll" + System.currentTimeMillis() + ".pcm");
//
//        if (!mAudioRecordFileAll.getParentFile().exists()) {
//            mAudioRecordFileAll.getParentFile().mkdirs();
//            Log.d(VRCommonDef.VRLogTag, "Failed create file 1");
//        }
//        try {
//            mAudioRecordFileAll.createNewFile();
//        } catch (IOException e) {
//            Log.d(VRCommonDef.VRLogTag, "Failed create file 2");
//            e.printStackTrace();
//        }
//        try {
//            mFileOutputStreamAll = new FileOutputStream(mAudioRecordFileAll);
//        } catch (FileNotFoundException e) {
//            Log.d(VRCommonDef.VRLogTag, "Failed create file 3");
//            e.printStackTrace();
//        }
//
//        minBufferSize = AudioRecord.getMinBufferSize(16000, 16, AudioFormat.ENCODING_PCM_16BIT);
//        Log.d(VRCommonDef.VRLogTag, "minbuffersize is = " + minBufferSize);
        //===============test=======================
    }

    public void onStart()   {
        recognizerThread = new RecognizerThread();
        recognizerThread.start();
    }

    private final class RecognizerThread extends Thread {

        @Override
        public void run(){
            Log.d(VRCommonDef.VRLogTag, "Thread xj 0");
            VrAudioManager.this.m_recorder.startRecord();
            if (VrAudioManager.this.m_recorder.getRecordingState() == 1) {
                VrAudioManager.this.m_recorder.stop();
                Log.d(VRCommonDef.VRLogTag, "Failed to start recording. Microphone might be already in use.");
            } else {
                short m_buffershort[] = new short[m_buffersize];
                while(!interrupted()) {
                    int nread = VrAudioManager.this.m_recorder.read(m_buffershort);
                    if (-1 == nread) {
                        Log.d(VRCommonDef.VRLogTag, "error reading audio buffer");
                    }
                    if (nread > 0) {
                        //需要有一个map指针判断开关,如果是sphinx就把数据写个sphinx,如果是baidu就把数据写个baidu
                        if(VrAudioRecord.getAudioswitch() == "sphinx") {
//                            //把数据传给sphinx
//                            //Log.d(VRCommonDef.VRLogTag, "thread nread sphinx");
//                            if(MainActivity.m_engine.get("sphinx") == null) {
//                                continue;
//                            }
//                            MainActivity.m_engine.get("sphinx").VrWriteAudioDataToEngine(m_buffershort,m_buffersize);
                        }
                        else if(VrAudioRecord.getAudioswitch() == "baiduengine") {
                            Log.d(VRCommonDef.VRLogTag, "thread nread baiduengine");
                            MainActivity.m_engine.get("baiduengine").VrWriteAudioDataToEngine(m_buffershort,nread);
                            //test=============================================================
//                            m_bufferbyte = shortToBytes(m_buffershort);
//                            try {
//                                Log.d(VRCommonDef.VRLogTag, "thread nread = " + nread);
//                                mFileOutputStreamAll.write(m_bufferbyte, 0, nread*2);
//                            } catch (IOException e) {
//                                Log.d(VRCommonDef.VRLogTag, "wirte data failed");
//                                e.printStackTrace();
//                            }
                            //test==============================================================
                        }
                        else {
                            continue;
                        }
                    }

                }
                VrAudioManager.this.m_recorder.stop();
                MainActivity.m_engine.get("sphinx").Destroy();
            }

        }
    }

    public boolean stopRecognizerThread() {
        if (null == this.recognizerThread) {
            return false;
        } else {
            try {
                this.recognizerThread.interrupt();
                this.recognizerThread.join();
            } catch (InterruptedException var2) {
                Thread.currentThread().interrupt();
            }
            this.recognizerThread = null;
            return true;
        }
    }

//    public byte[] shortToBytes(short[] shorts) {
//        if(shorts==null){
//            return null;
//        }
//        byte[] bytes = new byte[shorts.length * 2];
//        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shorts);
//        return bytes;
//    }
}
