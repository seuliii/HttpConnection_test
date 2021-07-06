package com.example.yogatintegration;

import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import java.util.Locale;

public class TextToSpeech_yogat{

    private TextToSpeech tts = initTTS();

    private TextToSpeech initTTS(){
        tts = new TextToSpeech(MainActivity.context,new TextToSpeech.OnInitListener(){
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int language = tts.setLanguage(Locale.KOREAN);
                    if(language == TextToSpeech.LANG_MISSING_DATA || language == TextToSpeech.LANG_NOT_SUPPORTED){
                        Toast.makeText(MainActivity.context,"지원하지 않는 언어입니다",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(MainActivity.context,"작업실패",Toast.LENGTH_LONG).show();

                }
            }
        });

        return tts;
    }
    public void sayText(String responseData){
       /* if(responseData == null && responseData == ""){
            Toast.makeText(MainActivity.context,"null",Toast.LENGTH_LONG).show();
        }else{
            tts.speak(responseData, TextToSpeech.QUEUE_FLUSH, null);
            Toast.makeText(MainActivity.context, responseData,Toast.LENGTH_LONG).show();
        }*/
        tts.speak(responseData, TextToSpeech.QUEUE_FLUSH, null);
    }
    public void stopTTS() {
        if (tts != null) {
            tts.shutdown();
            tts.stop();
            tts = null;
        }
    }

}
