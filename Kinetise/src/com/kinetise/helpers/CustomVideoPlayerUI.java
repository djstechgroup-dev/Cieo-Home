package com.kinetise.helpers;

import android.os.Handler;
import android.os.Message;
import android.view.View;

import java.util.ArrayList;

public class CustomVideoPlayerUI {
    private ArrayList<View> mCustomUIViews;
    private boolean mUIShown;


    private static final int FADE_OUT = 0;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FADE_OUT:
                    hideCustomUI();
                    break;
            }
        }
    };

    public CustomVideoPlayerUI(ArrayList<View> customUIViews) {
        mCustomUIViews = customUIViews;
    }

    public CustomVideoPlayerUI(){
        mCustomUIViews = new ArrayList<View>();
    }

    public void addUIView(View view){
        mCustomUIViews.add(view);
    }

    public void showCustomUI(int timeout) {
        if (!mUIShown) {
            for(View view : mCustomUIViews){
                view.setVisibility(View.VISIBLE);
            }
            mUIShown = true;
        }

        Message msg = mHandler.obtainMessage(FADE_OUT);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    public void hideCustomUI() {
        for(View view : mCustomUIViews){
            view.setVisibility(View.INVISIBLE);
        }
        mUIShown = false;
    }
}
