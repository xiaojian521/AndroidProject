package vr.suntec.net.vrapp.vrService.vrEngine;
//
//
////baiduengine 发送
public class VrEngineJNICreate {

    public static native boolean CreateEngine();
    public static native boolean DestroyEngine();

    public static MsgCallBack m_pMsgCallBack;

    public void setMsgCallBack(MsgCallBack ptrMsg) {
        m_pMsgCallBack = ptrMsg;
    }

    public static boolean ListenerVrMessage(String str) {
        if (m_pMsgCallBack != null) {
            m_pMsgCallBack.OnMessage(str);
        }
        return true;
    }

    public interface MsgCallBack{
        public void OnMessage(String str);
    }
}
//
//class test{
//    private VrMsgControlHandler mHandler = new VrMsgControlHandler(Looper.getMainLooper());
//
//    public void demoTest() {
//        VrEngineJNICreate.MsgCallBack callBack = new VrEngineJNICreate.MsgCallBack(){
//            @Override
//            public void Message(String str) {
//                MsgToApk(str);
//            }
//        };
//    }
//
//    public synchronized void MsgToApk(String str) {
//        Message msg = mHandler.obtainMessage();
//        msg.what = VR_MSG_INFO;
//        msg.obj = str;
//        mHandler.sendMessage(msg);
//    }
//
//            if(msg == null) {
//
//                return;
//            }
//            if (VR_MSG_INFO == msg.what) {
//                String info = (String) msg.obj;
//
//            }
//        }
//    }
//
//}
//
