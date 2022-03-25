package com.kinetise.helpers.drawing;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import com.kinetise.data.descriptors.types.AGSizeModeType;

public class ScaleHelper {
    private final AGSizeModeType scaleType;
    private final int viewWidth;
    private final int viewHeight;
    private final int imgWidth;
    private final int imgHeight;

    private PointF mScale = new PointF(1f, 1f);
    private PointF mTranslation = new PointF(0f, 0f);
    private Matrix transformationMatrix;
    private RectF resultBounds;

    public ScaleHelper(AGSizeModeType scaleType, int viewWidth, int viewHeight, int imgWidth, int imgHeight) {
        this.scaleType = scaleType;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
    }

    public void calculateTranslationAndScale() {
        if (viewHeight <= 0 || viewWidth <= 0) {
            mScale = new PointF(0f, 0f);
        } else {
            switch (scaleType) {
                case LONGEDGE:
                    mScale.x = mScale.y = Math.min((float) viewWidth / (float) imgWidth,
                            (float) viewHeight / (float) imgHeight);

                    mTranslation.x = Math.round((viewWidth - imgWidth * mScale.x) * 0.5f);
                    mTranslation.y = Math.round((viewHeight - imgHeight * mScale.y) * 0.5f);
                    break;

                case SHORTEDGE:
                    if (imgWidth * viewHeight > viewWidth * imgHeight) {
                        float scaleFactorShortedge = (float) viewHeight / (float) imgHeight;
                        mScale = new PointF(scaleFactorShortedge, scaleFactorShortedge);
                        mTranslation.x = Math.round((viewWidth - imgWidth * mScale.x) * 0.5f);
                    } else {
                        float scaleFactorShortedge = (float) viewWidth / (float) imgWidth;
                        mScale = new PointF(scaleFactorShortedge, scaleFactorShortedge);
                        mTranslation.y = Math.round((viewHeight - imgHeight * mScale.y) * 0.5f);
                    }

                    break;

                case STRETCH:
                    mScale.x = viewWidth / (float) imgWidth;
                    mScale.y = viewHeight / (float) imgHeight;
                    break;
            }
        }

        transformationMatrix = new Matrix();
        transformationMatrix.setScale(mScale.x, mScale.y);
        transformationMatrix.postTranslate(mTranslation.x, mTranslation.y);

        resultBounds = new RectF(mTranslation.x, mTranslation.y, mTranslation.x + imgWidth * mScale.x, mTranslation.y + imgHeight * mScale.y);
    }

    public Matrix getMatrixForScale() {
        if (transformationMatrix == null)
            calculateTranslationAndScale();
        return transformationMatrix;
    }

    public RectF getResultBounds() {
        if (resultBounds == null) {
            calculateTranslationAndScale();
        }
        return resultBounds;
    }
}
