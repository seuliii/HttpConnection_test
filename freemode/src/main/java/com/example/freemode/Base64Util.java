package com.example.freemode;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public class Base64Util {


    public static String encode(byte[] bytes) throws UnsupportedEncodingException{
        return Base64.getEncoder().encodeToString(bytes);
    }
}
