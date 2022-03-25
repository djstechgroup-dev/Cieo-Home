package com.kinetise.data.application.actionmanager.functioncommands;

import android.content.Intent;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.application.actionmanager.nativeshare.ShareData;
import com.kinetise.data.descriptors.AbstractAGViewDataDesc;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;
import com.kinetise.data.descriptors.actions.VariableDataDesc;
import com.kinetise.data.parsermanager.xmlparser.structureparsers.valueparsers.AGXmlActionParser;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.data.systemdisplay.SystemDisplay;

public class FunctionNativeShare extends AbstractFunction {

    public static final String TEXT_CONTENT_TYPE = "text/plain";
    public static final String TEXT_URL = "text-url";
    public static final String IMAGE = "image";
    public static final String VALUE_NODE_NAME = "value";
    public static final String TYPE = "type";

    public FunctionNativeShare(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        final SystemDisplay display = mApplication.getSystemDisplay();
        display.blockScreenWithLoadingDialog(true);

        VariableDataDesc[] attributes = mFunctionDataDesc.getAttributes();
        final ShareData shareData = getShareDataFromJson(attributes[0].getStringValue());

        if (shareData.allDataDownloaded()){
            startShareIntent(shareData);
        } else {
           shareData.downloadImages(new ShareData.OnAllDownloadsFinishedCallback() {
               @Override
               public void onDownloadsFinished() {
                   mApplication.getSystemDisplay().blockScreenWithLoadingDialog(false);
                   startShareIntent(shareData);
               }
           }, ((AbstractAGViewDataDesc) getFunctionDataDesc().getContextDataDesc()).getFeedBaseAdress());
        }

        return null;
    }


    private void startShareIntent(ShareData shareData) {
        Intent sendIntent = createIntentForShareData(shareData);
        if (sendIntent != null) {
            String chooserTitle = LanguageManager.getInstance().getString(LanguageManager.SHARE_DIALOG_TITLE);
            sendIntent = Intent.createChooser(sendIntent, chooserTitle);
            AGApplicationState.getInstance().getActivity().startActivity(sendIntent);
        }
    }

    public ShareData getShareDataFromJson(String jsonString) {
        ShareData shareData = new ShareData();

        try {
            JsonParser parser = new JsonParser();
            JsonElement root = parser.parse(jsonString);
            JsonArray jsonArray = root.getAsJsonArray();

            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject object = jsonArray.get(i).getAsJsonObject();
                if (object.get(TYPE).getAsString().equals(TEXT_URL)) {
                    parseText(shareData, object);
                } else if (object.get(TYPE).getAsString().equals(IMAGE)) {
                    parseImage(shareData, object);
                }
            }
        } catch (Exception e) {
        }
        return shareData;
    }

    private void parseText(ShareData shareData, JsonObject object) {
        VariableDataDesc variable = AGXmlActionParser.createVariable(object.get(VALUE_NODE_NAME).getAsString(), getFunctionDataDesc().getContextDataDesc());
        variable.resolveVariable();
        shareData.putText(variable.getStringValue());
    }

    private void parseImage(ShareData shareData, JsonObject object) {
        VariableDataDesc variable = AGXmlActionParser.createVariable(object.get(VALUE_NODE_NAME).getAsString(), getFunctionDataDesc().getContextDataDesc());
        variable.resolveVariable();
        shareData.putImageSource(variable.getStringValue());
    }

    private Intent createIntentForShareData(ShareData shareData) {
        if (shareData.hasText() || shareData.hasImage()) {
            Intent intent = new Intent();
            shareData.setIntentData(intent);
            return intent;
        }

        return null;
    }
}

