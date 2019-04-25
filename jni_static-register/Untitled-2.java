package vr.suntec.net.vrapp.vrService.awake;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import vr.suntec.net.vrapp.R;
import vr.suntec.net.vrapp.VRCommonDef;


import static android.widget.Toast.makeText;

public class VrAwakeEngine implements
        RecognitionListener {
    private TextView m_caption_text;
    private TextView m_result_text;
    public static Context m_context;
    private static VrAwakeEngine s_instance = null;
    private static final String KWS_SEARCH = "wakeup";
    private static final String FORECAST_SEARCH = "";
    private static final String DIGITS_SEARCH = "";
    private static final String PHONE_SEARCH = "";
    private static final String MENU_SEARCH = "";


    private static final String KEYPHRASE = "你好小泰";
    public VrSpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;


    public static synchronized VrAwakeEngine GetInstance() {
        return s_instance;
    }

    public static synchronized VrAwakeEngine CreateInstance() {
        if (s_instance == null) {
            s_instance = new VrAwakeEngine();
        }
        return s_instance;
    }

    public void onCreate(Context context,TextView caption_text, TextView result_text) {
        m_context = context;
        m_caption_text = caption_text;
        m_result_text = result_text;
        captions = new HashMap<>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        captions.put(MENU_SEARCH, R.string.menu_caption);
        captions.put(DIGITS_SEARCH, R.string.digits_caption);
        captions.put(PHONE_SEARCH, R.string.phone_caption);
        captions.put(FORECAST_SEARCH, R.string.forecast_caption);
        m_caption_text.setText("Preparing the recognizer");

        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    Assets assets = new Assets(m_context);
                    File assetDir = assets.syncAssets();
                    setupRecognizer(assetDir);
                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
//                    ((TextView) findViewById(R.id.caption_text))
//                            .setText("Failed to init recognizer " + result);
                    Log.d(VRCommonDef.VRLogTag, "Failed to init recognizer");
                } else {
                    switchSearch(KWS_SEARCH);
                }
            }
        }.execute();
    }


    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null)
            return;

        String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE)) {
            //Log.d(VRCommonDef.VRLogTag, "onPartialResult 1" + text);
            m_result_text.setText(text);
            switchSearch(KWS_SEARCH);
        }
//            switchSearch(MENU_SEARCH);
//        else if (text.equals(DIGITS_SEARCH))
//            switchSearch(DIGITS_SEARCH);
//        else if (text.equals(PHONE_SEARCH))
//            switchSearch(PHONE_SEARCH);
//        else if (text.equals(FORECAST_SEARCH))
//            switchSearch(FORECAST_SEARCH);
        else
            m_result_text.setText(text);
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        m_result_text.setText("");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            Log.d(VRCommonDef.VRLogTag,"onResult"  + text);
            makeText(m_context, text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(VRCommonDef.VRLogTag,"onBeginningOfSpeech");
    }

    @Override
    public void onEndOfSpeech() {
        if (!recognizer.getSearchName().equals(KWS_SEARCH))
            Log.d(VRCommonDef.VRLogTag,"onEndOfSpeech ");
        switchSearch(KWS_SEARCH);
    }

    private void switchSearch(String searchName) {
        recognizer.stop();
        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH)) {
            Log.d(VRCommonDef.VRLogTag,"switchSearch 1" + searchName);
            recognizer.startListening(searchName);
        }
        else {
            Log.d(VRCommonDef.VRLogTag,"switchSearch 2" + searchName);
            recognizer.startListening(searchName, 5000);
        }
        String caption = m_context.getResources().getString(captions.get(searchName));
        Log.d(VRCommonDef.VRLogTag,"switchSearch 3" + caption);
        m_caption_text.setText(caption);
    }

    private void setupRecognizer(File assetsDir) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them
        Log.d(VRCommonDef.VRLogTag, "setupRecognizer");
        recognizer = VrSpeechRecognizerSetup.defaultSetup()
                .setAcousticModel(new File(assetsDir, "en-us-ptm"))
                .setDictionary(new File(assetsDir, "CNBaidu.dic"))
                // To disable logging of raw audio comment out this call (takes a lot of space on the device)
                .setRawLogDir(assetsDir)
                // Threshold to tune for keyphrase to balance between false alarms and misses
                .setKeywordThreshold(1e-45f)
                // Use context-independent phonetic search, context-dependent is too slow for mobile
                .setBoolean("-allphone_ci", true)

                .getRecognizer();
        recognizer.addListener(this);
        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */
//        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        // Create grammar-based search for selection between demos
//        File menuGrammar = new File(assetsDir, "menu.gram");
//        recognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
//
//        // Create grammar-based search for digit recognition
//        File digitsGrammar = new File(assetsDir, "digits.gram");
//        recognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
//
//        // Create language model search
//        File languageModel = new File(assetsDir, "weather.dmp");
//        recognizer.addNgramSearch(FORECAST_SEARCH, languageModel);
//
//        // Phonetic search
//        File phoneticModel = new File(assetsDir, "en-phone.dmp");
//        recognizer.addAllphoneSearch(PHONE_SEARCH, phoneticModel);
        Log.d(VRCommonDef.VRLogTag, "setupRecognizer xj 5");
    }

    @Override
    public void onError(Exception error) {
        m_caption_text.setText(error.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch(KWS_SEARCH);
    }
}

