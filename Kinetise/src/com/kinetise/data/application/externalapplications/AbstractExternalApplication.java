package com.kinetise.data.application.externalapplications;

import android.app.Activity;
import com.kinetise.data.application.AGApplicationState;

/**
 * User: Mateusz
 * Date: 13.03.13
 * Time: 13:52
 */
public abstract class AbstractExternalApplication implements IExternalApplication {

    private String mMessage;

    public AbstractExternalApplication() {
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        mMessage = message;
    }

    @Override
    public boolean open(Activity activity) {

        AGApplicationState.getInstance().onOpenExternalApplication();

        return true;
    }

    @Override
    public void close() {

        AGApplicationState.getInstance().onCloseExternalApplication();
    }
}
