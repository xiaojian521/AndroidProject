package vr.suntec.net.vrapp.vrService.TtsEngine;

import android.content.Context;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.nuance.android.vocalizer.*;
import vr.suntec.net.vrapp.VRCommonDef;

public class NuanceEngine implements VocalizerEngineListener, VocalizerSpeechMarkListener, VocalizerAudioOutputListener, TtsEngineBase {
    private Context m_con;
    private static NuanceEngine s_instance = null;
    /**
     * The text to speech engine.
     */
    private VocalizerEngine ttsEngine = null;

    /**
     * The list of available voices that can be used to speak.
     * @see #updateVoiceList()
     */
    private VocalizerVoice[] availableVoices = null;

    /**
     * Array of possible volumes to be used when speaking.
     */
    private static int volumeTable[] = {0, 10, 20, 30, 40, 50, 60, 70, 80, 100};

    /**
     * Array of possible pitch values to be used when speaking.
     */
    private static int pitchTable[] = {50, 70, 90, 100, 110, 120, 140, 160, 170, 200};

    /**
     * Array of possible rates to be used when speaking.
     */
    private static int rateTable[] = {50, 80, 100, 130, 150, 180, 200, 280, 350, 400};

    private int m_rate = rateTable[2];
    private int m_volume = volumeTable[9];
    private int m_pitch = pitchTable[3];

    private String m_ttsEngineCurState;

    public static synchronized NuanceEngine GetInstance() {
        return s_instance;
    }

    public static synchronized NuanceEngine CreateInstance(Context context) {
        if (s_instance == null) {
            s_instance = new NuanceEngine();
            s_instance.onTtsCreate(context);
        }
        return s_instance;
    }


//    private TtsEngineControl m_pTtsEngineControl = null;

    public NuanceEngine() {
        Log.d(VRCommonDef.VRLogTag, "NuanceEngine");
    }

    /** Called when the activity is first created. */
    @Override
    public void onTtsCreate(Context con) {
        Log.d(VRCommonDef.VRLogTag, "NuanceEngine::onTtsCreate");
        initializeSpeechEngine(con);
    }

    /**
     * Called when the activity is being destroyed. Note that Android can call this
     * method even if the activity is not being finished. For example, as a result
     * of a screen rotation the activity will be destroyed and recreated.
     * We need to make sure that the Text-to-Speech engine is released only
     * when the activity is really finishing.
     *
     * @see #initializeSpeechEngine()
     * @see #onRetainNonConfigurationInstance()
     */

    @Override
    public void onTtsDestroy() {
        if ( ttsEngine != null ) {
            ttsEngine.release();
        }
    }

    @Override
    public int onStartSpeak(String context, String textId) {
        if (ttsEngine != null) {
            Log.d(VRCommonDef.VRLogTag, "NuanceEngine::onStartSpeak : " + context);
            ttsEngine.speak(context, false, textId);
        }
        return 0;
    }

    @Override
    public int onStopSpeak() {
        Log.d(VRCommonDef.VRLogTag, "NuanceEngine::onStopSpeak");
        int code = -1;
        if (getTtsStatus() == VocalizerEngine.STATE_READY) {
        }
        else {
            code = ttsEngine.stop() ? 0 : -1;
        }
        return code;
    }

    @Override
    public String GetEngineIsAvailable() {
        return m_ttsEngineCurState;
    }

    public boolean onResumeSpeak() {
        Log.d(VRCommonDef.VRLogTag, "NuanceEngine::onResumeSpeak");
        if ( ttsEngine.isPaused() ) {
            ttsEngine.resume();
            return true;
        }
        return false;
    }

    public boolean onPauseSpeak() {
        Log.d(VRCommonDef.VRLogTag, "NuanceEngine::onPauseSpeak");
        if ( !ttsEngine.isPaused() ) {
            ttsEngine.pause();
            return true;
        }
        return false;
    }

    // Initialize the Text-to-Speech engine.
    public void onInitialize() {
        Log.d(VRCommonDef.VRLogTag, "NuanceEngine::Initialize");
        if ( !ttsEngine.isInitialized() ) {
            ttsEngine.initialize();
        }
    }

    // release the Text-to-Speech engine.
    public void onRelease() {
        Log.d(VRCommonDef.VRLogTag, "NuanceEngine::onRelease");
        if ( ttsEngine.isInitialized() ) {
            ttsEngine.release();
        }
    }

    /**
     * public static final int STATE_UNINITIALIZED = 1;
     * public static final int STATE_INITIALIZED = 2;
     * public static final int STATE_SPEAKING = 3;
     * public static final int STATE_READY = 4;
     * public static final int STATE_PAUSED = 5;
     * public static final int STATE_INIT_ERROR = 6;
     */
    public int getTtsStatus() {
        int status = ttsEngine.getState();
        Log.d(VRCommonDef.VRLogTag, "NuanceEngine::getTtsStatus: " + status);
        return status;
    }

    /**
     * Loads the voice that is currently selected in the voice list spinner.
     */
    private void loadSelectedVoice() {
        Log.d(VRCommonDef.VRLogTag, "NuanceEngine::loadSelectedVoice");
        if ( availableVoices == null ) {
            Log.e(VRCommonDef.VRLogTag, "loadSelectedVoice: no available voices.");
            return;
        }

        try {
            if ( ttsEngine.loadVoice(availableVoices[0]) ) {
                configureSpeechProperties();
            }
        }
        catch(Exception e) {
            Log.e(VRCommonDef.VRLogTag, "EXCEPTION WHILE TRYING TO LOAD VOICE: " + e);
        }
    }

    /**
     * Updates the Text-to-Speech engine properties (volume, rate and pitch) with
     * the values currently selected in the UI. This method will be called whenever
     * the selection of any of the properties spinners is changed, or when a new
     * voice is loaded.
     */
    private void configureSpeechProperties() {
        Log.d(VRCommonDef.VRLogTag, "NuanceEngine::configureSpeechProperties");
        ttsEngine.setSpeechVolume(m_volume);
        ttsEngine.setSpeechRate(m_rate);
        ttsEngine.setSpeechPitch(m_pitch);
    }

    /**
     * Obtains a list of voices that are currently available in the system that can
     * be used by the Text-to-Speech engine.
     *
     * This method also populates the voice list spinner with all available voices
     * and selects the spinner item that corresponds to the currently used voice (if any)
     */
    public void updateVoiceList() {
        Log.d(VRCommonDef.VRLogTag, "NuanceEngine::updateVoiceList");
        availableVoices = ttsEngine.getVoiceList();

        if ( availableVoices != null ) {
            VocalizerVoice currentVoice = ttsEngine.getCurrentVoice();

            // Make all the voices available in the voice list
             //ArrayAdapter<String> voiceListAdapter=new ArrayAdapter<String>(m_con, android.R.layout.simple_spinner_item);
            for ( VocalizerVoice voice : availableVoices ) {
                //voiceListAdapter.add(new String(voice.getVoiceName()+" (" + voice.getLanguage() + ")"));
                Log.d(VRCommonDef.VRLogTag, "VocalizerVoice : " + voice);
            }
        }
    }

    /**
     * This method will be called when the state of the Text-to-Speech engine changes.
     * The main purpose of this method is to update the contents of the UI according
     * to the current engine state.
     *
     * Note that when the engine is done initializing the first voice in the list
     * will automatically be loaded by a call to loadSelectedVoice. It is not
     * the default behavior of the Text-to-Speech engine to load any voice upon
     * initialization.
     */
    @Override
    public void onStateChanged(int newState) {

        Log.d(VRCommonDef.VRLogTag, "NuanceEngine::onStateChanged " + newState);

        switch (newState) {
            case VocalizerEngine.STATE_INITIALIZED:
                updateVoiceList();
                loadSelectedVoice();
                m_ttsEngineCurState = "invalide";
                break;
            case VocalizerEngine.STATE_READY:

            case VocalizerEngine.STATE_SPEAKING:

            case VocalizerEngine.STATE_PAUSED:
                m_ttsEngineCurState = "available";
                break;
            case VocalizerEngine.STATE_UNINITIALIZED:
                m_ttsEngineCurState = "invalide";
                break;
            default:
                break;
        }

    }

    /**
     * This method will be called by the Text-to-Speech engine when
     * it begins speaking. Note that in order to be notified one must pass
     * a text id to the speak method.
     */
    @Override
    public void onSpeakElementStarted(String id) {
        Log.d(VRCommonDef.VRLogTag, "NuanceEngine::onSpeakElementStarted : " + id);
        //m_pTtsEngineControl.onCompletedSpeak(id);
    }

    /**
     * This method will be called by the Text-to-Speech engine when
     * it has completed speaking the text. Note that in order to be notified
     * one must pass a text id to the speak method.
     */
    @Override
    public void onSpeakElementCompleted(String id) {
        Log.d(VRCommonDef.VRLogTag, "NuanceEngine::onSpeakElementCompleted : " + id);
        //m_pTtsEngineControl.onCompletedSpeak(id);
    }

    /**
     * This method will be called by the Text-to-Speech engine when a
     * speech mark is received. There are several types of speech marks.
     * This example handles lip position information and word markers
     * within the original text.
     */
    @Override
    public void onSpeechMarkReceived(VocalizerSpeechMark mark) {
        if ( mark.getType() == VocalizerSpeechMark.VMOBILE_MRK_WORD ) {
             Log.d(VRCommonDef.VRLogTag, "VocalizerSpeechMark : " + mark.getSrcPos() + mark.getSrcTextLen());
        }

//         mark.getMouthWidth();
//         mark.getMouthHeight();
//         mark.getMouthUpturn();
//
//         if ( mark.getType() == VocalizerSpeechMark.VMOBILE_MRK_PHONEME ) {
//             // We have received a new phoneme. Pass the lip information
//             // to the view handling the mouth movements.
//             mouthView.setMark(mark);
//         }
//         else if ( mark.getType() == VocalizerSpeechMark.VMOBILE_MRK_WORD ) {
//             textField.setSelection(mark.getSrcPos(), mark.getSrcPos()+mark.getSrcTextLen());
//             textField.moveCursorToVisibleOffset();
//         }


    }

    /**
     * This method will be called by the Text-to-Speech engine as audio
     * samples are generated. This demo application displays the audio data
     * in an oscilloscope.
     */
    @Override
    public void onAudioData(final VocalizerAudioSettings settings, final short[] audioData) {
        // We have received audio samples. Show them in the oscilloscope.
        // osciView.setAudioData(audioData);
    }

    /**
     * This method will be called by the Text-to-Speech engine when the list
     * of available voices changes. This is due to a voice package being
     * installed or removed in the system.
     */
    @Override
    public void onVoiceListChanged() {
        updateVoiceList();
    }

    /**
     * Constructs and begins initialization of the Text-to-Speech engine.
     *
     * This method will be called once during the life time of the activity, but
     * in response of two events: first time creation of the activity (when the
     * activity is first launched) and recreation of the activity (as a result, for example,
     * of a screen rotation).
     *
     */
    private void initializeSpeechEngine(Context con) {
        this.m_con = con;
        // Check if we have a Text-to-Speech engine object from the last session
        // (activity recreation).
        ttsEngine = new VocalizerEngine(con);
        // Register this activity as the listener to receive state change events.
        ttsEngine.setListener(this);
        // Register this activity to receive speech markers when text is being spoken.
        ttsEngine.setSpeechMarkListener(this);
        // Register this activity to receive audio samples.
        ttsEngine.setAudioOutputListener(this, false);
        // Begin the initialization of the Text-to-Speech engine. Note that the
        // initialization process is asynchronous. onStateChanged will be invoked
        // once the initialization process is completed.
        ttsEngine.setAudioStream(AudioTrack.MODE_STREAM);
        ttsEngine.initialize();

    }
}
