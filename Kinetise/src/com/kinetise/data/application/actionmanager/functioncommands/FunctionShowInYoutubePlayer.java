package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

import java.security.InvalidParameterException;

public class FunctionShowInYoutubePlayer extends AbstractFunction {

    public FunctionShowInYoutubePlayer(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    /**
     * Starts new YouTubePlayerApp {@link com.kinetise.data.application.externalapplications.YouTubePlayerApp}
     * to watch movie.
     *
     * @param desc Unneeded param
     * @return null
     */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        if (desc != null) {
            throw new InvalidParameterException(
                    "FunctionShowInYoutubePlayer function does not execute on descriptor! The descriptor should be null!");
        }
        VariableDataDesc attribute = mFunctionDataDesc.getAttributes()[0];
        ActionManager.getInstance().showInYoutubePlayer(attribute.getStringValue());
        return null;
    }


}
