package com.anhdt.doranewsvermain.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

public class NewsBackgroundLayout extends LinearLayout implements Target {
    public NewsBackgroundLayout(Context context) {
        super(context);
    }

    public NewsBackgroundLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NewsBackgroundLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        setBackground(new BitmapDrawable(getResources(), bitmap));

    }

    @Override
    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
        Log.d("ABC-XX", errorDrawable.toString());

    }
//
//    @Override
//    public void onBitmapFailed(Drawable errorDrawable) {
//    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        Log.d("ABC", "onPrepareLoad");
    }
}
