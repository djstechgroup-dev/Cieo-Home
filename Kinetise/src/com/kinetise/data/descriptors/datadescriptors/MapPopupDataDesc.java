package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.AbstractAGSectionDataDesc;
import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;

import java.security.InvalidParameterException;
import java.util.List;

public class MapPopupDataDesc extends AbstractAGTemplateDataDesc {
    private String mMessage;

    public MapPopupDataDesc(AbstractAGTemplateDataDesc desc) {

        if (desc != null) {

            List<AbstractAGElementDataDesc> controls = desc.getAllControls();

            if (controls.size() != 1 || !(controls.get(0) instanceof AbstractAGContainerDataDesc)) {
                throw new InvalidParameterException("Template must have only one child which is mInstance of" +
                        "AbstractAGContainerDataDesc");
            }

            AbstractAGContainerDataDesc containerDataDesc = (AbstractAGContainerDataDesc) controls.get(0);
            AbstractAGContainerDataDesc newDesc = containerDataDesc.copy();
            addControl(newDesc);
        }
    }

    public void setItemIndex(int index) {
        List<AbstractAGElementDataDesc> controls = this.getAllControls();
        for (int i = 0; i < controls.size(); i++) {
            setItemIndex(controls.get(i), index);
        }
    }

    private void setItemIndex(AbstractAGElementDataDesc control, int index) {
        if (control instanceof AbstractAGViewDataDesc) {
            ((AbstractAGViewDataDesc) control).setFeedItemIndex(index);
        }
        if (control instanceof AbstractAGContainerDataDesc) {
            List<AbstractAGElementDataDesc> elements = ((AbstractAGContainerDataDesc) control).getAllControls();
            for (int i = 0; i < elements.size(); i++) {
                setItemIndex(elements.get(i), index);
            }
            return;
        }

        if (control instanceof AbstractAGSectionDataDesc) {
            List<AbstractAGElementDataDesc> elements = ((AbstractAGSectionDataDesc) control).getAllControls();
            for (int i = 0; i < elements.size(); i++) {
                setItemIndex(elements.get(i), index);
            }
        }
    }

    @Override
    public MapPopupDataDesc createInstance() {
        return new MapPopupDataDesc(null);
    }

    @Override
    public MapPopupDataDesc copy() {
        return (MapPopupDataDesc) super.copy();
    }


    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    @Override
    public AbstractAGElementDataDesc getParent() {
        return null;
    }
}
