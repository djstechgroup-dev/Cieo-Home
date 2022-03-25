package com.kinetise.data.adapters.chart;

import com.kinetise.data.descriptors.DataSetDescriptor;
import com.kinetise.data.descriptors.types.AGChartType;

import java.security.InvalidParameterException;
import java.util.List;

public class ChartDataAdaptersFactory {
    public static ChartDataAdapter getChartDataAdapter(AGChartType chartType, List<DataSetDescriptor> dataSetDescriptors, List<Integer> colors, String labelsItemPath) {
        switch(chartType){
            case LINE:
                return new LineChartDataAdapter(dataSetDescriptors, colors, labelsItemPath);
            case PIE:
                return new PieChartDataAdapter(dataSetDescriptors, colors, labelsItemPath);
            case BAR:
            case HORIZONTAL_BAR:
                return new BarChartDataAdapter(dataSetDescriptors, colors, labelsItemPath);

        }

        throw new InvalidParameterException("No chart adapter found for type "+ chartType);
    }
}
