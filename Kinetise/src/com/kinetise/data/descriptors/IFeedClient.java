package com.kinetise.data.descriptors;

import com.kinetise.data.application.feedmanager.DownloadFeedCommand;
import com.kinetise.data.application.feedmanager.datafeed.DataFeed;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.datadescriptors.AbstractAGTemplateDataDesc;
import com.kinetise.data.descriptors.datadescriptors.feeddatadesc.*;
import com.kinetise.data.descriptors.desctriptorvisitors.IDataDescVisitor;
import com.kinetise.data.descriptors.types.AGFeedCachePolicyType;
import com.kinetise.data.descriptors.types.AGFeedFormatType;
import com.kinetise.data.descriptors.types.AGHttpMethodType;

import java.util.List;

public interface IFeedClient {


    /**
     * Hash code of a screen this feed client is displayed on
     */
    void setScreenHashCode(int screenHashcode);

    /**
     * see IFeedClient.setScreenHashCode()
     */
    int getScreenHashcode();

    /**
     * @return Feed descriptor of given feed
     * */
    abstract DataFeed getFeedDescriptor();

    HttpParamsDataDesc getHeaders();

    String getId();
    /**
     * @return ItemPath for feed(Item path is representation of xPath query for objects we are searching)
     * */
    ItemPath getItemPath();
    /**
     * @return Number of how many items we have per page
     * */

    String getGUIDNodeName();

    void setGUIDNodeName(String GUIDNodeName);

    int getNumberItemsPerPage();

    /**
     * @return Url of webservice
     * */
    VariableDataDesc getSource();
    /**
     * @return Template of hierarchy to be added to screen for each item
     * */
    AGItemTemplateDataDesc getTempleteDataDesc(int index);

    /**
     * gets item template based on the fields present
     *
     * @param item Item for which itemTemplate we are looking
     * @return item template matching the fields array
     */
    AGItemTemplateDataDesc getMatchingTemplete(DataFeedItem item);

    /**
     * @return UsingField(sub items of searched xpath)
     * */
    UsingFields getUsingFields();

    /**
     * Sets new FeedDescriptor for feed
     * @param descriptor FeedDescriptor for feed
     * */
    void setFeedDescriptor(DataFeed descriptor);

    /**
     * Setter for ItemPath(xpath query and format) for feed
     * @param itemPath ItemPath for feed
     * */
    void setItemPath(ItemPath itemPath);

    /**
     * Sets how many items should be shown per page
     * @param itemsPerPageCount Number of items per page
     * */
    void setNumberItemsPerPage(int itemsPerPageCount);

    String getStringSource();

    /**
     * Sets url of feed
     * @param sourceUri Url of feed
     * */
    void setSource(VariableDataDesc sourceUri);
    /**
     * Sets template for item in feed
     * @param templateDataDesc Template for feed
     * */
    void addTempleteDataDesc(AGItemTemplateDataDesc templateDataDesc);

    void setUsingFields(UsingFields usingFields);
    /**
     * Adds new control to feed
     * @param control Descriptor of view
     * */
    void addFeedClientControl(AbstractAGElementDataDesc control);

    List<AbstractAGElementDataDesc> getFeedClientControls();

    List<AbstractAGElementDataDesc> getFeedPresentClientControls();

    /**
     * @return Array containing information about descriptor hierarchy
     * @param template Template with controls to obtain
     * */
    List<AbstractAGElementDataDesc> copyTemplateControls(AbstractAGTemplateDataDesc template);

    /**
     * @return Gets active item field
     * */
    int getActiveItemIndex();
    /**
     * @return LoadMore descriptor for feed
     * */
    LoadMoreDataDesc getLoadMoreTemplate();;
    /**
     * @return NoDataDataDesc descriptor for feed
     * */
    NoDataDataDesc getNoDataTemplate();;
    /**
     * @return LoadingDataDesc descriptor for feed
     * */
    LoadingDataDesc getLoadingTemplate();;
    /**
     * @return ErrorDataDesc descriptor for feed
     * */
    ErrorDataDesc getErrorTemplate();
    /**
     * @return Namespaces in feed
     * */
    Namespaces getNamespaces();

    /**
     * Sets active item index in feed
     * */
    void setActiveItemIndex(int index);

    /**
     * Sets LoadMoreTemplate descriptor for feed
     * @param loadMore Descriptor of LoadMoreTemplate type for feed
     * */
    void setLoadMoreTemplate(LoadMoreDataDesc loadMore);
    /**
     * Sets NoDataTemplate descriptor for feed
     * @param noData Descriptor of NoDataTemplate type for feed
     * */
    void setNoDataTemplate(NoDataDataDesc noData);
    /**
     * Sets LoadingTemplate descriptor for feed
     * @param loading Descriptor of LoadingTemplate type for feed
     * */
    void setLoadingTemplate(LoadingDataDesc loading);
    /**
     * Sets ErrorTemplate descriptor for feed
     * @param error Descriptor of ErrorTemplate type for feed
     * */
    void setErrorTemplate(ErrorDataDesc error);
    /**
     * Sets namespaces to be parsed on current feed
     * @param namespaces List of namespaces to be parsed
     * */
    void setNamespaces(Namespaces namespaces);
    /**
     * Removes all controls from feed
     * */
    void clearFeedControls();

    void removeFeedControl(AbstractAGElementDataDesc control);

     /**
      * Method for allowing visitors traverse feed hierarchy
      * */
    boolean accept(IDataDescVisitor visitor);

    void resetFeed();

    HttpParamsDataDesc getHttpParams();

    HttpParamsDataDesc getLocalDBParamsDataDesc();

    HttpParamsDataDesc getBodyParamsDataDesc();

    AGFeedFormatType getFormat();

    boolean isLoadingMore();

    void setIsLoadingMore(boolean b);

    void setDownloadCommand(DownloadFeedCommand downloadFeedCommand);

    DownloadFeedCommand getDownloadCommad();

    int getLastItemIndex();

    void setLastItemIndex(int index);

    void addTemplateControl(AbstractAGElementDataDesc control);

    void clearTemplateControls();

    /**
     * Sets total count of feed items that has been last displayed
     * @param count
     */
    void setLastFeedItemCount(int count);

    /**
     * Gets total count of feed items that has been last displayed
     * @return
     */
    int getLastFeedItemCount();

    void resetScrolls();

    AGHttpMethodType getHttpMethod();

    AGFeedCachePolicyType getCachePolicyType();

    long getCachePolicyAttribute();

    void showErrorTemplate();

    void saveFormData();

    void recreateFormData();

    void setPagination(Pagination pagination);

    Pagination getPagination();

    void setResolvedURL(String url);

    String getResolvedUrl();

    String getFormId();

    String getRequestBodyTrasform();

    void clearFormData();

    void setShouldRecreate(boolean shouldRecreate);

    boolean shouldRecrete();
}
