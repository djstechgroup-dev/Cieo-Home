package com.kinetise.app.views;

import android.view.LayoutInflater;
import android.view.View;

import com.kinetise.data.descriptors.datadescriptors.AGCustomControlDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.views.AGCustomControlView;
import com.kinetise.stub.R;


public class CustomInflatedLayout extends AGCustomControlView {
    private View mRootView;

    public CustomInflatedLayout(SystemDisplay display, AGCustomControlDataDesc desc) {
        super(display, desc);
        mRootView = LayoutInflater.from(display.getActivity()).inflate(R.layout.custom_layout, this);
    }
}

