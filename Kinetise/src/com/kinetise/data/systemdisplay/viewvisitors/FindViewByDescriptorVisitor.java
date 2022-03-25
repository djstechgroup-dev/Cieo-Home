package com.kinetise.data.systemdisplay.viewvisitors;

import android.view.View;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.systemdisplay.views.IAGView;

public class FindViewByDescriptorVisitor implements IViewVisitor {

	AbstractAGElementDataDesc mDescriptor;
	IAGView mFoundView;
	
	public FindViewByDescriptorVisitor(AbstractAGElementDataDesc elementDataDesc) {
		mDescriptor = elementDataDesc;
	}

	@Override
	public boolean visit(IAGView screenView) {
		if(!screenView.getDescriptor().equals(mDescriptor)){
			return false;
        }

        mFoundView = screenView;
		return true;
	}

	public View getFoundView() {
		return (View)mFoundView;
	}


}
