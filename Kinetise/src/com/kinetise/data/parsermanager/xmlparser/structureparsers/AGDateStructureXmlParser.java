package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.StringVariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGDateDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AGDatePickerDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGDateXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.helpers.time.DateSourceType;

public class AGDateStructureXmlParser extends AGTextStructureXmlParser {

    private static final String NODE_NAME = AGXmlNodes.CONTROL_DATE;

    @Override
    protected String getStructureRootNodeName() {
        return NODE_NAME;
    }

    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGDateDataDesc(id);
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc descriptor, String id, String value) {

        AGDateDataDesc desc = (AGDateDataDesc) descriptor;

        if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if (id
                .equals(AGDateXmlAttributes.DATE_FORMAT)) {
            setDateFormatForDataDescriptor(desc, value);
            return true;
        } else if (id.equals(AGDateXmlAttributes.DATE_SRC)) {
            desc.setDataSource(AGXmlParserHelper.getDateSrcFromString(value));
            return true;
        } else if (id.equals(AGDateXmlAttributes.TICKING)) {
            boolean ticking = AGXmlParserHelper.convertYesNoToBoolean(value);
            desc.setTicking(ticking);
            return true;
        } else if (id.equals(AGDateXmlAttributes.TIME_ZONE)) {
            if(value.equals("default"))
                desc.setIsDefaultTimezone(true);
            else
                desc.setIsDefaultTimezone(false);
            return true;
        }

        return false;
    }

    public static void setDateFormatForDataDescriptor(AbstractAGViewDataDesc desc, String format) {
        String pattern = format;

        if (pattern == null) {
            return;
        }

        boolean isLowerCaseAMPMmarker = true;

        if (pattern.contains("ampm")) {
            isLowerCaseAMPMmarker = true;
            pattern = pattern.replace("ampm", "a");
        }

        if (pattern.contains("AMPM")) {
            isLowerCaseAMPMmarker = false;
            pattern = pattern.replace("AMPM", "a");
        }

        pattern = pattern.replace("hh12", "xx");
        pattern = pattern.replace("h12", "x");

        // 'yyyy' and 'yy' is fine
        pattern = pattern.replace("mmmm", "MMMM");
        pattern = pattern.replace("mmm", "MMM");
        pattern = pattern.replace("mm", "MM");
        pattern = pattern.replace("m", "M");
        // 'd' and 'dd' is fine
        pattern = pattern.replace("dddd", "EEEE");
        pattern = pattern.replace("ddd", "EEE");
        pattern = pattern.replace("hh", "HH");
        pattern = pattern.replace("h", "H");
        pattern = pattern.replace("HHampm", "hha");
        pattern = pattern.replace("Hampm", "ha");
        pattern = pattern.replace("nn", "mm");
        pattern = pattern.replace("n", "m");
        // 's' and 'ss' is fine
        pattern = pattern.replace("zzz", "SSS");
        pattern = pattern.replace("zz", "SS");
        pattern = pattern.replace("z", "S");

        pattern = pattern.replace("xx", "hh");
        pattern = pattern.replace("x", "h");

        if (desc instanceof AGDateDataDesc) {
            AGDateDataDesc dateDataDesc = (AGDateDataDesc) desc;
            dateDataDesc.setFormat(pattern);
            dateDataDesc.setLowerCaseAMPMmarker(isLowerCaseAMPMmarker);
        } else if (desc instanceof AGDatePickerDataDesc) {
            AGDatePickerDataDesc dateDataDesc = (AGDatePickerDataDesc) desc;
            dateDataDesc.setFormat(pattern);
        }
    }

    @Override
    protected boolean parseNodeValue(AbstractAGViewDataDesc descriptor) {
        if (((AGDateDataDesc) descriptor).getDataSource() == DateSourceType.INTERNET) {
            ((AGDateDataDesc) descriptor).getTextDescriptor().setText(new StringVariableDataDesc(""));
        } else {
            String dateString = AGXmlParserHelper.loadXmlNodeValue();
            ((AGDateDataDesc) descriptor).setDateVariable(AGXmlActionParser.createVariable(dateString, descriptor));
        }

        return true;
    }
}
