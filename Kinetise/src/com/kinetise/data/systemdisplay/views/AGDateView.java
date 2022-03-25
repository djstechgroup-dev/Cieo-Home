package com.kinetise.data.systemdisplay.views;

import android.app.Activity;
import android.os.Handler;
import android.view.View;

import com.kinetise.data.descriptors.datadescriptors.AGDateDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.time.DateSourceType;
import com.kinetise.helpers.time.DateUpdater;
import com.kinetise.support.logger.Logger;

import java.util.Date;

public class AGDateView extends AGTextView<AGDateDataDesc> implements Runnable {
    public static final int TICKING_INTERVAL = 500;
    private long mLastUpdateTimestamp = -1;
    private Handler mHandler;

    public AGDateView(SystemDisplay display, AGDateDataDesc desc) {
        super(display, desc);
        setClickable(false);
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        if ((getDescriptor()).isTicking()) {
            mHandler = new Handler();
            run();
        } else {
            updateDate();
        }
    }

    @Override
    public boolean accept(IViewVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void run() {
        updateDate();
        mHandler.removeCallbacks(this);
        mHandler.postDelayed(this, TICKING_INTERVAL);
    }

    public void updateDate() {
        Thread t = new Thread() {
            @Override
            public void run() {
                if (mLastUpdateTimestamp < 0) {
                    mLastUpdateTimestamp = System.currentTimeMillis();
                }

                AGDateDataDesc desc = mDescriptor;

                long dateToSet = 0;

                Logger.v(this, "updateDate", desc.toString());

                dateToSet = getDateToSet(desc, dateToSet);
                Date newDate = new Date(dateToSet);

                desc.setDateObjectAndUpdateText(newDate);

                final SystemDisplay systemDisplay = mDisplay;
                final AGDateDataDesc finalDesc = desc;
                if (mDisplay != null) {
                    Activity activity = mDisplay.getActivity();
                    if (activity != null)
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                systemDisplay.runCalcManager(finalDesc);
                                getBasicTextView().invalidate();
                            }
                        });
                }
                mLastUpdateTimestamp = System.currentTimeMillis();
            }
        };
        t.start();
    }

    private long getDateToSet(AGDateDataDesc desc, long dateToSet) {
        DateSourceType dataSource = desc.getDataSource();
        if (dataSource == DateSourceType.NODE) {
            long lastDate = desc.getDateObject().getTime();
            dateToSet = lastDate + System.currentTimeMillis() - mLastUpdateTimestamp;
        } else if (dataSource == DateSourceType.INTERNET) {
            dateToSet = DateUpdater.getInstance().getCurrentTime();
        } else if (dataSource == DateSourceType.LOCAL) {
            dateToSet = System.currentTimeMillis();
        }
        return dateToSet;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mDescriptor.isTicking()) {
            run();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null)
            mHandler.removeCallbacks(this);
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        if (mHandler != null)
            mHandler.removeCallbacks(this);
    }

    @Override
    protected void detachViewFromParent(View child) {
        super.detachViewFromParent(child);
        if (mHandler != null)
            mHandler.removeCallbacks(this);
    }
}
