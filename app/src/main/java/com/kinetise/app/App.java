package com.kinetise.app;

import com.kinetise.app.views.CustomCompatButton;
import com.kinetise.app.views.CustomInflatedLayout;
import com.kinetise.data.systemdisplay.views.ViewFactoryManager;
import com.kinetise.stub.InjectKinetiseApplication;

public class App extends InjectKinetiseApplication{

    @Override
    public void onCreate() {
        super.onCreate();

        //add any custom views that are defined inside Kinetise editor
        ViewFactoryManager.registerCustomView("controlcustombutton", CustomCompatButton.class);
        ViewFactoryManager.registerCustomView("controlcustomlayout", CustomInflatedLayout.class);
    }
}
