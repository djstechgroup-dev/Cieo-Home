package com.kinetise.data.descriptors.actions.jsapi;

import com.kinetise.data.VariableStorage;


public class VariablesJS implements Variables {
    private static VariablesJS variablesJS;

    public static VariablesJS getInstance() {
        if (variablesJS == null) {
            variablesJS = new VariablesJS();
        }
        return variablesJS;
    }

    @Override
    public void set(String variableName, Object value) {
        VariableStorage.getInstance().addValue(variableName, value != null ? value.toString() : "");


    }

    @Override
    public String get(String variableName) {
        return VariableStorage.getInstance().getValue(variableName) != null ? VariableStorage.getInstance().getValue(variableName) : "";
    }
}
