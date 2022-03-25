package com.kinetise.data.systemdisplay.helpers;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.Toast;

import com.kinetise.components.activity.KinetiseActivity;
import com.kinetise.data.application.popupmanager.PopupManager;
import com.kinetise.data.sourcemanager.LanguageManager;
import com.kinetise.data.systemdisplay.views.AGGetPhoneContactView;

import java.util.ArrayList;
import java.util.List;

public class GetPhoneContactSetter {

    public static final int REQUEST_GET_PHONE_CONTACT = 47;
    private static AGGetPhoneContactView mGetPhoneContactView;

    public static AGGetPhoneContactView getClickedGetPhoneContactView() {
        return mGetPhoneContactView;
    }

    public static void setClickedGetPhoneContactView(AGGetPhoneContactView getPhoneContactView) {
        mGetPhoneContactView = getPhoneContactView;
    }

    public static void setPhoneContact(int resultCode, Intent data, KinetiseActivity kinetiseActivity) {
        if (mGetPhoneContactView!=null) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contactData = data.getData();
                List<String> numbers = new ArrayList<>();
                String displayName;
                Cursor cursor = kinetiseActivity.getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();
                String hasPhone = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID));

                int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                displayName = cursor.getString(nameIndex);

                if (hasPhone.equals("1")) {
                    Cursor phones = kinetiseActivity.getContentResolver().query
                            (ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                            + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        String number = phones.getString(phones.getColumnIndex
                                (ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("[-() ]", "");
                        if (!numbers.contains(number))
                            numbers.add(number);
                    }
                    phones.close();
                } else {
                    Toast.makeText(kinetiseActivity.getApplicationContext(), "This contact has no phone number", Toast.LENGTH_LONG).show();
                }
                cursor.close();

                mGetPhoneContactView.setPhoneContact(displayName, numbers);
                setClickedGetPhoneContactView(null);
            } else {
                handleCodeScannerError();
            }
        }
    }

    public static void handleCodeScannerError() {
        PopupManager.showErrorPopup(LanguageManager.getInstance().getString(LanguageManager.ERROR_GET_PHONE_CONTACT));
    }
}
