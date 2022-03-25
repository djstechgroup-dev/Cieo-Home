package com.kinetise.data.systemdisplay;

public interface IFeedView {

    void notifyLoadingStarted();

    void notifyDataChanged();

    void notifyDownloadError();

}
