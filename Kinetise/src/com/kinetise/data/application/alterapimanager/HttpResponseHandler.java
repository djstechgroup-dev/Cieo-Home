package com.kinetise.data.application.alterapimanager;

import com.kinetise.data.application.popupmanager.PopupMessage;
import com.kinetise.data.sourcemanager.LanguageManager;

import java.util.ArrayList;
import java.util.List;

public class HttpResponseHandler {

    public static PopupMessage getCustomHttpErrorMessage(int statusCode) {
        LanguageManager lm = LanguageManager.getInstance();
        String description = lm.getString(LanguageManager.ERROR_HTTP + "_" + statusCode);
        if (description.equals(LanguageManager.TEXT_NOT_FOUND_IN_DICTIONARY)) {
            description = lm.getString(LanguageManager.ERROR_HTTP) + " " + statusCode;
        }

        return new PopupMessage(lm.getString(LanguageManager.POPUP_ERROR_HEADER), description);
    }

    public static PopupMessage[] getAlterApiMessages(AAResponse response, String header) {
        List<PopupMessage> messages = new ArrayList<>();
        if (response != null && response.message != null) {
            String title = response.title;
            List<String> descriptions = response.message.getMessageValues();

            if (title != null && descriptions != null) {
                messages.add(new PopupMessage(title, descriptions.get(0)));
            } else if (descriptions != null) {
                for (String description : descriptions) {
                    messages.add(new PopupMessage(header, description));
                }
            }
        }
        return messages.toArray(new PopupMessage[messages.size()]);
    }

}
