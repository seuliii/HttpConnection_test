package com.example.sendimagetojango;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.loopj.android.http.*;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    Bitmap imgBitmap;
    TextView textView;
    static String filename;
    CreateThumbnail createThumbnail = new CreateThumbnail();
    VideoRecording videoRecording = new VideoRecording();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         textView = findViewById(R.id.textView);
         String url = "http://192.168.1.11:8000/yogat/";

        ContentValues cValue = new ContentValues();
        cValue.put("value","success");

        NetworkTask networkTask = new NetworkTask(url,cValue);
        networkTask.execute();

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("MyCameraApp", "--startRecording--");
                filename = videoRecording.record();
            }
        });

        Button sendBtn = findViewById(R.id.sendbtn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String value = "seulll";
                RequestParams params = new RequestParams();
                params.put("value",value);

            }
        });
        //썸네일 추출 후 이미지 갤러리에 저장
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("MyCameraApp", "--getVideo--");
                List<Bitmap> list = new ArrayList<>();
                List<byte[]> byteImageList = new ArrayList<>();

                list = createThumbnail.create(filename);
                for(Bitmap b : list){
                    byteImageList.add(bitmapToByteArray(b));

                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 101){
            if(resultCode == RESULT_OK){
                Uri fileUri = data.getData();

                ContentResolver resolver = getContentResolver();

                try{
                    InputStream instream = resolver.openInputStream(fileUri);
                    imgBitmap = BitmapFactory.decodeStream(instream);

                    byte[] imagebytes;
                    imagebytes= bitmapToByteArray(imgBitmap);
                    Log.e("seul","!!!!!!!!!!"+imagebytes.length+"!!!!!!!!!");
                    RequestParams params = new RequestParams();
                    params.put("imagebytes",imagebytes);
                    instream.close();

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

   public byte[] bitmapToByteArray(Bitmap $bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        $bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] byteArray = stream.toByteArray();
        Log.e("seul","::::::::::::::"+byteArray.length+":::::::::;");
        return byteArray;
    }



     class NetworkTask extends AsyncTask<Void,Void,String> {
        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values){
            this.url = url;
            this.values = values;
        }

        //
        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            Log.e("seul","-----" + result + "-----");

            return result;
        }

        //doInBackground() 메소드 작업이 끝나면 onPostExcutd()로 결과
        //파라미터를 리턴하면서 그 리턴 값을 통해 스레드 작업이 끝났을 때 동작을 구현
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            textView.setText(s);
        }

    }
}