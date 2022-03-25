package com.kinetise.data.descriptors.datadescriptors;

import com.kinetise.data.application.feedmanager.DownloadFeedCommand;
import com.kinetise.data.application.feedmanager.FeedManager;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.application.formdatautils.FeedFormData;
import com.kinetise.data.application.formdatautils.FormDataSerializer;
import com.kinetise.data.application.formdatautils.GetFormDataVisitor;
import com.kinetise.data.descriptors.AbstractAGContainerDataDesc;
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
import com.kinetise.data.descriptors.types.AGLayoutType;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Abstract class concreting feeds functionality
 */
public abstract class AbstractAGDataFeedDataDesc extends AbstractAGContainerDataDesc implements IFeedClient {

    protected DataFeed mDataFeed;
    protected ItemPath mItemPath;
    protected String mGUIDNodeName;
    protected VariableDataDesc mSource;
    protected List<AGItemTemplateDataDesc> mTemplates;
    protected LoadMoreDataDesc mLoadMoreDataDesc;
    protected NoDataDataDesc mNoDataDataDesc;
    protected LoadingDataDesc mLoadingDataDesc;
    protected ErrorDataDesc mErrorDataDesc;
    protected int mNumberItemsPerPage;
    private UsingFields mUsingFields;
    private Namespaces mNamespaces;
    private int mActiveItemIndex;
    private HttpParamsDataDesc mLocalDBParamsDataDesc;
    private HttpParamsDataDesc mHttpParamsDataDesc;
    private HttpParamsDataDesc mHeaderParamsDataDesc;
    private HttpParamsDataDesc mBodyParamsDataDesc;
    private AGHttpMethodType mHttpMethod;
    private AGFeedFormatType mFormat;
    private boolean mIsLoadingMore;
    private DownloadFeedCommand mDownloadFeedCommand;
    private int mLastItemIndex;
    private int mLastFeedItemCount;
    private FeedFormData mFormData;
    private Pagination mPagination;

    private List<AbstractAGElementDataDesc> mTemplateControls = new ArrayList<AbstractAGElementDataDesc>();
    transient public boolean deserialized = true;
    private static final String LOADMORE_ID = "loadmore";
    private int elementNumber = 0;
    private AGFeedCachePolicyType mCachePolicy;
    private long mCachePolicyAttribute;
    private String mResolvedUrl;
    private double mContentHeight;
    private double mContentWidth;
    private String mRequestBodyTrasform;
    private VariableDataDesc mFormId;
    private boolean mShouldRecreate;

    public AbstractAGDataFeedDataDesc(String id, AGLayoutType layoutType) {
        super(id, layoutType);
        deserialized = false;
        mHttpMethod = AGHttpMethodType.GET;
    }

    @Override
    public void setGUIDNodeName(String GUIDNodeName) {
        mGUIDNodeName = GUIDNodeName;
    }

    @Override
    public String getGUIDNodeName() {
        return mGUIDNodeName;
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
    public boolean isLoadingMore() {
        return mIsLoadingMore;
    }

    @Override
    public void setIsLoadingMore(boolean isLoading) {
        mIsLoadingMore = isLoading;
    }

    @Override
    public void setDownloadCommand(DownloadFeedCommand downloadFeedCommand) {
        if (mDownloadFeedCommand != null)
            mDownloadFeedCommand.cancel();
        mDownloadFeedCommand = downloadFeedCommand;
    }

    @Override
    protected boolean acceptDepthFirst(IDataDescVisitor visitor) {
        if (visitor.visit(this)) {
            return true;
        }
        if (!(visitor.getClass().equals(GetFormDataVisitor.class))) {
            for (AbstractAGElementDataDesc desc : getAllControls()) {
                if (desc != null && desc.accept(visitor)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void resolveVariables() {
        super.resolveVariables();
        mFormId.resolveVariable();
        mSource.resolveVariable();
        mHttpParamsDataDesc.resolveVariables();
        mLocalDBParamsDataDesc.resolveVariables();
        mHeaderParamsDataDesc.resolveVariables();
        mBodyParamsDataDesc.resolveVariables();
    }

    @Override
    public Namespaces getNamespaces() {
        return mNamespaces;
    }

    @Override
    public void setNamespaces(Namespaces namespaces) {
        mNamespaces = namespaces;
    }

    /**
     * @return Active item Index
     */
    @Override
    public int getActiveItemIndex() {
        return mActiveItemIndex;
    }

    @Override
    public void setActiveItemIndex(int index) {
        mActiveItemIndex = index;
    }

    @Override
    public DataFeed getFeedDescriptor() {
        return mDataFeed;
    }

    @Override
    public void setFeedDescriptor(DataFeed descriptor) {
        mDataFeed = descriptor;
        if (mDataFeed != null)
            mDataFeed.setGUID(mGUIDNodeName);
    }

    @Override
    public void clearFeedControls() {
        removeAllControls();
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
    public int getNumberItemsPerPage() {
        return mNumberItemsPerPage;
    }

    @Override
    public void setNumberItemsPerPage(int itemsPerPageCount) {
        mNumberItemsPerPage = itemsPerPageCount;
    }

    @Override
    public void setSection(IAGCollectionDataDesc section) {
        super.setSection(section);
    }

    @Override
    public String getStringSource() {
        return mSource.getStringValue().replaceAll("\\{", "%7B").replaceAll("\\}", "%7D").trim();
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
        if (index >= mTemplates.size() || index < 0)
            throw new ArrayIndexOutOfBoundsException(index);
        return mTemplates.get(index);
    }

    @Override
    public void addTempleteDataDesc(AGItemTemplateDataDesc templateDataDesc) {
        if (mTemplates == null) mTemplates = new ArrayList<AGItemTemplateDataDesc>();
        mTemplates.add(templateDataDesc);
        templateDataDesc.setTemplateNumber(mTemplates.size());

    }

    @Override
    public List<AbstractAGElementDataDesc> getFeedClientControls() {
        return getAllControls();
    }

    @Override
    public AGItemTemplateDataDesc getMatchingTemplete(DataFeedItem item) {
        for (AGItemTemplateDataDesc templateDataDesc : mTemplates) {
            if (templateDataDesc.templateMatches(item))
                return templateDataDesc;
        }
        return null;
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
    public AbstractAGDataFeedDataDesc copy() {
        AbstractAGDataFeedDataDesc copied = (AbstractAGDataFeedDataDesc) super.copy();
        copied.mNumberItemsPerPage = mNumberItemsPerPage;
        copied.mActiveItemIndex = mActiveItemIndex;
        copied.mLastItemIndex = mLastItemIndex;
        copied.mLastFeedItemCount = mLastFeedItemCount;
        copied.mFormat = mFormat;
        copied.mCachePolicy = mCachePolicy;
        copied.mCachePolicyAttribute = mCachePolicyAttribute;
        copied.mRequestBodyTrasform = mRequestBodyTrasform;
        if (mItemPath != null)
            copied.mItemPath = mItemPath.copy();

        if (mSource != null)
            copied.mSource = mSource.copy(copied);
        if (mDataFeed != null)
            copied.mDataFeed = mDataFeed;


        if (mTemplates != null) {
            copied.mTemplates = new ArrayList<AGItemTemplateDataDesc>();
            for (AGItemTemplateDataDesc template : mTemplates)
                copied.mTemplates.add(template.copy());
        }
        if (mUsingFields != null)
            copied.mUsingFields = mUsingFields.copy();

        if (mNamespaces != null)
            copied.mNamespaces = mNamespaces.copy();

        if (mLoadMoreDataDesc != null)
            copied.mLoadMoreDataDesc = mLoadMoreDataDesc.copy();
        if (mNoDataDataDesc != null)
            copied.mNoDataDataDesc = mNoDataDataDesc.copy();
        if (mLoadingDataDesc != null)
            copied.mLoadingDataDesc = mLoadingDataDesc.copy();
        if (mErrorDataDesc != null)
            copied.mErrorDataDesc = mErrorDataDesc.copy();

        if (mHttpParamsDataDesc != null) {
            copied.mHttpParamsDataDesc = mHttpParamsDataDesc.copy(copied);
        }
        if (mHeaderParamsDataDesc != null) {
            copied.mHeaderParamsDataDesc = mHeaderParamsDataDesc.copy(copied);
        }
        if (mHttpMethod != null) {
            copied.mHttpMethod = mHttpMethod;
        }
        if (mBodyParamsDataDesc != null) {
            copied.mBodyParamsDataDesc = mBodyParamsDataDesc.copy(copied);
        }
        if (mFormId != null) {
            copied.mFormId = mFormId.copy(copied);
        }
        if (mResolvedUrl != null) {
            copied.mResolvedUrl = mResolvedUrl;
        }
        if (mLocalDBParamsDataDesc != null) {
            copied.mLocalDBParamsDataDesc = mLocalDBParamsDataDesc.copy(copied);
        }
        copied.setGUIDNodeName(mGUIDNodeName);

        return copied;
    }

    @Override
    public void addFeedClientControl(AbstractAGElementDataDesc control) {

        if (((AbstractAGViewDataDesc) control).getParentContainer() == null) {
            ((AbstractAGViewDataDesc) control).setParentContainer(this);
        }

        addControl(control);
    }

    @Override
    public List<AbstractAGElementDataDesc> copyTemplateControls(AbstractAGTemplateDataDesc template) {
        List<AbstractAGElementDataDesc> controls = template.getAllControls();
        List<AbstractAGElementDataDesc> copies = new ArrayList<AbstractAGElementDataDesc>(controls.size());

        for (int i = 0; i < controls.size(); i++) {
            copies.add(controls.get(i).copy());
        }

        return copies;
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
        setIdIfNone(mLoadMoreDataDesc);
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

    private void setIdIfNone(IAGCollectionDataDesc loadMoreDataDesc) {
        if (loadMoreDataDesc != null) {

            for (AbstractAGElementDataDesc element : loadMoreDataDesc.getAllControls()) {
                if (((AbstractAGViewDataDesc) element).getId() == null) {
                    ((AbstractAGViewDataDesc) element).setId(String.format(Locale.US, "%s_%s_%d", getId(), LOADMORE_ID, elementNumber++));
                    if (element instanceof IAGCollectionDataDesc) {
                        setIdIfNone((IAGCollectionDataDesc) element);
                    }
                }
            }

        }
    }

    @Override
    public AbstractAGDataFeedDataDesc createInstance() {
        return (AbstractAGDataFeedDataDesc) super.copy();
    }

    @Override
    public void resetFeed() {
        mLastItemIndex = -1;
        clearFeedControls();
    }

    @Override
    public void resetScrolls() {
        setScrollX(0);
        setScrollY(0);
    }

    private int mScreenHashCode;

    @Override
    public void setScreenHashCode(int screenHashcode) {
        mScreenHashCode = screenHashcode;
    }

    @Override
    public int getScreenHashcode() {
        return mScreenHashCode;
    }

    public HttpParamsDataDesc getHttpParams() {
        return mHttpParamsDataDesc;
    }

    public HttpParamsDataDesc getLocalDBParamsDataDesc() {
        return mLocalDBParamsDataDesc;
    }

    public void setHttpParams(HttpParamsDataDesc pHttpParamsDataDesc) {
        mHttpParamsDataDesc = pHttpParamsDataDesc;
    }

    public void setHeaderParams(HttpParamsDataDesc pHttpParamsDataDesc) {
        mHeaderParamsDataDesc = pHttpParamsDataDesc;
    }

    public void setLocalDBParams(HttpParamsDataDesc mLocalDBParamsDataDesc) {
        this.mLocalDBParamsDataDesc = mLocalDBParamsDataDesc;
    }

    @Override
    public HttpParamsDataDesc getBodyParamsDataDesc() {
        return mBodyParamsDataDesc;
    }

    public void setBodyParams(HttpParamsDataDesc pHttpParamsDataDesc) {
        mBodyParamsDataDesc = pHttpParamsDataDesc;
    }

    public void setHttpMethod(AGHttpMethodType methodType) {
        mHttpMethod = methodType;
    }

    @Override
    public AGHttpMethodType getHttpMethod() {
        return mHttpMethod;
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
    public HttpParamsDataDesc getHeaders() {
        return mHeaderParamsDataDesc;
    }

    @Override
    public void removeFeedControl(AbstractAGElementDataDesc control) {
        removeControl(control);
    }

    @Override
    public void addTemplateControl(AbstractAGElementDataDesc control) {
        mTemplateControls.add(control);
    }

    @Override
    public void clearTemplateControls() {
        for (AbstractAGElementDataDesc control : mTemplateControls) {
            removeFeedControl(control);
        }
        mTemplateControls.clear();
    }

    @Override
    public DownloadFeedCommand getDownloadCommad() {
        return mDownloadFeedCommand;
    }

    @Override
    public List<AbstractAGElementDataDesc> getFeedPresentClientControls() {
        return getPresentControls();
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
    public void showErrorTemplate() {
        resetFeed();
        setLastFeedItemCount(0);
        FeedManager.addTemplate(this, getErrorTemplate());
        FeedManager.reloadFeed(this);
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

    public void setContentHeight(double contentHeight) {
        mContentHeight = contentHeight;
    }

    public double getContentHeight() {
        return mContentHeight;
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

    public void setFormId(VariableDataDesc formId) {
        mFormId = formId;
    }

    public String getFormId() {
        return mFormId.getStringValue();
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
