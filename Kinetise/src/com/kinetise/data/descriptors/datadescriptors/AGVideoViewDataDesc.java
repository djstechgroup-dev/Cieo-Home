package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

public class AGVideoViewDataDesc extends AbstractAGViewDataDesc {
    private VariableDataDesc mVideoSrc;
    private boolean mAutoplay;

    public AGVideoViewDataDesc(String id) {
        super(id);
    }

    @Override
    public AGVideoViewDataDesc createInstance() {
        return new AGVideoViewDataDesc(getId());
    }

    @Override
    public AGVideoViewDataDesc copy() {
        AGVideoViewDataDesc copied = (AGVideoViewDataDesc) super.copy();
        copied.setVideoSrc(mVideoSrc);
        copied.setAutoplay(mAutoplay);
        return copied;
    }

    public void setVideoSrc(VariableDataDesc videoSrc) {
        mVideoSrc = videoSrc;
    }

    public VariableDataDesc getVideoSrc() {
        return mVideoSrc;
    }

    public void setAutoplay(boolean autoplay) {
        mAutoplay = autoplay;
    }

    public boolean getAutoplay() {
        return mAutoplay;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mVideoSrc.resolveVariable();
    }
}
