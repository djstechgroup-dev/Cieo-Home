package com.kinetise.data.parsermanager.xmlparser;

import com.kinetise.data.packagemanager.AppPackage;
import com.kinetise.data.packagemanager.AppPackageManager;
import com.kinetise.data.parsermanager.AGParser;

public class AGXmlParser extends AGParser {

    @Override
    public String getXml() {
        AppPackageManager packageManager = AppPackageManager.getInstance();
        if (packageManager != null) {
            AppPackage appPackage = packageManager.getPackage();
            return appPackage.getApplicationXml();
        }
        return null;
    }

}
