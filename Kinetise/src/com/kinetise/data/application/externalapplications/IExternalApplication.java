package com.kinetise.data.application.externalapplications;

import android.app.Activity;

/**
 * User: Mateusz
 * Date: 13.03.13
 * Time: 13:51
 */
public interface IExternalApplication {

    boolean open(Activity activity);
    void close();
}
