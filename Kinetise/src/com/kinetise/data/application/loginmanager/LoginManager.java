package com.kinetise.data.application.loginmanager;

import com.kinetise.data.application.alterapimanager.AlterApiManager;
import com.kinetise.helpers.SalesForceHelper;

public class LoginManager {
    public boolean isUserLoggedIn() {
        boolean result = false;
        try{
            result = (AlterApiManager.isLoggedInToAlterApi() || BasicAuthLoginManager.getInstance().isUserLoggedIn());
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return result;
    }

    public void clearLoginData() {
        AlterApiManager.setAlterApiSessionId(null);
        SalesForceHelper.clearToken();
        BasicAuthLoginManager.getInstance().clearAuthenticationToken();
    }

}
