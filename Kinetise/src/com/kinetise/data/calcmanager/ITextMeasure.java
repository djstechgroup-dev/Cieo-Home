package com.kinetise.data.calcmanager;

public interface ITextMeasure {

    public double getMeasureHeight();

    public double getMeasureWidth();

    public void measure(String text, double width);

    public void setTextParams(int maxLines, int maxCharacters, boolean underline, boolean italic, boolean bold, double fontSize);
}
