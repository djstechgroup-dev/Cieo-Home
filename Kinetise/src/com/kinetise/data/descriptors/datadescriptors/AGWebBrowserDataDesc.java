package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;

public class AGWebBrowserDataDesc extends AbstractAGViewDataDesc {

    private VariableDataDesc mSource;
    private boolean mGoToExternalBrowser;

    public AGWebBrowserDataDesc(String id) {
        super(id);
    }

    @Override
    public int getDepthCount() {
        return super.getDepthCount() + 1;
    }

    public VariableDataDesc getSource() {
        return mSource;
    }

    public void setSource(VariableDataDesc source) {
        mSource = source;
    }

    public boolean isGoToExternalBrowser() {
        return mGoToExternalBrowser;
    }

    public void setGoToExternalBrowser(boolean goToExternalBrowser) {
        this.mGoToExternalBrowser = goToExternalBrowser;
    }

    @Override
    public AGWebBrowserDataDesc createInstance() {
        return new AGWebBrowserDataDesc(getId());
    }

    @Override
    public AGWebBrowserDataDesc copy() {
        AGWebBrowserDataDesc copied = (AGWebBrowserDataDesc) super.copy();
        if (this.mSource != null) {
            copied.mSource = this.mSource.copy(copied);
            copied.mGoToExternalBrowser = this.mGoToExternalBrowser;
        }
        return copied;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mSource.resolveVariable();
    }
}
