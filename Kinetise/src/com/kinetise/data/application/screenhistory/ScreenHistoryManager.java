package com.kinetise.data.application.screenhistory;

import com.kinetise.data.application.AGApplicationState;

import java.io.Serializable;

public class ScreenHistoryManager implements Serializable{

    private HistoryStack mHistoryStack;
    
    public ScreenHistoryManager() {
        mHistoryStack = new HistoryStack();
    }

    public void putCurrentStateOnStack(){
            ApplicationState appState = AGApplicationState.getInstance().getApplicationState();

        putStateOnStack(appState);
    }

    public void putStateOnStack(ApplicationState appState) {
        if (appState == null)
            return;
        String screenId = appState.getScreenId();
        if (screenId == null)
            return;
        if(AGApplicationState.isSplashScreen(screenId))
            return;

        mHistoryStack.push(appState.copy());
    }

    public ApplicationState getLastScreenWithId(String screenId) {
        String screenIdFromStack = "";
        ApplicationState state = null;
        while(!screenIdFromStack.equals(screenId)){
            state=getPreviousState();
            if(state == null)
                return null;
            screenIdFromStack = state.getScreenId();
        }

        return state;
    }

    public ApplicationState getScreenByID(String id){
        ApplicationState state = getLastScreenWithId(id);
        if(state==null)
            return new ApplicationState(AGApplicationState.getInstance().getFirstScreenNoSplash());
        return state;
    }


    public ApplicationState getScreenBySteps(int steps) {
        ApplicationState state = null;
        while(mHistoryStack.getSize()>0&&steps>0){
            state=getPreviousState();
            steps--;
        }
        if(state==null)
            return new ApplicationState(AGApplicationState.getInstance().getFirstScreenNoSplash());
        return state;
    }

    public void clear() {
        mHistoryStack.clear();
    }

    private ApplicationState getPreviousState() {
        if(AGApplicationState.getInstance().isUserLoggedIn()){
            return getPreviousNonLoginScreen();
        }
        else return mHistoryStack.pop();

    }

    private ApplicationState getPreviousNonLoginScreen() {
        ApplicationState state = mHistoryStack.pop();
        while(state != null && AGApplicationState.isLoginScreen(state.getScreenId()))
            state = mHistoryStack.pop();
        return state;
    }


    public ApplicationState getPreviousScreen() {
        return  getPreviousState();
    }
}
