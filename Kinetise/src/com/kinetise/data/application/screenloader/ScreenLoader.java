package com.kinetise.data.application.screenloader;

import com.crashlytics.android.Crashlytics;
import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.actionmanager.ExecuteActionManager;
import com.kinetise.data.application.actionmanager.functioncommands.FunctionGoToProtectedScreen;
import com.kinetise.data.application.feedmanager.FeedManager;
import com.kinetise.data.application.feedmanager.datafeed.DataFeedItem;
import com.kinetise.data.application.overalymanager.OverlayManager;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.application.screenhistory.ApplicationState;
import com.kinetise.data.application.screenhistory.ScreenHistoryManager;
import com.kinetise.data.descriptors.AGScreenDataDesc;
import com.kinetise.data.descriptors.AbstractAGElementDataDesc;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.IFeedClient;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.descriptors.desctriptorvisitors.FindDescendantsByTypeVisitor;
import com.kinetise.data.descriptors.types.AGScreenTransition;
import com.kinetise.data.exceptionmanager.ExceptionManager;
import com.kinetise.data.systemdisplay.SystemDisplay;
import com.kinetise.helpers.analytics.AnalyticsManager;
import com.kinetise.support.logger.Logger;

import java.security.InvalidParameterException;

public class ScreenLoader {

    public static final String GOOGLE_ANALITICS_NODE = "gid";

    public ScreenLoader() {
    }

    public synchronized void loadNextScreen(String screenId, AGScreenTransition transition) {
        loadNextScreen(screenId, transition, null, null, null, true);
    }

    public synchronized void loadNextScreen(String screenId, AGScreenTransition transition, AbstractAGViewDataDesc context, String alterApiContext, String guid, boolean addToBackstack) {
        PopupManager.closeMapPopup();
        if (addToBackstack) {
            addStateToBackstack();
        }

        OverlayManager.getInstance().hideCurrentOverlay(getSystemDisplay());

        ApplicationState newState = new ApplicationState(screenId, context, alterApiContext, guid);
        loadApplicationState(newState, transition);
    }

    private AGScreenDataDesc getScreenDesc(String screenId) {
        return AGApplicationState.getInstance().getScreenDesc(screenId);
    }

    private void addStateToBackstack() {
        ScreenHistoryManager historyManager = AGApplicationState.getInstance().getHistoryManager();
        historyManager.putCurrentStateOnStack();
    }

    /**
     * Performs full screen reload in context of current application state
     */
    public void reloadCurrentScreen() {
        PopupManager.closeMapPopup();
        OverlayManager.getInstance().hideCurrentOverlay(getSystemDisplay());
        loadApplicationState(AGApplicationState.getInstance().getApplicationState(), AGScreenTransition.NONE);
    }

    public synchronized void loadApplicationState(ApplicationState applicationState, AGScreenTransition transition) {
        // Cleanup previous screen and run onExit action
        ExecuteActionManager.cancelDelayedActions();
        getSystemDisplay().pauseBackgroundVideo();
        AGScreenDataDesc currentScreenDesc = AGApplicationState.getInstance().getCurrentScreenDesc();
        VariableDataDesc onScreenExitAction;
        if (currentScreenDesc != null) {
            onScreenExitAction = currentScreenDesc.getOnScreenExitAction();
            if (onScreenExitAction != null)
                onScreenExitAction.resolveVariable();

            // clear feed clients references on screen exit
            currentScreenDesc.clearFeedClients();
        }

        AGScreenDataDesc screenDataDesc = getScreenDesc(applicationState.getScreenId());

        if (screenDataDesc.isProtected() && !AGApplicationState.getInstance().isUserLoggedIn()) {
            FunctionGoToProtectedScreen.setDestinationAplicationState(applicationState);
            String protectedScreenId = AGApplicationState.getInstance().getApplicationDescription().getProtectedLoginScreenId();
            AGScreenDataDesc protectedScreenDataDesc = getScreenDesc(protectedScreenId);
            AGApplicationState.getInstance().setApplicationState(applicationState);
            applicationState = new ApplicationState(protectedScreenId, null, null);
            screenDataDesc = protectedScreenDataDesc;
        }
        AGApplicationState.getInstance().setApplicationState(applicationState);
        if (KinetiseApplication.getInstance().useCrashlytics()) {
            Crashlytics.getInstance().core.setString("ScreenID", screenDataDesc.getScreenId());
            Crashlytics.getInstance().core.log("Loading screen: " + screenDataDesc.getScreenId());
        }
        //load screen and reset screen (scrolls and feeds) for detail screens
        loadScreen(screenDataDesc, applicationState.getContext() != null, transition);
    }

    public synchronized boolean loadPreviousScreen(AGScreenTransition transition) {
        ScreenHistoryManager historyManager = AGApplicationState.getInstance().getHistoryManager();
        ApplicationState appState = historyManager.getPreviousScreen();

        if (appState != null) {
            loadApplicationState(appState, transition);
            return true;
        }

        return false;
    }

    private void loadScreen(final AGScreenDataDesc screenDataDesc, final boolean resetScreen, AGScreenTransition transition) {
        PopupManager.dismissDialog();
        // we find all feed clients once to optimize as they are needed in couple places
        screenDataDesc.updateFeeds();
        for (IFeedClient feed : screenDataDesc.getFeedClients()) {
            feed.clearFeedControls();
        }
        clearSavedFormData(screenDataDesc);
        screenDataDesc.resolveVariables();

        FeedManager.clearControlsOnFeeds(screenDataDesc);

        if (screenDataDesc == null) {
            throw new InvalidParameterException("Cannot load screen!");
        }
        Logger.v(this, "loadScreen", "LoadedScreen :" + screenDataDesc.getScreenId());

        SystemDisplay display = getSystemDisplay();
        display.onScreenLoading();
        try {
            String detalGoogleId = null;
            AbstractAGElementDataDesc context = AGApplicationState.getInstance().getApplicationState().getContext();
            int activeItemField;
            if (context != null && context instanceof IFeedClient) {
                IFeedClient contextFeed = (IFeedClient) context;
                activeItemField = contextFeed.getActiveItemIndex();
                setItemIndexOnAllViewDescriptors(screenDataDesc, activeItemField);
                DataFeedItem item = contextFeed.getFeedDescriptor().getItem(activeItemField);
                if (item.hasField(GOOGLE_ANALITICS_NODE))
                    detalGoogleId = item.getByKey(GOOGLE_ANALITICS_NODE).toString();
            }

            display.displayScreen(screenDataDesc, resetScreen, transition);

            AnalyticsManager.getInstance().sendScreenView(screenDataDesc.getAnalitycsTag(), detalGoogleId);
        } catch (Exception e) {
            ExceptionManager.getInstance().handleException(e);
        }
    }

    private void clearSavedFormData(AGScreenDataDesc screenDataDesc) {
        for (IFeedClient feed : screenDataDesc.getFeedClients()) {
            feed.clearFormData();
        }
    }

    private void setItemIndexOnAllViewDescriptors(AGScreenDataDesc screenDataDesc, int activeItemField) {
        FindDescendantsByTypeVisitor<AbstractAGViewDataDesc> visitor = new FindDescendantsByTypeVisitor<AbstractAGViewDataDesc>(AbstractAGViewDataDesc.class);
        screenDataDesc.accept(visitor);
        for (AbstractAGViewDataDesc client : visitor.getFoundDataDescriptors()) {
            client.setFeedItemIndex(activeItemField);
        }
    }

    private SystemDisplay getSystemDisplay() {
        return AGApplicationState.getInstance().getSystemDisplay();
    }
}
