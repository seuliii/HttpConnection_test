package com.example.yogatintegration;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class CreateThumbnail {

    public static List<Bitmap> create(String filename) {
        MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
        Bitmap bitmap = null;
        List<Bitmap> list = new ArrayList<Bitmap>();

        try {
            mediaMetadataRetriever.setDataSource(filename);
            String time =
                    mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

            Log.d("MycameraApp","time:"+time);
            long timeInmillisec = Long.parseLong( time );

            for(int i = 1; i<=(timeInmillisec/1000);i++){
                for (int j = 0; j < 3; j++) {
                    bitmap = mediaMetadataRetriever.getFrameAtTime(i*1000000, MediaMetadataRetriever.OPTION_CLOSEST);
                    list.add(bitmap);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (mediaMetadataRetriever != null) mediaMetadataRetriever.release();
        }
        return list;
    }
}
