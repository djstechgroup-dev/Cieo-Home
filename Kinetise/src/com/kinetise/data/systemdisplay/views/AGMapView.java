package com.kinetise.data.systemdisplay.views;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterManager;
import com.kinetise.components.activity.KinetiseActivity;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGMapDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGTemplateDataDesc;
import com.kinetise.data.descriptors.datadescriptors.MapPopupDataDesc;
import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.data.systemdisplay.IRebuildableView;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.bitmapsettercommands.SetPinBitmapCommand;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.data.systemdisplay.helpers.AGViewHelper;
import com.kinetise.data.systemdisplay.helpers.ViewIdGenerator;
import com.kinetise.data.systemdisplay.views.maps.AGMapFragment;
import com.kinetise.data.systemdisplay.views.maps.CustomPinRenderer;
import com.kinetise.data.systemdisplay.views.maps.IMapPopUpClick;
import com.kinetise.data.systemdisplay.views.maps.PinInfo;
import com.kinetise.data.systemdisplay.views.text.TiledView;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.drawing.ViewDrawer;
import com.kinetise.support.logger.Logger;
import com.kinetise.support.scrolls.scrollManager.EventDirection;
import com.kinetise.support.scrolls.scrollManager.ScrollManager;
import com.kinetise.support.scrolls.scrollManager.ScrollType;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AGMapView extends AGControl<AGMapDataDesc> implements IAGView, IMapPopUpClick, IRebuildableView, IScrollable, CustomPinRenderer.RendererState {
    private ArrayList<PinInfo> mPins = new ArrayList<>();
    private AGMapFragment mMapFragment;
    private Map<String, Bitmap> bitmapDictionary = new HashMap<>();
    private ClusterManager<PinInfo> mClusterManager;
    private boolean mapIsReady = true;
    private CustomPinRenderer.RendererState mRendererState;
    private boolean isFirstDisplayMapPin = true;
    private boolean pinsReadyToDraw = false;

    /**
     * AGMapView constructor is always being called twice - once to initialize the view, second time when {@link com.kinetise.data.sourcemanager.AssetsManager SourceManager} has retrieved
     * all required data (from web or assets). To avoid glitches we want {@link AGMapView#replaceFragment() replaceFragment} method to be called only the second time.
     * We use static sRefreshTagCounter to count how many times we called constructor with given AGMapDataDesc, which is uniqe for each view.
     */
    public AGMapView(SystemDisplay display, AGMapDataDesc desc) {
        super(display, desc);
        this.setId(ViewIdGenerator.generateViewId(mDescriptor.getId()));
        DataFeed feed = mDescriptor.getFeedDescriptor();
        replaceFragment();
        initializeClusterManager();
        mRendererState = this;
        if (feed != null) {
            if (feed.getItemsCount() > 0) {
                initializePinList(feed);
            }
        }

    }


    private void initializeClusterManager() {
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMapFragment.setGoogleMap(googleMap);
                mClusterManager = new ClusterManager(getContext(), googleMap);
                googleMap.setOnCameraChangeListener(mClusterManager);
                googleMap.setOnMarkerClickListener(mClusterManager);
                mClusterManager.getMarkerManager();
                mClusterManager.setOnClusterItemClickListener(mMapFragment);
                mClusterManager.setRenderer(new CustomPinRenderer(getContext(), googleMap, mClusterManager, mRendererState));
                drawPinsOnMap();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // at the moment AGMapView can have one child - either fragment container or map placeholder
        View child = getChildAt(0);

        if (child != null && getDescriptor().getCalcDesc() != null) {
            AGViewCalcDesc calcDesc = getDescriptor().getCalcDesc();
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(calcDesc.getViewWidth(), MeasureSpec.EXACTLY);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(calcDesc.getViewHeight(), MeasureSpec.EXACTLY);
            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        // at the moment AGMapView can have one child - either fragment container with actual map or map placeholder
        View child = getChildAt(0);
        if (child != null) {
            // layout to match parent, minus padding and border
            AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();

            int childLeft = (int) Math.round(calcDesc.getBorder().getLeft() + calcDesc.getPaddingLeft());
            int childTop = (int) Math.round(calcDesc.getBorder().getTop() + calcDesc.getPaddingTop());
            int childRight = childLeft + (int) Math.round(calcDesc.getContentWidth());
            int childBottom = childTop + (int) Math.round(calcDesc.getContentHeight());
            child.layout(childLeft, childTop, childRight, childBottom);
        }
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        if (!mDisplay.isGoogleMapSupported()) {
            //mozliwe ze mozna przeniesc do konstruktora AGMapView
            showMapPlaceholder();
        }
    }

    private void showMapPlaceholder() {
        Logger.v(this, "showMapPlaceholder");

        for (int x = 0; x < getChildCount(); x++) {
            getChildAt(x).clearAnimation();
        }
        removeAllViews();
        TiledView tv = new TiledView(getContext());
        tv.setText(LanguageManager.getInstance().getString(LanguageManager.MAP_KEY_NOT_FOUND));
        this.addView(tv);
    }


    public void replaceFragment() {
        mMapFragment = new AGMapFragment();
        mMapFragment.setMarkerClickListener(this);

        try {
            FragmentManager fragmentManager = (mDisplay.getActivity()).getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(this.getId(), mMapFragment, AGMapFragment.TAG);
            fragmentTransaction.disallowAddToBackStack();
            fragmentTransaction.commitAllowingStateLoss();
        } catch (Exception e) {
            ExceptionManager.getInstance().handleException(e, false);
        }
        mMapFragment.setDesc(mDescriptor);
    }

    @Override
    public void rebuildView() {
        initializePinList(mDescriptor.getFeedDescriptor());
    }

    /**
     * {@link com.kinetise.data.sourcemanager.AbstractGetSourceCommand IGetSourceCommand}
     * powinien byc callbackiem do {@link com.kinetise.data.systemdisplay.views.maps.PinInfo PinInfo}, tam ustawiamy bitmape.
     * Dodatkowo, kazdy z naszych pinow ma callback do mapview, zeby po ustawieniu dodal sie sam do pinlist w mapview
     */
    public synchronized void initializePinList(DataFeed feed) {
        pinsReadyToDraw = false;
        if (!mapIsReady || feed == null) {
            return;
        }

        mapIsReady = false;


        int[] pinDimensions = AGViewHelper.calculateMapPinSize();
        bitmapDictionary.clear();
        for (PinInfo p : mPins) {
            p.clearPinBitmap();
        }
        mPins.clear();
        PinInfo pin;
        for (DataFeedItem item : feed.getItems()) {
            try {
                pin = readPin(item, feed, pinDimensions);
                if (pin != null) {
                    mPins.add(pin);
                    bitmapDictionary.put(pin.getImageAddress(), null);
                }
            } catch (Exception e) {
                PopupManager.showErrorPopup(LanguageManager.getInstance().getString(LanguageManager.ERROR_DATA));
                mMapFragment.hideLoading();
                return;
            }
        }
        downloadBitmaps(pinDimensions);
    }

    private void downloadBitmaps(int[] dimensions) {
        for (String address : bitmapDictionary.keySet()) {
            SetPinBitmapCommand command = new SetPinBitmapCommand(mDescriptor.getFeedBaseAdress(), address, dimensions[0], dimensions[1], this);
            AssetsManager.getInstance().getAsset(command, AssetsManager.ResultType.IMAGE, mDescriptor.getHeaders(), mDescriptor.getHttpParams(), null);
        }
    }

    public void addBitmapToDictionaryAndUpdateIfallAdded(String address, Bitmap bitmap) {
        bitmapDictionary.put(address, bitmap);
        if (!bitmapDictionary.containsValue(null)) {
            setBitmapToPins();
            pinsReadyToDraw = true;
            drawPinsOnMap();
        }
    }

    private void drawPinsOnMap() {
        if (mClusterManager != null && pinsReadyToDraw) {
            if (mPins.size() == 0) {
                mapIsReady = true;
                return;
            }
            mClusterManager.clearItems();
            mClusterManager.addItems(mPins);

            if (isFirstDisplayMapPin) {
                mMapFragment.scrollMapForClustering(mPins);
                isFirstDisplayMapPin = false;
            } else {
                mClusterManager.cluster();
            }
        }
    }

    private void setBitmapToPins() {
        for (PinInfo p : mPins) {
            p.setPinBitmap(bitmapDictionary.get(p.getImageAddress()));
        }
    }

    private PinInfo readPin(DataFeedItem item, DataFeed feed, int[] pinDimensions) {
        String x = "";
        String y = "";
        String GUID = "";
        String guidNodeName = mDescriptor.getGUIDNodeName();
        String latitudeNodeName = mDescriptor.getLatitudeNodeName();
        String longtitudeNodeName = mDescriptor.getLongtitudeNodeName();
        if (item.containsFieldByKey(longtitudeNodeName) && item.containsFieldByKey(latitudeNodeName)) {
            y = item.getByKey(latitudeNodeName).toString();
            x = item.getByKey(longtitudeNodeName).toString();
            GUID = item.getByKey(guidNodeName).toString();
        }
        if (x.equals("") || y.equals("")) {
            return null; //xml is ok but coordinates are null - don't add pin
        }
        double lat;
        double lon;

        PinInfo pinInfo = new PinInfo();
        try {
            lat = Double.parseDouble(y);
            lon = Double.parseDouble(x);
        } catch (Exception e) {
            return null;
        }
        pinInfo.setLatLng(new LatLng(lat, lon));
        final int index = feed.getItems().indexOf(item);
        pinInfo.setIndex(index);
        pinInfo.setPinWidth(pinDimensions[0]);
        pinInfo.setPinHeight(pinDimensions[1]);
        pinInfo.setGUID(GUID);
        VariableDataDesc source = mDescriptor.getPinImageAdress().copy(new AbstractAGViewDataDesc("") {
            @Override
            public AbstractAGElementDataDesc createInstance() {
                return null;
            }

            @Override
            public int getFeedItemIndex() {
                return index;
            }
        });
        source.resolveVariable();
        pinInfo.setImageAddress(source.getStringValue());
        return pinInfo;
    }

    @Override
    public boolean accept(IViewVisitor visitor) {
        return visitor.visit(this);
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
    public void onMarkerClick(PinInfo pin) {
        mDescriptor.setActiveItemIndex(pin.getIndex());
        if (getDescriptor().isShowMapPopup()) {
            AbstractAGTemplateDataDesc descCopy = mDescriptor.getTempleteDataDesc(0);
            MapPopupDataDesc desc = new MapPopupDataDesc(descCopy);
            desc.setItemIndex(pin.getIndex());
            KinetiseActivity activity = mDisplay.getActivity();
            PopupManager.showMapPopup(desc);
        }
    }


    // nieskonczony scroll
    @Override
    public ScrollType getScrollType() {
        return ScrollType.FREESCROLL;
    }

    @Override
    public int getScrollXValue() {
        return Integer.MAX_VALUE / 2;
    }

    @Override
    public int getScrollYValue() {
        return Integer.MAX_VALUE / 2;
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
        return Integer.MAX_VALUE;
    }

    @Override
    public int getContentHeight() {
        return Integer.MAX_VALUE;
    }

    @Override
    public EventDirection getEventDirectionForScrollType() {
        return EventDirection.UNKNOWN;
    }

    @Override
    public String getTag() {
        return "AGMapView" + super.getTag();
    }


    public void hideLoading() {
        mMapFragment.hideLoading();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result = false;

        int actionType = ev.getAction();
        switch (actionType) {
            case MotionEvent.ACTION_DOWN:

                ScrollManager.getInstance().setUpdate(this, ev);
                onTouchEvent(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                mMapFragment.onMoveMapView();
                ScrollManager scrollManager = ScrollManager.getInstance();
                scrollManager.setUpdate(this, ev);

                result = true;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        Logger.d("TouchManager", "onInterceptTouchEvent: event[" + ev.getAction() + "], result[" + result + "], desc[" +
                getDescriptor().toString() + "]");

        return super.onInterceptTouchEvent(ev);
    }

    public void scrollViewTo(int x, int y) {
        scrollTo(x, y);
    }

    public void restoreScroll() {
    }

    @Override
    public void setBackgroundBitmap(Bitmap bitmap) {

    }

    @Override
    public void onMarkerIsReady() {
        mapIsReady = true;
    }
}
