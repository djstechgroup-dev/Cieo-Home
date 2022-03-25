package com.kinetise.data.systemdisplay.views;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.calcmanager.CalcManager;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGErrorDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGLoadingDataDesc;
import com.kinetise.data.descriptors.helpers.DataDescHelper;
import com.kinetise.data.systemdisplay.SystemDisplay;

import java.util.List;

/**
 * @author: Marcin Narowski
 * Date: 23.04.13
 * Time: 09:52
 */
public class AGGalleryAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener {

    private final SystemDisplay mDisplay;
    private List<AbstractAGElementDataDesc> mCollection;
    private ViewPager mPagerView;
    private SparseArray<View> mViews = new SparseArray<View>();

    /** Number of a current page in pager, used to restore pager to a previous state when rotating phone**/
    private int mCurrentPageNumber;

    public AGGalleryAdapter(SystemDisplay display) {
        super();
        mDisplay = display;
    }

    public void updateViews(int parentWidth, int parentHeight) {
        for (int i = 0; i < mViews.size(); i++) {
            int key = mViews.keyAt(i);
            View view = mViews.get(key);
            if (view != null) {
                if (view instanceof FrameLayout) {
                    view.setMinimumWidth(parentWidth);
                    view.setMinimumHeight(parentHeight);
                    View child = ((ViewGroup) view).getChildAt(0);
                    if (child instanceof AGLoadingView) {
                        refreshLoadingViewLayoutParams(child);
                    }
                    if (child instanceof AGErrorView) {
                        refreshErrorViewLayoutParams((AGErrorView) child);
                    }
                }
                view.measure(parentWidth, parentHeight);
                view.requestLayout();
                view.invalidate();
            }
        }
    }

    private void refreshLoadingViewLayoutParams(View loadingView) {
        FrameLayout.LayoutParams loadingViewParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        loadingViewParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        loadingView.setLayoutParams(loadingViewParams);

    }

    /**
     * Trick dla poprawnego ułożenia AGLoadingView w Galerii
     */
    private View loadingImageCenter(AGLoadingView loadingView) {
        FrameLayout frameLayout = new FrameLayout(mDisplay.getActivity());
        frameLayout.setBackgroundColor(Color.TRANSPARENT);
        frameLayout.addView(loadingView);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        refreshLoadingViewLayoutParams(loadingView);

        frameLayout.setLayoutParams(params);
        return frameLayout;
    }

    public void setPagerView(ViewPager viewPager) {
        mPagerView = viewPager;
    }

    public void setGalleryElements(List<AbstractAGElementDataDesc> collectionDataDesc) {
        mCollection = collectionDataDesc;
        setGalleryViews();
    }

    private void setGalleryViews() {
        AGScreenDataDesc screenDataDesc= AGApplicationState.getInstance().getCurrentScreenDesc();
        AbstractAGViewDataDesc viewDataDesc = ((AbstractAGViewDataDesc)((IAGView)mPagerView).getDescriptor());
        if(DataDescHelper.findDescendantById(screenDataDesc,viewDataDesc.getId())==null){
           return;
        }
        AGViewCalcDesc calcDesc = (AGViewCalcDesc) ((IAGView) mPagerView).getDescriptor().getCalcDesc();

        CalcManager calcManager = mDisplay.getCalcManager();
        for (AbstractAGElementDataDesc viewDesc : mCollection) {
            if (viewDesc instanceof AGLoadingDataDesc) {
                break;
            }
            if (viewDesc instanceof AGErrorDataDesc) {
                break;
            }
            ((AbstractAGViewDataDesc) viewDesc).setParentContainer((AbstractAGViewDataDesc) ((IAGView) mPagerView).getDescriptor());

            calcManager.measureBlockWidth(viewDesc, calcDesc.getContentSpaceWidth(), calcDesc.getContentSpaceWidth());
            calcManager.measureBlockHeight(viewDesc, calcDesc.getContentSpaceHeight(), calcDesc.getContentSpaceHeight());
            calcManager.layout(viewDesc);
        }
    }

    @Override
    public synchronized java.lang.Object instantiateItem(ViewGroup container, int position) {
        AbstractAGViewDataDesc viewDesc = (AbstractAGViewDataDesc) mCollection.get(position);
        View v = ViewFactoryManager.createViewHierarchy(viewDesc, mDisplay);
        if (v instanceof AGLoadingView) {
            v = loadingImageCenter((AGLoadingView) v);
        } else if (v instanceof AGErrorView) {
            v = errorImageCenter((AGErrorView) v);
        }
        ((IAGView)v).loadAssets();
        v.setSoundEffectsEnabled(false);
        container.addView(v);
        mViews.put(position, v);
        return v;
    }

    private View errorImageCenter(AGErrorView v) {
        FrameLayout frameLayout = new FrameLayout(mDisplay.getActivity());
        frameLayout.setBackgroundColor(Color.TRANSPARENT);
        frameLayout.addView(v);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        refreshErrorViewLayoutParams(v);

        frameLayout.setLayoutParams(params);
        return frameLayout;
    }

    private void refreshErrorViewLayoutParams(AGErrorView errorView) {
        FrameLayout.LayoutParams loadingViewParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);

        loadingViewParams.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        errorView.setLayoutParams(loadingViewParams);

    }

    @Override
    public void onPageScrolled(int i, float v, int i2) {
        // nothing to do
    }

    @Override
    public void onPageSelected(int i) {
        //save current page number
        mCurrentPageNumber = i;
    }

    @Override
    public void onPageScrollStateChanged(int i) {
        // nothing to do
    }

    @Override
    public int getCount() {
        return mCollection != null ? mCollection.size() : 0;
    }

    @Override
    public boolean isViewFromObject(View view, java.lang.Object o) {
        return view == o;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, java.lang.Object object) {
        container.removeView((View) object);
        mViews.remove(position);
    }

    @Override
    public int getItemPosition(java.lang.Object object) {
        return POSITION_NONE;
    }

    public int getCurrentPageNumber() {
        return mCurrentPageNumber;
    }

    public void setCurrentPageNumber(int currentPageNumber) {
        mCurrentPageNumber = currentPageNumber;
    }
}
