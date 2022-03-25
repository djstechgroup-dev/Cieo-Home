package com.kinetise.data.parsermanager;

import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.ApplicationDescriptionDataDesc;
import com.kinetise.data.descriptors.LocalStorageDescriptionDataDesc;
import com.kinetise.data.descriptors.datadescriptors.OverlayDataDesc;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

public class ParserManager implements IParserManager {

    private static IParserManager instance;
    private AGParser mParser;
    private boolean isPrepared;

    private ApplicationDescriptionDataDesc mAppDataDesc;
    private Map<String, AGScreenDataDesc> mScreenMap = new HashMap<>();
    private Map<String, OverlayDataDesc> mApplicationOverlays;
    private LocalStorageDescriptionDataDesc mLocalStorageDataDesc;

    private ParserManager() {
    }

    public static IParserManager getInstance() {
        if (instance == null) {
            instance = new ParserManager();
        }
        return instance;
    }

    @Override
    public void prepare(ILoadDescriptorListener listener) {
        if (!isPrepared)
            mParser.loadDescriptors(new LoadDescriptorsCallback(listener));
        else if (listener != null)
            listener.onParseCompleted();
    }

    @Override
    public boolean isPrepared() {
        return isPrepared;
    }

    @Override
    public ApplicationDescriptionDataDesc getApplicationDescription() {
        if (mAppDataDesc == null)
            throw new IllegalStateException("Did you forgot to call prepare or initialize?");
        return mAppDataDesc;
    }

    @Override
    public AGScreenDataDesc getScreenDescriptor(String screenId) {
        if (mScreenMap == null || mScreenMap.isEmpty()) {
            throw new IllegalStateException("Did you forgot to call prepare or initialize?");
        }

        if (!mScreenMap.containsKey(screenId)) {
            throw new InvalidParameterException("Cannot find screen descriptor with id=" + screenId);
        }

        return mScreenMap.get(screenId);
    }

    @Override
    public void initialize(AGParser parser) {
        mParser = parser;
    }

    @Override
    public AGParser getIAGParser() {
        return mParser;
    }

    public Map<String, AGScreenDataDesc> getScreenMap() {
        if (mScreenMap == null || mScreenMap.isEmpty()) {
            throw new IllegalStateException("Did you forgot to call prepare or initialize?");
        }
        return mScreenMap;
    }

    public Map<String, OverlayDataDesc> getApplicationOverlays() {
        if (mApplicationOverlays == null)
            throw new IllegalStateException("Did you forgot to call prepare or initialize?");
        return mApplicationOverlays;
    }

    @Override
    public LocalStorageDescriptionDataDesc getLocalStorageDataDesc() {
        return mLocalStorageDataDesc;
    }

    @Override
    public boolean isAdvertScreen() {
        return mParser.isAdvertScreen();
    }

    public class LoadDescriptorsCallback {

        private ILoadDescriptorListener prepareListener;

        public LoadDescriptorsCallback(ILoadDescriptorListener listener) {
            this.prepareListener = listener;
        }

        public void onScreensLoaded(Map<String, AGScreenDataDesc> screenMap) {
            mScreenMap = screenMap;
        }

        public void onDescriptorsLoaded(ApplicationDescriptionDataDesc appDataDesc) {
            mAppDataDesc = appDataDesc;
        }

        public void onOverlaysLoaded(Map<String, OverlayDataDesc> applicationOverlays) {
            mApplicationOverlays = applicationOverlays;
        }

        public void onLocalStorageDescriptorLoaded(LocalStorageDescriptionDataDesc localStorageDescriptionDataDesc) {
            mLocalStorageDataDesc = localStorageDescriptionDataDesc;
        }

        public void onParseCompleted() {
            isPrepared = true;
            if (prepareListener != null)
                prepareListener.onParseCompleted();
        }
    }

    public interface ILoadDescriptorListener {
        void onParseCompleted();
    }
}
