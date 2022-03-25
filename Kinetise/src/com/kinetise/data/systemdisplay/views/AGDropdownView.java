package com.kinetise.data.systemdisplay.views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.formdatautils.FormValidationRule;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.DecoratorCalcDescriptor;
import com.kinetise.data.descriptors.datadescriptors.AGDropdownDataDesc;
import com.kinetise.data.descriptors.helpers.DataDescHelper;
import com.kinetise.data.sourcemanager.ImageSource;
import com.kinetise.data.systemdisplay.LayoutHelper;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.TextMeasurer;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageChangeListener;

import java.util.List;

public class AGDropdownView extends AGButtonView<AGDropdownDataDesc> implements OnStateChangedListener, IValidateListener, IFormView {

    private final InvalidView mInvalidView;
    private BasicImageView mDecoratorView;

    protected ImageSource mDecoratorImageSource;
    protected ImageSource mActiveDecoratorImageSource;

    public AGDropdownView(SystemDisplay display, AGDropdownDataDesc desc) {
        super(display, desc);
        mDescriptor.setStateChangeListener(this);

        mInvalidView = createInvalidView();
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

        addView(mDecoratorView);
        addView(mInvalidView);

        mDescriptor.initValue();
        recalculateText();
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
        try {
            mDecoratorImageSource.refresh(baseUrl, calcDesc.getWidth(), calcDesc.getHeight());
            mActiveDecoratorImageSource.refresh(baseUrl, calcDesc.getWidth(), calcDesc.getHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        createAndShowPopup();
    }

    @Override
    protected void executeClick(View v) {
        createAndShowPopup();
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

    private void createAndShowPopup() {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                mDisplay.getActivity(),
                android.R.layout.select_dialog_singlechoice);

        populateListAdapter(arrayAdapter, mDescriptor.getDropdownValues());

        AlertDialog alertDialog = createAlertDialog(arrayAdapter);

        alertDialog.show();
        alertDialog.getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        alertDialog.getListView().setItemChecked(mDescriptor.getCheckedItemIndex(), true);
    }

    private AlertDialog createAlertDialog(ArrayAdapter<String> arrayAdapter) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mDisplay.getActivity());
        alertDialogBuilder.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveSelectedValue(which);
                    }
                });
        return alertDialogBuilder.create();
    }

    private void populateListAdapter(ArrayAdapter<String> arrayAdapter, List<AGDropdownDataDesc.DropdownValue> values) {
        for (AGDropdownDataDesc.DropdownValue value : values)
            arrayAdapter.add(value.text);

    }

    private void saveSelectedValue(int which) {
        mDescriptor.setSelected(which);
        recalculateText();
        mDescriptor.onChange();
    }

    private void recalculateText() {
        mDisplay.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AGViewCalcDesc calcDesc = getDescriptor().getCalcDesc();

                TextMeasurer measurer = new TextMeasurer(mDescriptor.getTextDescriptor());
                measurer.measure(mDescriptor.getTextDescriptor().getText().getStringValue(), mDescriptor.getCalcDesc().getContentWidth());
                measurer.layout(calcDesc.getContentWidth(), calcDesc.getContentHeight());
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
        LayoutHelper.measureExactly(mDecoratorView, (int) (decoratorCalcDesc.getWidth()), (int) (decoratorCalcDesc.getHeight()));
    }

    @Override
    protected void onLayout(boolean bool, int l, int t, int r, int b) {
        super.onLayout(bool, l, t, r, b);

        AGViewCalcDesc calcDesc = getDescriptor().getCalcDesc();
        int right = (r - l) - ((int) Math.round(calcDesc.getBorder().getRight()));
        mInvalidView.layoutInvalidView(calcDesc, right);
        DecoratorCalcDescriptor calcDescriptor = getDescriptor().getDecoratorDescriptor().getCalcDescriptor();
        mDecoratorView.layout((int) (calcDescriptor.getPositionX()), (int) (calcDescriptor.getPositionY()), (int) (calcDescriptor.getPositionX() + calcDescriptor.getWidth()), (int) (calcDescriptor.getPositionY() + calcDescriptor.getHeight()));
    }

    @Override
    public void onStateChanged() {
        recalculateText();
        mTextView.setTextColor(mDescriptor.getTextDescriptor().getTextColor());
        validateForm();
    }

    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        mDescriptor.removeOnStateChangedListener(this);
        super.setDescriptor(descriptor);
        mDescriptor.initValueIfNotInitialized();
        mDescriptor.setStateChangeListener(this);
    }

    @Override
    public void validateForm() {
        setValid(true);
        for (FormValidationRule rule : mDescriptor.getFormDescriptor().getFormValidation().getRules()) {
            if (rule.getType().equals(FormValidationRule.TYPE_REQUIRED)) {
                if (mDescriptor.getOption() == null) {
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
