package com.kinetise.data.systemdisplay.views.scrolls;

import android.view.View;

import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.support.scrolls.scrollManager.ScrollType;

public interface IFeedScrollView {
    AGDataFeedScrollView getScrollView();
    void attachAndLayoutChild(View view, boolean isRecycled);
    int getFeedScrollX();
    int getFeedScrollY();
    AbstractAGContainerDataDesc getFeedDescriptor();
    ScrollType getFeedScrollType();
}
