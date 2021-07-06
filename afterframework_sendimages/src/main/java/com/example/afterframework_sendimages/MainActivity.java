package com.example.afterframework_sendimages;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //layout
    Button recordingBtn;
    Button createFramesBtn;
    Button sendImageBtn;
    Button stopRecoringBtn;
    VideoView videoView;
    FrameLayout videoContainer;


    //recording
    MediaRecorder recorder;
    MediaPlayer player;
    String filename;
    SurfaceHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recordingBtn = findViewById(R.id.stopRecording);
        createFramesBtn = findViewById(R.id.createFrames);
        sendImageBtn = findViewById(R.id.sendBtn);
        stopRecoringBtn = findViewById(R.id.stopRecording);
        videoContainer = findViewById(R.id.container);

        //recording
        SurfaceView surface = new SurfaceView(this);
        holder = surface.getHolder();
        videoContainer.addView(surface);

        recordingBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                recordingVideo();
            }
        });

        stopRecoringBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });


        createFramesBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //createFrames();
            }
        });
        sendImageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //sendImages();
            }
        });
        videoContainer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });
        //java.lang.RuntimeException: setAudioSource failed.
        //권한 허용
        File file = getOutputFile();
        if (file != null) {
            filename = file.getAbsolutePath();
        }
        AndPermission.with(this)
                .runtime()
                .permission(
                        Permission.CAMERA,
                        Permission.RECORD_AUDIO,
                        Permission.READ_EXTERNAL_STORAGE,
                        Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        showToast("허용된 권한 갯수 : " + permissions.size());
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        showToast("거부된 권한 갯수 : " + permissions.size());
                    }
                })
                .start();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode ==  200 && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
        }

    }

    void recordingVideo() {


        if (recorder == null) {
            recorder = new MediaRecorder();
        }
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
        recorder.setOutputFile(filename);
        recorder.setPreviewDisplay(holder.getSurface());

        try {
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            recorder.release();
            recorder = null;
        }
    }
    public void showToast(String text){
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }

    //video파일 저장
    public File getOutputFile() {
        File mediaFile = null;
        try {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "MyApp");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("MyCameraApp", "failed to create directory");
                    return null;
                }
            }
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "recorded.mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaFile;
    }

    public void stopRecording() {
        if (recorder == null) {
            return;
        }

        recorder.stop();
        recorder.reset();
        recorder.release();
        recorder = null;

        ContentValues values = new ContentValues(10);

        values.put(MediaStore.MediaColumns.TITLE, "RecordedVideo");
        values.put(MediaStore.Audio.Media.ALBUM, "Video Album");
        values.put(MediaStore.Audio.Media.ARTIST, "Mike");
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, "RecordedVideo");
        values.put(MediaStore.MediaColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "Video/mp4");
        values.put(MediaStore.Audio.Media.DATA, filename);

        Uri videoUri = getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
        if (videoUri == null) {
            Log.d("SampleVideoRecorder", "Video insert failed.");
            return;
        }

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, videoUri));

    }

}