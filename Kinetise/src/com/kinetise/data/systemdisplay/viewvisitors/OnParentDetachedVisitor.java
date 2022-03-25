package com.kinetise.data.systemdisplay.viewvisitors;

import android.view.View;

import com.kinetise.data.systemdisplay.views.IAGView;

public class OnParentDetachedVisitor implements IViewVisitor {

	@Override
	public boolean visit(IAGView view) {
        ((View)view).onStartTemporaryDetach();
		return false;
	}


}
