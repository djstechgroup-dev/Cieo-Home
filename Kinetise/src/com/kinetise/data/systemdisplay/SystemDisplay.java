package com.kinetise.data.systemdisplay;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.kinetise.components.activity.KinetiseActivity;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.externalapplications.IExternalApplication;
import com.kinetise.data.application.feedmanager.FeedManager;
import com.kinetise.data.application.overalymanager.OverlayManager;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.calcmanager.CalcManager;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.desctriptorvisitors.DepthDescVisitor;
import com.kinetise.data.descriptors.desctriptorvisitors.RemoveCalcDescVisitor;
import com.kinetise.data.descriptors.desctriptorvisitors.ResetScrollAndFeedVisitor;
import com.kinetise.data.descriptors.helpers.DataDescHelper;
import com.kinetise.data.descriptors.types.AGScreenTransition;
import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.systemdisplay.views.AGContainerView;
import com.kinetise.data.systemdisplay.views.AGLoadingView;
import com.kinetise.data.systemdisplay.views.AGPopupView;
import com.kinetise.data.systemdisplay.views.AGScreenView;
import com.kinetise.data.systemdisplay.views.IAGView;
import com.kinetise.data.systemdisplay.views.ViewFactoryManager;
import com.kinetise.data.systemdisplay.views.scrolls.InnerScroll;
import com.kinetise.data.systemdisplay.viewvisitors.FindViewByDescriptorVisitor;
import com.kinetise.data.systemdisplay.viewvisitors.RequestLayoutVisitor;
import com.kinetise.support.logger.Logger;

import java.security.InvalidParameterException;

public class SystemDisplay {
    private static final int MAX_DEPTH = 9;
    private AGScreenView mScreenView;
    transient private KinetiseActivity mActivity;
    private int mWidth;
    private int mHeight;
    private AGScreenDataDesc mCurrentScreenDesc;
    private static Dialog mCurrentLoadingDialog;

    public SystemDisplay(KinetiseActivity activity) {
        mActivity = activity;
        if (activity == null) {
            throw new InvalidParameterException("PlatformView parameter have to implement IPlatformView interface");
        }
    }

    public KinetiseActivity getActivity() {
        return mActivity;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setDisplaySize(final int width, final int height) {
        mWidth = width;
        mHeight = height;
    }

    // MAIN PUBLIC SYSTEM DISPLAY METHODS

    /**
     * Displays whole new screen including running calculates, creating all views, starting feed download
     *
     * @param screenDataDesc screen desc to repaint
     */
    public void displayScreen(final AGScreenDataDesc screenDataDesc, final boolean resetScreen, AGScreenTransition transition) {
        mCurrentScreenDesc = substituteScreenIfHierarchyTooDeep(screenDataDesc);

        resetScrollsAndFeeds(resetScreen);

        final SystemDisplay display = this;
        runCalcManager(mCurrentScreenDesc);
        final IAGView newView;
        try {
            newView = (IAGView) ViewFactoryManager.createViewHierarchy(mCurrentScreenDesc, display);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return;
        }


        if (!(newView instanceof AGScreenView)) {
            throw new InvalidParameterException("Created view should be an AGScreenView mInstance");
        }

        mScreenView = (AGScreenView) newView;

        mScreenView.loadAssets();

        mActivity.setMainView(display, mScreenView, transition);

        // TODO: Przesunąć do ScreenLoader, SystemDisplay powinien zarządzać jedynie wyświetlaniem, nie logiką ani danymi. Pewnie wymaga wyprostowania wątków najpierw.
        FeedManager.startLoadingFeeds(mCurrentScreenDesc, resetScreen, false);

        onScreenLoaded();
        setStatusBar(screenDataDesc.getStatusBarColor(), screenDataDesc.isStatusBarColorInvert(), mActivity.getWindow());
    }

    private void resetScrollsAndFeeds(boolean resetScreen) {
        if (resetScreen) {
            ResetScrollAndFeedVisitor visitor = new ResetScrollAndFeedVisitor();
            if (mCurrentScreenDesc != null) {
                mCurrentScreenDesc.accept(visitor);
            }
        }
    }

    /**
     * Reloads single feed based on new descriptor. Whole screen is recalculated but only feed children views are affected (removed/created, assets reloaded).
     *
     * @param elementDataDesc feed desc to reload
     */
    public void reloadFeed(final IFeedClient elementDataDesc) {
        runCalcManager(mCurrentScreenDesc);
        if (OverlayManager.getInstance().isOverlayShown()) {
            runCalcManager(OverlayManager.getInstance().getCurrentOverlayViewDataDesc());
        }
        reloadFeedViewForDescriptor((AbstractAGElementDataDesc) elementDataDesc);
    }

    /**
     * Recalculates, reloads assets and redraws whole screen, but without removing/creating any views.
     */
    public void recalculateAndLayoutScreen() {
        if (mCurrentScreenDesc != null) {
            runCalcManager(mCurrentScreenDesc);
            RequestLayoutVisitor visitor = new RequestLayoutVisitor();
            mScreenView.accept(visitor);
            mScreenView.measure(0, 0);

            if (OverlayManager.getInstance().isOverlayShown()) {
                View overlayView = OverlayManager.getInstance().getOverlayView();
                IAGView agView = ((IAGView) overlayView);
                AbstractAGViewDataDesc overlayDataDesc = OverlayManager.getInstance().getCurrentOverlayViewDataDesc();
                runCalcManager(overlayDataDesc);
                RequestLayoutVisitor layoutVisitor = new RequestLayoutVisitor();
                agView.accept(layoutVisitor);
            }

            if (PopupManager.getCurrentPopupView() != null) {
                AGPopupView currentDialog = PopupManager.getCurrentPopupView();
                runCalcManager(currentDialog.getDescriptor());
                RequestLayoutVisitor popupVisitor = new RequestLayoutVisitor();
                currentDialog.accept(popupVisitor);
            }
        }
    }

    // END OF MAIN SYSTEM DISPLAY OPERATIONS


    public IAGView getScreenView() {
        return mScreenView;
    }

    public AGScreenDataDesc getCurrentScreen() {
        return mCurrentScreenDesc;
    }

    private void reloadFeedViewForDescriptor(final AbstractAGElementDataDesc desc) {
        FindViewByDescriptorVisitor findViewByDescriptorVisitor = new FindViewByDescriptorVisitor(desc);
        if (mScreenView.accept(findViewByDescriptorVisitor) || (OverlayManager.getInstance().isOverlayShown() && ((AGContainerView) OverlayManager.getInstance().getOverlayView()).accept(findViewByDescriptorVisitor))) {
            View foundView = findViewByDescriptorVisitor.getFoundView();
            if (foundView instanceof IRebuildableView && foundView instanceof IAGView) {
                if (foundView instanceof InnerScroll) {
                    foundView = (View) foundView.getParent();
                }
                ((IAGView) foundView).setDescriptor(desc);
                ((IRebuildableView) foundView).rebuildView();
                ((IAGView) foundView).loadAssets();
                ((IFeedClient) desc).setIsLoadingMore(false);
            }
        }
    }

    public static boolean isScreenBlocked() {
        return mCurrentLoadingDialog != null;
    }

    public void blockScreenWithLoadingDialog(final boolean screenDim) {
        blockScreenWithLoadingDialog(screenDim, mActivity);
    }

    public static void blockScreenWithLoadingDialog(final boolean screenDim, final Activity activity) {
        blockScreenWithLoadingDialog(screenDim, activity, false);
    }

    public static void blockScreenWithLoadingDialog(final boolean screenDim, final Activity activity, boolean isTransparent) {
        try {
            if (screenDim) {
                final AGLoadingView loadingView = new AGLoadingView(activity);
                final RelativeLayout loadingWrapper = new RelativeLayout(activity);
                loadingWrapper.setGravity(Gravity.CENTER);
                loadingWrapper.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                if (!isTransparent)
                    loadingWrapper.addView(loadingView);
                if (mCurrentLoadingDialog == null) {
                    mCurrentLoadingDialog = new Dialog(activity);
                    mCurrentLoadingDialog.setContentView(loadingWrapper);
                    mCurrentLoadingDialog.setCancelable(false);
                    Window window = mCurrentLoadingDialog.getWindow();
                    if (isTransparent) {
                        window.setBackgroundDrawable(new ColorDrawable(0x00000000));
                    } else {
                        window.setBackgroundDrawable(new ColorDrawable(0x90000000));
                    }
                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    window.setGravity(Gravity.CENTER_HORIZONTAL);
                    if (AGApplicationState.getInstance().getCurrentScreenDesc() != null) {
                        AGScreenDataDesc currentScreenDesc = AGApplicationState.getInstance().getCurrentScreenDesc();
                        setStatusBar(currentScreenDesc.getStatusBarColor(), currentScreenDesc.isStatusBarColorInvert(), window);
                    }
                    mCurrentLoadingDialog.show();
                }
            } else {
                if (mCurrentLoadingDialog != null) {
                    mCurrentLoadingDialog.dismiss();
                    mCurrentLoadingDialog = null;
                }
            }
        } catch (Exception e) {
            ExceptionManager.getInstance().handleException(e);
        }
    }

    public static void setStatusBar(int color, boolean invertIconColor, Window window) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            setStatusBarIconColorMode(invertIconColor, window);
            window.setStatusBarColor(color);
        }
    }

    private static void setStatusBarIconColorMode(boolean invertIconColor, Window window) {
        if (invertIconColor)
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        else
            window.getDecorView().setSystemUiVisibility(0);
    }

    public void openExternalApplication(final IExternalApplication externalApplication) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logger.v(this, "openExternalApplication");
                try {
                    externalApplication.open(getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void destroy() {
        try {
            RemoveCalcDescVisitor visitor = new RemoveCalcDescVisitor();

            if (mCurrentScreenDesc != null) {
                mCurrentScreenDesc.accept(visitor);
            }

        } catch (Exception e) {
            ExceptionManager.getInstance().handleException(e);
        }
        mActivity = null;
    }

    public CalcManager getCalcManager() {
        return CalcManager.getInstance();
    }

    public boolean isGoogleMapSupported() {
        return mActivity.isGoogleMapSupported();
    }

    private AGScreenDataDesc substituteScreenIfHierarchyTooDeep(AGScreenDataDesc screenDataDesc) {
        DepthDescVisitor depthDescVisitor = new DepthDescVisitor();
        screenDataDesc.accept(depthDescVisitor);
        int depth = depthDescVisitor.getDepth();

        if (depth > MAX_DEPTH) {
            return DataDescHelper.ExceedDepthDescriptorFactory.createFor(screenDataDesc.getScreenId());
        } else {
            return screenDataDesc;
        }
    }

    /**
     * Calculates proper values for given elementDataDesc
     * depending on width and height of display
     */
    public void runCalcManager(AbstractAGElementDataDesc elementDataDesc) {
        measureAndLayout(elementDataDesc, mWidth, mHeight);
    }

    private void measureAndLayout(AbstractAGElementDataDesc elementDataDesc, double width, double height) {
        CalcManager calcManager = getCalcManager();
        calcManager.initCalcManager(mWidth, mHeight);
        calcManager.measureBlockWidth(elementDataDesc, width, width);
        calcManager.measureBlockHeight(elementDataDesc, height, height);
        calcManager.layout(elementDataDesc);
    }

    public void onScreenLoading() {
        if (mActivity != null && mActivity.getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mActivity.getCurrentFocus().getApplicationWindowToken(), 0);
        }
    }

    public void onScreenLoaded() {
        mActivity.detachMapFragment();
    }

    public void addWebViewCallback(AGWebViewCallback pAGWebViewCallback) {
        pAGWebViewCallback.attach(mActivity);
    }

    public void removeWebViewCallback(AGWebViewCallback pAGWebViewCallback) {
        if (pAGWebViewCallback != null) {
            pAGWebViewCallback.unattach();
        }
    }

    public void pauseBackgroundVideo() {
        AGScreenView screenView = (AGScreenView) getScreenView();
        if (screenView != null) {
            screenView.pauseBackgroundVideo();
        }
    }

    public void startBackgroundVideo() {
        AGScreenView screenView = (AGScreenView) getScreenView();
        if (screenView != null) {
            screenView.startBackgroundVideo();
        }
    }
}
