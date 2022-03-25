package com.kinetise.data.location;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class LocationUpdateSession extends RealmObject {

    @PrimaryKey
    private int sessionId;
    private String url;
    private RealmList<LocationUpdateParams> headerParams;
    private boolean isActive;

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RealmList<LocationUpdateParams> getHeaderParams() {
        return headerParams;
    }

    public void setHeaderParams(RealmList<LocationUpdateParams> headerParams) {
        this.headerParams = headerParams;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
