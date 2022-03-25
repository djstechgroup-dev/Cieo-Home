package com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.ActionVariableDataDesc;
import com.kinetise.data.descriptors.actions.JavaScriptVariableDataDesc;
import com.kinetise.data.descriptors.actions.MultiActionDataDesc;
import com.kinetise.data.descriptors.actions.NullVariableDataDesc;
import com.kinetise.data.descriptors.actions.StringVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.XmlAttributeValues;
import com.kinetise.helpers.ActionStringTokenizer;

import java.util.ArrayList;

import static com.kinetise.data.parsermanager.xmlparser.attributes.XmlAttributeValues._NONE;

public class AGXmlActionParser {
    public static MultiActionDataDesc createMultiAction(String actionString, AbstractAGElementDataDesc multiActionContextDataDesc) {
        MultiActionDataDesc actionDesc = null;

        if (actionString != null) {
            actionString = removePrefixAndSuffix(actionString, XmlAttributeValues.DYNAMIC_PREFIX, XmlAttributeValues.DYNAMIC_SUFFIX);
            actionDesc = parseMultiAction(actionString, multiActionContextDataDesc);
        }

        return actionDesc;
    }

    public static VariableDataDesc createVariable(String multiActionString, AbstractAGElementDataDesc desc) {
        VariableDataDesc variable;

        if (multiActionString == null || multiActionString.equals(_NONE)) {
            variable = new NullVariableDataDesc();
        } else if (isVariableAMultiacion(multiActionString)) {
            variable = createVariableWithMultiAction(multiActionString, desc);
        } else if (isVariableJavaScript(multiActionString)) {
            variable = createVariableWithJavaScript(multiActionString, desc);
        } else {
            variable = createVariableWithStringValue(multiActionString);
        }

        return variable;
    }

    public static ActionVariableDataDesc getVariableForEscapedActionString(String actionString) {
        actionString = unescape(actionString);
        return createVariableWithMultiAction(actionString, null);
    }

    private static boolean isVariableAMultiacion(String multiActionString) {
        return multiActionString != null
                && multiActionString.startsWith(XmlAttributeValues.DYNAMIC_PREFIX)
                && multiActionString.endsWith(XmlAttributeValues.DYNAMIC_SUFFIX);
    }

    private static boolean isVariableJavaScript(String JavaScriptString) {
        return JavaScriptString != null
                && JavaScriptString.startsWith(XmlAttributeValues.JAVASCRIPT_PREFIX)
                && JavaScriptString.endsWith(XmlAttributeValues.JAVASCRIPT_SUFFIX);
    }

    public static VariableDataDesc createVariableAttribute(String multiActionString, AbstractAGElementDataDesc desc) {
        if (isVariableAString(multiActionString)) {
            return createVariableWithStringValue(multiActionString);
        } else {
            return createVariableWithMultiAction(multiActionString, desc);
        }
    }

    private static boolean isVariableAString(String variableString) {
        return variableString.startsWith("'") && variableString.endsWith("'");
    }

    public static VariableDataDesc createVariableWithStringValue(String stringValue) {
        stringValue = removeApostrophesIfNecessary(stringValue);
        stringValue = unescape(stringValue);
        return new StringVariableDataDesc(stringValue);
    }

    private static JavaScriptVariableDataDesc createVariableWithJavaScript(String javaScriptString, AbstractAGElementDataDesc desc) {
        javaScriptString = removePrefixAndSuffix(javaScriptString, XmlAttributeValues.JAVASCRIPT_PREFIX, XmlAttributeValues.JAVASCRIPT_SUFFIX);
        return new JavaScriptVariableDataDesc(javaScriptString, desc);
    }

    private static ActionVariableDataDesc createVariableWithMultiAction(String multiActionString, AbstractAGElementDataDesc desc) {
        multiActionString = removePrefixAndSuffix(multiActionString, XmlAttributeValues.DYNAMIC_PREFIX, XmlAttributeValues.DYNAMIC_SUFFIX);
        return parseActionVariable(multiActionString, desc);
    }

    private static String removeApostrophesIfNecessary(String actionString) {
        if (actionString != null && actionString.startsWith("\'") && actionString.endsWith("\'"))
            actionString = actionString.substring(1, actionString.length() - 1);
        return actionString;
    }

    private static ActionVariableDataDesc parseActionVariable(String actionString, AbstractAGElementDataDesc desc) {
        MultiActionDataDesc multiAction = createMultiAction(actionString, desc);
        return new ActionVariableDataDesc(multiAction);
    }

    private static String removePrefixAndSuffix(String actionString, String prefix, String sufix) {
        if (actionString.startsWith(prefix)) {
            actionString = actionString.substring(prefix.length(), actionString.length());
        }
        if (actionString.endsWith(sufix)) {
            actionString = actionString.substring(0, actionString.length() - sufix.length());
        }
        return actionString;
    }

    private static MultiActionDataDesc parseMultiAction(String multiActionsDataDesc, AbstractAGElementDataDesc contextDataDesc) {
        MultiActionDataDesc result = new MultiActionDataDesc(contextDataDesc);

        ArrayList<String> actionStrings = ActionStringTokenizer.tokenize(multiActionsDataDesc, ';');

        for (String actionString : actionStrings) {
            ActionDataDesc actionDataDesc = parseAction(actionString, result);
            result.addAction(actionDataDesc);
        }

        return result;
    }

    private static ActionDataDesc parseAction(String actionString, MultiActionDataDesc context) {
        ActionDataDesc actionDescResult = new ActionDataDesc(context);

        actionString = actionString.trim();

        ArrayList<String> functionStrings = ActionStringTokenizer.tokenize(actionString, '.');

        for (String functionString : functionStrings) {

            AbstractFunctionDataDesc funcDesc = AGXmlFunctionDescFactory.getFunctionDescriptor(functionString, actionDescResult);

            AbstractAGXmlParserFunction.parseFunctionAndAddToAction(functionString, funcDesc, actionDescResult);
        }

        return actionDescResult;
    }

    public static String unescape(String string) {
        if (string != null) {
            int i = 0;
            while (i < string.length()) {
                if (string.charAt(i) == '\\')
                    string = string.substring(0, i) + string.substring(i + 1);
                ++i;

            }
        }
        return string;
    }

}