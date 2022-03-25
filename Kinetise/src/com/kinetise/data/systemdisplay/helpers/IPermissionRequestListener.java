package com.kinetise.data.systemdisplay.helpers;

/**
 * Created by SzymonGrzeszczuk on 2015-12-29.
 */
public interface IPermissionRequestListener {
   void onPermissionGranted();
   void  onPermissionDenied();
}
