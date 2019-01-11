package vr.suntec.net.vrapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import vr.suntec.net.vrapp.VRCommonDef;

public class BootCompleteReceiver extends BroadcastReceiver {
    private static final String strActopnBootCmpName = "android.intent.action.BOOT_COMPLETED";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (strActopnBootCmpName.equals(intent.getAction())) {
            Log.d(VRCommonDef.VRLogTag, "VR BootCompleteReceiver finished");
            // Create Remote Service
        }
    }
}
