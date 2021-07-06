package com.example.httpconnection_test;

import android.content.ContentValues;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class RequestHttpURLConnection {

    public String request(String _url, ContentValues _params){

        HttpURLConnection urlConn = null;
        StringBuffer sbParams = new StringBuffer();

        //string Buffer에 파라미터 연결

        if(_params == null)
            sbParams.append("");
        else{
            boolean isAnd = false;

            String key;
            String value;

            for(Map.Entry<String,Object> parameter : _params.valueSet()){
                key = parameter.getKey();
                value = parameter.getValue().toString();

                if(isAnd){
                    sbParams.append("&");
                }

                sbParams.append(key).append("=").append(value);

                if(!isAnd)
                    if(_params.size() >= 2)
                        isAnd = true;
            }
        }

        try{
            URL url = new URL(_url);
            urlConn = (HttpURLConnection) url.openConnection();

            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Accept-Charset","UTF-8");
            urlConn.setRequestProperty("Context_Type","application/x-www-form-urlencoded;charset=UTF-8");

            String strParams = sbParams.toString();
            OutputStream os = urlConn.getOutputStream();
            os.write(strParams.getBytes("UTF-8"));
            os.flush();
            os.close();

            if(urlConn.getResponseCode() != HttpURLConnection.HTTP_OK)
                return urlConn.getResponseCode() + "";


            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream(),"UTF-8"));

            String line="";
            String page="*^^@@@@*";

            while((line=reader.readLine()) != null){
                page += line;
            }
            return page;

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(urlConn!= null)
                urlConn.disconnect();
        }
        return "(-:";
    }
}
