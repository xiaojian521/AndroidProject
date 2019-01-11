package vr.suntec.net.vrapp.vrService.vrEngine;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import vr.suntec.net.vrapp.vrService.audioManager.VrAudioRecord;

public class BaiduPocJNI extends VrEngineProxy{
    private short m_buffershort[];
    private int m_buffersize;
    private VrAudioRecord m_audiorecord;
    private FileOutputStream mFileOutputStreamAll;
    private File mAudioRecordFileAll;
    private static int minBufferSize;
    private static Thread m_recognizerThread;
    private byte m_bufferbyte[];
    private static BaiduPocJNI m_instance;

    public static synchronized BaiduPocJNI GetInstance() {
        if(m_instance == null) {
            m_instance = new BaiduPocJNI();
            return m_instance;
        }
        return m_instance;
    }

    public BaiduPocJNI() {
        m_buffersize = 320;
        m_buffershort = new short[m_buffersize];
        m_bufferbyte = new byte[m_buffersize*2];
        m_audiorecord = VrAudioRecord.GetInstance();
    }

    public void VrSendMessage(String msg) {
        SendMessage(msg);
    }

    public void VrStopDiag() {
        StopDiag();
    }

    public void VrStartDiag(String doMainType) {
        StartDiag(doMainType);
    }

    //m_audioswitch true is sphinx , false is baiduengine
    @Override
    public void VrWriteAudioDataToEngine(short[] pData, int size) {
        WriteAudioDataToEngine(pData, size);
    }
    @Override
    public void Destroy(){

    }


    public native static void SendMessage(String msg);
    public native static void StopDiag();
    public native static void StartDiag(String doMainType);
    public native static void WriteAudioDataToEngine(short[] pData, int size);

}

