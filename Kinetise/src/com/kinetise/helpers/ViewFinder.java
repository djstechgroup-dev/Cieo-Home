package com.kinetise.helpers;

import android.view.View;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.systemdisplay.viewvisitors.FindViewByDescriptorVisitor;

public class ViewFinder {
    public static View getViewByDescriptor(AbstractAGElementDataDesc functionDataDesc) {
        FindViewByDescriptorVisitor visitor = new FindViewByDescriptorVisitor(functionDataDesc);
        AGApplicationState.getInstance().getSystemDisplay().getScreenView().accept(visitor);
        return visitor.getFoundView();
    }
}
