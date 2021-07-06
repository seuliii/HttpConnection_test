package com.example.checkcamerasize;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
    private static final String TAG = "MyService";
    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate()호출됨");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand() 호출");

        if(intent == null){
            return Service.START_STICKY;

        }else{
            processCommand(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void processCommand(Intent intent){
        String command = intent.getStringExtra("command:");
        String name = intent.getStringExtra("name");

        Log.d(TAG,"command : " + command + "name : " + name);

        for(int i = 0; i<5; i++){
            try{
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
            Log.d(TAG,"Waition : "+ i + "seconds");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}