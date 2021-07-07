package com.example.yogatintegration;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class MainHandler extends Handler {


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        Bundle bundle = msg.getData();
        if(bundle.getString("responseData") == null) {
            Toast.makeText(MainActivity.context, "로딩 중", Toast.LENGTH_LONG).show();
        }
    }
}
