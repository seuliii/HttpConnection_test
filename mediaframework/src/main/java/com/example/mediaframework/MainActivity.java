package com.example.mediaframework;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    MediaPlayer player;
    MediaRecorder recorder;
    static String filename;
    SurfaceHolder holder;
    static List<Bitmap> list = new ArrayList<>();
    TextView textView;
    static List<String> stringImageList = new ArrayList<>();

    CreateThumbnail createThumbnail = new CreateThumbnail();
    VideoRecording videoRecording = new VideoRecording();
    VideoView videoView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = findViewById(R.id.video_view);
        textView = findViewById(R.id.textView);


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

        //썸네일 추출
        Button button5 = findViewById(R.id.button5);
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyCameraApp", "--getVideo--");
                list = createThumbnail.create(filename);
                /*for (Bitmap b : list) {
                    save(b);
                }*/
            }
        });

        Button button6 = findViewById(R.id.button6);
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send(list);
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

    public void showToast (String message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    public File getOutputFile () {
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


    public void stopRecording () {
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

    public void send (List<Bitmap> list) {
/*

        ContentResolver resolver = getContentResolver();
        InputStream instream = resolver.openInputStream(fileUri);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap imgBitmap = BitmapFactory.decodeStream(instream);
        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
*/


        byte[] byteImage;

        OKHttpConnection okHttpConnection = new OKHttpConnection();
        for (Bitmap bitmap : list) {
            byteImage = bitmapToByteArray(bitmap);
            try {
                String imageString = Base64Util.encode(byteImage);
                stringImageList.add(imageString);
                Log.d("seul","Image made of String value is" + imageString);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        okHttpConnection.execute(stringImageList);
/*

        Log.d("check Base64", "!!!!!!!!!!" + imageString);

        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
*/
    }

    public byte[] bitmapToByteArray (Bitmap $bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        $bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public void save (Bitmap bitmap){
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

    class VideoRecording extends AsyncTask<Void, Void, Void> {
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
    public class OKHttpConnection extends AsyncTask<List<String>,Void,Void> {

        TextView textView;
        @Override
        protected Void doInBackground(List<String>... imageString) {

            OkHttpClient client = new OkHttpClient();
            RequestBody body = null;
            for(List<String> imagelist : imageString) {
                for (String image : imagelist) {
                    body = new FormBody.Builder()
                            .add("image", image).build();
                    Log.d("seul", "imageString in OKHTTP : " + image);
                }
            }
            Request request = new Request.Builder()
                    .url("http://192.168.1.41:8000/yogat/project/predict")
                    .post(body)
                    .build();


            client.newCall(request).enqueue(OKHttpCallback);
            return null;
        }

        private Callback OKHttpCallback = new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
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

}
