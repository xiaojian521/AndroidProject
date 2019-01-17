//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package vr.suntec.net.vrapp.vrService.awake;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.FsgModel;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import vr.suntec.net.vrapp.VRCommonDef;
import vr.suntec.net.vrapp.ui.MainActivity;
import vr.suntec.net.vrapp.vrService.audioManager.VrAudioManager;
import vr.suntec.net.vrapp.vrService.audioManager.VrAudioRecord;
import vr.suntec.net.vrapp.vrService.audioManager.paramsStruct;
import vr.suntec.net.vrapp.vrService.vrEngine.BaiduPocJNI;

public class VrSpeechRecognizer {
    protected static final String TAG = VrSpeechRecognizer.class.getSimpleName();
    public final Decoder decoder;
    private final int sampleRate;
    private static final float BUFFER_SIZE_SECONDS = 0.4F;
    private int bufferSize;
    private VrAudioRecord m_recorder;
    paramsStruct m_paramsStruct;
    private Thread recognizerThread;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Collection<RecognitionListener> listeners = new HashSet();

    //创建audiorecorfer
    protected VrSpeechRecognizer(Config config) throws IOException {
        this.decoder = new Decoder(config);
        this.sampleRate = 16000;
        m_paramsStruct = new paramsStruct(1,sampleRate,2,16);
        if(m_paramsStruct.getChannel() == 2) {
            this.bufferSize = 640;
        }
        else if (m_paramsStruct.getChannel() == 1) {
            this.bufferSize = 320;
        }
        else
            throw new IOException("audio channel is not right ");
        //this.bufferSize = Math.round((float)this.sampleRate * 0.4F);
        this.m_recorder = VrAudioRecord.GetInstance();
        this.m_recorder.init(m_paramsStruct);

        if (this.m_recorder.getState() == 0) {
            this.m_recorder.release();
            throw new IOException("Failed to initialize recorder. Microphone might be already in use.");
        }
    }

    public void addListener(RecognitionListener listener) {
        Collection var2 = this.listeners;
        synchronized(this.listeners) {
            this.listeners.add(listener);
        }
    }

    public void removeListener(RecognitionListener listener) {
        Collection var2 = this.listeners;
        synchronized(this.listeners) {
            this.listeners.remove(listener);
        }
    }

    public boolean startListening(String searchName) {
        if (null != this.recognizerThread) {
            return false;
        } else {
            Log.i(TAG, String.format("Start recognition \"%s\"", searchName));
            this.decoder.setSearch(searchName);
            this.recognizerThread = new VrSpeechRecognizer.RecognizerThread();
            this.recognizerThread.start();
            return true;
        }
    }

    public boolean startListening(String searchName, int timeout) {
        if (null != this.recognizerThread) {
            return false;
        } else {
            Log.i(TAG, String.format("Start recognition \"%s\"", searchName));
            this.decoder.setSearch(searchName);
            this.recognizerThread = new VrSpeechRecognizer.RecognizerThread(timeout);
            this.recognizerThread.start();
            return true;
        }
    }

    private boolean stopRecognizerThread() {
        if (null == this.recognizerThread) {
            return false;
        } else {
            try {
                this.recognizerThread.interrupt();
                this.recognizerThread.join();
            } catch (InterruptedException var2) {
                Thread.currentThread().interrupt();
            }

            this.recognizerThread = null;
            return true;
        }
    }

    public boolean stop() {
        boolean result = this.stopRecognizerThread();
        if (result) {
            Log.i(TAG, "Stop recognition");
            Hypothesis hypothesis = this.decoder.hyp();
            this.mainHandler.post(new VrSpeechRecognizer.ResultEvent(hypothesis, true));
        }

        return result;
    }

    public boolean cancel() {
        boolean result = this.stopRecognizerThread();
        if (result) {
            Log.i(TAG, "Cancel recognition");
        }

        return result;
    }

    public Decoder getDecoder() {
        return this.decoder;
    }

    public void shutdown() {
        this.m_recorder.release();
    }

    public String getSearchName() {
        return this.decoder.getSearch();
    }

    public void addFsgSearch(String searchName, FsgModel fsgModel) {
        this.decoder.setFsg(searchName, fsgModel);
    }

    public void addGrammarSearch(String name, File file) {
        Log.i(TAG, String.format("Load JSGF %s", file));
        this.decoder.setJsgfFile(name, file.getPath());
    }

    public void addNgramSearch(String name, File file) {
        Log.i(TAG, String.format("Load N-gram model %s", file));
        this.decoder.setLmFile(name, file.getPath());
    }

    public void addKeyphraseSearch(String name, String phrase) {
        this.decoder.setKeyphrase(name, phrase);
    }

    public void addKeywordSearch(String name, File file) {
        this.decoder.setKws(name, file.getPath());
    }

    public void addAllphoneSearch(String name, File file) {
        this.decoder.setAllphoneFile(name, file.getPath());
    }

    private class TimeoutEvent extends VrSpeechRecognizer.RecognitionEvent {
        private TimeoutEvent() {
            super();
        }

        protected void execute(RecognitionListener listener) {
            listener.onTimeout();
        }
    }

    private class OnErrorEvent extends VrSpeechRecognizer.RecognitionEvent {
        private final Exception exception;

        OnErrorEvent(Exception exception) {
            super();
            this.exception = exception;
        }

        protected void execute(RecognitionListener listener) {
            listener.onError(this.exception);
        }
    }

    private class ResultEvent extends VrSpeechRecognizer.RecognitionEvent {
        protected final Hypothesis hypothesis;
        private final boolean finalResult;

        ResultEvent(Hypothesis hypothesis, boolean finalResult) {
            super();
            this.hypothesis = hypothesis;
            this.finalResult = finalResult;
        }

        protected void execute(RecognitionListener listener) {
            if (this.finalResult) {
                listener.onResult(this.hypothesis);
            } else {
                listener.onPartialResult(this.hypothesis);
            }
        }
    }

    private class InSpeechChangeEvent extends VrSpeechRecognizer.RecognitionEvent {
        private final boolean state;

        InSpeechChangeEvent(boolean state) {
            super();
            this.state = state;
        }

        protected void execute(RecognitionListener listener) {
            if (this.state) {
                listener.onBeginningOfSpeech();
            } else {
                listener.onEndOfSpeech();
            }

        }
    }

    private abstract class RecognitionEvent implements Runnable {
        private RecognitionEvent() {
        }

        public void run() {
            RecognitionListener[] emptyArray = new RecognitionListener[0];
            RecognitionListener[] var2 = (RecognitionListener[])VrSpeechRecognizer.this.listeners.toArray(emptyArray);
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                RecognitionListener listener = var2[var4];
                this.execute(listener);
            }

        }

        protected abstract void execute(RecognitionListener var1);
    }

    private final class RecognizerThread extends Thread {
        private int remainingSamples;
        private int timeoutSamples;
        private static final int NO_TIMEOUT = -1;

        public RecognizerThread(int timeout) {
            if (timeout != -1) {
                this.timeoutSamples = timeout * VrSpeechRecognizer.this.sampleRate / 1000;
            } else {
                this.timeoutSamples = -1;
            }

            this.remainingSamples = this.timeoutSamples;
        }

        public RecognizerThread() {
            this(-1);
        }

        public void run() {
            VrSpeechRecognizer.this.m_recorder.startRecord();
            if (VrSpeechRecognizer.this.m_recorder.getRecordingState() == 1) {
                VrSpeechRecognizer.this.m_recorder.stop();
                IOException ioe = new IOException("Failed to start recording. Microphone might be already in use.");
                VrSpeechRecognizer.this.mainHandler.post(VrSpeechRecognizer.this.new OnErrorEvent(ioe));
            } else {
                Log.d(VrSpeechRecognizer.TAG, "Starting decoding");
                VrSpeechRecognizer.this.decoder.startUtt();
                short[] buffershort = new short[VrSpeechRecognizer.this.bufferSize];
                boolean inSpeech = VrSpeechRecognizer.this.decoder.getInSpeech();
                VrSpeechRecognizer.this.m_recorder.read(buffershort);
                //Log.d(VRCommonDef.VRLogTag, "RecognizerThread run xj 1" + Arrays.toString(buffershort));

                while(!interrupted() && (this.timeoutSamples == -1 || this.remainingSamples > 0)) {
                    int nread = VrSpeechRecognizer.this.m_recorder.read(buffershort);
                    //Log.d(VRCommonDef.VRLogTag, "RecognizerThread run xj 1" + nread);

                    if (-1 == nread) {
                        throw new RuntimeException("error reading audio buffer");
                    }

                    if (nread > 0) {
                        //inSpeech is true startspeaking false stopspeaking
                        if (VrAudioRecord.getAudioswitch() == "sphinx") {
                            VrSpeechRecognizer.this.decoder.processRaw(buffershort, (long) nread, false, false);
                            if (VrSpeechRecognizer.this.decoder.getInSpeech() != inSpeech) {
                                inSpeech = VrSpeechRecognizer.this.decoder.getInSpeech();
                                VrSpeechRecognizer.this.mainHandler.post(VrSpeechRecognizer.this.new InSpeechChangeEvent(inSpeech));
                            }

                            if (inSpeech) {
                                this.remainingSamples = this.timeoutSamples;
                            }

                            Hypothesis hypothesis = VrSpeechRecognizer.this.decoder.hyp();
                            VrSpeechRecognizer.this.mainHandler.post(VrSpeechRecognizer.this.new ResultEvent(hypothesis, false));


                            if (this.timeoutSamples != -1) {
                                this.remainingSamples -= nread;
                            }
                        }
                        else if(VrAudioRecord.getAudioswitch() == "baiduengine") {
                            BaiduPocJNI.GetInstance().VrWriteAudioDataToEngine(buffershort,nread);
                        }
                    }
                }

                VrSpeechRecognizer.this.m_recorder.stop();
                VrSpeechRecognizer.this.decoder.endUtt();
                VrSpeechRecognizer.this.mainHandler.removeCallbacksAndMessages((Object)null);
                if (this.timeoutSamples != -1 && this.remainingSamples <= 0) {
                    VrSpeechRecognizer.this.mainHandler.post(VrSpeechRecognizer.this.new TimeoutEvent());
                }

            }
        }
    }
}

