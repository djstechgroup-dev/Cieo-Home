package com.kinetise.data.systemdisplay.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.view.View;

import com.kinetise.data.descriptors.calcdescriptors.TextCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.components.TextDescriptor;
import com.kinetise.data.descriptors.types.IntQuad;
import com.kinetise.data.sourcemanager.AbstractGetSourceCommand;
import com.kinetise.data.sourcemanager.AssetsManager;
import com.kinetise.data.systemdisplay.fontsettercommands.FontSetterCommand;
import com.kinetise.data.systemdisplay.helpers.AGTypefaceLocation;
import com.kinetise.data.systemdisplay.views.text.LineData;

import java.util.ArrayList;

public class BasicTextView extends View implements FontSetterCallback {
    private static final String ASSETS_PREFIX = "assets://";

    private TextPaint mTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
    private TextDescriptor mTextDescriptor;

    public BasicTextView(Context context) {
        super(context);
    }

    public void setTextDescriptor(TextDescriptor textDescriptor) {
        mTextDescriptor = textDescriptor;
        mTextPaint.setTextSize((float) textDescriptor.getCalcDescriptor().getFontSize());
        if (mTextDescriptor.getTextDecoration()) {
            mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.UNDERLINE_TEXT_FLAG);
        }
        setTextColor(textDescriptor.getTextColor());
        IntQuad padding = mTextDescriptor.getCalcDescriptor().getPadding();
        setPadding(padding.left, padding.top, padding.right, padding.bottom);
        setFontTypeface();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float oldTextSize = mTextPaint.getTextSize();
        float newTextSize = (float) mTextDescriptor.getCalcDescriptor().getFontSize();
        if(Float.compare(oldTextSize,newTextSize)!=0) {
            mTextPaint.setTextSize(newTextSize);
            invalidate();
        }
    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.clipRect(getPaddingLeft(),getPaddingTop(),getWidth()-getPaddingRight(),getHeight()-getPaddingBottom());
        TextCalcDesc textCalcDesc = mTextDescriptor.getCalcDescriptor();

        ArrayList<LineData> linesData = textCalcDesc.getLinesData();
        if (linesData == null)
            return;

        for (int i = 0; i < linesData.size(); i++) {
            LineData line = linesData.get(i);
            canvas.drawText(line.text, line.positionX, line.positionY, mTextPaint);
        }
    }

    @Override
    public void setTypeface(Typeface typeface) {
        mTextPaint.setTypeface(typeface);
    }

    private void setFontTypeface() {
        String source = ASSETS_PREFIX;
        int flag;
        if (mTextDescriptor.isItalic() && mTextDescriptor.isBold()) {
            source += AGTypefaceLocation.FONT_BOLD_ITALIC;
            flag = Typeface.BOLD_ITALIC;
        } else if (mTextDescriptor.isItalic()) {
            source += AGTypefaceLocation.FONT_ITALIC;
            flag = Typeface.ITALIC;
        } else if (mTextDescriptor.isBold()) {
            source += AGTypefaceLocation.FONT_BOLD;
            flag = Typeface.BOLD;
        } else {
            source += AGTypefaceLocation.FONT_NORMAL;
            flag = Typeface.NORMAL;
        }

        AbstractGetSourceCommand command = new FontSetterCommand(source, this, flag);
        AssetsManager.getInstance().getAsset(command, AssetsManager.ResultType.FONT);
    }

    public void setTextColor(int color){
        mTextPaint.setColor(color);
        invalidate();
    }
}
