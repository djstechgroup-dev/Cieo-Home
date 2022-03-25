package com.kinetise.data.systemdisplay.views;

import android.view.View;
import com.kinetise.data.descriptors.datadescriptors.AGRadioButtonDataDesc;
import com.kinetise.data.systemdisplay.SystemDisplay;

public class AGRadioButtonView extends AbstractAGCompoundView<AGRadioButtonDataDesc> {

    public AGRadioButtonView(SystemDisplay display, AGRadioButtonDataDesc desc) {
        super(display, desc);
        if(desc.isChecked()) {
            setActive();
        }
    }
    
    @Override
    public void onClick(View view) {
        activateRadioButton();
    }

    public void activateRadioButton() {
        if (!mDescriptor.isChecked()) {
            mDescriptor.setChecked(true);
            mDescriptor.getParentContainer().onChange();
        }
    }

    public void setActive() {
        mImageView.setImageBitmap(mCheckedImageSource.getBitmap());
    }

    public void setInactive(){
        mImageView.setImageBitmap(mUncheckedImageSource.getBitmap());
    }

    @Override
    public void onStateChanged() {
        syncStateWithDescriptor();
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        syncStateWithDescriptor();
    }

    protected void syncStateWithDescriptor() {
        if (mDescriptor.isChecked())
            setActive();
        else
            setInactive();
    }
}
