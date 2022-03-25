package com.kinetise.data.systemdisplay.views;

import android.content.pm.ApplicationInfo;
import android.graphics.*;
import android.net.Uri;
import android.os.Build;
import android.os.Message;
import android.view.*;
import android.webkit.*;
import android.widget.LinearLayout;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.externalapplications.WebBrowserApp;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGWebBrowserDataDesc;
import com.kinetise.data.systemdisplay.AGWebViewCallback;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.PdfManager;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.helpers.drawing.BackgroundSetterCommandCallback;
import com.kinetise.support.logger.Logger;
import com.kinetise.support.scrolls.scrollManager.EventDirection;
import com.kinetise.support.scrolls.scrollManager.ScrollManager;
import com.kinetise.support.scrolls.scrollManager.ScrollType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.InvalidParameterException;

public class AGWebBrowserView extends AGControl<AGWebBrowserDataDesc> implements IAGView, BackgroundSetterCommandCallback {
    private LinearLayout mLoadingOverlay;
    private InnerWebBrowserView mInnerView;

    public AGWebBrowserView(SystemDisplay display, AGWebBrowserDataDesc desc) {
        super(display, desc);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (0 != (mDisplay.getActivity().getApplicationInfo().flags &= ApplicationInfo.FLAG_DEBUGGABLE)) {
                WebView.setWebContentsDebuggingEnabled(true);
            }
        }

        initInnerView(display, desc);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        AGViewCalcDesc mCalcDesc = mDescriptor.getCalcDesc();
        int left = (int) (Math.round(mCalcDesc.getPaddingLeft() + mCalcDesc.getBorder().getLeft()));
        int top = (int) ((Math.round(mCalcDesc.getBorder().getTop() + mCalcDesc.getPaddingTop())));
        int right = (int) Math.round(mCalcDesc.getBlockWidth() - mCalcDesc.getPaddingRight() - mCalcDesc.getBorder().getRightAsInt());
        int bottom = (int) Math.round(mCalcDesc.getBlockHeight() - mCalcDesc.getPaddingBottom() - mCalcDesc.getBorder().getBottomAsInt());

        mInnerView.layout(left, top, right, bottom);

        if (mLoadingOverlay != null) {
            mLoadingOverlay.layout(0, 0, r - l, b - t);
        }
    }

    private void createAndShowLoadingOverlay(SystemDisplay display) {
        if (mLoadingOverlay == null && display != null) {
            mLoadingOverlay = new LinearLayout(display.getActivity());
            AGLoadingView loadingView = new AGLoadingView(display.getActivity());
            loadingView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            mLoadingOverlay.setGravity(Gravity.CENTER);
            if (getDescriptor().getBackgroundColor() != -1)
                mLoadingOverlay.setBackgroundColor(getDescriptor().getBackgroundColor());
            else
                mLoadingOverlay.setBackgroundColor(Color.WHITE);
            mLoadingOverlay.addView(loadingView);
            addView(mLoadingOverlay);
        }
    }

    private void initInnerView(SystemDisplay pDisplay, AbstractAGElementDataDesc pDesc) {
        mInnerView = new InnerWebBrowserView(pDisplay, pDesc);
        addView(mInnerView, 0);
    }

    private void setJavascriptWidth(int javascript) {
        Logger.v(this, "setJavascriptWidth", String.format("New size of javascript window: %d", javascript));
        mInnerView.setJavascriptWidth(javascript);

    }

    public void onCallbackPause() {
        mInnerView.onCallbackPause();

    }

    public void onCallbackResume() {
        mInnerView.onCallbackResume();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        AGViewCalcDesc mCalcDesc = getDescriptor().getCalcDesc();
        int mPWidth = (int) (Math.round(mCalcDesc.getBlockWidth() - mCalcDesc.getMarginRight() - mCalcDesc.getMarginLeft() + mCalcDesc.getPositionX()) - Math.round(mCalcDesc.getPositionX()));
        int mPHeight = (int) (Math.round(mCalcDesc.getBlockHeight() - mCalcDesc.getMarginTop() - mCalcDesc.getMarginBottom() + mCalcDesc.getPositionY()) - Math.round(mCalcDesc.getPositionY()));
        Logger.v(this, "onMeasure", String.format("Web broser size %d x %d", mPWidth, mPHeight));
        int width = MeasureSpec.makeMeasureSpec(mPWidth, MeasureSpec.EXACTLY);
        int height = MeasureSpec.makeMeasureSpec(mPHeight, MeasureSpec.EXACTLY);

        super.onMeasure(width, height);

        setMeasuredDimension(mPWidth, mPHeight);

        if (mLoadingOverlay != null) {
            mLoadingOverlay.measure(mPWidth, mPHeight);
        }

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
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
    public void loadAssets() {
        super.loadAssets();
        mInnerView.loadAssets();
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
        super.setDescriptor(descriptor);
        mInnerView.setDescriptor(descriptor);
    }

    @Override
    public void onClick(View v) {

    }

    public void pauseTimers() {
        mInnerView.pauseTimers();

    }

    public void resumeTimers() {
        mInnerView.resumeTimers();

    }

    class innerJavaScriptInterface {

        public void setContentWidth(String value) {
            if (value != null) {
                setJavascriptWidth(Integer.parseInt(value));
            }
        }
    }

    class InnerWebBrowserView extends WebView implements IAGView, IScrollable, PdfManager.PdfCallback {

        //wartosci constów wg. http://elastos.org/elorg_files/ReleaseDocs/ElastosOnlineDoc/html/class_web_view_core_1_1_event_hub.html
        private static final int ON_PAUSE = 143;
        private static final int ON_RESUME = 144;
        private static final String ABOUT_BLANK = "about:blank";
        private AGWebBrowserDataDesc mDataDesc;
        private AGWebViewCallback mCalback;

        private final SystemDisplay mSystemDisplay;
        private boolean mPaused = false;
        private boolean mHadUserInteraction = false;

        @Override
        protected void onSizeChanged(int w, int h, int ow, int oh) {
            super.onSizeChanged(w, h, ow, oh);
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {
            super.onLayout(changed, l, t, r, b);
        }


        public InnerWebBrowserView(SystemDisplay display, AbstractAGElementDataDesc desc) {
            super(display.getActivity());

            mCalback = new AGWebViewCallback(AGWebBrowserView.this);
            display.addWebViewCallback(mCalback);
            mSystemDisplay = display;
            mDataDesc = (AGWebBrowserDataDesc) desc;
            WebSettings webSettings = getSettings();
            webSettings.setJavaScriptEnabled(true);

            addJavascriptInterface(new innerJavaScriptInterface(), "KinetiseJSHTMLOUT");
            final InnerWebBrowserView self = this;

            setWebChromeClient(new WebChromeClient());

            setWebViewClient(new WebViewClient() {

                private String mFirstPageUrl;

                @Override
                public void onScaleChanged(WebView view, float oldScale, float newScale) {
                    super.onScaleChanged(view, oldScale, newScale);
                    int javascript = (int) (getContentWidth() * newScale);
                    setJavascriptWidth(javascript);
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    if (mFirstPageUrl == null)
                        mFirstPageUrl = url;
                    createAndShowLoadingOverlay(mDisplay);
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    if (!self.mPaused) {
                        String command;
                        command = "javascript:window.KinetiseJSHTMLOUT.setContentWidth(document.getElementsByTagName('html')[0].scrollWidth);";
                        self.loadUrl(command);
                    }

                    hideLoadingView();
                }

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (mDataDesc.isGoToExternalBrowser() && mHadUserInteraction && mFirstPageUrl != null && !mFirstPageUrl.equals(url)) {
                        mHadUserInteraction = false;
                        WebBrowserApp webBrowserApp = new WebBrowserApp(Uri.parse(url));
                        mDisplay.openExternalApplication(webBrowserApp);
                        return true;
                    } else {
                        return false;
                    }
                }
            });

            // we need to register if user interaction (touch) happened, so we can differentiate URL change for redirect and link change
            // not perfect
            setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (v.getId() == InnerWebBrowserView.this.getId() && event.getAction() == MotionEvent.ACTION_UP) {
                        mHadUserInteraction = true;
                    }

                    return false;
                }
            });
        }

        private void hideLoadingView() {
            if (mLoadingOverlay != null) {
                mLoadingOverlay.setVisibility(GONE);
                mLoadingOverlay.removeAllViews();
                mLoadingOverlay = null;
            }
        }

        @Override
        public void pdfDownloaded(String filepath) {
            loadUrl(filepath);
        }

        private void setJavascriptWidth(int javascript) {

            Logger.v("WebViewLog", String.format("New size of javascript window: %d", javascript));
        }

        public void onCallbackPause() {
            loadUrl(ABOUT_BLANK);
            mPaused = true;
            callOnPause();
        }

        private void callOnPause() {
            try {
                Field field = WebView.class.getDeclaredField("mWebViewCore");
                field.setAccessible(true);
                java.lang.Object o = field.get(this);


                Method m = Class.forName("android.webkit.WebViewCore").getDeclaredMethod("sendMessage", Message.class);

                m.setAccessible(true);
                m.invoke(o, Message.obtain(null, ON_PAUSE));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void callOnResume() {
            try {
                Field field = WebView.class.getDeclaredField("mWebViewCore");
                field.setAccessible(true);
                java.lang.Object o = field.get(this);

                Method m = Class.forName("android.webkit.WebViewCore").getDeclaredMethod("sendMessage", Message.class);
                m.setAccessible(true);
                m.invoke(o, Message.obtain(null, ON_RESUME));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void onCallbackResume() {
            mPaused = false;
            callOnResume();
            String url = mDataDesc.getSource().getStringValue();
            loadWebpage(url);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            stopLoading();
            if (getSystemDisplay() != null) {
                getSystemDisplay().removeWebViewCallback(mCalback);
            }
            mCalback = null;
            setWebChromeClient(null);
        }

        @Override
        public AbstractAGElementDataDesc getDescriptor() {
            return mDataDesc;
        }

        @Override
        public SystemDisplay getSystemDisplay() {
            return mSystemDisplay;
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
            return null;
        }

        @Override
        public void loadAssets() {
            String url = mDataDesc.getSource().getStringValue();
            loadWebpage(url);
        }

        @Override
        public boolean accept(IViewVisitor visitor) {
            return visitor.visit(this);
        }

        @Override
        public void setDescriptor(AbstractAGElementDataDesc descriptor) {
            mDataDesc = (AGWebBrowserDataDesc) descriptor;
        }

        @Override
        public void onClick(View v) {
            VariableDataDesc action = mDataDesc.getOnClickActionDesc();
            if (action != null) {
                action.resolveVariable();
            } else {
                ViewParent parent = getParent();
                if (parent instanceof IAGView) {
                    ((IAGView) parent).onClick(v);
                }
            }
        }

        @Override
        public ScrollType getScrollType() {
            return ScrollType.FREESCROLL;
        }

        @Override
        public int getScrollXValue() {
            return getScrollX();
        }

        @Override
        public int getScrollYValue() {
            return getScrollY();
        }

        @Override
        public int getViewPortWidth() {
            return getWidth();
        }

        @Override
        public int getViewPortHeight() {
            return getHeight();
        }

        @Override
        public int getContentWidth() {
            return computeHorizontalScrollRange();
        }

        @Override
        public int getContentHeight() {
            return computeVerticalScrollRange();
        }

        @Override
        public String getTag() {
            return "webBrowser" + mDataDesc.getId();
        }

        @Override
        public EventDirection getEventDirectionForScrollType() {
            return EventDirection.UNKNOWN;
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent ev) {
            boolean result = false;
            int actionType = ev.getAction();
            switch (actionType) {
                case MotionEvent.ACTION_DOWN:
                    Logger.v(this, "onInterceptTouchEvent", "Action down");

                    ScrollManager.getInstance().setUpdate(this, ev);
                    break;
                case MotionEvent.ACTION_MOVE:
                    Logger.v(this, "onInterceptTouchEvent", "Action move");

                    ScrollManager.getInstance().setUpdate(this, ev);

                    if (ScrollManager.getInstance().getEventDirection() == EventDirection.VERTICAL) {
                        float deltaY = ScrollManager.getInstance().getMotionEventDeltaY();
                        result = ScrollManager.getInstance().anyChildCanScroll(this, 0.0f, deltaY);
                        Logger.v(this, "onInterceptTouchEvent", "Vertical scroll: deltaY:" + deltaY + " result:" + result);
                    }
                    if (ScrollManager.getInstance().getEventDirection() == EventDirection.HORIZONTAL) {
                        float deltaX = ScrollManager.getInstance().getMotionEventDeltaX();
                        result = ScrollManager.getInstance().anyChildCanScroll(this, deltaX, 0.0f);
                        Logger.v(this, "onInterceptTouchEvent", "Horizontal scroll: deltaX:" + deltaX + " result:" + result);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    Logger.v(this, "onInterceptTouchEvent", String.format("ScrollVerticalView [%s]: onInterceptTouchEvent[CANCEL]", getTag()));
                    break;
                case MotionEvent.ACTION_UP:
                    Logger.v(this, "onInterceptTouchEvent", String.format("ScrollVerticalView [%s]: onInterceptTouchEvent[UP]", getTag()));
                    break;
                default:
                    break;
            }
            return result;
        }

        private void loadWebpage(String url) {
            createAndShowLoadingOverlay((SystemDisplay) AGApplicationState.getInstance().getSystemDisplay());
            if (url.endsWith(".pdf") || url.contains("type=pdf")) {
                PdfManager.getInstance().getPdf(url, this, getContext());
            } else {
                loadUrl(url);
            }
        }

        @Override
        public void loadUrl(String url) {
            Logger.v(this, "loadUrl", "Loading url " + url);
            super.loadUrl(url);
        }

        public void scrollViewTo(int x, int y) {
            scrollTo(x, y);
        }

        public void restoreScroll() {
        }
    }

}
