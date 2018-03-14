package com.example.hayley.cs410sla;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.util.Base64;
import com.google.api.services.speech.v1beta1.*;
import com.google.api.services.speech.v1beta1.model.RecognitionAudio;
import com.google.api.services.speech.v1beta1.model.RecognitionConfig;
import com.google.api.services.speech.v1beta1.model.SpeechRecognitionResult;
import com.google.api.services.speech.v1beta1.model.SyncRecognizeRequest;
import com.google.api.services.speech.v1beta1.model.SyncRecognizeResponse;

import org.apache.commons.io.IOUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import android.speech.tts.TextToSpeech;

public class TranscribeFileActivity extends AppCompatActivity {

    //you'll need to add your api key - you can just add it here even
    private APIKey key = new APIKey();
    private final String CLOUD_API_KEY = key.APIKey;
    private Translate translate = new Translate();
    final private String ENGLISH = "en";
    final private String SPANISH = "es";
    final private String FRENCH = "fr";
    String text = null;
    private TextToSpeech tts;
    private final String UTTERANCEID = "SPEECHSYNTH";
    String language = null;
    String country = null;
    String toLang = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcribe_file);

        final Button browseButton = (Button) findViewById(R.id.browse_button);
        final Button translateButton = (Button) findViewById(R.id.translate_button);
        browseButton.setEnabled(false);
        translateButton.setEnabled(false);
        Spinner spinner = (Spinner) findViewById(R.id.pick_language_spinner);
        //Create ArrayAdapter using string array of options and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.languages_array, android.R.layout.simple_spinner_dropdown_item);
        //Specify layout to use when list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply adapter to spinner
        spinner.setAdapter(adapter);
        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                translateButton.setEnabled(true);
                Intent filePicker = new Intent(Intent.ACTION_GET_CONTENT);
                filePicker.setType("storage/Android/data/com.example.hayley.cs410sli/files/*");
                startActivityForResult(filePicker, 1);
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                browseButton.setEnabled(true);
                String value = parent.getItemAtPosition(position).toString();
                if(value.equals("Spanish")) {
                    language = "spa";
                    country = "US";
                    toLang = SPANISH;
                } else if(value.equals("French")) {
                    language = "fr";
                    country = "FR";
                    toLang = FRENCH;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if(status == TextToSpeech.SUCCESS) {
                            Locale locSpanish = new Locale(language, country);
                            int result = tts.setLanguage(locSpanish);

                            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                Log.e("TTS", "Lang not supported!");
                            } else {
                                speakOut();
                            }
                        } else {
                            Log.e("TTS", "INIT FAILED");
                        }
                    }
                    private void speakOut() {
                        CharSequence translation = text;
                        tts.speak(translation, TextToSpeech.QUEUE_FLUSH, null, UTTERANCEID);
                    }
                });
            }
        });
    }

    public void onPause() {
        if(tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            final Uri soundUri = data.getData();
            String path = data.getData().getPath();
            String[] parts = path.split("/");
            final String filePath = parts[parts.length - 1];
            Log.d("File path", filePath);
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView speechToTextResult = (TextView) findViewById(R.id.speech_to_text_result);
                                speechToTextResult.setText("Transcribing audio...");
                            }
                        });
                        InputStream stream = getContentResolver().openInputStream(soundUri);
                        byte[] audioData = IOUtils.toByteArray(stream);
                        stream.close();

                        String base64EncodedData = Base64.encodeBase64String(audioData);

                        MediaPlayer player = new MediaPlayer();
                        player.setDataSource(TranscribeFileActivity.this, soundUri);
                        player.prepare();
                        player.start();
                        player.setOnCompletionListener(
                                new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mediaPlayer) {
                                        mediaPlayer.release();
                                    }
                                }
                        );
                        processSpeech(base64EncodedData, "LINEAR16", 44100);
                    } catch(FileNotFoundException e) {
                        e.printStackTrace();
                    } catch(IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    }

    private void processSpeech(String data, String encoding, int sampleRate) throws IOException {
        Speech speechService = new Speech.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(),
                null
        ).setSpeechRequestInitializer(new SpeechRequestInitializer(CLOUD_API_KEY)).build();
        RecognitionConfig recognitionConfig = new RecognitionConfig();
        recognitionConfig.setLanguageCode("en-US");
        recognitionConfig.setEncoding(encoding);
        recognitionConfig.setSampleRate(sampleRate);
        RecognitionAudio recognitionAudio = new RecognitionAudio();
        recognitionAudio.setContent(data);

        //create speech api request
        SyncRecognizeRequest request = new SyncRecognizeRequest();
        request.setConfig(recognitionConfig);
        request.setAudio(recognitionAudio);
        Log.d("Request config: ", request.getConfig().toString());
        Log.d("Request audio: ", request.getAudio().toString());


        //generate response
        SyncRecognizeResponse response = speechService.speech().syncrecognize(request).execute();
        Log.d("Response: ", response.toString());
        //Extract transcript

        SpeechRecognitionResult result = response.getResults().get(0);
        final String transcript = result.getAlternatives().get(0).getTranscript();
        //Take the text and convert it
        Log.d("To language", toLang);
        text = translate.translate(transcript,ENGLISH,toLang);
        Log.d("Text", text);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView speechToTextResult = (TextView) findViewById(R.id.speech_to_text_result);
                speechToTextResult.setText(text);
            }
        });
    }
}
