package com.kinetise.data.descriptors.actions;

import com.kinetise.data.application.actionmanager.JSEvaluator;
import com.kinetise.data.application.actionmanager.JSEvaluatorFactory;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;

public class JavaScriptVariableDataDesc extends VariableDataDesc {
    public String javaScript;
    public AbstractAGElementDataDesc context;

    public JavaScriptVariableDataDesc(String code, AbstractAGElementDataDesc context) {
        this.javaScript = code;
        this.context = context;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }

    @Override
    public void resolveVariable() {
        JSEvaluator evaluator =  JSEvaluatorFactory.getInstance().getEvaluator();
        evaluator.setContextControl(context);
        Object resolvedValue = evaluator.evaluate(javaScript);
        evaluator.close();
        setResolvedValue(resolvedValue);
    }

    @Override
    public VariableDataDesc copy(AbstractAGElementDataDesc copyDesc) {
        return new JavaScriptVariableDataDesc(javaScript, copyDesc);
    }

}
