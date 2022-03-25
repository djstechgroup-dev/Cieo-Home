package com.kinetise.data.systemdisplay.helpers;

/**
 * Created by SzymonGrzeszczuk on 2015-12-21.
 */
public interface IPermissionListener {
    int CAMERA_REQUEST_CODE = 217;
    int READ_CONTACTS_REQUEST_CODE = 218;
    int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 219;
    int ACCESS_FINE_LOCATION_REQUEST_CODE = 223;

    void onPermissionLack (int permission, IPermissionRequestListener iPermissionRequestListener);
}
