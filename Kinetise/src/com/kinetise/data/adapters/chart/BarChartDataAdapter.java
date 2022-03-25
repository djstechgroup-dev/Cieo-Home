package com.kinetise.data.adapters.chart;


import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.kinetise.data.descriptors.DataSetDescriptor;

import java.util.ArrayList;
import java.util.List;

public class BarChartDataAdapter extends ChartDataAdapter {

    public BarChartDataAdapter(List<DataSetDescriptor> dataSetDescriptors, List<Integer> colors, String labelsItemPath) {
        super(dataSetDescriptors, colors, labelsItemPath);
    }

    @Override
    protected BarData createChartData() {
        BarData data = new BarData(prepareLabels());
        createDataSets(data);
        return data;
    }

    @Override
    protected BarDataSet createDataSet(String path, String label, int index) {
        BarDataSet barDataSet = new BarDataSet(createEntryArray(path), label);
        barDataSet.setColor(getColorForIndex(index));
        return barDataSet;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ArrayList<BarEntry> createEntryArray(String valueItemPathName) {
        return super.createEntryArray(valueItemPathName);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected BarEntry createEntry(float value, int index) {
        return new BarEntry(value, index);
    }
}
