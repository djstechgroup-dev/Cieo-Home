package com.kinetise.data.systemdisplay.views;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewParent;
import android.widget.ImageButton;
import android.widget.VideoView;
import com.kinetise.components.activity.FullscreenVideoActivity;
import com.kinetise.components.activity.KinetiseActivity;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGVideoViewDataDesc;
import com.kinetise.data.descriptors.types.Quad;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.CustomVideoPlayerUI;
import com.kinetise.helpers.FullscreenVideoBridge;
import com.kinetise.helpers.RWrapper;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.helpers.drawing.BackgroundSetterCommandCallback;
import com.kinetise.support.CustomMediaController;

import java.security.InvalidParameterException;

public class AGVideoView extends AGControl<AGVideoViewDataDesc> implements IAGView, BackgroundSetterCommandCallback {
    private VideoView mVideoView;

    public AGVideoView(SystemDisplay systemDisplay, AGVideoViewDataDesc desc) {
        super(systemDisplay, desc);
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        String videoUrl = mDescriptor.getVideoSrc().getStringValue();
        createAndAddVideoView(videoUrl);
        String baseUrl = mDescriptor.getFeedBaseAdress();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
    }

    private void createAndAddVideoView(String videoUrl) {
        createLayoutFromXml(videoUrl);

        setOwnLayoutParams();
        postInvalidate();
    }

    private void createLayoutFromXml(String videoUrl) {
        final View videoRoot = mDisplay.getActivity().getLayoutInflater().inflate(RWrapper.layout.video_view, null);
        addView(videoRoot);

        ImageButton openFullscreenButton = (ImageButton) videoRoot.findViewById(RWrapper.id.open_fullscreen_video_button);
        openFullscreenButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onFullscreenButtonPressed();
            }
        });

        mVideoView = (VideoView) videoRoot.findViewById(RWrapper.id.video_view);

        CustomMediaController mediaController = createMediaController(mVideoView, openFullscreenButton);

        initVideoView(videoUrl, mediaController);
    }

    private void initVideoView(String videoUrl, CustomMediaController mediaController) {
        mVideoView.setMediaController(mediaController);
        if (videoUrl != null && !videoUrl.equals("")) {
            Uri vidUri = Uri.parse(videoUrl);
            mVideoView.setVideoURI(vidUri);
        } else {
            mVideoView.setVideoPath("");
        }
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (mDescriptor.getAutoplay())
                    mVideoView.start();
            }
        });
        mVideoView.requestFocus();
    }

    private CustomMediaController createMediaController(VideoView anchorView, ImageButton openFullscreenButton) {
        CustomVideoPlayerUI customVideoPlayerUI = new CustomVideoPlayerUI();
        customVideoPlayerUI.addUIView(openFullscreenButton);

        final CustomMediaController mediaController = new CustomMediaController(mDisplay.getActivity(), customVideoPlayerUI);


        mediaController.setAnchorView(anchorView);
        return mediaController;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed,left,top,right,bottom);

        View child = getChildAt(0);
        int parentWidth = right - left;
        int parentHeight = bottom - top;
        int childLeft = (parentWidth - child.getMeasuredWidth()) / 2;
        int childTop = (parentHeight - child.getMeasuredHeight()) / 2;
        int childRight = childLeft + child.getMeasuredWidth();
        int childBottom = childTop + child.getMeasuredHeight();
        child.layout(childLeft, childTop, childRight, childBottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View child = getChildAt(0);
        Quad border = mDescriptor.getCalcDesc().getBorder();
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        child.measure(MeasureSpec.makeMeasureSpec(parentWidth - (int) Math.round(border.getHorizontalBorderWidth()) - getPaddingLeft() - getPaddingRight(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(parentHeight - (int) Math.round(border.getVerticalBorderHeight()) - getPaddingTop() - getPaddingBottom(), MeasureSpec.EXACTLY));
    }

    @Override
    public IAGView getAGViewParent() {
        ViewParent parent = getParent();
        if (!(parent instanceof IAGView)) {
            throw new InvalidParameterException("Parent of IAGView object have to implement IAGView interface");
        }

        return (IAGView) parent;
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return mDrawer;
    }

    private void setOwnLayoutParams() {
        setPadding((int) Math.round(getCalcDesc().getPaddingLeft()),
                (int) Math.round(getCalcDesc().getPaddingTop()),
                (int) Math.round(getCalcDesc().getPaddingRight()),
                (int) Math.round(getCalcDesc().getPaddingBottom()));
    }

    private AGViewCalcDesc getCalcDesc() {
        return mDescriptor != null ? mDescriptor.getCalcDesc() : null;
    }

    @Override
    public boolean accept(IViewVisitor visitor) {
        return visitor.visit(this);
    }

    public void onFullscreenButtonPressed() {
        boolean playing = mVideoView.isPlaying();
        int currentPosition = mVideoView.getCurrentPosition();
        String videoUrl = mDescriptor.getVideoSrc().getStringValue();

        KinetiseActivity activity = mDisplay.getActivity();

        Intent i = new Intent(activity, FullscreenVideoActivity.class);

        Bundle b = new Bundle();
        b.putString(FullscreenVideoActivity.VIDEO_URL_KEY, videoUrl);
        b.putBoolean(FullscreenVideoActivity.IS_PLAYING_KEY, playing);
        b.putInt(FullscreenVideoActivity.CURRENT_VIDEO_POSITION_KEY, currentPosition);
        b.putString(FullscreenVideoActivity.REQUESTING_VIEW_ID_KEY, mDescriptor.getId());

        i.putExtras(b);

        activity.startActivityForResult(i, FullscreenVideoActivity.FULLSCREEN_VIDEO_REQUEST_CODE);
    }

    public void onFullscreenClosed(int currenPosition, final boolean isPlaying) {
        mVideoView.seekTo(currenPosition);
        //to restore pointer position we have to start and pause video
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //to restore pointer position we have to start and pause video
                mVideoView.start();
                if (!isPlaying)
                    mVideoView.pause();
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        FullscreenVideoBridge.getInstance().registerView(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        FullscreenVideoBridge.getInstance().removeView(this);
    }
}
