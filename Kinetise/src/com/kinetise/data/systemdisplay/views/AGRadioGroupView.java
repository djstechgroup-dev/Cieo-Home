package com.kinetise.data.systemdisplay.views;

import android.view.View;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.formdatautils.FormValidationRule;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGRadioGroupDataDesc;
import com.kinetise.data.descriptors.helpers.DataDescHelper;
import com.kinetise.data.systemdisplay.SystemDisplay;

public class AGRadioGroupView extends AGContainerView<AbstractAGRadioGroupDataDesc> implements IFormView, OnStateChangedListener, IValidateListener {

    private final InvalidView mInvalidView;

    public AGRadioGroupView(SystemDisplay display, AbstractAGRadioGroupDataDesc desc) {
        super(display, desc);

        mInvalidView = createInvalidView();
        addView(mInvalidView);
        setValid(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDescriptor.setValidateListener(this);
        mDescriptor.setOnStateChangedListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDescriptor.removeValidateListener(this);
        mDescriptor.removeOnStateChangedListener(this);
    }

    @Override
    public void onStateChanged() {
        validateForm();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mInvalidView.measure();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        AGViewCalcDesc calcDesc = getDescriptor().getCalcDesc();
        int right = (r - l) - ((int) Math.round(calcDesc.getBorder().getRight()));
        mInvalidView.layoutInvalidView(calcDesc, right);
    }

    @Override
    public void validateForm() {
        setValid(true);
        for (FormValidationRule rule : mDescriptor.getFormDescriptor().getFormValidation().getRules()) {
            if (rule.getType().equals(FormValidationRule.TYPE_REQUIRED)) {
                if (mDescriptor.getCheckedIndex() < 0) {
                    setValid(false, rule.getMessage());
                    return;
                }
            } else if (rule.getType().equals(FormValidationRule.TYPE_SAME_AS)) {
                AbstractAGElementDataDesc control = DataDescHelper.findDescendantById(AGApplicationState.getInstance().getCurrentScreenDesc(), rule.getControlId());
                if (control!=null && control instanceof IFormControlDesc) {
                    if (!((IFormControlDesc) control).getFormValue().equals(getDescriptor().getFormValue())) {
                        setValid(false, rule.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public void setValidation(boolean isValid) {
        setValid(isValid);
    }

    private void setValid(boolean isValid) {
        setValid(isValid, null);
    }

    private void setValid(boolean isValid, String message) {
        mDescriptor.setValid(isValid, message);
        if (isValid) {
            hideInvalidView();
        } else {
            showInvalidView();
        }
        mDrawer.refresh();
    }

    @Override
    public void showInvalidMessageToast() {
        PopupManager.showInvalidFormToast(mDescriptor.getInvalidMessage());
    }

    @Override
    public void hideInvalidView() {
        mInvalidView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showInvalidView() {
        mInvalidView.setVisibility(View.VISIBLE);
    }

    private InvalidView createInvalidView() {
        return InvalidView.createInvalidView(mDisplay.getActivity(), mDescriptor.getFormDescriptor().getInvalidBorderColor(), this);
    }

}
