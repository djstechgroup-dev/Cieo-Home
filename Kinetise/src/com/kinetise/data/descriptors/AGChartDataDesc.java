package com.kinetise.data.descriptors;

import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGDataFeedViewDataDesc;
import com.kinetise.data.descriptors.types.AGChartType;

import java.util.ArrayList;
import java.util.List;

public class AGChartDataDesc extends AbstractAGDataFeedViewDataDesc implements IFeedClient {

    private AGChartType mChartType;
    private List<DataSetDescriptor> mDataSetDescriptors;
    List<Integer> mColors;

    private String mLabelsItemPath;

    public AGChartDataDesc(String id) {
        super(id);
        mDataSetDescriptors = new ArrayList<>();
    }

    @Override
    public void setFeedDescriptor(DataFeed descriptor) {
        mDataFeed = descriptor;
    }

    @Override
    public AbstractAGElementDataDesc createInstance() {
        return null;
    }

    public void setChartType(AGChartType chartType) {
        mChartType = chartType;
    }

    public AGChartType getChartType() {
        return mChartType;
    }

    public void addDataSetDescriptor(DataSetDescriptor dataSetDescriptors) {
        mDataSetDescriptors.add(dataSetDescriptors);
    }

    public void clearDataSetDescriptors() {
        mDataSetDescriptors.clear();
    }

    public void setLabelsItemPath(String labelsItemPath) {
        mLabelsItemPath = labelsItemPath;
    }

    public void setColors(List<Integer> colors) {
        mColors = colors;
    }

    public String getLabelsItemPath() {
        return mLabelsItemPath;
    }

    public List<Integer> getColors() {
        return mColors;
    }

    public List<DataSetDescriptor> getDataSetDescriptors() {
        return mDataSetDescriptors;
    }

}