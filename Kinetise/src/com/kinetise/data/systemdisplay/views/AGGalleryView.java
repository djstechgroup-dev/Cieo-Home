package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGGalleryDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGDataFeedViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.BackgroundImageDescriptor;
import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;
import com.kinetise.data.descriptors.types.Quad;
import com.kinetise.data.sourcemanager.ImageSource;
import com.kinetise.data.systemdisplay.IRebuildableView;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageChangeListener;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.drawing.BackgroundSetterCommandCallback;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.support.logger.Logger;

import java.security.InvalidParameterException;
import java.util.List;

/**
 * @author: Marcin Narowski
 * Date: 23.04.13
 * Time: 09:51
 */
public class AGGalleryView extends ViewPager implements IAGView, IRebuildableView, BackgroundSetterCommandCallback {

    public static final int pageMargin = 20;

    private final ViewDrawer mDrawer;
    private SystemDisplay mDisplay;
    private AGGalleryDataDesc mDescriptor;
    private AGGalleryAdapter mAdapter;
    protected ImageSource mBackgroundSource;

    public AGGalleryView(final SystemDisplay display, AGGalleryDataDesc desc) {
        super(display.getActivity());
        super.setId(desc.hashCode());
        mDrawer = new ViewDrawer(this);
        mDisplay = display;
        mDescriptor = desc;
        setPageMargin(pageMargin);
        initAdapter();
        this.setCurrentItem(mDescriptor.getActiveItemIndex(), false);
        if (mDescriptor instanceof AbstractAGViewDataDesc) {
            ImageDescriptor imageDescriptor = new BackgroundImageDescriptor(((AbstractAGViewDataDesc) mDescriptor).getBackground());
            mBackgroundSource = new ImageSource(imageDescriptor, new BackgroundChangeListener());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        getViewDrawer().refresh();
        super.onLayout(changed, l, t, r, b);
    }


    @Override
    public void draw(Canvas canvas) {
        mDrawer.draw(canvas);
        super.draw(canvas);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        mDrawer.onAfterDispatchDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w - getPageMargin(), h, oldw - getPageMargin(), oldh);
    }

    private void initAdapter() {
        mAdapter = new AGGalleryAdapter(mDisplay);
        mAdapter.setPagerView(this);
        setAdapter(mAdapter);
        addOnPageChangeListener(mAdapter);
    }

    private void setLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        params.setMargins((int) Math.round(calcDesc.getMarginLeft()), (int) Math.round(calcDesc.getMarginTop()),
                (int) Math.round(calcDesc.getMarginRight()), (int) Math.round(calcDesc.getMarginBottom()));
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        Quad border = calcDesc.getBorder();
        int left = (int) Math.round(calcDesc.getPaddingLeft()) + border.getLeftAsInt();
        int right = (int) Math.round(calcDesc.getPaddingRight()) + border.getRightAsInt();
        int top = (int) Math.round(calcDesc.getPaddingTop()) + border.getTopAsInt();
        int bottom = (int) Math.round(calcDesc.getPaddingBottom()) + border.getBottomAsInt();
        setPadding(left, top, right, bottom);

        setLayoutParams(params);
    }

    @Override
    public AbstractAGElementDataDesc getDescriptor() {
        return mDescriptor;
    }

    @Override
    public SystemDisplay getSystemDisplay() {
        return mDisplay;
    }

    @Override
    public IAGView getAGViewParent() {
        ViewParent parent = getParent();
        if (!(parent instanceof IAGView)) {
            throw new InvalidParameterException("Parent of IAGView object have to implement IAGView interface");
        }

        return (IAGView) parent;
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return mDrawer;
    }

    @Override
    protected void onDetachedFromWindow() {
        removeOnPageChangeListener(mAdapter);
        setAdapter(null);
        super.onDetachedFromWindow();
    }

    @Override
    public void loadAssets() {
        setLayoutParams();
        String baseUrl = mDescriptor.getFeedBaseAdress();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
    }

    @Override
    public boolean accept(IViewVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        mDescriptor = (AGGalleryDataDesc) descriptor;
    }

    @Override
    public void onClick(View view) {
    }


    @Override
    public void rebuildView() {
        Logger.d("Adapter", "RepaintView");
        AbstractAGDataFeedViewDataDesc feedClient = (AbstractAGDataFeedViewDataDesc) getDescriptor();
        List<AbstractAGElementDataDesc> views = feedClient.getFeedPresentClientControls();
        setGalleryElements(views);
    }

    private void setGalleryElements(List<AbstractAGElementDataDesc> views) {
        mAdapter.setGalleryElements(views);
        mAdapter.notifyDataSetChanged();
        setCurrentItem(mDescriptor.getActiveItemIndex(), false);
    }

    @Override
    public void setBackgroundBitmap(Bitmap bitmap) {
        mDrawer.setBackgroundBitmap(bitmap);
    }

    private class BackgroundChangeListener implements ImageChangeListener {
        @Override
        public void onImageChanged(Bitmap bitmap) {
            setBackgroundBitmap(bitmap);
        }
    }
}
