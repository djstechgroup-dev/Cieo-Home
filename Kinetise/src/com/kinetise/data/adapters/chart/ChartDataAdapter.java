package com.kinetise.data.adapters.chart;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.descriptors.DataSetDescriptor;

import java.util.ArrayList;
import java.util.List;

public abstract class ChartDataAdapter {

    DataFeed mDataFeed;
    ChartData data;

    List<DataSetDescriptor> mDataSetDescriptors;
    List<Integer> mColors;

    String mLabelsItemPath;

    public ChartDataAdapter(List<DataSetDescriptor> dataSetDescriptors, List<Integer> colors, String labelsItemPath) {
        mDataSetDescriptors = dataSetDescriptors;
        mColors = colors;
        mLabelsItemPath = labelsItemPath;
    }

    public void setDataFeed(DataFeed dataFeed) {
        mDataFeed = dataFeed;
        refreshChartData();
    }

    private void refreshChartData() {
        if(mDataFeed!=null){
            data = createChartData();
        }
        else
            data = null;
    }

    protected abstract ChartData createChartData();

    protected void createDataSets(ChartData data) {
        for(int i=0;i<mDataSetDescriptors.size();i++){
            DataSetDescriptor dataSetDescriptor = mDataSetDescriptors.get(i);
            data.addDataSet(createDataSet(dataSetDescriptor.getDataPath(), dataSetDescriptor.getName(), i));
        }
    }

    protected abstract IDataSet createDataSet(String path, String label, int index);

    protected List<String> prepareLabels() {
        ArrayList<String> labels = new ArrayList<>();
        for (DataFeedItem item : mDataFeed.getItems()) {
            if (item.containsFieldByKey(mLabelsItemPath)) {
                Object value = item.getByKey(mLabelsItemPath);
                labels.add(value.toString());
            }
            else
                labels.add("");
        }
        return labels;
    }

    protected int getColorForIndex(int i){
        return mColors.get(i%mColors.size());
    }

    protected <T extends Entry> ArrayList<T> createEntryArray(String valueItemPathName) {
        ArrayList<T> values = new ArrayList<>();
        int i=0;
        for (DataFeedItem item : mDataFeed.getItems()) {
            if (item.containsFieldByKey(valueItemPathName)) {
                Object value = item.getByKey(valueItemPathName);
                try{
                    float floatValue = Float.parseFloat(value.toString());
                    values.add((T) createEntry(floatValue, i));
                }
                catch(NumberFormatException e){

                }
            }
            i++;
        }
        return values;
    }

    protected abstract <T extends Entry> T createEntry(float value, int index);

    public ChartData getChartData() {
        return data;
    }
}
