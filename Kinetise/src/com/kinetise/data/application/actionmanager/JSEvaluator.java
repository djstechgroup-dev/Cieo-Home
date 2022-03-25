package com.kinetise.data.application.actionmanager;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.actions.jsapi.Internal;
import com.kinetise.data.descriptors.actions.jsapi.InternalJS;
import com.kinetise.data.descriptors.actions.jsapi.Screen;
import com.kinetise.data.descriptors.actions.jsapi.ScreenJs;
import com.kinetise.data.descriptors.actions.jsapi.System;
import com.kinetise.data.descriptors.actions.jsapi.SystemJS;
import com.kinetise.data.descriptors.actions.jsapi.Variables;
import com.kinetise.data.descriptors.actions.jsapi.VariablesJS;
import com.kinetise.support.logger.Logger;
import com.squareup.duktape.Duktape;

import java.util.ArrayList;

public class JSEvaluator {
    private static final String SYSTEM = "system";
    private static final String SCREEN = "screen";
    private static final String STORAGE = "storage";
    private static final String INTERNAL = "internal";
    private static final String CUSTOM = "custom";
    private static final String FUNCTION_NAME = "variableFunction";
    private Duktape duktape;
    private StringBuilder sb = new StringBuilder();
    private InternalJS internalAPI;

    protected JSEvaluator() {
        duktape = Duktape.create();
        initAPI(duktape);
    }

    public void close() {
        duktape.close();
    }

    public void setContextControl(AbstractAGElementDataDesc contextControl) {
        internalAPI.setContext(contextControl);
    }

    public Object evaluate(String code) {
        return evaluate(code, null, null, null);
    }

    public Object evaluate(String code, String[] functionArguments, String[] callArguments, Object context) {
        String script = sb.toString() + createJSAppObject() + createControlObject() + wrapCode(code, functionArguments, callArguments);
        Logger.d(JSEvaluator.this, script);
        Object result = duktape.evaluate(script);
        return result;
    }

    private void initAPI(Duktape duktape) {
        duktape.set(STORAGE, Variables.class, VariablesJS.getInstance());
        duktape.set(SYSTEM, System.class, SystemJS.getInstance());
        duktape.set(SCREEN, Screen.class, ScreenJs.getInstance());
        internalAPI = new InternalJS();
        duktape.set(INTERNAL, Internal.class, internalAPI);
    }

    private String createJSAppObject() {
        return addBackMethodToScreenObject() +
                addControlMethodToScreenObject() +
                addNumberToHexMethodToInternal() +
                "var App = function App(" + SCREEN + ", " + STORAGE + ", " + SYSTEM + ") {\n" +
                "this." + SCREEN + " = " + SCREEN + ";\n" +
                "this." + STORAGE + " = " + STORAGE + ";\n" +
                "this." + SYSTEM + " = " + SYSTEM + ";\n" +
                "this." + SCREEN + ".control = function(id){\n" +
                "\tvar control = new Control(id);\n" +
                defineGettersAndSetterForControl("control") +
                "return control;\n" +
                "};\n" +
                "};\n" +
                "var app = new App(" + SCREEN + "," + STORAGE + "," + SYSTEM + ");\n";
    }

    private String defineGettersAndSetterForControl(String control) {
        StringBuilder sb = new StringBuilder();
        addProperty(sb,control,"textColor","getTextColor","setTextColor","color",true);
        addProperty(sb,control,"backgroundColor","getBackgroundColor","setBackgroundColor","color",true);
        addProperty(sb,control,"text","getText","setText","color",true);
        return sb.toString();
    }

    public void addProperty(StringBuilder sb, String control, String name, String getter, String setter, String param, boolean useID){
        String firstParamGetter = useID?"this._id":"";
        String firstParamSetter = useID?"this._id, ":"";
        sb.append("\tObject.defineProperty(" + control + ", '"+name+"', {\n");
        sb.append("\t\tget: function() {\n");
        sb.append("\t\t\t return " + INTERNAL + "."+getter+"("+firstParamGetter+");\n");
        sb.append("\t\t},\n");
        sb.append("\t\tset: function("+param+") {\n");
        sb.append("\t\t\t" + INTERNAL + "."+setter+"("+firstParamSetter+param+");\n");
        sb.append("\t\t}");
        sb.append("\t});\n");
    }

    private String defineGettersAndSetterForThis(String control) {
        StringBuilder sb = new StringBuilder();
        addProperty(sb,control,"textColor","getThisTextColor","setThisTextColor","color",false);
        addProperty(sb,control,"backgroundColor","getThisBackgroundColor","setThisBackgroundColor","color",false);
        addProperty(sb,control,"text","getThisText","setThisText","color",false);
        return sb.toString();
    }

    private String addControlMethodToScreenObject() {
        return SCREEN + ".control = function(id){ return new Control(id);}\n";
    }

    private String addNumberToHexMethodToInternal() {
        return INTERNAL + ".numberToHex = function(num){return \"0x\" + num.toString(16);}\n";
    }

    private String addBackMethodToScreenObject() {
        return SCREEN + ".back = function (val){\n" +
                "if( typeof val === \"string\" ) {\n" +
                "this.backById(val);\n" +
                "return;\n" +
                "}\n" +
                "if ( typeof val === \"number\" ){\n" +
                "this.backBySteps(val);\n" +
                "return;\n" +
                "}\n" +
                "this.backBySteps(1)\n" +
                "}\n";
    }

    private String createControlObject() {
        return "var Control = function(id) {\n" +
                "\tthis._id=id;\n" +
                "\tthis.update = function(){\n" +
                "\t\t" + INTERNAL + ".update(_id)\n" +
                "\t};\n" +
                "};\n";
    }

    public String wrapCode(String code, String[] functionArguments, String[] callArguments) {
        StringBuilder sb = new StringBuilder();
        sb.append("var contextControl = new Control(\" \");");
        sb.append(defineGettersAndSetterForThis("contextControl"));
        sb.append("( function " + FUNCTION_NAME + "(");
        appendCommaSeparatedList(sb, functionArguments);
        sb.append(") {\n");
        sb.append(code);
        sb.append("\n}).call(contextControl");

        appendCommaSeparatedList(sb, callArguments, true);
        sb.append(");\n");
        return sb.toString();
    }

    private void appendCommaSeparatedList(StringBuilder sb, String[] arguments) {
        appendCommaSeparatedList(sb, arguments, false);
    }

    private void appendCommaSeparatedList(StringBuilder sb, String[] arguments, boolean addInitialComa) {
        if (arguments != null && arguments.length > 0) {
            if (addInitialComa) sb.append(",");
            for (int i = 0; i < arguments.length; i++) {
                sb.append(arguments[i]);
                if (i != arguments.length - 1) {
                    sb.append(", ");
                }
            }
        }
    }

    public void appendFunctionCall(StringBuilder sb, String functionName, String[] arguments) {
        sb.append(functionName + "(");
        appendCommaSeparatedList(sb, arguments);
        sb.append(");");
    }

    public void appendCode(String code) {
        sb.append(code);
    }

    public void setCustomInterface(Class customInterface, Object objectImplementInterface) {
        duktape.set(CUSTOM, customInterface, objectImplementInterface);
    }
}
