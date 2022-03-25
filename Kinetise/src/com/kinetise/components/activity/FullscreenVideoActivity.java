package com.kinetise.components.activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.VideoView;

import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.helpers.CustomVideoPlayerUI;
import com.kinetise.helpers.RWrapper;
import com.kinetise.support.CustomMediaController;

public class FullscreenVideoActivity extends Activity {
    public static final int FULLSCREEN_VIDEO_REQUEST_CODE = 5051;

    public static final String CURRENT_VIDEO_POSITION_KEY = "currentPosition";
    public static final String VIDEO_URL_KEY = "videoUrl";
    public static final String IS_PLAYING_KEY = "isPlaying";
    public static final String REQUESTING_VIEW_ID_KEY = "requestingViewId";

    private VideoView mVideoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(RWrapper.layout.fullscreen_video_dialog);
        KinetiseApplication.getInstance().logToCrashlytics("[FSVA] onCreate");

        ImageButton exitFullscreenButton = (ImageButton) findViewById(RWrapper.id.exit_fullscreen_button);
        exitFullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CustomVideoPlayerUI customVideoPlayerUI = new CustomVideoPlayerUI();
        customVideoPlayerUI.addUIView(exitFullscreenButton);

        mVideoView = (VideoView) findViewById(RWrapper.id.fullscreen_video_view);

        final CustomMediaController mediaController = new CustomMediaController(this,customVideoPlayerUI);
        mediaController.setAnchorView(mVideoView);

        mVideoView.setMediaController(mediaController);

        Bundle b = getIntent().getExtras();
        if (b!=null) {
            String videoUrl = b.getString(VIDEO_URL_KEY);

            if (videoUrl != null && !videoUrl.equals("")) {
                Uri vidUri = Uri.parse(videoUrl);
                mVideoView.setVideoURI(vidUri);
            } else {
                mVideoView.setVideoPath("");
            }

            int currentPosition;
            if (savedInstanceState != null) {
                currentPosition = savedInstanceState.getInt(CURRENT_VIDEO_POSITION_KEY);
            } else {
                currentPosition = b.getInt(CURRENT_VIDEO_POSITION_KEY);
            }

            mVideoView.seekTo(currentPosition);

            final boolean isPlaying = b.getBoolean(IS_PLAYING_KEY);

            mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //to restore pointer position we have to start and pause video
                    mVideoView.start();
                        if (!isPlaying)
                            mVideoView.pause();
                }
            });

            mVideoView.requestFocus();
        } else {
            finish();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        KinetiseApplication.getInstance().logToCrashlytics("[FSVA] onSaveInstanceState");
        outState.putInt(CURRENT_VIDEO_POSITION_KEY, mVideoView.getCurrentPosition());
    }

    @Override
    public void finish() {
        Intent intent = getIntent();
        intent.putExtra(CURRENT_VIDEO_POSITION_KEY, mVideoView.getCurrentPosition());
        intent.putExtra(IS_PLAYING_KEY, mVideoView.isPlaying());
        setResult(0, intent);

        super.finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        KinetiseApplication.getInstance().logToCrashlytics("[FSVA] onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        KinetiseApplication.getInstance().logToCrashlytics("[FSVA] onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        KinetiseApplication.getInstance().logToCrashlytics("[FSVA] onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        KinetiseApplication.getInstance().logToCrashlytics("[FSVA] onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        KinetiseApplication.getInstance().logToCrashlytics("[FSVA] onDestroy");
    }
}