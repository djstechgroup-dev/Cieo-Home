package com.kinetise.data.descriptors;

import com.kinetise.data.descriptors.actions.StringVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.AGHttpMethodType;

import java.io.Serializable;


public class SynchronizationMethodDataDesc implements Serializable {

    protected VariableDataDesc mSrc;
    protected HttpParamsDataDesc mHttpParamsDataDesc;
    protected HttpParamsDataDesc mHeaderParamsDataDesc;
    protected HttpParamsDataDesc mBodyParams;
    protected AGHttpMethodType mHttpMethod;
    protected String mRequestBodyTrasform;

    public String getSource() {
        if (mSrc == null) {
            return null;
        }
        return mSrc.getStringValue();
    }

    public void resolveVariable() {
        mSrc.resolveVariable();
        if (mHttpParamsDataDesc != null)
            mHttpParamsDataDesc.resolveVariables();
        if (mHeaderParamsDataDesc != null)
            mHeaderParamsDataDesc.resolveVariables();
    }

    public void setSrc(VariableDataDesc src) {
        mSrc = src;
    }

    public void setImageSrc(String src) {
        mSrc = new StringVariableDataDesc(src);
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

    public SynchronizationMethodDataDesc copy(AbstractAGElementDataDesc parent) {
        SynchronizationMethodDataDesc copy = new SynchronizationMethodDataDesc();
        if (mSrc != null) {
            copy.mSrc = mSrc.copy(parent);
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
        return mSrc != null && mSrc.getValue() != null;
    }

}
