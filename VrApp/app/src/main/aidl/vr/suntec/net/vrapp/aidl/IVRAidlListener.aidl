package vr.suntec.net.vrapp.aidl;


interface IVRAidlListener {
    void notifyUI(String SessionID, String Msg);
    void replyStart(int AppType, String SessionID, int Result);
    void replyStop(String SessionID, int Type);
    void replyRegisterAddAppInfo(String AppID, int AppType, boolean Success);
    void replyRegisterDeleteAppInfo(String AppID, int AppType, boolean Success);
    void replyRegisterUpdateAppInfo(String AppID, int AppType, boolean Success);
    void replySendDataToVR(String SessionID, boolean Success);
    void highLevelInterrupt(String SessionID, int InterruptType);
    void receiveKWD(String Msg);
}
