package com.kinetise.helpers.drawing;

import android.graphics.*;
import android.view.View;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.types.AGSizeModeType;
import com.kinetise.data.descriptors.types.IntQuad;
import com.kinetise.data.systemdisplay.views.IAGView;

public class ViewDrawer<T extends View & IAGView> {
    protected Path mBorderPath;
    protected Path mBorderClipPath;

    private final Paint mBorderPaint = new Paint();
    private final Paint mBackgroundPaint = new Paint();
    private final Paint mBackgroundColorPaint = new Paint();
    private final Matrix mBackgroundMatrix = new Matrix();

    private RectF mBackgroundBounds;

    private Bitmap mBackgroundBitmap;
    private BitmapShader mBackgroundShader;
    protected IntQuad mBorderQuad;

    private final T mView;
    private int clippedBorderState =0;

    public ViewDrawer(T view) {
        mView = view;
        mBorderQuad = new IntQuad();
        setAntialiasing(mBackgroundPaint);
        mBackgroundPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        setAntialiasing(mBorderPaint);
        mBorderPaint.setStyle(Paint.Style.FILL);

        setAntialiasing(mBackgroundColorPaint);
        mBackgroundColorPaint.setStyle(Paint.Style.FILL);
    }

    private void setAntialiasing(Paint paint) {
         paint.setAntiAlias(true);
         paint.setFilterBitmap(true);
    }

    protected AbstractAGViewDataDesc getDataDescriptor() {
        return (AbstractAGViewDataDesc) mView.getDescriptor();
    }

    protected AGViewCalcDesc getCalcDesc() {
        return getDataDescriptor().getCalcDesc();
    }

    /** Sets drawer settings based on related view descriptor.
     *  Should be called only when related view is created or descriptor is changed.
     *
     */

    public void refresh() {
        mBorderPaint.setColor(getDataDescriptor().getCurrentBorderColor());
        mBackgroundColorPaint.setColor(getDataDescriptor().getBackgroundColor());
        mBorderQuad = new IntQuad();
        mBorderQuad.left = getCalcDesc().getBorder().getLeftAsInt();
        mBorderQuad.right = getCalcDesc().getBorder().getRightAsInt();
        mBorderQuad.top = getCalcDesc().getBorder().getTopAsInt();
        mBorderQuad.bottom = getCalcDesc().getBorder().getBottomAsInt();

        setRenderPaths();
        setMatrix();
    }

    public T getView() {
        return mView;
    }

    protected Path getBorderPath() {
        AGViewCalcDesc calcDesc = getCalcDesc();

        Path path = new Path();

        Rect drawingRect = new Rect();
        getView().getDrawingRect(drawingRect);
        RectF innerSrc = getBackgroundRect(drawingRect);

        path.setFillType(Path.FillType.EVEN_ODD);
        path.addRoundRect(new RectF(0, 0, drawingRect.width(), drawingRect.height()), prepareRadiuses(calcDesc), Path.Direction.CW);
        path.addRoundRect(innerSrc, prepareRadiuses(calcDesc, mBorderQuad), Path.Direction.CW);
        return path;
    }

    private RectF getBackgroundRect(Rect inSrcRect) {
        return new RectF(mBorderQuad.left, mBorderQuad.top, inSrcRect.width() - mBorderQuad.right, inSrcRect.height() - mBorderQuad.bottom);
    }

    public Path getBorderClipPath(){
        Path path = new Path();
        Rect src = new Rect();
        AGViewCalcDesc calcDesc = getCalcDesc();
        getView().getDrawingRect(src);
        RectF innerSrc = new RectF(0, 0, src.width() - (mBorderQuad.left+mBorderQuad.right), src.height() - (mBorderQuad.bottom+mBorderQuad.top));
        path.addRoundRect(innerSrc, prepareRadiuses(calcDesc, mBorderQuad), Path.Direction.CW);
        return path;
    }

    /**
     * prepares Paths for border, background, and image
     */
    protected void setRenderPaths() {
        AGViewCalcDesc calcDesc = getCalcDesc();
        if (calcDesc != null) {
            if (!mBorderQuad.isAllZeros()) {
                mBorderPath = getBorderPath();
            }
            if (calcDesc.hasRadiuses()) {
                mBorderClipPath = getBorderClipPath();
            } else {
                mBorderClipPath = null;
            }
        }
    }

    public void setBackgroundBitmap(Bitmap background) {
        if (background != null) {
            mBackgroundBitmap = background;
            mBackgroundShader = new BitmapShader(mBackgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            AGSizeModeType sizemode = getSizeModeFromDescriptor();

            int viewWidth = getCalculatedWidth() - (mBorderQuad.left+mBorderQuad.right);
            int viewHeight = getCalculatedHeight() - (mBorderQuad.bottom+mBorderQuad.top);
            ScaleHelper scaleHelper = new ScaleHelper(sizemode, viewWidth, viewHeight, mBackgroundBitmap.getWidth(), mBackgroundBitmap.getHeight());
            Matrix scaleMatrix = scaleHelper.getMatrixForScale();
            mBackgroundMatrix.set(scaleMatrix);
            mBackgroundShader.setLocalMatrix(mBackgroundMatrix);
            mBackgroundPaint.setShader(mBackgroundShader);

            mBackgroundBounds = scaleHelper.getResultBounds();

            mView.invalidate();
        }
    }

    private AGSizeModeType getSizeModeFromDescriptor() {
        AbstractAGElementDataDesc descriptor = getView().getDescriptor();

        if (descriptor instanceof AbstractAGViewDataDesc) {
            return ((AbstractAGViewDataDesc) descriptor).getBackgroundSizeMode();
        }
        return AGSizeModeType.STRETCH;
    }

    protected int getCalculatedWidth() {
        return (int) Math.round(getCalcDesc().getBlockWidth() - getCalcDesc().getMarginRight() - getCalcDesc().getMarginLeft());
    }

    protected int getCalculatedHeight() {
        return (int) Math.round(getCalcDesc().getBlockHeight() - getCalcDesc().getMarginTop() - getCalcDesc().getMarginBottom());
    }

    protected void setMatrix() {
    if (mBackgroundBitmap != null) {
        int contentWidth = (int) Math.round(getCalcDesc().getWidth());
        int contentHeight = (int) Math.round(getCalcDesc().getHeight());

        AGSizeModeType sizemode = getSizeModeFromDescriptor();

        ScaleHelper scaleHelper = new ScaleHelper(sizemode, contentWidth, contentHeight, mBackgroundBitmap.getWidth(), mBackgroundBitmap.getHeight());
        mBackgroundMatrix.set(scaleHelper.getMatrixForScale());
        mBackgroundShader.setLocalMatrix(mBackgroundMatrix);

        mBackgroundBounds = scaleHelper.getResultBounds();
    }
}

    /**
     * Draws background, border, backgroundcolor on current view should be called before View.draw(Canvas canvas)
     *
     * @param canvas
     */
    public void draw(Canvas canvas) {
        canvas.translate(getView().getScrollX(), getView().getScrollY());
        clippedBorderState = canvas.save();
        canvas.translate(mBorderQuad.left, mBorderQuad.top);
        setClipPath(canvas, mBorderClipPath);
        drawInternal(canvas);

        canvas.translate(-(mBorderQuad.left + getView().getScrollX()), -(mBorderQuad.top + getView().getScrollY()));
    }

    private void setClipPath(Canvas canvas, Path borderClipPath) {
        if(borderClipPath!=null)
            try {
                canvas.clipPath(borderClipPath);
            } catch (Exception e){
                try{
                    mView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                    canvas.clipPath(borderClipPath);
                }
                catch(Exception ex){

                }
            }
        }

    protected void drawInternal(Canvas canvas) {
        //background color
        canvas.drawColor(mBackgroundColorPaint.getColor());
        //background image
        if (mBackgroundBitmap != null) {
            canvas.drawRect(mBackgroundBounds, mBackgroundPaint);
        }
    }

    public void onAfterDispatchDraw(Canvas canvas) {
        canvas.restoreToCount(clippedBorderState);
        if (!mBorderQuad.isAllZeros()) {
            canvas.drawPath(mBorderPath, mBorderPaint);
        }
    }

    private float[] prepareRadiuses(AGViewCalcDesc calcDesc){
        float[] radiuses = new float[8];

        int radiusTopLeft = calcDesc.getRadiusTopLeft();
        int radiusTopRight = calcDesc.getRadiusTopRight();
        int radiusBottomRight = calcDesc.getRadiusBottomRight();
        int radiusBottomLeft = calcDesc.getRadiusBottomLeft();

        radiuses[0] = radiusTopLeft;
        radiuses[1] = radiusTopLeft;
        radiuses[2] = radiusTopRight;
        radiuses[3] = radiusTopRight;
        radiuses[4] = radiusBottomRight;
        radiuses[5] = radiusBottomRight;
        radiuses[6] = radiusBottomLeft;
        radiuses[7] = radiusBottomLeft;

        return radiuses;
    }

    private float[] prepareRadiuses(AGViewCalcDesc calcDesc, IntQuad border) {
        float[] radiuses = new float[8];

        int radiusTopLeft = calcDesc.getRadiusTopLeft();
        int radiusTopRight = calcDesc.getRadiusTopRight();
        int radiusBottomRight = calcDesc.getRadiusBottomRight();
        int radiusBottomLeft = calcDesc.getRadiusBottomLeft();

        radiuses[0] = radiusTopLeft > border.left ? radiusTopLeft - border.left : 0;
        radiuses[1] = radiusTopLeft > border.top ? radiusTopLeft - border.top : 0;
        radiuses[2] = radiusTopRight > border.right ? radiusTopRight - border.right : 0;
        radiuses[3] = radiusTopRight > border.top ? radiusTopRight - border.top : 0;
        radiuses[4] = radiusBottomRight > border.right ? radiusBottomRight - border.right : 0;
        radiuses[5] = radiusBottomRight > border.bottom ? radiusBottomRight - border.bottom : 0;
        radiuses[6] = radiusBottomLeft > border.left ? radiusBottomLeft - border.left : 0;
        radiuses[7] = radiusBottomLeft > border.bottom ? radiusBottomLeft - border.bottom : 0;

        return radiuses;
    }
}
