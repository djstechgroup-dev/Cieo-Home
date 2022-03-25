package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.CodeScannerTypesDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.types.FormString;
import com.kinetise.data.systemdisplay.views.IValidateListener;
import com.kinetise.data.systemdisplay.views.OnStateChangedListener;
import com.kinetise.helpers.asynccaller.AsyncCaller;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class AGCodeScannerDataDesc extends AGButtonDataDesc implements IFormControlDesc<FormString> {

    private FormString mFormValue;
    private FormDescriptor mFormDescriptor;
    private OnStateChangedListener mListener;
    private CodeScannerTypesDataDesc mCodeTypesDataDesc;
    private boolean mIsValid;
    private String mInvalidMessage;
    private IValidateListener mValidateListener;

    public AGCodeScannerDataDesc(String id) {
        super(id);
        mFormValue = new FormString(null);
        mFormDescriptor = new FormDescriptor();
        mIsValid = true;
    }

    public void setFormValue(String formValue) {
        mFormValue = new FormString(formValue);
        if (mListener != null) {
            mListener.onStateChanged();
        }
    }

    @Override
    public FormString getFormValue() {
        return mFormValue;
    }

    public FormDescriptor getFormDescriptor() {
        return mFormDescriptor;
    }

    public void setFormDescriptor(FormDescriptor formDescriptor) {
        mFormDescriptor = formDescriptor;
    }

    @Override
    public void clearFormValue() {
        initValue();
        if (mValidateListener != null)
            mValidateListener.setValidation(true);
    }

    public void initValue() {
        mFormValue.setOriginalValue(null);
        AsyncCaller.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mListener != null)
                    mListener.onStateChanged();
            }
        });
    }

    @Override
    public AGCodeScannerDataDesc createInstance() {
        return new AGCodeScannerDataDesc(getId());
    }

    @Override
    public AGCodeScannerDataDesc copy() {
        AGCodeScannerDataDesc copied = (AGCodeScannerDataDesc) super.copy();
        copied.mFormValue = mFormValue.copy();
        copied.setFormDescriptor(mFormDescriptor.copy(copied));
        copied.mCodeTypesDataDesc = mCodeTypesDataDesc.copy(mCodeTypesDataDesc);
        return copied;
    }

    public void setStateChangeListener(OnStateChangedListener listener) {
        mListener = listener;
    }

    public void removeStateChangeListener(OnStateChangedListener listener) {
        if (mListener == listener)
            mListener = null;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mFormDescriptor.resolveVariable();
    }

    @Override
    public String getFormId() {
        return mFormDescriptor.getFormId();
    }

    public static ArrayList<String> parseCodeTypes(String value) {
        ArrayList<String> codes = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(value);
            for (int i = 0; i < array.length(); i++) {
                codes.add(array.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return codes;
    }

    public void setCodeTypes(CodeScannerTypesDataDesc codes) {
        mCodeTypesDataDesc = codes;
    }

    public void setCodeTypes(ArrayList<String> codes) {
        mCodeTypesDataDesc = new CodeScannerTypesDataDesc(codes);
    }

    public List<String> getCodesTypes() {
        return mCodeTypesDataDesc.getCodeTypes();
    }


    @Override
    public boolean isFormValid() {
        if (mValidateListener != null) {
            mValidateListener.validateForm();
            return mIsValid;
        }
        return false;
    }


    public void setValid(boolean isValid, String message) {
        mIsValid = isValid;
        mInvalidMessage = message;
        if (mIsValid) {
            setCurrentBorderColor(getBorderColor());
        } else {
            setCurrentBorderColor(mFormDescriptor.getInvalidBorderColor());
        }
    }

    public String getInvalidMessage() {
        return mInvalidMessage;
    }

    @Override
    public void setValidateListener(IValidateListener listener) {
        mValidateListener = listener;
    }

    @Override
    public void removeValidateListener(IValidateListener listener) {
        if (mValidateListener == listener)
            mValidateListener = null;
    }

}
