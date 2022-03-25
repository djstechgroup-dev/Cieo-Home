package com.kinetise.data.descriptors.actions.jsapi;

import com.kinetise.data.application.sdk.ActionManager;
import com.kinetise.data.descriptors.types.AGScreenTransition;


public class ScreenJs implements Screen {
   private static ScreenJs screenJs;

    private ScreenJs() {

    }

    public static ScreenJs getInstance() {
        if (screenJs == null) {
            screenJs = new ScreenJs();
        }
        return screenJs;
    }

    @Override
    public void go(String screenId) {
        ActionManager.getInstance().goToScreen(screenId, AGScreenTransition.NONE);
    }


    @Override
    public void backById(String screenID) {
        ActionManager.getInstance().backToScreen(screenID, AGScreenTransition.NONE);
    }

    @Override
    public void backBySteps(int steps) {
        ActionManager.getInstance().backBySteps(steps, AGScreenTransition.NONE);
    }


    @Override
    public void refresh() {
        ActionManager.getInstance().refresh();
    }

    @Override
    public void reload() {
        ActionManager.getInstance().reload();
    }


}
