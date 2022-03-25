package com.kinetise.data.systemdisplay.views;

import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.view.View;

import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGElementCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.packagemanager.AppPackageManager;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.helpers.drawing.BackgroundSetterCommandCallback;
import com.kinetise.support.logger.Logger;

import java.io.IOException;

public class AGScreenView extends AGControl<AGScreenDataDesc> implements IAGView, BackgroundSetterCommandCallback {

    private AGHeaderView mHeaderView;
    private AGBodyView mBodyView;
    private AGNaviPanelView mNaviPanelView;
    private AGVideoOnBgView mBackgroundVideoView;

    public AGScreenView(SystemDisplay display, AGScreenDataDesc desc) {
        super(display, desc);

        if (mDescriptor.getBackgroundVideoName() != null) {
            mBackgroundVideoView = new AGVideoOnBgView(getContext(), mDisplay, mDescriptor);
            this.addView(mBackgroundVideoView, 0);
        }
    }



    @Override
    public void requestLayout() {
        super.requestLayout();
    }

    public void setSection(AGHeaderView header) {
        mHeaderView = header;
        addView(mHeaderView);
    }

    public void setSection(AGBodyView body) {
        mBodyView = body;

        int bodyViewIndex = 0;
        if (mBackgroundVideoView != null) {
            // if there is bg video view, it still should be on index 0 to be drawn first
            bodyViewIndex = 1;
        }
        addView(mBodyView, bodyViewIndex);
    }

    public void setSection(AGNaviPanelView naviPanel) {
        mNaviPanelView = naviPanel;
        addView(mNaviPanelView);
    }

    public void setSection(IAGSectionView section){
        if (section instanceof AGHeaderView) {
            setSection((AGHeaderView) section);
        } else if (section instanceof AGBodyView) {
            setSection((AGBodyView) section);
        } else if (section instanceof AGNaviPanelView) {
            setSection((AGNaviPanelView) section);
        }
    }

    @Override
    public AGScreenDataDesc getDescriptor() {
        return mDescriptor;
    }

    @Override
    public IAGView getAGViewParent() {
        return null;
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return mDrawer;
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        String baseUrl = mDescriptor.getFeedBaseAdress();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
        if (mHeaderView != null) {
            mHeaderView.loadAssets();
        }
        if (mBodyView != null) {
            mBodyView.loadAssets();
        }
        if (mNaviPanelView != null) {
            mNaviPanelView.loadAssets();
        }

        if (mBackgroundVideoView != null) {
            try {
                String backgroundVideoName = mDescriptor.getBackgroundVideoName();
                AssetFileDescriptor assetFileDescriptor = AppPackageManager.getInstance().getPackage().getAssetFileDescriptor(backgroundVideoName);

                mBackgroundVideoView.setDataSource(assetFileDescriptor);
                mBackgroundVideoView.setLooping(true);
                mBackgroundVideoView.play();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean accept(IViewVisitor visitor) {

        if (visitor.visit(this)) {
            return true;
        }

        if (mHeaderView != null && mHeaderView.accept(visitor)) {
            return true;
        }

        if (mBodyView != null && mBodyView.accept(visitor)) {
            return true;
        }

        if (mNaviPanelView != null && mNaviPanelView.accept(visitor)) {
            return true;
        }

        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        AGElementCalcDesc calcDesc = getDescriptor().getCalcDesc();
        int specWidth = MeasureSpec.makeMeasureSpec(calcDesc.getViewWidth(), MeasureSpec.EXACTLY);
        int specHeight = MeasureSpec.makeMeasureSpec(calcDesc.getViewHeight(), MeasureSpec.EXACTLY);

        setMeasuredDimension(specWidth, specHeight);

        final int count = getChildCount();
        AGElementCalcDesc childCalcDesc;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                childCalcDesc = ((IAGView) child).getDescriptor().getCalcDesc();
                int childSpecWidth = MeasureSpec.makeMeasureSpec(childCalcDesc.getViewWidth(), MeasureSpec.EXACTLY);
                int childSpecHeight = MeasureSpec.makeMeasureSpec(childCalcDesc.getViewHeight(), MeasureSpec.EXACTLY);
                child.measure(childSpecWidth, childSpecHeight);
            }
        }
    }

    @Override
    public void forceLayout() {
        super.forceLayout();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int right, int bottom) {
        super.onLayout(changed,l,t,right,bottom);
        Logger.d("ScreenView"," onLayout");
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            final IAGView agChild = (IAGView) child;

            AbstractAGElementDataDesc desc = agChild.getDescriptor();
            final AGElementCalcDesc childCalcDesc = desc.getCalcDesc();

            // sizes
            int width = (int) (Math.round(childCalcDesc.getWidth() + childCalcDesc.getPositionX()) - Math.round(childCalcDesc.getPositionX()));
            int height = (int) (Math.round(childCalcDesc.getHeight() + childCalcDesc.getPositionY()) - Math.round(childCalcDesc.getPositionY()));

            int left = (int) Math.round(childCalcDesc.getPositionX());
            int top = (int) Math.round(childCalcDesc.getPositionY());

            // Commented-out as String.format is heavy operation. Uncomment when needed for debugging.
               /* Logger.v(this, "onLayout", String.format("%s: child.layout: left=%d, top=%d, width=%d, height=%d",
                        child.getClass().getName(), left, top, width, height));*/

            child.layout(left, top, left + width, top + height);
        }
    }

    @Override
    public void onClick(View view) {
        // nothing to do
    }

    public AGBodyView getBodyView() {
        return mBodyView;
    }

    public void pauseBackgroundVideo(){
        if(mBackgroundVideoView != null){
            mBackgroundVideoView.stop();
        }
    }

    public void startBackgroundVideo(){
        if(mBackgroundVideoView != null){
            mBackgroundVideoView.play();
        }
    }

    public void onScreenEntered() {
        VariableDataDesc onScreenEnterAction = mDescriptor.getOnScreenEnterAction();
        if (onScreenEnterAction != null)
            onScreenEnterAction.resolveVariable();
    }
}
