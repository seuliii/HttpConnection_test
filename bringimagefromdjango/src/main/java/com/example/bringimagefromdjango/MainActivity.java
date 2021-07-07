package com.example.bringimagefromdjango;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String responseData;
    private TextView textView;
    private OKHttpConnection okHttpConnection;
    Callback OKHttpCallback;
    private ImageView imageView;
    private byte[] imageByteArr;
    private Bitmap image;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
//        button.setEnabled(false);
        button.setOnClickListener(this);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);
        okHttpConnection = new OKHttpConnection();
        okHttpConnection.sendToServer(getOKHttpCallback());
//        responseData = OKHttpConnection.responseData;


        /*
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    //imageByteArr = responseData.getBytes();
                    Log.d("seul","ImageByteArr is " + imageByteArr);
                    imageByteArr = Base64Util.decode(responseData);
                    Log.d("seul","Length of imageByteArr is " + imageByteArr.length);
                    image = BitmapFactory.decodeByteArray(imageByteArr, 0, imageByteArr.length);

                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
//                textView.setText(responseData);
                imageView.setImageBitmap(image);

            }
        });
         */
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            try {
                if (responseData != null) {
                    //imageByteArr = responseData.getBytes();
                    Log.d("seul", "ImageByteArr is " + imageByteArr);
                    imageByteArr = Base64Util.decode(responseData);
                    Log.d("seul", "Length of imageByteArr is " + imageByteArr.length);
                    image = BitmapFactory.decodeByteArray(imageByteArr, 0, imageByteArr.length);
                } else {
                    Toast.makeText(MainActivity.this, "데이터 로딩장!", Toast.LENGTH_LONG).show();
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
//                textView.setText(responseData);
            imageView.setImageBitmap(image);
        }
    }

    private Callback getOKHttpCallback() {
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

        return OKHttpCallback;
    }


}
