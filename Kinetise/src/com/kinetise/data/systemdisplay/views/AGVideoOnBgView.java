package com.kinetise.data.systemdisplay.views;

import android.content.Context;
import android.view.View;
import com.dd.crop.TextureVideoView;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.drawing.ViewDrawer;

public class AGVideoOnBgView extends TextureVideoView implements IAGView{
    private AbstractAGElementDataDesc mDescriptor;
    private final SystemDisplay mDisplay;

    public AGVideoOnBgView(Context context, SystemDisplay display, AbstractAGElementDataDesc desc) {
        super(context);
        mDisplay = display;
        mDescriptor = desc;
    }

    @Override
    public AbstractAGElementDataDesc getDescriptor() {
        return mDescriptor;
    }

    @Override
    public SystemDisplay getSystemDisplay() {
        return mDisplay;
    }

    @Override
    public IAGView getAGViewParent() {
        return (IAGView) getParent();
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return null;
    }

    @Override
    public void loadAssets() {

    }

    @Override
    public boolean accept(IViewVisitor visitor) {
        return false;
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        mDescriptor = descriptor;
    }

    @Override
    public void onClick(View v) {

    }
}
