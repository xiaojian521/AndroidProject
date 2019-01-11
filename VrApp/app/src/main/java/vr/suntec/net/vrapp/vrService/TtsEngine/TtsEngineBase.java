package vr.suntec.net.vrapp.vrService.TtsEngine;

import android.content.Context;

public interface TtsEngineBase {
    public void onTtsCreate(Context con);
    public void onTtsDestroy();
    public int onStopSpeak();
    public int onStartSpeak(String context, String textId);
    public String GetEngineIsAvailable();
}