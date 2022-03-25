package com.kinetise.data.descriptors.datadescriptors.feeddatadesc;

import com.kinetise.data.descriptors.datadescriptors.AbstractAGTemplateDataDesc;

public class AGItemTemplateDataDesc extends AbstractAGTemplateDataDesc {
    private String mDetailScreenId;

    @Override
    public AGItemTemplateDataDesc createInstance() {
        return new AGItemTemplateDataDesc();
    }

    @Override
    public AGItemTemplateDataDesc copy() {
        AGItemTemplateDataDesc copiedDesc = (AGItemTemplateDataDesc) super.copy();
        copiedDesc.setDetailScreenId(mDetailScreenId);
        return copiedDesc;
    }

    public String getDetailScreenId() {
        return mDetailScreenId;
    }

    public void setDetailScreenId(String detailScreenId) {
        mDetailScreenId = detailScreenId;
    }
}
