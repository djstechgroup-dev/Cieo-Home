package com.kinetise.data.exceptionmanager;

import com.kinetise.support.logger.Logger;

import java.util.*;

public class ExceptionManager {
    private static ExceptionManager mInstance;
    private Vector<Throwable> mThrowables = new Vector<Throwable>(10);

    public static ExceptionManager getInstance() {
        if (mInstance == null) {
            synchronized (ExceptionManager.class){
                if (mInstance == null) {
                    mInstance = new ExceptionManager();
                }
            }
        }
        return mInstance;
    }

    private ExceptionManager() {
    }

    public static void clearInstance(){
        mInstance = null;
    }

    private void logException(Throwable exception) {
        //prevent multi logging exception
        if ((mThrowables.contains(exception))) {
            return;
        }
        if(mThrowables.size() > 10){
            mThrowables.remove(0);
        }
        mThrowables.add(exception);
        for (StackTraceElement stackTraceElement : exception.getStackTrace()) {
            Logger.e(this, String.format("%s at line %s in method %s(native:%s) - file: %s", stackTraceElement.getClassName(), stackTraceElement.getLineNumber(), stackTraceElement.getMethodName(), String.valueOf(stackTraceElement.isNativeMethod()), stackTraceElement.getFileName()));
        }
        Throwable cause = exception.getCause();
        if (cause != null) {
            Logger.e(this, String.format("Exception Cause:%nCause message:%s%nCause class:%s", String.valueOf(cause.getMessage()), cause.getClass().getSimpleName()));
        }
    }

    public void handleException(Exception exception) {
        handleException(exception,true);
    }

    public void handleException(Throwable e, boolean rethrow) {
        logException(e);
        if (rethrow) {
            AnyThrow.throwUncheked(e);
        }
    }

    /**
     * @see{@link{http://blog.ragozin.info/2011/10/java-how-to-throw-undeclared-checked.html}}
     */
    static class AnyThrow {

        public static void throwUncheked(Throwable e) {
            AnyThrow.<RuntimeException>throwAny(e);
        }

        @SuppressWarnings("unchecked")
        private static <E extends Throwable> void throwAny(Throwable e) throws E {
            throw (E) e;
        }
    }

}
