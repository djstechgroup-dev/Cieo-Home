package com.kinetise.data.descriptors;

import com.kinetise.data.descriptors.actions.VariableDataDesc;

import java.io.Serializable;

/**
 * @author: Marcin Narowski
 * Date: 03.04.14
 * Time: 12:53
 */
public class HttpParamsElementDataDesc implements Serializable {

    private String mParamName;
    private VariableDataDesc mParamValue;

    public HttpParamsElementDataDesc(String pParamName, VariableDataDesc pParamValue) {
        setParamName(pParamName);
        setParamValue(pParamValue);
    }

    public HttpParamsElementDataDesc() {
    }

    public HttpParamsElementDataDesc copy(AbstractAGElementDataDesc pElementDataDesc) {
        HttpParamsElementDataDesc copied = new HttpParamsElementDataDesc();
        copied.setParamName(mParamName);
        if (mParamValue != null) {
            copied.mParamValue = mParamValue.copy(pElementDataDesc);
        }
        return copied;
    }

    public String getParamName() {
        return mParamName;
    }

    public void setParamName(String pParamName) {
        mParamName = pParamName;
    }

    public VariableDataDesc getParamValue() {
        return mParamValue;
    }

    public void setParamValue(VariableDataDesc pParamValue) {
        mParamValue = pParamValue;
    }

    public void resolveVariables() {
        mParamValue.resolveVariable();
    }

    @Override
    public boolean equals(Object o) {
        HttpParamsElementDataDesc compared = (HttpParamsElementDataDesc) o;

        if (mParamName.equals(compared.getParamName()) && mParamValue.getStringValue().equals(compared.getParamValue().getStringValue()))
            return true;
        else
            return false;
    }
}
