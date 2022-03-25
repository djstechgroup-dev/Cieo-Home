package com.kinetise.data.systemdisplay.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.systemdisplay.LayoutHelper;
import com.kinetise.helpers.calcmanagerhelper.CalcManagerHelper;

public class InvalidView extends View {

    public static final int INVALIDVIEW_SIZE = 60;
    public static final int INVALIDVIEW_MARGIN = 6;
    public static final double TEXTSIZE_PERCENTAGE = 0.5;
    public static final double TEXTCENTER_PERCENTAGE = 0.6;
    private final int mBackgroundColor;

    public InvalidView(Context context, int backgroundColor) {
        super(context);
        mBackgroundColor = backgroundColor;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTriangle(canvas);
    }

    private void drawTriangle(Canvas canvas) {
        Path path = new Path();
        Paint paint = new Paint();

        path.moveTo(0, 0);
        path.lineTo(getWidth(), 0);
        path.lineTo(getWidth(), getHeight());
        path.close();

        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(mBackgroundColor);
        paint.setStyle(Paint.Style.FILL);

        canvas.drawPath(path, paint);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(Math.round(TEXTSIZE_PERCENTAGE * (double) getHeight()));

        canvas.drawText("!", Math.round(TEXTCENTER_PERCENTAGE * getWidth()), getHeight() / 2, textPaint);
    }

    public void layoutInvalidView(AGViewCalcDesc calcDesc, int right) {
        int invalidViewTop = ((int) Math.round(calcDesc.getBorder().getTop()));
        int invalidViewLeft = (right - getMeasuredWidth());
        int invalidViewBottom = (invalidViewTop + getMeasuredHeight());
        layout(invalidViewLeft, invalidViewTop, right, invalidViewBottom);
    }

    public void measure() {
        LayoutHelper.measureExactly(this, (int) CalcManagerHelper.KPXtoPixels(INVALIDVIEW_SIZE), (int) CalcManagerHelper.KPXtoPixels(INVALIDVIEW_SIZE));
    }

    public static InvalidView createInvalidView(Context context, int backgroundColor, final IFormView formView) {
        InvalidView view = new InvalidView(context, backgroundColor);
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                formView.showInvalidMessageToast();
            }
        });
        return view;
    }

}
