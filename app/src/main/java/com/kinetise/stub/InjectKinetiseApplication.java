package com.kinetise.stub;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.kinetise.app.javascript.CustomJSInterface;
import com.kinetise.app.javascript.ICustomJSInterface;
import com.kinetise.components.application.KinetiseApplication;
import com.kinetise.data.application.actionmanager.JSEvaluatorFactory;
import com.kinetise.data.parsermanager.ParserManager;
import com.kinetise.data.parsermanager.xmlparser.AGXmlParser;
import com.kinetise.support.logger.Logger;

public class InjectKinetiseApplication extends KinetiseApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        JSEvaluatorFactory.getInstance().setCustomHandler(ICustomJSInterface.class, new CustomJSInterface());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        RWrapperUtil.inject();
        ParserManager.getInstance().initialize(new AGXmlParser());
        Logger.setDebuggable(((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0));
    }
}
