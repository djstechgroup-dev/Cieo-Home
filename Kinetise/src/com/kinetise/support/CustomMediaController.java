package com.kinetise.support;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.MediaController;
import com.kinetise.helpers.CustomVideoPlayerUI;

import java.lang.reflect.Field;

public class CustomMediaController extends MediaController {

    public interface OnMediaControllerInteractionListener {
        void onFullscreenButtonPressed();
    }

    Context mContext;
    private OnMediaControllerInteractionListener mListener;
    private int mFullscreenButtonViewId;
    private CustomVideoPlayerUI mCustomVideoPlayerUI;

    public CustomMediaController(Context context, CustomVideoPlayerUI customVideoPlayerUI){
        super(context);
        mCustomVideoPlayerUI = customVideoPlayerUI;
        init(context,0);
    }

    public CustomMediaController(Context context){
        super(context);
        init(context,0);
    }

    public CustomMediaController(Context context, int fullscreenButtonViewId) {
        super(context);
        init(context,fullscreenButtonViewId);
    }

    private void init(Context context, int fullscreenButtonViewId){
        mContext = context;
        mFullscreenButtonViewId = fullscreenButtonViewId;
    }

    public void setListener(OnMediaControllerInteractionListener listener) {
        mListener = listener;
    }

    @Override
    public void setAnchorView(View view) {
        super.setAnchorView(view);

        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        frameParams.gravity = Gravity.RIGHT|Gravity.CENTER_VERTICAL;


        if(mFullscreenButtonViewId != 0) {
            ImageButton fullscreenButton = (ImageButton) LayoutInflater.from(mContext)
                    .inflate(mFullscreenButtonViewId, null);

            fullscreenButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onFullscreenButtonPressed();
                    }
                }
            });

            ((LinearLayout)((LinearLayout) getChildAt(0)).getChildAt(0)).addView(fullscreenButton, frameParams);
        }
    }

    @Override
    public void show(int timeout) {
        super.show(timeout);
        if(mCustomVideoPlayerUI != null)
            mCustomVideoPlayerUI.showCustomUI(timeout);
        // fix pre Android 4.3 strange positioning when used in Fragments
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion < Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                Field field1 = MediaController.class.getDeclaredField("mAnchor");
                field1.setAccessible(true);
                View mAnchor = (View)field1.get(this);

                Field field2 = MediaController.class.getDeclaredField("mDecor");
                field2.setAccessible(true);
                View mDecor = (View)field2.get(this);

                Field field3 = MediaController.class.getDeclaredField("mDecorLayoutParams");
                field3.setAccessible(true);
                WindowManager.LayoutParams mDecorLayoutParams = (WindowManager.LayoutParams)field3.get(this);

                Field field4 = MediaController.class.getDeclaredField("mWindowManager");
                field4.setAccessible(true);
                WindowManager mWindowManager = (WindowManager)field4.get(this);

                // NOTE: this appears in its own Window so co-ordinates are screen co-ordinates
                int [] anchorPos = new int[2];
                mAnchor.getLocationOnScreen(anchorPos);

                // we need to know the size of the controller so we can properly position it
                // within its space
                mDecor.measure(MeasureSpec.makeMeasureSpec(mAnchor.getWidth(), MeasureSpec.AT_MOST),
                        MeasureSpec.makeMeasureSpec(mAnchor.getHeight(), MeasureSpec.AT_MOST));

                mDecor.setPadding(0,0,0,0);

                mDecorLayoutParams.verticalMargin = 0;
                mDecorLayoutParams.horizontalMargin = 0;
                mDecorLayoutParams.width = mAnchor.getWidth();
                mDecorLayoutParams.gravity = Gravity.LEFT|Gravity.TOP;
                mDecorLayoutParams.x = anchorPos[0];// + (mAnchor.getWidth() - p.width) / 2;
                mDecorLayoutParams.y = anchorPos[1] + mAnchor.getHeight() - mDecor.getMeasuredHeight();
                mWindowManager.updateViewLayout(mDecor, mDecorLayoutParams);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void hide() {
        super.hide();
        mCustomVideoPlayerUI.hideCustomUI();
    }
}