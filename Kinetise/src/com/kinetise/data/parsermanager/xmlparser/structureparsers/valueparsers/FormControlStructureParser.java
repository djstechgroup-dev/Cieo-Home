package com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers;

import com.google.gson.Gson;
import com.kinetise.data.application.formdatautils.FormValidation;
import com.kinetise.data.application.formdatautils.FormValidationRule;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.IFormControlDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.parsermanager.xmlparser.attributes.AGFormAttributes;

import java.util.ArrayList;

public class FormControlStructureParser {
    public static boolean parseNodeAttribute(IFormControlDesc descriptor, String id, String value) {
        if (id.equals(AGFormAttributes.FORM_ID)) {
            VariableDataDesc variable = AGXmlActionParser.createVariable(value, (AbstractAGViewDataDesc) descriptor);
            descriptor.getFormDescriptor().setFormId(variable);
            return true;
        } else if (id.equals(AGFormAttributes.INIT_VALUE)) {
            descriptor.getFormDescriptor().setInitValue(AGXmlActionParser.createVariable(value, (AbstractAGViewDataDesc) descriptor));
            return true;
        } else if (id.equals(AGFormAttributes.VALIDATION_RULES)) {
            Gson gson = new Gson();
            FormValidation rule = gson.fromJson(value, FormValidation.class);
            if (rule == null) {
                rule = new  FormValidation();
                rule.setRules(new ArrayList<FormValidationRule>());
            }
            descriptor.getFormDescriptor().setFormValidationRule(rule);
            return true;
        } else {
            return InvalidFormControlStructureParser.parseNodeAttribute(descriptor, id, value);
        }
    }
}
