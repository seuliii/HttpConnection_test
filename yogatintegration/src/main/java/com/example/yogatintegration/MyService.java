package com.example.yogatintegration;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.UnsupportedEncodingException;

public class MyService extends Service {

    String responseData;
    TextToSpeech_yogat tts;
    OKHttpConnection okHttpConnection;
    String imageString;
    @Override
    public IBinder onBind(Intent intent) {
        //액티비티와 데이터를 주고받을 떄 사용하는 메서드
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        okHttpConnection = new OKHttpConnection();
        tts = new TextToSpeech_yogat();

        try{
            //Thread.sleep(10000);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        responseData = intent.getStringExtra("responseData");
        if(intent == null){
            Log.d("seul","intent가 null이야");
            return Service.START_STICKY;

        }else{
            /*if(responseData == null && responseData == ""){
                Toast.makeText(MainActivity.context,"null",Toast.LENGTH_LONG).show();
            }else{
                tts.sayText(responseData);
                Toast.makeText(MainActivity.context, responseData,Toast.LENGTH_LONG).show();
            }*/

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void sayText(){

        responseData = OKHttpConnection.responseData;
        Toast.makeText(MainActivity.context, responseData,Toast.LENGTH_LONG).show();


        tts.sayText(responseData);
        Log.d("seul","responseData in Service : " + responseData);

    }

    public void sendToServer(Intent intent) {
        try {
            //http통신에선 데이터를 주고 받을 때 String형식을 통해 주고받을 수 있음
            //Base64를 통해서 byteArray를 String으로 변환해서 전송


            byte[] pictureByteArr = intent.getByteArrayExtra("picByteArr");
            imageString = Base64Util.encode(pictureByteArr);
            Log.d("seul","imageString in Service : " + imageString);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        okHttpConnection.sendToServer(imageString);

        responseData = OKHttpConnection.responseData;
        Intent broadIntent = new Intent ();
        broadIntent.putExtra("responseData",responseData);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadIntent);
        Log.d("seul","responseData in Service : " + responseData);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("seul","Service onDestroy");
    }
}
