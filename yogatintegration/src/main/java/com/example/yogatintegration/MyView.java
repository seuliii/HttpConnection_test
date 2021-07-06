package com.example.yogatintegration;

import android.content.Context;
import android.graphics.Canvas;
import android.media.Image;
import android.view.View;
import android.widget.ImageView;

public class MyView extends View {
    private ImageView imageView;


    public MyView(Context context) {
        super(context);

    }
    public MyView(Context context,ImageView imageView) {
        super(context);
        this.imageView = imageView;

    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        imageView.setImageResource(R.drawable.yoga_pose);
        invalidate();
    }
}
