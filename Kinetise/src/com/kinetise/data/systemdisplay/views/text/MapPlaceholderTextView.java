package com.kinetise.data.systemdisplay.views.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * @author: Marcin Narowski
 * Date: 27.02.14
 * Time: 10:07
 */
public class MapPlaceholderTextView extends TextView {
    Rect rect = new Rect();
    public MapPlaceholderTextView(Context context) {
        super(context);
        init();

    }

    public MapPlaceholderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapPlaceholderTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
    	setGravity(Gravity.CENTER);
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
////        int width,height;
////        width = height = MeasureSpec.makeMeasureSpec(256,MeasureSpec.EXACTLY);
//        super.onMeasure(MeasureSpec.makeMeasureSpec(256, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(256, MeasureSpec.EXACTLY));
//    }
    
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
    		int bottom) {
    	super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        getDrawingRect(rect);
        canvas.rotate(-45, rect.width()/2, rect.height()/2);
//        canvas.translate(rect.height()/4,rect.width()/4);
        super.onDraw(canvas);
        canvas.restore();
    }
}
