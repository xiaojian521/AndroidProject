//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package vr.suntec.net.vrapp.vrService.awake;

import java.io.File;
import java.io.IOException;

import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;

public class VrSpeechRecognizerSetup {
    private final Config config;

    public static VrSpeechRecognizerSetup defaultSetup() {
        return new VrSpeechRecognizerSetup(Decoder.defaultConfig());
    }

    public static VrSpeechRecognizerSetup setupFromFile(File configFile) {
        return new VrSpeechRecognizerSetup(Decoder.fileConfig(configFile.getPath()));
    }

    private VrSpeechRecognizerSetup(Config config) {
        this.config = config;
    }

    public VrSpeechRecognizer getRecognizer() throws IOException {
        return new VrSpeechRecognizer(this.config);
    }

    public VrSpeechRecognizerSetup setAcousticModel(File model) {
        return this.setString("-hmm", model.getPath());
    }

    public VrSpeechRecognizerSetup setDictionary(File dictionary) {
        return this.setString("-dict", dictionary.getPath());
    }

    public VrSpeechRecognizerSetup setSampleRate(int rate) {
        return this.setFloat("-samprate", (double)rate);
    }

    public VrSpeechRecognizerSetup setRawLogDir(File dir) {
        return this.setString("-rawlogdir", dir.getPath());
    }

    public VrSpeechRecognizerSetup setKeywordThreshold(float threshold) {
        return this.setFloat("-kws_threshold", (double)threshold);
    }

    public VrSpeechRecognizerSetup setBoolean(String key, boolean value) {
        this.config.setBoolean(key, value);
        return this;
    }

    public VrSpeechRecognizerSetup setInteger(String key, int value) {
        this.config.setInt(key, value);
        return this;
    }

    public VrSpeechRecognizerSetup setFloat(String key, double value) {
        this.config.setFloat(key, value);
        return this;
    }

    public VrSpeechRecognizerSetup setString(String key, String value) {
        this.config.setString(key, value);
        return this;
    }

    static {
        System.loadLibrary("pocketsphinx_jni");
    }
}

