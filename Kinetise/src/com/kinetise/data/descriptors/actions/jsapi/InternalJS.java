package com.kinetise.data.descriptors.actions.jsapi;

import android.util.Log;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.overalymanager.OverlayManager;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.StringVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.ITextDescriptor;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;


public class InternalJS implements Internal {
    private Object mContext;


    public InternalJS() {
    }

    @Override
    public void setTextColor(String id, String color) {
        Object control = ActionManager.getInstance().getControl(null, id);
        setTextColor(control, color);
    }

    @Override
    public void setBackgroundColor(String id, String color) {
        Object control = ActionManager.getInstance().getControl(null, id);
       setBackgroundColor(control, color);
    }

    @Override
    public void update(String id) {
        Object control = ActionManager.getInstance().getControl(null, id);
        update(control);
    }

    @Override
    public String getTextColor(String id) {
        Object control = ActionManager.getInstance().getControl(null, id);
        return getTextColor(control);
    }

    @Override
    public String getBackgroundColor(String id) {
        Object control = ActionManager.getInstance().getControl(null, id);
        return getBackgroundColor(control);
    }

    @Override
    public String getText(String id) {
        Object control = ActionManager.getInstance().getControl(null, id);
        return getText(control);
    }

    @Override
    public void setText(String id, String text) {
        Object control = ActionManager.getInstance().getControl(null, id);
        setText(control, text);
    }

    @Override
    public String getThisTextColor() {
        return getTextColor(mContext);
    }

    @Override
    public void setThisTextColor(String color) {
        setTextColor(mContext, color);
    }

    @Override
    public String getThisBackgroundColor() {
        return getBackgroundColor(mContext);
    }

    @Override
    public void setThisBackgroundColor(String color) {
        setBackgroundColor(mContext, color);
    }

    @Override
    public String getThisText() {
        return getText(mContext);
    }

    @Override
    public void setThisText(String text) {
        setText(mContext, text);
    }

    public String getTextColor(Object control) {
        if (control instanceof ITextDescriptor) {
            ITextDescriptor controlDataDesc = (ITextDescriptor) control;
            int color = controlDataDesc.getTextDescriptor().getTextColor();
            Log.d("JSTest", "getTextColor: " + String.format("%08X", color));
            return String.format("%08X", color);

        }
        Log.d("JSTest", "getBackgroundColor: NaN ");
        return "NaN";
    }


    public void setTextColor(Object control, String color) {
        if (control instanceof ITextDescriptor) {
            ITextDescriptor textDataDesc = (ITextDescriptor) control;
            color.replace("0x", "");
            textDataDesc.getTextDescriptor().setTextColor(AGXmlParserHelper.getColorFromHex(color));
            Log.d("JSTest", "setTextColor: " + color);

        }
        update(control);
    }

    public String getBackgroundColor(Object control) {
        if (control instanceof AbstractAGViewDataDesc) {
            AbstractAGViewDataDesc controlDataDesc = (AbstractAGViewDataDesc) control;
            int color = controlDataDesc.getBackgroundColor();
            Log.d("JSTest", "getTextColor: " + String.format("%08X", color));
            return String.format("%08X", color);
        }
        Log.d("JSTest", "getBackgroundColor: NaN ");
        return "NaN";
    }

    public void setBackgroundColor(Object control, String color) {
        if (control instanceof AbstractAGViewDataDesc) {
            AbstractAGViewDataDesc textDataDesc = (AbstractAGViewDataDesc) control;
            color.replace("0x", "");
            textDataDesc.setBackgroundColor(AGXmlParserHelper.getColorFromHex(color));
            Log.d("JSTest", "setBackgroundColor: " + color);
        }
        update(color);
    }

    public String getText(Object control) {
        if (control instanceof ITextDescriptor) {
            ITextDescriptor controlDataDesc = (ITextDescriptor) control;
            VariableDataDesc text = controlDataDesc.getTextDescriptor().getText();
            String value = text.getStringValue();
            return value;
        }
        return "NaN";
    }

    public void setText(Object control, String text) {
        if (control instanceof ITextDescriptor) {
            ITextDescriptor textDataDesc = (ITextDescriptor) control;
            textDataDesc.getTextDescriptor().setText(new StringVariableDataDesc(text));
            if (OverlayManager.getInstance().isOverlayShown()) {
                OverlayManager.getInstance().getCurrentOverlayViewDataDesc().resolveVariables();
            }
            AGApplicationState applicationState = AGApplicationState.getInstance();
            applicationState.getCurrentScreenDesc().resolveVariables();
            applicationState.getSystemDisplay().recalculateAndLayoutScreen();
            update(control);
        }
    }

    private void update(Object control) {
        if (control instanceof AbstractAGViewDataDesc) {
            AbstractAGViewDataDesc controlDataDesc = (AbstractAGViewDataDesc) control;
            if (controlDataDesc.getOnUpdateListener() != null)
                controlDataDesc.getOnUpdateListener().onUpdated();
        }
    }


    public void setContext(Object contextControl) {
        mContext = contextControl;
    }
}
