package com.example.mediaframework;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OKHttpConnection extends AsyncTask<String,Void,Void> {

    TextView textView;
    @Override
    protected Void doInBackground(String... imageString) {

        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("image", String.valueOf(imageString)).build();
        Log.d("seul","imageString in OKHTTP : "+ imageString);
        Request request = new Request.Builder()
                .url("http://192.168.1.41:8000/yogat/project/predict")
                .post(body)
                .build();


        client.newCall(request).enqueue(OKHttpCallback);
        return null;
    }

    private Callback OKHttpCallback = new Callback() {
        @Override
        public void onFailure(Call call,IOException e) {
            Log.d("seul","Error Message : "+e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String responseData = response.body().string();

            //textView.setText(responseData);
            Log.d("seul","responseData :" +responseData);
        }
    };

}
