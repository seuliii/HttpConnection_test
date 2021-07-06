package com.example.freemode;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

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


    //camera preview
    private Camera mCamera;
    private int mCameraFacing;
    private RelativeLayout preview;
    private CameraPreview mCameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
            }

        };

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("카메라 권한이 필요합니다")
                .setDeniedMessage("카메라 권한을 거부하셨습니다.\n 본 앱을 이용하실 수 없습니다.")
                .setPermissions(Manifest.permission.CAMERA)
                .check();


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
       //onCreate

        //촬영 버튼
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerInTakingPictures();
            }
        });

        //정지 버튼
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //타이머 해제
                tt.cancel();
            }
        });
    }


    //Django서버에 이미지를 전송
    public void sendToServer() {
        try {

            //http통신에선 데이터를 주고 받을 때 String형식을 통해 주고받을 수 있음
            //Base64를 통해서 byteArray를 String으로 변환해서 전송
            imageBitmap =  mCameraView.getBitmap();
            pictureByteArr = bitmapToByteArray(imageBitmap);
            imageString = Base64Util.encode(pictureByteArr);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        okHttpConnection.sendToServer(imageString);
        responseData = OKHttpConnection.responseData;
        Log.d("seul","responseData in sendToServer method in Main is" + responseData);
        intent.putExtra("responseData",responseData);

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
                pictureByteArr = CameraPreview.imagebytes;
                sendToServer();
                Log.d("seul","responseData in Timer is " + responseData  );
                tts.sayText(responseData);
            }
        };
        //timer 설정 (해야할 일 , 몇 초후에 시작할지, 타이머 주기)
        Timer timer = new Timer();
        timer.schedule(tt,10000,10000);
    }


    public byte[] bitmapToByteArray (Bitmap $bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        $bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

}
