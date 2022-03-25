package com.kinetise.data.application.actionmanager.functioncommands;

import android.support.annotation.Nullable;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.application.screenloader.ScreenLoader;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.types.AGScreenTransition;
import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.parsermanager.xmlparser.helpers.AGXmlParserHelper;

public class FunctionGoToScreenWithContext extends AbstractFunction {

    public FunctionGoToScreenWithContext(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    /**
     * Redirects user to screen with given id(screen id is param of this function in xml) also setting proper context for it
     * eg. moving to details screen for feed
     *
     * @param desc Descriptor on which action should be called
     * @return null
     */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        if (desc != null) {
            throw new IllegalArgumentException("GoToScreenWithContext function is not execute on descriptor! The descriptor should be null!");
        }

        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();

        if (attributes.length != 3) {
            throw new IllegalArgumentException("GoToScreenWithContext function should have only three parameter!");
        }

        String nextScreenId = attributes[0].getStringValue();
        AbstractAGViewDataDesc context = (AbstractAGViewDataDesc) attributes[1].getValue();
        AGScreenTransition transition = AGXmlParserHelper.getScreenTransition(attributes[2].getStringValue());
        AbstractAGViewDataDesc abstractAGViewDataDesc = (AbstractAGViewDataDesc) mFunctionDataDesc.getContextDataDesc();
        ActionManager.getInstance().goToScreenWithContext(getActionsContextFeedItemIndex(abstractAGViewDataDesc), nextScreenId, context, transition);
        return null;
    }

    private int getActionsContextFeedItemIndex(AbstractAGViewDataDesc controlContextDataDesc) {
        return controlContextDataDesc.getFeedItemIndex();
    }

}
