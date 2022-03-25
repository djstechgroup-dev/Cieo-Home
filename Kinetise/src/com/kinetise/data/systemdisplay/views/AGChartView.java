package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.view.View;

import com.github.mikephil.charting.charts.Chart;
import com.kinetise.data.adapters.chart.ChartDataAdapter;
import com.kinetise.data.adapters.chart.ChartDataAdaptersFactory;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.calcmanager.CalcManager;
import com.kinetise.data.descriptors.AGChartDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGElementCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.systemdisplay.IFeedView;
import com.kinetise.data.systemdisplay.IRebuildableView;
import com.kinetise.data.systemdisplay.LayoutHelper;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.TemplateInflater;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.helpers.drawing.ViewDrawer;

public class AGChartView extends AGControl<AGChartDataDesc> implements IRebuildableView, IFeedView {
    Chart chart;
    ChartDataAdapter mChartDataAdapter;
    View loadingView;
    View errorView;
    View noDataView;

    public AGChartView(SystemDisplay systemDisplay, AGChartDataDesc descriptor) {
        super(systemDisplay, descriptor);
        chart = createInnerchartView();
        mChartDataAdapter = createChartDataAdapter();
        if (chart != null)
            addView(chart);
        prepareTemplateViews(systemDisplay);
    }

    private void prepareTemplateViews(SystemDisplay systemDisplay) {
        loadingView = TemplateInflater.inflateTemplate(getDescriptor().getLoadingTemplate(), systemDisplay);
        initializeInnerView(loadingView);
        errorView = TemplateInflater.inflateTemplate(getDescriptor().getErrorTemplate(), systemDisplay);
        initializeInnerView(errorView);
        noDataView = TemplateInflater.inflateTemplate(getDescriptor().getNoDataTemplate(), systemDisplay);
        initializeInnerView(noDataView);
        showNoData();
    }

    private void initializeInnerView(View view) {
        if (view != null) {
            addView(view);
            hideInnerView(view);
        }
    }

    private Chart createInnerchartView() {
        return ChartViewFactory.createChartView(mDescriptor.getChartType(), getSystemDisplay().getActivity());
    }

    private ChartDataAdapter createChartDataAdapter() {
        return ChartDataAdaptersFactory.getChartDataAdapter(getDescriptor().getChartType(), getDescriptor().getDataSetDescriptors(), getDescriptor().getColors(), getDescriptor().getLabelsItemPath());
    }

    @Override
    public void loadAssets() {
        super.loadAssets();

        //todo move background loading to AGControl
        String baseUrl = mDescriptor.getFeedBaseAdress();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return mDrawer;
    }

    @Override
    public void rebuildView() {
        DataFeed feedDescriptor = mDescriptor.getFeedDescriptor();
        if (feedDescriptor != null && feedDescriptor.isEmpty()) {
            showNoData();
        }

        mChartDataAdapter.setDataFeed(feedDescriptor);
        chart.setData(mChartDataAdapter.getChartData());
        chart.invalidate();
    }

    public void showLoading() {
        showInnerView(loadingView);
    }

    public void hideLoading() {
        hideInnerView(loadingView);
    }

    public void showError() {
        showInnerView(errorView);
    }

    public void hideError() {
        hideInnerView(errorView);
    }

    public void showNoData() {
        showInnerView(noDataView);
    }

    public void hideNoData() {
        hideInnerView(noDataView);
    }

    private void showInnerView(View view) {
        if (view != null) {
            view.setVisibility(VISIBLE);
        }
    }

    private void hideInnerView(View view) {
        if (view != null)
            view.setVisibility(GONE);
    }

    private void hideAllInnerViews() {
        hideError();
        hideLoading();
        hideNoData();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        LayoutHelper.measureFill(chart, getDescriptor().getCalcDesc());
        measureInnerView(loadingView);
        measureInnerView(errorView);
        measureInnerView(noDataView);
    }

    private void measureInnerView(View innerView) {
        if (innerView != null) {
            AGViewCalcDesc calcDesc = getDescriptor().getCalcDesc();
            double contentSpaceWidth = calcDesc.getContentSpaceWidth();
            double contentSpaceHeight = calcDesc.getContentSpaceHeight();
            CalcManager.getInstance().measureBlockWidth(((IAGView) innerView).getDescriptor(), contentSpaceWidth, contentSpaceWidth);
            CalcManager.getInstance().measureBlockHeight(((IAGView) innerView).getDescriptor(), contentSpaceHeight, contentSpaceHeight);
            AGElementCalcDesc loadingCalcDesc = ((IAGView) innerView).getDescriptor().getCalcDesc();
            innerView.measure(loadingCalcDesc.getWidthAsMeasureSpec(), loadingCalcDesc.getHeightAsMeasureSpec());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        getViewDrawer().refresh();
        LayoutHelper.layoutFill(chart, getDescriptor().getCalcDesc());
        layoutInnerView(loadingView);
        layoutInnerView(errorView);
        layoutInnerView(noDataView);
        super.onLayout(changed, left, top, right, bottom);
    }

    public void layoutInnerView(View view) {
        if (view != null) {
            LayoutHelper.layoutCenter(view, (AGViewCalcDesc) ((IAGView) view).getDescriptor().getCalcDesc(), getDescriptor().getCalcDesc());
        }
    }

    @Override
    public void notifyLoadingStarted() {
        hideAllInnerViews();
        showLoading();
    }

    @Override
    public void notifyDataChanged() {
        hideAllInnerViews();
    }

    @Override
    public void notifyDownloadError() {
        hideAllInnerViews();
        showError();
    }


}
