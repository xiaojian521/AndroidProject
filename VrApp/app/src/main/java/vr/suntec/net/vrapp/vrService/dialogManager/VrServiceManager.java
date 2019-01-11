package vr.suntec.net.vrapp.vrService.dialogManager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import vr.suntec.net.vrapp.vrService.eventSys.VrMsgThread;
import vr.suntec.net.vrapp.vrService.vrEngine.BaiduPocJNI;
import vr.suntec.net.vrapp.vrService.vrEngine.VrEngineJNICreate;

public class VrServiceManager {
    private static VrServiceManager m_VrServiceManager;

    private VrMsgThread mMsgThread = null;
    private Context mContext = null;
    private VrEngineJNICreate m_createBaidujni = null;
    private BaiduPocJNI m_baiduPoc = null;



    public VrServiceManager(Context context) {
        mContext = context;
        m_createBaidujni = new VrEngineJNICreate();
        m_baiduPoc = new BaiduPocJNI();
    }

    public void CreateMsgThread() {
        mMsgThread = new VrMsgThread(mContext, m_createBaidujni, m_baiduPoc);
        mMsgThread.start();
    }

    public static synchronized VrServiceManager GetInstance(Context context) {
        if (null == m_VrServiceManager) {
            m_VrServiceManager = new VrServiceManager(context);
        }
        return m_VrServiceManager;
    }

    public void sendMessage(Message msg) {
        msg.setTarget(mMsgThread.getmHandler());





        mMsgThread.getmHandler().sendMessage(msg);
    }

    public Handler getHandler() {
        return mMsgThread.getmHandler();
    }
}
