package com.taviscratch.ponychatandroidclient;


import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MyFrameLayout extends FrameLayout {



    public MyFrameLayout(Context context) {
        super(context);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        this.onTouchEvent(ev);
        return false;
    }
}
