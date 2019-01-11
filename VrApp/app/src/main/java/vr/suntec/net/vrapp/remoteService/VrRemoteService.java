package vr.suntec.net.vrapp.remoteService;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;


import vr.suntec.net.vrapp.aidl.IVRAidlService;
import vr.suntec.net.vrapp.aidl.IVRAidlCallBack;
import vr.suntec.net.vrapp.aidl.IVRAidliautolinkListener;
import vr.suntec.net.vrapp.aidl.IVRAidlListener;

public class VrRemoteService extends Service {
    private NotificationManager mNotificationManager;
    public VrRemoteService() {
    }

    @Override
    public void onDestroy() {
        Log.d("VrRemoteService","xj onDestroy");

    }
    Binder m_binder = new IVRAidlService.Stub() {
        @Override
        public void registerListener(IVRAidlCallBack l)
        {}
        @Override
        public void registerAppListener(IVRAidlListener l, String AppType)
        {}
        @Override
        public void startVR()
        {
            Log.d("VrRemoteService","xj tongxin");
        }
        @Override
        public void getCurrentVRStatus()
        {}
        @Override
        public void resetVR()
        {}
        @Override
        public void setUseVR(boolean status)
        {}
        @Override
        public void setEngineUseVR(String engine)
        {}
        @Override
        public void exitVR()
        {}
        /*********************************these function will be delete END*****************/

        @Override
        public void registerAidlListener(IVRAidlListener l, int type)
        {}
        @Override
        public void registerAidlCommonListener(IVRAidlCallBack l, String type)
        {
        }
        @Override
        public void registerMsgList(String type, String doMain, String op)
        {}
        @Override
        public void ttsMsg(String msg)
        {}
        /*------------------------------------------------------------------*/
        ///< UI APIS start
        /*------------------------------------------------------------------*/
        @Override
        public void reqStart(int AppType, String AppID)
        {}
        @Override
        public void reqStop(String SessionID,int Type)
        {}
        @Override
        public void reqRegisterAddAppInfo(String AppID, String AppPathInfo, int AppType)
        {}
        @Override
        public void reqRegisterDeleteAppInfo(String AppID, String AppPathInfo, int AppType)
        {}
        @Override
        public void reqRegisterUpdateAppInfo(String AppID, String AppPathInfo, int AppType)
        {}
        /*------------------------------------------------------------------*/
        ///< UI APIS END
        /*------------------------------------------------------------------*/
        @Override
        public void reqSendDataToVR(String SessionID, String Data)
        {}

        /*------------------------------------------------------------------*/
        ///< iautolink APIS start
        /*------------------------------------------------------------------*/
        @Override
        public void iautolinkSendDataToVR(String SessionID, String Msg, String DateType)
        {}
        @Override
        public void iSuggestionDataToVR(String Msg)
        {}
        @Override
        public void registeriautolinkListener(IVRAidliautolinkListener l)
        {}

        /*------------------------------------------------------------------*/
        ///< iautolink APIS end
        /*------------------------------------------------------------------*/

        /*------------------------------------------------------------------*/
        ///< webApp(wechat)
        /*------------------------------------------------------------------*/
        @Override
        public void wechatSendDataToVR(String SessionID, String Msg)
        {}
    };


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.d("VrRemoteService","onBind");
        return m_binder;
    }

    @Override
    public void onCreate()
    {
        Log.d("VrRemoteService","onCreate");
        super.onCreate();
//
//        NotificationChannel channel = new NotificationChannel("TtsServiceChannel", "TtsService", NotificationManager.IMPORTANCE_LOW);
//        NotificationManager manager = (NotificationManager)getSystemService(getApplicationContext().NOTIFICATION_SERVICE);
//        manager.createNotificationChannel(channel);
//
//        Notification notification = new Notification.Builder(getApplicationContext(), "vrAppServiceChannel").build();
//
//        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        mNotificationManager.notify(0, notification);

    }

}
