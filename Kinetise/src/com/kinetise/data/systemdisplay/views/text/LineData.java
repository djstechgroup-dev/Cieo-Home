package com.kinetise.data.systemdisplay.views.text;

import java.io.Serializable;

public class LineData implements Serializable{
    public String text;
    public double width;
    public int positionX;
    public int positionY;

    public LineData(){

    }

    public LineData(String text, double width) {
        this.text = text;
        this.width = width;
    }
}
