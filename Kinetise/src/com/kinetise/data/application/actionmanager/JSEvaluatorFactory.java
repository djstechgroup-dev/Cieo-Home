package com.kinetise.data.application.actionmanager;


import android.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class JSEvaluatorFactory {
    private static JSEvaluatorFactory mInstance;
    private Pair<Class, Object> mCustomHandler;

    public static JSEvaluatorFactory getInstance() {
        if (mInstance == null) {
            mInstance = new JSEvaluatorFactory();
        }
        return mInstance;
    }

    public void setCustomHandler(Class customInterface, Object customObject) {
        if (!customInterface.isInterface())
            throw new IllegalArgumentException("not an interface");

        validateInterface(customInterface, customObject);
        mCustomHandler =  new Pair<>(customInterface, customObject);
    }


    private void validateInterface(Class customInterface, Object customObject) {
        int i = 0;
        boolean interfaceCheck = false;
        while (i < customObject.getClass().getInterfaces().length) {
            if (customObject.getClass().getInterfaces()[i].equals(customInterface)) {
                interfaceCheck = true;
                break;
            }
            i++;
        }

        if (!interfaceCheck) {
            throw new IllegalArgumentException("custom object must implement custom interface");
        }
    }

    public JSEvaluator getEvaluator() {
        JSEvaluator evaluator = new JSEvaluator();
        evaluator.setCustomInterface(mCustomHandler.first, mCustomHandler.second);
        return evaluator;
    }

}
