package com.kinetise.data.descriptors.helpers;

import android.graphics.Color;

import com.kinetise.data.descriptors.AGBodyDataDesc;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGSectionDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.StringVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGErrorDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGLoadingDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGTextDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGTextImageDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.TextDescriptor;
import com.kinetise.data.descriptors.desctriptorvisitors.FindDescendantByIdVisitor;
import com.kinetise.data.descriptors.desctriptorvisitors.FindDescendantsByTypeVisitor;
import com.kinetise.data.descriptors.types.AGAlignType;
import com.kinetise.data.descriptors.types.AGSizeDesc;
import com.kinetise.data.descriptors.types.AGSizeModeType;
import com.kinetise.data.descriptors.types.AGTextAlignType;
import com.kinetise.data.descriptors.types.AGTextVAlignType;
import com.kinetise.data.descriptors.types.AGUnitType;
import com.kinetise.data.descriptors.types.AGVAlignType;
import com.kinetise.data.descriptors.types.SizeQuad;
import com.kinetise.data.sourcemanager.LanguageManager;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Helper for all DataDesc related operations
 * */
public class DataDescHelper {

    /**
     * Creates new {@link com.kinetise.data.descriptors.desctriptorvisitors.FindDescendantByIdVisitor} and traverse
     * descriptors hierarchy to find one with given id
     * @param desc Descriptor to be traversed
     * @param id Id of descriptor we are searching for
     * @return Found descriptor(may be null)
     * */
    public static AbstractAGElementDataDesc findDescendantById(
            AbstractAGElementDataDesc desc, String id) {
        FindDescendantByIdVisitor visitor = new FindDescendantByIdVisitor(id);
        desc.accept(visitor);

        return visitor.getFoundDataDesc();
    }
    /**
     * Recursively traverses descriptors tree to find all instances of class using {@link com.kinetise.data.descriptors.desctriptorvisitors.FindDescendantsByTypeVisitor}
     * and returns array of found descriptors
     * @param desc Descriptor to be traversed
     * @param type Class of which instances are we searching for
     * @return Array of found descriptors
     * */
    public static <T extends AbstractAGElementDataDesc> T[] findDescendant(AbstractAGElementDataDesc desc, Class<T> type) {

        FindDescendantsByTypeVisitor<T> visitor = new FindDescendantsByTypeVisitor<T>(type);
        desc.accept(visitor);

        ArrayList<T> descs = visitor.getFoundDataDescriptors();

        T[] array = (T[]) Array.newInstance(type, descs.size());
        for (int i = 0; i < descs.size(); i++) {
            array[i] = descs.get(i);
        }

        return array;
    }

    /**
     * Creates new ErrorDataDesc with proper formatted values.This method is a factory for
     * all ErrorDataDesc elements
     * @param error Error string
     * @param height Height of control
     * @param width Width of control
     * @return New ErrorDataDesc mInstance
     * */
    public static AGErrorDataDesc getErrorDataDesc(String error, int width, int height) {

        AGErrorDataDesc errorDesc = new AGErrorDataDesc();

        VariableDataDesc svdd = new StringVariableDataDesc("assets://" + error);
        errorDesc.getImageDescriptor().setImageSrc(svdd);
        formatPlaceholder(errorDesc, width, height);

        return errorDesc;
    }
    /**
     * Creates new LoadingDataDesc with proper formatted values.This method is a factory for
     * all LoadingDataDesc elements
     * @return New LoadingDataDesc mInstance
     * @param width
     * @param height*/
    public static AGLoadingDataDesc createLoadingDataDesc(int width, int height) {
        AGLoadingDataDesc loadingDesc = new AGLoadingDataDesc();
        setLoadingDescriptorParameters(width, height, loadingDesc);
        return loadingDesc;
    }

    private static void setLoadingDescriptorParameters(int width, int height, AGLoadingDataDesc loadingDesc) {
        loadingDesc.setWidth(new AGSizeDesc(width, AGUnitType.KPX));
        loadingDesc.setHeight(new AGSizeDesc(height, AGUnitType.KPX));
        loadingDesc.getMargin().setBottom(new AGSizeDesc(0, AGUnitType.KPX));
        loadingDesc.getMargin().setLeft(new AGSizeDesc(150, AGUnitType.KPX));
        loadingDesc.getMargin().setRight(new AGSizeDesc(150, AGUnitType.KPX));
        loadingDesc.getMargin().setTop(new AGSizeDesc(150, AGUnitType.KPX));
        loadingDesc.setPaddingLeft(AGSizeDesc.ZEROKPX);
        loadingDesc.setPaddingTop(AGSizeDesc.ZEROKPX);
        loadingDesc.setPaddingBottom(AGSizeDesc.ZEROKPX);
        loadingDesc.setPaddingRight(AGSizeDesc.ZEROKPX);
        loadingDesc.setRadiusBottomLeft(AGSizeDesc.ZEROKPX);
        loadingDesc.setRadiusTopLeft(AGSizeDesc.ZEROKPX);
        loadingDesc.setRadiusTopRight(AGSizeDesc.ZEROKPX);
        loadingDesc.setRadiusBottomRight(AGSizeDesc.ZEROKPX);
        loadingDesc.setAlign(AGAlignType.CENTER);
        loadingDesc.setVAlign(AGVAlignType.CENTER);
    }

    private static void setBorderTo0Kpx(SizeQuad border) {
        border.setLeft(AGSizeDesc.ZEROKPX);
        border.setRight(AGSizeDesc.ZEROKPX);
        border.setTop(AGSizeDesc.ZEROKPX);
        border.setBottom(AGSizeDesc.ZEROKPX);
    }

    /**
     * Sets default values for all placeholder descriptors
     * @param width Width of control
     * @param height Height of control
     * @param placeholder Descriptors to be configured
     * */
    private static void formatPlaceholder(AGTextImageDataDesc placeholder, int width, int height) {
        placeholder.getImageDescriptor().setSizeMode(AGSizeModeType.LONGEDGE);
        TextDescriptor textDescriptor = placeholder.getTextDescriptor();
        textDescriptor.setMaxCharacters(0);
        textDescriptor.setMaxLines(0);
        textDescriptor.setText(new StringVariableDataDesc(null));
        textDescriptor.setFontSizeDesc(new AGSizeDesc(0, AGUnitType.KPX));
        textDescriptor.setTextVAlign(AGTextVAlignType.CENTER);
        textDescriptor.setTextAlign(AGTextAlignType.CENTER);
        textDescriptor.setTextDecoration(false);
        textDescriptor.setTextColor(Color.TRANSPARENT);

        // width/height max
        placeholder.setWidth(new AGSizeDesc(width, AGUnitType.KPX));
        placeholder.setHeight(new AGSizeDesc(height, AGUnitType.KPX));

        // margins
        placeholder.getMargin().setAll(AGSizeDesc.ZEROKPX);

        // padding
        placeholder.setPaddingBottom(new AGSizeDesc(0, AGUnitType.KPX));
        placeholder.setPaddingTop(new AGSizeDesc(0, AGUnitType.KPX));
        placeholder.setPaddingLeft(new AGSizeDesc(0, AGUnitType.KPX));
        placeholder.setPaddingRight(new AGSizeDesc(0, AGUnitType.KPX));

        placeholder.setRadiusBottomLeft(new AGSizeDesc(0, AGUnitType.KPX));
        placeholder.setRadiusBottomRight(new AGSizeDesc(0, AGUnitType.KPX));
        placeholder.setRadiusTopRight(new AGSizeDesc(0, AGUnitType.KPX));
        placeholder.setRadiusTopLeft(new AGSizeDesc(0, AGUnitType.KPX));

        placeholder.setBackgroundColor(-1);
        placeholder.setBorderColor(-1);
        placeholder.setAlign(AGAlignType.CENTER);
        placeholder.setVAlign(AGVAlignType.CENTER);

    }
    /**
     * Returns first parent of descriptor that with and height is different from {@link com.kinetise.data.descriptors.types.AGUnitType#MIN}
     * */
    public static AbstractAGElementDataDesc findAscendantToRepaint(AbstractAGElementDataDesc desc) {

        AbstractAGElementDataDesc foundDesc = null;
        if (desc instanceof AGScreenDataDesc) {
            foundDesc = desc;
        } else if (desc instanceof AbstractAGSectionDataDesc) {
            foundDesc = ((AbstractAGSectionDataDesc) desc).getScreenDesc();
        } else if (desc instanceof AbstractAGViewDataDesc) {

            AbstractAGViewDataDesc parent = (AbstractAGViewDataDesc) desc;
            while ((parent = parent.getParentContainer()) != null) {
                if (!parent.getWidth().getDescUnit().equals(AGUnitType.MIN) &&
                        !parent.getHeight().getDescUnit().equals(AGUnitType.MIN)) {
                    foundDesc = parent;
                    break;
                }
            }

            if (foundDesc == null) {
                foundDesc = (AbstractAGElementDataDesc) ((AbstractAGViewDataDesc) desc).getSection();
            }
        } else {
            throw new IllegalArgumentException("Cannot find ascendant to repaint");
        }

        return foundDesc;
    }

    /**
     * Factory class for creating screen replacement when screen hierarchy is too deep
     * */
    public static class ExceedDepthDescriptorFactory {
        private static final String TEXT_ERROR_ID = "text_control_error_depth";
        /**
         * @param id Id of screen we replace descriptor
         * @return Modified screen hierarchy
         * */
        public static AGScreenDataDesc createFor(String id) {
            AGScreenDataDesc screenDataDesc = new AGScreenDataDesc(id);
            screenDataDesc.setBackgroundColor(Color.BLACK);
            AGBodyDataDesc bodyDataDesc = new AGBodyDataDesc();

            AGTextDataDesc textControl = new AGTextDataDesc(TEXT_ERROR_ID);
            String value = LanguageManager.getInstance().getString(LanguageManager.ERROR_SCREEN_HIERARCHY);
            textControl.setWidth(new AGSizeDesc(0, AGUnitType.MAX));
            textControl.setHeight(new AGSizeDesc(0, AGUnitType.MAX));
            textControl.setAlign(AGAlignType.CENTER);
            textControl.setVAlign(AGVAlignType.CENTER);
            textControl.getTextDescriptor().setText(new StringVariableDataDesc(value));
            textControl.getTextDescriptor().setFontSizeDesc(new AGSizeDesc(50, AGUnitType.KPX));
            textControl.getTextDescriptor().setTextAlign(AGTextAlignType.CENTER);
            textControl.getTextDescriptor().setTextColor(Color.GRAY);
            textControl.getTextDescriptor().setTextVAlign(AGTextVAlignType.CENTER);

            textControl.getMargin().setAll(AGSizeDesc.ZEROKPX);

            textControl.setPaddingBottom(new AGSizeDesc(0, AGUnitType.KPX));
            textControl.setPaddingTop(new AGSizeDesc(0, AGUnitType.KPX));
            textControl.setPaddingLeft(new AGSizeDesc(0, AGUnitType.KPX));
            textControl.setPaddingRight(new AGSizeDesc(0, AGUnitType.KPX));

            textControl.setRadiusTopLeft(new AGSizeDesc(0, AGUnitType.KPX));
            textControl.setRadiusTopRight(new AGSizeDesc(0, AGUnitType.KPX));
            textControl.setRadiusBottomLeft(new AGSizeDesc(0, AGUnitType.KPX));
            textControl.setRadiusBottomRight(new AGSizeDesc(0, AGUnitType.KPX));

            bodyDataDesc.addControl(textControl);
            screenDataDesc.setScreenBody(bodyDataDesc);

            return screenDataDesc;
        }
    }
}
