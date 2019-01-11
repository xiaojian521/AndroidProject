package vr.suntec.net.vrapp;

import android.app.Application;
import android.util.Log;

public class VrApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(VRCommonDef.VRLogTag, "VrApplication::onCreate");
        System.loadLibrary("DialogEnginexj");
        // Create DM
    }
}
