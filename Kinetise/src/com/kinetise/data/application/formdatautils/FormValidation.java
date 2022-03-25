package com.kinetise.data.application.formdatautils;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class FormValidation {

    @SerializedName("dependencies")
    private List<String> dependencies;

    @SerializedName("rules")
    private List<FormValidationRule> rules;

    public FormValidation() {
        dependencies = new ArrayList<>();
        rules = new ArrayList<>();
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = dependencies;
    }

    public List<FormValidationRule> getRules() {
        return rules;
    }

    public void setRules(List<FormValidationRule> rules) {
        this.rules = rules;
    }

    public void addDependency(String dependency) {
        dependencies.add(dependency);
    }

    public void addRule(FormValidationRule rule) {
        rules.add(rule);
    }

    public FormValidation copy() {
        FormValidation copied = new FormValidation();
        List<FormValidationRule> copiedRules = new ArrayList<>();
        for (FormValidationRule rule : rules) {
            copiedRules.add(rule.copy());
        }
        copied.setRules(copiedRules);
        copied.setDependencies(new ArrayList<>(dependencies));
        return copied;
    }

}
