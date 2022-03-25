package com.kinetise.data.calcmanager;

import com.kinetise.data.descriptors.*;
import com.kinetise.data.descriptors.calcdescriptors.AGElementCalcDesc;
import com.kinetise.data.descriptors.calcdescriptors.AGViewCalcDesc;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.descriptors.types.AGUnitType;
import com.kinetise.data.descriptors.types.Quad;
import com.kinetise.data.descriptors.types.SizeQuad;
import com.kinetise.helpers.calcmanagerhelper.CalcManagerHelper;

public abstract class AbstractCalculate implements ICalculate {

    @Override
    public void measureBlockWidth(AbstractAGElementDataDesc dataDesc, double maxWidth, double maxSpaceForMax) {
        AbstractAGViewDataDesc viewDataDesc = (AbstractAGViewDataDesc) dataDesc;
        AGViewCalcDesc calcDesc = viewDataDesc.getCalcDesc();

        measureHorizontalMargins(viewDataDesc);
        measureBorder(viewDataDesc);

        AGUnitType widthUnit = viewDataDesc.getWidth().getDescUnit();
        switch(widthUnit) {
            case KPX:
                measureWidthForKpxValue(viewDataDesc);
                measureHorizontalPaddings(viewDataDesc);
                break;
            case MAX:
                measureWidthForMax(viewDataDesc, maxSpaceForMax);
                measureHorizontalPaddings(viewDataDesc);
                break;
            case PERCENT:
                measureWidthForPercentValue(viewDataDesc);
                measureHorizontalPaddings(viewDataDesc);
                break;
            case MIN:
                measureHorizontalPaddings(viewDataDesc);
                double contentSpace = maxWidth - getTotalHorizontalMarginWidth(calcDesc) - getTotalBorderWidth(calcDesc) - getTotalHorizontalPaddingWidth(calcDesc);
                double contentSpaceForMax = maxSpaceForMax - getTotalHorizontalMarginWidth(calcDesc) - getTotalBorderWidth(calcDesc) - getTotalHorizontalPaddingWidth(calcDesc);
                measureWidthForMin(dataDesc, contentSpace, contentSpaceForMax);
        }
    }

    private void measureWidthForKpxValue(AbstractAGViewDataDesc dataDesc) {
        dataDesc.getCalcDesc().setWidth(dataDesc.getWidth().inPixels());
    }


    private void measureWidthForMax(AbstractAGViewDataDesc dataDesc, double maxSpaceForMax) {
        AGViewCalcDesc calcDesc = dataDesc.getCalcDesc();
        dataDesc.getCalcDesc().setWidth(maxSpaceForMax - getTotalHorizontalMarginWidth(calcDesc) - getTotalBorderWidth(calcDesc));
    }

    private void measureWidthForPercentValue(AbstractAGViewDataDesc dataDesc) {
        double freeSpace;
        AbstractAGViewDataDesc parentContainer = dataDesc.getParentContainer();
        if (parentContainer != null) {
            freeSpace = parentContainer.getCalcDesc().getWidth();
        } else {
            freeSpace = getCalcManager().getScreenWidth();
        }
        double percentValue = dataDesc.getWidth().getDescValue();
        dataDesc.getCalcDesc().setWidth(measurePercent(percentValue, freeSpace));
    }

    @Override
    public void measureBlockHeight(AbstractAGElementDataDesc dataDesc, double maxHeight, double maxSpaceForMax) {
        AbstractAGViewDataDesc viewDataDesc = (AbstractAGViewDataDesc) dataDesc;
        AGViewCalcDesc calcDesc = viewDataDesc.getCalcDesc();

        measureVerticalMargins(viewDataDesc);
        //we first measureBlockWidth so border was already measured then, we don't need to repeat that step

        AGUnitType heightUnit = viewDataDesc.getHeight().getDescUnit();
        switch(heightUnit){
            case KPX:
                measureHeightForKpxValue(viewDataDesc);
                measureVerticalPaddings(viewDataDesc);
                break;
            case PERCENT:
                measureHeightForPercentValue(viewDataDesc);
                measureVerticalPaddings(viewDataDesc);
                break;
            case MAX:
                measureHeightForMax(viewDataDesc, maxSpaceForMax);
                measureVerticalPaddings(viewDataDesc);
                break;
            case MIN:
                measureVerticalPaddings(viewDataDesc);
                double contentVerticalSpace = maxHeight - calcDesc.getMarginTop() - calcDesc.getMarginBottom() - getTotalBorderHeight(calcDesc) - calcDesc.getPaddingTop() - calcDesc.getPaddingBottom();
                double contentVerticalSpaceForMax = maxSpaceForMax - getTotalVerticalPaddingsHeight(calcDesc) -getTotalVerticalMarginHeight(calcDesc) - getTotalBorderHeight(calcDesc);
                measureHeightForMin(dataDesc,contentVerticalSpace, contentVerticalSpaceForMax);
        }

        measureRadius(dataDesc);
    }

    private void measureHeightForKpxValue(AbstractAGViewDataDesc dataDesc) {
        dataDesc.getCalcDesc().setHeight(dataDesc.getHeight().inPixels());
    }

    private double measureVerticalMarginForPercent(AbstractAGViewDataDesc dataDesc, AGSizeDesc marginSize){
        AbstractAGViewDataDesc parentContainer = dataDesc.getParentContainer();
        IAGCollectionDataDesc section = dataDesc.getSection();
        int marginValue = marginSize.getDescValue();
        if (parentContainer != null) {
            return measurePercent(marginValue, parentContainer.getCalcDesc().getHeight());
        } else if (section instanceof AGBodyDataDesc) {
            AGBodyDataDesc bodyDataDesc = (AGBodyDataDesc) section;
            double bodySpaceHeight = getBodySpaceHeight(bodyDataDesc.getScreenDesc());
            return measurePercent(marginValue, bodySpaceHeight);
        } else if (section instanceof AGHeaderDataDesc || section instanceof AGNaviPanelDataDesc) {
            return measurePercent(marginValue, getCalcManager().getScreenHeight());
        }
        return 0;
    }

    private void measureHeightForPercentValue(AbstractAGViewDataDesc dataDesc) {
        AGElementCalcDesc calcDesc = dataDesc.getCalcDesc();
        AbstractAGViewDataDesc parentContainer = dataDesc.getParentContainer();
        IAGCollectionDataDesc section = dataDesc.getSection();
        double freeSpace = 0;
        if (parentContainer != null) {
            freeSpace = parentContainer.getCalcDesc().getHeight();
        } else if (section instanceof AGBodyDataDesc) {
            AGBodyDataDesc bodyDesc = (AGBodyDataDesc)section;
            freeSpace = getBodySpaceHeight(bodyDesc.getScreenDesc());
        } else if ((section instanceof AGHeaderDataDesc)||(section instanceof AGNaviPanelDataDesc)) {
            freeSpace = getCalcManager().getScreenHeight();
        }

        int height = dataDesc.getHeight().getDescValue();
        calcDesc.setHeight(measurePercent(height,freeSpace));
    }

    private void measureHeightForMax(AbstractAGViewDataDesc dataDesc, double maxSpaceForMax) {
        IAGCollectionDataDesc section = dataDesc.getSection();
        AGViewCalcDesc calcDesc = dataDesc.getCalcDesc();
        if (section instanceof AGBodyDataDesc && dataDesc.getParentContainer() == null) {
            AGBodyDataDesc bodyDataDesc = (AGBodyDataDesc) section;
            double bodySpaceHeight = getBodySpaceHeight(bodyDataDesc.getScreenDesc());
            calcDesc.setHeight((bodySpaceHeight - calcDesc.getMarginTop() - calcDesc.getMarginBottom() - getTotalBorderHeight(calcDesc)));
        } else {
            calcDesc.setHeight(maxSpaceForMax - getTotalVerticalMarginHeight(calcDesc) - getTotalBorderHeight(calcDesc));
        }
    }

    private void measureHorizontalMargins(AbstractAGViewDataDesc viewDataDesc) {
        measureMarginLeft(viewDataDesc);
        measureMarginRight(viewDataDesc);
    }

    private void measureMarginLeft(AbstractAGViewDataDesc dataDesc) {
        dataDesc.getCalcDesc().setMarginLeft(measureHorizontalMargin(dataDesc, dataDesc.getMargin().getLeft()));
    }

    private void measureMarginRight(AbstractAGViewDataDesc dataDesc) {
        dataDesc.getCalcDesc().setMarginRight(measureHorizontalMargin(dataDesc, dataDesc.getMargin().getRight()));
    }

    protected void measureVerticalMargins(AbstractAGViewDataDesc viewDataDesc) {
        measureMarginTop(viewDataDesc);
        measureMarginBottom(viewDataDesc);
    }

    private void measureMarginTop(AbstractAGViewDataDesc dataDesc) {
        dataDesc.getCalcDesc().setMarginTop(measureVerticalMargin(dataDesc, dataDesc.getMargin().getTop()));
    }

    private void measureMarginBottom(AbstractAGViewDataDesc dataDesc) {
        dataDesc.getCalcDesc().setMarginBottom(measureVerticalMargin(dataDesc, dataDesc.getMargin().getBottom()));
    }

    private double getTotalHorizontalMarginWidth(AGViewCalcDesc calcDesc) {
        return calcDesc.getMarginLeft() + calcDesc.getMarginRight();
    }

    private double getTotalVerticalMarginHeight(AGViewCalcDesc calcDesc) {
        return calcDesc.getMarginTop() + calcDesc.getMarginBottom();
    }

    protected void measureHorizontalPaddings(AbstractAGViewDataDesc viewDataDesc) {
        measurePaddingLeft(viewDataDesc);
        measurePaddingRight(viewDataDesc);
    }

    protected void measureVerticalPaddings(AbstractAGViewDataDesc viewDataDesc) {
        measurePaddingTop(viewDataDesc);
        measurePaddingBottom(viewDataDesc);
    }

    protected double getTotalHorizontalPaddingWidth(AGViewCalcDesc calcDesc) {
        return (calcDesc.getPaddingLeft() + calcDesc.getPaddingRight());
    }

    protected double getTotalVerticalPaddingsHeight(AGViewCalcDesc calcDesc) {
        return calcDesc.getPaddingTop() + calcDesc.getPaddingBottom();
    }

    protected double getTotalBorderWidth(AGViewCalcDesc calcDesc) {
        return calcDesc.getBorder().getHorizontalBorderWidth();
    }

    protected double getTotalBorderHeight(AGViewCalcDesc calcDesc) {
        return calcDesc.getBorder().getVerticalBorderHeight();
    }

    private double measureHorizontalMargin(AbstractAGViewDataDesc dataDesc, AGSizeDesc marginSize) {
        AGUnitType descUnit = marginSize.getDescUnit();
        switch (descUnit) {
            case KPX:
                return marginSize.inPixels();
            case PERCENT:
                return measureHorizontalMarginForPercent(dataDesc, marginSize);
            default:
                return 0;
        }
    }

    private double measureVerticalMargin(AbstractAGViewDataDesc dataDesc, AGSizeDesc marginSize) {
        AGUnitType descUnit = marginSize.getDescUnit();
        switch (descUnit) {
            case KPX:
                return marginSize.inPixels();
            case PERCENT:
                return measureVerticalMarginForPercent(dataDesc, marginSize);
            default:
                return 0;
        }
    }

    private double measureHorizontalMarginForPercent(AbstractAGViewDataDesc dataDesc, AGSizeDesc marginSize){
        int marginValue = marginSize.getDescValue();
        if (dataDesc.getParentContainer() != null) {
            return measurePercent(marginValue, dataDesc.getParentContainer().getCalcDesc().getWidth());
        } else {
            return measurePercent(marginValue, getCalcManager().getScreenWidth());
        }
    }

    private double measureVerticalPadding(AbstractAGElementDataDesc dataDesc, double paddingValue, AGUnitType paddingSizeUnit) {
        if (paddingSizeUnit.equals(AGUnitType.KPX)) {
            return CalcManagerHelper.KPXtoPixels(paddingValue);
        } else if (paddingSizeUnit.equals(AGUnitType.PERCENT)) {
            return measurePercent(paddingValue, dataDesc.getCalcDesc().getHeight());
        }
        return 0;
    }

    private double measureHorizontalPadding(AbstractAGElementDataDesc dataDesc, double paddingValue, AGUnitType paddingSizeUnit) {
        if (paddingSizeUnit.equals(AGUnitType.KPX)) {
            return CalcManagerHelper.KPXtoPixels(paddingValue);
        } else if (paddingSizeUnit.equals(AGUnitType.PERCENT)) {
            return measurePercent(paddingValue, dataDesc.getCalcDesc().getWidth());
        }
        return 0;
    }

    private void measurePaddingLeft(AbstractAGViewDataDesc dataDesc) {
        AGSizeDesc padding = dataDesc.getPaddingLeft();
        double paddingSize = measureHorizontalPadding(dataDesc, padding.getDescValue(), padding.getDescUnit());
        dataDesc.getCalcDesc().setPaddingLeft(paddingSize);
    }

    private void measurePaddingRight(AbstractAGViewDataDesc dataDesc) {
        AGSizeDesc padding = dataDesc.getPaddingRight();
        double paddingSize = measureHorizontalPadding(dataDesc, padding.getDescValue(), padding.getDescUnit());
        dataDesc.getCalcDesc().setPaddingRight(paddingSize);
    }

    private void measurePaddingTop(AbstractAGViewDataDesc dataDesc) {
        AGSizeDesc padding = dataDesc.getPaddingTop();
        double paddingSize = measureVerticalPadding(dataDesc, padding.getDescValue(), padding.getDescUnit());
        dataDesc.getCalcDesc().setPaddingTop(paddingSize);
    }

    private void measurePaddingBottom(AbstractAGViewDataDesc dataDesc) {
        AGSizeDesc padding = dataDesc.getPaddingBottom();
        double paddingSize = measureVerticalPadding(dataDesc, padding.getDescValue(), padding.getDescUnit());
        dataDesc.getCalcDesc().setPaddingBottom(paddingSize);
    }

    protected double measurePercent(double percentValue, double originalValue) {
        if (percentValue < 1) {
            return 0;
        } else {
            return ((originalValue * percentValue) / 100);
        }
    }

    protected void measureBorder(AbstractAGElementDataDesc dataDesc) {
        AbstractAGViewDataDesc desc = (AbstractAGViewDataDesc) dataDesc;
        SizeQuad border = desc.getBorder();
        AGViewCalcDesc calcDesc = desc.getCalcDesc();
        Quad calcBorder = calcDesc.getBorder();

        calcBorder.setLeft(border.getLeft().inPixels());
        calcBorder.setRight(border.getRight().inPixels());
        calcBorder.setTop(border.getTop().inPixels());
        calcBorder.setBottom(border.getBottom().inPixels());
    }

    private double getBodySpaceHeight(AGScreenDataDesc screenDesc) {
        AGHeaderDataDesc header = screenDesc.getScreenHeader();
        AGNaviPanelDataDesc navipanel = screenDesc.getScreenNaviPanel();

        double headerHeight = header != null ? header.getCalcDesc().getHeight() : 0;
        double navipanelHeight = navipanel != null ? navipanel.getCalcDesc().getHeight() : 0;

        return getCalcManager().getScreenHeight() - headerHeight - navipanelHeight;
    }

    private void measureRadius(AbstractAGElementDataDesc desc) {
        AbstractAGViewDataDesc dataDesc = (AbstractAGViewDataDesc) desc;
        AGViewCalcDesc calcDesc = dataDesc.getCalcDesc();

        double halfWidth = calcDesc.getWidth()/2;
        double halfHeight = calcDesc.getHeight()/2;

        Quad border = calcDesc.getBorder();
        double topLeftLimit = Math.min(border.getTop() + halfHeight, border.getLeft() + halfWidth);
        double topRightLimit = Math.min(border.getTop() + halfHeight, border.getRight() + halfWidth);
        double bottomLeftLimit = Math.min(border.getBottom() + halfHeight, border.getLeft() + halfWidth);
        double bottomRightLimit = Math.min(border.getBottom() + halfHeight, border.getRight() + halfWidth);

        calcDesc.setRadiusTopLeft(Math.min(dataDesc.getRadiusTopLeft().inPixels(), topLeftLimit));
        calcDesc.setRadiusTopRight(Math.min(dataDesc.getRadiusTopRight().inPixels(), topRightLimit));
        calcDesc.setRadiusBottomLeft( Math.min(dataDesc.getRadiusBottomLeft().inPixels(), bottomLeftLimit));
        calcDesc.setRadiusBottomRight(Math.min(dataDesc.getRadiusBottomRight().inPixels(), bottomRightLimit));
    }

    public void layout(AbstractAGElementDataDesc desc){

    }

    protected CalcManager getCalcManager() {
        return CalcManager.getInstance();
    }

    public abstract void measureHeightForMin(AbstractAGElementDataDesc desc, double maxHeight, double maxSpaceForMax);

    public abstract void measureWidthForMin(AbstractAGElementDataDesc desc, double maxWidth, double maxSpaceForMax);
}
