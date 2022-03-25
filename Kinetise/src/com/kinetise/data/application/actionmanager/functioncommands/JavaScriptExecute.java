package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.actions.functions.JavaScriptExecuteDesc;
import com.squareup.duktape.Duktape;

public class JavaScriptExecute extends AbstractFunction {

    public JavaScriptExecute(JavaScriptExecuteDesc calculateGeoDistanceDesc, AGApplicationState instance) {
        super(calculateGeoDistanceDesc, instance);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String javaScriptCode = mFunctionDataDesc.getAttributes()[0].getStringValue();
        return  evaluateToStringJavaScriptCode(javaScriptCode);
    }

    private static Object evaluateToStringJavaScriptCode(String javaScriptCode) {
        Duktape duktape = Duktape.create();
        return duktape.evaluate(javaScriptCode);
    }

}
