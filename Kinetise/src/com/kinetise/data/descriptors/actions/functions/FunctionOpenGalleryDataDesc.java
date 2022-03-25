package com.kinetise.data.descriptors.actions.functions;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionOpenGallery;
import com.kinetise.data.descriptors.actions.ActionDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionOpenGalleryDataDesc extends AbstractFunctionDataDesc<FunctionOpenGallery> {

	public FunctionOpenGalleryDataDesc(ActionDataDesc actionDataDesc) {
		super(actionDataDesc);
	}

	@Override
	public AbstractFunctionDataDesc copyInstance(
            ActionDataDesc copyDesc) {
		return new FunctionOpenGalleryDataDesc(copyDesc);
	}

	@Override
	public FunctionOpenGallery getFunction() {
		return new FunctionOpenGallery(this, AGApplicationState.getInstance());
	}
}
