package com.kinetise.data.descriptors.actions.jsapi;

/**
 * Created by SzymonGrzeszczuk on 2017-02-07.
 */

public interface Internal {
    String getTextColor(String id);
    void setTextColor(String id, String color);
    String getBackgroundColor(String id);
    void setBackgroundColor(String id, String color);
    String getText(String id);
    void setText(String id, String text);
    String getThisTextColor();
    void setThisTextColor(String color);
    String getThisBackgroundColor();
    void setThisBackgroundColor(String color);
    String getThisText();
    void setThisText(String text);
    void update(String id);
}
