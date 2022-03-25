package com.kinetise.data.systemdisplay.views;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.formdatautils.FormValidationRule;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGTextInputCalcDescriptor;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.BasicViewCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.DecoratorCalcDescriptor;
import com.kinetise.data.descriptors.datadescriptors.AGTextInputDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.BackgroundImageDescriptor;
import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;
import com.kinetise.data.descriptors.helpers.DataDescHelper;
import com.kinetise.data.descriptors.types.AGTextAlignType;
import com.kinetise.data.sourcemanager.AbstractGetSourceCommand;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.data.sourcemanager.ImageSource;
import com.kinetise.data.systemdisplay.LayoutHelper;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.data.systemdisplay.bitmapsettercommands.ImageChangeListener;
import com.kinetise.data.systemdisplay.fontsettercommands.FontSetterCommand;
import com.kinetise.data.systemdisplay.helpers.AGControl;
import com.kinetise.data.systemdisplay.helpers.AGTypefaceLocation;
import com.kinetise.data.systemdisplay.viewvisitors.IViewVisitor;
import com.kinetise.helpers.drawing.BackgroundSetterCommandCallback;
import com.kinetise.helpers.drawing.ViewDrawer;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;

public class AGTextInputView extends AGControl<AGTextInputDataDesc> implements IFormView, TextView.OnEditorActionListener, View.OnFocusChangeListener, BackgroundSetterCommandCallback, OnStateChangedListener, TextWatcher, IValidateListener {

    protected static final String ASSETS_PREFIX = "assets://";
    protected final static String EMPTY_HINT = " ";
    public static final String CONTENT_DESCRIPTION_EDITTEXT = "edittext";
    private final InvalidView mInvalidView;
    private AGScreenDataDesc mScreenDescriptor;

    protected ImageSource mBackgroundSource;
    protected EditText mInputView;
    protected BasicImageView mDecoratorView;
    private boolean mIsValid;

    protected ImageSource mDecoratorImageSource;
    protected ImageSource mActiveDecoratorImageSource;
    private boolean isButtonPressed;

    public AGTextInputView(SystemDisplay display, AGTextInputDataDesc desc) {
        super(display, desc);
        mDescriptor.initValue(mDescriptor.getFormDescriptor().getInitValue().getStringValue());
        mScreenDescriptor = AGApplicationState.getInstance().getCurrentScreenDesc();

        mInputView = createInputView();
        mInvalidView = createInvalidView();
        mDecoratorView = createDecoratorView();

        mInputView.setBackgroundDrawable(null);

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

        addView(mInputView);
        addView(mInvalidView);
        addView(mDecoratorView);

        setOnClickListener(this);
        desc.setStateChangeListener(this);
        if (mDescriptor instanceof AbstractAGViewDataDesc) {
            ImageDescriptor imageDescriptor = new BackgroundImageDescriptor(mDescriptor.getBackground());
            mBackgroundSource = new ImageSource(imageDescriptor, new BackgroundChangeListener());
        }

        setValid(true);
    }

    private BasicImageView createDecoratorView() {
        BasicImageView view = new BasicImageView(mDisplay.getActivity());
        view.setSizeMode(mDescriptor.getDecoratorDescriptor().getImageDescriptor().getSizeMode());
        view.setSoundEffectsEnabled(false);
        setOnClickListener(this);
        return view;
    }

    private EditText createInputView() {
        EditText view = new EditText(mDisplay.getActivity());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
        view.addTextChangedListener(this);
        view.setContentDescription(CONTENT_DESCRIPTION_EDITTEXT);
        view.setPadding(0, 0, 0, 0);
        return view;
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
        mScreenDescriptor = null;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        AGTextInputCalcDescriptor calcDesc = getDescriptor().getCalcDesc();
        BasicViewCalcDesc editTextCalcDesc = calcDesc.getEditTextCalcDesc();

        View inputView = getChildAt(0);
        int editTextLeft = editTextCalcDesc.getPositionX();
        int editTextTop = editTextCalcDesc.getPositionY();
        inputView.layout(editTextLeft, editTextTop, editTextLeft + editTextCalcDesc.getWidth(), editTextTop + editTextCalcDesc.getHeight());

        DecoratorCalcDescriptor calcDescriptor = getDescriptor().getDecoratorDescriptor().getCalcDescriptor();
        mDecoratorView.layout((int) (calcDescriptor.getPositionX()), (int) (calcDescriptor.getPositionY()), (int) (calcDescriptor.getPositionX() + calcDescriptor.getWidth()), (int) (calcDescriptor.getPositionY() + calcDescriptor.getHeight()));

        int right = (r - l) - ((int) Math.round(calcDesc.getBorder().getRight()));
        InvalidView invalidView = (InvalidView) getChildAt(1);
        invalidView.layoutInvalidView(calcDesc, right);

    }

    @Override
    public void loadAssets() {
        initEditText();
        String baseUrl = mDescriptor.getFeedBaseAdress();
        AGViewCalcDesc calcDesc = mDescriptor.getCalcDesc();
        mBackgroundSource.refresh(baseUrl, calcDesc.getViewWidth(), calcDesc.getViewHeight());
        mDecoratorImageSource.refresh(baseUrl, calcDesc.getWidth(), calcDesc.getHeight());
        mActiveDecoratorImageSource.refresh(baseUrl, calcDesc.getWidth(), calcDesc.getHeight());
    }

    private int roundToInt(double marginLeft) {
        return (int) Math.round(marginLeft);
    }

    protected void initEditText() {
        initInput();
        mInputView.setSingleLine();
        mInputView.setMaxLines(1);
    }

    protected void initInput() {
        int textColor = mDescriptor.getTextDescriptor().getTextColor();
        mInputView.setGravity(getTextGravity());
        mInputView.setTextColor(textColor);
        mInputView.setHintTextColor(textColor);
        setUnderLine();
        setFontTypeface();
        mInputView.setTextSize(TypedValue.COMPLEX_UNIT_PX, roundToInt(mDescriptor.getTextDescriptor().getCalcDescriptor().getFontSize()));
        setHintFromDescriptor();
        mInputView.setHintTextColor(mDescriptor.getWatermarkColor());

        mInputView.setFocusable(true);
        mInputView.setFocusableInTouchMode(true);
        mInputView.setOnEditorActionListener(this);
        mInputView.setOnFocusChangeListener(this);
        mInputView.setEllipsize(TextUtils.TruncateAt.END);
        mInputView.setText(mDescriptor.getInputValue());
        mInputView.setInputType(mDescriptor.getKeyboard());
        try {
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(this, 0);
        } catch (Exception ignored) {
        }
    }

    private void setHintFromDescriptor() {
        String watermark;
        if (mDescriptor.getWatermark() != null && !mDescriptor.getWatermark().equals("")) {
            watermark = mDescriptor.getWatermark();
        } else {
            watermark = EMPTY_HINT;
        }
        mInputView.setHint(watermark);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        if (mDescriptor != null) {
            String newText = text.toString();
            if (!newText.equals(mDescriptor.getFormValue())) {
                mDescriptor.onChange();
            }
            mDescriptor.setFormValue(newText, false);
            if (!mIsValid) {
                validateForm();
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    protected void setFontTypeface() {
        String source = ASSETS_PREFIX;

        int flag;
        if (mDescriptor.getTextDescriptor().isItalic() && mDescriptor.getTextDescriptor().isBold()) {
            source += AGTypefaceLocation.FONT_BOLD_ITALIC;
            flag = Typeface.BOLD_ITALIC;
        } else if (mDescriptor.getTextDescriptor().isItalic()) {
            source += AGTypefaceLocation.FONT_ITALIC;
            flag = Typeface.ITALIC;
        } else if (mDescriptor.getTextDescriptor().isBold()) {
            source += AGTypefaceLocation.FONT_BOLD;
            flag = Typeface.BOLD;
        } else {
            source += AGTypefaceLocation.FONT_NORMAL;
            flag = Typeface.NORMAL;
        }

        AbstractGetSourceCommand command = new FontSetterCommand(source, mInputView, flag);
        AssetsManager.getInstance().getAsset(command, AssetsManager.ResultType.FONT);
    }

    private void setUnderLine() {
        if (mDescriptor.getTextDescriptor().getTextDecoration()) {
            mInputView.setPaintFlags(mInputView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }
    }

    protected int getTextGravity() {
        int gravity = mInputView.getGravity();
        AGTextAlignType textAlign = getDescriptor().getTextDescriptor().getTextAlign();
        if (textAlign == null) {
            return gravity;
        }
        if (textAlign.equals(AGTextAlignType.CENTER)) {
            gravity = Gravity.CENTER;
        }
        if (textAlign.equals(AGTextAlignType.LEFT)) {
            gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        }
        if (textAlign.equals(AGTextAlignType.RIGHT)) {
            gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
        }

        return gravity;
    }

    @Override
    public IAGView getAGViewParent() {
        ViewParent parent = getParent();
        if (!(parent instanceof IAGView)) {
            throw new InvalidParameterException(
                    "Parent of IAGView object have to implement IAGView interface");
        }

        return (IAGView) parent;
    }

    @Override
    public ViewDrawer getViewDrawer() {
        return mDrawer;
    }

    @Override
    public boolean accept(IViewVisitor visitor) {
        return visitor.visit(this);
    }


    @Override
    public void onClick(View view) {
        VariableDataDesc action = mDescriptor.getOnClickActionDesc();
        if (action != null) {
            action.resolveVariable();
        } else {
            ViewParent parent = getParent();
            if (parent instanceof IAGView) {
                ((IAGView) parent).onClick(view);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        View inputView = getChildAt(0);
        DecoratorCalcDescriptor decoratorCalcDesc = getDescriptor().getDecoratorDescriptor().getCalcDescriptor();
        BasicViewCalcDesc editTextCalcDesc = getDescriptor().getCalcDesc().getEditTextCalcDesc();
        LayoutHelper.measureExactly(inputView, editTextCalcDesc.getWidth(), editTextCalcDesc.getHeight());
        InvalidView invalidView = (InvalidView) getChildAt(1);
        invalidView.measure();

        LayoutHelper.measureExactly(mDecoratorView, (int) (decoratorCalcDesc.getWidth()), (int) (decoratorCalcDesc.getHeight()));
    }


    @Override
    public void setDescriptor(AbstractAGElementDataDesc descriptor) {
        mDescriptor.setStateChangeListener(null);
        mDescriptor = (AGTextInputDataDesc) descriptor;
        mDescriptor.setStateChangeListener(this);
    }

    private void setPaddings() {
        AGViewCalcDesc calcDesc = getViewCalcDesc();
        if (getViewCalcDesc() != null) {
            setPadding(
                    roundToInt(calcDesc.getPaddingLeft() + calcDesc.getBorder().getLeft()),
                    roundToInt(calcDesc.getPaddingTop() + calcDesc.getBorder().getTop()),
                    roundToInt(calcDesc.getPaddingRight() + calcDesc.getBorder().getRight()),
                    roundToInt(calcDesc.getPaddingBottom() + calcDesc.getBorder().getBottom())
            );
        }

    }

    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        //event is never consumed, do not removed
        //if this callback is not implemented event is sometimes consumed, which is not desirable
        return false;
    }


    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (mScreenDescriptor == AGApplicationState.getInstance().getCurrentScreenDesc()) {
            if (hasFocus) { //focus gained
                mInputView.setHint(EMPTY_HINT);
                View nextAGTextInputView = getNextAGTextInputView();
                if (nextAGTextInputView != null) {
                    mInputView.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                    view.setNextFocusForwardId(nextAGTextInputView.getId());
                } else {
                    mInputView.setImeOptions(EditorInfo.IME_ACTION_DONE);
                }
            } else {
                setHintFromDescriptor();
                if (!mDescriptor.isFormValid()) {
                    showInvalidMessageToast();
                }
            }
        }
    }

    private View getNextAGTextInputView() {
        ViewParent parent = getParent();
        if (!(parent instanceof AGContainerView))
            return null;

        ArrayList<IAGView> childrenViews = ((AGContainerView) parent).getChildrenViews();
        boolean thisViewFound = false;
        for (IAGView childrenView : childrenViews) {
            if (childrenView.equals(this)) {
                thisViewFound = true;
                continue;
            }
            if (!thisViewFound)
                continue;
            if (childrenView instanceof AGTextInputView) {
                return (View) childrenView;
            }
        }
        return null;
    }

    @Override
    public void onStateChanged() {
        mInputView.setText(mDescriptor.getInputValue());
        mInputView.invalidate();
    }

    AGViewCalcDesc getViewCalcDesc() {
        return mDescriptor.getCalcDesc();
    }

    @Override
    protected void setActiveState() {
        if (isButtonPressed) {
            return;
        }
        super.setActiveState();
        if (hasActiveDecorator()) {
            mDecoratorView.setImageBitmap(mActiveDecoratorImageSource.getBitmap());
        }
    }

    private boolean hasActiveDecorator() {
        return mActiveDecoratorImageSource.getBitmap() != null;
    }

    @Override
    protected void setInactiveState() {
        if (!isButtonPressed)
            return;
        super.setInactiveState();
        if (hasDecorator()) {
            mDecoratorView.setImageBitmap(mDecoratorImageSource.getBitmap());
        }
    }

    private boolean hasDecorator() {
        return mDecoratorImageSource.getBitmap() != null;
    }

    @Override
    public void validateForm() {
        setValid(true);
        for (FormValidationRule rule : mDescriptor.getFormDescriptor().getFormValidation().getRules()) {
            if (rule.getType().equals(FormValidationRule.TYPE_REQUIRED)) {
                if (mInputView.getText().toString().length() == 0) {
                    setValid(false, rule.getMessage());
                    return;
                }
            } else if (rule.getType().equals(FormValidationRule.TYPE_REGEX)) {
                boolean isMatch = mInputView.getText().toString().matches(rule.getRegex().toString());
                if (!isMatch) {
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
            } else if (rule.getType().equals(FormValidationRule.TYPE_JAVASCRIPT)) {
                //execute(rule.getCode(), getDescriptor().getFormValue());
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
        mIsValid = isValid;
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