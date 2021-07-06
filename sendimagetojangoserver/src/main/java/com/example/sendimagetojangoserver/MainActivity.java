package com.example.sendimagetojangoserver;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
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
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    MediaPlayer player;
    MediaRecorder recorder;
    static String filename;
    SurfaceHolder holder;

    private static final String TAG = "MainActivity";

    CreateThumbnail createThumbnail = new CreateThumbnail();
    VideoRecording videoRecording = new VideoRecording();
    StartPlaying startPlaying = new StartPlaying();

    VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.video_view);


        SurfaceView surface = new SurfaceView(this);
        holder = surface.getHolder();

        FrameLayout frame = findViewById(R.id.container);
        frame.addView(surface);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("MyCameraApp", "--startRecording--");
                videoRecording.execute();
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();

            }
        });

      /*  Button button3 = findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlaying.execute();
            }
        });

        Button button4 = findViewById(R.id.button4);
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlay();
            }
        });
*/
        //썸네일 추출 후 이미지 갤러리에 저장
        Button button5 = findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyCameraApp", "--getVideo--");
                List<Bitmap> list = new ArrayList<>();
                list = createThumbnail.create(filename);
                //Log.d("MyCameraApp","!!!!!!!!!"+bitmap+"!!!!!!!!!");

                for(Bitmap b : list){
                    save(b);
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
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

    public void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

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

    public void stopPlay() {
        if (player == null) return;
        player.stop();
        player.release();
        player = null;
    }

    private void saveBitmapToJpg(Bitmap bitmap,String name){
        FileOutputStream out = null;
        try{
            out = new FileOutputStream( name + ".png");
            bitmap.compress(Bitmap.CompressFormat.PNG,100,out);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void save(Bitmap bitmap) {
        String outUriStr = MediaStore.Images.Media.insertImage(
                getContentResolver(),
                bitmap,
                "Captured Image",
                "Captured Image using Camera.");

        if (outUriStr == null) {
            Log.d("SampleCapture", "Image insert failed.");
            return;
        } else {
            Uri outUri = Uri.parse(outUriStr);
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, outUri));
        }
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
            recorder.setPreviewDisplay(holder.getSurface());

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

    class StartPlaying extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {

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
            return null;
        }
    }

}