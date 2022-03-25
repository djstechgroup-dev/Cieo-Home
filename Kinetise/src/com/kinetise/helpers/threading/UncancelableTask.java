package com.kinetise.helpers.threading;


public final class UncancelableTask extends AGAsyncTask {
    private final Runnable mRunnable;

    @Override
    public void cancel() {
    }

    public UncancelableTask(Runnable code){
        mRunnable = code;
    }

    @Override
    public void run() {
        mRunnable.run();
    }
}
