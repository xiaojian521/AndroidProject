package vr.suntec.net.vrapp.ui;

import android.app.Activity;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import edu.cmu.pocketsphinx.Decoder;
import vr.suntec.net.vrapp.R;
import vr.suntec.net.vrapp.VRCommonDef;
import vr.suntec.net.vrapp.vrService.TtsEngine.NuanceEngine;
import vr.suntec.net.vrapp.vrService.audioManager.VrAudioManager;
import vr.suntec.net.vrapp.vrService.audioManager.VrAudioRecord;
import vr.suntec.net.vrapp.vrService.awake.VrAwakeEngine;
import vr.suntec.net.vrapp.vrService.eventSys.VrMsgThread;
import vr.suntec.net.vrapp.vrService.vrEngine.BaiduPocJNI;
import vr.suntec.net.vrapp.vrService.vrEngine.VrEngineJNICreate;
import vr.suntec.net.vrapp.vrService.vrEngine.VrEngineJNICreate.MsgCallBack;
import vr.suntec.net.vrapp.vrService.vrEngine.VrEngineProxy;


public class MainActivity extends Activity {
    public TextView caption_text;
    public TextView result_text;
    private VrAwakeEngine m_awake;
    public Button m_startDialog;
    public Button m_stopDialog;
    public static HashMap<String, VrEngineProxy> m_engine;
    Thread MsgThread;
    //private NuanceEngine m_nuance;
    private VrEngineJNICreate m_createBaidujni = null;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        m_engine = new HashMap<>();
        super.onCreate(savedInstanceState);
        try {
            finalize();
        }catch (Throwable a) {
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // Example of a call to a native method
//        TextView tv = (TextView) findViewById(R.id.sample_text);
//        tv.setText(stringFromJNI());
        //sphinx==============================
            //先创建audio
//            VrAudioManager m_micaudio= new VrAudioManager();
//            m_micaudio.onCreate();
//            m_micaudio.onStart();


            caption_text = (TextView) findViewById(R.id.caption_text);
            caption_text.setY(400);
            result_text = (TextView) findViewById(R.id.result_text);
            result_text.setY(800);
            m_awake = VrAwakeEngine.GetInstance();
            VrAudioRecord.setAudioswitch("sphinx");
            m_awake.onCreate(getApplicationContext(), caption_text, result_text);

//            Log.d(VRCommonDef.VRLogTag, "create xj 4");
        //sphinx================================

        //tts===================================
//        m_nuance = NuanceEngine.CreateInstance(getApplicationContext());
//        m_tts = (Button) findViewById(R.id.button);
//        m_tts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                m_nuance.onStartSpeak("你好","test_id");
//            }
//        });
        //tts===================================
        //baidu=======================================
        final BaiduPocJNI m_baidujni= new BaiduPocJNI();
        m_engine.put("baiduengine", m_baidujni);
        m_createBaidujni= new VrEngineJNICreate();
        MsgThread = VrMsgThread.CreateInstance(getApplicationContext(), m_createBaidujni , m_baidujni);
        m_startDialog = (Button)findViewById(R.id.startdialog);
        m_startDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //m_baidujni.VrStartDiag("");
                //开始录音,将录音数据传给baidu_engine
                VrAudioRecord.setAudioswitch("baiduengine");
                //
                //m_baidujni.VrSendMessage("XJ TEST");
            }
        });
        m_stopDialog = (Button) findViewById(R.id.stopdialog);
        m_stopDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // m_baidujni.VrStopDiag();
                VrAudioRecord.setAudioswitch("sphinx");
            }
        });

        //baidu=======================================

    }

    public synchronized void MsgToApk(String str) {
//        Message msg = mHandler.obtainMessage();
//        msg.what = VR_MSG_INFO;
//        msg.obj = str;
//        mHandler.sendMessage(msg);
        Log.d(VRCommonDef.VRLogTag, "MsgToApk = " + str);
    }

    void InitVrEngineCallBack() {
        Log.d(VRCommonDef.VRLogTag, "InitVrEngineCallBack");
        VrEngineJNICreate.MsgCallBack callBack = new VrEngineJNICreate.MsgCallBack(){
            @Override
            public void OnMessage(String str) {
                MsgToApk(str);
            }
        };
        if (callBack != null) {
            m_createBaidujni.setMsgCallBack(callBack);
        }
        else {
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (m_awake.recognizer != null) {
            m_awake.recognizer.cancel();
            m_awake.recognizer.shutdown();
        }
    }
}
