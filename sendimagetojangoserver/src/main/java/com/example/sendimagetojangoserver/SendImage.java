package com.example.sendimagetojangoserver;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

public class SendImage {

    TextView textView;

    public void send(Bitmap frames){

        byte[] bImage = null;

        String url = "http://192.168.1.11:8000/yogat/";
        ContentValues cValue = new ContentValues();
        cValue.put("value","succeess");

        NetworkTask networkTask = new NetworkTask(url,cValue);
        networkTask.execute();

        bImage = bitmapToByteArray(frames);
    }
/*

    RequestParams params = new RequestParams();
                    params.put("imagebytes",imagebytes);
                    instream.close();
*/


    public byte[] bitmapToByteArray(Bitmap $bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        $bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }




    //http통신
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
}

