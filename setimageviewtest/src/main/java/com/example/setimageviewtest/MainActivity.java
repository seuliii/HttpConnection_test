package com.example.setimageviewtest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Camera;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    SurfaceView cameraSurfaceView;
    CameraPreview cameraPreview;
    int mCameraFacing;
    Camera mCamera;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCamera = Camera.open();
        cameraSurfaceView = findViewById(R.id.cameraSurfaceView);

        mCameraFacing = (mCameraFacing == Camera.CameraInfo.CAMERA_FACING_BACK) ?
                Camera.CameraInfo.CAMERA_FACING_FRONT
                : Camera.CameraInfo.CAMERA_FACING_BACK;

        cameraPreview = new CameraPreview(this, mCamera, mCameraFacing);


        cameraSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

                cameraPreview.surfaceCreated(surfaceHolder);
                //imageView.invalidate();

                return;
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                cameraPreview.surfaceChanged(surfaceHolder,i,i1,i2);
            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {

                cameraPreview.surfaceDestroyed(surfaceHolder);
            }
        });
    }
}