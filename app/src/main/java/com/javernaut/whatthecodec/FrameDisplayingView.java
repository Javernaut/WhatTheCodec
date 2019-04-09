package com.javernaut.whatthecodec;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.widget.ImageView;

public class FrameDisplayingView extends ImageView {
    public FrameDisplayingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setVideoConfig(VideoFileConfig config) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        doTheThingWithBitmap(bitmap);
        setImageBitmap(bitmap);
    }

    private static native void doTheThingWithBitmap(Bitmap bitmap);
}
