package com.kinetise.data.systemdisplay.views;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.helpers.calcmanagerhelper.CalcManagerHelper;

public class PullToRefreshAnimator {
    private boolean mActivePullToRefreshText;

    private final double PULL_TO_REFRESH_SLOPE_FACTOR = 0.8;

    private String mPullToRefreshText;
    private String mReleaseToRefreshText;

    private float mPullToRefreshTextWidth;
    private float mReleaseToRefreshTextWidth;
    private int mPullToRefreshTextHeight;
    private int mReleaseToRefreshTextHeight;
    private Paint mPaint;

    public PullToRefreshAnimator(){
        initializeTextsFromDictionary();
        initializePaint();
        measureTextSizes();
    }

    protected void initializePaint() {
        mPaint = createTextPaintForPullToRefreshText();
    }

    private void initializeTextsFromDictionary(){
        mPullToRefreshText = LanguageManager.getInstance().getString(LanguageManager.PULL_TO_REFRESH_TEXT_KEY);
        mReleaseToRefreshText = LanguageManager.getInstance().getString(LanguageManager.RELEASE_TEXT_KEY);
    }

    private Paint createTextPaintForPullToRefreshText() {
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize((float) CalcManagerHelper.KPXtoPixels(60));
        paint.setShadowLayer(5, 0, 0, Color.BLACK);
        return paint;
    }

    private void measureTextSizes() {
        mPullToRefreshTextWidth = getTextWidth(mPaint, mPullToRefreshText);
        mReleaseToRefreshTextWidth = getTextWidth(mPaint, mReleaseToRefreshText);
        mPullToRefreshTextHeight = getTextHeight(mPaint, mPullToRefreshText);
        mReleaseToRefreshTextHeight = getTextHeight(mPaint, mReleaseToRefreshText);
    }

    private int getTextHeight(Paint textPaint, String text) {
        Rect textRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textRect);

        return textRect.bottom - textRect.top;
    }

    private int getTextWidth(Paint textPaint, String text) {
        Rect textRect = new Rect();
        textPaint.getTextBounds(text, 0, text.length(), textRect);

        return textRect.right - textRect.left;
    }

    public void setPullToRefreshAsActiveText(){
        mActivePullToRefreshText = true;
    }

    public void setReleaseToRefreshAsActiveText(){
        mActivePullToRefreshText = false;
    }

    public String getActiveText(){
        if(mActivePullToRefreshText)
            return mPullToRefreshText;
        return mReleaseToRefreshText;
    }

    public float getActiveTextWidth(){
        if(mActivePullToRefreshText)
            return mPullToRefreshTextWidth;
        return mReleaseToRefreshTextWidth;
    }
    public float getActiveTextHeight(){
        if(mActivePullToRefreshText)
            return mPullToRefreshTextHeight;
        return mReleaseToRefreshTextHeight;
    }

    public Paint getPullToRefreshPaint(){
        return mPaint;
    }

    public boolean isPulledFarEnought(float translationPercent){
        return translationPercent >= PULL_TO_REFRESH_SLOPE_FACTOR;
    }

    public void setTextSize(float textSize) {
        mPaint.setTextSize(textSize);
    }

    public void updateActiveText(float translationPercent) {
        if(isPulledFarEnought(translationPercent)){
            setReleaseToRefreshAsActiveText();
        } else{
            setPullToRefreshAsActiveText();
        }
    }

    public void setPaintAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }
}
