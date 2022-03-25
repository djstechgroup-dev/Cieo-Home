package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.actions.StringVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.DecoratorDescriptor;
import com.kinetise.data.descriptors.types.FormString;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.systemdisplay.views.IValidateListener;
import com.kinetise.data.systemdisplay.views.OnStateChangedListener;
import com.kinetise.helpers.parser.JsonFeedParser;
import com.kinetise.helpers.parser.JsonParserException;
import com.kinetise.support.logger.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class AGDropdownDataDesc extends AGButtonDataDesc implements IFormControlDesc<FormString> {
    private static VariableDataDesc emptyStringVariable = new StringVariableDataDesc("");

    private DropdownValuesCollection mDropdownValues;
    private DropdownValue mCurrentValue;
    private boolean valueInitialized = false;
    private VariableDataDesc mOptionsList;
    private FormString mValue;
    private OnStateChangedListener mOnStateChangedListener;
    private VariableDataDesc mWatermark;
    private int mWatermarkColor;
    private int mTextColor;
    private FormDescriptor mFormDescriptor;
    private boolean mIsValid;
    private String mInvalidMessage;
    private IValidateListener mValidateListener;
    private DecoratorDescriptor mDecoratorDescriptor;
    private VariableDataDesc mItemPath;
    private VariableDataDesc mTextPath;
    private VariableDataDesc mValuePath;

    public AGDropdownDataDesc(String id) {
        super(id);
        mValue = new FormString(null);
        mFormDescriptor = new FormDescriptor();
        mDecoratorDescriptor = new DecoratorDescriptor();
        getTextDescriptor().setText(emptyStringVariable);
        mIsValid = true;
    }

    public int getTextColor() {
        return mTextColor;
    }

    public FormDescriptor getFormDescriptor() {
        return mFormDescriptor;
    }

    public void setFormDescriptor(FormDescriptor formDescriptor) {
        mFormDescriptor = formDescriptor;
    }

    public void setItemPath(VariableDataDesc itemPath) {
        mItemPath = itemPath;
    }

    public void setTextPath(VariableDataDesc textPath) {
        mTextPath = textPath;
    }

    public void setValuePath(VariableDataDesc valuePath) {
        mValuePath = valuePath;
    }

    @Override
    public AGButtonDataDesc createInstance() {
        return new AGDropdownDataDesc(getId());
    }

    @Override
    public FormString getFormValue() {
        initValueIfNotInitialized();
        return mValue;
    }

    @Override
    public void clearFormValue() {
        getTextDescriptor().setText(mWatermark);
        initValue();
        if (mValidateListener != null)
            mValidateListener.setValidation(true);
    }

    public void initValueIfNotInitialized() {
        if (!valueInitialized)
            initValue();
    }

    public void initValue() {
        valueInitialized = true;
        String initValue = mFormDescriptor.getInitValue().getStringValue();
        DropdownValue value = mDropdownValues.getByFormValue(initValue);
        if (value != null) {
            setOption(value);
            setFormValue(value.formValue);
        } else {
            setWatermarkAsText();
            setFormValue(null);
        }
    }

    public DecoratorDescriptor getDecoratorDescriptor() {
        return mDecoratorDescriptor;
    }

    public void setDecoratorDescriptor(DecoratorDescriptor decoratorDescriptor) {
        mDecoratorDescriptor = decoratorDescriptor;
    }

    private void setOption(DropdownValue value) {
        mCurrentValue = value;
        VariableDataDesc textVariable = new StringVariableDataDesc(value.text);
        textVariable.resolveVariable();
        getTextDescriptor().setText(textVariable);
        getTextDescriptor().setTextColor(getTextColor());
        if (mOnStateChangedListener != null) {
            mOnStateChangedListener.onStateChanged();
        }
    }

    public DropdownValue getOption() {
        return mCurrentValue;
    }

    public void setOptionByFormValue(String formValue) {
        DropdownValue value = mDropdownValues.getByFormValue(formValue);
        if (value != null) {
            setOption(value);
            setFormValue(value.formValue);
        } else {
            mCurrentValue = null;
        }
    }

    public void setSelected(int index) {
        DropdownValue value = mDropdownValues.getByIndex(index);
        setOption(value);
        setFormValue(value.formValue);
    }

    @Override
    public void setFormValue(String formValue) {
        if (mValue.getOriginalValue() == null || !mValue.getOriginalValue().equals(formValue)) {
            mValue = new FormString(formValue);
            setOptionByFormValue(mValue.toString());
            if (mOnStateChangedListener != null) {
                mOnStateChangedListener.onStateChanged();
            }
        }
    }

    @Override
    public String getFormId() {
        return mFormDescriptor.getFormId();
    }

    public void setStateChangeListener(OnStateChangedListener onStateChangedListener) {
        mOnStateChangedListener = onStateChangedListener;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mFormDescriptor.resolveVariable();
        mOptionsList.resolveVariable();
        mDropdownValues = parseOptionsList();
        mWatermark.resolveVariable();
        initValue();
    }

    @Override
    public AGButtonDataDesc copy() {
        AGDropdownDataDesc agDropdownDataDesc = (AGDropdownDataDesc) super.copy();
        agDropdownDataDesc.setFormDescriptor(mFormDescriptor.copy(agDropdownDataDesc));
        agDropdownDataDesc.setWatermarkColor(mWatermarkColor);
        agDropdownDataDesc.setTextColor(mTextColor);
        agDropdownDataDesc.setOptionsList(mOptionsList.copy(agDropdownDataDesc));
        agDropdownDataDesc.setWatermark(mWatermark.copy(agDropdownDataDesc));
        agDropdownDataDesc.mDecoratorDescriptor = mDecoratorDescriptor.copy(agDropdownDataDesc);
        agDropdownDataDesc.mItemPath = mItemPath.copy(agDropdownDataDesc);
        agDropdownDataDesc.mTextPath = mTextPath.copy(agDropdownDataDesc);
        agDropdownDataDesc.mValuePath = mValuePath.copy(agDropdownDataDesc);
        return agDropdownDataDesc;
    }

    public void setOptionsList(VariableDataDesc optionsList) {
        mOptionsList = optionsList;
    }

    private DropdownValuesCollection parseOptionsList() {
        DropdownValuesCollection result = new DropdownValuesCollection();
        JSONArray jsonArray;
        try {
            Object[] objects = JsonFeedParser.parseItemsWithPath(mOptionsList.getStringValue(), mItemPath.getStringValue(), null);
            jsonArray = (JSONArray) objects[0];
            for (int i = 0; i < jsonArray.length(); ++i) {
                try {
                    getItemFromJsonAndAddItToCollection(result, jsonArray, i);
                } catch (Exception e) {
                    Logger.d("Dropdown parser", e.getMessage());
                }
            }
        } catch (JSONException | InvalidParameterException e) {
            result = new DropdownValuesCollection();
        } catch (JsonParserException e) {
            e.printStackTrace();
        }


        return result;
    }

    private void getItemFromJsonAndAddItToCollection(DropdownValuesCollection dropdownValues, JSONArray jsonArray, int itemIndex) throws JSONException {
        String valuePath = mValuePath.getStringValue();
        String textPath = mTextPath.getStringValue();

        String text = null;
        String value = null;

        if (textPath.equals("") || valuePath.equals("")) {
            text = jsonArray.get(itemIndex).toString();
            value = text;
        } else {
            try {
                JSONObject jsonObject = (JSONObject) jsonArray.get(itemIndex);
                value = JsonFeedParser.parseItemsWithPath(jsonObject, valuePath, null)[0].toString();
                text = JsonFeedParser.parseItemsWithPath(jsonObject, textPath, null)[0].toString();
            } catch (Exception e) {
                throw new InvalidParameterException("Could not parse value or text");
            }
        }

        if (text == null || value == null) {
            throw new InvalidParameterException("Invalid listsrc json.");
        }

        VariableDataDesc textVariable = AGXmlActionParser.createVariable(text, this);
        textVariable.resolveVariable();

        VariableDataDesc valueVariable = AGXmlActionParser.createVariable(value, this);
        valueVariable.resolveVariable();
        dropdownValues.add(textVariable.getStringValue(), valueVariable.getStringValue());
    }

    public List<DropdownValue> getDropdownValues() {
        return mDropdownValues.getValues();
    }

    public int getWatermarkColor() {
        return mWatermarkColor;
    }

    public VariableDataDesc getWatermark() {
        return mWatermark;
    }

    public void setWatermarkColor(int color) {
        mWatermarkColor = color;
    }

    public void setWatermark(VariableDataDesc watermark) {
        mWatermark = watermark;
    }

    public void setWatermarkAsText() {
        getTextDescriptor().setText(getWatermark());
        getTextDescriptor().setTextColor(getWatermarkColor());
    }

    public void setTextColor(int textColor) {
        mTextColor = textColor;
    }

    public void removeOnStateChangedListener(OnStateChangedListener onStateChangedListener) {
        if (mOnStateChangedListener == onStateChangedListener)
            mOnStateChangedListener = null;
    }

    public int getCheckedItemIndex() {
        return mDropdownValues.indexOf(mCurrentValue);
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

    public class DropdownValue {
        public String text;
        public String formValue;

        public DropdownValue(String text, String formValue) {
            this.text = text;
            this.formValue = formValue;
        }
    }

    public class DropdownValuesCollection {
        private List<DropdownValue> data;

        public DropdownValuesCollection() {
            data = new ArrayList<>();
        }

        public DropdownValue getByFormValue(String formValue) {
            for (DropdownValue value : data) {
                if (value.formValue.equals(formValue)) {
                    return value;
                }
            }
            return null;
        }

        public void add(String text, String formValue) {
            data.add(new DropdownValue(text, formValue));
        }

        public int indexOf(DropdownValue value) {
            return data.indexOf(value);
        }

        public List<DropdownValue> getValues() {
            return data;
        }

        public DropdownValue getByIndex(int index) {
            return data.get(index);
        }
    }
}
