package com.kinetise.helpers;

import com.kinetise.data.systemdisplay.views.AGVideoView;

import java.util.ArrayList;

public class FullscreenVideoBridge {
    private static FullscreenVideoBridge mInstance;
    private ArrayList<AGVideoView> mExistingViews;

    private FullscreenVideoBridge() {
        mExistingViews = new ArrayList<AGVideoView>();
    }

    public static FullscreenVideoBridge getInstance() {
        if (mInstance == null) {
            mInstance = new FullscreenVideoBridge();
        }

        return mInstance;
    }

    public void registerView(AGVideoView videoView) {
        mExistingViews.add(videoView);
    }

    public void removeView(AGVideoView videoView) {
        mExistingViews.remove(videoView);
    }

    private AGVideoView getViewById(String viewId) {
        for (AGVideoView videoView : mExistingViews) {
            if(videoView.getDescriptor().getId().equals(viewId))
                return videoView;
        }
        return null;
    }

    public void notifyVideoViewById(String viewId, int currentPosition, boolean isPlaying){
        AGVideoView videoView = getViewById(viewId);
        if(videoView != null)
            videoView.onFullscreenClosed(currentPosition, isPlaying);
    }
}
