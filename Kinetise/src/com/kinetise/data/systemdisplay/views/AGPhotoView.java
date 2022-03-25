package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.view.View;

import com.kinetise.components.activity.KinetiseActivity;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.formdatautils.FormValidationRule;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.AGPhotoDataDesc;
import com.kinetise.data.descriptors.helpers.DataDescHelper;
import com.kinetise.data.sourcemanager.ImageSource;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageChangeListener;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageSetterCommandCallback;
import com.kinetise.data.systemdisplay.helpers.PhotoSetter;

public class AGPhotoView extends AGButtonView<AGPhotoDataDesc> implements IFormView, OnStateChangedListener, ImageSetterCommandCallback, IValidateListener {

    private final InvalidView mInvalidView;
    private KinetiseActivity mKinetiseActivityCallback;
    ImageSource photoSource;

    public AGPhotoView(SystemDisplay display, AGPhotoDataDesc desc) {
        super(display, desc);
        mKinetiseActivityCallback = display.getActivity();
        mDescriptor.setStateChangeListener(this);
        ImageChangeListener imageSetCallback = new ImageChangeListener() {
            @Override
            public void onImageChanged(Bitmap bitmap) {
                if (mLoadingView != null)
                    removeView(mLoadingView);
                mImageView.setImageBitmap(bitmap);
            }
        };
        photoSource = new ImageSource(mDescriptor.getPhotoDescriptor(), imageSetCallback);
        ImageChangeListener imageChange = new ImageChangeListener() {
            @Override
            public void onImageChanged(Bitmap bitmap) {
                if (mDescriptor.isPhotoTaken())
                    return;
                if (mLoadingView != null)
                    removeView(mLoadingView);
                mImageView.setImageBitmap(bitmap);
            }
        };
        imageSource.addImageChangeListener(imageChange);
        mDescriptor.getPhotoDescriptor().setSizeMode(mDescriptor.getImageDescriptor().getSizeMode());

        mInvalidView = createInvalidView();

        addView(mInvalidView);
        setValid(true);
    }

    @Override
    public void onLoadingStarted() {
        if (!mDescriptor.isPhotoTaken())
            super.onLoadingStarted();
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
    protected void onLayout(boolean bool, int l, int t, int r, int b) {
        super.onLayout(bool, l, t, r, b);

        AGViewCalcDesc calcDesc = getDescriptor().getCalcDesc();

        int right = (r - l) - ((int) Math.round(calcDesc.getBorder().getRight()));
        mInvalidView.layoutInvalidView(calcDesc, right);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mInvalidView.measure();
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        PhotoSetter.setClickedPhotoButtonView(this);
        requestFocus();
    }

    @Override
    protected void setActiveState() {
        if (!mDescriptor.isPhotoTaken()) {
            super.setActiveState();
        }
    }

    @Override
    protected void setInactiveState() {
        if (mDescriptor.isPhotoTaken()) {
            mImageView.setImageBitmap(photoSource.getBitmap());
        } else {
            super.setInactiveState();
        }
    }

    public void setPhotoImage(String imageFilePath) {
        scanFile(imageFilePath);
        mDescriptor.setFormValue(imageFilePath);
    }

    private void scanFile(String imageFilePath) {
        // Magic, do not touch (https://code.google.com/p/android/issues/detail?id=38282)
        MediaScannerConnection.scanFile(mKinetiseActivityCallback, new String[]{imageFilePath}, null, null);
    }


    @Override
    public void onStateChanged() {
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
    public void loadAssets() {
        String baseUrl = mDescriptor.getFeedBaseAdress();
        super.loadAssets();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        if (mDescriptor.isPhotoTaken()) {
            photoSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
            mImageView.setImageBitmap(photoSource.getBitmap());
        } else {
            mImageView.setImageBitmap(imageSource.getBitmap());
        }
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        mDescriptor.setStateChangeListener(null);
        super.setDescriptor(descriptor);
        photoSource.setImageDescriptor(mDescriptor.getPhotoDescriptor());
        mDescriptor.setStateChangeListener(this);
    }

    @Override
    public void validateForm() {
        setValid(true);
        for (FormValidationRule rule : mDescriptor.getFormDescriptor().getFormValidation().getRules()) {
            if (rule.getType().equals(FormValidationRule.TYPE_REQUIRED)) {
                if (!mDescriptor.getFormValue().isPhotoTaken()) {
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
