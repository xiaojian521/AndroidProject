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
import vr.suntec.net.vrapp.ui.MainActivity;


import static android.widget.Toast.makeText;

public class VrAwakeEngine implements
        RecognitionListener {
    private TextView m_caption_text;
    private TextView m_result_text;
    public static Context m_context;
    private static VrAwakeEngine s_instance = null;
    private static final String KWS_SEARCH = "wakeup";

    private static final String KEYPHRASE = "你好小泰";
    public VrSpeechRecognizer recognizer;
    private HashMap<String, Integer> captions;

    public static synchronized VrAwakeEngine GetInstance() {
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
            Log.d(VRCommonDef.VRLogTag, "onPartialResult 1" + text);
            m_result_text.setText(text);
            recognizer.stop();
        }
//        else
//            m_result_text.setText(text);
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        m_result_text.setText("hahah");
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            Log.d(VRCommonDef.VRLogTag,"onResult"  + text);
            makeText(m_context, text, Toast.LENGTH_SHORT).show();
        }
        switchSearch(KWS_SEARCH);
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(VRCommonDef.VRLogTag,"onBeginningOfSpeech");
    }

    @Override
    public void onEndOfSpeech() {
        Log.d(VRCommonDef.VRLogTag,"onEndOfSpeech ");
        recognizer.stop();
    }

    private void switchSearch(String searchName) {

        //创建新线程
        //switchSearch(KWS_SEARCH);
        // If we are not spotting, start listening with timeout (10000 ms or 10 seconds).
        if (searchName.equals(KWS_SEARCH)) {
            Log.d(VRCommonDef.VRLogTag, "switchSearch " + searchName);
            recognizer.startListening(searchName);
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
                .setKeywordThreshold(1e-20f)
                // Use context-independent phonetic search, context-dependent is too slow for mobile
                .setBoolean("-allphone_ci", true)

                .getRecognizer();
        MainActivity.m_engine.put("sphinx",recognizer);
        recognizer.addListener(this);
        /* In your application you might not need to add all those searches.
          They are added here for demonstration. You can leave just one.
         */
//        // Create keyword-activation search.
        recognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
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
