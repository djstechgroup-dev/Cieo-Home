package com.kinetise.helpers.gcm;

import com.google.android.gms.iid.InstanceIDListenerService;

public class GCMInstanceIDListenerService extends InstanceIDListenerService {

    @Override
    public void onTokenRefresh() {
            new GCMManager().registerGCM(getApplicationContext());
    }

}
