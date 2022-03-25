package com.kinetise.helpers.threading;

public abstract class AGAsyncTask implements Runnable{
    protected boolean mIsCanceled = false;

    public synchronized void cancel(){
        mIsCanceled=true;
    }
}
