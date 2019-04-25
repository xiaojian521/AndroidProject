package com.iauto.baiduvoice;

import android.util.Log;

public class VrBaiduServiceJni {
    private static final String TAG = "VrBaiduServiceJni";

    static {
        try {
            Log.d(TAG, "VrBaiduServiceJni static loadLibrary");
            System.loadLibrary("nmvr_jni");
        }catch(Throwable e) {
            Log.e(TAG, "static initializer: " + e.getMessage());
        }
    }

    public VrBaiduServiceJni() {
        Log.d(TAG, "VrBaiduServiceJni construct");
        native_setupListener();
        Log.d(TAG, "VrBaiduServiceJni construct success");
    }


    public static void onVrServiceMessage(String msgName, byte[] data) {
        Log.d(TAG, "Recv message from vr service, msg name: " + msgName);
        //======================================================
        //use our service
        // VrAppService service = mListener.getAppService();
        // if (null == service) {
        //     Log.e(TAG, "onVrServiceMessage: Can't fetch service instance");
        //     return;
        // }

        // service.sendRequest(msgName, data);
        //======================================================
    }


    public void onCreate() {
        native_onCreate();
    }
    public void onDestroy() {
        native_onDestroy();
    }
    public void onStart() {
        native_onStart();
    }
    public void onStop() {
        native_onStop();
    }
    public void onSuspend() {
        native_onSuspend();
    }
    public void onAwake() {
        native_onAwake();
    }
    public void onUIResumed() {
        native_onUIResumed();
    }
    public void onCommand() {
        native_onCommand();
    }
    


    private native final void native_setupListener();

    //lifecycle interface
    private native void native_onCreate();
    private native void native_onDestroy();
    private native void native_onStart();
    private native void native_onStop();
    private native void native_onSuspend();
    private native void native_onAwake();
    private native void native_onUIResumed();
    private native void native_onCommand();

    //aidl interface
    private native void native_ttsMsg(String msg);
    private native void native_reqStart(int AppType, String AppID);
    private native void native_reqStop(String SessionID,int Type);
    private native void native_reqSendDataToVR(String SessionID, String Data);
    private native void native_iautolinkSendDataToVR(String SessionID, String Msg, String DateType);
    private native void native_iSuggestionDataToVR(String Msg);
    private native void native_wechatSendDataToVR(String SessionID, String Msg);
    private native void native_notifyUI(String SessionID, String Msg);
    private native void native_replyStart(int AppType, String SessionID, int Result);
    private native void native_replyStop(String SessionID, int Type);
    private native void native_replySendDataToVR(String SessionID, boolean Success);
    private native void native_highLevelInterrupt(String SessionID, int InterruptType);
    private native void native_receiveKWD(String Msg);
    private native void native_notifyiAutolinkInfo(String SessionID, String Msg);
    private native void native_notify(String msg, String sessionId);

}
