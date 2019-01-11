package vr.suntec.net.vrapp.vrService.audioManager;

import android.media.AudioFormat;
import android.media.MediaRecorder;

public class paramsStruct {
    private int AUDIOINPUT = MediaRecorder.AudioSource.MIC;
    private int SAMPLERATE = 16000;
    private int CHANNEL = AudioFormat.CHANNEL_IN_STEREO;
    private int AUDIOENCODING = AudioFormat.ENCODING_PCM_16BIT;
    public paramsStruct() {

    }
    //audioInput : 默认麦克风　　1
    //sampleRate : 默认　　　　　16000
    //channel    :　默认(双通道) 2　　　可选：单通道 1
    //audioEncoding : 默认16位　16   可选：8位　　　8　　
    public paramsStruct(int audioInput, int sampleRate, int channel, int audioEncoding) {
        AUDIOINPUT = audioInput;
        SAMPLERATE = sampleRate;
        CHANNEL = channel;
        AUDIOENCODING = audioEncoding;
    }
    public int getInput() {
        return  0;
    }
    public int getSampleRate() {
        return SAMPLERATE;
    }
    public int getChannel() {
        return CHANNEL;
    }
    public int getEncoding() {
        return AUDIOENCODING;
    }
}