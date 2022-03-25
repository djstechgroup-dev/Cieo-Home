package com.kinetise.helpers.drawing;

import android.graphics.Canvas;
import android.graphics.Paint;
import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGContainerCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.datadescriptors.*;
import com.kinetise.data.systemdisplay.views.AGContainerView;

import java.util.List;

public class ContainerDrawer extends ViewDrawer<AGContainerView> {

    private static boolean ROUND_INNER_BORDER = false;
    private final Paint mInnerBorderPaint = new Paint();

    public ContainerDrawer(AGContainerView view) {
        super(view);
    }

    @Override
    public void refresh() {
        super.refresh();
        mInnerBorderPaint.setColor(getDataDescriptor().getSeparatorColor());
    }

    @Override
    protected AGContainerCalcDesc getCalcDesc() {
        return (AGContainerCalcDesc)super.getCalcDesc();
    }

    @Override
    protected AbstractAGContainerDataDesc getDataDescriptor() {
        return (AbstractAGContainerDataDesc) super.getDataDescriptor();
    }

    private void drawVerticalInnerBorder(final Canvas canvas, final AGViewCalcDesc childCalcDesc) {
        double itemSeparation = getCalcDesc().getItemSeparation();
        double innerBorder = getCalcDesc().getItemBorder();
        if (innerBorder > 0 && innerBorder < 1) {
            innerBorder = 1;
        }

        double yPossition = childCalcDesc.getPositionY() + getCalcDesc().getContentVerticalOffset();
        double itemContentWidth = Math.max(getCalcDesc().getContentWidth(), 0);
        double left = getCalcDesc().getContetHorizontelOffset() + getCalcDesc().getItemBorderMarginStart();
        double top = yPossition + childCalcDesc.getBlockHeight() + (itemSeparation/2) - (innerBorder/2);
        double right = left + itemContentWidth - (getCalcDesc().getItemBorderMarginEnd() + getCalcDesc().getItemBorderMarginStart());
        double bottom = top + innerBorder;

        canvas.translate(-getView().getScrollX(),-getView().getScrollY());
        canvas.drawRect((float) left, (float) top, (float) right, (float) bottom, mInnerBorderPaint);
        canvas.translate(getView().getScrollX(),getView().getScrollY());
    }

    private void drawHorizontalInnerBorder(final Canvas canvas, final AGViewCalcDesc childCalcDesc) {

        AGContainerCalcDesc containerCalcDesc = getCalcDesc();
        double itemSeparation = containerCalcDesc.getItemSeparation();
        double innerBorder = containerCalcDesc.getItemBorder();
        if (innerBorder > 0 && innerBorder < 1) {
            innerBorder = 1;
        }

        double xPosition = childCalcDesc.getPositionX() + containerCalcDesc.getContetHorizontelOffset();
        double left = xPosition + childCalcDesc.getBlockWidth() + (itemSeparation/2) - (innerBorder/2);
        double right = left + innerBorder;
        double top = containerCalcDesc.getContentVerticalOffset() + containerCalcDesc.getItemBorderMarginStart();
        double itemContentHeight = Math.max(containerCalcDesc.getContentHeight(), 0);
        double bottom = top + itemContentHeight - (containerCalcDesc.getItemBorderMarginEnd() + containerCalcDesc.getItemBorderMarginStart());

        canvas.translate(-getView().getScrollX(),-getView().getScrollY());
        canvas.drawRect((float) left, (float) top, (float) right, (float) bottom, mInnerBorderPaint);
        canvas.translate(getView().getScrollX(), getView().getScrollY());
    }

    @Override
    public void onAfterDispatchDraw(Canvas canvas) {
        super.onAfterDispatchDraw(canvas);
        //Musimy sclipować obszar w którym rysują się innerbordery - w tym paddingi!
        int saveCount = canvas.getSaveCount();

        if (getCalcDesc().getItemSeparation() > 0 && getView().getChildCount() > 1) {

            List<AbstractAGElementDataDesc> childDescs = getDataDescriptor().getPresentControls();

            for (int i = 0; i < childDescs.size() - 1; i++) {
                if (childDescs.get(i) != null) {
                    final AGViewCalcDesc childCalcDesc = ((AbstractAGViewDataDesc)childDescs.get(i)).getCalcDesc();

                    if (isVerticalContainer(getDataDescriptor())) {
                            drawVerticalInnerBorder(canvas, childCalcDesc);
                    } else if (isHorizontalContainer(getDataDescriptor())) {
                            drawHorizontalInnerBorder(canvas, childCalcDesc);
                    }
                }
            }
        }
        canvas.restoreToCount(saveCount);
    }

    private boolean isHorizontalContainer(final AbstractAGContainerDataDesc desc) {
        return (desc instanceof AGContainerHorizontalDataDesc || desc instanceof AGDataFeedHorizontalDataDesc ||
                desc instanceof AGRadioGroupHorizontalDataDesc);
    }

    private boolean isVerticalContainer(final AbstractAGContainerDataDesc desc) {
        return (desc instanceof AGContainerVerticalDataDesc || desc instanceof AGDataFeedVerticalDataDesc ||
                desc instanceof AGRadioGroupVerticalDataDesc);
    }
}
