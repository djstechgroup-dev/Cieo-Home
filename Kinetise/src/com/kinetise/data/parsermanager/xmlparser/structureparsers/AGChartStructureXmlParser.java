package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kinetise.data.descriptors.AGChartDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.DataSetDescriptor;
import com.kinetise.data.descriptors.types.AGChartType;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGGraphXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;

import java.util.ArrayList;
import java.util.List;

public class AGChartStructureXmlParser extends AbstractAGDataFeedViewStructureXmlParser {
    @Override
    protected AbstractAGViewDataDesc createDescriptor(String id) {
        return new AGChartDataDesc(id);
    }

    @Override
    protected String getStructureRootNodeName() {
        return AGXmlNodes.CONTROL_CHART;
    }

    public AGChartType parserChartType(String value){
        switch (value){
            case "line":
                return AGChartType.LINE;
            case "pie":
                return AGChartType.PIE;
            case "bar":
                return AGChartType.BAR;
            case "horizontalbar":
                return AGChartType.HORIZONTAL_BAR;
            default:
                return AGChartType.LINE;
        }
    }

    @Override
    protected boolean parseNodeAttribute(AbstractAGViewDataDesc desc, String id, String value) {
        AGChartDataDesc descriptor = (AGChartDataDesc) desc;

        if (super.parseNodeAttribute(descriptor, id, value)) {
            return true;
        } else if (id.equals(AGGraphXmlAttributes.typeNodeName)) {
            descriptor.setChartType(parserChartType(value));
            return true;
        } else if (id.equals(AGGraphXmlAttributes.labelsNodeName)) {
            descriptor.setLabelsItemPath(value);
            return true;
        }else if (id.equals(AGGraphXmlAttributes.dataSetsNodeName)) {
            parseDataSetsJson(descriptor,value);
            return true;
        }else if (id.equals(AGGraphXmlAttributes.colorNodeName)) {
            parseColorsJson(descriptor,value);
            return true;
        }
        return false;
    }

    private void parseDataSetsJson(AGChartDataDesc descriptor, String value) {
        try{
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(value).getAsJsonArray();
            DataSetDescriptor dataSetDescriptor;
            for(JsonElement element:array){
                try {
                    JsonObject dataSetObject = element.getAsJsonObject();
                    dataSetDescriptor = new DataSetDescriptor();
                    dataSetDescriptor.setName(dataSetObject.get("name").getAsString());
                    dataSetDescriptor.setDataPath(dataSetObject.get("value").getAsString());
                    descriptor.addDataSetDescriptor(dataSetDescriptor);
                }catch(Exception e){

                }
                }
        } catch (Exception e1){

        }
    }

    private void parseColorsJson(AGChartDataDesc descriptor, String value){
        List<Integer> colors = new ArrayList<>();
        try{
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(value).getAsJsonArray();
            for(JsonElement element:array){
                try {
                    colors.add(AGXmlParserHelper.getColorFromHex(element.getAsString()));
                }catch(Exception e){

                }
            }
        } catch (Exception e1){

        }
        descriptor.setColors(colors);
    }
}
