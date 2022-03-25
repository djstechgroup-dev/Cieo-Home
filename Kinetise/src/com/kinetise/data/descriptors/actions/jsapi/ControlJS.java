package com.kinetise.data.descriptors.actions.jsapi;


import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.datadescriptors.components.ITextDescriptor;

public class ControlJS implements Control {

    private static ControlJS controlJS;

    public static ControlJS getInstance() {
        if (controlJS == null) {
            controlJS = new ControlJS();
        }
        return controlJS;
    }

    @Override
    public void setTextColor(String id, String color) {
        Object control = ActionManager.getInstance().getControl(null, id);

        if (control instanceof Control) {
            ///((Control) control).setTextColor(color);
        }
    }

    public void invalidate(String id) {
        Object control = ActionManager.getInstance().getControl(null, id);
    }

    public ITextDescriptor getControl(String controlId) {
        return (ITextDescriptor) ActionManager.getInstance().getControl(null, controlId);
    }
}
