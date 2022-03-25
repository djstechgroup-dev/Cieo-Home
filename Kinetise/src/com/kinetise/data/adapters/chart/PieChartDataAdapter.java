package com.kinetise.data.adapters.chart;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.kinetise.data.descriptors.DataSetDescriptor;

import java.util.List;

public class PieChartDataAdapter extends ChartDataAdapter {
    public PieChartDataAdapter(List<DataSetDescriptor> dataSetDescriptors, List<Integer> colors, String labelsItemPath) {
        super(dataSetDescriptors, colors, labelsItemPath);
    }

    @Override
    protected PieData createChartData() {
        PieData data = new PieData(prepareLabels());
        createDataSets(data);
        return data;
    }

    @Override
    protected void createDataSets(ChartData data) {
        DataSetDescriptor dataSetDescriptor = mDataSetDescriptors.get(0);
        data.addDataSet(createDataSet(dataSetDescriptor.getDataPath(), dataSetDescriptor.getName(), 0));
    }

    @Override
    protected PieDataSet createDataSet(String path, String label, int index) {
        List<Entry> values = createEntryArray(path);
        PieDataSet pieDataSet = new PieDataSet(values, label);
        pieDataSet.setColors(getPieColorsArray(values.size()));
        return pieDataSet;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Entry createEntry(float value, int index) {
        return new Entry(value,index);
    }

    private int[] getPieColorsArray(int size) {
        int colors[] = new int[size];
        for (int i = 0; i < size; i++)
            colors[i] = getColorForIndex(i);
        return colors;
    }
}
