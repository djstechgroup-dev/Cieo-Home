package com.kinetise.data.sourcemanager;

import com.kinetise.data.application.feedmanager.DownloadFeedCommand;
import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.helpers.http.CacheControlOptions;
import com.kinetise.helpers.threading.ThreadPool;
import com.kinetise.helpers.threading.AGAsyncTask;

/**
 * All command classes that download content should inherit from this class.
 */
public abstract class AbstractGetSourceCommand<T> implements IGetSourceCommand<T> {

    protected AGAsyncTask mAssociatedTask;

    protected String mUri;
    protected String mBaseUri;

    public AbstractGetSourceCommand(String baseUri, String source){
        mUri = source;
        mBaseUri = baseUri;
    }

    /**
     * Method checks if setLoading method should be called
 */
    protected void setLoadingIfNecessary() {
        if (mUri != null && (mUri.startsWith(AssetsManager.PREFIX_HTTP) || mUri.startsWith(AssetsManager.PREFIX_HTTPS) ||
            (mUri.startsWith(AssetsManager.PREFIX_ASSETS) && this instanceof DownloadFeedCommand))) {
        setLoading();
    }
    }

    protected void setAssociatedTask(AGAsyncTask task){
        mAssociatedTask = task;
    }

    protected void clearAssociatedTask(){
        mAssociatedTask = null;
    }

    public void cancel() {
        if(mAssociatedTask!=null) {
            ThreadPool.getInstance().cancelTask(mAssociatedTask);
            clearAssociatedTask();
        }
    }


    /**
     * Method for setting loading placeholder in an appropriate view, has to bo overridden in inheriting classes.
     */
    protected void setLoading(){}

    @Override
    public void setCacheOption(CacheControlOptions cache) {
    }

    @Override
    public CacheControlOptions getCacheOption() {
        return CacheControlOptions.NO_CACHE;
    }

    public String getUri(){
        return mUri;
    }

    public void setUri(String uri){
        mUri = uri;
    }

    public void setResolvedUri(String resolvedUri){

    }

    public boolean isDisplayResult() {
        return false;
    }

    @Override
    public void onError(int status, PopupMessage... messages) {
        onError();
    }


    public String getBaseUri() {
        return mBaseUri;
    }

    public String resolveURI() {
        return  AssetsManager.resolveURI(mUri, mBaseUri);
    }
}
