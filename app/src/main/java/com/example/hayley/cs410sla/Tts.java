package com.example.hayley.cs410sla;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by niruiz3964 on 3/12/18.
 */

public class Tts extends Activity implements TextToSpeech.OnInitListener {

    //Variable Declaration
    private TextToSpeech tts;
    private TextView txt;
    private Button tButton;
    private final String UTTERANCEID = "SPEACHSYNTH";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tts = new TextToSpeech(this, this);

        //Using button 5 "transcribe button"
        tButton = (Button)findViewById(R.id.button5);

        //Saving the text from the text view to variable of EditText
        txt = (TextView) findViewById(R.id.speech_to_text_result);

        //Set an on click event to translate when the translate button
        //is clicked and call speakOut();
        tButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                speakOut();
            }
        });
    }



    //Close tts
    @Override
    public void onDestroy(){
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS){

            //Set the Locale for spanish
            Locale locSpanish = new Locale("spa", "Mexico");
            int result = tts.setLanguage(locSpanish);

            //error checking
            if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS", "Lang not supported!");
            }else{
                speakOut();
            }

        }else{
            Log.e("TTS", "INIT FAILED");
        }
    }

    //Grab the text from the textView that is storing the transcribing text and call
    //the function speak to say outloud the text.
    private void speakOut(){
        CharSequence text = txt.getText();
        tts.speak(text,TextToSpeech.QUEUE_FLUSH,null, UTTERANCEID);

    }

}
