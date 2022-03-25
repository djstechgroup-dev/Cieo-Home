package com.kinetise.data.descriptors.datadescriptors.components;

import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.AGSizeModeType;

public class BackgroundImageDescriptor extends ImageDescriptor {

    public BackgroundImageDescriptor(VariableDataDesc background) {
        super();
        mSizeMode = AGSizeModeType.SHORTEDGE;
        mImageSrc = background;
    }

}