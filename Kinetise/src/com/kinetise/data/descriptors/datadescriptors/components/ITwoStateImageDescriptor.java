package com.kinetise.data.descriptors.datadescriptors.components;

public interface ITwoStateImageDescriptor extends IImageDescriptor{
    public ImageDescriptor getActiveImageDescriptor();
    void setActiveImageDescriptor(ImageDescriptor activeImageDescriptor);
}
