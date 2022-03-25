package com.kinetise.data.sourcemanager;

import com.kinetise.data.location.LocationUpdate;
import com.kinetise.data.location.LocationUpdateSession;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmManager {

    private static RealmManager mInstance;
    private final Realm mRealm;

    public static RealmManager getInstance() {
        if (mInstance == null) {
            mInstance = new RealmManager();
        }
        return mInstance;
    }

    private RealmManager() {
        mRealm = Realm.getDefaultInstance();
    }

    public Realm getRealm() {
        return mRealm;
    }

    public void insertLocationUpdate(LocationUpdate locationUpdate) {
        mRealm.beginTransaction();
        mRealm.copyToRealm(locationUpdate);
        mRealm.commitTransaction();
    }

    public void insertLocationUpdateSession(LocationUpdateSession session) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(session);
        mRealm.commitTransaction();
    }

    public List<LocationUpdate> getFirstLocationUpdates(int limit) {
        LocationUpdate firstLocation = mRealm.where(LocationUpdate.class).findFirst();
        if (firstLocation == null)
            return null;
        RealmResults<LocationUpdate> locations = mRealm.where(LocationUpdate.class).equalTo("sessionId", firstLocation.getSessionId()).findAll();
        int size = locations.size();

        return locations.subList(0, Math.min(limit, size));
    }

    public LocationUpdateSession getLocationUpdatesSession(int sessionId) {
        return mRealm.where(LocationUpdateSession.class).equalTo("sessionId", sessionId).findFirst();
    }

    public void removeLocation(List<LocationUpdate> locations) {
        mRealm.beginTransaction();
        for (int i = locations.size() - 1; i >= 0; i--) {
            LocationUpdate update = locations.get(i);
            update.deleteFromRealm();
        }
        mRealm.commitTransaction();
    }

    public RealmResults<LocationUpdateSession> getSessions() {
        return mRealm.where(LocationUpdateSession.class).findAll();
    }

    public LocationUpdateSession getSession(int sessionId) {
        return mRealm.where(LocationUpdateSession.class).equalTo("sessionId", sessionId).findFirst();
    }

    public int getLastSessionId() {
        Number sessionId = mRealm.where(LocationUpdateSession.class).max("sessionId");
        if (sessionId == null)
            return 0;
        else
            return sessionId.intValue();
    }

    public void closeAllSessions() {
        mRealm.beginTransaction();
        for (LocationUpdateSession session : RealmManager.getInstance().getSessions()) {
            session.setActive(false);
            mRealm.copyToRealmOrUpdate(session);
        }
        mRealm.commitTransaction();
    }

    public void deleteInactiveSessions() {
        mRealm.beginTransaction();
        for (LocationUpdateSession session : RealmManager.getInstance().getSessions()) {
            if (!session.isActive()) {
                session.getHeaderParams().deleteAllFromRealm();
                session.deleteFromRealm();
            }
        }
        mRealm.commitTransaction();
    }
}
