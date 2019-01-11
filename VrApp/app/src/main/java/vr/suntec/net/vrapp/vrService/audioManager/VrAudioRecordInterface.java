package vr.suntec.net.vrapp.vrService.audioManager;


public interface VrAudioRecordInterface {
    //初始化
    public void init(paramsStruct params);
    //结束录音
    public void stopRecord();
    //开始录音
    public void startRecord();
    //读取录音数据
    //buffer　录音数据(buffer大小建议：单通道320,双通道640)
    public int read(byte[] buffer);
    public int read(short[] buffer);
    ///////////////////////////////////TEST/////////////
    //channels 单双通道（１/２）
    public void initTest(int channels);
    //Test开始录音
    public void startRecordTest();
    ///////////////////////////////////TEST/////////////

    public void uninit();
}
