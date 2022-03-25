package com.kinetise.data.calcmanager;

import java.util.ArrayList;
import java.util.List;

public class ChildrenLayout{
    private List<Integer> numberOfChildrenInRow;
    private List<Double> rowHeights;
    private List<Double> rowWidths;

    public ChildrenLayout(){
        numberOfChildrenInRow = new ArrayList<Integer>();
        rowHeights = new ArrayList<Double>();
        rowWidths = new ArrayList<Double>();
    }

    public void addRow(int numberOfChildren, double rowWidth, double rowHeight){
        numberOfChildrenInRow.add(numberOfChildren);
        rowWidths.add(rowWidth);
        rowHeights.add(rowHeight);
    }

    public int getNumberOfChildrenInRow(int rowNumber){
        return numberOfChildrenInRow.get(rowNumber);
    }

    public double getRowHight(int rowNumber){
        return rowHeights.get(rowNumber);
    }

    public double getRowWidth(int rowNumber){
        return rowWidths.get(rowNumber);
    }

    public int getNumberOfRows(){
        return numberOfChildrenInRow.size();
    }

    public double getCombinedRowsHight(){
        double sumOfHights = 0;
        for(int row=0;row<getNumberOfRows();row++){
            sumOfHights += getRowHight(row);
        }
        return sumOfHights;
    }

    public double getWidestRowsWidth(){
        double widest = 0;
        for(int row=0;row<getNumberOfRows();row++){
            widest = Math.max(widest,getRowWidth(row));
        }
        return widest;
    }
}
