package com.kinetise.data.application.actionmanager.functioncommands;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.actions.functions.FunctionShowInVideoPlayerDataDesc;
import com.kinetise.helpers.unescapeUtils.StringEscapeUtils;

public class FunctionShowInVideoPlayer extends AbstractFunction {

    public FunctionShowInVideoPlayer(FunctionShowInVideoPlayerDataDesc functionShowInVideoPlayerDataDesc, AGApplicationState instance) {
        super(functionShowInVideoPlayerDataDesc, instance);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        String htmlEscapedvideoUrl = mFunctionDataDesc.getAttributes()[0].getStringValue();
        String videoUrl = StringEscapeUtils.unescapeHtml(htmlEscapedvideoUrl);
        ActionManager.getInstance().showInVideoPlayer(videoUrl);
        return null;
    }

}
