package com.kinetise.data.descriptors.datadescriptors.components;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.StringVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.AGHttpMethodType;
import com.kinetise.data.descriptors.types.AGSizeModeType;

import java.io.Serializable;

public class ImageDescriptor implements Serializable {
    protected AGSizeModeType mSizeMode;
    protected VariableDataDesc mImageSrc;
    protected HttpParamsDataDesc mHttpParamsDataDesc;
    protected HttpParamsDataDesc mHeaderParamsDataDesc;
    protected HttpParamsDataDesc mBodyParams;
    protected AGHttpMethodType mHttpMethod;
    protected String mRequestBodyTrasform;

    public String getImageSource() {
        if (mImageSrc == null) {
            return null;
        }
        return mImageSrc.getStringValue();
    }

    public void resolveVariable() {
        mImageSrc.resolveVariable();
        if (mHttpParamsDataDesc != null)
            mHttpParamsDataDesc.resolveVariables();
        if (mHeaderParamsDataDesc != null)
            mHeaderParamsDataDesc.resolveVariables();
    }

    public AGSizeModeType getSizeMode() {
        return mSizeMode;
    }

    public void setImageSrc(VariableDataDesc imageSrc) {
        mImageSrc = imageSrc;
    }

    public void setImageSrc(String src) {
        mImageSrc = new StringVariableDataDesc(src);
    }

    public void setSizeMode(AGSizeModeType sizeMode) {
        mSizeMode = sizeMode;
    }

    public HttpParamsDataDesc getHttpParams() {
        return mHttpParamsDataDesc;
    }

    public void setHttpParams(HttpParamsDataDesc pHttpParamsDataDesc) {
        mHttpParamsDataDesc = pHttpParamsDataDesc;
    }

    public HttpParamsDataDesc getHeaders() {
        return mHeaderParamsDataDesc;
    }

    public void setHeaderParams(HttpParamsDataDesc pHeaderParamsDataDesc) {
        mHeaderParamsDataDesc = pHeaderParamsDataDesc;
    }

    public HttpParamsDataDesc getBodyParams() {
        return mBodyParams;
    }

    public void setBodyParams(HttpParamsDataDesc pHeaderParamsDataDesc) {
        mBodyParams = pHeaderParamsDataDesc;
    }

    public ImageDescriptor copy(AbstractAGElementDataDesc parent) {
        ImageDescriptor copy = new ImageDescriptor();
        copy.mSizeMode = mSizeMode;
        if (mImageSrc != null) {
            copy.mImageSrc = mImageSrc.copy(parent);
        }
        if (mHttpParamsDataDesc != null) {
            copy.mHttpParamsDataDesc = mHttpParamsDataDesc.copy(parent);
        }
        if (mHeaderParamsDataDesc != null) {
            copy.mHeaderParamsDataDesc = mHeaderParamsDataDesc.copy(parent);
        }
        if (mBodyParams != null) {
            copy.mBodyParams = mBodyParams.copy(parent);
        }
        if (mHttpMethod != null) {
            copy.mHttpMethod = mHttpMethod;
        }
        copy.mRequestBodyTrasform = mRequestBodyTrasform;
        return copy;
    }

    public void setHttpMethod(AGHttpMethodType methodType) {
        mHttpMethod = methodType;
    }

    public AGHttpMethodType getHttpMethod() {
        return mHttpMethod;
    }

    public void setRequestBodyTrasform(String requestBodyTrasform) {
        mRequestBodyTrasform = requestBodyTrasform;
    }

    public String getRequestBodyTrasform() {
        return mRequestBodyTrasform;
    }

    public boolean hasDecorator() {
        return mImageSrc != null && mImageSrc.getValue() != null;
    }
}