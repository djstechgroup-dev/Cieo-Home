package com.kinetise.data.application.overalymanager;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import com.kinetise.components.activity.KinetiseActivity;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.feedmanager.FeedManager;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.OverlayDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.IAGView;
import com.kinetise.data.systemdisplay.views.ViewFactoryManager;
import com.kinetise.helpers.RWrapper;
import com.kinetise.views.InterceptFrameLayout;

public class OverlayManager {
    private static final int MAIN_DISPLAY_ID = RWrapper.id.mainDisplay;
    private static final int ROOT_LAYOUT_ID = RWrapper.id.rootLayout;
    private static final int TRANSPARENT_BLACK = 0xff000000;
    private static final float TRANSPARENT_ALPHA = 0f;

    private static OverlayManager mInstance;

    private OverlayDataDesc mCurrentOverlayDataDesc;

    private OverlayAnimator mOverlayAnimator;

    public static OverlayManager getInstance() {
        if (mInstance == null) {
            synchronized (OverlayManager.class) {
                if (mInstance == null) {
                    mInstance = new OverlayManager();
                }
            }
        }

        return mInstance;
    }

    public static void clearInstance() {
        mInstance = null;
    }

    protected OverlayManager() {
    }

    public synchronized void showOverlay(final OverlayDataDesc overlayDataDesc, final SystemDisplay systemDisplay) {
        showOverlayInternal(overlayDataDesc, systemDisplay);
    }

    private void showOverlayInternal(OverlayDataDesc overlayDataDesc, SystemDisplay systemDisplay) {
        if (!isOverlayShown()) {
            closeKeyboard();
            mCurrentOverlayDataDesc = overlayDataDesc;

            AbstractAGViewDataDesc mainViewDesc = overlayDataDesc.getMainViewDesc();
            mainViewDesc.resolveVariables();

            Activity currentActivity = systemDisplay.getActivity();

            final ViewGroup rootView = getRootView(currentActivity);
            final ViewGroup mainDisplayView = getMainDisplayView(currentActivity);

            View overlayView = createOverlayView(systemDisplay, mainViewDesc);

            if (AGApplicationState.getInstance().getActivity()!=null) {
            }

            InterceptFrameLayout interceptView = createAndSetupInterceptView(systemDisplay);

            mOverlayAnimator = createOverlayAnimator(overlayDataDesc, interceptView, overlayView);

            mOverlayAnimator.addAndAnimateViews(systemDisplay, rootView, mainDisplayView);

            FeedManager.startLoadingFeeds(mainViewDesc, false, false);

            executeOnEnterAction();
        }
    }

    protected void closeKeyboard() {
        ((KinetiseActivity) AGApplicationState.getInstance().getActivity()).closeKeyboard();
    }

    protected OverlayAnimator createOverlayAnimator(OverlayDataDesc overlayDataDesc, InterceptFrameLayout interceptView, View overlayView) {
        return new OverlayAnimator(overlayView, interceptView, overlayDataDesc);
    }

    public boolean isOverlayShown() {
        return mOverlayAnimator != null;
    }


    protected ViewGroup getMainDisplayView(Activity currentActivity) {
        return (ViewGroup) currentActivity.findViewById(MAIN_DISPLAY_ID);
    }

    protected ViewGroup getRootView(Activity currentActivity) {
        return (ViewGroup) currentActivity.findViewById(ROOT_LAYOUT_ID);
    }

    public AbstractAGViewDataDesc getCurrentOverlayViewDataDesc() {
        if (mCurrentOverlayDataDesc != null)
            return mCurrentOverlayDataDesc.getMainViewDesc();
        else
            return null;
    }

    public View getOverlayView() {
        return mOverlayAnimator.getOverlayView();
    }

    private InterceptFrameLayout createAndSetupInterceptView(final SystemDisplay systemDisplay) {
        Runnable onTouchAction = new Runnable() {
            @Override
            public void run() {
                OverlayManager.getInstance().hideCurrentOverlay(systemDisplay);
            }
        };

        final InterceptFrameLayout interceptView = createInterceptView(systemDisplay.getActivity(), onTouchAction);

        if (mCurrentOverlayDataDesc.isGrayoutBackground()) {
            interceptView.setBackgroundColor(TRANSPARENT_BLACK);
        }

        interceptView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        interceptView.setAlpha(TRANSPARENT_ALPHA);

        return interceptView;
    }

    protected InterceptFrameLayout createInterceptView(Activity activity, Runnable onTouchAction) {
        return new InterceptFrameLayout(activity, onTouchAction);
    }

    private View createOverlayView(SystemDisplay systemDisplay, AbstractAGViewDataDesc mainViewDesc) {
        systemDisplay.runCalcManager(mainViewDesc);
        View overlayView = createViewFromDescriptor(systemDisplay, mainViewDesc);
        ((IAGView) overlayView).loadAssets();

        return overlayView;
    }

    public void recalculateOverlays(SystemDisplay systemDisplay) {
        if (isOverlayShown()) {
            Activity currentActivity = systemDisplay.getActivity();

            final ViewGroup mainDisplayView = getMainDisplayView(currentActivity);

            mOverlayAnimator.updateViewsPositions(systemDisplay, mainDisplayView);
        }
    }

    protected View createViewFromDescriptor(SystemDisplay systemDisplay, AbstractAGViewDataDesc overlayViewDesc) {
        return ViewFactoryManager.createViewHierarchy(overlayViewDesc, systemDisplay);
    }

    public void hideCurrentOverlay(final SystemDisplay systemDisplay) {
        if (mOverlayAnimator != null) {
            closeKeyboard();
            final Activity currentActivity = systemDisplay.getActivity();
            final ViewGroup mainDisplayView = getMainDisplayView(currentActivity);

            FeedManager.saveFeedsDataOfAllFeedsInside(mCurrentOverlayDataDesc.getMainViewDesc());

            mOverlayAnimator.animateAndRemoveViews(systemDisplay, mainDisplayView);
            mOverlayAnimator = null;
           executeOnExitAction();
        }
    }

    private void executeOnEnterAction() {
        VariableDataDesc onEnterAction = mCurrentOverlayDataDesc.getOnOverlayEnterAction();
        if(onEnterAction!=null){
            onEnterAction.resolveVariable();
        }
    }


    private void executeOnExitAction() {
        if (mCurrentOverlayDataDesc != null) {
            VariableDataDesc onExitAction = mCurrentOverlayDataDesc.getOnOverlayExitAction();
            if (onExitAction != null) {
                onExitAction.resolveVariable();
            }
        }
    }

}
