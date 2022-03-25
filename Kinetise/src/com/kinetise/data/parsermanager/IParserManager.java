package com.kinetise.data.parsermanager;

import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.ApplicationDescriptionDataDesc;
import com.kinetise.data.descriptors.LocalStorageDescriptionDataDesc;
import com.kinetise.data.descriptors.datadescriptors.OverlayDataDesc;

import java.util.Map;

public interface IParserManager {

    /**
     * {@link #prepare()} must be called first
     * Returns application description for given appId
     *
     * @return application description
     */
    ApplicationDescriptionDataDesc getApplicationDescription();

    /**
     * {@link #prepare()} must be called first
     * Gets descriptor for given screen and application
     *
     * @param screenId id of screen
     * @return ScreenDescriptor
     */
    AGScreenDataDesc getScreenDescriptor(String screenId);

    Map<String, AGScreenDataDesc> getScreenMap();

    Map<String, OverlayDataDesc> getApplicationOverlays();

    LocalStorageDescriptionDataDesc getLocalStorageDataDesc();

    /**
     * {@link #prepare()} must be called first
     * Initializes ParserManager with Parser strategy
     *
     * @param loaderStrategy strategy to inject
     */
    void initialize(AGParser loaderStrategy);

    /**
     * {@link #prepare(ParserManager.ILoadDescriptorListener prepareListener)} must be called first
     * Get IAGParser
     */
    AGParser getIAGParser();

    /**
     * Must be called first, loads all descriptors
     */
    void prepare(ParserManager.ILoadDescriptorListener prepareListener);

    boolean isPrepared();

    boolean isAdvertScreen();
}
