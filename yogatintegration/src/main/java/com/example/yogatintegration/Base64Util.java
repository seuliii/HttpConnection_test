package com.example.yogatintegration;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Util {


    public static String encode(byte[] bytes) throws UnsupportedEncodingException{
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] bitmapToByteArray (Bitmap $bitmap){

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        $bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

}
