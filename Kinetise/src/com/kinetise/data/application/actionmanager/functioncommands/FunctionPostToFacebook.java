package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

public class FunctionPostToFacebook extends AbstractFunction {

	public FunctionPostToFacebook(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
		super(functionDesc,application);
	}
    /**
     * Start new facebook apllication.
     * @see {@link com.kinetise.data.application.externalapplications.PostToFacebookApp}
     * @param desc Descriptor on which action should be called
     * @return null
     * */
	@Override
	public Object execute(Object desc) {
		super.execute(desc);

		VariableDataDesc[] attributes  = mFunctionDataDesc.getAttributes();
		final String appId = attributes[0].getStringValue();
		final String appName = attributes[1].getStringValue();
        final String caption = attributes[2].getStringValue();
		final String link = attributes[3].getStringValue();
		final String picture = attributes[4].getStringValue();
		final String description = attributes[5].getStringValue();

		ActionManager.getInstance().postToFacebook(appName,caption, link, picture, description);
		return null;
	}
}
