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

import java.util.Collection;
import java.util.HashSet;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.FsgModel;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;

import vr.suntec.net.vrapp.VRCommonDef;
import vr.suntec.net.vrapp.vrService.audioManager.VrAudioManager;
import vr.suntec.net.vrapp.vrService.vrEngine.VrEngineProxy;

public class VrSpeechRecognizer extends VrEngineProxy {
    public final Decoder decoder;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Collection<RecognitionListener> listeners = new HashSet();
    private boolean flag = true;
    private boolean inSpeech;
    private VrAudioManager m_micaudio;
    //创建audiorecorfer
    protected VrSpeechRecognizer(Config config) throws IOException {
        this.decoder = new Decoder(config);
        m_micaudio= new VrAudioManager();
        m_micaudio.onCreate();
        //m_micaudio.onStart();
    }

    @Override
    public void VrWriteAudioDataToEngine(short[] pData, int size) {
        if(flag) {
            VrSpeechRecognizer.this.decoder.startUtt();
            inSpeech = VrSpeechRecognizer.this.decoder.getInSpeech();
            flag = false;
        }
        VrSpeechRecognizer.this.decoder.processRaw(pData, (long)size, false, false);
        if (VrSpeechRecognizer.this.decoder.getInSpeech() != inSpeech) {
            inSpeech = VrSpeechRecognizer.this.decoder.getInSpeech();
            VrSpeechRecognizer.this.mainHandler.post(VrSpeechRecognizer.this.new InSpeechChangeEvent(inSpeech));
        }
        Hypothesis hypothesis = VrSpeechRecognizer.this.decoder.hyp();
        VrSpeechRecognizer.this.mainHandler.post(VrSpeechRecognizer.this.new ResultEvent(hypothesis, false));
    }

    @Override
    public void Destroy(){
        VrSpeechRecognizer.this.decoder.endUtt();
        VrSpeechRecognizer.this.mainHandler.removeCallbacksAndMessages((Object)null);
        flag = true;
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

    private boolean stopRecognizerThread() {
        if(m_micaudio.stopRecognizerThread()) {
            return true;
        }
        return false;
    }

    public boolean startListening(String searchName) {
        m_micaudio.onStart();
        this.decoder.setSearch(searchName);
        return true;

    }

    public boolean startListening(String searchName, int timeout) {
        this.decoder.setSearch(searchName);
        return true;
    }

    public boolean stop() {
        boolean result = this.stopRecognizerThread();
        if (result) {
            Hypothesis hypothesis = this.decoder.hyp();
            this.mainHandler.post(new VrSpeechRecognizer.ResultEvent(hypothesis, true));
        }
        return result;
    }

    public boolean cancel() {
        boolean result = this.stopRecognizerThread();
        if (result) {
        }

        return result;
    }

    public Decoder getDecoder() {
        return this.decoder;
    }

    public void shutdown() {
    }

    public String getSearchName() {
        return this.decoder.getSearch();
    }

    public void addFsgSearch(String searchName, FsgModel fsgModel) {
        this.decoder.setFsg(searchName, fsgModel);
    }

    public void addGrammarSearch(String name, File file) {
        this.decoder.setJsgfFile(name, file.getPath());
    }

    public void addNgramSearch(String name, File file) {
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

}

