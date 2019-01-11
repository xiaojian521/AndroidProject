package vr.suntec.net.vrapp.aidl;
import vr.suntec.net.vrapp.aidl.IVRAidlCallBack;
import vr.suntec.net.vrapp.aidl.IVRAidliautolinkListener;
import vr.suntec.net.vrapp.aidl.IVRAidlListener;

/** {@hide} */
interface IVRAidlService {
    /*********************************these function will be delete Start*****************/
    void registerListener(IVRAidlCallBack l);
    void registerAppListener(IVRAidlListener l, String AppType);
    void startVR();
    void getCurrentVRStatus();
    void resetVR();
    void setUseVR(boolean status);
    void setEngineUseVR(String engine);
    void exitVR();
    /*********************************these function will be delete END*****************/
    void registerAidlListener(IVRAidlListener l, int type);
    void registerAidlCommonListener(IVRAidlCallBack l, String type);
    void registerMsgList(String type, String doMain, String op);
    void ttsMsg(String msg);
    /*------------------------------------------------------------------*/
    ///< UI APIS start
    /*------------------------------------------------------------------*/
    void reqStart(int AppType, String AppID);
    void reqStop(String SessionID,int Type);
    void reqRegisterAddAppInfo(String AppID, String AppPathInfo, int AppType);
    void reqRegisterDeleteAppInfo(String AppID, String AppPathInfo, int AppType);
    void reqRegisterUpdateAppInfo(String AppID, String AppPathInfo, int AppType);
    /*------------------------------------------------------------------*/
    ///< UI APIS END
    /*------------------------------------------------------------------*/

    void reqSendDataToVR(String SessionID, String Data);

    /*------------------------------------------------------------------*/
    ///< iautolink APIS start
    /*------------------------------------------------------------------*/
    void iautolinkSendDataToVR(String SessionID, String Msg, String DateType);
    void iSuggestionDataToVR(String Msg);
    void registeriautolinkListener(IVRAidliautolinkListener l);

    /*------------------------------------------------------------------*/
    ///< iautolink APIS end
    /*------------------------------------------------------------------*/

    /*------------------------------------------------------------------*/
    ///< webApp(wechat)
    /*------------------------------------------------------------------*/
    void wechatSendDataToVR(String SessionID, String Msg);
}
