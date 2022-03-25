package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.types.FormString;

public class AGRadioButtonDataDesc extends AbstractAGCompoundButtonDataDesc {

    private int mIndex;
    private FormString mValue;
    private AbstractAGRadioGroupDataDesc mParentRadioGroup;

    public AGRadioButtonDataDesc(String id) {
        super(id);
    }

    public void setRadioGroupIndex(int index) {
        mIndex = index;
    }

    public int getRadioGroupIndex() {
        return mIndex;
    }

    public FormString getValue() {
        return mValue;
    }

    public void setValue(String value) {
        mValue = new FormString(value);
    }

    @Override
    public void setChecked(boolean check) {
        super.setChecked(check);
        if (check && (mParentRadioGroup != null)) {
            mParentRadioGroup.checkAt(mIndex);
        }
    }

    public void setParentRadioGroup(AbstractAGRadioGroupDataDesc parentRadioGroup) {
        mParentRadioGroup = parentRadioGroup;
    }

    @Override
    public AbstractAGCompoundButtonDataDesc createInstance() {
        return new AGRadioButtonDataDesc(getId());
    }

    @Override
    public AGRadioButtonDataDesc copy() {
        AGRadioButtonDataDesc copy = (AGRadioButtonDataDesc) super.copy();
        copy.setValue(mValue.toString());
        return copy;
    }


}
