package com.example.yogatintegration;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    //MainActivity
    static Context context;
    private TimerTask tt;

    //send image to Django
    static String imageString;
    private String responseData;
    private byte[] pictureByteArr;
    private Bitmap imageBitmap;
    private Intent intent;

    //layout
    private Button captureBtn;
    private Button stopBtn;
    private ImageView imageView;

    //object
    private OKHttpConnection okHttpConnection;
    private TextToSpeech_yogat tts;
    Handler handler;


    //camera preview
    private Camera mCamera;
    private int mCameraFacing;
    private RelativeLayout preview;
    private CameraPreview mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        //init
        mCamera = getCameraInstance();
        context = getApplicationContext();
        okHttpConnection = new OKHttpConnection();
        tts = new TextToSpeech_yogat();
        intent = new Intent();

        //layout init
        imageView = findViewById(R.id.imageView);
        preview = findViewById(R.id.preview);
        captureBtn = findViewById(R.id.captureBtn);
        stopBtn = (Button)findViewById(R.id.stopBtn);

        //카메라 프리뷰
        //mCameraFacing = 전면 or 후면을 결정
        mCameraFacing = (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) ?
                Camera.CameraInfo.CAMERA_FACING_FRONT
                : Camera.CameraInfo.CAMERA_FACING_BACK;
        mCameraView = new CameraPreview(this, mCamera, mCameraFacing,imageView);

        preview.addView(mCameraView);
        imageView.setAlpha(90); //이미지 투명도
        imageView.bringToFront();   //imageView 최상단으로 올리기


        //촬영 버튼
        captureBtn.setOnClickListener(this);
        //정지 버튼
        stopBtn.setOnClickListener(this);
        //handler = new MainHandler();
    }   //onCreate

    //Django서버에 이미지를 전송
    public void sendToServer() {
        try {

            //http통신에선 데이터를 주고 받을 때 String형식을 통해 주고받을 수 있음
            //Base64를 통해서 byteArray를 String으로 변환해서 전송
            imageBitmap =  mCameraView.getBitmap();
            pictureByteArr = Base64Util.bitmapToByteArray(imageBitmap);
            imageString = Base64Util.encode(pictureByteArr);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        okHttpConnection.sendToServer(imageString);
        responseData = OKHttpConnection.responseData;
        Log.d("seul","responseData in sendToServer method in Main is" + responseData);




    }

    public Camera getCameraInstance() {
        Camera c = null;
        try {
            // attempt to get a Camera instance
            c = Camera.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // returns null if camera is unavailable
        return c;
    }

    //timer가 끝난 후 사진 촬영
    public void timerInTakingPictures(){

        //timer가 실행될 때 해야할 일
        tt = new TimerTask() {
            @Override
            public void run() {
                mCameraView.capture();
                sendToServer();
                Log.d("seul","responseData in Timer is " + responseData  );

                tts.sayText(responseData);
            }
        };
        //timer 설정 (해야할 일 , 몇 초후에 시작할지, 타이머 주기)
        Timer timer = new Timer();
        timer.schedule(tt,10000,10000);
    }



    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.captureBtn){

            timerInTakingPictures();

        }else if(v.getId() == R.id.stopBtn){
            tt.cancel();
        }else{

        }
    }
    private void checkPermission(){
        //권한 허가 (onCreate메소드 안에서 호출해야 함)
        AndPermission.with(MainActivity.this)
                .runtime()
                .permission(
                        com.yanzhenjie.permission.runtime.Permission.CAMERA,
                        com.yanzhenjie.permission.runtime.Permission.RECORD_AUDIO,
                        com.yanzhenjie.permission.runtime.Permission.READ_EXTERNAL_STORAGE,
                        com.yanzhenjie.permission.runtime.Permission.WRITE_EXTERNAL_STORAGE)
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Toast.makeText(MainActivity.this,"허용된 권한 갯수: " + permissions.size(),Toast.LENGTH_LONG).show();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Toast.makeText(MainActivity.this,"거부된 권한 갯수: " + permissions.size(),Toast.LENGTH_LONG).show();
                        //권한 허가 거부하면 어플을 사용할 수 없습니다 라는 안내문 표시
                    }
                })
                .start();
    }
}
