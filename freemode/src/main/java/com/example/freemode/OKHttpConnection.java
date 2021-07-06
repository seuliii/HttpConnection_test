package com.example.freemode;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

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
                .url("http://192.168.0.9:8000/yogat/free_mode")
                .post(body)
                .build();
        client.newCall(request).enqueue(OKHttpCallback);


    }
}