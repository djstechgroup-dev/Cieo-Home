package com.kinetise.data.systemdisplay.views;

import android.view.View;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.drawing.ViewDrawer;

public interface IAGView extends View.OnClickListener {

	AbstractAGElementDataDesc getDescriptor();
    SystemDisplay getSystemDisplay();

    IAGView getAGViewParent();

    ViewDrawer getViewDrawer();

    void loadAssets();

    boolean accept(IViewVisitor visitor);

    void setDescriptor(AbstractAGElementDataDesc descriptor);

}
