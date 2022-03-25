package com.kinetise.data.systemdisplay.views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.View;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.formdatautils.FormValidationRule;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGGetPhoneContactDataDesc;
import com.kinetise.data.descriptors.helpers.DataDescHelper;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.TextMeasurer;
import com.kinetise.data.systemdisplay.helpers.GetPhoneContactSetter;
import com.kinetise.data.systemdisplay.helpers.IPermissionListener;
import com.kinetise.data.systemdisplay.helpers.IPermissionRequestListener;
import com.kinetise.data.systemdisplay.helpers.PermissionManager;

import java.util.List;

public class AGGetPhoneContactView extends AGButtonView<AGGetPhoneContactDataDesc> implements OnStateChangedListener, IPermissionRequestListener, IFormView, IValidateListener {

    private final InvalidView mInvalidView;

    public AGGetPhoneContactView(SystemDisplay display, AGGetPhoneContactDataDesc desc) {
        super(display, desc);

        mDescriptor.setStateChangeListener(this);
        mDescriptor.initValue();
        recalculateText();

        mInvalidView = createInvalidView();
        addView(mInvalidView);
        setValid(true);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDescriptor.setStateChangeListener(this);
        mDescriptor.setValidateListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDescriptor.removeStateChangeListener(this);
        mDescriptor.removeValidateListener(this);
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
    public void onClick(View v) {
        super.onClick(v);

        Activity activity = AGApplicationState.getInstance().getActivity();
        if (activity != null) {
            GetPhoneContactSetter.setClickedGetPhoneContactView(this);
            if (PermissionManager.hasGrantedPermission(activity.getApplicationContext(), Manifest.permission.READ_CONTACTS) == false) {
                requestPermission((IPermissionListener) activity);
            } else {
                getPhoneContact(activity);
                requestFocus();
            }
        }
    }

    private void getPhoneContact(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        activity.startActivityForResult(intent, GetPhoneContactSetter.REQUEST_GET_PHONE_CONTACT);
    }

    private void requestPermission(IPermissionListener listener) {
        listener.onPermissionLack(IPermissionListener.READ_CONTACTS_REQUEST_CODE, this);
    }

    @Override
    protected void setActiveState() {
        if (!mDescriptor.isPhoneContactSet()) {
            super.setActiveState();
        }
    }

    @Override
    protected void setInactiveState() {
        if (!mDescriptor.isPhoneContactSet()) {
            super.setInactiveState();
        }
    }

    @Override
    public void onStateChanged() {
        //TODO: quick fix for resetting form value, should be changed with AGPhotoView refactor
        recalculateText();
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        mDescriptor.setStateChangeListener(null);
        super.setDescriptor(descriptor);
        mDescriptor.setStateChangeListener(this);
    }

    private void recalculateText() {
        mDisplay.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AGViewCalcDesc calcDesc = getDescriptor().getCalcDesc();
                TextMeasurer measurer = new TextMeasurer(mDescriptor.getTextDescriptor());
                measurer.measure(mDescriptor.getTextDescriptor().getText().getStringValue(), mDescriptor.getCalcDesc().getContentWidth());
                measurer.layout(calcDesc.getContentWidth(), calcDesc.getContentHeight());
                mTextView.setTextColor(mDescriptor.getTextDescriptor().getTextColor());
                mTextView.invalidate();
            }
        });
    }

    public void setPhoneContact(String displayName, List<String> numbers) {
        StringBuilder builder = new StringBuilder();
        builder.append(displayName);
        for (int i = 0; i < numbers.size(); i++) {
            if (i == 0) {
                builder.append(": ");
            } else {
                builder.append(", ");
            }
            builder.append(numbers.get(i));
        }

        mDescriptor.setFormValue(builder.toString());
        mDescriptor.setIsPhoneContact(true);
        recalculateText();
    }

    @Override
    public void onPermissionGranted() {
        Activity activity = AGApplicationState.getInstance().getActivity();
        getPhoneContact(activity);
    }

    @Override
    public void onPermissionDenied() {
    }

    @Override
    public void validateForm() {
        setValid(true);
        for (FormValidationRule rule : mDescriptor.getFormDescriptor().getFormValidation().getRules()) {
            if (rule.getType().equals(FormValidationRule.TYPE_REQUIRED)) {
                if (mDescriptor.getFormValue() == null) {
                    setValid(false, rule.getMessage());
                    return;
                }
            } else if (rule.getType().equals(FormValidationRule.TYPE_SAME_AS)) {
                AbstractAGElementDataDesc control = DataDescHelper.findDescendantById(AGApplicationState.getInstance().getCurrentScreenDesc(), rule.getControlId());
                if (control != null && control instanceof IFormControlDesc) {
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
