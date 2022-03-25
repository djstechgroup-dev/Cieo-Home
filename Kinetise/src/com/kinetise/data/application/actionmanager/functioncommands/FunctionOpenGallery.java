package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionOpenGallery extends AbstractFunction {

    public FunctionOpenGallery(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    /**
     * Starts {@link com.kinetise.data.application.externalapplications.OpenGalleryApp} to choose/take photo.
     *
     * @param desc Descriptor on which action should be called
     * @return null
     */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        ActionManager.getInstance().openGallery();
        return null;
    }


}
