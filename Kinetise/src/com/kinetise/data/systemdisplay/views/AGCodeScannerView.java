package com.kinetise.data.systemdisplay.views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import com.kinetise.components.activity.ScanCodeActivity;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.formdatautils.FormValidationRule;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGCodeScannerDataDesc;
import com.kinetise.data.descriptors.helpers.DataDescHelper;
import com.kinetise.data.packagemanager.AppPackageManager;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageSetterCommandCallback;
import com.kinetise.data.systemdisplay.helpers.CodeScannerSetter;
import com.kinetise.data.systemdisplay.helpers.IPermissionListener;
import com.kinetise.data.systemdisplay.helpers.IPermissionRequestListener;
import com.kinetise.data.systemdisplay.helpers.PermissionManager;

import java.util.ArrayList;

public class AGCodeScannerView extends AGButtonView<AGCodeScannerDataDesc> implements OnStateChangedListener, ImageSetterCommandCallback, IPermissionRequestListener, IFormView, IValidateListener {

    private final InvalidView mInvalidView;

    public AGCodeScannerView(SystemDisplay display, AGCodeScannerDataDesc desc) {
        super(display, desc);

        mDescriptor.initValue();
        mDescriptor.setStateChangeListener(this);

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
            CodeScannerSetter.setClickedCodeScannerView(this);

            if (PermissionManager.hasGrantedPermission(activity.getApplicationContext(), Manifest.permission.CAMERA) == false) {
                requestPermission((IPermissionListener) activity);
            } else {
                openCodeScanner(activity);
                requestFocus();
            }
        }
    }

    private void requestPermission(IPermissionListener listener) {
        listener.onPermissionLack(IPermissionListener.CAMERA_REQUEST_CODE, this);
    }

    private void openCodeScanner(Activity activity) {
        activity.startActivityForResult(getCodeScannerIntent(), ScanCodeActivity.REQUEST_SCAN_CODE_CONTROL);
    }

    @Override
    protected void setActiveState() {
        if (mDescriptor.getFormValue() == null) {
            super.setActiveState();
        }
    }

    @Override
    protected void setInactiveState() {
        if (mDescriptor.getFormValue() == null) {
            super.setInactiveState();
        }
    }

    @Override
    public void onStateChanged() {
        //TODO: quick fix for resetting form value, should be changed with AGPhotoView refactor
        loadAssets();
        validateForm();
    }

    @Override
    public void loadingStarted() {

    }

    @Override
    public void setImageSrc(Bitmap b) {
        mImageView.setImageBitmap(b);
        mDescriptor.onChange();
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        mDescriptor.setStateChangeListener(null);
        super.setDescriptor(descriptor);
        mDescriptor.setStateChangeListener(this);
    }

    public void setScannedCode(String scannedText) {
        mDescriptor.setFormValue(scannedText);

        // when code is taken background is changed to placeholder and text is made invisible; view will never exit this state
        mImageView.setImageBitmap(AppPackageManager.getInstance().getPackage().getScannedQRCodePlaceholder());
        if (mTextView != null) {
            mTextView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPermissionGranted() {
        Activity activity = AGApplicationState.getInstance().getActivity();
        openCodeScanner(activity);
    }

    @Override
    public void onPermissionDenied() {
    }

    private Intent getCodeScannerIntent() {
        Intent intent;
        intent = new Intent(AGApplicationState.getInstance().getActivity(), ScanCodeActivity.class);
        intent.putExtra(ScanCodeActivity.REQUEST_CODE, ScanCodeActivity.REQUEST_SCAN_CODE_CONTROL);
        intent.putStringArrayListExtra(ScanCodeActivity.FORMATS, (ArrayList) mDescriptor.getCodesTypes());
        return intent;
    }

    @Override
    public void validateForm() {
        setValid(true);
        for (FormValidationRule rule : mDescriptor.getFormDescriptor().getFormValidation().getRules()) {
            if (rule.getType().equals(FormValidationRule.TYPE_REQUIRED)) {
                if (mDescriptor.getFormValue().getOriginalValue() == null) {
                    setValid(false, rule.getMessage());
                    return;
                }
            } else if (rule.getType().equals(FormValidationRule.TYPE_SAME_AS)) {
                AbstractAGElementDataDesc control = DataDescHelper.findDescendantById(AGApplicationState.getInstance().getCurrentScreenDesc(), rule.getControlId());
                if (control != null && control instanceof IFormControlDesc) {
                    Object controlValue = ((IFormControlDesc) control).getFormValue();
                    Object thisValue = getDescriptor().getFormValue();

                    if (!(controlValue == null && thisValue == null) && controlValue != null && !controlValue.equals(thisValue)) {
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
