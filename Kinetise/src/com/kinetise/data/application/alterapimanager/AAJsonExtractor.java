package com.kinetise.data.application.alterapimanager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;

public class AAJsonExtractor extends AAExtractor {
    JsonObject mParsedJson;
    public static final String DESCRIPTION_NODE_NAME = "description";
    public static final String TITLE_NODE_NAME = "title";

    @Override
    protected void initParser(String response) throws InvalidAlterAPIResponse {
        super.initParser(response);
        Object parsedObject;

        if (response.equals(""))
            return;

        try {
            parsedObject = ((new JsonParser())).parse(response);
        } catch (Exception e){
            throw new InvalidAlterAPIResponse();
        }
        if(!(parsedObject instanceof JsonObject)){
            throw new InvalidAlterAPIResponse();
        }
        mParsedJson = (JsonObject) parsedObject;
    }

    @Override
    protected void parseSessionId() throws InvalidAlterAPIResponse{
        try{
            mResponse.sessionId = getStringValue(SESSION_ID_NODE);
        } catch (Exception e){}
    }

    @Override
    protected void parseMessages() throws InvalidAlterAPIResponse{
        parseDescription();
        parseTitle();
    }

    private void parseTitle() {
        try{
            JsonObject value = mParsedJson.getAsJsonObject(MESSAGE_NODE);
            if(value.has(TITLE_NODE_NAME)){
                mResponse.title = value.get(TITLE_NODE_NAME).getAsString();
            }
        } catch (Exception e){}
    }

    private void parseDescription() {
        try{
            JsonObject value = mParsedJson.getAsJsonObject(MESSAGE_NODE);
            if (value.has(DESCRIPTION_NODE_NAME)){
            mResponse.message = new AAMessage();
            mResponse.message.addMessageValue(value.get(DESCRIPTION_NODE_NAME).getAsString());
            }
        } catch (Exception e){}
    }

    @Override
    protected void parseExpiredUrls() throws InvalidAlterAPIResponse{
        JsonArray urls;
        try{
            urls = mParsedJson.get(EXPIRED_URLS_NODE).getAsJsonArray();
        } catch (Exception e){
            return;
        }

        try{
            int length = urls.size();
            String url;
            for (int i = 0; i < length; i++) {
                url = urls.get(i).getAsString();
                mResponse.expiredUrls.add(url);
            }
        }catch(Exception e) {
                throw new InvalidAlterAPIResponse();
        }
    }

    @Override
    protected void parseAppVariables() throws InvalidAlterAPIResponse{
        JsonObject variables;
        try {
            variables = mParsedJson.getAsJsonObject(APP_VARIABLES_NODE);
            if(variables==null)
                return;
        }catch(Exception e) {
            return;
        }
        try{
            for(Map.Entry<String,JsonElement> elements :variables.entrySet()){
                mResponse.applicationVariables.put(elements.getKey(),elements.getValue().getAsString());
            }
        }catch(Exception e) {
            throw new InvalidAlterAPIResponse();
        }
    }

    private String getStringValue(String key) throws InvalidAlterAPIResponse{
        try {
            return mParsedJson.get(key).getAsString();
        } catch (Exception e){
            throw new InvalidAlterAPIResponse();
        }
    }
}
