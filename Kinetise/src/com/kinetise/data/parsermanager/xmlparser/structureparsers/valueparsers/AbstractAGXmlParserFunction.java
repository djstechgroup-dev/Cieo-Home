package com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers;

import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.helpers.ActionStringTokenizer;

import java.util.ArrayList;

public class AbstractAGXmlParserFunction {

    public static AbstractFunctionDataDesc parseFunctionAndAddToAction(String functionString, AbstractFunctionDataDesc functionDataDesc, ActionDataDesc actionDataDesc) {

        String attributesString = getAttributesString(functionString);

        ArrayList<String> functions = ActionStringTokenizer.tokenize(attributesString,',');

        for(String function : functions){
            VariableDataDesc attributeDataDesc = AGXmlActionParser.createVariableAttribute(function, actionDataDesc.getContextDataDesc());
            functionDataDesc.addAttribute(attributeDataDesc);
        }

        return functionDataDesc;
    }

    private static String getAttributesString(String functionString) {
        return functionString.substring(functionString.indexOf('(')+1,functionString.lastIndexOf(')'));
    }

}
