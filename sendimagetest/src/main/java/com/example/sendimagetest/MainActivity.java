    package com.example.sendimagetest;


import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

    TextView textView;
    ImageView imageView;
    static String imageString;
    static List<Bitmap> frameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.textView);
        imageView = findViewById(R.id.imageView);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                getImageFromGallery();
            }
        });

        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                OKHttpClient okHttpClient = new OKHttpClient();
                okHttpClient.execute();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 200){
            if(resultCode == RESULT_OK){
                Uri fileUri = data.getData();
                try{
                    ContentResolver resolver = getContentResolver();
                    InputStream instream = resolver.openInputStream(fileUri);
                    ByteArrayOutputStream baos  = new ByteArrayOutputStream();
                    Bitmap imgBitmap = BitmapFactory.decodeStream(instream);
                    imgBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);

                    imageView.setImageBitmap(imgBitmap);
                    byte[] imageBytes = baos.toByteArray();



                    imageString = Base64Util.encode(imageBytes);
                    Log.d("check Base64","!!!!!!!!!!"+ imageString);


                    /*
                    String url = "http://192.168.1.11:8000/yogat/save_image";
                    ContentValues cValue = new ContentValues();
                    cValue.put("image",imageString);
                    NetworkTask networkTask = new NetworkTask(url,cValue);
                    networkTask.execute();

*/


                /*
                    try{


                        Log.d("checkImage","++++++"+instream+"+++++");
                        Log.d("checkImage","!!!!!!"+imgBitmap+"!!!!!!!!");

                try{
                ContentResolver resolver = getContentResolver();
                InputStream instream = resolver.openInputStream(fileUri);
                Bitmap imgBitmap = BitmapFactory.decodeStream(instream);
                String result = createCopyAndReturnRealPath(getApplicationContext(),fileUri);
                imageView.setImageBitmap(imgBitmap);
                DoFileUpload doFileUpload = new DoFileUpload();
                 dos = doFileUpload.request(result);

                        //byte[]로 보내기
                        imageView.setImageBitmap(imgBitmap);

                        imagebytes= bitmapToByteArray(imgBitmap);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        String url = "http://192.168.1.11:8000/yogat/save_image";
                        ContentValues cValue = new ContentValues();
                        //cValue.put("image", imagebytes);
                        cValue.put("test","");

                        NetworkTask networkTask = new NetworkTask(url,cValue);
                        networkTask.execute();

                        Log.d("seul","!!!!!!!!!!"+imagebytes.length+"!!!!!!!!!");
                        instream.close();*/
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

/*
    public static String createCopyAndReturnRealPath(Context context, Uri uri) {
        final ContentResolver contentResolver = context.getContentResolver();
        if (contentResolver == null) return null;

        String filePath = context.getApplicationInfo().dataDir + File.separator +
                System.currentTimeMillis();

        File file = new File(filePath);

        try {
            InputStream inputStream = contentResolver.openInputStream(uri);
            if(inputStream == null) return null;

            OutputStream outputStream = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int len;
            while((len = inputStream.read(buf))>0)
                outputStream.write(buf,0,len);

            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
            return null;
        }
        return file.getAbsolutePath();
    }*/
    public void getImageFromGallery(){

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,200);

    }



   /* //http통신
    public class NetworkTask extends AsyncTask<Void,Void,String> {
        private String url;
        private ContentValues values;

        public NetworkTask(String url, ContentValues values) {
            this.url = url;
            this.values = values;
        }

        protected String doInBackground(Void... params) {

            String result; // 요청 결과를 저장할 변수.
            RequestHttpURLConnection requestHttpURLConnection = new RequestHttpURLConnection();
            result = requestHttpURLConnection.request(url, values); // 해당 URL로 부터 결과물을 얻어온다.
            Log.e("seul", "-----" + result + "-----");

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
*/
    public class OKHttpClient extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {

            OkHttpClient client = new OkHttpClient();
            RequestBody body = new FormBody.Builder()
                    .add("image",imageString).build();
            Log.d("seul","imageString in OKHTTP : "+ imageString);
            Request request = new Request.Builder()
                    .url("http://192.168.1.41:8000/yogat/project/predict")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(OKHttpCallback);
            return null;
        }

        private Callback OKHttpCallback = new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d("seul","Error Message : "+e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    String responseData = response.body().string();
                    textView.setText(responseData);
                    Log.d("seul","responseData :" +responseData);
                }
            };
        }
}
