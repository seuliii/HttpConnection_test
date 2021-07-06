package com.example.frontvideorecording;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.content.ContentValues;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.nio.channels.AsynchronousChannelGroup;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button recordBtn;
    Button stopBtn;
    Button saveBtn;
    Button playBtn;
    Button playstopBtn;
    MediaRecorder recorder;
    MediaPlayer player;
    String filename;
    SurfaceHolder holder;
    int mCameraFacing;
    CameraPreview mPreview;
    FrameLayout frame;
    Camera mCamera;

    VideoRecording videoRecording = new VideoRecording();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //mCameraFacing = Camera.CameraInfo.CAMERA_FACING_BACK; // 최초 카메라 상태는 후면카메라로 설정
        mCamera = getCameraInstance();
        SurfaceView surface = new SurfaceView(this);
        holder = surface.getHolder();

        frame = findViewById(R.id.container);



        recordBtn = findViewById(R.id.button);
        recordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                videoRecording.execute();
            }
        });
        stopBtn = findViewById(R.id.button2);
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRecording();
            }
        });
        playBtn = findViewById(R.id.button4);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPlay();
            }
        });
        playstopBtn = findViewById(R.id.button5);
        playstopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopPlay();
            }
        });

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
                        showToast("허용된 권한 갯수: " + permissions.size());
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

    void showToast(String text){
        Toast.makeText(this, text,Toast.LENGTH_LONG).show();
    }

    void stopRecording () {
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
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance

        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }
    void startPlay () {
        mCameraFacing = (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) ?
                Camera.CameraInfo.CAMERA_FACING_FRONT
                : Camera.CameraInfo.CAMERA_FACING_BACK;

        mPreview = new CameraPreview(this,mCamera,mCameraFacing);
        frame.addView(mPreview);

        if (player == null) {
            player = new MediaPlayer();
        }

        try {
            player.setDataSource(filename);
            player.setDisplay(holder);
            Log.d("MyCameraApp", "--before prepare--");
            player.prepare();
            Log.d("MyCameraApp", "--before start--");
            player.start();
        } catch (Exception e) {
            Log.d("MyCameraApp", "--Exception--");
            e.printStackTrace();
        }
    }
    public void stopPlay() {
        if (player == null) return;
        player.stop();
        player.release();
        player = null;
    }
    public File getOutputFile () {
        java.io.File mediaFile = null;
        try {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "MyApp");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("MyCameraApp", "failed to create directory");
                    return null;
                }
            }
            mediaFile = new File(mediaStorageDir.getPath() + java.io.File.separator + "recorded.mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaFile;
    }
    class VideoRecording extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            if (recorder == null) {
                recorder = new MediaRecorder();
            }
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);
            recorder.setOutputFile(filename);
            // recorder.setPreviewDisplay(holder.getSurface());

            try {
                recorder.prepare();
                recorder.start();
            } catch (Exception e) {
                e.printStackTrace();

                recorder.release();
                recorder = null;
            }
            return null;
        }
    }

}
