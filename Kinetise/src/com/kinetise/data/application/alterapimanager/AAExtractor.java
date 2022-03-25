package com.kinetise.data.application.alterapimanager;


public abstract class AAExtractor {
    public static final String SESSION_ID_NODE = "sessionId";
    public static final String MESSAGE_NODE = "message";
    public static final String RESPONSE = "response";
    public static final String EXPIRED_URLS_NODE = "expiredUrls";
    public static final String URL_NODE = "url";
    public static final String APP_VARIABLES_NODE = "applicationVariables";
    public static final String VARIABLE_NODE = "variable";
    public static final String KEY_NODE = "key";
    public static final String VALUE_NODE = "value";

    AAResponse mResponse;

    public AAResponse parse(String response) {
        if (response == null) {
            return null;
        }
        if (response.equals(""))
            return new AAResponse();


        try {
            initParser(response);
            parseSessionId();
            parseMessages();
            parseExpiredUrls();
            parseAppVariables();
            return mResponse;
        } catch (InvalidAlterAPIResponse e) {
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    protected abstract void parseSessionId() throws InvalidAlterAPIResponse;

    protected abstract void parseMessages() throws InvalidAlterAPIResponse;

    protected abstract void parseExpiredUrls() throws InvalidAlterAPIResponse;

    protected abstract void parseAppVariables() throws InvalidAlterAPIResponse;

    protected void initParser(String response) throws InvalidAlterAPIResponse{
        mResponse = new AAResponse();
    }

    public class InvalidAlterAPIResponse extends Exception{
    }
}
