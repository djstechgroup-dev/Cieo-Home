package com.kinetise.data.descriptors.actions.jsapi;


public interface Screen {
    void go(String screenId);
    void backById(String screenID);
    void backBySteps(int steps);

    void refresh();

    void reload();
}