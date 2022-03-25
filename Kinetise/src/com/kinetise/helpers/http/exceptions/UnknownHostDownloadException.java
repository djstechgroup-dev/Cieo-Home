package com.kinetise.helpers.http.exceptions;

import java.net.URI;

public class UnknownHostDownloadException extends DownloadException {
    private String mUrl;

    public UnknownHostDownloadException(String url){
        mUrl = url;
    }

    public String getHostName(){
        try{
            URI uri = URI.create(mUrl);
            return uri.getHost();
        } catch (Exception e){
            return mUrl;
        }
    }
}
