package vr.suntec.net.vrapp.vrService.eventSys;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import vr.suntec.net.vrapp.VRCommonDef;
import vr.suntec.net.vrapp.vrService.vrEngine.BaiduPocJNI;
import vr.suntec.net.vrapp.vrService.vrEngine.VrEngineJNICreate;

public class VrMsgThread extends Thread{

    //private static VrMsgThread s_VrMsgThread;
    private Handler mHandler = null;
    private Context mContext = null;
    private AudioManager mAudioManager = null;
    private AudioAttributes mPlaybackAttributes = null;
    private AudioFocusRequest mFocusRequest = null;
    private MediaPlayer mMediaPlayer = null;
    private boolean mPlaybackDelayed = false;
    private VrEngineJNICreate m_createBaidujni = null;
    private BaiduPocJNI m_baiduPoc = null;
    private static VrMsgThread s_VrMsgThread = null;
    //private Dialogmanager mDialogManager;

//    public setDialogManager(Dialogmanager dialogmanager) {
//        mDialogManager = dialogmanager;
//    }

    public VrMsgThread(Context context, VrEngineJNICreate createBaidujni, BaiduPocJNI baiduPoc) {
        mContext = context;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        m_createBaidujni = createBaidujni;
        m_baiduPoc = baiduPoc;
    }

    public class VrEngineValue extends Object{
        public String value = null;
    }

    ////////////////////////////
    @Override
    public void run() {
        //super.run();
        Looper.prepare();

        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg)
            {
                // process incoming messages here
                switch (msg.what) {
                    case VrMsgDefine.VrMessageToEngine_CreateEngine : {
                        //create engine
                        m_createBaidujni.CreateEngine();
                    }
                    case VrMsgDefine.VrMessageToEngine_StartDialog : {
                        //start dialog
                        m_baiduPoc.VrStartDiag("");
                    }
                    case VrMsgDefine.VrMessageToEngine_StopDialog : {
                        //stop dialog
                        m_baiduPoc.StopDiag();
                        //release mic

                        //release audio focus
                        int result = mAudioManager.abandonAudioFocusRequest(mFocusRequest);
                    }
                    case VrMsgDefine.VrMessageToEngine_DestroyEngine : {
                        //destroy engine
                        m_createBaidujni.DestroyEngine();
                    }
                    case VrMsgDefine.VrMessageToDM_Awake : {
                        //get mic

                        //get audio focus
                        AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
                            public void onAudioFocusChange(int focusChange) {
                                switch (focusChange) {
                                    case AudioManager.AUDIOFOCUS_GAIN :
                                        if (mPlaybackDelayed) {
                                            //start dialog now
                                            m_baiduPoc.VrStartDiag("test_zz");
                                            mPlaybackDelayed = false;
                                        }
                                        break;
                                    case AudioManager.AUDIOFOCUS_LOSS :

                                        break;
                                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT :
                                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK :

                                        break;
                                }
                            }
                        };

                        mPlaybackAttributes = new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_MEDIA)
                                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                                .build();
                        mFocusRequest = new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                                .setAudioAttributes(mPlaybackAttributes)
                                .setAcceptsDelayedFocusGain(true)
                                .setWillPauseWhenDucked(true)
                                .setOnAudioFocusChangeListener(afChangeListener, mHandler)
                                .build();
                        mMediaPlayer = new MediaPlayer();
                        mMediaPlayer.setAudioAttributes(mPlaybackAttributes);
                        final Object mFocusLock = new Object();

                        int result = mAudioManager.requestAudioFocus(mFocusRequest);
                        synchronized (mFocusLock) {
                            if (result == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                                mPlaybackDelayed = false;
                            } else if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                                mPlaybackDelayed = false;
                                //start dialog now
                                m_baiduPoc.VrStartDiag("test_zz");
                            } else if (result == AudioManager.AUDIOFOCUS_REQUEST_DELAYED) {
                                mPlaybackDelayed = true;
                            }
                        }
                        //start dialog

                    }
                    case VrMsgDefine.VrMessageToDM_FromDE : {
                        //msg from DE
                        Log.d(VRCommonDef.VRLogTag, "VrMsgThread::VrMessageToDM_FromDE(222222222222)");
                    }
                }
            }
        };

        Looper.loop();
    }

    public static synchronized VrMsgThread CreateInstance(Context context, VrEngineJNICreate createBaidujni, BaiduPocJNI baiduPoc) {
        if (null == s_VrMsgThread) {
            s_VrMsgThread = new VrMsgThread(context,createBaidujni,baiduPoc);
        }
        return s_VrMsgThread;
    }

    public static synchronized VrMsgThread GetInstance() {
        return s_VrMsgThread;
    }

    public Handler getmHandler() {
        return mHandler;
    }

    /////////////////test//////////////////////
    public void TestSendMessage() {
        VrEngineValue mVrEngineValue =new VrEngineValue();
        mVrEngineValue.value = "CreateEngine";
        Message message = getmHandler().obtainMessage();
        message.what = VrMsgDefine.VrMessageToEngine_CreateEngine;
        message.obj = mVrEngineValue;
        mHandler.sendMessage(message);
    }
}
