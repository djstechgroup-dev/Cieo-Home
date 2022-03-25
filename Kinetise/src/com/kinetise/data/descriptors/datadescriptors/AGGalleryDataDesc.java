package com.kinetise.data.descriptors.datadescriptors;

public class AGGalleryDataDesc extends AbstractAGDataFeedViewDataDesc {

    private boolean mLoaded;

    public AGGalleryDataDesc(String id) {
        super(id);
        setLastFeedItemCount(0);
    }

    @Override
    public AGGalleryDataDesc createInstance() {
        return new AGGalleryDataDesc(getId());
    }

    @Override
    public AGGalleryDataDesc copy() {
        AGGalleryDataDesc copied = (AGGalleryDataDesc) super.copy();
        copied.mLoaded = mLoaded;
        return copied;
    }

    @Override
    public void resetFeed() {
        super.resetFeed();
        mLastItemIndex = -1;
    }
}
