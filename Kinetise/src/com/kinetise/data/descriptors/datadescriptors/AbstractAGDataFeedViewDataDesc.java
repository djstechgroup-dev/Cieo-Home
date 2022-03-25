package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.application.feedmanager.DownloadFeedCommand;
import com.kinetise.data.application.feedmanager.FeedManager;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.application.formdatautils.FeedFormData;
import com.kinetise.data.application.formdatautils.FormDataSerializer;
import com.kinetise.data.application.formdatautils.GetFormDataVisitor;
import com.kinetise.data.descriptors.AGBodyDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.HttpParamsDataDesc;
import com.kinetise.data.descriptors.IAGCollectionDataDesc;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.AGItemTemplateDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.ErrorDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.ItemPath;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.LoadMoreDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.LoadingDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Namespaces;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.NoDataDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.Pagination;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.UsingFields;
import com.kinetise.data.descriptors.desctriptorvisitors.IDataDescVisitor;
import com.kinetise.data.descriptors.types.AGFeedCachePolicyType;
import com.kinetise.data.descriptors.types.AGFeedFormatType;
import com.kinetise.data.descriptors.types.AGHttpMethodType;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAGDataFeedViewDataDesc extends AbstractAGViewDataDesc implements IFeedClient {

    protected DataFeed mDataFeed;
    protected int mPageIndex = 0;
    protected ItemPath mItemPath;
    protected String mGUIDNodeName;
    protected String mServiceId;
    protected VariableDataDesc mSource;
    protected List<AGItemTemplateDataDesc> mTemplates;
    protected LoadMoreDataDesc mLoadMoreDataDesc;
    protected NoDataDataDesc mNoDataDataDesc;
    protected LoadingDataDesc mLoadingDataDesc;
    protected ErrorDataDesc mErrorDataDesc;
    protected int mNumberItemsPerPage;
    protected UsingFields mUsingFields;
    protected Namespaces mNamespaces;
    protected int mActiveItemIndex;
    protected HttpParamsDataDesc mLocalDBParamsDataDesc;
    protected HttpParamsDataDesc mHttpParamsDataDesc;
    protected HttpParamsDataDesc mHeaderParamsDataDesc;
    protected HttpParamsDataDesc mBodyParamsDataDesc;
    private AGHttpMethodType mHttpMethod;
    protected AGFeedFormatType mFormat;
    protected boolean mIsLoadingMore;
    protected DownloadFeedCommand mDownloadFeedCommand;
    protected int mLastItemIndex;
    protected int mLastFeedItemCount;
    protected int mScreenId;
    protected ArrayList<AbstractAGElementDataDesc> mFeedControls = new ArrayList<AbstractAGElementDataDesc>();
    private List<AbstractAGElementDataDesc> mTemplateControls = new ArrayList<AbstractAGElementDataDesc>();
    private AGFeedCachePolicyType mCachePolicy;
    private long mCachePolicyAttribute;
    private FeedFormData mFormData;
    private Pagination mPagination;
    private String mResolvedUrl;
    private double mContentHeight;
    private double mContentWidth;
    private String mRequestBodyTrasform;
    private VariableDataDesc mFormId;
    private boolean mShouldRecreate;

    public AbstractAGDataFeedViewDataDesc(String id) {
        super(id);
        mHttpMethod = AGHttpMethodType.GET;
    }

    @Override
    public DataFeed getFeedDescriptor() {
        return mDataFeed;
    }

    @Override
    public void setFeedDescriptor(DataFeed descriptor) {
        mDataFeed = descriptor;
        if (descriptor == null)
            Thread.dumpStack();
    }

    public HttpParamsDataDesc getHttpParams() {
        return mHttpParamsDataDesc;
    }

    public void setHttpParams(HttpParamsDataDesc pHttpParamsDataDesc) {
        mHttpParamsDataDesc = pHttpParamsDataDesc;
    }

    @Override
    public HttpParamsDataDesc getLocalDBParamsDataDesc() {
        return mLocalDBParamsDataDesc;
    }


    @Override
    public HttpParamsDataDesc getHeaders() {
        return mHeaderParamsDataDesc;
    }

    public void setHeaderParams(HttpParamsDataDesc pHeaderParamsDataDesc) {
        mHeaderParamsDataDesc = pHeaderParamsDataDesc;
    }

    @Override
    public HttpParamsDataDesc getBodyParamsDataDesc() {
        return mBodyParamsDataDesc;
    }

    public void setBodyParams(HttpParamsDataDesc pHeaderParamsDataDesc) {
        mBodyParamsDataDesc = pHeaderParamsDataDesc;
    }

    public void setHttpMethod(AGHttpMethodType methodType) {
        mHttpMethod = methodType;
    }

    @Override
    public AGHttpMethodType getHttpMethod() {
        return mHttpMethod;
    }

    @Override
    public ItemPath getItemPath() {
        return mItemPath;
    }

    @Override
    public void setItemPath(ItemPath itemPath) {
        mItemPath = itemPath;
    }

    @Override
    public String getGUIDNodeName() {
        return mGUIDNodeName;
    }

    @Override
    public void setGUIDNodeName(String GUIDNodeName) {
        mGUIDNodeName = GUIDNodeName;
    }

    @Override
    public int getNumberItemsPerPage() {
        return mNumberItemsPerPage;
    }

    @Override
    public void setNumberItemsPerPage(int itemsPerPageCount) {
        mNumberItemsPerPage = itemsPerPageCount;
    }

    @Override
    public UsingFields getUsingFields() {
        return mUsingFields;
    }

    @Override
    public void setUsingFields(UsingFields usingFields) {
        mUsingFields = usingFields;
    }

    @Override
    public String getStringSource() {
        return mSource.getStringValue().trim();
    }

    @Override
    public VariableDataDesc getSource() {
        return mSource;
    }

    @Override
    public void setSource(VariableDataDesc sourceUri) {
        mSource = sourceUri;
    }

    @Override
    public AGItemTemplateDataDesc getTempleteDataDesc(int index) {
        return mTemplates.get(index);
    }

    @Override
    public AGItemTemplateDataDesc getMatchingTemplete(DataFeedItem item) {
        return mTemplates.get(0);
    }

    @Override
    public void setScreenHashCode(int screenHashcode) {
        mScreenId = screenHashcode;
    }

    @Override
    public int getScreenHashcode() {
        return mScreenId;
    }

    public void setFormat(AGFeedFormatType pFormat) {
        mFormat = pFormat;
    }

    public void setCachePolicyType(AGFeedCachePolicyType cachePolicy) {
        mCachePolicy = cachePolicy;
    }

    public void setCachePolicyAttribute(long attribute) {
        mCachePolicyAttribute = attribute;
    }

    @Override
    public AGFeedCachePolicyType getCachePolicyType() {
        return mCachePolicy;
    }

    @Override
    public long getCachePolicyAttribute() {
        return mCachePolicyAttribute;
    }

    public AGFeedFormatType getFormat() {
        return mFormat;
    }

    @Override
    public boolean isLoadingMore() {
        return mIsLoadingMore;
    }

    @Override
    public int getActiveItemIndex() {
        return mActiveItemIndex;
    }

    @Override
    public void setActiveItemIndex(int index) {
        mActiveItemIndex = index;
    }

    @Override
    public void setIsLoadingMore(boolean isLoadingMore) {
        mIsLoadingMore = isLoadingMore;
    }

    @Override
    public void setDownloadCommand(DownloadFeedCommand downloadFeedCommand) {
        if (mDownloadFeedCommand != null)
            mDownloadFeedCommand.cancel();
        mDownloadFeedCommand = downloadFeedCommand;
    }

    @Override
    public DownloadFeedCommand getDownloadCommad() {
        return mDownloadFeedCommand;
    }

    @Override
    public int getLastItemIndex() {
        return mLastItemIndex;
    }

    @Override
    public void setLastItemIndex(int lastItemIndex) {
        mLastItemIndex = lastItemIndex;
    }

    @Override
    public Namespaces getNamespaces() {
        return mNamespaces;
    }

    @Override
    public void setNamespaces(Namespaces namespaces) {
        mNamespaces = namespaces;
    }

    @Override
    public List<AbstractAGElementDataDesc> getFeedClientControls() {
        return mFeedControls;
    }

    @Override
    public LoadMoreDataDesc getLoadMoreTemplate() {
        return mLoadMoreDataDesc;
    }

    @Override
    public NoDataDataDesc getNoDataTemplate() {
        return mNoDataDataDesc;
    }

    @Override
    public LoadingDataDesc getLoadingTemplate() {
        return mLoadingDataDesc;
    }

    @Override
    public ErrorDataDesc getErrorTemplate() {
        return mErrorDataDesc;
    }

    @Override
    public void setLoadMoreTemplate(LoadMoreDataDesc loadMore) {
        mLoadMoreDataDesc = loadMore;
    }

    @Override
    public void setNoDataTemplate(NoDataDataDesc noData) {
        mNoDataDataDesc = noData;
    }

    @Override
    public void setLoadingTemplate(LoadingDataDesc loading) {
        mLoadingDataDesc = loading;
    }

    @Override
    public void setErrorTemplate(ErrorDataDesc error) {
        mErrorDataDesc = error;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mFormId.resolveVariable();
        mSource.resolveVariable();
        mHttpParamsDataDesc.resolveVariables();
        mHeaderParamsDataDesc.resolveVariables();
        mBodyParamsDataDesc.resolveVariables();
    }

    @Override
    public void addTempleteDataDesc(AGItemTemplateDataDesc templateDataDesc) {
        if (mTemplates == null) mTemplates = new ArrayList<AGItemTemplateDataDesc>();
        mTemplates.add(templateDataDesc);
    }

    @Override
    public List<AbstractAGElementDataDesc> copyTemplateControls(AbstractAGTemplateDataDesc template) {
        List<AbstractAGElementDataDesc> controls = template.getAllControls();
        List<AbstractAGElementDataDesc> copies = new ArrayList<AbstractAGElementDataDesc>(controls.size());
        for (int i = 0; i < controls.size(); i++) {
            copies.add(controls.get(i).copy());
            ((AbstractAGViewDataDesc) copies.get(i)).setParentContainer(this);
        }

        return copies;
    }

    @Override
    public void addFeedClientControl(AbstractAGElementDataDesc control) {
        IAGCollectionDataDesc section = ((AbstractAGViewDataDesc) control).getSection();

        if (!(section instanceof AGBodyDataDesc) && section != null) {
            throw new InvalidParameterException(String.format(
                    "Cannot put %s control implementing IFeedClient interface in '%s' section", this.getClass().getSimpleName(), ((AbstractAGViewDataDesc) control).getSection()));
        }

        if (((AbstractAGViewDataDesc) control).getParentContainer() == null) {
            ((AbstractAGViewDataDesc) control).setParentContainer(this);
        }

        mFeedControls.add(control);
    }

    @Override
    public AbstractAGDataFeedViewDataDesc copy() {
        AbstractAGDataFeedViewDataDesc copied = (AbstractAGDataFeedViewDataDesc) super.copy();

        copied.mNumberItemsPerPage = mNumberItemsPerPage;
        copied.mPageIndex = mPageIndex;
        copied.mServiceId = mServiceId;
        copied.mFormat = mFormat;
        copied.mCachePolicy = mCachePolicy;
        copied.mCachePolicyAttribute = mCachePolicyAttribute;

        if (mSource != null) {
            copied.mSource = mSource.copy(copied);
        }

        // DataFeed is readonly; it shouldn't be problem to retain reference in the copy; anyway, without this, we have null reference in copy
        copied.mDataFeed = mDataFeed;

        copied.mActiveItemIndex = mActiveItemIndex;
        copied.mLastItemIndex = mLastItemIndex;
        copied.mLastFeedItemCount = mLastFeedItemCount;
        copied.mRequestBodyTrasform = mRequestBodyTrasform;
        if (mItemPath != null)
            copied.mItemPath = mItemPath.copy();

        if (mTemplates != null) {
            copied.mTemplates = new ArrayList<AGItemTemplateDataDesc>();
            for (AGItemTemplateDataDesc template : mTemplates) {
                copied.mTemplates.add(template.copy());
            }
        }
        if (mUsingFields != null)
            copied.mUsingFields = mUsingFields.copy();

        if (mLoadMoreDataDesc != null)
            copied.mLoadMoreDataDesc = mLoadMoreDataDesc.copy();
        if (mNoDataDataDesc != null)
            copied.mNoDataDataDesc = mNoDataDataDesc.copy();
        if (mLoadingDataDesc != null)
            copied.mLoadingDataDesc = mLoadingDataDesc.copy();
        if (mErrorDataDesc != null)
            copied.mErrorDataDesc = mErrorDataDesc.copy();

        if (mNamespaces != null)
            copied.mNamespaces = mNamespaces.copy();

        if (mFeedControls != null) {
            copied.mFeedControls = new ArrayList<AbstractAGElementDataDesc>();
            for (AbstractAGElementDataDesc elem : copied.mFeedControls) {
                copied.addFeedClientControl(elem.copy());
            }
        }
        if (mHttpParamsDataDesc != null) {
            copied.mHttpParamsDataDesc = mHttpParamsDataDesc.copy(copied);
        }
        if (mHeaderParamsDataDesc != null) {
            copied.mHeaderParamsDataDesc = mHeaderParamsDataDesc.copy(copied);
        }
        if (mBodyParamsDataDesc != null) {
            copied.mBodyParamsDataDesc = mBodyParamsDataDesc.copy(copied);
        }
        if (mHttpMethod != null) {
            copied.mHttpMethod = mHttpMethod;
        }
        if (mFormId != null) {
            copied.mFormId = mFormId.copy(copied);
        }
        if (mResolvedUrl != null)
            copied.mResolvedUrl = mResolvedUrl;
        copied.setGUIDNodeName(mGUIDNodeName);
        return copied;
    }

    @Override
    protected boolean acceptDepthFirst(IDataDescVisitor visitor) {
        if (visitor.visit(this)) {
            return true;
        }
        if (!(visitor.getClass().equals(GetFormDataVisitor.class))) {
            for (AbstractAGElementDataDesc desc : getFeedClientControls()) {
                if (desc != null && desc.accept(visitor)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void clearTemplateControls() {
        for (AbstractAGElementDataDesc control : mTemplateControls) {
            removeFeedControl(control);
        }
        mTemplateControls.clear();
    }

    @Override
    public void addTemplateControl(AbstractAGElementDataDesc control) {
        mTemplateControls.add(control);
    }

    @Override
    public void removeFeedControl(AbstractAGElementDataDesc control) {
        mFeedControls.remove(control);
    }

    @Override
    public List<AbstractAGElementDataDesc> getFeedPresentClientControls() {
        return getFeedClientControls();
    }


    @Override
    public void setLastFeedItemCount(int count) {
        mLastFeedItemCount = count;
    }

    @Override
    public int getLastFeedItemCount() {
        return mLastFeedItemCount;
    }

    @Override
    public void clearFeedControls() {
        mFeedControls.clear();
    }

    @Override
    public void resetFeed() {
        clearFeedControls();
    }

    @Override
    public void showErrorTemplate() {
        resetFeed();
        setLastFeedItemCount(0);
        FeedManager.addTemplate(this, getErrorTemplate());
        FeedManager.reloadFeed(this);
    }

    @Override
    public void resetScrolls() {
        setScrollX(0);
        setScrollY(0);
    }

    @Override
    public void saveFormData() {
        mFormData = FormDataSerializer.serializeFormData(this);
    }

    @Override
    public void recreateFormData() {
        if (mFormData != null)
            FormDataSerializer.recreateFeedFormData(this, mFormData);
    }

    @Override
    public Pagination getPagination() {
        return mPagination;
    }

    @Override
    public void setPagination(Pagination pagination) {
        mPagination = pagination;
    }

    @Override
    public void setResolvedURL(String url) {
        mResolvedUrl = url;
    }

    @Override
    public String getResolvedUrl() {
        return mResolvedUrl;
    }

    public double getContentHeight() {
        return mContentHeight;
    }

    public void setContentHeight(double contentHeight) {
        mContentHeight = contentHeight;
    }

    public double getContentWidth() {
        return mContentWidth;
    }

    public void setContentWidth(double contentWidth) {
        mContentWidth = contentWidth;
    }

    public void setRequestBodyTrasform(String requestBodyTrasform) {
        mRequestBodyTrasform = requestBodyTrasform;
    }

    @Override
    public String getRequestBodyTrasform() {
        return mRequestBodyTrasform;
    }

    @Override
    public String getFormId() {
        return mFormId.getStringValue();
    }

    public void setFormId(VariableDataDesc formId) {
        mFormId = formId;
    }

    @Override
    public void clearFormData() {
        mFormData = null;
    }


    @Override
    public void setShouldRecreate(boolean shouldRecreate) {
        mShouldRecreate = shouldRecreate;
    }


    @Override
    public boolean shouldRecrete() {
        return mShouldRecreate;
    }
}

