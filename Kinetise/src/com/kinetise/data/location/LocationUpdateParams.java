package com.kinetise.data.location;

import java.util.HashMap;
import java.util.Map;

import io.realm.RealmList;
import io.realm.RealmObject;

public class LocationUpdateParams extends RealmObject {

    private String key;
    private String value;

    public LocationUpdateParams() {}

    public LocationUpdateParams(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static RealmList<LocationUpdateParams> getHttpParamsAsRealmList(Map<String, String> httpParams) {
        RealmList<LocationUpdateParams> params = new RealmList<>();
        for (Map.Entry<String, String> entry : httpParams.entrySet()) {
            params.add(new LocationUpdateParams(entry.getKey(), entry.getValue()));
        }

        return params;
    }

    public static Map<String, String> getHttpParamsAsMap(RealmList<LocationUpdateParams> params) {
        Map<String, String> httpParams = new HashMap<>();
        for (LocationUpdateParams param : params) {
            httpParams.put(param.key, param.value);
        }
        return httpParams;
    }

}