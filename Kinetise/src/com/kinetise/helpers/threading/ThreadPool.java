package com.kinetise.helpers.threading;

import com.kinetise.support.logger.Logger;

import java.util.concurrent.*;

public class ThreadPool {
    private static ThreadPool mInstance;
    private ThreadPoolExecutor mThreadPoolExecutor;
    private ThreadPoolExecutor mBackgroundExecutor;
    private ThreadPoolExecutor mFeedLongExecutor; //executor for feeds cache policy = 'refreshEvery' and 'live'

    public synchronized static ThreadPool getInstance() {
        if (mInstance == null) {
            mInstance = new ThreadPool();
            return mInstance;
        }
        return mInstance;
    }

    public static void clearInstance(){
        mInstance = null;
    }

    private ThreadPool() {
        mThreadPoolExecutor = createExecutor(3, 3);
        mBackgroundExecutor = createExecutor(3, 3);
        mFeedLongExecutor = createUnqueuingExecutor(2, 30);
    }

    private ThreadPoolExecutor createExecutor(int startingPoolSize, int maximumPoolSize) {
            return new ThreadPoolExecutor(startingPoolSize, maximumPoolSize, 0, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory());
    }

    /**
     * Using SynchronousQueue doesn't wait with creating new Threads untill queue is full but created new Thread immediately (if only maxPoolSize is not reached)
     * @see <a href="http://developer.android.com/reference/java/util/concurrent/ThreadPoolExecutor.html">ThreadPoolExecutor</a>
     * @param startingPoolSize the number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set maximumPoolSize
     * @param maximumPoolSize the maximum number of threads to allow in the pool
     * @return
     */
    private ThreadPoolExecutor createUnqueuingExecutor(int startingPoolSize, int maximumPoolSize) {
            return new ThreadPoolExecutor(startingPoolSize, maximumPoolSize, 0, TimeUnit.NANOSECONDS, new SynchronousQueue<Runnable>(), Executors.defaultThreadFactory());
    }

    public void executeBackground(AGAsyncTask task){
        executeTask(mBackgroundExecutor,task);
    }

    public void executeFeedLongTask(AGAsyncTask task){
        executeTask(mFeedLongExecutor,task);
    }

    public void execute(AGAsyncTask task) {
        executeTask(mThreadPoolExecutor,task);
    }

    private void executeTask(ThreadPoolExecutor executor,AGAsyncTask task){
        try {
            executor.execute(task);
        } catch (RejectedExecutionException e) {
            Logger.d(this, e.getMessage());
        }
    }

    public void cancelTask(AGAsyncTask associatedTask) {
        associatedTask.cancel();
        mThreadPoolExecutor.remove(associatedTask);
        mBackgroundExecutor.remove(associatedTask);
        mFeedLongExecutor.remove(associatedTask);
    }

    public void shutdown() {
        mThreadPoolExecutor.shutdownNow();
        mBackgroundExecutor.shutdownNow();
        mFeedLongExecutor.shutdownNow();
        mThreadPoolExecutor = null;
        mBackgroundExecutor = null;
        mFeedLongExecutor = null;
        mInstance = null;
    }
}
