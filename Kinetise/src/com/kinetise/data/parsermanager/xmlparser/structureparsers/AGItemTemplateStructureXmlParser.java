package com.kinetise.data.parsermanager.xmlparser.structureparsers;

import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.ActionVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.actions.functions.FunctionGoToScreenWithContextDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGTemplateDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.AGItemTemplateDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.RequiredField;
import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.parsermanager.xmlparser.StructureXmlParsersFactory;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGItemTemplateXmlAttributes;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;
import com.kinetise.data.parsermanager.xmlparser.nodes.AGXmlNodes;
import com.kinetise.helpers.unescapeUtils.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AGItemTemplateStructureXmlParser extends
        AbstractAGTemplateStructureParser {

	private final static String NODE_NAME = AGXmlNodes.ITEM_TEMPLATE;

    @Override
    protected boolean parseNodeAttribute(AbstractAGTemplateDataDesc descriptor,String id,String value) {
        AGItemTemplateDataDesc desc = (AGItemTemplateDataDesc) descriptor;

        List<RequiredField> requiredFieldList = new ArrayList<>();
        RequiredField rf;

        if (id.equals(AGItemTemplateXmlAttributes.REQUIRED_FIELDS)) {

            String fields = value;
            if (fields.equals(""))
                desc.setRequiredFields(null);
            else {
                JSONArray paramsInJson;
                String valueX;
                try {
                    Pattern patter = Pattern.compile(Pattern.quote("\\["));
                    Matcher matcher = patter.matcher(fields);
                    valueX = matcher.replaceAll("[");
                    Pattern second = Pattern.compile(Pattern.quote("\\]"));
                    Matcher matcher1 = second.matcher(valueX);
                    valueX = matcher1.replaceAll("]");
                    fields = valueX;
                } catch (Exception e) {

                }
                String jsonString;
                jsonString = StringEscapeUtils.unescapeHtml(fields);

                try {
                    paramsInJson = new JSONArray(jsonString);
                } catch (JSONException e) {
                    e.printStackTrace();
                    descriptor.setRequiredFields(null);
                    return true;
                }
                for (int i = 0; i < paramsInJson.length(); i++) {
                    rf = new RequiredField();
                    try {
                        org.json.JSONObject object = paramsInJson.getJSONObject(i);
                        rf.setName(object.get("field").toString());
                        Object match = object.get("match");
                        if (match.equals(JSONObject.NULL))
                            rf.setMatch(DataFeedItem.NULL);
                        else
                            rf.setMatch(match);
                        rf.setRegexName(object.get("regexname").toString());
                        rf.setAllowEmpty(AGXmlParserHelper.convertYesNoToBoolean(object.get("allowempty").toString()));
                        requiredFieldList.add(rf);

                    } catch (JSONException e) {
                        ExceptionManager.getInstance().handleException(e, false);
                    }
                }
                descriptor.setRequiredFields(requiredFieldList);
            }

            return true;
        }

        return false;
    }

    @Override
    protected void proceedParseStructure(String nodeName,
                                         AbstractAGTemplateDataDesc desc) {
        if (nodeName.contains(AGXmlNodes.CONTROL) || nodeName.contains(AGXmlNodes.CONTAINER)) {

            AbstractAGViewDataDesc viewDataDesc = (AbstractAGViewDataDesc) StructureXmlParsersFactory.getStructureParser(nodeName).parseStructure();
            desc.addControl(viewDataDesc);

            VariableDataDesc multiActionDataDesc = viewDataDesc.getOnClickActionDesc();

            if (multiActionDataDesc != null && multiActionDataDesc instanceof ActionVariableDataDesc && ((ActionVariableDataDesc) multiActionDataDesc).getActions().getActions().length == 1) {
                ActionDataDesc firstAction = ((ActionVariableDataDesc) multiActionDataDesc).getActions().getActions()[0];

                if (firstAction != null && firstAction.getFunctions().length == 1) {
                    AbstractFunctionDataDesc firstFunction = firstAction.getFunctions()[0];

                    if (((AGItemTemplateDataDesc) desc).getDetailScreenId() == null && firstFunction instanceof FunctionGoToScreenWithContextDataDesc) {
                        ((AGItemTemplateDataDesc) desc).setDetailScreenId(firstFunction.getAttributes()[0].getStringValue());
                    }
                }


            }



        } else {
            throw new InvalidParameterException(String.format(
                    "Unexpected node '%s' in '%s' strucutre", nodeName,
                    getStructureRootNodeName()));
        }
    }

	@Override
	protected String getStructureRootNodeName() {
		return NODE_NAME;
	}

    @Override
    protected boolean parseNodeValue(AbstractAGTemplateDataDesc desc) {
        return true;
    }

    @Override
	protected AGItemTemplateDataDesc createDescriptor(String id) {
		return new AGItemTemplateDataDesc();
	}
}
