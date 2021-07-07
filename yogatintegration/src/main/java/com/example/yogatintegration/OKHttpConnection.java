package com.example.yogatintegration;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OKHttpConnection{
    OkHttpClient client = new OkHttpClient();
    RequestBody body = null;
    Request request;
    static String responseData;


    public void sendToServer(String imageString){


        Callback OKHttpCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("seul", "Error Message : " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                responseData = response.body().string();
                Log.d("seul", "responseData in OkhttpConnection : " + responseData);

            }
        };



        body = new FormBody.Builder()
                .add("image", imageString).build();
        request = new Request.Builder()
                .url("http://192.168.0.10:8000/yogat/userImage_save")
                .post(body)
                .build();
        client.newCall(request).enqueue(OKHttpCallback);
/*
        Handler handler = new MainHandler();
        Message message =handler.obtainMessage();
        Bundle bundle = new Bundle();
        bundle.putString("responseData",responseData);
        message.setData(bundle);

        handler.sendMessage(message);*/


    }
}