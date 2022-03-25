package com.kinetise.data.application.actionmanager.functioncommands;

import android.support.annotation.Nullable;
import android.view.View;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.feedmanager.FeedManager;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.systemdisplay.helpers.AGViewHelper;
import com.kinetise.helpers.ViewFinder;

import java.security.InvalidParameterException;

public class FunctionShowMore extends AbstractFunction {

    public FunctionShowMore(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc,application);
    }
    /**
     * Increment number of currently visible items in feed - forces {@link com.kinetise.data.application.feedmanager.DownloadFeedCommand} to download more info for given Feed
     * @param desc Descriptor on which action should be called
     * @return null
     * */
    @Override
    public Object execute(Object desc) {
        super.execute(desc);

        return showMore(desc);
    }

    @Nullable
    private Object showMore(Object desc) {
        mApplication.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setAlphaOnClickedView();
            }
        });


        if (desc == null) {
            throw new InvalidParameterException(
                    "LoadMore function need to execute on descriptor! The descriptor shouldn't be null!");
        }

        if (!(desc instanceof IFeedClient)) {
            throw new InvalidParameterException("Cannot execute FunctionLoadMore if descriptor does not implement " +
                    "IFeedClient interface");
        }


        IFeedClient feedClient = (IFeedClient) desc;
        FeedManager.showMoreItems(feedClient, false);
        return null;
    }

    private void setAlphaOnClickedView() {
        AbstractAGElementDataDesc contextDataDesc = mFunctionDataDesc.getActionDescriptor().getContextDataDesc();
        View view = ViewFinder.getViewByDescriptor(contextDataDesc);
        if(view != null)
            AGViewHelper.setHalftransparentIncludingChildren(view);
    }

}