package com.kinetise.data.descriptors.datadescriptors;


import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.IAGCollectionDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.RequiredField;
import com.kinetise.helpers.regexp.RegexpHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic class for templates - object that held part of descriptors hierarchy to be attached/added to screen
 */
public abstract class AbstractAGTemplateDataDesc implements IAGCollectionDataDesc {

    /**
     * Map of fields that are required in the item in order for given template to be used for it.
     * Key for the map is f
     */
    public List<RequiredField> mRequiredFields;
    private ArrayList<AbstractAGElementDataDesc> mControls = new ArrayList<AbstractAGElementDataDesc>();
    private List<AbstractAGElementDataDesc> mPresentControls = new ArrayList<AbstractAGElementDataDesc>();
    private int mTemplateNumber;

    public abstract AbstractAGTemplateDataDesc createInstance();

    public int getTemplateNumber() {
        return mTemplateNumber;
    }

    public void setTemplateNumber(int n) {
        mTemplateNumber = n;
    }

    @Override
    public void addControl(AbstractAGElementDataDesc control) {
        AbstractAGViewDataDesc view = (AbstractAGViewDataDesc) control;
        view.setSection(this);
        mControls.add(control);
        if (!view.isRemoved())
            mPresentControls.add(control);
    }

    @Override
    public List<AbstractAGElementDataDesc> getAllControls() {
        return mControls;
    }

    @Override
    public List<AbstractAGElementDataDesc> getPresentControls() {
        return mPresentControls;
    }

    @Override
    public void removeAllControls() {
        mControls.clear();
        mPresentControls.clear();
    }

    @Override
    public void removeControl(AbstractAGElementDataDesc control) {
        mControls.remove(control);
        mPresentControls.remove(control);
    }

    public List<RequiredField> getRequiredFields() {
        return mRequiredFields;
    }

    public void setRequiredFields(List<RequiredField> fields) {
        mRequiredFields = fields;
    }

    /**
     * Checks if all required fields for given template are present int given feed item
     *
     * @param item - Feed item to check agains
     * @return true - if all required fields can be found in parameter array; false - otherwise
     */
    public boolean templateMatches(DataFeedItem item) {
        if (mRequiredFields == null)
            return true;

        for (RequiredField field : mRequiredFields) {

            //We first check if given field is present in the feed item
            if (!item.containsFieldByKey(field.getName())) {
                return false;
            }

            //If it is we read field contents and run regexp under regexp name on it
            Object valueInItem = item.getByKey(field.getName());
            Object matchValue = field.getMatch();
            if (valueInItem instanceof String) {
                valueInItem = RegexpHelper.parseValue(field.getRegexName(), (String) valueInItem);
            }
            //If element is empty and allowEmpty is not set, template fails to match
            if (!field.isAllowEmpty() && ((valueInItem == null || valueInItem.equals("") || valueInItem instanceof DataFeedItem.NullItem)))
                return false;

            //lastly we check if match is non-empty in a rule, and if it is, we check if our field matches the rule
            if (matchValue.equals("")) {
                continue;
            } else if (!valueInItem.equals(matchValue)) {
                return false;
            }

        }

        return true;
    }

    @Override
    public AbstractAGElementDataDesc getParent() {
        return null;
    }

    public AbstractAGTemplateDataDesc copy() {

        AbstractAGTemplateDataDesc copied = createInstance();
        for (AbstractAGElementDataDesc desc : getAllControls()) {
            copied.addControl(desc.copy());
        }
        copied.setTemplateNumber(getTemplateNumber());
        copied.mRequiredFields = mRequiredFields;

        return copied;
    }

    public String getMessage() {
        return null;
    }

    public void resolveVariables() {
        mPresentControls.clear();
        for (AbstractAGElementDataDesc control : mControls) {
            control.resolveVariables();
            AbstractAGViewDataDesc view = (AbstractAGViewDataDesc) control;
            if (!view.isRemoved()) {
                mPresentControls.add(control);
            }
        }
    }
}
