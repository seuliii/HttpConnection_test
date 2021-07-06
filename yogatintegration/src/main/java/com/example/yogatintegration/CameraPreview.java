package com.example.yogatintegration;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;

import java.io.IOException;

/** A basic Camera preview class */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private int mCameraFacing;
    static boolean safeToTakePicture = false;
    static byte[] imagebytes;
    private ImageView imageView;
    static Bitmap imageBitmap;

    public CameraPreview(Context context, Camera camera, int cameraFacing,ImageView imageView) {
        super(context);
        mCamera = camera;
        this.imageView = imageView;
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);

        mCameraFacing = cameraFacing;

        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {

            //프리뷰위에 띄울 이미지
            imageView.setImageResource(R.drawable.yoga_pose);

            //카메라 세팅
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureSize(960,720);
            mCamera = Camera.open(mCameraFacing);
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();

        } catch (IOException e) {
            Log.d("camera Error", "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {

        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            // mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d("camera Error", "Error starting camera preview: " + e.getMessage());
        }
    }

    public void capture() {
        if (mCamera != null) {

            //셔터음 X
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(mCameraFacing, info);
            if (info.canDisableShutterSound) {
                mCamera.enableShutterSound(false);
            }

            mCamera.takePicture(null, rawCallback, jpegCallback);

            try {
                //촬영 끝나고도 preview 유지
                mCamera = Camera.open(mCameraFacing);
                mCamera.setDisplayOrientation(90);
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d("camera Error", "Error setting camera preview: " + e.getMessage());
            }
        }
    }

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
        }
    };

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(final byte[] data, Camera camera) {
            //찍힌 이미지 변수에 저장
            imagebytes = data;
            imageBitmap = BitmapFactory.decodeByteArray(imagebytes,0, imagebytes.length);
        }
    };


    //돌아간 사진 제대로 저장
    public static Bitmap rotateImage(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postRotate(270);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    public Bitmap getBitmap(){
        Log.d("seul","imageBitmap in CameraPreview is "+imageBitmap);
        Bitmap imageBitmap1 = rotateImage(imageBitmap);
        return imageBitmap1;
    }

    //picture & preview 기종에 맞는 사이즈 찾기
     /*
            if(parameters != null){
            List<Camera.Size> pictureSizeList = parameters.getSupportedPictureSizes();
            for (Camera.Size size : pictureSizeList) {        //지원하는 사진 크기

                Log.e("==PictureSize==", "width : " + size.width + "  height : " + size.height);
            }

            List<Camera.Size> previewSizeList = parameters.getSupportedPreviewSizes();
            for (Camera.Size size : previewSizeList) {        //지원하는 프리뷰 크기

                Log.e("==PreviewSize==", "width : " + size.width + "  height : " + size.height);
            }
            }*/

}