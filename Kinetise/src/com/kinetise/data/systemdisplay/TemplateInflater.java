package com.kinetise.data.systemdisplay;

import android.view.View;

import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGTemplateDataDesc;
import com.kinetise.data.systemdisplay.views.ViewFactoryManager;

import java.util.List;

public class TemplateInflater {
    public static View inflateTemplate(AbstractAGTemplateDataDesc templateDataDesc, SystemDisplay display){
        if(templateDataDesc==null)
            return null;
        List<AbstractAGElementDataDesc> templateControlls = templateDataDesc.getAllControls();
        if(templateControlls.size()==0 || templateControlls.get(0) == null)
            return null;
        return ViewFactoryManager.createViewHierarchy(templateControlls.get(0).copy(), display);
    }
}
