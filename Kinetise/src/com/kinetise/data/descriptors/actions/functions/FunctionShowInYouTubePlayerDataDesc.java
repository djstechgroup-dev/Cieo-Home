package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionShowInYoutubePlayer;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionShowInYouTubePlayerDataDesc extends
		AbstractFunctionDataDesc<FunctionShowInYoutubePlayer> {

    public FunctionShowInYouTubePlayerDataDesc(ActionDataDesc actionDataDesc) {
        super(actionDataDesc);
    }

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionShowInYouTubePlayerDataDesc(copyDesc);
	}

	@Override
	public FunctionShowInYoutubePlayer getFunction() {
		return new FunctionShowInYoutubePlayer(this, AGApplicationState.getInstance());
	}


}
