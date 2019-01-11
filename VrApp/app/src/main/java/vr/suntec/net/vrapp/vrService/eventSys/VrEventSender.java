package vr.suntec.net.vrapp.vrService.eventSys;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import vr.suntec.net.vrapp.VRCommonDef;

public class VrEventSender {
    private VrMsgThread m_msgthread = null;
    private Handler m_handler = null;

    public void onCreate() {
        m_msgthread = VrMsgThread.GetInstance();
    }

    public void MsgCreateEngine() {
        m_handler = m_msgthread.getmHandler();
        Message message = m_handler.obtainMessage();
        message.what = VrMsgDefine.VrMessageToEngine_CreateEngine;
        m_handler.sendMessage(message);
        Log.d(VRCommonDef.VRLogTag, "VrEventSender::MsgCreateEngine");
    }

    public void MsgStarDilog() {
        m_handler = m_msgthread.getmHandler();
        Message message = m_handler.obtainMessage();
        message.what = VrMsgDefine.VrMessageToEngine_StartDialog;
        m_handler.sendMessage(message);
        Log.d(VRCommonDef.VRLogTag, "VrEventSender::MsgStarDilog");
    }

    public void MsgStopDilog() {
        m_handler = m_msgthread.getmHandler();
        Message message = m_handler.obtainMessage();
        message.what = VrMsgDefine.VrMessageToEngine_StopDialog;
        m_handler.sendMessage(message);
        Log.d(VRCommonDef.VRLogTag, "VrEventSender::MsgStopDilog");
    }

}
