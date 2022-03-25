package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.view.View;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.formdatautils.FormValidationRule;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.DecoratorCalcDescriptor;
import com.kinetise.data.descriptors.datadescriptors.AGDatePickerDataDesc;
import com.kinetise.data.descriptors.helpers.DataDescHelper;
import com.kinetise.data.sourcemanager.ImageSource;
import com.kinetise.data.systemdisplay.LayoutHelper;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.TextMeasurer;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageChangeListener;
import com.kinetise.helpers.time.DateTimePicker;
import com.kinetise.helpers.time.IDateTimePickedListener;

import java.util.Date;

public class AGDatePickerView extends AGButtonView<AGDatePickerDataDesc> implements OnStateChangedListener, IDateTimePickedListener, IFormView, IValidateListener {

    private final InvalidView mInvalidView;
    private BasicImageView mDecoratorView;

    protected ImageSource mDecoratorImageSource;
    protected ImageSource mActiveDecoratorImageSource;

    public AGDatePickerView(SystemDisplay display, AGDatePickerDataDesc desc) {
        super(display, desc);
        mDescriptor.setStateChangeListener(this);
        mDescriptor.initValue();
        recalculateText();

        mDecoratorView = createDecoratorView();

        ImageChangeListener imageSetCallback = new ImageChangeListener() {
            @Override
            public void onImageChanged(Bitmap bitmap) {
                mDecoratorView.setImageBitmap(bitmap);
            }
        };
        ImageChangeListener activeImageSetCallback = new ImageChangeListener() {
            @Override
            public void onImageChanged(Bitmap bitmap) {
            }
        };

        mDecoratorImageSource = new ImageSource(mDescriptor.getDecoratorDescriptor().getImageDescriptor(), imageSetCallback, null);
        mActiveDecoratorImageSource = new ImageSource(mDescriptor.getDecoratorDescriptor().getActiveImageDescriptor(), activeImageSetCallback, null);


        mInvalidView = createInvalidView();
        addView(mDecoratorView);
        addView(mInvalidView);
        setValid(true);
    }

    private BasicImageView createDecoratorView() {
        BasicImageView view = new BasicImageView(mDisplay.getActivity());
        view.setSizeMode(mDescriptor.getDecoratorDescriptor().getImageDescriptor().getSizeMode());
        view.setSoundEffectsEnabled(false);
        setOnClickListener(this);
        return view;
    }

    @Override
    public void loadAssets() {
        super.loadAssets();
        String baseUrl = mDescriptor.getFeedBaseAdress();
        DecoratorCalcDescriptor calcDesc = mDescriptor.getDecoratorDescriptor().getCalcDescriptor();
        mDecoratorImageSource.refresh(baseUrl, calcDesc.getWidth(), calcDesc.getHeight());
        mActiveDecoratorImageSource.refresh(baseUrl, calcDesc.getWidth(), calcDesc.getHeight());

    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        showDateTimePicker();
    }

    private void showDateTimePicker() {
        Date dateValue = mDescriptor.getDateValue();
        Date minDate = mDescriptor.getMinDate();
        Date maxDate = mDescriptor.getMaxDate();
        if (dateValue == null)
            dateValue = AGDatePickerDataDesc.getDateInRange(new Date(), minDate, maxDate);
        DateTimePicker picker = new DateTimePicker(dateValue, mDescriptor.getDatePickerMode(), minDate, maxDate, this);
        picker.show();
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

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDescriptor.setStateChangeListener(this);
        mDescriptor.setValidateListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDescriptor.removeOnStateChangedListener(this);
        mDescriptor.removeValidateListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mInvalidView.measure();

        DecoratorCalcDescriptor decoratorCalcDesc = getDescriptor().getDecoratorDescriptor().getCalcDescriptor();
        LayoutHelper.measureExactly(mDecoratorView,(int)(decoratorCalcDesc.getWidth()),(int)(decoratorCalcDesc.getHeight()));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        AGViewCalcDesc calcDesc = getDescriptor().getCalcDesc();
        int right = (r - l) - ((int) Math.round(calcDesc.getBorder().getRight()));
        mInvalidView.layoutInvalidView(calcDesc, right);

        DecoratorCalcDescriptor calcDescriptor = getDescriptor().getDecoratorDescriptor().getCalcDescriptor();
        mDecoratorView.layout((int)(calcDescriptor.getPositionX()),(int)(calcDescriptor.getPositionY()), (int)(calcDescriptor.getPositionX()+calcDescriptor.getWidth()), (int)(calcDescriptor.getPositionY()+calcDescriptor.getHeight()));
    }

    @Override
    public void onStateChanged() {
        recalculateText();
        validateForm();
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        mDescriptor.removeOnStateChangedListener(this);
        super.setDescriptor(descriptor);
        mDescriptor.setStateChangeListener(this);
    }

    @Override
    public void onDateTimePicked(Date date) {
        mDescriptor.setDate(date);
        recalculateText();
    }

    @Override
    public void validateForm() {
        setValid(true);
        for (FormValidationRule rule : mDescriptor.getFormDescriptor().getFormValidation().getRules()) {
            if (rule.getType().equals(FormValidationRule.TYPE_REQUIRED)) {
                if (mDescriptor.getDateValue() == null) {
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
    protected void setActiveState() {
        if (isButtonPressed)
            return;
        super.setActiveState();

        mDecoratorView.setImageBitmap(mActiveDecoratorImageSource.getBitmap());
    }

    @Override
    protected void setInactiveState() {
        if (!isButtonPressed)
            return;
        super.setInactiveState();

        mDecoratorView.setImageBitmap(mDecoratorImageSource.getBitmap());
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
