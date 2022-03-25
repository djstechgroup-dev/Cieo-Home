package com.kinetise.data.adapters.chart;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.kinetise.data.descriptors.DataSetDescriptor;

import java.util.List;

public class LineChartDataAdapter extends ChartDataAdapter{

    public LineChartDataAdapter(List<DataSetDescriptor> dataSetDescriptors, List<Integer> colors, String labelsItemPath) {
        super(dataSetDescriptors, colors, labelsItemPath);
    }

    @Override
    protected IDataSet createDataSet(String path, String label, int index) {
        LineDataSet lineDataSet = new LineDataSet(createEntryArray(path), label);
        lineDataSet.setColor(getColorForIndex(index));
        lineDataSet.setCircleColor(getColorForIndex(index));
        lineDataSet.setDrawCircleHole(false);
        return lineDataSet;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Entry createEntry(float value, int index) {
        return new Entry(value,index);
    }

    @Override
    protected ChartData createChartData() {
        LineData data = new LineData(prepareLabels());
        createDataSets(data);
        return data;
    }


}
