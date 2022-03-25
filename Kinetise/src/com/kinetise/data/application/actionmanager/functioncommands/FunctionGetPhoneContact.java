package com.kinetise.data.application.actionmanager.functioncommands;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;

import com.kinetise.data.application.AGApplicationState;
import com.kinetise.data.application.IAGApplication;
import com.kinetise.data.descriptors.actions.AbstractFunctionDataDesc;

public class FunctionGetPhoneContact extends AbstractFunction {
    public static final int REQUEST_SCAN_CODE_FUNCTION = 46;
    private Intent intent;

    public FunctionGetPhoneContact(AbstractFunctionDataDesc functionDesc, IAGApplication application) {
        super(functionDesc, application);
    }

    @Override
    public Object execute(Object desc) {
        super.execute(desc);
        Activity activity = AGApplicationState.getInstance().getActivity();
        if (activity != null) {
            intent = getPhoneContactIntent();
            startGetPhoneContactActivity(activity);
        }

        return null;
    }

    private Intent getPhoneContactIntent() {
        return new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
    }

    private void startGetPhoneContactActivity(Activity activity) {
        if (intent != null)
            activity.startActivityForResult(intent, REQUEST_SCAN_CODE_FUNCTION);
    }
}
