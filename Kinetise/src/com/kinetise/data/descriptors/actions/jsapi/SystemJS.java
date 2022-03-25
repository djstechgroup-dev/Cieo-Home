package com.kinetise.data.descriptors.actions.jsapi;

import com.kinetise.data.application.actionmanager.functioncommands.FunctionGetControl;
import com.kinetise.data.application.popupmanager.PopupManager;


public class SystemJS implements System {
    private static SystemJS mSystemJS;
    private SystemJS(){}

    public static SystemJS getInstance(){
        if(mSystemJS==null){
            mSystemJS=new SystemJS();
        }
        return mSystemJS;
    }

    public void showAlert(String message) {
        PopupManager.showAlert(message, "jsAlert");
    }

    @Override
    public void showToast(String message) {
        PopupManager.showToast(message);
    }

}
