package com.kinetise.data.descriptors.datadescriptors;

public class AGPinchImageDataDesc extends AGTextImageDataDesc {
    public AGPinchImageDataDesc(String id) {
        super(id);
    }

    @Override
    public AGTextImageDataDesc createInstance() {
        return new AGPinchImageDataDesc(getId());
    }
}
