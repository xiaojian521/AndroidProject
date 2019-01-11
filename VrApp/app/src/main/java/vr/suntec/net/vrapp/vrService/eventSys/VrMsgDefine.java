package vr.suntec.net.vrapp.vrService.eventSys;

public final class VrMsgDefine {

    private VrMsgDefine() {}
    //////////message define//////////////
    public static final int   VrMessageToEngine_CreateEngine = 0;
    public static final int   VrMessageToEngine_StartDialog = 1;
    public static final int   VrMessageToEngine_StopDialog = 2;
    public static final int   VrMessageToEngine_DestroyEngine = 3;

    public static final int   VrMessageToDM_Awake = 4;

    public static final int   VrMessageToDM_FromDE = 5;
//    public class VrEngineValue extends Object{
//        public String value = null;
//    }
    //////////message define//////////////
}
