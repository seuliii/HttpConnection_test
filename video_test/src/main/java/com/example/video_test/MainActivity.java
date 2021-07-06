package com.example.video_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaDescrambler;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    MediaPlayer player;
    MediaRecorder recorder;

    String filename;
    SurfaceHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurfaceView surface = new SurfaceView(this);
        holder = surface.getHolder();
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        FrameLayout frame = findViewById(R.id.container);
        frame.addView(surface);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startRecording();
            }
        });
    }


    public void startRecording(){
        if(recorder == null){
            recorder = new MediaRecorder();
        }
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        recorder.setOutputFile(filename);

        //MediaRecorder에 미리보기 화면을 보여줄 객체

        recorder.setPreviewDisplay(holder.getSurface());

        try{
            recorder.prepare();
            recorder.start();
        }catch (Exception e){
            e.printStackTrace();
        }
        recorder.release();
        recorder = null;
    }

    public void stopRecording(){
        if(recorder == null){
            recorder = null;
        }

        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;
        ContentValues values = new ContentValues(10);

        values.put(MediaStore.MediaColumns.TITLE,"RecordedVideo");
        values.put(MediaStore.Audio.Media.ALBUM,"Video Album");
        values.put(MediaStore.Audio.Media.ARTIST,"Mike");
        values.put(MediaStore.Audio.Media.DISPLAY_NAME,"Recorded Video");
        values.put(MediaStore.MediaColumns.DATE_ADDED,System.currentTimeMillis()/1000);
        values.put(MediaStore.MediaColumns.MIME_TYPE,"video/mp4");
        values.put(MediaStore.Audio.Media.DATA,filename);

        Uri videoUri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,values);

        if(videoUri == null){
            Log.d("SampleVideoRecorder","Video insert failed");
            return;
        }
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, videoUri));
    }
}