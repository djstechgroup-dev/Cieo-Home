package com.kinetise.data.systemdisplay.views;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.kinetise.data.descriptors.types.AGChartType;

public class ChartViewFactory {


    public static final int ALPHA = 0x66; //40%

    public static Chart createChartView(AGChartType chartType, Activity activity) {
        switch (chartType){
            case LINE:
                LineChart lineChart = new LineChart(activity);
                setDefaults(lineChart);
                return lineChart;
            case PIE:
                return createPieChartView(activity);
            case BAR:
                BarChart barChart = new BarChart(activity);
                setDefaults(barChart);
                return barChart;
            case HORIZONTAL_BAR:
                HorizontalBarChart horizontalBarChart = new HorizontalBarChart(activity);
                setDefaults(horizontalBarChart);
                return horizontalBarChart;
        }
        return null;
    }

    @NonNull
    private static PieChart createPieChartView(Activity activity) {
        PieChart pieChart = new PieChart(activity);
        pieChart.setHighlightPerTapEnabled(false);
        pieChart.setDrawHoleEnabled(false);
        pieChart.setHoleRadius(0);
        pieChart.setTransparentCircleAlpha(ALPHA);
        pieChart.setDescription("");
        return pieChart;
    }

    private static void  setDefaults(Chart chart){
        chart.setDescription("");
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
    }
}
