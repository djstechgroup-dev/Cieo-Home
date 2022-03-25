package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGDataFeedDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGDataFeedViewDataDesc;
import com.kinetise.data.descriptors.datadescriptors.components.ImageDescriptor;

import java.util.HashMap;

public class FunctionGetHeaderParamValue extends AbstractFunction {

	public FunctionGetHeaderParamValue(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
		super(functionDesc,application);
	}
    /**
     * Traverse descriptors hierarchy and descriptor with given id
     * @param desc Descriptor on which action should be called
     * @return found descriptor
     * */
	@Override
	public Object execute(Object desc) {
        super.execute(desc);

        VariableDataDesc attr = mFunctionDataDesc.getAttributes()[0];
        String value = attr.getStringValue();

        String headerValue = null;

        HttpParamsDataDesc headerParams = null;
        if (desc instanceof AbstractAGDataFeedDataDesc)
            headerParams = ((AbstractAGDataFeedDataDesc)desc).getHeaders();
        else if (desc instanceof AbstractAGDataFeedViewDataDesc)
            headerParams = ((AbstractAGDataFeedViewDataDesc)desc).getHeaders();
        else if (desc instanceof ImageDescriptor)
            headerParams = ((ImageDescriptor)desc).getHeaders();
        if (headerParams!=null) {
            HashMap<String, String> headers = headerParams.getHttpParamsAsHashMap();
            if (headers.containsKey(value))
                headerValue = headers.get(value);
        }

        if (headerValue==null)
            headerValue = "";

        return headerValue;
	}

}
