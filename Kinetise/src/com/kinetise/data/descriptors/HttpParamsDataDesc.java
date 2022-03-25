package com.kinetise.data.descriptors;

import com.kinetise.data.descriptors.actions.StringVariableDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.helpers.unescapeUtils.StringEscapeUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HttpParamsDataDesc implements Serializable {
    private List<HttpParamsElementDataDesc> mHttpParamsElementDataDescs = new ArrayList<HttpParamsElementDataDesc>();

    public static HttpParamsDataDesc getHttpParams(String pValue, AbstractAGElementDataDesc pElementDataDesc) {
        HttpParamsDataDesc paramsDataDesc = new HttpParamsDataDesc();
        //TODO: Exact same code can be found in AGItemTemplateStructureXmlParser, exctract to methode
        JSONArray paramsInJson;
        String valueX;
        try {
            Pattern patter = Pattern.compile(Pattern.quote("\\["));
            Matcher matcher = patter.matcher(pValue);
            valueX = matcher.replaceAll("[");
            Pattern second = Pattern.compile(Pattern.quote("\\]"));
            Matcher matcher1 = second.matcher(valueX);
            valueX = matcher1.replaceAll("]");
            pValue = valueX;
        } catch (Exception e) {

        }
        String jsonString;
        jsonString = StringEscapeUtils.unescapeHtml(pValue);

        try {
            paramsInJson = new JSONArray(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
            return paramsDataDesc;
        }
        for (int i = 0; i < paramsInJson.length(); i++) {
            try {
                JSONObject object = paramsInJson.getJSONObject(i);
                String paramName = object.getString("paramName");
                String value = object.getString("paramValue");
                VariableDataDesc paramValue = AGXmlActionParser.createVariable(value, pElementDataDesc);
                paramsDataDesc.addHttpParam(new HttpParamsElementDataDesc(paramName, paramValue));
            } catch (JSONException e) {
                ExceptionManager.getInstance().handleException(e, false);
            }
        }
        return paramsDataDesc;
    }

    public static HttpParamsDataDesc getHttpParams(Map<String, String> params, AbstractAGElementDataDesc pElementDataDesc) {
        HttpParamsDataDesc paramsDataDesc = new HttpParamsDataDesc();

        Map.Entry<String, String> entry;
        Iterator<Map.Entry<String, String>> iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            entry = iter.next();
            String paramName = entry.getKey();
            String value = entry.getValue();
            VariableDataDesc paramValue = AGXmlActionParser.createVariable(value, pElementDataDesc);
            paramsDataDesc.addHttpParam(new HttpParamsElementDataDesc(paramName, paramValue));
        }

        return paramsDataDesc;
    }

    public HttpParamsDataDesc copy(AbstractAGElementDataDesc pElementDataDesc) {
        HttpParamsDataDesc dataDesc = new HttpParamsDataDesc();
        for (HttpParamsElementDataDesc elementDataDesc : mHttpParamsElementDataDescs) {
            dataDesc.addHttpParam(elementDataDesc.copy(pElementDataDesc));
        }
        return dataDesc;
    }

    public HttpParamsDataDesc copy() {
        resolveVariables();
        HttpParamsDataDesc copied = new HttpParamsDataDesc();
        Map<String, String> httpParamsMap = getHttpParamsAsHashMap();
        Map.Entry<String, String> entry;
        Iterator<Map.Entry<String, String>> iterator = httpParamsMap.entrySet().iterator();
        while (iterator.hasNext()) {
            entry = iterator.next();
            copied.addHttpParam(entry.getKey(), entry.getValue());
        }
        return copied;
    }

    public void resolveVariables() {
        for (HttpParamsElementDataDesc httpParamElementDataDesc : mHttpParamsElementDataDescs) {
            httpParamElementDataDesc.resolveVariables();
        }
    }

    public List<HttpParamsElementDataDesc> getHttpParamsElementDataDescs() {
        return mHttpParamsElementDataDescs;
    }

    public void addHttpParam(HttpParamsElementDataDesc pHttpParamsElementDataDesc) {
        mHttpParamsElementDataDescs.add(pHttpParamsElementDataDesc);
    }

    public void addHttpParam(String name, String value) {
        VariableDataDesc stringVariable = new StringVariableDataDesc(value);

        HttpParamsElementDataDesc paramElement = new HttpParamsElementDataDesc(name, stringVariable);
        mHttpParamsElementDataDescs.add(paramElement);
    }

    public HashMap<String, String> getHttpParamsAsHashMap() {
        HashMap<String, String> headersMap = new HashMap<String, String>();

        for (HttpParamsElementDataDesc header : getHttpParamsElementDataDescs()) {
            header.resolveVariables();
            VariableDataDesc paramValue = header.getParamValue();
            paramValue.resolveVariable();
            headersMap.put(header.getParamName(), paramValue.getStringValue());
        }

        return headersMap;
    }

    @Override
    public boolean equals(Object o) {
        HttpParamsDataDesc compared = (HttpParamsDataDesc) o;

        for (HttpParamsElementDataDesc element : mHttpParamsElementDataDescs) {
            if (!compared.getHttpParamsElementDataDescs().contains(element))
                return false;
        }

        return true;
    }
}
