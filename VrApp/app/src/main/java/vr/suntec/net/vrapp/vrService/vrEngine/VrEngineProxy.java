package vr.suntec.net.vrapp.vrService.vrEngine;

public abstract class VrEngineProxy {
    public abstract void VrWriteAudioDataToEngine(short[] pData, int size);
    public abstract void Destroy();
}
